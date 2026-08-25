package com.dev_crazy.internal_distribution_app.admin_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private String applicationCode;
    private String name;
    private String packageName;
    private Boolean enabled;
    private Date created;
    private Date updated;
}