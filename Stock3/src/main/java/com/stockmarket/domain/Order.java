package com.stockmarket.domain;

import java.time.LocalDateTime;

/**
 Zlecenie kupna/sprzedaży.
 zlecenia mogą oczekiwać na realizację w kolejce priorytetowej (PriorityQueue).

 Najważniejsze pola dla priorytetyzacji:
 - typ (BUY/SELL)
 - limitPrice (dla BUY wyższy = bardziej atrakcyjny)
 - createdAt + sequence (tie-breaker przy równym limicie)
 */
public class Order {
    private final OrderType type;                // BUY lub SELL
    private final AssetType assetType;           // typ aktywa (enum)
    private final String symbol;                 // ticker aktywa
    private final int quantity;                  // wolumen
    private final double limitPrice;             // cena limit
    private final double marketPriceAtCreation;  // cena rynkowa w momencie złożenia (informacyjnie)
    private final LocalDateTime createdAt;       // czas złożenia
    private final long sequence;                 // licznik do rozstrzygania remisów

    public Order(OrderType type,
                 AssetType assetType,
                 String symbol,
                 int quantity,
                 double limitPrice,
                 double marketPriceAtCreation,
                 LocalDateTime createdAt,
                 long sequence) {

        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (assetType == null) throw new IllegalArgumentException("assetType cannot be null");
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (limitPrice < 0) throw new IllegalArgumentException("limitPrice must be non-negative");
        if (marketPriceAtCreation < 0) throw new IllegalArgumentException("marketPriceAtCreation must be non-negative");
        if (createdAt == null) throw new IllegalArgumentException("createdAt cannot be null");

        this.type = type;
        this.assetType = assetType;
        this.symbol = symbol;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.marketPriceAtCreation = marketPriceAtCreation;
        this.createdAt = createdAt;
        this.sequence = sequence;
    }

    public OrderType getType() {
        return type;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public double getMarketPriceAtCreation() {
        return marketPriceAtCreation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getSequence() {
        return sequence;
    }
}