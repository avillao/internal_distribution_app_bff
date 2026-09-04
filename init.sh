#!/usr/bin/env bash

set -euo pipefail

# ============================================================
# CONFIGURACIÓN
# ============================================================

KC_SERVER="${KEYCLOAK_BASE_URI:-http://keycloak:8080}"

REALM="${KEYCLOAK_REALM:-mi-realm}"
CLIENT_ID="internal_distribution_app"

KC_ADMIN_USER="${KC_BOOTSTRAP_ADMIN_USERNAME:-admin}"
KC_ADMIN_PASSWORD="${KC_BOOTSTRAP_ADMIN_PASSWORD:-admin}"

KC_TOKEN_LIFESPAN="${KEYCLOAK_TOKEN_LIFESPAN:-900}"
KC_SESSION_IDLE_TIMEOUT="${KEYCLOAK_SESSION_IDLE_TIMEOUT:-43200}"
KC_SESSION_MAX_LIFESPAN="${KEYCLOAK_SESSION_MAX_LIFESPAN:-21600}"

# Client roles
CLIENT_ROLES=(
    "admin"
    "user_qa"
    "user_prd"
)

CLIENT_SCOPES=(
  "read"
  "write"
  "delete"
  "update"
)

# Roles que tendrá el Service Account sobre realm-management
ADMIN_ROLES=(
    "manage-clients"
    "manage-authorization"
    "view-clients"
    "view-authorization"
    "query-clients"
)

# Authorization Services
SCOPE_NAME="read"

RESOURCE_NAME="application"
RESOURCE_URI="/application"

POLICY_NAME="user_application_policy"
PERMISSION_NAME="user_application_permission"

# ============================================================
# HELPERS
# ============================================================

kc() {
    /opt/keycloak/bin/kcadm.sh "$@"
}

log() {
    echo ""
    echo "==> $1"
}

# ============================================================
# 1. ESPERAR A KEYCLOAK
# ============================================================

echo "Keycloak está listo."

# ============================================================
# 2. AUTENTICARSE
# ============================================================

log "Autenticando en Keycloak..."

kc config credentials \
    --server "$KC_SERVER" \
    --realm master \
    --user "$KC_ADMIN_USER" \
    --password "$KC_ADMIN_PASSWORD"

echo "Autenticación correcta."

# ============================================================
# 3. OBTENER REALM
# ============================================================

log "Verificando realm '$REALM'..."

if kc get "realms/$REALM" >/dev/null 2>&1; then

    echo "Realm '$REALM' existe."

else
    echo "Realm '$REALM' no existe."
    exit 1
fi

# ============================================================
# 4. CREAR CLIENT SI NO EXISTE
# ============================================================

log "Verificando client '$CLIENT_ID'..."

CLIENT_UUID=$(
    kc get clients \
        -r "$REALM" \
        -q "clientId=$CLIENT_ID" \
        --fields id \
        --format csv \
        --noquotes 2>/dev/null || true
)

if [[ -z "$CLIENT_UUID" ]]; then

    echo "Client '$CLIENT_ID' no existe."
    echo "Creando client..."

    kc create clients \
        -r "$REALM" \
        -s "clientId=$CLIENT_ID" \
        -s enabled=true \
        -s protocol=openid-connect \
        -s publicClient=false \
        -s serviceAccountsEnabled=true \
        -s authorizationServicesEnabled=true \
        -s directAccessGrantsEnabled=true \
        -s attributes="{\"access.token.lifespan\":\"$KC_TOKEN_LIFESPAN\",\"client.session.idle.timeout\":\"$KC_SESSION_IDLE_TIMEOUT\",\"client.session.max.lifespan\":\"$KC_SESSION_MAX_LIFESPAN\"}"

    CLIENT_UUID=$(
        kc get clients \
            -r "$REALM" \
            -q "clientId=$CLIENT_ID" \
            --fields id \
            --format csv \
            --noquotes
    )

    echo "Client creado."

else

    echo "Client '$CLIENT_ID' ya existe."

    # Aseguramos que tenga las capacidades necesarias.
    kc update "clients/$CLIENT_UUID" \
        -r "$REALM" \
        -s enabled=true \
        -s serviceAccountsEnabled=true \
        -s authorizationServicesEnabled=true \
        -s directAccessGrantsEnabled=true

fi

echo "Client UUID: $CLIENT_UUID"

# ============================================================
# 5. CREAR CLIENT ROLES
# ============================================================

log "Configurando client roles..."

for ROLE in "${CLIENT_ROLES[@]}"; do

    if kc get "clients/$CLIENT_UUID/roles/$ROLE" \
        -r "$REALM" >/dev/null 2>&1
    then

        echo "Role '$ROLE' ya existe."

    else

        echo "Creando role '$ROLE'..."

        kc create "clients/$CLIENT_UUID/roles" \
            -r "$REALM" \
            -s "name=$ROLE"

    fi

