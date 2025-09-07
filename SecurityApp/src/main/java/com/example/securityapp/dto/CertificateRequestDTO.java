package com.example.securityapp.dto;

import java.util.Map;

//KADA KORISNIK POPUNI FORMU ZA IZDAVANJE SERTIFIKATA BACK PRIMA OVO

public class CertificateRequestDTO {

        public String subject;        // Podaci o vlasniku (X500Name string ili posebna polja)
        public Integer issuerId;         // ID CA sertifikata koji potpisuje
        public int durationInDays;    // trajanje sertifikata
        public boolean isCA;          // da li se izdaje kao intermediate ili end-entity
        public Map<String, String> extensions; // npr. keyUsage, basicConstraints, itd.

        public CertificateRequestDTO() {}

        public CertificateRequestDTO(String subject, Integer issuerId, int durationInDays, boolean isCA, Map<String, String> extensions) {
                this.subject = subject;
                this.issuerId = issuerId;
                this.durationInDays = durationInDays;
                this.isCA = isCA;
                this.extensions = extensions;
        }

}
