package com.example.securityapp.config;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 65536;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Fiksni salt - možeš zameniti dinamičkim po deploymentu, ali pazi na kompatibilnost
    private static final byte[] FIXED_SALT = "SecurityApp-Salt".getBytes(java.nio.charset.StandardCharsets.UTF_8);

    // Generiši AES ključ iz master passphrase
    private static SecretKey getKeyFromPassword(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password, FIXED_SALT, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static String encryptWithPassword(byte[] plaintext, char[] password) throws Exception {
        SecretKey key = getKeyFromPassword(password);

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // AES/CBC zahteva IV od 16 bajtova
        byte[] iv = new byte[16];
        RANDOM.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encrypted = cipher.doFinal(plaintext);

        // Kombinuj IV (16) + encrypted
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static byte[] decryptWithPassword(String encryptedText, char[] password) throws Exception {
        SecretKey key = getKeyFromPassword(password);

        byte[] combined = Base64.getDecoder().decode(encryptedText);

        // minimalna dužina = IV(16) + 1
        if (combined.length <= 16) {
            throw new IllegalArgumentException("Invalid encrypted data - too short");
        }

        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 16];
        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(encrypted);
    }
}
