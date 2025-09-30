package com.example.securityapp.config;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtils {

    private static final String KDF_ALGO = "PBKDF2WithHmacSHA256";
    private static final int KDF_ITER = 200_000;
    private static final int KEY_LEN = 32; // 256 bit
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String encryptWithPassword(byte[] plaintext, char[] password) throws Exception {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        SecretKey key = deriveKey(password, salt);

        byte[] iv = new byte[12];
        RANDOM.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] cipherText = cipher.doFinal(plaintext);

        // store as: salt || iv || cipherText  (all Base64)
        byte[] out = new byte[salt.length + iv.length + cipherText.length];
        System.arraycopy(salt, 0, out, 0, salt.length);
        System.arraycopy(iv, 0, out, salt.length, iv.length);
        System.arraycopy(cipherText, 0, out, salt.length + iv.length, cipherText.length);
        return Base64.getEncoder().encodeToString(out);
    }

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        PBEKeySpec ks = new PBEKeySpec(password, salt, KDF_ITER, KEY_LEN * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF_ALGO);
        byte[] key = skf.generateSecret(ks).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    public static byte[] decryptWithPassword(String b64Input, char[] password) throws Exception {
        byte[] all = Base64.getDecoder().decode(b64Input);
        byte[] salt = Arrays.copyOfRange(all, 0, 16);
        byte[] iv = Arrays.copyOfRange(all, 16, 28);
        byte[] cipherText = Arrays.copyOfRange(all, 28, all.length);
        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return cipher.doFinal(cipherText);
    }

}
