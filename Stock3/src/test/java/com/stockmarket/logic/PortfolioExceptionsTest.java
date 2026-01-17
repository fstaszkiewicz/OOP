package com.stockmarket.logic;

import com.stockmarket.domain.InsufficientFundsException;
import com.stockmarket.domain.InsufficientHoldingsException;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioExceptionsTest {

    @Test
    void acquireShouldThrowInsufficientFunds() {
        Portfolio p = new Portfolio(50.0);
        Share s = new Share("S", "S", 100.0, 0.0);

        assertThrows(InsufficientFundsException.class, () -> p.acquire(s, 1, LocalDate.now(), 100.0));
    }

    @Test
    void sellShouldThrowWhenNoPosition() {
        Portfolio p = new Portfolio(0.0);
        assertThrows(InsufficientHoldingsException.class, () -> p.sell("MISSING", 1, 10.0));
    }
}