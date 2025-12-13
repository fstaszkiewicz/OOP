package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    // --- Konstruktor ---

    @Test
    void constructorShouldSetInitialCash() {
        Portfolio p = new Portfolio(1000.0);
        assertEquals(1000.0, p.getCash());
    }

    @Test
    void constructorShouldInitializeEmptyHoldings() {
        Portfolio p = new Portfolio(1000.0);
        assertEquals(0, p.getHoldingsCount());
    }

    @Test
    void constructorShouldThrowForNegativeCash() {
        assertThrows(IllegalArgumentException.class, () -> new Portfolio(-100));
    }

    // --- acquire() - sukces ---

    @Test
    void acquireShouldDecreaseCashByCost() {
        Portfolio p = new Portfolio(1000.0);
        // Share z fee 5.0, cena 100. Koszt 1 szt = 105.
        Share s = new Share("S", "S", 100.0, 5.0);

        p.acquire(s, 1);
        assertEquals(895.0, p.getCash());
    }

    @Test
    void acquireShouldIncreaseHoldingsCountForNewAsset() {
        Portfolio p = new Portfolio(1000.0);
        Share s = new Share("S", "S", 100.0, 5.0);

        p.acquire(s, 1);
        assertEquals(1, p.getHoldingsCount());
    }

    @Test
    void acquireShouldSetCorrectQuantityForNewAsset() {
        Portfolio p = new Portfolio(1000.0);
        Share s = new Share("S", "S", 100.0, 5.0);

        p.acquire(s, 5);
        assertEquals(5, p.getAssetQuantity(s));
    }

    // --- acquire() - agregacja ---

    @Test
    void acquireSameAssetShouldNotIncreaseHoldingsCount() {
        Portfolio p = new Portfolio(1000.0);
        Share s = new Share("S", "S", 10.0, 0.0);

        p.acquire(s, 5);
        p.acquire(s, 5);

        assertEquals(1, p.getHoldingsCount());
    }

    @Test
    void acquireSameAssetShouldSumQuantity() {
        Portfolio p = new Portfolio(1000.0);
        Share s = new Share("S", "S", 10.0, 0.0);

        p.acquire(s, 5);
        p.acquire(s, 5);

        assertEquals(10, p.getAssetQuantity(s));
    }

    // --- acquire() - błędy ---

    @Test
    void acquireShouldThrowForNullAsset() {
        Portfolio p = new Portfolio(100);
        assertThrows(IllegalArgumentException.class, () -> p.acquire(null, 1));
    }

    @Test
    void acquireShouldThrowForZeroQuantity() {
        Portfolio p = new Portfolio(100);
        Share s = new Share("S", "S", 10.0, 0.0);
        assertThrows(IllegalArgumentException.class, () -> p.acquire(s, 0));
    }

    @Test
    void acquireShouldThrowForNegativeQuantity() {
        Portfolio p = new Portfolio(100);
        Share s = new Share("S", "S", 10.0, 0.0);
        assertThrows(IllegalArgumentException.class, () -> p.acquire(s, -5));
    }

    @Test // Test logiczny: brak gotówki
    void acquireShouldThrowWhenNotEnoughCash() {
        Portfolio p = new Portfolio(50.0); // Za mało
        Share s = new Share("S", "S", 100.0, 0.0);

        assertThrows(IllegalStateException.class, () -> p.acquire(s, 1));
    }

    @Test // Test logiczny: ukryte koszty powodują brak gotówki
    void acquireShouldThrowWhenFeeMakesItTooExpensive() {
        Portfolio p = new Portfolio(100.0);
        // Cena 100 (stać nas), ale fee 5 (już nas nie stać)
        Share s = new Share("S", "S", 100.0, 5.0);

        assertThrows(IllegalStateException.class, () -> p.acquire(s, 1));
    }

    // --- calculateTotalValue() ---

    @Test
    void calculateTotalValueShouldReturnCashIfPortfolioEmpty() {
        Portfolio p = new Portfolio(500.0);
        assertEquals(500.0, p.calculateTotalValue());
    }

    @Test // Test integracyjny sumowania wartości
    void calculateTotalValueShouldSumValuesPolymorphically() {
        Portfolio p = new Portfolio(10000.0);
        Share share = new Share("S1", "Share", 100.0, 0.0); // Wartość 100
        Currency curr = new Currency("C1", "Curr", 100.0, 0.1); // Wartość 90 (spread)

        p.acquire(share, 1);
        p.acquire(curr, 1);

        // Cash: 10000 - 100 - 100 = 9800
        // Assets: 100 + 90 = 190
        // Total: 9990
        assertEquals(9990.0, p.calculateTotalValue());
    }

    // --- Helper ---

    @Test
    void getAssetQuantityShouldReturnZeroForNonExistentAsset() {
        Portfolio p = new Portfolio(100.0);
        Share s = new Share("S", "S", 10.0, 0.0);
        assertEquals(0, p.getAssetQuantity(s));
    }
}