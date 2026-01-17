package com.stockmarket.domain;

/**
 Rynek Walutowy.
 Uwzględnia spread:
 - przy wycenie portfela liczymy Bid Price = marketPrice * (1 - spread)
 */
public class Currency extends Asset {
    private final double spreadRate; // np. 0.02 dla 2%

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
        // Bid Price = Cena Rynkowa * (1 - spread)
        double bidPrice = getMarketPrice() * (1.0 - spreadRate);
        return bidPrice * quantity;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        // Przy zakupie płacimy cenę rynkową (marketPrice)
        return getMarketPrice() * quantity;
    }
}