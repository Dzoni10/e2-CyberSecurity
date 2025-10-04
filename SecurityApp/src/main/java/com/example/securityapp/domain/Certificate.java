package com.example.securityapp.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String alias;
    private String serialNumber;
    private String cn;
    private String o;
    private String ou;
    private String c;
    private String issuer;
    private Integer issuerId;

    private boolean isRoot;
    private boolean isIntermediate;
    private boolean isEndEntity;
    private boolean isCA;


    private LocalDate startDate;
    private LocalDate endDate;

    private boolean revoked;

    // JSON string za ekstenzije (keyUsage, basicConstraints, ...)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String extensions;

    private Integer keyStoreMetaId;

    public Certificate() {
    }

    public Certificate(int id, String alias, String serialNumber,String cn,String o,String ou, String c, String issuer,Integer issuerId,boolean isRoot,boolean isIntermediate,boolean isEndEntity,boolean isCA, LocalDate startDate, LocalDate endDate, boolean revoked, String extensions, Integer keyStoreMetaId) {
        this.id = id;
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.cn = cn;
        this.o = o;
        this.ou = ou;
        this.c = c;
        this.issuer = issuer;
        this.issuerId = issuerId;
        this.isRoot=isRoot;
        this.isIntermediate=isIntermediate;
        this.isCA= isCA;
        this.isEndEntity=isEndEntity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.revoked = revoked;
        this.extensions = extensions;
        this.keyStoreMetaId = keyStoreMetaId;
    }

    public Certificate(String alias, String serialNumber,String cn,String o,String ou, String c,  String issuer,Integer issuerId,boolean isRoot,boolean isIntermediate,boolean isEndEntity,boolean isCA, LocalDate startDate, LocalDate endDate, boolean revoked, String extensions, Integer keyStoreMetaId) {
        this.alias = alias;
        this.serialNumber = serialNumber;
        this.cn = cn;
        this.o = o;
        this.ou = ou;
        this.c = c;
        this.issuer = issuer;
        this.issuerId = issuerId;
        this.isRoot=isRoot;
        this.isIntermediate=isIntermediate;
        this.isEndEntity=isEndEntity;
        this.isCA=isCA;
        this.startDate = startDate;
        this.endDate = endDate;
        this.revoked = revoked;
        this.extensions = extensions;
        this.keyStoreMetaId = keyStoreMetaId;
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

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isIntermediate() {
        return isIntermediate;
    }

    public void setIntermediate(boolean isIntermediate) {
        this.isIntermediate = isIntermediate;
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

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public Integer getKeyStoreMetaId() {
        return keyStoreMetaId;
    }

    public void setKeyStoreMetaId(Integer keyStoreMetaId) {
        this.keyStoreMetaId = keyStoreMetaId;
    }
}
