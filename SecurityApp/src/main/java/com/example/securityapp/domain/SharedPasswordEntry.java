package com.example.securityapp.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_password_entry")
public class SharedPasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long passwordEntryId;

    @Column(nullable = false)
    private Integer sharedWithUserId;

    @Column(nullable = false)
    private LocalDateTime sharedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encryptedPassword;

    public SharedPasswordEntry() {}

    public SharedPasswordEntry(Long passwordEntryId, Integer sharedWithUserId, String encryptedPassword) {
        this.passwordEntryId = passwordEntryId;
        this.sharedWithUserId = sharedWithUserId;
        this.encryptedPassword = encryptedPassword;
        this.sharedAt = LocalDateTime.now();
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPasswordEntryId() { return passwordEntryId; }
    public void setPasswordEntryId(Long passwordEntryId) { this.passwordEntryId = passwordEntryId; }

    public Integer getSharedWithUserId() { return sharedWithUserId; }
    public void setSharedWithUserId(Integer sharedWithUserId) { this.sharedWithUserId = sharedWithUserId; }

    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }

    public LocalDateTime getSharedAt() { return sharedAt; }
    public void setSharedAt(LocalDateTime sharedAt) { this.sharedAt = sharedAt; }

}