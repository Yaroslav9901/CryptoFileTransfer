package com.example.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Сервис для шифрования и расшифровки данных.
 */
public class EncryptionService {

    private final SecretKey secretKey;
    private final Cipher cipher;
    private final String algorithm;

    /**
     * Конструктор для генерации нового ключа шифрования.
     * @param algorithm Алгоритм шифрования (AES или DES).
     */
    public EncryptionService(String algorithm) throws Exception {
        this.algorithm = algorithm;
        this.secretKey = generateSecretKey(algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    /**
     * Конструктор для использования существующего ключа.
     * @param base64Key Секретный ключ в формате Base64.
     * @param algorithm Алгоритм шифрования (AES или DES).
     */
    public EncryptionService(String base64Key, String algorithm) throws Exception {
        this.algorithm = algorithm;
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.secretKey = new SecretKeySpec(decodedKey, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    /**
     * Шифрование данных.
     * @param data Данные для шифрования.
     * @return Зашифрованные данные в формате Base64.
     */
    public String encrypt(String data) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Расшифровка данных.
     * @param encryptedData Зашифрованные данные в формате Base64.
     * @return Расшифрованные данные.
     */
    public String decrypt(String encryptedData) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    /**
     * Получение секретного ключа в формате Base64.
     * @return Секретный ключ.
     */
    public String getSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Генерация секретного ключа.
     * @param algorithm Алгоритм шифрования.
     * @return Секретный ключ.
     */
    private SecretKey generateSecretKey(String algorithm) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        if (algorithm.equals("AES")) {
            keyGenerator.init(256);
        } else if (algorithm.equals("DES")) {
            keyGenerator.init(56);
        }
        return keyGenerator.generateKey();
    }
}