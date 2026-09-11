package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;

import java.util.Optional;

public interface IArtifactBinaryRepository {
    Optional<BinaryDetail> findById(String artifactUuid);
    BinaryDetail save(BinaryDetail binaryDetail, String artifactUuid);
}
