package com.stockmarket.domain;

/**
 Próba sprzedaży aktywa, którego nie posiadamy lub posiadamy za mało.
 */
public class InsufficientHoldingsException extends RuntimeException {
    public InsufficientHoldingsException(String message) {
        super(message);
    }
}