package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBArtifact;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBArtifactBinary;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import com.dev_crazy.internal_distribution_app.admin_service.model.BinaryDetail;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DynamoDBArtifactBinaryRepository implements IArtifactBinaryRepository {

    @Autowired
    private DynamoDBMapper dynamoMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<BinaryDetail> findById(String artifactUuid) {
        DynamoDBArtifactBinary dynamoDBArtifactBinary = dynamoMapper.load(DynamoDBArtifactBinary.class, artifactUuid);
        if (dynamoDBArtifactBinary == null) {
            return Optional.empty();
        }

        BinaryDetail binaryDetail = modelMapper.map(dynamoDBArtifactBinary, BinaryDetail.class);
        return Optional.of(binaryDetail);
    }

    @Override
    public BinaryDetail save(BinaryDetail binaryDetail, String artifactUuid) {
        DynamoDBArtifactBinary dynamoDBArtifactBinary = modelMapper.map(binaryDetail, DynamoDBArtifactBinary.class);
        dynamoDBArtifactBinary.setArtifactId(artifactUuid);
        dynamoMapper.save(dynamoDBArtifactBinary);
        return binaryDetail;
    }
}