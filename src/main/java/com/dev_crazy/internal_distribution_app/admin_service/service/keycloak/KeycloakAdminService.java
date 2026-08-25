package com.dev_crazy.internal_distribution_app.admin_service.service.keycloak;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    @Value("${keycloak.base-uri}")
    private String baseUri;

    private WebClient webClient;

    public KeycloakAdminService(){}

    @PostConstruct
    public void init() {
        webClient = WebClient.create(baseUri);
    }

    private String getAdminToken(){
        MultiValueMap<String, String> form = MultiValueMap.fromMultiValue(
            Map.of(
                    "client_id", List.of(clientId),
                    "client_secret", List.of(clientSecret),
                    "grant_type", List.of("client_credentials")
            ));

        Map response = webClient.post()
                .uri(String.format("/realms/%s/protocol/openid-connect/token", realm))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return response.get("access_token").toString();
    }

    public String createClientRole(String role){
        String token = this.getAdminToken();

        Map<String, Object> form = Map.of(
                "name", role
        );

        try {

            webClient.post()
                    .uri(String.format("/admin/realms/%s/clients/%s/roles", realm, clientUuid))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(form))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map response = webClient.get()
                    .uri(String.format("/admin/realms/%s/clients/%s/roles/%s", realm, clientUuid, role))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response.get("id").toString();
        } catch (WebClientResponseException e){
            throw new BaseServiceException(e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createClientResource(String resource, List<String> uris){
        String token = this.getAdminToken();

        Map<String, Object> form = Map.of(
                "name", resource,
                "displayName", resource,
                "ownerManagedAccess", true,
                "uris", uris,
                "scopes", List.of(
                        Map.of("name", "read"),
                        Map.of("name", "write"),
                        Map.of("name", "delete"),
                        Map.of("name", "update")
                )
        );

        try{
            Map response = webClient.post()
                    .uri(String.format("/admin/realms/%s/clients/%s/authz/resource-server/resource", realm, clientUuid))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response.get("_id").toString();

        } catch (WebClientResponseException e){
            throw new BaseServiceException(e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createClientRolePolicy(String policy, List<String> roles){
        String token = this.getAdminToken();

        Map<String, Object> form = Map.of(
                "name", policy,
                "type", "rol",
                "logic", "POSITIVE",
                "fetchRoles", true,
                "roles", roles.stream().map((roleId)-> Map.of(
                        "id", roleId,
                        "required", true
                )).collect(Collectors.toList())
        );


        try{
            Map response = webClient.post()
                    .uri(String.format("/admin/realms/%s/clients/%s/authz/resource-server/policy/role", realm, clientUuid))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response.get("id").toString();

        } catch (WebClientResponseException e){
            throw new BaseServiceException(e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createClientScopePermission(String permissionName, List<String> resources, List<String> policies,
                                              List<String> scopes){
        String token = this.getAdminToken();

        Map<String, Object> form = Map.of(
                "name", permissionName,
                "decisionStrategy", "AFFIRMATIVE",
                "resources", resources,
                "policies", policies,
                "scopes", scopes
        );

        try{
            Map response = webClient.post()
                    .uri(String.format("/admin/realms/%s/clients/%s/authz/resource-server/permission/scope", realm, clientUuid))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response.get("id").toString();

        } catch (WebClientResponseException e){
            throw new BaseServiceException(e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getScopeByName(String name){
        String token = this.getAdminToken();

        try{
            List<Map<String, Object>> response = webClient.get()
                    .uri(String.format("/admin/realms/%s/clients/%s/authz/resource-server/scope?first=0&max=11&deep=false&name=%s",
                            realm, clientUuid, name))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            return response.get(0);

        } catch (WebClientResponseException e){
            throw new BaseServiceException(e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
