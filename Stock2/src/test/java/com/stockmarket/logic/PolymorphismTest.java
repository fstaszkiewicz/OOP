package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolymorphismTest {

    private Asset share;
    private Asset commodity;
    private Asset currency;
    private final int quantity = 10;

    @BeforeEach
    void setUp() {
        // Wszystkie mają cenę bazową 100.0
        // Share: Wartość = 100 * 10 = 1000 (Fee jest tylko przy zakupie)
        share = new Share("S1", "Share", 100.0, 5.0);

        // Commodity: Wartość < 1000 (przez storage cost zależny od wolumenu)
        commodity = new Commodity("C1", "Gold", 100.0, 0.01);

        // Currency: Wartość < 1000 (przez spread)
        currency = new Currency("F1", "USD", 100.0, 0.05);
    }

    @Test
    void shareValueShouldNotEqualCommodityValue() {
        assertNotEquals(share.getRealValue(quantity), commodity.getRealValue(quantity));
    }

    @Test
    void shareValueShouldNotEqualCurrencyValue() {
        assertNotEquals(share.getRealValue(quantity), currency.getRealValue(quantity));
    }

    @Test
    void commodityValueShouldNotEqualCurrencyValue() {
        // Dla podanych danych:
        // Commodity: 1000 - (1000 * 0.01 * 10) = 900
        // Currency: 1000 * 0.95 = 950
        assertNotEquals(commodity.getRealValue(quantity), currency.getRealValue(quantity));
    }
}