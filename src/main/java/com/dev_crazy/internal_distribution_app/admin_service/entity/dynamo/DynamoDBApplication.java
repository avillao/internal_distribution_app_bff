package com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "application")
public class DynamoDBApplication {
    @DynamoDBHashKey(attributeName = "application_code")
    private String applicationCode;

    @DynamoDBAttribute(attributeName = "name")
    private String name;

    @DynamoDBAttribute(attributeName = "package_name")
    private String packageName;

    @DynamoDBAttribute(attributeName = "enabled")
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    private Boolean enabled;

    @DynamoDBAttribute(attributeName = "created")
    private Date created;

    @DynamoDBAttribute(attributeName = "updated")
    private Date updated;
}
