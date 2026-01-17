package com.stockmarket.domain;

/**
 Wyjątek: brak środków na zakup aktywa.
 nie dopuszczamy do ujemnej gotówki.)
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}