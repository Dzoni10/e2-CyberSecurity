package com.example.securityapp.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class CRLEntry {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String serialNumber; // povučeni sertifikat
        private LocalDateTime revokedAt;
        private String reason;       // X.509 reason code
        private Long certificateId;

        // getters/setters
        public CRLEntry() {}

        public CRLEntry(Long id, String serialNumber, LocalDateTime revokedAt, String reason, Long certificateId) {
            this.id = id;
            this.serialNumber = serialNumber;
            this.revokedAt = revokedAt;
            this.reason = reason;
            this.certificateId = certificateId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public LocalDateTime getRevokedAt() {
            return revokedAt;
        }

        public void setRevokedAt(LocalDateTime revokedAt) {
            this.revokedAt = revokedAt;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Long getCertificateId() {
            return certificateId;
        }

        public void setCertificateId(Long certificateId) {
            this.certificateId = certificateId;
        }

}
