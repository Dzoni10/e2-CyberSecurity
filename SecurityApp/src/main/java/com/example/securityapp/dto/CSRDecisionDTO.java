package com.example.securityapp.dto;

public class CSRDecisionDTO {
    private Long csrId;
    private boolean approved;
    private String rejectionReason; // ako je odbijen
    private Integer finalDurationDays; // admin može promeniti trajanje

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
