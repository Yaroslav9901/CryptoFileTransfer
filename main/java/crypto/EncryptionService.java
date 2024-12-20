package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionService implements Encryptor {

    private final SecretKey secretKey;
    private final Cipher cipher;
    private final String algorithm;

    // Конструктор для генерации нового ключа шифрования
    public EncryptionService(String algorithm) throws Exception {
        this.algorithm = algorithm;
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(128); // для здоровья алгоритмов, измените под нужды
        this.secretKey = keyGenerator.generateKey();
        this.cipher = Cipher.getInstance(algorithm);
    }

    // Конструктор для работы с существующим секретным ключом
    public EncryptionService(String base64Key, String algorithm) throws Exception {
        this.algorithm = algorithm;
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.secretKey = new SecretKeySpec(decodedKey, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    @Override
    public String encrypt(byte[] data) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public String getSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}