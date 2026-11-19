package com.pushkar.ordermatchingengine.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public class OrderBook {
    private final String symbol;
    private final TreeMap<BigDecimal, Queue<Order>> buyOrders = new TreeMap<>(Comparator.reverseOrder());
    private final TreeMap<BigDecimal, Queue<Order>> sellOrders = new TreeMap<>(Comparator.naturalOrder());
    private final Map<String, Order> orderIndex = new HashMap<>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public List<Trade> submitOrder(Order order) {
        List<Trade> trades = new ArrayList<>();
        match(order, trades);
        if(order.getQuantity().compareTo(BigDecimal.ZERO) > 0) addToBook(order);
        return trades;
    }

    private void match(Order incoming, List<Trade> trades) {
        TreeMap<BigDecimal, Queue<Order>> oppositeBook = incoming.getSide() == OrderSide.BUY ? sellOrders : buyOrders;
        while(incoming.getQuantity().compareTo(BigDecimal.ZERO) > 0 && !oppositeBook.isEmpty()) {
            BigDecimal bestPrice = oppositeBook.firstKey();
            boolean priceCrossess = incoming.getSide() == OrderSide.BUY ? incoming.getPrice().compareTo(bestPrice) >= 0 : incoming.getPrice().compareTo(bestPrice) <= 0; 
            if(!priceCrossess) break;

            Queue<Order> queue = oppositeBook.get(bestPrice);
            Order resting = queue.peek();
            BigDecimal tradeQty = incoming.getQuantity().min(resting.getQuantity());

            String buyOrderId = incoming.getSide() == OrderSide.BUY ? incoming.getOrderId() : resting.getOrderId();
            String sellOrderId = incoming.getSide() == OrderSide.SELL ? incoming.getOrderId() : resting.getOrderId();

            trades.add(new Trade(buyOrderId, sellOrderId, bestPrice, tradeQty));
            incoming.setQuantity(incoming.getQuantity().subtract(tradeQty));
            resting.setQuantity(resting.getQuantity().subtract(tradeQty));

            if(resting.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                queue.poll();
                orderIndex.remove(resting.getOrderId());
            }
            if(queue.isEmpty()) oppositeBook.remove((bestPrice));
        }
    }

    private void addToBook(Order order) {
        TreeMap<BigDecimal, Queue<Order>> book = order.getSide() == OrderSide.BUY ? buyOrders : sellOrders;
        book.computeIfAbsent(order.getPrice(), price -> new LinkedList<>()).add(order);
        orderIndex.put(order.getOrderId(), order);
    }

    public boolean cancelOrder(String orderId) {
        Order order = orderIndex.get(orderId);
        if(order == null) return false;

        TreeMap<BigDecimal, Queue<Order>> book = order.getSide() == OrderSide.BUY ? buyOrders : sellOrders;
        Queue<Order> queue = book.get(order.getPrice());
        if(queue != null) {
            queue.remove(order);
            if(queue.isEmpty()) book.remove(order.getPrice());
        }

        orderIndex.remove(orderId);
        return true;
    }
    
    public BigDecimal getBestBidPrice() {
        return buyOrders.isEmpty() ? null : buyOrders.firstKey();
    }

    public BigDecimal getBestAskPrice() {
        return sellOrders.isEmpty() ? null : sellOrders.firstKey();
    }
}
