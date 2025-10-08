package com.example.securityapp.dto;

import com.example.securityapp.validation.ValidationConstants;
import jakarta.validation.constraints.*;

public class CSRDecisionDTO {

    @NotNull(message = "CSR ID is required")
    @Positive(message = "CSR ID must be positive")
    private Long csrId;

    private boolean approved;

    @Size(max = 500, message = "Rejection reason cannot exceed {max} characters")
    private String rejectionReason;

    @Min(value = ValidationConstants.MIN_DURATION_DAYS,
            message = "Duration must be at least {value} day")
    @Max(value = ValidationConstants.MAX_DURATION_DAYS,
            message = "Duration cannot exceed {value} days")
    private Integer finalDurationDays;

    public CSRDecisionDTO() {}

    public Long getCsrId() {
        return csrId;
    }

    public void setCsrId(Long csrId) {
        this.csrId = csrId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getFinalDurationDays() {
        return finalDurationDays;
    }

    public void setFinalDurationDays(Integer finalDurationDays) {
        this.finalDurationDays = finalDurationDays;
    }
}