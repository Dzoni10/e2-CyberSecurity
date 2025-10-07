package com.example.securityapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileExtensionValidator implements ConstraintValidator<ValidFileExtension, MultipartFile> {

    private String[] allowedExtensions;

    @Override
    public void initialize(ValidFileExtension constraintAnnotation) {
        this.allowedExtensions = constraintAnnotation.extensions();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String lowerFilename = filename.toLowerCase();
        return Arrays.stream(allowedExtensions)
                .anyMatch(lowerFilename::endsWith);
    }
}