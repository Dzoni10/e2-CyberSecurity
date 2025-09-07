package com.example.securityapp.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private String alias;
    private String serialNumber;
    private String subject;
    private String issuer;

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean isCA;
    private boolean revoked;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String publicKey;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String privateKey;

    // JSON string za ekstenzije (keyUsage, basicConstraints, ...)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String extensions;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String encodedCertificate;

    public Certificate() {}

    public Certificate(int id,String alias,String serialNumber,String subject,String issuer, LocalDate startDate,LocalDate endDate, boolean isCA, boolean revoked, String publicKey, String privateKey, String extensions,String encodedCertificate) {
        this.id = id;
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.subject = subject;
        this.issuer = issuer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCA = isCA;
        this.revoked = revoked;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.extensions = extensions;
        this.encodedCertificate=encodedCertificate;
    }

    public Certificate(String alias,String serialNumber,String subject,String issuer, LocalDate startDate,LocalDate endDate, boolean isCA, boolean revoked, String publicKey, String privateKey, String extensions,String encodedCertificate){
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.subject = subject;
        this.issuer = issuer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCA = isCA;
        this.revoked = revoked;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.extensions = extensions;
        this.encodedCertificate=encodedCertificate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    public String getEncodedCertificate(){
        return encodedCertificate;
    }
    public void setEncodedCertificate(String encodedCertificate){
        this.encodedCertificate=encodedCertificate;
    }
}
