package com.example.securityapp.dto;

public class PasswordEntryRequestDTO {
    public String siteName;
    public String username;
    public String encryptedPassword; // već enkriptovana na frontendu

    public PasswordEntryRequestDTO() {}

    public PasswordEntryRequestDTO(String siteName, String username, String encryptedPassword) {
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }
}