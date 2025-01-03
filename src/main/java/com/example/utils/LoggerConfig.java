package com.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Конфигурация логгера для приложения.
 *
 * Этот класс инициализирует логгер, используемый в приложении для журналирования событий.
 */
public class LoggerConfig {

    /**
     * Логгер для записи журналов приложения.
     * Использует Log4j для ведения логов.
     */
    public static final Logger logger = LogManager.getLogger(LoggerConfig.class);
}
