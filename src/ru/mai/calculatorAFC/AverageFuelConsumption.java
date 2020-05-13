package ru.mai.calculatorAFC;

import ru.mai.exceptions.NoDataException;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Предназначен для вычисления среднего расчета топлива в день
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
     * Хранит паттерн для проверки на корректность введенной строки
     */
    private static final Pattern PatCheckCorrectInput = Pattern.compile("Осталось:\\s*([^-][0-9]+\\.?[0-9]+)\\s*л\\.?");

    /**
     * Предназначен для обеспечения логирования исключительных ситуаций,
     * появляющихся в ходе выполнения метода calculationOfAverageFuelConsumption
     * {@link AverageFuelConsumption#calculationOfAverageFuelConsumption(Scanner)}
     */
    private Logger calculationLogger = Logger.getLogger("calculationLogs");

    /**
     * Если true, файл лога вычислений не удалось создать из-за политики безопасности
     */
    private boolean isSecurityException = false;

    /**
     * Если true, файл лога вычислений не удалось создать из-за ошибки ввода-вывода
     */
    private boolean isIOException = false;

    public AverageFuelConsumption() {
        try {
            FileHandler calcLogsFH = new FileHandler("calculation.log");
            SimpleFormatter formatter = new SimpleFormatter();
            calcLogsFH.setFormatter(formatter);
            calculationLogger.addHandler(calcLogsFH);

        } catch (SecurityException e) {
            isSecurityException = true;
            calculationLogger.log(Level.SEVERE,
                    "Не удалось создать файл лога вычислений из-за политики безопасности.",
                    e);
        } catch (IOException e) {
            isIOException = true;
            calculationLogger.log(Level.SEVERE,
                    "Не удалось создать файл лога вычислений из-за ошибки ввода-вывода.",
                    e);
        }
    }

    /**
     * Вычисляет средний расход топлива за день
     *
     * @param in Поток, из которого осуществляется ввод
     * @return рассчтанное значение среднего расхода топлива в день
     * @throws NoDataException
     */
    public double calculationOfAverageFuelConsumption(Scanner in) throws NoDataException {
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

    public boolean isSecurityException() {
        return isSecurityException;
    }

    public boolean isIOException() {
        return isIOException;
    }
}
