package com.pushkar.ordermatchingengine.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.pushkar.ordermatchingengine.model.Order;
import com.pushkar.ordermatchingengine.model.OrderBook;
import com.pushkar.ordermatchingengine.model.Trade;

@Service 
public class OrderBookService {
    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();
    
    public List<Trade> submitOrder(Order order) {
        OrderBook book = books.computeIfAbsent(order.getSymbol(), OrderBook::new);
        return book.submitOrder(order);
    }

    public boolean cancelOrder(String symbol, String orderId) {
        OrderBook book = books.get(symbol);
        if(book == null) return false;
        return book.cancelOrder(orderId);
    }

    public OrderBook getBook(String symbol) {
        return books.get(symbol);
    }
}
