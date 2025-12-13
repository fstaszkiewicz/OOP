package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void getRealValueShouldBeLoweredBySpread() {
        // Cena 100, Spread 10% (0.1)
        Currency c = new Currency("U", "U", 100.0, 0.1);

        // Bid Price = 100 * 0.9 = 90
        // Total = 90 * 2 = 180
        assertEquals(180.0, c.getRealValue(2));
    }

    @Test
    void getAcquisitionCostShouldBeMarketPriceTimesQuantity() {
        Currency c = new Currency("U", "U", 100.0, 0.1);
        assertEquals(200.0, c.getAcquisitionCost(2));
    }

    @Test
    void constructorShouldThrowForNegativeSpread() {
        assertThrows(IllegalArgumentException.class, () -> new Currency("S", "N", 10.0, -0.1));
    }

    @Test
    void constructorShouldThrowForSpreadOverOne() {
        assertThrows(IllegalArgumentException.class, () -> new Currency("S", "N", 10.0, 1.1));
    }
}