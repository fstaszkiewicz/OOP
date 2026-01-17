package com.stockmarket.logic;

import com.stockmarket.domain.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 Generator  raportu tekstowego o pozycjach w portfelu.
 - sortowanie aktywów wg złożonego klucza: Typ Aktywa -> Wartość Rynkowa (malejąco)
 - bez Stream API (zamiast tego Comparator + Collections.sort)
 */
public class ReportGenerator {

    public String generatePositionsReport(Map<String, Position> positionsBySymbol) {
        if (positionsBySymbol == null) throw new IllegalArgumentException("positionsBySymbol cannot be null");

        // przepisujemy mapę do listy, żeby dało się sortować
        List<Position> positions = new ArrayList<>();
        for (Position p : positionsBySymbol.values()) {
            positions.add(p);
        }

        // sortowanie: najpierw typ aktywa, potem market value malejąco
        Collections.sort(positions, new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                int t = a.getAsset().getType().compareTo(b.getAsset().getType());
                if (t != 0) return t;

                // market value desc:
                return Double.compare(b.getMarketValue(), a.getMarketValue());
            }
        });

        // raport separatorowy
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