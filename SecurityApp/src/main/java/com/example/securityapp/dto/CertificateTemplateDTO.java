package com.example.securityapp.dto;

import java.util.List;

public class CertificateTemplateDTO {
    private Long id;
    private String name;
    private Integer issuerId;
    private String commonNameRegex;
    private String subjectAltNameRegex;
    private Integer ttlDays;
    private List<String> keyUsage;
    private List<String> extendedKeyUsage;

    public CertificateTemplateDTO() {}

    public CertificateTemplateDTO(Long id, String name, Integer ttlDays, String commonNameRegex,  String subjectAltNameRegex, List<String> keyUsage, List<String> extendedKeyUsage) {
        this.id = id;
        this.name = name;
        //this.issuerId = issuerId;
        this.commonNameRegex = commonNameRegex;
        this.subjectAltNameRegex = subjectAltNameRegex;
        this.ttlDays = ttlDays;
        this.keyUsage = keyUsage;
        this.extendedKeyUsage = extendedKeyUsage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Integer issuerId) {
        this.issuerId = issuerId;
    }

    public String getCommonNameRegex() {
        return commonNameRegex;
    }

    public void setCommonNameRegex(String commonNameRegex) {
        this.commonNameRegex = commonNameRegex;
    }

    public String getSubjectAltNameRegex() {
        return subjectAltNameRegex;
    }

    public void setSubjectAltNameRegex(String subjectAltNameRegex) {
        this.subjectAltNameRegex = subjectAltNameRegex;
    }

    public Integer getTtlDays() {
        return ttlDays;
    }

    public void setTtlDays(Integer ttlDays) {
        this.ttlDays = ttlDays;
    }

    public List<String> getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(List<String> keyUsage) {
        this.keyUsage = keyUsage;
    }

    public List<String> getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    public void setExtendedKeyUsage(List<String> extendedKeyUsage) {
        this.extendedKeyUsage = extendedKeyUsage;
    }
}

