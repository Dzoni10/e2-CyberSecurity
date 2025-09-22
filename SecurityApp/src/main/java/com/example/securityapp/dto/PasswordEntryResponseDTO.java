package com.example.securityapp.dto;

import java.time.LocalDateTime;

public class PasswordEntryResponseDTO {
    private Long id;
    private String siteName;
    private String username;
    private String encryptedPassword;
    private LocalDateTime createdAt;

    public PasswordEntryResponseDTO() {}

    public PasswordEntryResponseDTO(Long id, String siteName, String username, String encryptedPassword, LocalDateTime createdAt) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.createdAt = createdAt;
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}