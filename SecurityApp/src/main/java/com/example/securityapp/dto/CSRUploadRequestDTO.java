package com.example.securityapp.dto;

import org.springframework.web.multipart.MultipartFile;

public class CSRUploadRequestDTO {
    private MultipartFile csrFile;  // .pem fajl
    private Long selectedCaId;      // odabrani CA
    private Integer requestedDurationDays;

    public CSRUploadRequestDTO() {}

    public CSRUploadRequestDTO(MultipartFile csrFile, Long selectedCaId, Integer requestedDurationDays) {
        this.csrFile = csrFile;
        this.selectedCaId = selectedCaId;
        this.requestedDurationDays = requestedDurationDays;
    }

    public Integer getRequestedDurationDays() {
        return requestedDurationDays;
    }

    public void setRequestedDurationDays(Integer requestedDurationDays) {
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
}
