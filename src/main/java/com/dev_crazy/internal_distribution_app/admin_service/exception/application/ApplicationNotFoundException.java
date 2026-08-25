package com.dev_crazy.internal_distribution_app.admin_service.exception.application;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;

public class ApplicationNotFoundException extends BaseServiceException {
    public ApplicationNotFoundException(String message, Throwable throwable){
        super(message, 404, throwable);
    }

    public ApplicationNotFoundException(Throwable throwable){
        this("Application not found", throwable);
    }

    public ApplicationNotFoundException(){
        this(null);
    }
}
