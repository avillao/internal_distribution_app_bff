package com.dev_crazy.internal_distribution_app.admin_service.dto.request.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCreateDTO {
    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z\\d_]*$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    @Schema(example = "inventory_service")
    private String applicationCode;

    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^[\\w ]+$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    @Schema(example = "Internal Distribution")
    private String name;

    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^[A-Za-z0-9áéíóúÁÉÍÓÚñÑüÜ\\s,.:\\-_'\"]{10,255}$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    @Schema(example = "Servicio interno para distribuir aplicaciones")
    private String description;

    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^([A-Za-z][A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$",
            message = "Valor no permitido"
    )
    @Schema(example = "com.dev_crazy.inventory")
    private String packageName;
}
