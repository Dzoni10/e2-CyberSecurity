package com.example.securityapp.service;

import com.example.securityapp.RepositoryInterfaces.KeyStoreMetaRepository;
import com.example.securityapp.config.CryptoUtils;
import com.example.securityapp.domain.KeyStoreMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Service
public class KeyStoreService {

    private final KeyStoreMetaRepository metaRepo;

    @Value("${keystore.storage.dir:/var/securityapp/keystores}")
    private String keystoreDir;

    // passphrase za dekriptovanje (iz ENV)
    @Value("${master.passphrase}")
    private String masterPassphrase;

    public KeyStoreService(KeyStoreMetaRepository metaRepo) {
        this.metaRepo = metaRepo;
    }

    public KeyStoreMeta createAndStoreKeyStore(
            X509Certificate[] chain,
            PrivateKey privateKey,
            String alias,
            Integer createdBy
    ) {
        try {
            // 1) generate random keystore password
            byte[] pwdBytes = new byte[32];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(pwdBytes);
            String ksPassword = Base64.getEncoder().encodeToString(pwdBytes);

            // 2) create keystore (PKCS12)
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(null, null);
            ks.setKeyEntry(alias, privateKey, ksPassword.toCharArray(), chain);

            // ensure dir exists
            Path dir = Paths.get(keystoreDir);
            Files.createDirectories(dir);

            String filename = "keystore-" + System.currentTimeMillis() + "-" + sr.nextInt(9999) + ".p12";
            Path file = dir.resolve(filename);
            try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
                ks.store(fos, ksPassword.toCharArray());
            }

            // 3) encrypt ksPassword with master passphrase

            System.out.println("========== KeyStore DEBUG ==========");
            System.out.println("Keystore path: " + file.toString());
            System.out.println("Master passphrase (iz .env): " + masterPassphrase);
            System.out.println("Keystore password (pre enkripcije): " + ksPassword);


            String encryptedPassword = CryptoUtils.encryptWithPassword(ksPassword.getBytes("UTF-8"), masterPassphrase.toCharArray());

            System.out.println("Encrypted password (Base64, upisana u bazu): " + encryptedPassword);
            System.out.println("=====================================");


            // 4) persist KeyStoreMeta
            KeyStoreMeta meta = new KeyStoreMeta();
            meta.setPath(file.toString());
            meta.setEncryptedPassword(encryptedPassword);

            return metaRepo.save(meta);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create keystore: " + e.getMessage(), e);
        }
    }

    public KeyStore loadKeyStore(KeyStoreMeta meta) {
        try {
            String enc = meta.getEncryptedPassword();
            byte[] decrypted = CryptoUtils.decryptWithPassword(enc, masterPassphrase.toCharArray());
            String ksPassword = new String(decrypted, "UTF-8");

            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (var fis = Files.newInputStream(Paths.get(meta.getPath()))) {
                ks.load(fis, ksPassword.toCharArray());
            }
            return ks;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load keystore: " + e.getMessage(), e);
        }
    }

    // da bih mogao da se izvuce issuer private key kada se potpisuje sertifikat
    public KeyStoreMeta getMetaById(Integer id) {
        return metaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Keystore meta not found: " + id));
    }

    //iz keystore izvlaci private key preko aliasa
    public PrivateKey loadPrivateKey(KeyStoreMeta meta, String alias) {
        try {
            // dekodiraj password
            byte[] decrypted = CryptoUtils.decryptWithPassword(meta.getEncryptedPassword(), masterPassphrase.toCharArray());
            String ksPassword = new String(decrypted, "UTF-8");

            // učitaj keystore
            KeyStore ks = loadKeyStore(meta);

            // izvuci ključ
            return (PrivateKey) ks.getKey(alias, ksPassword.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key for alias " + alias, e);
        }
    }

    public X509Certificate loadCertificate(KeyStoreMeta meta, String alias) {
        try {
            KeyStore ks = loadKeyStore(meta);
            return (X509Certificate) ks.getCertificate(alias);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load certificate for alias " + alias, e);
        }
    }

}
