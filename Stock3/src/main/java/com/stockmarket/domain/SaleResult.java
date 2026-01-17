package com.stockmarket.domain;

import java.util.ArrayList;
import java.util.List;

/**
 Raport z transakcji sprzedaży.
 Zawiera:
 - symbol i typ aktywa
 - wolumen sprzedany i cenę sprzedaży
 - listę linii (SaleLine) odpowiadających partiom zużytym FIFO
 - sumaryczny zysk/stratę (totalProfit)
 */

public class SaleResult {
    private final String symbol;         // ticker aktywa
    private final AssetType type;        // typ aktywa
    private final int quantitySold;      // sprzedany wolumen
    private final double sellUnitPrice;  // cena sprzedaży za sztukę

    private final List<SaleLine> lines;  // szczegóły FIFO (z jakich partii sprzedano)
    private double totalProfit;          // suma profit ze wszystkich linii

    public SaleResult(String symbol, AssetType type, int quantitySold, double sellUnitPrice) {
        this.symbol = symbol;
        this.type = type;
        this.quantitySold = quantitySold;
        this.sellUnitPrice = sellUnitPrice;
        this.lines = new ArrayList<>();
        this.totalProfit = 0.0;
    }

    // Dodaje jedną linię raportu i aktualizuje sumaryczny wynik
    public void addLine(SaleLine line) {
        if (line == null) throw new IllegalArgumentException("line cannot be null");
        lines.add(line);
        totalProfit += line.getProfit();
    }

    public String getSymbol() {
        return symbol;
    }

    public AssetType getType() {
        return type;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getSellUnitPrice() {
        return sellUnitPrice;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    // Zwracamy kopię listy, żeby nie dało się modyfikować wnętrza SaleResult z zewnątrz
    public List<SaleLine> getLines() {
        return new ArrayList<>(lines);
    }
}