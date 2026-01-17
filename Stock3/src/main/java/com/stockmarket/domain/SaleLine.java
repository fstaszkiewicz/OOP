package com.stockmarket.domain;

import java.time.LocalDate;

public class SaleLine {
    private final LocalDate buyDate;
    private final int quantitySoldFromLot;
    private final double buyUnitPrice;
    private final double sellUnitPrice;
    private final double profit;

    public SaleLine(LocalDate buyDate, int quantitySoldFromLot, double buyUnitPrice, double sellUnitPrice) {
        if (buyDate == null) throw new IllegalArgumentException("buyDate cannot be null");
        if (quantitySoldFromLot <= 0) throw new IllegalArgumentException("quantitySoldFromLot must be positive");
        this.buyDate = buyDate;
        this.quantitySoldFromLot = quantitySoldFromLot;
        this.buyUnitPrice = buyUnitPrice;
        this.sellUnitPrice = sellUnitPrice;
        this.profit = quantitySoldFromLot * (sellUnitPrice - buyUnitPrice);
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public int getQuantitySoldFromLot() {
        return quantitySoldFromLot;
    }

    public double getBuyUnitPrice() {
        return buyUnitPrice;
    }

    public double getSellUnitPrice() {
        return sellUnitPrice;
    }

    public double getProfit() {
        return profit;
    }
}