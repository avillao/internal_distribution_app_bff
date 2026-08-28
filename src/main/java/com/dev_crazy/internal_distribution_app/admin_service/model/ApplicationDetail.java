package com.dev_crazy.internal_distribution_app.admin_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDetail {
    private String applicationCode;
    private String name;
    private String description;
    private String packageName;
    private Boolean enabled;
    private Date created;
    private Date updated;
    private List<Branch> branches;
    private List<Platform> platforms;
}