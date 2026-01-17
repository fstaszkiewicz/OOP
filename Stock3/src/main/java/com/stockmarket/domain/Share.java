package com.stockmarket.domain;

/**
 Rynek Akcji.
 Najprostszy instrument:
 - wartość rzeczywista = cena rynkowa * ilość
 - koszt nabycia uwzględnia stałą opłatę manipulacyjną (handlingFee)
 */
public class Share extends Asset {
    private final double handlingFee; // stała opłata manipulacyjna przy zakupie

    public Share(String symbol, String name, double marketPrice, double handlingFee) {
        super(AssetType.SHARE, symbol, name, marketPrice);
        if (handlingFee < 0) throw new IllegalArgumentException("handlingFee cannot be negative");
        this.handlingFee = handlingFee;
    }

    public double getHandlingFee() {
        return handlingFee;
    }

    @Override
    public double getRealValue(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity cannot be negative");
        return getMarketPrice() * quantity;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        return (getMarketPrice() * quantity) + handlingFee;
    }
}