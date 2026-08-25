package com.dev_crazy.internal_distribution_app.admin_service.dto.request.artifact;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactFilterDTO {
    private Boolean enabled;
}
