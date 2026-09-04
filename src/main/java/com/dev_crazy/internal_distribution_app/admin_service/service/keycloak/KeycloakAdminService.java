package com.dev_crazy.internal_distribution_app.admin_service.service.keycloak;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.Logic;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.client_uuid}")
    private String clientUuid;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.client_secret}")
    private String clientSecret;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.base_uri}")
    private String baseUri;

    private Keycloak keycloak;

    public KeycloakAdminService(){}

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(baseUri)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public String createClientRole(String roleName) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();
            ClientResource clientResource = clientsResource.get(clientUuid);

            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            clientResource.roles().create(role);

            // Buscar el rol por nombre y devolver su id
            RoleRepresentation createdRole = clientResource.roles().get(roleName).toRepresentation();
            return createdRole.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getClientRoleId(String roleName) {
        try{
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();
            ClientResource clientResource = clientsResource.get(clientUuid);

            RoleRepresentation createdRole = clientResource.roles().get(roleName).toRepresentation();
            return createdRole.getId();
        }catch (NotFoundException exception) {
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createClientResource(String resourceName, List<String> uris) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();
            ClientResource clientResource = clientsResource.get(clientUuid);

            ResourceRepresentation resource = new ResourceRepresentation();
            resource.setName(resourceName);
            resource.setDisplayName(resourceName);
            resource.setOwnerManagedAccess(true);
            resource.setUris(new HashSet<>(uris));
            resource.setScopes(new HashSet<>(List.of(
                    new ScopeRepresentation("read"),
                    new ScopeRepresentation("write"),
                    new ScopeRepresentation("delete"),
                    new ScopeRepresentation("update")
            )));

            clientResource.authorization().resources().create(resource);
            // Buscar el recurso por nombre y devolver su id
            List<ResourceRepresentation> resources = clientResource.authorization().resources().findByName(resourceName);
            return resources.get(0).getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createClientRolePolicy(String policyName, List<String> roleId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();
            ClientResource clientResource = clientsResource.get(clientUuid);

            PolicyRepresentation policy = new PolicyRepresentation();
            policy.setName(policyName);
            policy.setType("role");
            policy.setLogic(Logic.POSITIVE);
            policy.setDecisionStrategy(DecisionStrategy.UNANIMOUS);

            // Asignar roles usando el campo config (para policies de tipo role)
            // El valor debe ser un JSON string con la estructura esperada por Keycloak
            // Ejemplo: {"roles": "[{\"id\":\"role-id\",\"required\":true}]"}
            List<Map<String, Object>> rolesList = roleId.stream().map(id -> {
                return (Map<String, Object>) (Map) Map.of("id", id, "required", true);
            }).collect(Collectors.toList());
            String rolesJson = new ObjectMapper().writeValueAsString(rolesList);
            policy.setConfig(Map.of(
                    "roles", rolesJson,
                    "fetchRoles", "true"
            ));

            clientResource.authorization().policies().create(policy);
            PolicyRepresentation policyRepresentation = clientResource.authorization().policies().findByName(policyName);
            return policyRepresentation.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createClientScopePermission(String permissionName, List<String> resourceIds, List<String> policyIds, List<String> scopeNames) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();
            ClientResource clientResource = clientsResource.get(clientUuid);

            PolicyRepresentation permission = new PolicyRepresentation();
            permission.setName(permissionName);
            permission.setType("scope");
            permission.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
            permission.setResources(new HashSet<>(resourceIds));
            permission.setPolicies(new HashSet<>(policyIds));
            permission.setScopes(new HashSet<>(scopeNames));

            clientResource.authorization().policies().create(permission);
            PolicyRepresentation policyRepresentation = clientResource.authorization().policies().findByName(permissionName);

            return policyRepresentation.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getScopeId(String name) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            ClientsResource clientsResource = realmResource.clients();
            ClientResource clientResource = clientsResource.get(clientUuid);
            ScopeRepresentation scope = clientResource.authorization().scopes().findByName(name);
            return scope.getId();
        } catch (NotFoundException exception) {
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
