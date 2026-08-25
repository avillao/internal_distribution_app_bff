package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IArtifactRepository {
    Optional<Artifact> findByCode(String resourceApplicationCode, String artifactCode);
    List<Artifact> findAll(String resourceApplicationCode, Map<String, Object> filters);
    Optional<Artifact> findLatest(String resourceApplicationCode, Map<String, Object> filters);
    Artifact save(Artifact artifact);
}
