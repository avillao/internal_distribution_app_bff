package com.dev_crazy.internal_distribution_app.admin_service.exception.application;

import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;

public class ApplicationAlreadyExistsException extends BaseServiceException {
    public ApplicationAlreadyExistsException(String message, Throwable throwable){
        super(message, 400, throwable);
    }

    public ApplicationAlreadyExistsException(Throwable throwable){
        this("Already exists application with this applicationCode", throwable);
    }

    public ApplicationAlreadyExistsException(){
        this(null);
    }
}
