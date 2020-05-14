package ru.mai.calculatorAFC;

/**
 * Предназначен для хранения и передачи необходимых данных для расчета среднего суточного расхода
 *
 * @author Урубков Владислав
 */
public class CalculationData {
    /**
     * Хранит показание объема топлива в начальный момент времени
     */
    FuelTank firstIndication;
    /**
     * Хранит показание объема топлива в последний момент времени
     */
    FuelTank lastIndication;
    /**
     * Хранит время в часах, затраченное на все измерения
     */
    int hours;

    public CalculationData(FuelTank firstIndication, FuelTank secondIndication, int hours) {
        this.firstIndication = firstIndication;
        this.lastIndication = secondIndication;
        this.hours = hours;
    }

    public FuelTank getFirstIndication() {
        return firstIndication;
    }

    public FuelTank getLastIndication() {
        return lastIndication;
    }

    public int getHours() {
        return hours;
    }
}
