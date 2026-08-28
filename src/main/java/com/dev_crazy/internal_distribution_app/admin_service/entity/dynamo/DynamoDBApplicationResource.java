package com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "application_resource")
public class DynamoDBApplicationResource {
    @DynamoDBHashKey(attributeName = "application_code")
    private String applicationCode;

    @DynamoDBRangeKey(attributeName = "resource_uuid")
    private String resourceId;

    @DynamoDBAttribute(attributeName = "platform")
    private String platform;

    @DynamoDBAttribute(attributeName = "branch")
    private String branch;
}
