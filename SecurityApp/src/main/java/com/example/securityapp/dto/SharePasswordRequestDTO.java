package com.example.securityapp.dto;

public class SharePasswordRequestDTO {
    public Long passwordEntryId;
    public Integer shareWithUserId;
    public String encryptedPasswordForUser; // enkriptovana javnim ključem drugog korisnika

    public SharePasswordRequestDTO() {}

    public SharePasswordRequestDTO(Long passwordEntryId, Integer shareWithUserId, String encryptedPasswordForUser) {
        this.passwordEntryId = passwordEntryId;
        this.shareWithUserId = shareWithUserId;
        this.encryptedPasswordForUser = encryptedPasswordForUser;
    }
}