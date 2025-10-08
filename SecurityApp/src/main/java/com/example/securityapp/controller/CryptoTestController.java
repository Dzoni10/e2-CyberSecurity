package com.example.securityapp.controller;

import com.example.securityapp.config.CryptoUtils;
import com.example.securityapp.domain.KeyStoreMeta;
import com.example.securityapp.service.KeyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto-test")
public class CryptoTestController {

    @Autowired
    private KeyStoreService keyStoreService;

    @Value("${master.passphrase}")
    private String masterPassphrase;

    /**
     * Test enkriptovanja i dekriptovanja
     */
    @GetMapping("/test-encryption")
    public ResponseEntity<Map<String, String>> testEncryption() {
        Map<String, String> result = new HashMap<>();

        try {
            // 1. Generiši test lozinku (kao što bi KeyStore generisao)
            String testPassword = "TestPassword123!@#";
            result.put("1_original_password", testPassword);

            // 2. Enkriptuj
            String encrypted = CryptoUtils.encryptWithPassword(
                    testPassword.getBytes("UTF-8"),
                    masterPassphrase.toCharArray()
            );
            result.put("2_encrypted", encrypted);
            result.put("2_encrypted_length", String.valueOf(encrypted.length()));

            // 3. Dekriptuj
            byte[] decrypted = CryptoUtils.decryptWithPassword(
                    encrypted,
                    masterPassphrase.toCharArray()
            );
            String decryptedPassword = new String(decrypted, "UTF-8");
            result.put("3_decrypted", decryptedPassword);

            // 4. Proveri match
            boolean matches = testPassword.equals(decryptedPassword);
            result.put("4_matches", String.valueOf(matches));

            result.put("status", "SUCCESS ✓");

        } catch (Exception e) {
            result.put("status", "FAILED ✗");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Pokušaj dekriptovanja postojećeg KeyStoreMeta
     */
    @GetMapping("/decrypt-meta/{metaId}")
    public ResponseEntity<Map<String, String>> decryptMeta(@PathVariable Integer metaId) {
        Map<String, String> result = new HashMap<>();

        try {
            KeyStoreMeta meta = keyStoreService.getMetaById(metaId);

            result.put("meta_id", String.valueOf(meta.getId()));
            result.put("meta_path", meta.getPath());
            result.put("encrypted_password", meta.getEncryptedPassword());
            result.put("encrypted_length", String.valueOf(meta.getEncryptedPassword().length()));

            // Pokušaj dekriptovanja
            byte[] decrypted = CryptoUtils.decryptWithPassword(
                    meta.getEncryptedPassword(),
                    masterPassphrase.toCharArray()
            );

            String password = new String(decrypted, "UTF-8");
            result.put("decrypted_password_length", String.valueOf(password.length()));
            result.put("decrypted_password_preview", password.substring(0, Math.min(10, password.length())) + "...");
            result.put("status", "SUCCESS ✓");

        } catch (IllegalArgumentException e) {
            result.put("status", "FAILED - INVALID DATA ✗");
            result.put("error", "Encrypted data is too short or corrupted");
            result.put("hint", "The encrypted password in DB seems invalid. It should be at least 44 characters long.");
        } catch (Exception e) {
            result.put("status", "FAILED ✗");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Proveri sve KeyStoreMeta zapise u bazi
     */
    @GetMapping("/check-all-metas")
    public ResponseEntity<Map<String, Object>> checkAllMetas() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Ovo pretpostavlja da imate metod u KeyStoreService
            // Ako nemate, dodajte: public List<KeyStoreMeta> getAllMetas() { return metaRepo.findAll(); }

            result.put("message", "Add getAllMetas() method to KeyStoreService to implement this");
            result.put("status", "NOT_IMPLEMENTED");

        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Test samo master passphrase
     */
    @GetMapping("/check-master-passphrase")
    public ResponseEntity<Map<String, String>> checkMasterPassphrase() {
        Map<String, String> result = new HashMap<>();

        result.put("master_passphrase_set", masterPassphrase != null ? "YES" : "NO");
        result.put("master_passphrase_length", masterPassphrase != null ? String.valueOf(masterPassphrase.length()) : "0");
        result.put("master_passphrase_preview", masterPassphrase != null ?
                masterPassphrase.substring(0, Math.min(5, masterPassphrase.length())) + "..." : "NULL");

        return ResponseEntity.ok(result);
    }
}
