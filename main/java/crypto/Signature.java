package crypto;

import java.security.PublicKey;
/**
 * Класс Signature представляет собой реализацию интерфейса цифровой подписи.
 */
public interface Signature {
    String sign(String data) throws Exception;
    boolean verify(String data, String signatureStr, PublicKey publicKey) throws Exception;
}
