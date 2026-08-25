package com.dev_crazy.internal_distribution_app.admin_service.dto.request.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotEmpty(message = "Campo obligatorio")
    private String username;

    @NotEmpty(message = "Campo obligatorio")
    private String password;
}
