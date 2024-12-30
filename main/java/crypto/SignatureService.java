package crypto;

import java.security.*;
import java.util.Base64;
/**
 * Класс SignatureService представляет собой реализацию сервиса, который осуществляет подпись документа.
 */
public class SignatureService implements Signature {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final String algorithm;

    public SignatureService(String algorithm) throws Exception {
        this.algorithm = algorithm;
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    @Override
    public String sign(String data) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    @Override
    public boolean verify(String data, String signatureStr, PublicKey publicKey) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        byte[] digitalSignature = Base64.getDecoder().decode(signatureStr);
        return signature.verify(digitalSignature);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
