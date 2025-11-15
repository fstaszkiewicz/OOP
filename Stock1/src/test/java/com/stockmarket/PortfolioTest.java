package com.stockmarket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    // Sprawdza, czy nowo utworzony, pusty portfel ma poprawny stan początkowy
    @Test
    void emptyPortfolioHasInitialCashAndZeroHoldings() {
        Portfolio p = new Portfolio(1000.0);
        assertEquals(1000.0, p.getCash());
        assertEquals(0, p.getHoldingsCount());
        assertEquals(0.0, p.calculateStockValue());
        assertEquals(1000.0, p.calculateTotalValue());
    }

    // Testuje dodanie pierwszej, nowej akcji do portfela i weryfikuje obliczenia
    @Test
    void addingNewStockCreatesHolding() {
        Portfolio p = new Portfolio(500.0);
        Stock s = new Stock("AAA", "A Corp", 2.0);
        p.addStock(s, 10);
        assertEquals(1, p.getHoldingsCount()); // Powinna być 1 pozycja
        assertEquals(10, p.getStockQuantity(s)); // Ilość tej akcji to 10
        assertEquals(20.0, p.calculateStockValue()); // Wartość akcji (10 * 2.0)
        assertEquals(520.0, p.calculateTotalValue()); // Całkowita wartość (gotówka + akcje)
    }

    // Testuje, czy dodanie tej samej akcji kolejny raz tylko zwiększa jej ilość, a nie tworzy nowej pozycji
    @Test
    void addingSameStockIncreasesQuantity() {
        Portfolio p = new Portfolio(0.0);
        Stock s = new Stock("XYZ", "X Y Z", 5.0);
        p.addStock(s, 3);
        p.addStock(s, 7); // Dodanie tej samej akcji
        assertEquals(1, p.getHoldingsCount()); // Nadal powinna być tylko 1 pozycja
        assertEquals(10, p.getStockQuantity(s)); // Ilość powinna się zsumować (3 + 7)
        assertEquals(50.0, p.calculateStockValue());
    }

    // Testuje, czy dodanie różnych akcji tworzy osobne pozycje w portfelu
    @Test
    void addingDifferentStocksCreatesSeparateHoldings() {
        Portfolio p = new Portfolio(100.0);
        Stock a = new Stock("A", "AB", 1.0);
        Stock b = new Stock("B", "BA", 2.0);
        p.addStock(a, 5);
        p.addStock(b, 3);
        assertEquals(2, p.getHoldingsCount()); // Powinny być 2 pozycje
        assertEquals(5, p.getStockQuantity(a)); // Ilość A
        assertEquals(3, p.getStockQuantity(b)); // Ilość B
        assertEquals(5*1.0 + 3*2.0, p.calculateStockValue()); // Łączna wartość akcji
    }

    // Sprawdza, czy portfel poprawnie zwraca 0 dla akcji, której nie posiada
    @Test
    void getStockQuantityReturnsZeroForMissingStock() {
        Portfolio p = new Portfolio(0.0);
        Stock s = new Stock("NONE", "None", 1.0);
        assertEquals(0, p.getStockQuantity(s));
    }

    // Testuje sytuację brzegową: próbę dodania 11-tej unikalnej akcji do portfela o rozmiarze 10
    @Test
    void cannotAddStockWhenArrayIsFull() {
        Portfolio p = new Portfolio(0.0);
        // Wypełniamy całą tablicę (rozmiar 10)
        for (int i = 0; i < 10; i++) {
            p.addStock(new Stock("S" + i, "Stock " + i, 1.0 + i), 1);
        }
        assertEquals(10, p.getHoldingsCount());

        // Próba dodania 11-tej akcji
        Stock extra = new Stock("S10", "Stock 10", 11.0);
        // Oczekujemy, że poleci wyjątek 'IllegalStateException'
        assertThrows(IllegalStateException.class, () -> p.addStock(extra, 1));
    }

    // Sprawdza, czy metoda addStock() odrzuca nieprawidłowe argumenty (null, 0, ujemna ilość).
    @Test
    void addStockRejectsInvalidArguments() {
        Portfolio p = new Portfolio(0.0);
        Stock s = new Stock("T", "T Co", 3.0);
        assertThrows(IllegalArgumentException.class, () -> p.addStock(null, 1)); // Akcja nie może być null
        assertThrows(IllegalArgumentException.class, () -> p.addStock(s, 0)); // Ilość musi być dodatnia
        assertThrows(IllegalArgumentException.class, () -> p.addStock(s, -5)); // Ilość nie może być ujemna
    }
}