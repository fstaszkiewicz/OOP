package com.stockmarket.logic;

import com.stockmarket.domain.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    public String generatePositionsReport(Map<String, Position> positionsBySymbol) {
        if (positionsBySymbol == null) throw new IllegalArgumentException("positionsBySymbol cannot be null");

        List<Position> positions = new ArrayList<>();
        for (Position p : positionsBySymbol.values()) {
            positions.add(p);
        }

        // Sortowanie: Typ -> Wartość Rynkowa malejąco (w obrębie typu)
        Collections.sort(positions, new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                int t = a.getAsset().getType().compareTo(b.getAsset().getType());
                if (t != 0) return t;

                // market value desc:
                return Double.compare(b.getMarketValue(), a.getMarketValue());
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("SYMBOL|TYPE|QTY|MARKET_PRICE|MARKET_VALUE|REAL_VALUE\n");
        for (int i = 0; i < positions.size(); i++) {
            Position p = positions.get(i);
            sb.append(p.getAsset().getSymbol()).append("|")
                    .append(p.getAsset().getType()).append("|")
                    .append(p.getTotalQuantity()).append("|")
                    .append(p.getAsset().getMarketPrice()).append("|")
                    .append(p.getMarketValue()).append("|")
                    .append(p.getRealValue())
                    .append("\n");
        }
        return sb.toString();
    }
}