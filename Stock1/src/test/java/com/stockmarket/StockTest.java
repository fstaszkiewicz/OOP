package com.stockmarket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    // Testuje, czy konstruktor i gettery działają poprawnie
    @Test
    void createsStockAndGettersReturnValues() {
        Stock s = new Stock("CDR", "CD Projekt", 120.5);
        assertEquals("CDR", s.getSymbol());
        assertEquals("CD Projekt", s.getName());
        assertEquals(120.5, s.getInitialPrice());
    }

    // Testuje, czy metoda equals() i hashCode() uznają akcje o tym samym symbolu za równe
    @Test
    void equalsReturnsTrueForSameSymbol() {
        Stock a = new Stock("ABC", "Company A", 10.0);
        Stock b = new Stock("ABC", "Another Name", 20.0);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    // Sprawdza, czy akcje o różnych symbolach nie są uznawane za równe
    @Test
    void equalsReturnsFalseForDifferentSymbol() {
        Stock a = new Stock("AAA", "Company A", 10.0);
        Stock b = new Stock("BBB", "Company B", 10.0);
        assertNotEquals(a, b);
    }

    // Testuje przypadki brzegowe metody equals(): porównanie z samym sobą, null i innym typem obiektu
    @Test
    void equalsHandlesSelfAndNull() {
        Stock s = new Stock("X", "Name", 1.0);
        assertEquals(s, s); // Porównanie z samym sobą
        assertNotEquals(s, null); // Porównanie z null
        assertNotEquals(s, "not a stock"); // Porównanie z innym typem
    }

    // Sprawdza, czy konstruktor poprawnie rzuca wyjątek przy próbie podania 'null' jako symbol lub nazwa
    @Test
    void constructorRejectsNullSymbolOrName() {
        assertThrows(IllegalArgumentException.class, () -> new Stock(null, "Name", 1.0));
        assertThrows(IllegalArgumentException.class, () -> new Stock("SYM", null, 1.0));
    }
}