done

# ============================================================
# 6. OBTENER SERVICE ACCOUNT USER
# ============================================================

SERVICE_ACCOUNT_USERNAME="service-account-$CLIENT_ID"

log "Obteniendo Service Account User..."

SERVICE_ACCOUNT_USER_ID=$(
    kc get users \
        -r "$REALM" \
        -q "username=$SERVICE_ACCOUNT_USERNAME" \
        --fields id \
        --format csv \
        --noquotes 2>/dev/null || true
)

if [[ -z "$SERVICE_ACCOUNT_USER_ID" ]]; then

    echo "ERROR: No se encontró el Service Account User:"
    echo "$SERVICE_ACCOUNT_USERNAME"

    echo ""
    echo "El client fue configurado con serviceAccountsEnabled=true,"
    echo "pero Keycloak no creó/encontró el Service Account."

    exit 1
fi

echo "Service Account:"
echo "  Username: $SERVICE_ACCOUNT_USERNAME"
echo "  UUID:     $SERVICE_ACCOUNT_USER_ID"

# ============================================================
# 7. OBTENER realm-management
# ============================================================

log "Obteniendo client 'realm-management'..."

REALM_MANAGEMENT_UUID=$(
    kc get clients \
        -r "$REALM" \
        -q "clientId=realm-management" \
        --fields id \
        --format csv \
        --noquotes
)

if [[ -z "$REALM_MANAGEMENT_UUID" ]]; then
    echo "ERROR: No se encontró realm-management."
    exit 1
fi

echo "realm-management UUID: $REALM_MANAGEMENT_UUID"

# ============================================================
# 8. ASIGNAR ROLES ADMINISTRATIVOS AL SERVICE ACCOUNT
# ============================================================

log "Configurando roles de realm-management..."

for ROLE in "${ADMIN_ROLES[@]}"; do

    echo "Verificando '$ROLE'..."

    kc add-roles \
        -r "$REALM" \
        --uusername "$SERVICE_ACCOUNT_USERNAME" \
        --cclientid "realm-management" \
        --rolename "$ROLE"

    echo "  '$ROLE' asignado."

done

# ============================================================
# 9. OBTENER UUID DEL ROLE user_qa y user_prd
# ============================================================

log "Obteniendo UUID de 'user_qa' y 'user_prd'..."

USER_QA_ROLE_ID=$(
    kc get "clients/$CLIENT_UUID/roles/user_qa" \
        -r "$REALM" \
        --fields id \
        --format csv \
        --noquotes
)

USER_PRD_ROLE_ID=$(
    kc get "clients/$CLIENT_UUID/roles/user_prd" \
        -r "$REALM" \
        --fields id \
        --format csv \
        --noquotes
)

echo "user_qa UUID: $USER_QA_ROLE_ID"
echo "user_prd UUID: $USER_PRD_ROLE_ID"

# ============================================================
# 10. CREAR AUTHORIZATION SCOPE
# ============================================================
declare -A SCOPE_IDS
log "==> Configurando scopes..."

for SCOPE in "${CLIENT_SCOPES[@]}"; do
    SCOPE_ID=$(
        kc get "clients/$CLIENT_UUID/authz/resource-server/scope" \
            -r "$REALM" \
            -q "name=$SCOPE" \
            --fields id \
            --format csv \
            --noquotes 2>/dev/null || true
    )

    if [[ -z "$SCOPE_ID" ]]; then
        echo "Creando scope '$SCOPE'..."

        kc create \
            "clients/$CLIENT_UUID/authz/resource-server/scope" \
            -r "$REALM" \
            -s "name=$SCOPE"

        SCOPE_ID=$(
            kc get "clients/$CLIENT_UUID/authz/resource-server/scope" \
                -r "$REALM" \
                -q "name=$SCOPE" \
                --fields id \
                --format csv \
                --noquotes
        )
    fi

    SCOPE_IDS["$SCOPE"]="$SCOPE_ID"
done

log "Configurando Authorization Scope '$SCOPE_NAME'..."

SCOPE_ID=${SCOPE_IDS["$SCOPE_NAME"]}

echo "Scope UUID: $SCOPE_ID"

# ============================================================
# 11. CREAR RESOURCE
# ============================================================

log "Configurando Resource '$RESOURCE_NAME'..."

RESOURCE_ID=$(
    kc get "clients/$CLIENT_UUID/authz/resource-server/resource" \
        -r "$REALM" \
        -q "name=$RESOURCE_NAME" \
        --fields _id \
        --format csv \
        --noquotes 2>/dev/null || true
)

