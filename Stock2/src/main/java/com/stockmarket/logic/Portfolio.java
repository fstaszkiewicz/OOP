package com.stockmarket.logic;

import com.stockmarket.domain.Asset;

public class Portfolio {
    private double cash;
    private final PortfolioHolding[] holdings;
    private int holdingsCount;

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("initialCash cannot be negative");
        }
        this.cash = initialCash;
        this.holdings = new PortfolioHolding[10];
        this.holdingsCount = 0;
    }

    // zagnieżdżona klasa przechowująca aktywo i ilość
    public static class PortfolioHolding {
        private final Asset asset;
        private int quantity;

        public PortfolioHolding(Asset asset, int quantity) {
            if (asset == null) throw new IllegalArgumentException("asset cannot be null");
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
            this.asset = asset;
            this.quantity = quantity;
        }

        public Asset getAsset() {
            return asset;
        }

        public int getQuantity() {
            return quantity;
        }

        public void increaseQuantity(int delta) {
            if (delta <= 0) throw new IllegalArgumentException("delta must be positive");
            this.quantity += delta;
        }
    }

    public double getCash() {
        return cash;
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    // mechanizm zakupu dowolnego aktywa
    public void acquire(Asset asset, int quantity) {
        if (asset == null) throw new IllegalArgumentException("asset cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");

        double acquisitionCost = asset.getAcquisitionCost(quantity);
        if (acquisitionCost <= 0) {
            throw new IllegalArgumentException("acquisitionCost must be positive");
        }
        if (cash < acquisitionCost) {
            throw new IllegalStateException("Not enough cash to acquire asset");
        }

        // dopiero tutaj modyfikujemy stan portfela
        cash -= acquisitionCost;

        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].getAsset().equals(asset)) {
                holdings[i].increaseQuantity(quantity);
                return;
            }
        }

        if (holdingsCount >= holdings.length) {
            throw new IllegalStateException("Holdings array is full");
        }
        holdings[holdingsCount++] = new PortfolioHolding(asset, quantity);
    }

    public int getAssetQuantity(Asset asset) {
        if (asset == null) return 0;
        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].getAsset().equals(asset)) {
                return holdings[i].getQuantity();
            }
        }
        return 0;
    }

    // Suma wartości rzeczywistych wszystkich aktywów (polimorficznie)
    public double calculateAssetsValue() {
        double sum = 0.0;
        for (int i = 0; i < holdingsCount; i++) {
            PortfolioHolding h = holdings[i];
            sum += h.getAsset().getRealValue(h.getQuantity());
        }
        return sum;
    }

    // Całkowita wartość portfela (gotówka + wartość aktywów)
    public double calculateTotalValue() {
        return cash + calculateAssetsValue();
    }
}