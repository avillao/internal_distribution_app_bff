package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBApplication;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.repository.IApplicationRepository;
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
    public Application save(Application application) {
        DynamoDBApplication applicationEntity = modelMapper.map(application, DynamoDBApplication.class);
        dynamoMapper.save(applicationEntity);
        return application;
    }
}
