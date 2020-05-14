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
        CalculationData indicationForCalculation = input();

        if (indicationForCalculation != null) {
            double averageFuelConsumption = calculation(indicationForCalculation);
            output("Средний расход топлива в день - "+ averageFuelConsumption +" л/дн.");
        } else {
            output("Расчет выполнить не удалось.");
        }
    }

    /**
     * Вычисляет средний расход топлива за день
     *
     * @param indicationsForCalculation объект с первым и последним значениями объема топлива и временем в часах, за которое были сделаны измерения
     * @return рассчтанное значение среднего расхода топлива в день
     */
    private double calculation(CalculationData indicationsForCalculation) {
        FuelTank firstIndicationVolume = indicationsForCalculation.getFirstIndication();
        FuelTank lastIndicationVolume = indicationsForCalculation.getLastIndication();
        int hours = indicationsForCalculation.getHours();

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

    /**
     * Осуществляет чтение входных данных, и возвращает первое, последнее показания и время в часах
     *
     * @param in поток, из которого осуществляется чтение
     * @return объект с первым и последним значениями объема топлива и временем в часах, за которое были сделаны измерения
     * @throws NoDataException если в потоке нет данных или они все не корректны, "пробрасывается" исключение
     */
    private CalculationData inputFirstAndLAstIndication(Scanner in) throws NoDataException {
        FuelTank firstIndicationVolume = inputVolume(in);

        if (firstIndicationVolume == null) {
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
        return new CalculationData(firstIndicationVolume, lastIndicationVolume, hours);
    }

    /**
     * Осуществляет чтение входных данных, и возвращает первое, последнее показания и время в часах
     *
     * @return объект с первым и последним значениями объема топлива и временем в часах, за которое были сделаны измерения
     */
    private CalculationData input() {
        CalculationData indicationForCalculation = null;
        try (Scanner in = new Scanner(new InputStreamReader(new FileInputStream(INPUT_FILE), StandardCharsets.UTF_8))) {
            try {
                indicationForCalculation = inputFirstAndLAstIndication(in);
            } catch (NoDataException e) {
                System.out.println("Невозможно продолжить выполнение программы - отсутствуют данные");
                calculationLogger.log(Level.SEVERE,
                        "Невозможно продолжить выполнение программы - отсутствуют данные");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл \"input.txt\" не найден.\n" +
                    "Дальнейшее выполнение программы не возможно.");
            calculationLogger.log(Level.SEVERE, "Файл \"input.txt\" не найден.", e);
        }
        return indicationForCalculation;
    }

    /**
     * Записывает в файл рассчитанное значение среднего суточного расхода с пояснением или текст ошибки о не удачном расчете
     *
     * @param outputText результирующая строка для вывода
     */
    private void output(String outputText) {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE), StandardCharsets.UTF_8))) {
            out.write(outputText);
        } catch (UnsupportedCharsetException e) {
            System.out.println("Продолжение выполнения программы невозможно, т.к. отсутствует или поврежден файл для выходных данных \"output.txt\".");
            calculationLogger.log(Level.SEVERE, "Ошибка кодировки файла \"output.txt\".", e);
        } catch (FileNotFoundException e) {
            System.out.println("Продолжение выполнения программы невозможно, т.к. отсутствует или поврежден файл для выходных данных \"output.txt\".");
            calculationLogger.log(Level.SEVERE, "Файл \"output.txt\" не найден.", e);
        } catch (IOException e) {
            System.out.println("Продолжение выполнения программы невозможно, т.к. отсутствует или поврежден файл для выходных данных \"output.txt\".");
            calculationLogger.log(Level.SEVERE, "Не удалось открыть файл \"output.txt\" из-за ошибки ввода-вывода", e);
        }
    }
}
