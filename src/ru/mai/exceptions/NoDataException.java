package ru.mai.exceptions;

/**
 * Предназначен для генерации исключения в случае отсутствия данных в начале расчета
 *
 * @author Урубков Владислав
 */
public class NoDataException extends Throwable {
    public NoDataException() {
        super("Отсутствуют данные для дальнейшего расчета.");
    }

}
