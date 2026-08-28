package com.dev_crazy.internal_distribution_app.admin_service.seed;

import com.dev_crazy.internal_distribution_app.admin_service.service.keycloak.KeycloakAdminService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class KeycloakCreateResourceTest {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Test
    @Disabled
    public void createResource() {
        if(keycloakAdminService.getClientRoleId("admin") == null) {
            keycloakAdminService.createClientRole("admin");
        }

        String roleId = keycloakAdminService.getClientRoleId("user_qa");
        if(roleId == null){
            keycloakAdminService.createClientRole("user_qa");
        }

        if(keycloakAdminService.getClientRoleId("user_prd") == null) {
            keycloakAdminService.createClientRole("user_prd");
        }

        // Resource
        String resourceName = "application";
        String resourceUri = "/application";

        String resourceId = keycloakAdminService.createClientResource(resourceName, List.of(resourceUri));

        // Policy
        String policyName = "user_application_policy";
        String policyId = keycloakAdminService.createClientRolePolicy(policyName, List.of(roleId));

        // Permission
        String permissionName = "user_application_permission";
        String scopeId = keycloakAdminService.getScopeId("read");
        keycloakAdminService.createClientScopePermission(permissionName,
                List.of(resourceId), List.of(policyId), List.of(scopeId));

    }
}
