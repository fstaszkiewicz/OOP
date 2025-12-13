package com.stockmarket.domain;

public class Share extends Asset {
    private final double handlingFee;

    public Share(String symbol, String name, double marketPrice, double handlingFee) {
        super(symbol, name, marketPrice);
        if (handlingFee < 0) {
            throw new IllegalArgumentException("Handling fee cannot be negative");
        }
        this.handlingFee = handlingFee;
    }

    @Override
    public double getRealValue(int quantity) {
        // Wartość rzeczywista to cena rynkowa * ilość (bez ukrytych kosztów przy wycenie)
        return getMarketPrice() * quantity;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        // Cena * ilość + stała opłata manipulacyjna
        return (getMarketPrice() * quantity) + handlingFee;
    }
}