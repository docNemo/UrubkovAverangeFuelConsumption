package ru.mai.calculatorAFC;

import ru.mai.exceptions.NoDataException;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Предназначен для вычисления среднего расчета топлива в день, по данным хранящимся в файле "input.txt".
 * Результат записывается в файл "output.txt"
 *
 * @author Урубков Владислав
 */
public class AverageFuelConsumption {

    /**
     * Хранит номер группы регулярного выражения, в которой содержится значение объема
     */
    private static final byte NUM_GROUP_VOLUME = 1;

    /**
     * Хранит количество часов в сутках
     */
    private static final byte HOURS_PER_DAY = 24;
    /**
     * Хранит код ошибки для организации выхода из программы
     */
    private static final byte ERROR_CODE = -1;
    /**
     * Хранит относительный путь до файла с входными данными
     */
    private static final String INPUT_FILE = "input.txt";
    /**
     * Хранит относительный путь до файла с выходними данными
     */
    private static final String OUTPUT_FILE = "output.txt";
    /**
     * Хранит паттерн для проверки на корректность введенной строки
     */
    private static final Pattern PatCheckCorrectInput = Pattern.compile("Осталось:\\s*([^-][0-9]+\\.?[0-9]+)\\s*л\\.?");

    /**
     * Предназначен для обеспечения логирования исключительных ситуаций,
     * появляющихся в ходе выполнения метода calculationOfAverageFuelConsumption
     * {@link AverageFuelConsumption#calculationOfAverageFuelConsumption()}
     */
    private Logger calculationLogger = Logger.getLogger("calculationLogs");

    public AverageFuelConsumption() {
        try {
            FileHandler calcLogsFH = new FileHandler("calculation.log");
            SimpleFormatter formatter = new SimpleFormatter();
            calcLogsFH.setFormatter(formatter);
            calculationLogger.addHandler(calcLogsFH);

        } catch (SecurityException e) {
            calculationLogger.log(Level.SEVERE,
                    "Не удалось создать файл лога вычислений из-за политики безопасности.",
                    e);
        } catch (IOException e) {
            calculationLogger.log(Level.SEVERE,
                    "Не удалось создать файл лога вычислений из-за ошибки ввода-вывода.",
                    e);
        }
    }

    /**
     * Вычисляет средний расход топлива за день по данным из файла "input.txt" и записывает это значение или текст ошибки в файл "output.txt"
     */
    public void calculationOfAverageFuelConsumption() {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE), StandardCharsets.UTF_8))) {
            try (Scanner in = new Scanner(new InputStreamReader(new FileInputStream(INPUT_FILE), StandardCharsets.UTF_8))) {
                double averageFuelConsumption;

                try {
                    averageFuelConsumption = calculation(in);
                    out.write("Средний расход топлива в день - " + averageFuelConsumption + " л/дн.");
                } catch (NoDataException e) {
                    calculationLogger.log(Level.SEVERE,
                            "Невозможно продолжить выполнение программы - отсутствуют данные");
                    out.write("Вычисление невозможно.\n" +
                            "В файле \"input.txt\" отсутствуют корректные входные данные.");
                    System.exit(ERROR_CODE);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Файл \"input.txt\" не найден.\n" +
                        "Дальнейшее выполнение программы не возможно.");
                calculationLogger.log(Level.SEVERE, "Файл \"input.txt\" не найден.", e);
                out.write("Файл \"input.txt\" не найден.\n" +
                        "Вычисление невозможно.");
                System.exit(ERROR_CODE);
            }
        } catch (UnsupportedCharsetException e) {
            System.out.println("Продолжение выполнения программы невозможно, т.к. отсутствует или поврежден файл для выходных данных \"output.txt\".");
            calculationLogger.log(Level.SEVERE, "Ошибка кодировки файла \"output.txt\".", e);
            System.exit(ERROR_CODE);
        } catch (FileNotFoundException e) {
            System.out.println("Продолжение выполнения программы невозможно, т.к. отсутствует или поврежден файл для выходных данных \"output.txt\".");
            calculationLogger.log(Level.SEVERE, "Файл \"output.txt\" не найден.", e);
            System.exit(ERROR_CODE);
        } catch (IOException e) {
            System.out.println("Продолжение выполнения программы невозможно, т.к. отсутствует или поврежден файл для выходных данных \"output.txt\".");
            calculationLogger.log(Level.SEVERE, "Не удалось открыть файл \"output.txt\" из-за ошибки ввода-вывода", e);
            System.exit(ERROR_CODE);
        }
    }

    /**
     * Вычисляет средний расход топлива за день
     *
     * @param in Поток, из которого осуществляется ввод
     * @return рассчтанное значение среднего расхода топлива в день
     * @throws NoDataException
     */
    private double calculation(Scanner in) throws NoDataException {
        FuelTank firstIndicationVolume = inputVolume(in);

        if (firstIndicationVolume == null) {
            calculationLogger.log(Level.SEVERE,
                    "Невозможно продолжить выполнение программы - отсутствуют данные");
            throw new NoDataException();
        }

        FuelTank lastIndicationVolume = firstIndicationVolume;
        int hours = 0;

        while (in.hasNextLine()) {
            FuelTank currentVolume = inputVolume(in);

            if (currentVolume != null) {
                hours++;

                if (lastIndicationVolume.compareTo(currentVolume) < 0) {
                    firstIndicationVolume.add(currentVolume.difference(lastIndicationVolume));
                }

                lastIndicationVolume = currentVolume;
            }
        }
        return firstIndicationVolume.difference(lastIndicationVolume) / hours * HOURS_PER_DAY;
    }

    /**
     * Считывает одно входное значение и если оно корректно, инициализирует им объект класса FuelTank {@link FuelTank} и возвращает его
     *
     * @param in Поток, из которого осуществляется ввод
     * @return объкт класса FuelTank {@link FuelTank}
     */
    private FuelTank inputVolume(Scanner in) {
        String inputStr;
        Matcher mat;
        FuelTank currentVolume = new FuelTank();

        while (in.hasNextLine()) {
            inputStr = in.nextLine();
            mat = PatCheckCorrectInput.matcher(inputStr);

            if (mat.find()) {
                currentVolume.setResidualVolume(Double.parseDouble(mat.group(NUM_GROUP_VOLUME)));
                return currentVolume;
            } else {
                calculationLogger.log(Level.WARNING,
                        "Пользователем введены некорректные данные: " + inputStr);
            }
        }
        return null;
    }
}
