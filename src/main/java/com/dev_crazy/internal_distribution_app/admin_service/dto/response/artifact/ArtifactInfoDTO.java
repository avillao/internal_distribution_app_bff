package com.dev_crazy.internal_distribution_app.admin_service.dto.response.artifact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtifactInfoDTO {
    private String resourceApplicationCode;
    private String artifactId;
    private String applicationCode;
    private String version;
    private String branch;
    private String platform;
    private Boolean enabled;
    private Date created;
    private Date updated;
}
