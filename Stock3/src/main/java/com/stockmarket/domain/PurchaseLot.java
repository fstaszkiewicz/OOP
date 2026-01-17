package com.stockmarket.domain;

import java.time.LocalDate;

/**
 Partia zakupowa (Purchase Lot).
 Reprezentuje pojedynczy zakup: data + ilość + cena jednostkowa.

 Etap 3:
 - portfel nie przechowuje średniej ceny,
 - historia zakupu jest niezbędna do FIFO przy sprzedaży.
 */
public class PurchaseLot {
    private final LocalDate date;   // data zakupu
    private int quantity;           // ile sztuk zostało w tej partii (może maleć przy sprzedaży częściowej)
    private final double unitPrice; // cena zakupu za 1 sztukę

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

    // Zmniejsza ilość w partii (sprzedaż częściowa)
    public void decreaseQuantity(int delta) {
        if (delta <= 0) throw new IllegalArgumentException("delta must be positive");
        if (delta > quantity) throw new IllegalArgumentException("delta cannot exceed lot quantity");
        this.quantity -= delta;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}