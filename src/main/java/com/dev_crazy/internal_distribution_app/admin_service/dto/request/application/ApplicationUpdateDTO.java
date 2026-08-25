package com.dev_crazy.internal_distribution_app.admin_service.dto.request.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUpdateDTO {
    @Pattern(
            regexp = "^\\w+$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "No se permiten caracteres especiales"
    )
    private String name;

    private Boolean enabled;

    @JsonIgnore // Evita que este campo se serialice en la respuesta
    @AssertTrue(message = "Debe proporcionar al menos un campo para actualizar")
    public boolean isbodyInvalid() {
        return Objects.nonNull(name) || Objects.nonNull(enabled);
    }
}
