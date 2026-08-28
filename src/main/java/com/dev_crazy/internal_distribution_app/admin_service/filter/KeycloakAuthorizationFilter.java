package com.dev_crazy.internal_distribution_app.admin_service.filter;

import com.dev_crazy.internal_distribution_app.admin_service.service.keycloak.KeycloakAuthzService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakAuthorizationFilter extends OncePerRequestFilter {

    @Value("${keycloak.client_id}")
    private String clientId;

    @Autowired
    private KeycloakAuthzService keycloakAuthzService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.equals("/auth/login")
                || path.equals("/auth/refresh")
                || path.equals("/api-docs")
                || path.startsWith("/api-docs/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/");
    }

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
                keycloakAuthzService.checkPermission(jwt.getTokenValue(), path, scope);
            }

            filterChain.doFilter(request, response);
        } catch (HttpResponseException e){
            response.sendError(e.getStatusCode(), e.getMessage());
        } catch (AuthorizationDeniedException e){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            if(e.getCause() instanceof HttpResponseException ex){
                response.sendError(ex.getStatusCode(), e.getMessage());
                return;
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error validating access");
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
}