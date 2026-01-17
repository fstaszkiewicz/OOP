package com.stockmarket.domain;

public class Share extends Asset {
    private final double handlingFee;

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