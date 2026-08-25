package com.dev_crazy.internal_distribution_app.admin_service.repository.dynamo.artifact;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo.DynamoDBArtifact;
import com.dev_crazy.internal_distribution_app.admin_service.model.Application;
import com.dev_crazy.internal_distribution_app.admin_service.model.Artifact;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DynamoDBArtifactRepository implements IArtifactRepository {

    @Autowired
    private DynamoDBMapper dynamoMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<Artifact> findByCode(String resourceApplicationCode, String artifactCode) {
        DynamoDBArtifact dynamoDBArtifact = dynamoMapper.load(DynamoDBArtifact.class, resourceApplicationCode, artifactCode);
        if (dynamoDBArtifact == null) {
            return Optional.empty();
        }

        Artifact artifact = modelMapper.map(dynamoDBArtifact, Artifact.class);
        return Optional.of(artifact);
    }

    @Override
    public List<Artifact> findAll(String resourceApplicationCode, Map<String, Object> filters) {
        Map<String, Condition> queryFilter = new HashMap<>();

        if(filters != null && filters.get("enabled") != null) {
            boolean enabled = (boolean) filters.get("enabled");
            queryFilter.put(
                    "enabled",
                    new Condition()
                            .withComparisonOperator("EQ")
                            .withAttributeValueList(new AttributeValue().withBOOL(enabled))
            );
        }

        DynamoDBArtifact artifact = new DynamoDBArtifact();
        artifact.setResourceApplicationCode(resourceApplicationCode);

        DynamoDBQueryExpression<DynamoDBArtifact> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.setIndexName("created_index");
        queryExpression.setHashKeyValues(artifact);
        queryExpression.setScanIndexForward(false);

        if (!queryFilter.isEmpty()){
            queryExpression.setQueryFilter(queryFilter);
        }

        PaginatedQueryList<DynamoDBArtifact> dynamoDBArtifacts = dynamoMapper.query(DynamoDBArtifact.class, queryExpression);

        if (dynamoDBArtifacts.isEmpty()){
            return List.of();
        }

        return modelMapper.map(dynamoDBArtifacts.stream().toList(), new TypeToken<List<Artifact>>() {}.getType());
    }

    @Override
    public Optional<Artifact> findLatest(String resourceApplicationCode, Map<String, Object> filters) {
        Map<String, Condition> queryFilter = new HashMap<>();

        if(filters != null && filters.get("enabled") != null) {
            boolean enabled = (boolean) filters.get("enabled");
            queryFilter.put(
                    "enabled",
                    new Condition()
                            .withComparisonOperator("EQ")
                            .withAttributeValueList(new AttributeValue().withBOOL(enabled))
            );
        }

        DynamoDBArtifact artifact = new DynamoDBArtifact();
        artifact.setResourceApplicationCode(resourceApplicationCode);

        DynamoDBQueryExpression<DynamoDBArtifact> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.setIndexName("created_index");
        queryExpression.setHashKeyValues(artifact);
        queryExpression.setScanIndexForward(false);

        if (!queryFilter.isEmpty()) {
            queryExpression.setLimit(10);
            queryExpression.setQueryFilter(queryFilter);
        }else{
            queryExpression.setLimit(1);
        }

        PaginatedQueryList<DynamoDBArtifact> dynamoDBArtifacts = dynamoMapper.query(DynamoDBArtifact.class, queryExpression);

        if (dynamoDBArtifacts.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(modelMapper.map(dynamoDBArtifacts.get(0), Artifact.class));
    }

    @Override
    public Artifact save(Artifact artifact) {
        DynamoDBArtifact dynamoDBArtifact = modelMapper.map(artifact, DynamoDBArtifact.class);
        dynamoMapper.save(dynamoDBArtifact);
        return artifact;
    }
}
