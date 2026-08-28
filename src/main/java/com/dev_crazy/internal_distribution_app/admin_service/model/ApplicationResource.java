package com.dev_crazy.internal_distribution_app.admin_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResource {
    private String applicationCode;
    private Branch branch;
    private Platform platform;
}
