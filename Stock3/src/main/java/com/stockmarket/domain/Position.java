package com.stockmarket.domain;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 Pozycja w portfelu dla jednego aktywa.
 Zawiera:
 - referencję do aktywa (Share/Currency/Commodity),
 - kolejkę partii zakupowych (PurchaseLot) w kolejności FIFO.
 */
public class Position {
    private final Asset asset;
    private final Deque<PurchaseLot> lots; // FIFO: najstarsza partia jest na początku kolejki

    public Position(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("asset cannot be null");
        this.asset = asset;
        this.lots = new ArrayDeque<>();
    }

    public Asset getAsset() {
        return asset;
    }

    // Dodanie nowej partii zakupowej na koniec kolejki (nowsze zakupy)
    public void addLot(LocalDate date, int quantity, double unitPrice) {
        lots.addLast(new PurchaseLot(date, quantity, unitPrice));
    }

    // Suma ilości ze wszystkich partii
    public int getTotalQuantity() {
        int sum = 0;
        for (PurchaseLot lot : lots) {
            sum += lot.getQuantity();
        }
        return sum;
    }

    // Wartość rynkowa (bez ukrytych kosztów) = marketPrice * ilość
    public double getMarketValue() {
        return asset.getMarketPrice() * getTotalQuantity();
    }

    // Wartość "rzeczywista" (polimorficzna) = getRealValue(totalQty)
    public double getRealValue() {
        return asset.getRealValue(getTotalQuantity());
    }

    /**
     Sprzedaż metodą FIFO:
     - redukujemy stan od najstarszej partii,
     - obsługujemy sprzedaż częściową (zmniejszenie quantity w locie),
     - obsługujemy sprzedaż przez wiele partii (zamykanie kolejnych lotów),
     - zwracamy raport P&L (SaleResult + SaleLine).
     */
    public SaleResult sellFifo(int quantityToSell, double sellUnitPrice) {
        if (quantityToSell <= 0) throw new IllegalArgumentException("quantityToSell must be positive");
        if (sellUnitPrice < 0) throw new IllegalArgumentException("sellUnitPrice must be non-negative");

        int total = getTotalQuantity();
        if (quantityToSell > total) {
            throw new InsufficientHoldingsException("Not enough holdings to sell. Have=" + total + " want=" + quantityToSell);
        }

        SaleResult result = new SaleResult(asset.getSymbol(), asset.getType(), quantityToSell, sellUnitPrice);

        int remaining = quantityToSell;

        // dopóki nie sprzedamy całej wymaganej ilości:
        while (remaining > 0) {
            PurchaseLot lot = lots.peekFirst();
            if (lot == null) throw new IllegalStateException("Internal error: lots empty during sell");

            int available = lot.getQuantity();

            if (available <= remaining) {
                // zamykamy cały najstarszy lot
                lots.removeFirst();
                result.addLine(new SaleLine(lot.getDate(), available, lot.getUnitPrice(), sellUnitPrice));
                remaining -= available;
            } else {
                // sprzedaż częściowa: lot zostaje, ale quantity się zmniejsza
                lot.decreaseQuantity(remaining);
                result.addLine(new SaleLine(lot.getDate(), remaining, lot.getUnitPrice(), sellUnitPrice));
                remaining = 0;
            }
        }

        return result;
    }

    // Snapshot do persystencji/raportów (żeby nie wystawiać bezpośrednio wewnętrznej kolejki)
    public Deque<PurchaseLot> getLotsSnapshot() {
        return new ArrayDeque<>(lots);
    }
}