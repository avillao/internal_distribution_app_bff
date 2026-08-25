package com.dev_crazy.internal_distribution_app.admin_service.exception;

import lombok.Data;

@Data
public class BaseServiceException extends RuntimeException {
    private int statusCode;

    public BaseServiceException(String message, int statusCode, Throwable throwable){
        super(message);
        this.statusCode = statusCode;
        this.initCause(throwable);
    }
}
