package ru.mai.calculatorAFC;

/**
 * Предназначен для хранения объема оставшегося в баке топлива
 *
 * @author Урубков Владислав
 */
public class FuelTank {

    /**
     * Хранит объем топлива, оставшегося в баке
     */
    private double residualVolume;

    /**
     * Добавляет к значению поля residualVolume {@link #residualVolume} значение параметра
     *
     * @param addedVolume значение, на которое надо увеличить поле residualVolume {@link #residualVolume}
     */
    public void add(double addedVolume) {
        residualVolume += addedVolume;
    }

    /**
     * Считает разность поля residualVolume {@link #residualVolume} данного объекта и объекта-параметра
     *
     * @param ft объект разность, с которым надо вычислить
     * @return разность между значеними поля residualVolume {@link #residualVolume} данного объекта и объекта-параметра
     */
    public double difference(FuelTank ft) {
        return residualVolume - ft.residualVolume;
    }

    /**
     * Сравнивает 2 объекта по полю residualVolume {@link #residualVolume}
     *
     * @param ft объект
     * @return отрицательное число, если поле данного объекта меньше поля объекта-параметра;
     * 0 - если равны, положительное число, если большею
     */
    public int compareTo(FuelTank ft) {
        if (residualVolume < ft.getResidualVolume()) {
            return -1;
        } else if (residualVolume == ft.residualVolume) {
            return 0;
        } else {
            return 1;
        }
    }

    public double getResidualVolume() {
        return residualVolume;
    }

    public void setResidualVolume(double residualVolume) {
        this.residualVolume = residualVolume;
    }
}
