package com.dev_crazy.internal_distribution_app.admin_service.repository;

import com.dev_crazy.internal_distribution_app.admin_service.model.Application;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IApplicationRepository {
    Optional<Application> findByCode(String applicationCode);
    List<Application> findAll(Map<String, Object> filters);
    Application save(Application application);
}
