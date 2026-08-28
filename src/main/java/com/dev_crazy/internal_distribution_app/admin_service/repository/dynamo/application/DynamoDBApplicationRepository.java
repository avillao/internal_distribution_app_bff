package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.application;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBApplication;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBApplicationResource;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.ApplicationResource;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.util.*;

@Repository
public class DynamoDBApplicationRepository implements IApplicationRepository {

    @Autowired
    private DynamoDBMapper dynamoMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<Application> findByCode(String applicationCode) {
        DynamoDBApplication dynamoDBApplication = dynamoMapper.load(DynamoDBApplication.class, applicationCode);
        if (dynamoDBApplication == null) {
            return Optional.empty();
        }

        Application application = modelMapper.map(dynamoDBApplication, Application.class);
        return Optional.of(application);
    }

    @Override
    public List<Application> findAll(Map<String, Object> filters) {
        Map<String, Condition> filtersConditional = new HashMap<>();

        filters.forEach((key, value) -> {
            AttributeValue attributeValue;
            if (value instanceof Boolean) {
                attributeValue = new AttributeValue().withBOOL((Boolean) value);
            } else if (value instanceof Number) {
                attributeValue = new AttributeValue().withN(value.toString());
            } else {
                attributeValue = new AttributeValue().withS(value.toString());
            }

            filtersConditional.put(key,
                    new Condition()
                            .withComparisonOperator("EQ")
                            .withAttributeValueList(attributeValue)
            );
        });

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.setScanFilter(filtersConditional);

        List<DynamoDBApplication> applicationListEntity = dynamoMapper.scan(
                DynamoDBApplication.class, scanExpression
        );
        return modelMapper.map(applicationListEntity, new TypeToken<List<Application>>() {}.getType());
    }

    @Override
    public List<ApplicationResource> getApplicationResources(String applicationCode) {
        DynamoDBApplicationResource dynamoDBApplicationResource = new DynamoDBApplicationResource();
        dynamoDBApplicationResource.setApplicationCode(applicationCode);

        DynamoDBQueryExpression<DynamoDBApplicationResource> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.setHashKeyValues(dynamoDBApplicationResource);

        PaginatedQueryList<DynamoDBApplicationResource> dynamoDBdetails = dynamoMapper.query(DynamoDBApplicationResource.class, queryExpression);

        if (dynamoDBdetails.isEmpty()){
            return List.of();
        }

        //Set<String> branches = dynamoDBdetails.stream().map(DynamoDBApplicationDetail::getBranch).collect(Collectors.toSet());
        //Set<String> platforms = dynamoDBdetails.stream().map(DynamoDBApplicationDetail::getPlatform).collect(Collectors.toSet());

        //ApplicationDetail applicationDetail = new ApplicationDetail();
        //applicationDetail.setApplicationCode(applicationCode);
        //applicationDetail.setBranches(modelMapper.map(branches, new TypeToken<Branch[]>() {}.getType()));
        //applicationDetail.setPlatforms(modelMapper.map(platforms, new TypeToken<Platform[]>() {}.getType()));

        return modelMapper.map(dynamoDBdetails, new TypeToken<List<ApplicationResource>>() {}.getType());
    }

    @Override
    public Application save(Application application) {
        DynamoDBApplication applicationEntity = modelMapper.map(application, DynamoDBApplication.class);
        dynamoMapper.save(applicationEntity);
        return application;
    }
}
