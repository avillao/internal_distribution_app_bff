package com.dev_crazy.internal_distribution_app.admin_service.cdk;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;

public class IDAStack extends Stack {
    public IDAStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public IDAStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        final String STAGE = System.getenv("SPRING_PROFILES_ACTIVE").toLowerCase().strip();
        final String NAME_STACK_APPLICATION = System.getenv("KEYCLOAK_CLIENT_ID").toLowerCase().strip();
        final String STACK_NAME = String.format("%s-%s", NAME_STACK_APPLICATION, STAGE);

        final boolean isProduction = STAGE.equals(System.getenv("prd")) || STAGE.equals(System.getenv("prod"));

        // DynamoDB
        TableV2 tblApplication = TableV2.Builder
                .create(this,"tblApplication")
                .tableName(String.format("%s-%s", STACK_NAME, "tbl-application"))
                .partitionKey(Attribute.builder().name("application_code").type(AttributeType.STRING).build())
                .billing(Billing.onDemand())
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();

        TableV2 tblApplicationResource = TableV2.Builder
                .create(this, "tblApplicationResource")
                .tableName(String.format("%s-%s", STACK_NAME, "tbl-application-resource"))
                .partitionKey(Attribute.builder().name("application_code").type(AttributeType.STRING).build())
                .sortKey(Attribute.builder().name("resource_uuid").type(AttributeType.STRING).build())
                .billing(Billing.onDemand())
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();

        TableV2 tblArtifact = TableV2.Builder
                .create(this, "tblArtifact")
                .tableName(String.format("%s-%s", STACK_NAME, "tbl-artifact"))
                .partitionKey(Attribute.builder().name("resource_application_code").type(AttributeType.STRING).build())
                .sortKey(Attribute.builder().name("artifact_uuid").type(AttributeType.STRING).build())
                .billing(Billing.onDemand())
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();

        tblArtifact.addGlobalSecondaryIndex(GlobalSecondaryIndexPropsV2.builder()
                        .indexName("created_index")
                        .partitionKey(Attribute.builder().name("resource_application_code").type(AttributeType.STRING).build())
                        .sortKey(Attribute.builder().name("created").type(AttributeType.STRING).build())
                        .projectionType(ProjectionType.ALL)
                .build());

        TableV2 tblArtifactBinary = TableV2.Builder
                .create(this, "tblArtifactBinary")
                .tableName(String.format("%s-%s", STACK_NAME, "tbl-artifact-binary"))
                .partitionKey(Attribute.builder().name("artifact_uuid").type(AttributeType.STRING).build())
                .billing(Billing.onDemand())
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();

        // S3
        Bucket artifactBucket = Bucket.Builder
                .create(this, "bucketArtifact")
                .bucketName(String.format("%s-%s", STACK_NAME, "bucket-artifact"))
                .versioned(false)
                .encryption(BucketEncryption.S3_MANAGED)
                .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();
    }
}