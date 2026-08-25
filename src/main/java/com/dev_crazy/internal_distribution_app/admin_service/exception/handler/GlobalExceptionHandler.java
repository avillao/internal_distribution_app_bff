package com.dev_crazy.internal_distribution_app.admin_service.exception.handler;

import com.dev_crazy.internal_distribution_app.admin_service.dto.response.ResponseDTO;
import com.dev_crazy.internal_distribution_app.admin_service.exception.BaseServiceException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {BaseServiceException.class})
    public ResponseEntity<ResponseDTO<String>> handleVersionException(BaseServiceException ex){
        ResponseDTO<String> response = new ResponseDTO<>();
        log.error("e: ", ex);
        response.setMessage(ex.getMessage());
        response.setError(true);
        response.setStatus(ex.getStatusCode());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    protected ResponseEntity<ResponseDTO<Map<String, Object>>> handleValidatorException(Exception ex) {
        ResponseDTO<Map<String, Object>> response = new ResponseDTO<>();
        log.error("e: ", ex);
        String errorFieldsMessage = "Campos faltantes o incorrectos";
        response.setMessage(errorFieldsMessage);
        response.setStatus(400);
        response.setError(true);

        Map<String, Object> errors = new HashMap<>();

        if (ex instanceof MethodArgumentTypeMismatchException exc){
            errors.put(exc.getPropertyName(), String.format("Valor [%s] no permitido", exc.getValue()));
        } else if(ex instanceof HttpMessageNotReadableException exc) {
            InvalidFormatException invExc = (InvalidFormatException)exc.getCause();
            errors.put(invExc.getPath().get(0).getFieldName(), String.format("Valor [%s] no permitido", invExc.getValue()));
        }else if (ex instanceof MissingServletRequestParameterException exc) {
            errors.put(exc.getParameterName(), "Campo obligatorio");
        }else if(ex instanceof MethodArgumentNotValidException exc){
            for (FieldError fieldError: exc.getBindingResult()
                    .getFieldErrors()){
                if(fieldError.isBindingFailure()){
                    errors.put(fieldError.getField(), "Valor no permitido");
                }else{

                    ConstraintViolation c = fieldError.unwrap(ConstraintViolation.class);

                    Iterator<Path.Node> propertyIterator = c.getPropertyPath().iterator();
                    String parentProperty = "";
                    int i = 1;

                    while(propertyIterator.hasNext()){
                        Path.Node node = propertyIterator.next();
                        if(node.getKind().name().equals("PROPERTY") && propertyIterator.hasNext()){
                            if(errors.get(node.toString()) == null){
                                errors.put(node.toString(),new HashMap<String, String>());
                            }

                            parentProperty = node.toString();
                        }else if(i > 1){
                            Map<String,String> property = (Map<String, String>) errors.get(parentProperty);
                            property.put(node.getName(), c.getMessage());

                        }else{
                            errors.put(node.getName(), c.getMessage());
                        }
                        i++;
                    }
                }

            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("errors",errors);

        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ResponseDTO<String>> handleMediaTypeException(HttpMediaTypeNotSupportedException exc){
        ResponseDTO<String> response = new ResponseDTO<>();
        log.error("e: ", exc);
        response.setMessage(exc.getMessage());
        response.setError(true);
        response.setStatus(415);
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseDTO<String>> handleException(Exception ex){
        ResponseDTO<String> response = new ResponseDTO<>();
        log.error("e: ", ex);
        response.setMessage("Error");
        response.setStatus(500);
        response.setError(true);
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
