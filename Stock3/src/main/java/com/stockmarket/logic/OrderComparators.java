package com.stockmarket.logic;

import com.stockmarket.domain.Order;
import com.stockmarket.domain.OrderType;

import java.util.Comparator;

public final class OrderComparators {
    private OrderComparators() {}

    public static Comparator<Order> byAttractiveness() {
        return new Comparator<Order>() {
            @Override
            public int compare(Order a, Order b) {
                // Najpierw typ: BUY i SELL można mieszać, ale wtedy trzeba konsekwencji.
                // Tu: sortujemy w obrębie typu; BUY przed SELL (prościej).
                int t = a.getType().compareTo(b.getType());
                if (t != 0) return t;

                if (a.getType() == OrderType.BUY) {
                    // BUY: wyższy limit = bardziej atrakcyjne => ma być "pierwsze" w PQ
                    int c = Double.compare(b.getLimitPrice(), a.getLimitPrice());
                    if (c != 0) return c;
                } else {
                    // SELL: niższy limit = bardziej atrakcyjne => ma być "pierwsze"
                    int c = Double.compare(a.getLimitPrice(), b.getLimitPrice());
                    if (c != 0) return c;
                }

                // Tie-breaker: wcześniejsze zlecenie ma pierwszeństwo
                int time = a.getCreatedAt().compareTo(b.getCreatedAt());
                if (time != 0) return time;

                return Long.compare(a.getSequence(), b.getSequence());
            }
        };
    }
}