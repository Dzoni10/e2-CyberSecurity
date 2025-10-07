package com.example.securityapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<ValidFileSize, MultipartFile> {

    private long maxSizeInBytes;

    @Override
    public void initialize(ValidFileSize constraintAnnotation) {
        this.maxSizeInBytes = constraintAnnotation.maxSizeInBytes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Let @NotNull handle null validation
        }

        return file.getSize() <= maxSizeInBytes;
    }
}