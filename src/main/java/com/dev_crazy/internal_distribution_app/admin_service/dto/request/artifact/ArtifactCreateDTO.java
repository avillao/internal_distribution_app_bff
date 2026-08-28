package com.dev_crazy.internal_distribution_app.admin_service.dto.request.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.validator.BranchValid;
import com.dev_crazy.internal_distribution_app.admin_service.validator.PlatformValid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactCreateDTO {
    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^(\\d+\\.){1,2}\\d+$",
            message = "Valor no permitido"
    )
    @Schema(example = "1.0.0")
    private String version;

    @NotEmpty(message = "Campo obligatorio")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z\\d_]*$", flags = {Pattern.Flag.CASE_INSENSITIVE},
            message = "Valor no permitido"
    )
    @Schema(example = "inventory_service")
    private String applicationCode;

    @NotEmpty(message = "Campo obligatorio")
    @PlatformValid
    @Schema(example = "android", allowableValues = {"android", "ios"})
    private String platform;

    @NotEmpty(message = "Campo obligatorio")
    @BranchValid
    @Schema(example = "qa", allowableValues = {"qa", "prd"})
    private String branch;

}
