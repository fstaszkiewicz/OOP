package com.stockmarket.domain;

public class Currency extends Asset {
    private final double spreadRate;

    public Currency(String symbol, String name, double marketPrice, double spreadRate) {
        super(AssetType.CURRENCY, symbol, name, marketPrice);
        if (spreadRate < 0 || spreadRate >= 1.0) throw new IllegalArgumentException("spreadRate must be in [0,1)");
        this.spreadRate = spreadRate;
    }

    public double getSpreadRate() {
        return spreadRate;
    }

    @Override
    public double getRealValue(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity cannot be negative");
        double bidPrice = getMarketPrice() * (1.0 - spreadRate);
        return bidPrice * quantity;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        return getMarketPrice() * quantity;
    }
}