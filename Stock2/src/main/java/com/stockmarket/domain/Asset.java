package com.stockmarket.domain;

import java.util.Objects;

/**
 Abstrakcyjna klasa bazowa
 */

public abstract class Asset {
    private final String symbol;
    private final String name;
    private final double marketPrice;

    public Asset(String symbol, String name, double marketPrice) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (marketPrice < 0) {
            throw new IllegalArgumentException("Market price must be non-negative");
        }
        this.symbol = symbol;
        this.name = name;
        this.marketPrice = marketPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    /**
      Oblicza rzeczywistą wartość posiadanych aktywów (przy sprzedaży/wycenie)
      Uwzględnia specyficzne koszty (magazynowanie, spread)
     */
    public abstract double getRealValue(int quantity);

    /**
      Oblicza całkowity koszt zakupu danej ilości aktywa.
      Uwzględnia opłaty transakcyjne ( opłatę manipulacyjną przy zakupie akcji)
     */
    public abstract double getAcquisitionCost(int quantity);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(symbol, asset.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}