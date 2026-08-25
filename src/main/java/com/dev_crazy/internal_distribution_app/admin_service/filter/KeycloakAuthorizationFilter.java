package com.dev_crazy.internal_distribution_app.admin_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KeycloakAuthorizationFilter extends OncePerRequestFilter {

    @Value("${keycloak.base-uri}")
    private String baseUri;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String path = request.getRequestURI();
        if (path.startsWith("/")){
            path = path.substring(1);
        }

        if (auth instanceof AnonymousAuthenticationToken &&
                path.startsWith("auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!(auth instanceof JwtAuthenticationToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        Jwt jwt = ((JwtAuthenticationToken) auth).getToken();

        String method = request.getMethod();

        String scope = switch (method) {
            case "GET" -> "read";
            case "POST" -> "write";
            case "PUT" -> "update";
            case "DELETE" -> "delete";
            default -> "read";
        };

        try {
            if (path.startsWith("api/admin")) {
                if (!hasRole(jwt, "admin")) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient role");
                    return;
                }
            } else {
                path = path.replace("api", "");
                this.checkPermissionWithKeycloak(jwt, path, scope);
            }

            filterChain.doFilter(request, response);
        } catch (WebClientResponseException e){
            response.sendError(e.getStatusCode().value(), e.getMessage());
            return;
        }
        catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error validating access");
            return;
        }
    }

    private boolean hasRole(Jwt jwtToken, String roleName) {
        try {
            Map<String, Object> claims = jwtToken.getClaims();
            Map<String, Object> resource_access = (Map<String, Object>) claims.get("resource_access");
            Map<String, Object> client = (Map<String, Object>) resource_access.get(clientId);
            List<String> roles = (List<String>) client.get("roles");
            return roles.contains(roleName);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkPermissionWithKeycloak(Jwt userToken, String resource, String scope) {

        WebClient client = WebClient.create(baseUri);
        MultiValueMap<String, String> form = MultiValueMap.fromMultiValue(
                Map.of(
                "grant_type", List.of("urn:ietf:params:oauth:grant-type:uma-ticket"),
                "audience", List.of(clientId),
                "permission", List.of(resource + "#" + scope),
                "response_mode", List.of("decision"),
                "permission_resource_format", List.of("uri")
        ));

        Map response = client.post()
                .uri(String.format("/realms/%s/protocol/openid-connect/token", realm))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + userToken.getTokenValue())
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return Boolean.TRUE.equals(response.get("result"));
    }
}