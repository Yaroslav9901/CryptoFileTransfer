package client;

import crypto.EncryptionService;
import crypto.SignatureService;
import utils.LoggerConfig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Base64;

/**
 * Клиент для отправки зашифрованного и подписанного файла серверу.
 */
public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Необходимо указать путь к файлу, алгоритм шифрования и алгоритм подписи.");
            return;
        }

        String filePath = args[0];
        String encryptionAlgorithm = args[1];
        String signatureAlgorithm = args[2];

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            LoggerConfig.logger.error("Файл не найден или это не файл: " + filePath);
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             FileInputStream fileInputStream = new FileInputStream(file)) {

            LoggerConfig.logger.info("Соединение с сервером установлено.");

            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);

            EncryptionService encryptionService = new EncryptionService(encryptionAlgorithm);
            String encryptedData = encryptionService.encrypt(fileBytes);
            LoggerConfig.logger.info("Файл зашифрован.");

            SignatureService signatureService = new SignatureService(signatureAlgorithm);
            String signature = signatureService.sign(encryptedData);
            LoggerConfig.logger.info("Файл подписан.");

            // Передача информации на сервер
            dataOutputStream.writeUTF(encryptionAlgorithm);
            dataOutputStream.writeUTF(signatureAlgorithm);
            dataOutputStream.writeUTF(encryptionService.getSecretKey());
            dataOutputStream.writeUTF(encryptedData);
            dataOutputStream.writeUTF(signature);
            dataOutputStream.writeUTF(Base64.getEncoder().encodeToString(signatureService.getPublicKey().getEncoded()));
            LoggerConfig.logger.info("Файл отправлен серверу.");
        } catch (Exception e) {
            LoggerConfig.logger.error("Ошибка при отправке данных серверу: " + e.getMessage(), e);
        }
    }

    public static boolean isRunning() {
        return true;
    }
}
