package com.example.securityapp.controller;

import com.example.securityapp.dto.PasswordEntryRequestDTO;
import com.example.securityapp.dto.PasswordEntryResponseDTO;
import com.example.securityapp.dto.SharePasswordRequestDTO;
import com.example.securityapp.service.PasswordManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/password-manager")
public class PasswordManagerController {

    @Autowired
    private PasswordManagerService passwordManagerService;

    @PostMapping("/entries")
    @PreAuthorize("hasRole('BASIC')")
    public ResponseEntity<PasswordEntryResponseDTO> savePasswordEntry(@RequestBody PasswordEntryRequestDTO request) {
        PasswordEntryResponseDTO response = passwordManagerService.savePasswordEntry(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entries")
    @PreAuthorize("hasRole('BASIC')")
    public ResponseEntity<List<PasswordEntryResponseDTO>> getUserPasswordEntries() {
        List<PasswordEntryResponseDTO> entries = passwordManagerService.getUserPasswordEntries();
        return ResponseEntity.ok(entries);
    }

    @PostMapping("/share")
    @PreAuthorize("hasRole('BASIC')")
    public ResponseEntity<Void> sharePassword(@RequestBody SharePasswordRequestDTO request) {
        passwordManagerService.sharePassword(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/entries/{id}")
    @PreAuthorize("hasRole('BASIC')")
    public ResponseEntity<Void> deletePasswordEntry(@PathVariable Long id) {
        passwordManagerService.deletePasswordEntry(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public-key")
    @PreAuthorize("hasRole('BASIC')")
    public ResponseEntity<Map<String, String>> getUserPublicKey() {
        String publicKey = passwordManagerService.getUserPublicKey();
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }

    @GetMapping("/public-key/{userId}")
    @PreAuthorize("hasRole('BASIC')")
    public ResponseEntity<Map<String, String>> getUserPublicKey(@PathVariable Integer userId) {
        String publicKey = passwordManagerService.getUserPublicKey(userId);
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }
}