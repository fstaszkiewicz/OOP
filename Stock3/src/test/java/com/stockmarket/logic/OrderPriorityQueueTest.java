package com.stockmarket.logic;

import com.stockmarket.domain.AssetType;
import com.stockmarket.domain.Order;
import com.stockmarket.domain.OrderType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderPriorityQueueTest {

    @Test
    void buyLimitOrderCloserToMarketShouldBeOnTop() {
        Portfolio p = new Portfolio(0.0);

        double marketPrice = 110.0;

        p.submitLimitOrder(OrderType.BUY, AssetType.SHARE, "XYZ", 1, 100.0, marketPrice);
        p.submitLimitOrder(OrderType.BUY, AssetType.SHARE, "XYZ", 1, 105.0, marketPrice);

        Order best = p.peekBestOrder();
        assertEquals(105.0, best.getLimitPrice(), 1e-9);
    }
}