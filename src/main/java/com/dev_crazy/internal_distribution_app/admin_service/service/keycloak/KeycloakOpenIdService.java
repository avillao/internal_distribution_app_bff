package com.dev_crazy.internal_distribution_app.admin_service.service.keycloak;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakOpenIdService {

    private WebClient webClient;

    @Value("${keycloak.base-uri}")
    private String baseUri;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.client_secret}")
    private String clientSecret;

    public KeycloakOpenIdService(){}

    @PostConstruct
    public void init() {
        webClient = WebClient.create(baseUri);
    }

    public Map<String, Object> getUserToken(String username, String password){
        MultiValueMap<String, String> form = MultiValueMap.fromMultiValue(
                Map.of(
                        "client_id", List.of(clientId),
                        "client_secret", List.of(clientSecret),
                        "grant_type", List.of("password"),
                        "username", List.of(username),
                        "password", List.of(password),
                        "scope", List.of("openid")
                ));

        try {

            Map response = webClient.post()
                    .uri(String.format("/realms/%s/protocol/openid-connect/token", realm))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response;
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
