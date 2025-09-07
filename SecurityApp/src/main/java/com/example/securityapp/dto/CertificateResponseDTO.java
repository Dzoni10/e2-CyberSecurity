package com.example.securityapp.dto;

import java.time.LocalDate;


//KADA FRONT TRAZI SVE SERTIFIKATE ILI POJEDINACAN BACK VRACA OVO


public class CertificateResponseDTO {

        private int id;
        private String alias;
        private String serialNumber;
        private String subject;
        private String issuer;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isCA;
        private boolean revoked;

        public CertificateResponseDTO() {}

        public CertificateResponseDTO(int id, String alias,String serialNumber,String subject,String issuer,LocalDate startDate,LocalDate endDate,boolean isCA, boolean revoked){
            this.id = id;
            this.alias = alias;
            this.serialNumber = serialNumber;
            this.subject = subject;
            this.issuer = issuer;
            this.startDate = startDate;
            this.endDate = endDate;
            this.isCA = isCA;
            this.revoked = revoked;
        }

    public CertificateResponseDTO(String alias,String serialNumber,String subject,String issuer,LocalDate startDate,LocalDate endDate,boolean isCA, boolean revoked){
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.subject = subject;
        this.issuer = issuer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCA = isCA;
        this.revoked = revoked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean CA) {
        isCA = CA;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
