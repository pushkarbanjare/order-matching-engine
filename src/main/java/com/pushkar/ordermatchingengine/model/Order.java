package com.pushkar.ordermatchingengine.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class Order {
    private final String orderId;
    private final String symbol;
    private final OrderSide side;
    private final BigDecimal price;
    private BigDecimal quantity;
    private final Instant timestamp;

    public Order(String symbol, OrderSide side, BigDecimal price, BigDecimal quantity) {
        this.orderId = UUID.randomUUID().toString();
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = Instant.now();
    }
}
