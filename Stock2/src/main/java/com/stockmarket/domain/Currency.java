package com.stockmarket.domain;

/**
  Rynek Walutowy.
  Uwzględnia Spread.
  Wycena to Bid Price (cena rynkowa - spread).
 */
public class Currency extends Asset {
    private final double spreadRate; // np. 0.02 dla 2%

    public Currency(String symbol, String name, double marketPrice, double spreadRate) {
        super(symbol, name, marketPrice);
        if (spreadRate < 0 || spreadRate >= 1.0) {
            throw new IllegalArgumentException("Spread rate must be between 0 and 1");
        }
        this.spreadRate = spreadRate;
    }

    @Override
    public double getRealValue(int quantity) {
        // Bid Price = Cena Rynkowa * (1 - spread)
        double bidPrice = getMarketPrice() * (1 - spreadRate);
        return bidPrice * quantity;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        // Przy zakupie płacimy cenę rynkową (marketPrice)
        return getMarketPrice() * quantity;
    }
}