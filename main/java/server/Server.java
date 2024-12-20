package server;

import crypto.EncryptionService;
import crypto.SignatureService;
import utils.LoggerConfig;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Сервер для приема зашифрованных и подписанных данных от клиента.
 */
public class Server {

    private static final int PORT = 12345;
    private static final String PROJECT_DIRECTORY = System.getProperty("user.dir");
    private static boolean running = false;

    public static void main(String[] args) {
        running = true; // Установка состояния сервера в запущенное

        LoggerConfig.logger.info("Сервер запущен и ожидает подключения на порту " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (running) {
                LoggerConfig.logger.info("Ожидание подключения клиента...");
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    LoggerConfig.logger.error("Ошибка при обработке подключения клиента: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            LoggerConfig.logger.error("Ошибка при запуске сервера: " + e.getMessage(), e);
        }
    }

    private static void handleClient(Socket clientSocket) {
        LoggerConfig.logger.info("Клиент подключен: " + clientSocket.getInetAddress());
        try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream())) {
            String encryptionAlgorithm = dataInputStream.readUTF();
            String signatureAlgorithm = dataInputStream.readUTF();
            String secretKey = dataInputStream.readUTF();
            String encryptedData = dataInputStream.readUTF();
            String signature = dataInputStream.readUTF();
            String publicKeyStr = dataInputStream.readUTF();
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));

            EncryptionService encryptionService = new EncryptionService(secretKey, encryptionAlgorithm);

            String decryptedData = encryptionService.decrypt(encryptedData);
            LoggerConfig.logger.info("Данные успешно расшифрованы.");

            SignatureService signatureService = new SignatureService(signatureAlgorithm);
            boolean isVerified = signatureService.verify(encryptedData, signature, publicKey);

            if (isVerified) {
                LoggerConfig.logger.info("Цифровая подпись успешно проверена. Данные подлинны.");
                saveToFile(decryptedData, "received_data.txt");
            } else {
                LoggerConfig.logger.warn("Цифровая подпись не прошла проверку! Данные могут быть подделаны.");
                saveToFile(decryptedData, "unverified_data.txt");
            }
        } catch (Exception e) {
            LoggerConfig.logger.error("Ошибка при обработке данных клиента: " + e.getMessage(), e);
        }
    }

    private static void saveToFile(String data, String fileName) {
        File file = new File(PROJECT_DIRECTORY, fileName);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(data);
                LoggerConfig.logger.info("Данные сохранены в файл: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            LoggerConfig.logger.error("Ошибка при сохранении файла: " + e.getMessage(), e);
        }
    }

    public static boolean isRunning() {
        return running;
    }
}