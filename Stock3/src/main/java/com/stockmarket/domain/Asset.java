package com.stockmarket.domain;

import java.util.Objects;

/**
 Abstrakcyjna klasa bazowa dla dowolnego aktywa (instrumentu finansowego).
 Wspólne pola: symbol, nazwa, bieżąca cena rynkowa.

 Etap 3:
 - cena rynkowa nie jest finalna (może się zmieniać w czasie),
 - klasy potomne implementują specyficzną logikę:
 * wycena rzeczywista (np. spread, magazynowanie)
 * koszt nabycia (np. opłata manipulacyjna)
 */
public abstract class Asset {
    private final AssetType type;      // typ aktywa (enum) - zakaz sterowania logiką przez String
    private final String symbol;       // ticker / identyfikator
    private final String name;         // pełna nazwa
    private double marketPrice;        // bieżąca cena rynkowa (zmienna w czasie)

    public Asset(AssetType type, String symbol, String name, double marketPrice) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (symbol == null || symbol.trim().isEmpty()) throw new IllegalArgumentException("symbol cannot be null/empty");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name cannot be null/empty");
        if (marketPrice < 0) throw new IllegalArgumentException("marketPrice must be non-negative");
        this.type = type;
        this.symbol = symbol;
        this.name = name;
        this.marketPrice = marketPrice;
    }

    public AssetType getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    // Pozwala aktualizować wycenę rynkową (np. symulacja zmian rynku)
    public void setMarketPrice(double marketPrice) {
        if (marketPrice < 0) throw new IllegalArgumentException("marketPrice must be non-negative");
        this.marketPrice = marketPrice;
    }

    /**
     Oblicza rzeczywistą wartość posiadanych aktywów (przy sprzedaży/wycenie).
     Uwzględnia specyficzne koszty (magazynowanie, spread).
     */
    public abstract double getRealValue(int quantity);

    /**
     Oblicza całkowity koszt zakupu danej ilości aktywa.
     Uwzględnia opłaty transakcyjne (np. opłatę manipulacyjną przy zakupie akcji).
     */
    public abstract double getAcquisitionCost(int quantity);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asset)) return false;
        Asset asset = (Asset) o;
        // Przyjmujemy unikalność w obrębie (type + symbol)
        return type == asset.type && symbol.equals(asset.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, symbol);
    }
}