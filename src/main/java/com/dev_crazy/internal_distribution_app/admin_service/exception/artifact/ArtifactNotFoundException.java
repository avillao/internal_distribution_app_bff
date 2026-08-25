package com.dev_crazy.internal_distribution_app.admin_service.exception.artifact;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;

public class ArtifactNotFoundException extends BaseServiceException {
    public ArtifactNotFoundException(String message, Throwable throwable) {
        super(message, 404, throwable);
    }

    public ArtifactNotFoundException(Throwable throwable){
        this("Artifact not found", throwable);
    }

    public ArtifactNotFoundException(){
        this(null);
    }
}
