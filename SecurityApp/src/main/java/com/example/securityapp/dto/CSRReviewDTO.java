package com.example.securityapp.dto;

import com.example.securityapp.domain.CSRStatus;

import java.time.LocalDateTime;

//prikaz adminu
public class CSRReviewDTO {
    private Long id;
    private String filename;
    private LocalDateTime uploadedAt;
    private String uploaderUsername;
    private String subject;         // parsed iz CSR-a
    private String publicKeyInfo;   // algoritam + key size
    private Long selectedCaId;
    private String selectedCaName;
    private Integer requestedDurationDays;
    private CSRStatus status;

    public CSRReviewDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublicKeyInfo() {
        return publicKeyInfo;
    }

    public void setPublicKeyInfo(String publicKeyInfo) {
        this.publicKeyInfo = publicKeyInfo;
    }

    public Long getSelectedCaId() {
        return selectedCaId;
    }

    public void setSelectedCaId(Long selectedCaId) {
        this.selectedCaId = selectedCaId;
    }

    public String getSelectedCaName() {
        return selectedCaName;
    }

    public void setSelectedCaName(String selectedCaName) {
        this.selectedCaName = selectedCaName;
    }

    public Integer getRequestedDurationDays() {
        return requestedDurationDays;
    }

    public void setRequestedDurationDays(Integer requestedDurationDays) {
        this.requestedDurationDays = requestedDurationDays;
    }

    public CSRStatus getStatus() {
        return status;
    }

    public void setStatus(CSRStatus status) {
        this.status = status;
    }
}
