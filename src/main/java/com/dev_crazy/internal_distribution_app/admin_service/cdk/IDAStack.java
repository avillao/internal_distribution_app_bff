package com.dev_crazy.internal_distribution_app.admin_service.cdk;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.*;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class IDAStack extends Stack {
    public IDAStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public IDAStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        final String STAGE = System.getenv("SPRING_PROFILES_ACTIVE").toLowerCase().strip();
        final String NAME_STACK_APPLICATION = System.getenv("KEYCLOAK_CLIENT_ID").toLowerCase().strip();
        final String STACK_NAME = String.format("%s_%s", NAME_STACK_APPLICATION, STAGE);

        final boolean isProduction = STAGE.equals(System.getenv("prd")) || STAGE.equals(System.getenv("prod"));

        TableV2 tblApplication = TableV2.Builder
                .create(this,"tblApplication")
                .tableName(String.format("%s_%s", STACK_NAME, "tbl_application"))
                .partitionKey(Attribute.builder().name("application_code").type(AttributeType.STRING).build())
                .billing(Billing.onDemand())
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();

        TableV2 tblApplicationResource = TableV2.Builder
                .create(this, "tblApplicationResource")
                .tableName(String.format("%s_%s", STACK_NAME, "tbl_application_resource"))
                .partitionKey(Attribute.builder().name("application_code").type(AttributeType.STRING).build())
                .sortKey(Attribute.builder().name("resource_uuid").type(AttributeType.STRING).build())
                .billing(Billing.onDemand())
                .removalPolicy(isProduction ? RemovalPolicy.RETAIN : RemovalPolicy.DESTROY)
                .build();

        TableV2 tblArtifact = TableV2.Builder
                .create(this, "tblArtifact")
                .tableName(String.format("%s_%s", STACK_NAME, "tbl_artifact"))
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

    }
}