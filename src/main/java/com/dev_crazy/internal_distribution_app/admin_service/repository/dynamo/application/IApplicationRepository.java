package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.application;

import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.ApplicationResource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IApplicationRepository {
    Optional<Application> findByCode(String applicationCode);
    List<Application> findAll(Map<String, Object> filters);
    List<ApplicationResource> getApplicationResources(String applicationCode);
    Application save(Application application);
}
