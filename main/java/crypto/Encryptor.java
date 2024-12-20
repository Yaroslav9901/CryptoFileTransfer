package crypto;

public interface Encryptor {
    String encrypt(byte[] data) throws Exception;
    String decrypt(String encryptedData) throws Exception;
    String getSecretKey();
}
