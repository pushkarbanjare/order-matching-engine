package com.pushkar.ordermatchingengine.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter 
public class Trade {
    private final String tradeId;
    private final String buyOrderId;
    private final String sellOrderId;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private final Instant executedAt;

    public Trade(String buyOrderId, String sellOrderId, BigDecimal price, BigDecimal quantity) {
        this.tradeId = UUID.randomUUID().toString();
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
        this.executedAt = Instant.now();
    }
}
