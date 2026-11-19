# Order Matching Engine

A core-engineering backend project implementing a simplified limit order matching engine similar to the matching logic used by real stock exchanges built with Java and Spring Boot to demonstrate concurrency-safe data structures, REST API design, and persistence.

## Overview

This engine accepts buy/sell limit orders for any symbol, matches them against each other using **price-time priority** (best price first, oldest order first at the same price), and persists every executed trade. It supports partial fills, order cancellation, and concurrent order submission across multiple symbols.

## Features

- Limit order submission with automatic matching against the opposite side of the book
- Price-time priority matching (best price wins; ties broken by order arrival time)
- Partial fill support, orders can be matched across multiple counter-orders
- Order cancellation
- Thread-safe order books, one lock per symbol, allowing different symbols to process concurrently
- REST API for submitting orders, cancelling orders, and viewing book/trade data
- Trade history persisted to an embedded H2 database (survives app restarts)
- Input validation on all incoming requests


## Architecture

```
model/          → Order, OrderSide, Trade, OrderBook (core matching logic + in-memory data structures)
dto/            → OrderRequest (API request contract)
service/        → OrderBookService (manages one OrderBook per symbol, thread-safe via ConcurrentHashMap)
controller/     → OrderController (REST endpoints)
entity/         → TradeEntity (JPA persistence model for trade history)
repository/     → TradeRepository (Spring Data JPA repository)
```

Each symbol gets its own `OrderBook`, backed by two `TreeMap`s (buy side sorted highest-price-first, sell side sorted lowest-price-first), with a `Queue` at each price level enforcing time priority. A `ReentrantLock` per `OrderBook` ensures safe concurrent access without blocking unrelated symbols.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Submit a new order (matches immediately if possible, rests on book otherwise) |
| DELETE | `/api/orders/{symbol}/{orderId}` | Cancel a resting order |
| GET | `/api/orders/{symbol}/book` | Get the current best bid/ask for a symbol |
| GET | `/api/orders/{symbol}/trades` | Get trade history for a symbol |

### Example: Submit an order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"symbol":"TATA","side":"BUY","price":101.50,"quantity":20}'
```

## Running Locally

```bash
./mvnw clean install
./mvnw spring-boot:run
```
The app starts on `http://localhost:8080`.