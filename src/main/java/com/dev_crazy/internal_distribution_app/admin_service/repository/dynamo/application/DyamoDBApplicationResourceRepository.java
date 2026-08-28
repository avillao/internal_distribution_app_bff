package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.application;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBApplicationResource;
import com.dev_crazy.internal_distribution_app.admin_service.model.ApplicationResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class DyamoDBApplicationResourceRepository implements IApplicationResourceRepository {
    @Autowired
    private DynamoDBMapper dynamoMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApplicationResource save(ApplicationResource applicationResource) {
        DynamoDBApplicationResource applicationResourceEntity = modelMapper.map(applicationResource, DynamoDBApplicationResource.class);
        applicationResourceEntity.setResourceId(UUID.randomUUID().toString());

        dynamoMapper.save(applicationResourceEntity);
        return applicationResource;
    }
}
