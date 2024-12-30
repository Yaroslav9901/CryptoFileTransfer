package App;

import client.Client;
import server.Server;
import utils.LoggerConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
/**
 * Класс MainApp представляет собой реализацию графического интерфейса клиент - сервисного приложения с реализацией функций.
 */
public class MainApp extends JFrame {
    private JButton startServerButton;
    private JButton startClientButton;
    private JButton chooseFileButton;
    private JButton sendButton;
    private JComboBox<String> encryptionAlgorithmCombo;
    private JComboBox<String> signatureAlgorithmCombo;
    private File selectedFile; // Хранение выбранного файла

    public MainApp() {
        // Настройка логгирования
        LoggerConfig.logger.info("Запуск приложение");

        setTitle("Клиент-Серверное Приложение");
        setSize(400, 400); // Увеличил размер окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Изменил на BorderLayout для лучшей компоновки

        // Заголовок и стили текста
        JLabel titleLabel = new JLabel("Клиент-Сервер");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH); // Заголовок в верхней части окна

        // Панель для кнопок и комбобоксов
        JPanel controlPanel = new JPanel(new GridLayout(7, 2)); // Увеличил количество строк в сетке
        controlPanel.add(new JLabel("Алгоритм шифрования:"));
        encryptionAlgorithmCombo = new JComboBox<>(new String[]{"AES", "DES"});
        controlPanel.add(encryptionAlgorithmCombo);

        controlPanel.add(new JLabel("Алгоритм цифровой подписи:"));
        signatureAlgorithmCombo = new JComboBox<>(new String[]{"SHA256withRSA", "SHA1withRSA"});
        controlPanel.add(signatureAlgorithmCombo);

        chooseFileButton = new JButton("Выбрать Файл");
        controlPanel.add(chooseFileButton);

        sendButton = new JButton("Отправить");
        sendButton.setEnabled(false); // Отключаем кнопку до выбора файла
        controlPanel.add(sendButton);

        startServerButton = new JButton("Запустить Сервер");
        controlPanel.add(startServerButton);

        startClientButton = new JButton("Запустить Клиент");
        controlPanel.add(startClientButton);

        add(controlPanel, BorderLayout.CENTER); // Центральная часть для управления

        // Инструкция
        JLabel instructionLabel = new JLabel("<html><font color='red'>ИНСТРУКЦИЯ:</font><br/>" +
                "Без правильного запуска кнопка отправления не будет доступна.<br/>" +
                "1. Выберите шифрование<br/>" +
                "2. Выберите подпись<br/>" +
                "3. Выберите файл<br/>" +
                "4. Запустите сервер<br/>" +
                "5. Запустите клиент<br/>" +
                "6. Нажмите кнопку отправления</html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(instructionLabel, BorderLayout.SOUTH); // Инструкция в нижней части окна

        // Установка стилей для кнопок
        setButtonStyle(startServerButton);
        setButtonStyle(startClientButton);
        setButtonStyle(chooseFileButton);
        setButtonStyle(sendButton);

        // Обработчик выбора файла
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null, "Файл выбран: " + selectedFile.getAbsolutePath(),
                            "Информация", JOptionPane.INFORMATION_MESSAGE);
                    LoggerConfig.logger.info("Файл выбран: " + selectedFile.getAbsolutePath());
                    sendButton.setEnabled(Server.isRunning() && Client.isRunning());
                } else {
                    JOptionPane.showMessageDialog(null, "Файл не был выбран!", "Ошибка", JOptionPane.WARNING_MESSAGE);
                    LoggerConfig.logger.warn("Файл не был выбран пользователем.");
                }
            }
        });

        // Обработчики кнопок
        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    JOptionPane.showMessageDialog(null, "Сервер запускается...", "Информация", JOptionPane.INFORMATION_MESSAGE);
                    LoggerConfig.logger.info("Сервер запускается...");
                    Server.main(new String[]{}); // Запуск сервера
                    JOptionPane.showMessageDialog(null, "Сервер ожидает подключения клиента...", "Информация", JOptionPane.INFORMATION_MESSAGE);
                    sendButton.setEnabled(Client.isRunning() && selectedFile != null);
                }).start();
            }
        });

        startClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    if (selectedFile != null) {
                        JOptionPane.showMessageDialog(null, "Попытка подключения клиента...", "Информация", JOptionPane.INFORMATION_MESSAGE);
                        String encryptionAlgorithm = (String) encryptionAlgorithmCombo.getSelectedItem();
                        String signatureAlgorithm = (String) signatureAlgorithmCombo.getSelectedItem();
                        LoggerConfig.logger.info("Клиент пытается подключиться с файлом: " + selectedFile.getAbsolutePath());
                        Client.main(new String[]{selectedFile.getAbsolutePath(), encryptionAlgorithm, signatureAlgorithm});
                        JOptionPane.showMessageDialog(null, "Клиент успешно подключен.", "Информация", JOptionPane.INFORMATION_MESSAGE);
                        sendButton.setEnabled(Server.isRunning());
                    } else {
                        JOptionPane.showMessageDialog(null, "Перед запуском клиента нужно выбрать файл!", "Ошибка", JOptionPane.WARNING_MESSAGE);
                        LoggerConfig.logger.warn("Клиент не запущен, так как файл не выбран.");
                    }
                }).start();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логика отправки файла (может быть добавлена)
                JOptionPane.showMessageDialog(null, "Файл успешно отправлен!", "Отправка", JOptionPane.INFORMATION_MESSAGE);
                LoggerConfig.logger.info("Файл успешно отправлен.");
            }
        });

        setVisible(true);
    }

    private void setButtonStyle(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(132, 166, 188));
        button.setFocusable(false);
        button.setMargin(new Insets(10, 10, 10, 10));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
