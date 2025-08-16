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


}
