package com.stockmarket.domain;

import java.time.LocalDate;

public class PurchaseLot {
    private final LocalDate date;
    private int quantity;
    private final double unitPrice;

    public PurchaseLot(LocalDate date, int quantity, double unitPrice) {
        if (date == null) throw new IllegalArgumentException("date cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice must be non-negative");
        this.date = date;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void decreaseQuantity(int delta) {
        if (delta <= 0) throw new IllegalArgumentException("delta must be positive");
        if (delta > quantity) throw new IllegalArgumentException("delta cannot exceed lot quantity");
        this.quantity -= delta;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}