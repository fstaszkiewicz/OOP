package com.stockmarket.domain;

import java.time.LocalDateTime;

public class Order {
    private final OrderType type;
    private final AssetType assetType;
    private final String symbol;
    private final int quantity;
    private final double limitPrice;
    private final double marketPriceAtCreation;
    private final LocalDateTime createdAt;
    private final long sequence;

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