package com.stockmarket.domain;

import java.util.Objects;

public abstract class Asset {
    private final AssetType type;
    private final String symbol;
    private final String name;
    private double marketPrice;

    protected Asset(AssetType type, String symbol, String name, double marketPrice) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name cannot be null/empty");
        if (marketPrice < 0) throw new IllegalArgumentException("marketPrice must be non-negative");
        this.type = type;
        this.symbol = symbol;
        this.name = name;
        this.marketPrice = marketPrice;
    }

    public AssetType getType() {
        return type;
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

    public void setMarketPrice(double marketPrice) {
        if (marketPrice < 0) throw new IllegalArgumentException("marketPrice must be non-negative");
        this.marketPrice = marketPrice;
    }

    // Wartość "rzeczywista" w portfelu (uwzględnia koszty typu spread, storage, itd.)
    public abstract double getRealValue(int quantity);

    // Koszt zakupu (uwzględnia opłaty specyficzne rynku)
    public abstract double getAcquisitionCost(int quantity);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        return type == asset.type && symbol.equals(asset.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, symbol);
    }
}