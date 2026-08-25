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
public class ApplicationFilterDTO {
    @Pattern(
            regexp = "^\\w+$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    private String name;

    private Boolean enabled;

    @Pattern(
            regexp = "^([A-Za-z][A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$",
            message = "Valor no permitido"
    )
    private String packageName;
}
