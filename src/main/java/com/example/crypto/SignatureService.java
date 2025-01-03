package com.example.crypto;

import java.security.*;
import java.util.Base64;

/**
 * Класс SignatureService представляет собой реализацию сервиса, который осуществляет подпись документа.
 */
public class SignatureService {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final String algorithm;

    /**
     * Конструктор класса SignatureService.
     *
     * @param algorithm Алгоритм цифровой подписи (например, "SHA256withRSA").
     * @throws NoSuchAlgorithmException Если алгоритм не поддерживается.
     */
    public SignatureService(String algorithm) throws NoSuchAlgorithmException {
        this.algorithm = algorithm;
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * Подписывает предоставленные данные с использованием закрытого ключа.
     *
     * @param data Данные для подписи.
     * @return Цифровая подпись, закодированная в Base64.
     * @throws Exception Если произошла ошибка при подписывании данных.
     */
    public String sign(String data) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    /**
     * Проверяет цифровую подпись для предоставленных данных с использованием открытого ключа.
     *
     * @param data Данные, для которых нужно проверить подпись.
     * @param signatureStr Цифровая подпись, закодированная в Base64.
     * @param publicKey Открытый ключ для проверки подписи.
     * @return true, если подпись верна; false в противном случае.
     * @throws Exception Если произошла ошибка при проверке подписи.
     */
    public boolean verify(String data, String signatureStr, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        byte[] digitalSignature = Base64.getDecoder().decode(signatureStr);
        return signature.verify(digitalSignature);
    }

    /**
     * Возвращает открытый ключ.
     *
     * @return Открытый ключ.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
