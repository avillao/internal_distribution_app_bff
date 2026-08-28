package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.application;

import com.dev_crazy.internal_distribution_app.admin_service.model.ApplicationResource;

public interface IApplicationResourceRepository {
    ApplicationResource save(ApplicationResource applicationResource);
}
