package com.stockmarket.persistence;

import com.stockmarket.domain.DataIntegrityException;
import com.stockmarket.domain.Share;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioPersistenceTest {

    private Portfolio preparePortfolio() {
        Portfolio p = new Portfolio(2000.0);
        Share aapl = new Share("AAPL", "Apple", 110.0, 5.0);
        p.acquire(aapl, 10, LocalDate.parse("2023-05-10"), 150.0);
        p.acquire(aapl, 5, LocalDate.parse("2023-06-12"), 155.0);
        return p;
    }

    private Portfolio roundTrip(Portfolio p) throws Exception {
        File tmp = File.createTempFile("portfolio", ".txt");
        tmp.deleteOnExit();

        PortfolioPersistence io = new PortfolioPersistence();
        io.save(p, tmp);

        return io.load(tmp);
    }

    @Test
    void saveAndLoad_shouldPreserveCash() throws Exception {
        Portfolio p = preparePortfolio();
        Portfolio loaded = roundTrip(p);
        assertEquals(p.getCash(), loaded.getCash(), 1e-9);
    }

    @Test
    void saveAndLoad_shouldPreservePositionsCount() throws Exception {
        Portfolio p = preparePortfolio();
        Portfolio loaded = roundTrip(p);
        assertEquals(1, loaded.getPositionsCount());
    }

    @Test
    void saveAndLoad_shouldContainAaplPosition() throws Exception {
        Portfolio p = preparePortfolio();
        Portfolio loaded = roundTrip(p);
        assertTrue(loaded.getPositionsView().containsKey("AAPL"));
    }

    @Test
    void saveAndLoad_shouldPreserveAaplTotalQuantity() throws Exception {
        Portfolio p = preparePortfolio();
        Portfolio loaded = roundTrip(p);
        assertEquals(15, loaded.getPositionsView().get("AAPL").getTotalQuantity());
    }

    @Test
    void load_shouldThrowWhenLotSumMismatch() throws Exception {
        File tmp = File.createTempFile("badportfolio", ".txt");
        tmp.deleteOnExit();

        try (FileWriter w = new FileWriter(tmp)) {
            w.write("HEADER|CASH|1000.0\n");
            w.write("ASSET|SHARE|AAPL|Apple|110.0|15|5.0\n");
            w.write("LOT|2023-05-10|10|150.0\n"); // suma lotów 10 != deklarowane 15
        }

        PortfolioPersistence io = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> io.load(tmp));
    }
}