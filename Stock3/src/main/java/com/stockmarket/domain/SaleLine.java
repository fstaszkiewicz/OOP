package com.stockmarket.domain;

import java.time.LocalDate;

/**
 Pojedyncza linia raportu sprzedaży (FIFO).
 Opisuje, ile sztuk sprzedano z konkretnej partii zakupowej oraz zysk/stratę dla tej części transakcji.
 */
public class SaleLine {
    private final LocalDate buyDate;             // data zakupu partii
    private final int quantitySoldFromLot;       // ile sztuk "zużyto" z tej partii
    private final double buyUnitPrice;           // cena zakupu (dla tej partii)
    private final double sellUnitPrice;          // cena sprzedaży (dla całej transakcji)
    private final double profit;                 // P&L dla tej linii = qty * (sell - buy)

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