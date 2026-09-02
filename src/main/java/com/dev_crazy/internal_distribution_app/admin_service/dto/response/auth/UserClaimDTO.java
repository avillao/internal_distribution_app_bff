package com.dev_crazy.internal_distribution_app.admin_service.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserClaimDTO {
    private String username;
    private String name;
    private String email;
    private List<String> roles;
}
