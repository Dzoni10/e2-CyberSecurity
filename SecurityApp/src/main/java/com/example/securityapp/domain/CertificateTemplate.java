package com.example.securityapp.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class CertificateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Naziv

    @ManyToOne
    @JoinColumn(name = "issuer_certificate_id")
    private Certificate issuer; // CA koji je issuer

    private String commonNameRegex;
    private String subjectAltNameRegex;
    private Integer ttlDays;

    @ElementCollection
    @CollectionTable(name = "template_key_usage", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "usage")
    private List<String> keyUsage;

    @ElementCollection
    @CollectionTable(name = "template_extended_key_usage", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "usage")
    private List<String> extendedKeyUsage;

    @ManyToOne
    private User owner; // CA korisnik koji je napravio šablon

    // getteri/setteri

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

    public Certificate getIssuer() {
        return issuer;
    }

    public void setIssuer(Certificate issuer) {
        this.issuer = issuer;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

