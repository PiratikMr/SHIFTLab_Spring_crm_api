package com.shiftlab.crm.model;

public enum PeriodType {
    DAY, MONTH, QUARTER, YEAR;

    public static PeriodType of(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип периода: " + value + ". Допустимые значения: DAY, MONTH, QUARTER, YEAR");
        }
    }
}
