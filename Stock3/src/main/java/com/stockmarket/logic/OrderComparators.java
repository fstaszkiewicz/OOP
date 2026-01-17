package com.stockmarket.logic;

import com.stockmarket.domain.Order;
import com.stockmarket.domain.OrderType;

import java.util.Comparator;

/**
 Komparatory dla kolejki priorytetowej zleceń.
 Wymaganie etapu 3:
 - struktura automatycznie priorytetyzuje zlecenia wg atrakcyjności ceny względem rynku.

 BUY-limit:
 - im wyższy limit, tym bardziej atrakcyjne (bliżej rynku) -> ma być wyżej w PriorityQueue
 SELL-limit:
 - im niższy limit, tym bardziej atrakcyjne -> ma być wyżej w PriorityQueue
 */
public final class OrderComparators {
    private OrderComparators() {}

    public static Comparator<Order> byAttractiveness() {
        return new Comparator<Order>() {
            @Override
            public int compare(Order a, Order b) {
                // Grupowanie po typie zlecenia
                int t = a.getType().compareTo(b.getType());
                if (t != 0) return t;

                if (a.getType() == OrderType.BUY) {
                    // BUY: wyższy limit ma być wcześniej (descending)
                    int c = Double.compare(b.getLimitPrice(), a.getLimitPrice());
                    if (c != 0) return c;
                } else {
                    // SELL: niższy limit ma być wcześniej (ascending)
                    int c = Double.compare(a.getLimitPrice(), b.getLimitPrice());
                    if (c != 0) return c;
                }

                // tie-breaker: starsze zlecenie wyżej (czas + sekwencja)
                int time = a.getCreatedAt().compareTo(b.getCreatedAt());
                if (time != 0) return time;

                return Long.compare(a.getSequence(), b.getSequence());
            }
        };
    }
}