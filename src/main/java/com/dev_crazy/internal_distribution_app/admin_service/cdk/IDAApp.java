package com.dev_crazy.internal_distribution_app.admin_service.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class IDAApp {
    public static void main(final String[] args) {
        App app = new App();

        new IDAStack(app, "IDAStack", StackProps.builder()
                .env(Environment.builder()
                        .account("000000000000")
                        .region("us-east-1")
                        .build())
                .build());

        app.synth();
    }
}
