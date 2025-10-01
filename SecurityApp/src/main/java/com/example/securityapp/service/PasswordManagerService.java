package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.CSRRepositoryInterface;
import com.example.securityapp.RepositoryInterfaces.PasswordEntryRepositoryInterface;
import com.example.securityapp.RepositoryInterfaces.SharedPasswordEntryRepositoryInterface;
import com.example.securityapp.domain.CertificateSigningRequest;
import com.example.securityapp.domain.PasswordEntry;
import com.example.securityapp.domain.SharedPasswordEntry;
import com.example.securityapp.dto.PasswordEntryRequestDTO;
import com.example.securityapp.dto.PasswordEntryResponseDTO;
import com.example.securityapp.dto.SharePasswordRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PasswordManagerService {

    @Autowired
    private PasswordEntryRepositoryInterface passwordEntryRepository;

    @Autowired
    private SharedPasswordEntryRepositoryInterface sharedPasswordEntryRepository;

    @Autowired
    private CSRRepositoryInterface csrRepository;

    public PasswordEntryResponseDTO savePasswordEntry(PasswordEntryRequestDTO request) {
        Integer userId = getCurrentUserId();

        // Proveri da li korisnik ima CSR sa javnim ključem
        if (!userHasPublicKey(userId)) {
            throw new RuntimeException("User must have uploaded CSR to use password manager");
        }

        PasswordEntry entry = new PasswordEntry(userId, request.siteName, request.username, request.encryptedPassword);
        PasswordEntry saved = passwordEntryRepository.save(entry);

        return mapToResponseDTO(saved);
    }

    public List<PasswordEntryResponseDTO> getUserPasswordEntries() {
        Integer userId = getCurrentUserId();

        // Vlastite lozinke
        List<PasswordEntry> ownEntries = passwordEntryRepository.findByOwnerId(userId);
        List<PasswordEntryResponseDTO> result = ownEntries.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        // Dodaj podeljene lozinke
        List<SharedPasswordEntry> sharedEntries = sharedPasswordEntryRepository.findBySharedWithUserId(userId);
        for (SharedPasswordEntry shared : sharedEntries) {
            PasswordEntry originalEntry = passwordEntryRepository.findById(shared.getPasswordEntryId())
                    .orElse(null);
            if (originalEntry != null) {
                PasswordEntryResponseDTO dto = new PasswordEntryResponseDTO(
                        shared.getId(),
                        originalEntry.getSiteName() + " (shared)",
                        originalEntry.getUsername(),
                        shared.getEncryptedPassword(), // enkriptovana za ovog korisnika
                        originalEntry.getCreatedAt()
                );
                result.add(dto);
            }
        }

        return result;
    }

    public void sharePassword(SharePasswordRequestDTO request) {
        Integer userId = getCurrentUserId();

        // Proveri da li je vlasnik
        PasswordEntry entry = passwordEntryRepository.findById(request.passwordEntryId)
                .orElseThrow(() -> new RuntimeException("Password entry not found"));

        if (!entry.getOwnerId().equals(userId)) {
            throw new RuntimeException("You can only share your own passwords");
        }

        // Proveri da li korisnik sa kojim deli ima javni ključ
        if (!userHasPublicKey(request.shareWithUserId)) {
            throw new RuntimeException("Target user must have CSR to receive shared passwords");
        }

        // Sačuvaj podeljenu lozinku
        SharedPasswordEntry sharedEntry = new SharedPasswordEntry(
                request.passwordEntryId,
                request.shareWithUserId,
                request.encryptedPasswordForUser
        );

        sharedPasswordEntryRepository.save(sharedEntry);
    }

    public void deletePasswordEntry(Long entryId) {
        Integer userId = getCurrentUserId();
        PasswordEntry entry = passwordEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Password entry not found"));

        if (!entry.getOwnerId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this entry");
        }

        // Obriši i sva deljenja
        List<SharedPasswordEntry> shares = sharedPasswordEntryRepository.findByPasswordEntryId(entryId);
        sharedPasswordEntryRepository.deleteAll(shares);

        passwordEntryRepository.delete(entry);
    }

    public String getUserPublicKey() {
        Integer userId = getCurrentUserId();
        System.out.println("DEBUG: Looking for public key for userId: " + userId);

        List<CertificateSigningRequest> userCSRs = csrRepository.findAll()
                .stream()
                .filter(csr -> {
                    System.out.println("DEBUG: CSR uploadedByUserId: " + csr.getUploadedByUserId());
                    return csr.getUploadedByUserId() != null && csr.getUploadedByUserId().equals(userId);
                })
                .filter(csr -> {
                    System.out.println("DEBUG: CSR publicKey: " + (csr.getPublicKey() != null ? "exists" : "null"));
                    return csr.getPublicKey() != null;
                })
                .sorted((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()))
                .collect(Collectors.toList());

        System.out.println("DEBUG: Found " + userCSRs.size() + " matching CSRs");

        if (userCSRs.isEmpty()) {
            throw new RuntimeException("User has no CSR with public key");
        }

        return userCSRs.get(0).getPublicKey();
    }

    public String getUserPublicKey(Integer targetUserId) {
        // Za deljenje - dobij javni ključ drugog korisnika
        List<CertificateSigningRequest> userCSRs = csrRepository.findAll()
                .stream()
                .filter(csr -> csr.getUploadedByUserId() != null && csr.getUploadedByUserId().equals(targetUserId))
                .filter(csr -> csr.getPublicKey() != null)
                .sorted((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()))
                .collect(Collectors.toList());

        if (userCSRs.isEmpty()) {
            throw new RuntimeException("Target user has no CSR with public key");
        }

        return userCSRs.get(0).getPublicKey();
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof Integer) {
            return (Integer) principal;
        }

        return 1; // fallback
    }

    private boolean userHasPublicKey(Integer userId) {
        return csrRepository.findAll()
                .stream()
                .anyMatch(csr -> csr.getUploadedByUserId() != null &&
                        csr.getUploadedByUserId().equals(userId) &&
                        csr.getPublicKey() != null);
    }

    private PasswordEntryResponseDTO mapToResponseDTO(PasswordEntry entry) {
        return new PasswordEntryResponseDTO(
                entry.getId(),
                entry.getSiteName(),
                entry.getUsername(),
                entry.getEncryptedPassword(),
                entry.getCreatedAt()
        );
    }

    public List<Map<String, Object>> getUsersWithPublicKeys() {
        Integer currentUserId = getCurrentUserId();

        return csrRepository.findAll().stream()
                .filter(csr -> csr.getPublicKey() != null && csr.getUploadedByUserId() != null)
                .filter(csr -> !csr.getUploadedByUserId().equals(currentUserId))
                .map(csr -> {
                    Map<String, Object> user = new HashMap<>();
                    user.put("userId", csr.getUploadedByUserId());
                    String cn = extractCommonName(csr.getSubject());
                    user.put("commonName", cn != null ? cn : "User " + csr.getUploadedByUserId());
                    return user;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private String extractCommonName(String subject) {
        if (subject == null) return null;

        // Traži CN= u subject stringu
        String[] parts = subject.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith("CN=")) {
                return trimmed.substring(3); // ukloni "CN="
            }
        }
        return null;
    }
}