package com.stockmarket.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommodityTest {

    @Test
    void getRealValueShouldDecreaseByStorageCost() {
        // Cena 100 i koszt bazowy 1% na jednostkę wolumenu
        Commodity c = new Commodity("G", "G", 100.0, 0.01);

        // raw=200
        // effectiveRate=0.01*2=0.02 (poniżej cap 0.20)
        // storage=200*0.02=4
        // real=196
        assertEquals(196.0, c.getRealValue(2));
    }

    @Test
    void getAcquisitionCostShouldBeMarketPriceTimesQuantity() {
        Commodity c = new Commodity("G", "G", 100.0, 0.01);
        assertEquals(200.0, c.getAcquisitionCost(2));
    }

    @Test
    void constructorShouldThrowForNegativeStorageRate() {
        assertThrows(IllegalArgumentException.class, () -> new Commodity("S", "N", 10.0, -0.01));
    }

    @Test
    void higherQuantityShouldCauseHigherPercentageLossBeforeCap() {
        Commodity oil = new Commodity("OIL", "Oil", 100.0, 0.01);

        // quantity=1 => effectiveRate=1% => real=99
        assertEquals(99.0, oil.getRealValue(1));

        // quantity=10 => effectiveRate=10% => raw=1000, real=900
        assertEquals(900.0, oil.getRealValue(10));
    }

    @Test
    void percentageLossShouldBeCappedAtTwentyPercent() {
        Commodity oil = new Commodity("OIL", "Oil", 100.0, 0.01);

        // quantity=100 => effectiveRate=100% ale cap=20%
        // raw=100*100=10000 => real=8000
        assertEquals(8000.0, oil.getRealValue(100));
    }

    @Test
    void effectiveRateShouldBeCappedAtTwentyPercent() {
        Commodity c = new Commodity("OIL", "Oil", 100.0, 0.01);

        // quantity=100 => effectiveRate=0.01*100=1.0, ale cap=0.20
        // raw=100*100=10000
        // real=10000*(1-0.20)=8000
        assertEquals(8000.0, c.getRealValue(100));
    }
}