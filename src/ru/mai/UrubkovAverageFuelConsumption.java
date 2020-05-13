package ru.mai;

import ru.mai.calculatorAFC.AverageFuelConsumption;
import ru.mai.exceptions.NoDataException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Scanner;
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
     * появляющихся в ходе выполнения всей программы
     */
    private static Logger systemLogger = Logger.getLogger("system");

    private static Logger timeLogger = Logger.getLogger("execution time");
    /**
     * Хранит код ошибки для организации выхода из программы
     */
    private static byte ERROR_CODE = -1;

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        initializeLogger();

        try (Scanner in = new Scanner(new File("input.txt"))) {
            AverageFuelConsumption calculator = new AverageFuelConsumption();

            double averageFuelConsumption;
            try {
                averageFuelConsumption = calculator.calculationOfAverageFuelConsumption(in);
                try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), StandardCharsets.UTF_8))) {
                    out.write("Средний расход топлива в день - " + averageFuelConsumption + " л/дн.");
                    System.out.println("Средний расход топлива в день - " + averageFuelConsumption + " л/дн.");
                } catch(UnsupportedCharsetException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Средний расход топлива в день - " + averageFuelConsumption + " л/дн.");
            } catch (NoDataException e) {
                systemLogger.log(Level.SEVERE,
                        "Невозможно продолжить выполнение программы - отсутствуют данные");
                System.out.println("Error!");
                System.exit(ERROR_CODE);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл \"input.txt\" не найден.\n" +
                    "Дальнейшее выполнение программы не возможно.");
            systemLogger.log(Level.SEVERE, "Файл \"input.txt\" не найден.", e);
            System.exit(ERROR_CODE);
        } catch (Exception e) {
            systemLogger.log(Level.SEVERE,
                    "Непредвиденная ошибка",
                    e);
        }
        
        Long rt = System.currentTimeMillis() - t1;
        timeLogger.info(rt.toString());
    }

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
