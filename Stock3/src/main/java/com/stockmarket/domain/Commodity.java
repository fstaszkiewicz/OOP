package com.stockmarket.domain;

/**
 Rynek Surowcowy.
 Symuluje utratę wartości w czasie: przy każdej wycenie naliczany jest koszt magazynowania.

 Koszt jest procentowy i zależny od wolumenu:
 effectiveRate = storageCostRate * quantity.
 realValue = marketPrice * quantity * (1 - effectiveRate).

 Maksymalny spadek wartości w tej wycenie nie może przekroczyć 20%.
 */
public class Commodity extends Asset {
    private final double storageCostRate; // np. 0.001 dla 0.1% za jednostkę wolumenu
    private static final double MAX_EFFECTIVE_RATE = 0.20; // max 20%

    public Commodity(String symbol, String name, double marketPrice, double storageCostRate) {
        super(AssetType.COMMODITY, symbol, name, marketPrice);
        if (storageCostRate < 0) throw new IllegalArgumentException("storageCostRate cannot be negative");
        this.storageCostRate = storageCostRate;
    }

    public double getStorageCostRate() {
        return storageCostRate;
    }

    @Override
    public double getRealValue(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("quantity cannot be negative");

        double rawValue = getMarketPrice() * quantity;

        // procent kosztu rośnie wraz z wolumenem, ale nie więcej niż 20%
        double effectiveRate = storageCostRate * quantity;
        if (effectiveRate > MAX_EFFECTIVE_RATE) effectiveRate = MAX_EFFECTIVE_RATE;

        double realValue = rawValue * (1.0 - effectiveRate);

        // przy capie 20% realValue i tak będzie >= 0.8*rawValue, ale zostawiamy bezpiecznik
        if (realValue < 0) return 0.0;
        return realValue;
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        return getMarketPrice() * quantity;
    }
}