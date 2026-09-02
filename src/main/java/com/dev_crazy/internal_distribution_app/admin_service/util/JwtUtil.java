package com.dev_crazy.internal_distribution_app.admin_service.util;

import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;
import java.util.Map;

public class JwtUtil {

    public static List<String> getRoles(Jwt jwt, String clientId) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> resource_access = (Map<String, Object>) claims.get("resource_access");
        Map<String, Object> client = (Map<String, Object>) resource_access.get(clientId);
        List<String> roles = (List<String>) client.get("roles");
        return roles;
    }

    public static boolean hasRole(Jwt jwt, String clientId, String roleName) {
        try {
            List<String> roles = JwtUtil.getRoles(jwt, clientId);
            return roles.contains(roleName);
        } catch (Exception e) {
            return false;
        }
    }
}
