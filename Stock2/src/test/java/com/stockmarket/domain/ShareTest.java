package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShareTest {

    @Test
    void getRealValueShouldBeMarketPriceTimesQuantity() {
        // Wartość rzeczywista akcji to czysta wycena rynkowa
        Share s = new Share("S", "S", 100.0, 5.0);
        assertEquals(500.0, s.getRealValue(5));
    }

    @Test
    void getAcquisitionCostShouldIncludeFee() {
        // Koszt zakupu = (cena * ilosc) + oplata
        Share s = new Share("S", "S", 100.0, 5.0);
        // 100 * 5 + 5 = 505
        assertEquals(505.0, s.getAcquisitionCost(5));
    }

    @Test
    void constructorShouldThrowForNegativeFee() {
        assertThrows(IllegalArgumentException.class, () -> new Share("S", "N", 10.0, -1.0));
    }
}