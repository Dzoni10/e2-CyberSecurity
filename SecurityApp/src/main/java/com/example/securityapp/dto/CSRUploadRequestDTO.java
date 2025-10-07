package com.example.securityapp.dto;

import com.example.securityapp.validation.ValidFileExtension;
import com.example.securityapp.validation.ValidFileSize;
import com.example.securityapp.validation.ValidationConstants;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class CSRUploadRequestDTO {

    @NotNull(message = "CSR file is required")
    @ValidFileExtension(extensions = {".csr", ".pem"},
            message = "CSR file must have .csr or .pem extension")
    @ValidFileSize(maxSizeInBytes = ValidationConstants.MAX_FILE_SIZE,
            message = ValidationConstants.FILE_SIZE_EXCEEDED_MSG)
    private MultipartFile csrFile;

    @NotNull(message = "CA selection is required")
    @Positive(message = "Selected CA ID must be positive")
    private Long selectedCaId;

    @NotNull(message = "Duration is required")
    @Min(value = ValidationConstants.MIN_DURATION_DAYS,
            message = "Duration must be at least {value} day")
    @Max(value = ValidationConstants.MAX_DURATION_DAYS,
            message = "Duration cannot exceed {value} days")
    private Integer requestedDurationDays;

    public CSRUploadRequestDTO() {}

    public CSRUploadRequestDTO(MultipartFile csrFile, Long selectedCaId, Integer requestedDurationDays) {
        this.csrFile = csrFile;
        this.selectedCaId = selectedCaId;
        this.requestedDurationDays = requestedDurationDays;
    }

    public MultipartFile getCsrFile() {
        return csrFile;
    }

    public void setCsrFile(MultipartFile csrFile) {
        this.csrFile = csrFile;
    }

    public Long getSelectedCaId() {
        return selectedCaId;
    }

    public void setSelectedCaId(Long selectedCaId) {
        this.selectedCaId = selectedCaId;
    }

    public Integer getRequestedDurationDays() {
        return requestedDurationDays;
    }

    public void setRequestedDurationDays(Integer requestedDurationDays) {
        this.requestedDurationDays = requestedDurationDays;
    }
}