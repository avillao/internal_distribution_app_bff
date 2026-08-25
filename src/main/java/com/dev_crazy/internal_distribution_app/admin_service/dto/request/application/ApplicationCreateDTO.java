package com.dev_crazy.internal_distribution_app.admin_service.dto.request.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCreateDTO {
    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z\\d_]*$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    private String applicationCode;

    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^\\w+$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    private String name;

    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^([A-Za-z][A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$",
            message = "Valor no permitido"
    )
    private String packageName;
}
