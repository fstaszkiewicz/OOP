package com.stockmarket;

public class Portfolio {
    private double cash; // dostępna gotówka
    private StockHolding[] holdings; // tablica przechowująca posiadane akcje
    private int holdingsCount; // licznik mówiący ile pozycji jest aktualnie w tablicy

    public Portfolio(double initialCash) {
        this.cash = initialCash;
        this.holdings = new StockHolding[10];
        this.holdingsCount = 0;
    }

    public static class StockHolding {
        private Stock stock;
        private int quantity;

        public StockHolding(Stock stock, int quantity) {
            if (stock == null) throw new IllegalArgumentException("stock cannot be null");
            if (quantity < 0) throw new IllegalArgumentException("quantity cannot be negative");
            this.stock = stock;
            this.quantity = quantity;
        }

        public Stock getStock() {
            return stock;
        }

        public int getQuantity() {
            return quantity;
        }

        public void increaseQuantity(int delta) {
            if (delta < 0) throw new IllegalArgumentException("delta cannot be negative");
            this.quantity += delta;
        }
    }

    public double getCash() {
        return cash;
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public void addStock(Stock stock, int quantity) {
        if (stock == null) throw new IllegalArgumentException("stock cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");

        // check existing
        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].getStock().equals(stock)) {
                holdings[i].increaseQuantity(quantity);
                return;
            }
        }

        // add new if space
        if (holdingsCount >= holdings.length) {
            throw new IllegalStateException("Holdings array is full");
        }

        holdings[holdingsCount++] = new StockHolding(stock, quantity);
    }

    public int getStockQuantity(Stock stock) {
        if (stock == null) return 0;
        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].getStock().equals(stock)) {
                return holdings[i].getQuantity();
            }
        }
        return 0;
    }

    public double calculateStockValue() {
        double sum = 0.0;
        for (int i = 0; i < holdingsCount; i++) {
            StockHolding h = holdings[i];
            sum += h.getQuantity() * h.getStock().getInitialPrice();
        }
        return sum;
    }

    public double calculateTotalValue() {
        return cash + calculateStockValue();
    }
}