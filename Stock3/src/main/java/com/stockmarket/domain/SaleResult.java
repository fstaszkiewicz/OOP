package com.stockmarket.domain;

import java.util.ArrayList;
import java.util.List;

public class SaleResult {
    private final String symbol;
    private final AssetType type;
    private final int quantitySold;
    private final double sellUnitPrice;
    private final List<SaleLine> lines;
    private double totalProfit;

    public SaleResult(String symbol, AssetType type, int quantitySold, double sellUnitPrice) {
        this.symbol = symbol;
        this.type = type;
        this.quantitySold = quantitySold;
        this.sellUnitPrice = sellUnitPrice;
        this.lines = new ArrayList<>();
        this.totalProfit = 0.0;
    }

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

    public List<SaleLine> getLines() {
        // prosto: zwracamy kopię aby nie psuć stanu
        return new ArrayList<>(lines);
    }
}