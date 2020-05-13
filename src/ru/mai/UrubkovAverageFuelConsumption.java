package ru.mai;

import ru.mai.calculatorAFC.AverageFuelConsumption;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Используется для управления всей программой
 *
 * @author Урубков Владислав
 */
public class UrubkovAverageFuelConsumption {
    /**
     * Предназначен для обеспечения логирования исключительных ситуаций,
     * появляющихся в ходе выполнения всей программы и, в случае корректного выполнения, времения выполнения
     */
    private static Logger systemLogger = Logger.getLogger("system");
    /**
     * Хранит код ошибки для организации выхода из программы
     */
    private static final byte ERROR_CODE = -1;

    public static void main(String[] args) {
        try {
            long startTime = System.currentTimeMillis();

            initializeLogger();

            AverageFuelConsumption calculator = new AverageFuelConsumption();
            calculator.calculationOfAverageFuelConsumption();

            long execTime = System.currentTimeMillis() - startTime;
            systemLogger.info("Время выполнения " + execTime + "нс.");
        } catch (Exception e) {
            System.out.println("Непредвиденная ошибка. Программа завершает свою работу.");
            systemLogger.log(Level.SEVERE, "Непредвиденная ошибка", e);
            System.exit(ERROR_CODE);
        }

    }

    /**
     * Инициализирует основной логгер
     */
    private static void initializeLogger() {
        try {
            FileHandler calcLogsFH = new FileHandler("system.log");
            SimpleFormatter formatter = new SimpleFormatter();
            calcLogsFH.setFormatter(formatter);
            systemLogger.addHandler(calcLogsFH);

        } catch (SecurityException e) {
            systemLogger.log(Level.SEVERE,
                    "Не удалось создать файл системного лога из-за политики безопасности.",
                    e);
        } catch (IOException e) {
            systemLogger.log(Level.SEVERE,
                    "Не удалось создать файл системного лога из-за ошибки ввода-вывода.",
                    e);
        }
    }
}
