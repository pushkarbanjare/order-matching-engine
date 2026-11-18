package com.pushkar.ordermatchingengine.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

public class OrderBook {
    private final String symbol;

    // highest price first for BUY
    private final TreeMap<BigDecimal, Queue<Order>> buyOrders = new TreeMap<>(Comparator.reverseOrder());
    
    // lowest price first for SELL
    private final TreeMap<BigDecimal, Queue<Order>> sellOrders = new TreeMap<>(Comparator.naturalOrder());

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public void addOrder(Order order) {
        TreeMap<BigDecimal, Queue<Order>> book = order.getSide() == OrderSide.BUY ? buyOrders : sellOrders;
        book.computeIfAbsent(order.getPrice(), price -> new LinkedList<>()).add(order);
    }
    
    public BigDecimal getBestBidPrice() {
        return buyOrders.isEmpty() ? null : buyOrders.firstKey();
    }

    public BigDecimal getBestAskPrice() {
        return sellOrders.isEmpty() ? null : sellOrders.firstKey();
    }
}
