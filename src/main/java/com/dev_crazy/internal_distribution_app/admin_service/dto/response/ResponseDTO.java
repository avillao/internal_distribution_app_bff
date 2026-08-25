package com.dev_crazy.internal_distribution_app.admin_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private String message;
    private int status;
    private boolean error;
    private T data;
}
