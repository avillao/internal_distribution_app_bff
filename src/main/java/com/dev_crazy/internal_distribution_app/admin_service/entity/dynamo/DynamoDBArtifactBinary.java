package com.dev_crazy.internal_distribution_app.admin_service.entity.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "tbl-artifact-binary")
public class DynamoDBArtifactBinary {
    @DynamoDBHashKey(attributeName = "artifact_uuid")
    private String artifactId;

    @DynamoDBAttribute(attributeName = "filename")
    private String filename;

    @DynamoDBAttribute(attributeName = "type")
    private String type;

    @DynamoDBAttribute(attributeName = "filesize")
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.N)
    private Integer filesize;

    @DynamoDBAttribute(attributeName = "checksum")
    private String checksum;

    @DynamoDBAttribute(attributeName = "checksumtype")
    private String checksumtype;

    @DynamoDBAttribute(attributeName = "keypath")
    private String keypath;
}
