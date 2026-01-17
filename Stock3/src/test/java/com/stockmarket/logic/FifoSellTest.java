package com.stockmarket.logic;

import com.stockmarket.domain.SaleResult;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FifoSellTest {

    private Portfolio p;

    @BeforeEach
    void setUp() {
        p = new Portfolio(1_000_000.0);

        Share xyz = new Share("XYZ", "XYZ", 150.0, 0.0);
        p.acquire(xyz, 10, LocalDate.parse("2023-01-01"), 100.0);
        p.acquire(xyz, 10, LocalDate.parse("2023-02-01"), 120.0);
    }

    private SaleResult sell15At150() {
        return p.sell("XYZ", 15, 150.0);
    }

    @Test
    void sellFifo_shouldReturnSoldQuantity() {
        SaleResult res = sell15At150();
        assertEquals(15, res.getQuantitySold());
    }

    @Test
    void sellFifo_shouldCalculateTotalProfit() {
        SaleResult res = sell15At150();
        assertEquals(650.0, res.getTotalProfit(), 1e-9);
    }

    @Test
    void sellFifo_shouldCreateTwoSaleLines() {
        SaleResult res = sell15At150();
        assertEquals(2, res.getLines().size());
    }

    @Test
    void sellFifo_firstLineShouldConsumeTenFromFirstLot() {
        SaleResult res = sell15At150();
        assertEquals(10, res.getLines().get(0).getQuantitySoldFromLot());
    }

    @Test
    void sellFifo_secondLineShouldConsumeFiveFromSecondLot() {
        SaleResult res = sell15At150();
        assertEquals(5, res.getLines().get(1).getQuantitySoldFromLot());
    }
}