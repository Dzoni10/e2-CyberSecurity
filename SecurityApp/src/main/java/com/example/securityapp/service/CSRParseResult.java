package com.example.securityapp.service;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class CSRParseResult {
    private String subject;
    private String publicKeyAlgorithm;
    private int keySize;
    private PKCS10CertificationRequest pkcs10;

    public CSRParseResult() {}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }

    public void setPublicKeyAlgorithm(String publicKeyAlgorithm) {
        this.publicKeyAlgorithm = publicKeyAlgorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public PKCS10CertificationRequest getPkcs10() {
        return pkcs10;
    }

    public void setPkcs10(PKCS10CertificationRequest pkcs10) {
        this.pkcs10 = pkcs10;
    }
}

