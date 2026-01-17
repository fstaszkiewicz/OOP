package com.stockmarket.domain;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;

public class Position {
    private final Asset asset;
    private final Deque<PurchaseLot> lots;

    public Position(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("asset cannot be null");
        this.asset = asset;
        this.lots = new ArrayDeque<>();
    }

    public Asset getAsset() {
        return asset;
    }

    public void addLot(LocalDate date, int quantity, double unitPrice) {
        lots.addLast(new PurchaseLot(date, quantity, unitPrice));
    }

    public int getTotalQuantity() {
        int sum = 0;
        for (PurchaseLot lot : lots) { // enhanced for ok (to nie stream)
            sum += lot.getQuantity();
        }
        return sum;
    }

    public double getMarketValue() {
        return asset.getMarketPrice() * getTotalQuantity();
    }

    public double getRealValue() {
        return asset.getRealValue(getTotalQuantity());
    }

    public SaleResult sellFifo(int quantityToSell, double sellUnitPrice) {
        if (quantityToSell <= 0) throw new IllegalArgumentException("quantityToSell must be positive");
        if (sellUnitPrice < 0) throw new IllegalArgumentException("sellUnitPrice must be non-negative");

        int total = getTotalQuantity();
        if (quantityToSell > total) {
            throw new InsufficientHoldingsException("Not enough holdings to sell. Have=" + total + " want=" + quantityToSell);
        }

        SaleResult result = new SaleResult(asset.getSymbol(), asset.getType(), quantityToSell, sellUnitPrice);

        int remaining = quantityToSell;
        while (remaining > 0) {
            PurchaseLot lot = lots.peekFirst();
            if (lot == null) throw new IllegalStateException("Internal error: lots empty during sell");

            int available = lot.getQuantity();
            if (available <= remaining) {
                // zamykamy cały lot
                lots.removeFirst();
                SaleLine line = new SaleLine(lot.getDate(), available, lot.getUnitPrice(), sellUnitPrice);
                result.addLine(line);
                remaining -= available;
            } else {
                // częściowa sprzedaż z lotu
                lot.decreaseQuantity(remaining);
                SaleLine line = new SaleLine(lot.getDate(), remaining, lot.getUnitPrice(), sellUnitPrice);
                result.addLine(line);
                remaining = 0;
            }
        }

        return result;
    }

    public Deque<PurchaseLot> getLotsSnapshot() {
        // do persystencji/raportów – zwracamy kopię kolejki
        return new ArrayDeque<>(lots);
    }
}