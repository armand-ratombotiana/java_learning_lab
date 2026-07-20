# E-Commerce Platform

A production-grade e-commerce platform built in Java implementing product catalog management, shopping cart with persistence, order state machine, payment processing with idempotency, inventory management with reservation/release, collaborative filtering recommendation engine, and admin analytics dashboard.

## Architecture Overview

```
┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
│ Product     │  │ Shopping     │  │ Order State  │  │ Payment          │
│ Catalog     │  │ Cart         │  │ Machine      │  │ Processor        │
├─────────────┤  ├──────────────┤  ├──────────────┤  ├──────────────────┤
│ Search      │  │ Add/Remove   │  │ PENDING →    │  │ Idempotency      │
│ Filter      │  │ Quantity     │  │ CONFIRMED →  │  │ CREDIT_CARD      │
│ Category    │  │ Total Calc   │  │ PROCESSING → │  │ PAYPAL           │
│ Tag Index   │  │ Persistence  │  │ SHIPPED →    │  │ Refund           │
└─────────────┘  └──────────────┘  │ DELIVERED    │  └──────────────────┘
                                   └──────────────┘
┌─────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Inventory   │  │ Recommendation   │  │ Admin Analytics  │
│ Manager     │  │ Engine           │  │                  │
├─────────────┤  ├──────────────────┤  ├──────────────────┤
│ Reserve     │  │ Collaborative    │  │ Sales Summary    │
│ Release     │  │ Filtering        │  │ Category Breakd. │
│ Low Stock   │  │ Popular Products │  │ Conversion Rate  │
│ Thresholds  │  │ Frequently Together│  │ Revenue History  │
└─────────────┘  └──────────────────┘  └──────────────────┘
```

## Features

- **ProductCatalog**: Full CRUD with search, category/tag/price-range filtering, active flag management
- **ShoppingCart**: Thread-safe cart with quantity management, subtotal/total calculation, persistent mode flag
- **OrderStateMachine**: Finite state machine with valid transition validation, audit logging per order
- **PaymentProcessor**: Multi-method payments with idempotency key support, refund capability
- **InventoryManager**: Thread-safe stock reservation/release with low-stock and out-of-stock detection
- **RecommendationEngine**: Collaborative filtering using user behavior similarity, popular products, frequently-bought-together
- **AdminAnalytics**: Order/revenue tracking, category breakdown, day-over-day sales, conversion rate, AOV

## Tech Stack

- Java 21+ (records, sealed classes, pattern matching)
- JUnit 5 for testing
- In-memory concurrent storage (ConcurrentHashMap, CopyOnWriteArrayList)
- BigDecimal for precise monetary calculations

## Usage

```java
var catalog = new ProductCatalog();
catalog.addProduct(new Product("p1", "Laptop", "Gaming laptop",
    new BigDecimal("1299.99"), "Electronics", List.of("tech"), 50, true));

var cart = new ShoppingCart("cart-1", "user-1");
cart.addItem("p1", "Laptop", 1, new BigDecimal("1299.99"));

var osm = new OrderStateMachine();
osm.createOrder("ord-1", "user-1", cart.getItems(), cart.getTotal());
osm.transition("ord-1", OrderState.CONFIRMED, "Payment received");
```

## Testing

Run tests with JUnit 5. Tests cover:
- Product catalog CRUD, search, filtering
- Shopping cart operations and total calculation
- Order state machine transitions and validation
- Payment processing and idempotency
- Inventory reservation and release
- Recommendation collaborative filtering
- Admin analytics aggregation
