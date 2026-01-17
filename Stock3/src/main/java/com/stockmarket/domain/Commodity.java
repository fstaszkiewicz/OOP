package com.stockmarket.domain;

public class Commodity extends Asset {
    private final double storageCostRate;
    private static final double MAX_EFFECTIVE_RATE = 0.20;

    public Commodity(String symbol, String name, double marketPrice, double storageCostRate) {
        super(AssetType.COMMODITY, symbol, name, marketPrice);
        if (storageCostRate < 0) throw new IllegalArgumentException("storageCostRate cannot be negative");
        this.storageCostRate = storageCostRate;
    }

    public double getStorageCostRate() {
        return storageCostRate;
    }

    @Override
    public double getRealValue(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity cannot be negative");

        double raw = getMarketPrice() * quantity;
        double effectiveRate = storageCostRate * quantity;
        if (effectiveRate > MAX_EFFECTIVE_RATE) effectiveRate = MAX_EFFECTIVE_RATE;

        double real = raw * (1.0 - effectiveRate);
        if (real < 0) return 0.0;
        return real;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        return getMarketPrice() * quantity;
    }
}