package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    // Tworzymy anonimową klasę, aby przetestować abstrakcyjną klasę Asset
    private Asset createAsset(String symbol, String name, double price) {
        return new Asset(symbol, name, price) {
            @Override public double getRealValue(int q) { return 0; }
            @Override public double getAcquisitionCost(int q) { return 0; }
        };
    }

    // --- Gettery ---

    @Test
    void getSymbolShouldReturnSymbol() {
        Asset a = createAsset("SYM", "Name", 10.0);
        assertEquals("SYM", a.getSymbol());
    }

    @Test
    void getNameShouldReturnName() {
        Asset a = createAsset("SYM", "Name", 10.0);
        assertEquals("Name", a.getName());
    }

    @Test
    void getMarketPriceShouldReturnPrice() {
        Asset a = createAsset("SYM", "Name", 10.0);
        assertEquals(10.0, a.getMarketPrice());
    }

    // --- Walidacja (Konstruktor) ---

    @Test
    void constructorShouldThrowForNullSymbol() {
        assertThrows(IllegalArgumentException.class, () -> createAsset(null, "N", 10.0));
    }

    @Test
    void constructorShouldThrowForEmptySymbol() {
        assertThrows(IllegalArgumentException.class, () -> createAsset("", "N", 10.0));
    }

    @Test
    void constructorShouldThrowForNullName() {
        assertThrows(IllegalArgumentException.class, () -> createAsset("S", null, 10.0));
    }

    @Test
    void constructorShouldThrowForNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> createAsset("S", "N", -1.0));
    }

    @Test // test graniczny dla ceny zero (dozwolone bankructwo)
    void constructorShouldAllowZeroPrice() {
        assertDoesNotThrow(() -> createAsset("S", "N", 0.0));
    }

    // --- equals() i hashCode() odtworzone z pierwszej części ---

    @Test
    void equalsShouldReturnTrueForSameSymbol() {
        Asset a1 = createAsset("ABC", "Name1", 10.0);
        Asset a2 = createAsset("ABC", "Name2", 20.0); // Inne pola, ten sam symbol
        assertEquals(a1, a2);
    }

    @Test
    void equalsShouldReturnFalseForDifferentSymbol() {
        Asset a1 = createAsset("ABC", "Name", 10.0);
        Asset a2 = createAsset("XYZ", "Name", 10.0);
        assertNotEquals(a1, a2);
    }

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        Asset a1 = createAsset("ABC", "Name", 10.0);
        assertEquals(a1, a1);
    }

    @Test
    void hashCodeShouldBeEqualForSameSymbol() {
        Asset a1 = createAsset("ABC", "Name1", 10.0);
        Asset a2 = createAsset("ABC", "Name2", 20.0);
        assertEquals(a1.hashCode(), a2.hashCode());
    }



}