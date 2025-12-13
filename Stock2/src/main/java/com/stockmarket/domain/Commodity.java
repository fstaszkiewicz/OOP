package com.stockmarket.domain;

/**
  Rynek Surowcowy.
  Symuluje utratę wartości w czasie: przy każdej wycenie naliczany jest koszt magazynowania.

  Koszt jest procentowy i zależny od wolumenu:
  effectiveRate = storageCostRate * quantity.
  realValue = marketPrice * quantity * (1 - effectiveRate).

  maksymalny spadek wartości w tej wycenie nie może przekroczyć 20%.
 */
public class Commodity extends Asset {
    private final double storageCostRate; // np. 0.001 dla 0.1% za jednostkę wolumenu
    private static final double MAX_EFFECTIVE_RATE = 0.20; // max 20%

    public Commodity(String symbol, String name, double marketPrice, double storageCostRate) {
        super(symbol, name, marketPrice);
        if (storageCostRate < 0) {
            throw new IllegalArgumentException("Storage cost rate cannot be negative");
        }
        this.storageCostRate = storageCostRate;
    }

    @Override
    public double getRealValue(int quantity) {
        double rawValue = getMarketPrice() * quantity;

        // procent kosztu rośnie wraz z wolumenem, ale nie więcej niż 20%
        double effectiveRate = storageCostRate * quantity;
        effectiveRate = Math.min(MAX_EFFECTIVE_RATE, effectiveRate);

        double storageCost = rawValue * effectiveRate;
        double realValue = rawValue - storageCost;

        // przy capie 20% realValue i tak będzie >= 0.8*rawValue, ale zostawiamy bezpiecznik
        return Math.max(0, realValue);
    }

    @Override
    public double getAcquisitionCost(int quantity) {
        return getMarketPrice() * quantity;
    }
}