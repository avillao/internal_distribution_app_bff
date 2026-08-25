package com.dev_crazy.internal_distribution_app.admin_service.exception.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;

public class ArtifactVersionException extends BaseServiceException {
    public ArtifactVersionException(String message, Throwable throwable) {
        super(message, 400, throwable);
    }

    public ArtifactVersionException(Throwable throwable){
        this("Artifact version is incorrect", throwable);
    }

    public ArtifactVersionException(){
        this(null);
    }
}
