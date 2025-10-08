package com.example.securityapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileExtensionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileExtension {
    String message() default "Invalid file extension";
    String[] extensions();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}