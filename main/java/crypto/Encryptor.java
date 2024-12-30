package crypto;
/**
 * Класс Encryptor представляет собой реализацию шифровальщика.
 */
public interface Encryptor {
    String encrypt(byte[] data) throws Exception;
    String decrypt(String encryptedData) throws Exception;
    String getSecretKey();
}
