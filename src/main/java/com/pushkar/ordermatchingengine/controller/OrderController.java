package com.pushkar.ordermatchingengine.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pushkar.ordermatchingengine.dto.OrderRequest;
import com.pushkar.ordermatchingengine.entity.TradeEntity;
import com.pushkar.ordermatchingengine.model.Order;
import com.pushkar.ordermatchingengine.model.OrderBook;
import com.pushkar.ordermatchingengine.model.Trade;
import com.pushkar.ordermatchingengine.repository.TradeRepository;
import com.pushkar.ordermatchingengine.service.OrderBookService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderBookService orderBookService;
    private final TradeRepository tradeRepository;

    public OrderController(OrderBookService orderBookService, TradeRepository tradeRepository) {
        this.orderBookService = orderBookService;
        this.tradeRepository = tradeRepository;
    }

    @PostMapping 
    public ResponseEntity<Map<String, Object>> submitOrder(@Valid @RequestBody OrderRequest request) {
        Order order = new Order(request.getSymbol(), request.getSide(), request.getPrice(), request.getQuantity());
        List<Trade> trades = orderBookService.submitOrder(order);

        for(Trade trade : trades) {
            tradeRepository.save(new TradeEntity(
                trade.getTradeId(),
                request.getSymbol(),
                trade.getBuyOrderId(),
                trade.getSellOrderId(),
                trade.getPrice(),
                trade.getQuantity(),
                trade.getExecutedAt()
            ));
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "orderId", order.getOrderId(),
            "remainingQuantity", order.getQuantity(),
            "trades", trades
        ));
    }

    @DeleteMapping("/{symbol}/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String symbol, @PathVariable String orderId) {
        boolean cancelled = orderBookService.cancelOrder(symbol, orderId);
        return cancelled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{symbol}/book")
    public ResponseEntity<Map<String, BigDecimal>> getBookSummary(@PathVariable String symbol) {
        OrderBook book = orderBookService.getBook(symbol);
        if (book == null) return ResponseEntity.notFound().build();
        
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("bestBid", book.getBestBidPrice());
        summary.put("bestAsk", book.getBestAskPrice());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{symbol}/trades")
    public ResponseEntity<List<TradeEntity>> getTradeHistory(@PathVariable String symbol) {
        return ResponseEntity.ok(tradeRepository.findBySymbolOrderByExecutedAtDesc(symbol));
    }
}
