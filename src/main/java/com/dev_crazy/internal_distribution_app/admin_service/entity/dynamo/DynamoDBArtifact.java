package com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "artifact")
public class DynamoDBArtifact {
    @DynamoDBHashKey(attributeName = "resource_application_code")
    private String resourceApplicationCode;

    @DynamoDBRangeKey(attributeName = "artifact_uuid")
    private String artifactId;

    @DynamoDBAttribute(attributeName = "application_code")
    private String applicationCode;

    @DynamoDBAttribute(attributeName = "version")
    private String version;

    @DynamoDBAttribute(attributeName = "platform")
    private String platform;

    @DynamoDBAttribute(attributeName = "branch")
    private String branch;

    @DynamoDBAttribute(attributeName = "enabled")
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    private Boolean enabled;

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "created_index")
    private Date created;

    @DynamoDBAttribute(attributeName = "updated")
    private Date updated;
}
