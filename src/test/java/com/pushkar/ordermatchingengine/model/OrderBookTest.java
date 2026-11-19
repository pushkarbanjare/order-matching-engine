package com.pushkar.ordermatchingengine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderBookTest {

    private OrderBook book;

    @BeforeEach
    void setUp() {
        // runs fresh before EVERY @Test method, so tests don't leak state into each other
        book = new OrderBook("TATA");
    }

    @Test
    void shouldPartiallyFillIncomingOrderAgainstSmallerRestingOrder() {
        Order sell1 = new Order("TATA", OrderSide.SELL, new BigDecimal("101.50"), new BigDecimal("20"));
        book.submitOrder(sell1);

        Order buy1 = new Order("TATA", OrderSide.BUY, new BigDecimal("101.50"), new BigDecimal("35"));
        List<Trade> trades = book.submitOrder(buy1);

        assertEquals(1, trades.size(), "Exactly one trade should execute");
        assertEquals(new BigDecimal("20"), trades.get(0).getQuantity(), "Trade quantity should match the smaller order");
        assertEquals(new BigDecimal("101.50"), book.getBestBidPrice(), "Remaining 15 of buy1 should rest on the book");
    }

    @Test
    void shouldRespectTimePriorityAtSamePriceLevel() {
        Order sellA = new Order("TATA", OrderSide.SELL, new BigDecimal("100.00"), new BigDecimal("10"));
        Order sellB = new Order("TATA", OrderSide.SELL, new BigDecimal("100.00"), new BigDecimal("10"));
        book.submitOrder(sellA); // placed first
        book.submitOrder(sellB); // placed second, same price

        Order buy1 = new Order("TATA", OrderSide.BUY, new BigDecimal("100.00"), new BigDecimal("10"));
        List<Trade> trades = book.submitOrder(buy1);

        assertEquals(1, trades.size());
        assertEquals(sellA.getOrderId(), trades.get(0).getSellOrderId(), "Older order (sellA) must match first, not sellB");
    }

    @Test
    void shouldNotMatchWhenPricesDoNotCross() {
        Order sell1 = new Order("TATA", OrderSide.SELL, new BigDecimal("105.00"), new BigDecimal("10"));
        book.submitOrder(sell1);

        Order buy1 = new Order("TATA", OrderSide.BUY, new BigDecimal("100.00"), new BigDecimal("10"));
        List<Trade> trades = book.submitOrder(buy1);

        assertTrue(trades.isEmpty(), "No trade should happen when buy price is below the ask");
        assertEquals(new BigDecimal("100.00"), book.getBestBidPrice());
        assertEquals(new BigDecimal("105.00"), book.getBestAskPrice());
    }

    @Test
    void shouldCancelRestingOrderSuccessfully() {
        Order buy1 = new Order("TATA", OrderSide.BUY, new BigDecimal("99.00"), new BigDecimal("10"));
        book.submitOrder(buy1);

        boolean cancelled = book.cancelOrder(buy1.getOrderId());

        assertTrue(cancelled, "Cancel should succeed for an existing resting order");
        assertNull(book.getBestBidPrice(), "Book should be empty after the only order is cancelled");
    }

    @Test
    void cancellingNonExistentOrderShouldReturnFalse() {
        boolean cancelled = book.cancelOrder("some-fake-id");
        assertFalse(cancelled);
    }
}