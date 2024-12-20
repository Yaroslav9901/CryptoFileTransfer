package crypto;

import java.security.PublicKey;

public interface Signature {
    String sign(String data) throws Exception;
    boolean verify(String data, String signatureStr, PublicKey publicKey) throws Exception;
}
