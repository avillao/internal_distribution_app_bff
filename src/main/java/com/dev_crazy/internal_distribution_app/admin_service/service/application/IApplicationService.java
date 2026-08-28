package com.dev_crazy.internal_distribution_app.admin_service.service.application;

import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.ApplicationDetail;

import java.util.List;
import java.util.Map;

public interface IApplicationService {
    Application findByCode(String applicationCode);
    List<Application> findAll(Map<String, Object> filters);
    ApplicationDetail getApplicationDetail(String applicationCode);
    Application create(Application application);
    Application update(String applicationCode, Application application);
}
