package com.example.client;

import com.example.crypto.EncryptionService;
import com.example.crypto.SignatureService;
import com.example.utils.LoggerConfig;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

/**
 * Клиент для отправки зашифрованного и подписанного файла серверу.
 */
public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    /**
     * Главный метод для запуска клиента.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Запрос у пользователя алгоритма шифрования
        System.out.print("Выберите алгоритм шифрования (AES/DES): ");
        String encryptionAlgorithm = scanner.nextLine().toUpperCase();

        // Проверка на поддерживаемый алгоритм шифрования
        if (!encryptionAlgorithm.equals("AES") && !encryptionAlgorithm.equals("DES")) {
            System.out.println("Неподдерживаемый алгоритм шифрования. Используется AES по умолчанию.");
            encryptionAlgorithm = "AES";
        }

        // Запрос у пользователя алгоритма цифровой подписи
        System.out.print("Выберите алгоритм цифровой подписи (SHA256withRSA/SHA1withRSA): ");
        String signatureAlgorithm = scanner.nextLine().toUpperCase();

        // Проверка на поддерживаемый алгоритм цифровой подписи
        if (!signatureAlgorithm.equals("SHA256WITHRSA") && !signatureAlgorithm.equals("SHA1WITHRSA")) {
            System.out.println("Неподдерживаемый алгоритм цифровой подписи. Используется SHA256withRSA по умолчанию.");
            signatureAlgorithm = "SHA256withRSA";
        }

        // Запрос пути к файлу
        System.out.print("Введите путь к файлу для отправки: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        // Проверка существования файла
        if (!file.exists() || !file.isFile()) {
            LoggerConfig.logger.error("Файл не найден или это не файл: " + filePath);
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             FileInputStream fileInputStream = new FileInputStream(file)) {

            LoggerConfig.logger.info("Соединение с сервером установлено.");

            // Чтение содержимого файла
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            String fileContent = new String(fileBytes);

            // Шифрование данных
            EncryptionService encryptionService = new EncryptionService(encryptionAlgorithm);
            String encryptedData = encryptionService.encrypt(fileContent);

            // Создание цифровой подписи
            SignatureService signatureService = new SignatureService(signatureAlgorithm);
            PublicKey publicKey = signatureService.getPublicKey();
            String signature = signatureService.sign(encryptedData);

            // Отправка данных на сервер
            dataOutputStream.writeUTF(encryptionAlgorithm);
            dataOutputStream.writeUTF(signatureAlgorithm);
            dataOutputStream.writeUTF(encryptionService.getSecretKey());
            dataOutputStream.writeUTF(encryptedData);
            dataOutputStream.writeUTF(signature);
            dataOutputStream.writeUTF(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            LoggerConfig.logger.info("Файл зашифрован, подписан и отправлен серверу.");
        } catch (Exception e) {
            LoggerConfig.logger.error("Ошибка при отправке данных серверу: " + e.getMessage(), e);
        }
    }
}
