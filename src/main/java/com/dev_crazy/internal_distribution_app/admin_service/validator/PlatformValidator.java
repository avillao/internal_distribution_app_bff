package com.dev_crazy.internal_distribution_app.admin_service.validator;

import com.dev_crazy.internal_distribution_app.admin_service.model.Platform;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class PlatformValidator implements ConstraintValidator<PlatformValid, String> {
    private Enum<Platform>[] enumConstants;

    @Override
    public void initialize(PlatformValid constraintAnnotation) {
        this.enumConstants = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.stream(enumConstants).anyMatch(
                e -> e.toString().equals(value)
        );
    }
}