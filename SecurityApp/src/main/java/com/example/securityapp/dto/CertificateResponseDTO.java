package com.example.securityapp.dto;

import java.time.LocalDate;


//KADA FRONT TRAZI SVE SERTIFIKATE ILI POJEDINACAN BACK VRACA OVO


public class CertificateResponseDTO {

        private int id;
        private String alias;
        private String serialNumber;
        private String cn;
        private String o;
        private String ou;
        private String c;
        private boolean isRoot;
        private boolean isEndEntity;
        private String issuer;
        private Integer issuerId;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isIntermediate;
        private boolean isCA;
        private boolean revoked;

        public CertificateResponseDTO() {}

        public CertificateResponseDTO(int id, String alias,String serialNumber,String cn,String o,String ou, String c,String issuer,LocalDate startDate,LocalDate endDate,Integer issuerId,boolean isRoot,boolean isIntermediate,boolean isEndEntity,boolean isCA, boolean revoked){
            this.id = id;
            this.alias = alias;
            this.serialNumber = serialNumber;
            this.cn = cn;
            this.o = o;
            this.ou = ou;
            this.c = c;
            this.issuer = issuer;
            this.startDate = startDate;
            this.endDate = endDate;
            this.issuerId = issuerId;
            this.isRoot=isRoot;
            this.isIntermediate = isIntermediate;
            this.isEndEntity=isEndEntity;
            this.isCA=isCA;
            this.revoked = revoked;

        }

    public CertificateResponseDTO(String alias,String serialNumber,String cn,String o,String ou, String c,String issuer,LocalDate startDate,LocalDate endDate,Integer issuerId,boolean isRoot,boolean isIntermediate,boolean isEndEntity,boolean isCA, boolean revoked){
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.o = o;
        this.ou = ou;
        this.c = c;
        this.issuer = issuer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issuerId = issuerId;
        this.isRoot=isRoot;
        this.isIntermediate = isIntermediate;
        this.isEndEntity=isEndEntity;
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

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isEndEntity() {
        return isEndEntity;
    }

    public void setEndEntity(boolean endEntity) {
        isEndEntity = endEntity;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean CA) {
        isCA = CA;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Integer getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Integer issuerId) {
        this.issuerId = issuerId;
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

    public boolean isIntermediate() {
        return isIntermediate;
    }

    public void setIntermediate(boolean isIntermediate) {
        this.isIntermediate = isIntermediate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
