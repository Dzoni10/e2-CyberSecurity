package com.example.securityapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileSize {
    String message() default "File size must not exceed 1MB";
    long maxSizeInBytes() default 1024 * 1024; // 1MB default
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}