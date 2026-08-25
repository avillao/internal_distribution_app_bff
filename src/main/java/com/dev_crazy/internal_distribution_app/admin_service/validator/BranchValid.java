package com.dev_crazy.internal_distribution_app.admin_service.validator;

import com.dev_crazy.internal_distribution_app.admin_service.model.Branch;
import com.dev_crazy.internal_distribution_app.admin_service.model.Platform;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BranchValidator.class)
public @interface BranchValid {
    String message() default "Application Platform not valid, values allowed: ['android', 'ios']";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<Branch>> enumClass() default Branch.class;
}