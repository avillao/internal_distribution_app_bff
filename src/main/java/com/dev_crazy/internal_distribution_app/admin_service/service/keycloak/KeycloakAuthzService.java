package com.dev_crazy.internal_distribution_app.admin_service.service.keycloak;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import jakarta.annotation.PostConstruct;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationRequest.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Map;

@Service
public class KeycloakAuthzService {

    @Value("${keycloak.base_uri}")
    private String baseUri;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.client_secret}")
    private String clientSecret;

    private AuthzClient authzClient;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        // Configuración apuntando a tu Keycloak
        Configuration config = new Configuration(
                baseUri,
                realm,
                clientId,
                Map.of("secret", clientSecret),
                null
        );
        this.authzClient = AuthzClient.create(config);
        webClient = WebClient.create(baseUri);
    }

    public boolean checkPermission(String accessToken, String resource, String scope) throws AuthorizationDeniedException {
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(resource, scope);

        Metadata metadata= new Metadata();
        metadata.setPermissionResourceFormat("uri");

        request.setMetadata(metadata);

        authzClient.authorization(accessToken).authorize(request);
        return true;
    }

    public AccessTokenResponse getUserToken(String username, String password){
        return authzClient.obtainAccessToken(username, password);
    }

    public AccessTokenResponse refreshToken(String refreshToken){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        try {

            Map response = webClient.post()
                    .uri(String.format("/realms/%s/protocol/openid-connect/token", realm))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
            accessTokenResponse.setToken(response.get("access_token").toString());
            accessTokenResponse.setRefreshToken(response.get("refresh_token").toString());
            accessTokenResponse.setExpiresIn(Integer.parseInt(response.get("expires_in").toString()));
            accessTokenResponse.setRefreshExpiresIn(Integer.parseInt(response.get("refresh_expires_in").toString()));

            return accessTokenResponse;
        } catch (WebClientResponseException e){
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new BaseServiceException("Invalid user credentials", e.getStatusCode().value(), e);
            }
            throw new BaseServiceException(e.getMessage(), e.getStatusCode().value(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
