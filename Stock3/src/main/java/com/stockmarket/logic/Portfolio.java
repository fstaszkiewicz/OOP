package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Portfolio {
    private double cash;

    private final Map<String, Position> positionsBySymbol;
    private final Set<String> watchlist;
    private final PriorityQueue<Order> orders;

    private long orderSequence;

    public Portfolio(double initialCash) {
        if (initialCash < 0) throw new IllegalArgumentException("initialCash cannot be negative");
        this.cash = initialCash;
        this.positionsBySymbol = new HashMap<>();
        this.watchlist = new HashSet<>();
        this.orders = new PriorityQueue<>(OrderComparators.byAttractiveness());
        this.orderSequence = 0;
    }

    public double getCash() {
        return cash;
    }

    public int getPositionsCount() {
        return positionsBySymbol.size();
    }

    public boolean addToWatchlist(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        return watchlist.add(symbol);
    }

    public boolean isOnWatchlist(String symbol) {
        return watchlist.contains(symbol);
    }

    public void acquire(Asset asset, int quantity, LocalDate date, double unitPrice) {
        if (asset == null) throw new IllegalArgumentException("asset cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (date == null) throw new IllegalArgumentException("date cannot be null");
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice must be non-negative");

        double cost = asset.getAcquisitionCost(quantity);
        if (cash < cost) throw new InsufficientFundsException("Not enough cash. Have=" + cash + " need=" + cost);

        Position pos = positionsBySymbol.get(asset.getSymbol());
        if (pos == null) {
            pos = new Position(asset);
            positionsBySymbol.put(asset.getSymbol(), pos);
        }

        cash -= cost;
        pos.addLot(date, quantity, unitPrice);
    }

    public SaleResult sell(String symbol, int quantity, double sellUnitPrice) {
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (sellUnitPrice < 0) throw new IllegalArgumentException("sellUnitPrice must be non-negative");

        Position pos = positionsBySymbol.get(symbol);
        if (pos == null) throw new InsufficientHoldingsException("No position for symbol: " + symbol);

        SaleResult result = pos.sellFifo(quantity, sellUnitPrice);

        cash += sellUnitPrice * quantity;

        if (pos.getTotalQuantity() == 0) {
            positionsBySymbol.remove(symbol);
        }

        return result;
    }

    public double calculateTotalRealValue() {
        double sum = cash;
        for (Position pos : positionsBySymbol.values()) {
            sum += pos.getRealValue();
        }
        return sum;
    }

    public void submitLimitOrder(OrderType type, AssetType assetType, String symbol, int quantity, double limitPrice, double marketPrice) {
        Order order = new Order(
                type, assetType, symbol, quantity, limitPrice, marketPrice,
                LocalDateTime.now(), orderSequence++
        );
        orders.add(order);
    }

    public Order peekBestOrder() {
        return orders.peek();
    }

    public Order pollBestOrder() {
        return orders.poll();
    }

    // --- Persystencja: prosty dostęp do stanu ---

    public Map<String, Position> getPositionsView() {
        return positionsBySymbol;
    }

    public void setCashForPersistence(double cash) {
        if (cash < 0) throw new IllegalArgumentException("cash cannot be negative");
        this.cash = cash;
    }

    public void putPositionForPersistence(Position position) {
        if (position == null) throw new IllegalArgumentException("position cannot be null");
        positionsBySymbol.put(position.getAsset().getSymbol(), position);
    }
}