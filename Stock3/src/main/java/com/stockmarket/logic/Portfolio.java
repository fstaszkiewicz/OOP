package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 Portfel

 - pozycje przechowują historię partii zakupowych (PurchaseLot)
 - sprzedaż rozliczana jest FIFO
 - lookup pozycji po symbolu (HashMap)
 - watchlist bez duplikatów (HashSet)
 - kolejka priorytetowa zleceń (PriorityQueue)
 */
public class Portfolio {
    private double cash; // dostępna gotówka

    // lookup po symbolu:
    private final Map<String, Position> positionsBySymbol;

    // watchlist - brak duplikatów na poziomie struktury:
    private final Set<String> watchlist;

    // kolejka zleceń oczekujących na realizację, priorytet wg "atrakcyjności":
    private final PriorityQueue<Order> orders;

    // licznik do tie-breakera w PriorityQueue (stabilność przy remisach)
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

    // --- Watchlist ---

    public boolean addToWatchlist(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        return watchlist.add(symbol);
    }

    public boolean isOnWatchlist(String symbol) {
        return watchlist.contains(symbol);
    }

    // --- Zakup ---

    /**
     Zakup aktywa:
     - sprawdzamy czy stać nas na zakup (koszt zależy od typu aktywa -> polimorfizm),
     - odejmujemy gotówkę,
     - dodajemy nową partię zakupową do pozycji (data + ilość + cena zakupu).
     */
    public void acquire(Asset asset, int quantity, LocalDate date, double unitPrice) {
        if (asset == null) throw new IllegalArgumentException("asset cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (date == null) throw new IllegalArgumentException("date cannot be null");
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice must be non-negative");

        double cost = asset.getAcquisitionCost(quantity);
        if (cash < cost) throw new InsufficientFundsException("Not enough cash. Have=" + cash + " need=" + cost);

        // znajdź lub utwórz pozycję
        Position pos = positionsBySymbol.get(asset.getSymbol());
        if (pos == null) {
            pos = new Position(asset);
            positionsBySymbol.put(asset.getSymbol(), pos);
        }

        // dopiero gdy pozycja istnieje, modyfikujemy stan portfela
        cash -= cost;

        // zapisujemy partię zakupową (do FIFO)
        pos.addLot(date, quantity, unitPrice);
    }

    // --- Sprzedaż ---

    /**
     Sprzedaż aktywa:
     - wywołuje FIFO w Position.sellFifo(),
     - zwiększa gotówkę o wpływ ze sprzedaży,
     - zwraca raport P&L (SaleResult).
     */
    public SaleResult sell(String symbol, int quantity, double sellUnitPrice) {
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (sellUnitPrice < 0) throw new IllegalArgumentException("sellUnitPrice must be non-negative");

        Position pos = positionsBySymbol.get(symbol);
        if (pos == null) throw new InsufficientHoldingsException("No position for symbol: " + symbol);

        SaleResult result = pos.sellFifo(quantity, sellUnitPrice);

        // przepływ gotówki ze sprzedaży (uprościenie): qty * sellPrice
        cash += sellUnitPrice * quantity;

        // jeśli po sprzedaży nic nie zostało, usuwamy pozycję
        if (pos.getTotalQuantity() == 0) {
            positionsBySymbol.remove(symbol);
        }

        return result;
    }

    // --- Majątek ---

    // Całkowita wartość portfela: gotówka + wartość rzeczywista wszystkich pozycji
    public double calculateTotalRealValue() {
        double sum = cash;
        for (Position pos : positionsBySymbol.values()) {
            sum += pos.getRealValue();
        }
        return sum;
    }

    // --- Kolejka zleceń ---

    public void submitLimitOrder(OrderType type, AssetType assetType, String symbol, int quantity, double limitPrice, double marketPrice) {
        Order order = new Order(
                type, assetType, symbol, quantity, limitPrice, marketPrice,
                LocalDateTime.now(),
                orderSequence++
        );
        orders.add(order);
    }

    public Order peekBestOrder() {
        return orders.peek();
    }

    public Order pollBestOrder() {
        return orders.poll();
    }

    // --- Persystencja ---

    // Widok na mapę pozycji - wykorzystywane przez moduł persystencji
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