if [[ -z "$RESOURCE_ID" ]]; then

    echo "Creando resource '$RESOURCE_NAME'..."

    kc create \
        "clients/$CLIENT_UUID/authz/resource-server/resource" \
        -r "$REALM" \
        -s "name=$RESOURCE_NAME" \
        -s "displayName=$RESOURCE_NAME" \
        -s "ownerManagedAccess=true" \
        -s 'uris=["/application"]' \
        -s "scopes=[
                    {\"id\":\"${SCOPE_IDS[read]}\",\"name\":\"read\"},
                    {\"id\":\"${SCOPE_IDS[write]}\",\"name\":\"write\"},
                    {\"id\":\"${SCOPE_IDS[delete]}\",\"name\":\"delete\"},
                    {\"id\":\"${SCOPE_IDS[update]}\",\"name\":\"update\"}
        ]"

    RESOURCE_ID=$(
        kc get "clients/$CLIENT_UUID/authz/resource-server/resource" \
            -r "$REALM" \
            -q "name=$RESOURCE_NAME" \
            --fields _id \
            --format csv \
            --noquotes
    )

else

    echo "Resource '$RESOURCE_NAME' ya existe."

fi

echo "Resource UUID: $RESOURCE_ID"

# ============================================================
# 12. CREAR ROLE POLICY
# ============================================================

log "Configurando Policy '$POLICY_NAME'..."

POLICY_ID=$(
    kc get "clients/$CLIENT_UUID/authz/resource-server/policy" \
        -r "$REALM" \
        -q "name=$POLICY_NAME" \
        --fields id \
        --format csv \
        --noquotes 2>/dev/null || true
)

if [[ -z "$POLICY_ID" ]]; then

    echo "Creando role policy..."

    kc create \
        "clients/$CLIENT_UUID/authz/resource-server/policy/role" \
        -r "$REALM" \
        -s "name=$POLICY_NAME" \
        -s "type=role" \
        -s "logic=POSITIVE" \
        -s "decisionStrategy=UNANIMOUS" \
        -s "roles=[{\"id\":\"$USER_QA_ROLE_ID\"},{\"id\":\"$USER_PRD_ROLE_ID\"}]" \
        -s "fetchRoles=true"

    POLICY_ID=$(
        kc get "clients/$CLIENT_UUID/authz/resource-server/policy" \
            -r "$REALM" \
            -q "name=$POLICY_NAME" \
            --fields id \
            --format csv \
            --noquotes
    )

else

    echo "Policy '$POLICY_NAME' ya existe."

fi

echo "Policy UUID: $POLICY_ID"

# ============================================================
# 13. CREAR RESOURCE PERMISSION
# ============================================================

log "Configurando Permission '$PERMISSION_NAME'..."

PERMISSION_ID=$(
    kc get "clients/$CLIENT_UUID/authz/resource-server/permission" \
        -r "$REALM" \
        -q "name=$PERMISSION_NAME" \
        --fields id \
        --format csv \
        --noquotes 2>/dev/null || true
)

if [[ -z "$PERMISSION_ID" ]]; then

    echo "Creando resource permission..."

    kc create \
        "clients/$CLIENT_UUID/authz/resource-server/permission/scope" \
        -r "$REALM" \
        -s "name=$PERMISSION_NAME" \
        -s "decisionStrategy=AFFIRMATIVE" \
        -s "resources=[\"$RESOURCE_ID\"]" \
        -s "policies=[\"$POLICY_ID\"]" \
        -s "scopes=[\"$SCOPE_ID\"]"

else

    echo "Permission '$PERMISSION_NAME' ya existe."

fi

# ============================================================
# 14. RESUMEN
# ============================================================

echo ""
echo "============================================================"
echo " Keycloak bootstrap completado"
echo "============================================================"
echo ""
echo "Realm"
echo "  $REALM"
echo ""
echo "Client"
echo "  $CLIENT_ID"
echo "  UUID: $CLIENT_UUID"
echo ""
echo "Client Roles"
for ROLE in "${CLIENT_ROLES[@]}"; do
    echo "  - $ROLE"
done
echo ""
echo "Service Account"
echo "  $SERVICE_ACCOUNT_USERNAME"
echo "  UUID: $SERVICE_ACCOUNT_USER_ID"
echo ""
echo "realm-management roles"
for ROLE in "${ADMIN_ROLES[@]}"; do
    echo "  - $ROLE"
done
echo ""
echo "Authorization Services"
echo "  Scopes:"
for SCOPE in "${CLIENT_SCOPES[@]}"; do
    echo "   - $SCOPE"
done
echo ""
echo "  Resource:"
echo "    $RESOURCE_NAME"
echo "    URI: $RESOURCE_URI"
echo ""
echo "  Policy:"
echo "    $POLICY_NAME"
echo "    Role: user_qa"
echo "    Role: user_prd"
echo ""
echo "  Permission:"
echo "    $PERMISSION_NAME"
echo ""
echo "============================================================"