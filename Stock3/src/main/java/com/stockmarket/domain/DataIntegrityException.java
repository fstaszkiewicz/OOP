package com.stockmarket.domain;

/**
 Wyjątek biznesowy dla persystencji.
 Rzucany, gdy plik portfela jest uszkodzony lub niespójny (np. zła liczba pól, zły format, mismatch sum LOT).
 */
public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}