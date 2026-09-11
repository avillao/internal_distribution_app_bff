package com.dev_crazy.internal_distribution_app.admin_service.service.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IArtifactService {
    Artifact findByCode(String resourceApplicationCode, String artifactCode);
    BinaryDetail getBinaryDetail(String artifactCode);
    List<Artifact> findAll(String resourceApplicationCode, Map<String, Object> filters);
    Artifact findLatest(String resourceApplicationCode);
    Artifact create(Artifact artifact);
    Artifact saveBinary(BinaryDetail binaryDetail, InputStream inputStream, String resourceApplicationCode, String artifactCode);
}
