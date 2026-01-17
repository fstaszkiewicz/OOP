package com.stockmarket.persistence;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;

import java.io.*;
import java.time.LocalDate;

/**
 Moduł persystencji (I/O) portfela.

 Własny format tekstowy (separator '|'), przykładowo:
 HEADER|CASH|10500.50
 ASSET|SHARE|AAPL|Apple|110.0|15|5.0
 LOT|2023-05-10|10|150.00
 LOT|2023-06-12|5|155.00

 Walidacja:
 - suma ilości w LOT musi się zgadzać z QTY zadeklarowanym w ASSET,
 - błędy formatu/parsowania -> DataIntegrityException.
 */
public class PortfolioPersistence {

    public void save(Portfolio portfolio, File file) throws IOException {
        if (portfolio == null) throw new IllegalArgumentException("portfolio cannot be null");
        if (file == null) throw new IllegalArgumentException("file cannot be null");

        // try-with-resources -> bezpieczne zamykanie zasobów
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write(PortfolioFileFormat.HEADER + PortfolioFileFormat.OUT_SEP + PortfolioFileFormat.CASH + PortfolioFileFormat.OUT_SEP + portfolio.getCash());
            w.newLine();

            for (Position pos : portfolio.getPositionsView().values()) {
                Asset a = pos.getAsset();
                int qty = pos.getTotalQuantity();

                // EXTRA: parametr specyficzny dla typu (fee/spread/storageRate)
                String extra = buildExtra(a);

                // ASSET|TYPE|SYMBOL|NAME|MARKET_PRICE|QTY|EXTRA
                w.write(PortfolioFileFormat.ASSET + PortfolioFileFormat.OUT_SEP
                        + a.getType() + PortfolioFileFormat.OUT_SEP
                        + a.getSymbol() + PortfolioFileFormat.OUT_SEP
                        + escape(a.getName()) + PortfolioFileFormat.OUT_SEP
                        + a.getMarketPrice() + PortfolioFileFormat.OUT_SEP
                        + qty + PortfolioFileFormat.OUT_SEP
                        + extra);
                w.newLine();

                // LOT|DATE|QTY|UNIT_PRICE (każda partia zakupowa osobno)
                for (PurchaseLot lot : pos.getLotsSnapshot()) {
                    w.write(PortfolioFileFormat.LOT + PortfolioFileFormat.OUT_SEP
                            + lot.getDate() + PortfolioFileFormat.OUT_SEP
                            + lot.getQuantity() + PortfolioFileFormat.OUT_SEP
                            + lot.getUnitPrice());
                    w.newLine();
                }
            }
        }
    }

    public Portfolio load(File file) throws IOException {
        if (file == null) throw new IllegalArgumentException("file cannot be null");

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line = r.readLine();
            if (line == null) throw new DataIntegrityException("Empty file");

            double cash = parseHeader(line);
            Portfolio portfolio = new Portfolio(0.0);
            portfolio.setCashForPersistence(cash);

            Position currentPosition = null;
            String currentSymbol = null;

            int declaredQty = 0; // ilość zadeklarowana w ASSET
            int lotsQtySum = 0;  // suma ilości z linii LOT

            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(PortfolioFileFormat.SEP, -1);
                String tag = parts[0];

                if (PortfolioFileFormat.ASSET.equals(tag)) {
                    // Zanim zaczniemy nowy ASSET, walidujemy poprzedni
                    if (currentPosition != null) {
                        if (lotsQtySum != declaredQty) {
                            throw new DataIntegrityException("LOT quantity sum mismatch for " + currentSymbol
                                    + ". declared=" + declaredQty + " lotsSum=" + lotsQtySum);
                        }
                    }

                    // ASSET|TYPE|SYMBOL|NAME|MARKET_PRICE|QTY|EXTRA
                    if (parts.length != 7) {
                        throw new DataIntegrityException("Invalid ASSET line (expected 7 fields): " + line);
                    }

                    AssetType type = parseAssetType(parts[1], line);
                    String symbol = parts[2];
                    String name = unescape(parts[3]);
                    double marketPrice = parseDouble(parts[4], "marketPrice", line);
                    declaredQty = parseInt(parts[5], "qty", line);
                    String extra = parts[6];

                    Asset asset = buildAsset(type, symbol, name, marketPrice, extra);

                    currentPosition = new Position(asset);
                    portfolio.putPositionForPersistence(currentPosition);

                    currentSymbol = symbol;
                    lotsQtySum = 0;

                } else if (PortfolioFileFormat.LOT.equals(tag)) {
                    // LOT musi wystąpić po ASSET (inaczej błąd formatu)
                    if (currentPosition == null) throw new DataIntegrityException("LOT without ASSET context: " + line);
                    if (parts.length != 4) throw new DataIntegrityException("Invalid LOT line (expected 4 fields): " + line);

                    LocalDate date = parseDate(parts[1], line);
                    int qty = parseInt(parts[2], "lotQty", line);
                    double unitPrice = parseDouble(parts[3], "unitPrice", line);

                    currentPosition.addLot(date, qty, unitPrice);
                    lotsQtySum += qty;

                } else {
                    throw new DataIntegrityException("Unknown line tag: " + tag);
                }
            }

            // walidacja ostatniego aktywa
            if (currentPosition != null) {
                if (lotsQtySum != declaredQty) {
                    throw new DataIntegrityException("LOT quantity sum mismatch for " + currentSymbol
                            + ". declared=" + declaredQty + " lotsSum=" + lotsQtySum);
                }
            }

            return portfolio;
        }
    }

    // --- Helpers ---

    private double parseHeader(String line) {
        String[] parts = line.split(PortfolioFileFormat.SEP, -1);
        if (parts.length != 3) throw new DataIntegrityException("Invalid HEADER line: " + line);
        if (!PortfolioFileFormat.HEADER.equals(parts[0])) throw new DataIntegrityException("Missing HEADER: " + line);
        if (!PortfolioFileFormat.CASH.equals(parts[1])) throw new DataIntegrityException("Missing CASH header: " + line);
        return parseDouble(parts[2], "cash", line);
    }

    private AssetType parseAssetType(String s, String line) {
        try {
            return AssetType.valueOf(s);
        } catch (Exception e) {
            throw new DataIntegrityException("Invalid AssetType: " + s + " in line: " + line, e);
        }
    }

    private int parseInt(String s, String field, String line) {
        try {
            int v = Integer.parseInt(s);
            if (v < 0) throw new DataIntegrityException(field + " cannot be negative in line: " + line);
            return v;
        } catch (NumberFormatException e) {
            throw new DataIntegrityException("Invalid int for " + field + ": " + s + " in line: " + line, e);
        }
    }

    private double parseDouble(String s, String field, String line) {
        try {
            double v = Double.parseDouble(s);
            if (v < 0) throw new DataIntegrityException(field + " cannot be negative in line: " + line);
            return v;
        } catch (NumberFormatException e) {
            throw new DataIntegrityException("Invalid double for " + field + ": " + s + " in line: " + line, e);
        }
    }

    private LocalDate parseDate(String s, String line) {
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            throw new DataIntegrityException("Invalid date: " + s + " in line: " + line, e);
        }
    }

    private Asset buildAsset(AssetType type, String symbol, String name, double marketPrice, String extra) {
        // EXTRA to 1 liczba w zależności od typu
        double x = parseDouble(extra, "extra", "ASSET extra");
        if (type == AssetType.SHARE) return new Share(symbol, name, marketPrice, x);
        if (type == AssetType.CURRENCY) return new Currency(symbol, name, marketPrice, x);
        if (type == AssetType.COMMODITY) return new Commodity(symbol, name, marketPrice, x);
        throw new DataIntegrityException("Unsupported type: " + type);
    }

    private String buildExtra(Asset a) {
        if (a.getType() == AssetType.SHARE) return String.valueOf(((Share) a).getHandlingFee());
        if (a.getType() == AssetType.CURRENCY) return String.valueOf(((Currency) a).getSpreadRate());
        if (a.getType() == AssetType.COMMODITY) return String.valueOf(((Commodity) a).getStorageCostRate());
        return "0";
    }

    // escape, żeby '|' nie psuł formatu
    private String escape(String s) {
        return s.replace("|", "/");
    }

    private String unescape(String s) {
        return s;
    }
}