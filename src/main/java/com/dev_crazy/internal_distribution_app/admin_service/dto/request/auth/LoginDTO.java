package com.dev_crazy.internal_distribution_app.admin_service.dto.request.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotEmpty(message = "Campo obligatorio")
    @Schema(example = "usuario-demo")
    private String username;

    @NotEmpty(message = "Campo obligatorio")
    @Schema(example = "tu-password")
    private String password;
}
