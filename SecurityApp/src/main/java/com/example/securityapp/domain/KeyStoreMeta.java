package com.example.securityapp.domain;

import jakarta.persistence.*;

@Entity
public class KeyStoreMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String path;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String encryptedPassword;

    public KeyStoreMeta(){}

    public KeyStoreMeta(int id,String path, String encryptedPassword) {
        this.id = id;
        this.path = path;
        this.encryptedPassword = encryptedPassword;
    }

    public KeyStoreMeta(String path, String encryptedPassword) {
        this.path = path;
        this.encryptedPassword = encryptedPassword;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
