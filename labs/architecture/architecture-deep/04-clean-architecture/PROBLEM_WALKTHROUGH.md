# Lab 04: Problem Walkthrough — Use-Case Driven Application with Dependency Rule

## Problem Statement

Implement an order-management application following Clean Architecture. Requirements:

1. **Entities** hold enterprise business rules (order totals, status transitions) and depend on nothing.
2. **Use cases** hold application business rules (create, submit, cancel an order) and depend only on entities and **gateway interfaces**.
3. **Gateways** (output boundaries) are interfaces declared by the use-case layer.
4. **Presenters** (input boundaries) translate domain results into view models.
5. **Controllers and adapters** (interface adapters + frameworks) implement the boundaries — the **dependency rule** ensures source code dependencies point inward only.
6. The composition root wires the whole graph; the use cases must be runnable with zero frameworks.

## Constraints

- Java 21+ only.
- The entity + use case layers must compile with no imports other than `java.*` and other core types.
- Swapping frameworks (console vs web) must require changing only the outer layers.

## Approach

Clean Architecture (Uncle Bob) is concentric rings of dependencies:

```
Entities           <- innermost, no dependencies
Use Cases          <- depend on entities
Interface Adapters <- controllers, presenters, gateways implementations
Frameworks & Drvrs <- outermost: DB, web, console
```

The **Dependency Rule**: source code dependencies always point inward. The use-case layer defines gateway interfaces; the adapter layer implements them. This is the same inversion as hexagonal ports/adapters, expressed as concentric rings.

Flow for "submit order": the controller (adapter) calls `SubmitOrderUseCase` (use case), which reads via `OrderGateway` and writes via `OrderPresenter` (output boundary) — adapters implement those interfaces.

## Step-by-Step Solution

### Step 1: Entities

Entities are pure domain objects with enterprise rules. `Order` encapsulates total calculation and status transitions; it depends on nothing.

```java
enum OrderStatus { DRAFT, SUBMITTED, CANCELLED }

record OrderItem(String productId, String name, int quantity, long unitPrice) {
    long lineTotal() {
        return quantity * unitPrice;
    }
}

class Order {
    private final UUID id;
    private final String customerId;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    Order(UUID id, String customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    void addItem(OrderItem item) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify a " + status + " order");
        }
        items.add(item);
    }

    long total() {
        return items.stream().mapToLong(OrderItem::lineTotal).sum();
    }

    void submit() {
        if (items.isEmpty()) throw new IllegalStateException("Cannot submit an empty order");
        if (status == OrderStatus.CANCELLED) throw new IllegalStateException("Cancelled order cannot be submitted");
        status = OrderStatus.SUBMITTED;
    }

    void cancel() {
        if (status == OrderStatus.SUBMITTED) throw new IllegalStateException("Submitted order cannot be cancelled");
        status = OrderStatus.CANCELLED;
    }

    UUID id() { return id; }
    String customerId() { return customerId; }
    List<OrderItem> items() { return List.copyOf(items); }
    OrderStatus status() { return status; }
}
```

### Step 2: Use-Case Layer — Input and Output Boundaries

The use-case layer declares the **gateway interfaces** (output ports) and an **output boundary** for presentation. These interfaces are owned by the use-case layer, so dependencies point inward.

```java
interface OrderGateway {
    void save(Order order);
    Optional<Order> findById(UUID orderId);
}

interface ProductCatalogGateway {
    Optional<Product> findByProductId(String productId);
}

record Product(String productId, String name, long unitPrice, boolean inStock) {}

interface OrderOutputBoundary {
    void present(SubmitOrderResponse response);
    void presentError(String message);
}

record SubmitOrderResponse(UUID orderId, OrderStatus status, long total) {}
```

### Step 3: Use Cases

Each use case is a class with an `execute` method. It orchestrates entities and gateways — no framework types, no SQL, no HTTP.

```java
class SubmitOrderUseCase {
    private final OrderGateway orders;
    private final ProductCatalogGateway catalog;
    private final OrderOutputBoundary presenter;

    SubmitOrderUseCase(OrderGateway orders, ProductCatalogGateway catalog, OrderOutputBoundary presenter) {
        this.orders = orders;
        this.catalog = catalog;
        this.presenter = presenter;
    }

    void execute(UUID orderId) {
        var order = orders.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        for (var item : order.items()) {
            var product = catalog.findByProductId(item.productId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + item.productId()));
            if (!product.inStock()) {
                presenter.presentError("Product out of stock: " + product.name());
                return;
            }
        }
        order.submit();
        orders.save(order);
        presenter.present(new SubmitOrderResponse(order.id(), order.status(), order.total()));
    }
}

class CancelOrderUseCase {
    private final OrderGateway orders;

    CancelOrderUseCase(OrderGateway orders) {
        this.orders = orders;
    }

    void execute(UUID orderId) {
        var order = orders.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.cancel();
        orders.save(order);
    }
}
```

Note the boundary-crossing rule: `SubmitOrderUseCase` sends results to the presenter instead of returning them, so the use case doesn't know whether it's talking to a console, a web page, or a test spy.

### Step 4: Interface Adapters — Presenter and Controller

The presenter implements the output boundary; the controller adapts input into use-case calls.

```java
class OrderController {
    private final CreateOrderUseCase createOrder;
    private final SubmitOrderUseCase submitOrder;
    private final CancelOrderUseCase cancelOrder;

    OrderController(CreateOrderUseCase createOrder, SubmitOrderUseCase submitOrder, CancelOrderUseCase cancelOrder) {
        this.createOrder = createOrder;
        this.submitOrder = submitOrder;
        this.cancelOrder = cancelOrder;
    }

    UUID createOrderRequest(String customerId) {
        return createOrder.execute(customerId);
    }

    void submitOrderRequest(UUID orderId) {
        submitOrder.execute(orderId);
    }

    void cancelOrderRequest(UUID orderId) {
        cancelOrder.execute(orderId);
    }
}
```

### Step 5: Frameworks — Concrete Gateways

These are the outermost ring: persistence and delivery mechanisms.

```java
class InMemoryOrderGateway implements OrderGateway {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.id(), order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}

class InMemoryProductCatalogGateway implements ProductCatalogGateway {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    InMemoryProductCatalogGateway() {
        products.put("P1", new Product("P1", "Laptop", 1200, true));
        products.put("P2", new Product("P2", "Headphones", 150, false));
    }

    @Override
    public Optional<Product> findByProductId(String productId) {
        return Optional.ofNullable(products.get(productId));
    }
}
```

### Step 6: Composition Root

Wiring: controllers get use cases; use cases get gateways and the presenter. This is the only place concrete types meet.

```java
var orders = new InMemoryOrderGateway();
var catalog = new InMemoryProductCatalogGateway();
var presenter = new ConsoleOrderPresenter();
var createOrder = new CreateOrderUseCase(orders);
var submitOrder = new SubmitOrderUseCase(orders, catalog, presenter);
var cancelOrder = new CancelOrderUseCase(orders);
var controller = new OrderController(createOrder, submitOrder, cancelOrder);
```

The full `CleanArchLab.main` then drives `createOrderRequest`, `submitOrderRequest`, `cancelOrderRequest`, and a second `submitOrderRequest` to hit the error path.

## Complete Solution

The full compilable file, `CleanArchLab.java` in package `com.architecture.deep.lab04`:

```java
package com.architecture.deep.lab04;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CleanArchLab {
    public static void main(String[] args) {
        var orders = new InMemoryOrderGateway();
        var catalog = new InMemoryProductCatalogGateway();
        var presenter = new ConsoleOrderPresenter();

        var createOrder = new CreateOrderUseCase(orders);
        var submitOrder = new SubmitOrderUseCase(orders, catalog, presenter);
        var cancelOrder = new CancelOrderUseCase(orders);

        var controller = new OrderController(createOrder, submitOrder, cancelOrder);

        var orderId = controller.createOrderRequest("customer-42");
        try {
            controller.submitOrderRequest(orderId);
            controller.cancelOrderRequest(orderId);
            controller.submitOrderRequest(orderId);
        } catch (IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}

enum OrderStatus { DRAFT, SUBMITTED, CANCELLED }

record OrderItem(String productId, String name, int quantity, long unitPrice) {
    long lineTotal() {
        return quantity * unitPrice;
    }
}

class Order {
    private final UUID id;
    private final String customerId;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    Order(UUID id, String customerId) {
        this.id = id;
        this.customerId = customerId;
    }

    void addItem(OrderItem item) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify a " + status + " order");
        }
        items.add(item);
    }

    long total() {
        return items.stream().mapToLong(OrderItem::lineTotal).sum();
    }

    void submit() {
        if (items.isEmpty()) throw new IllegalStateException("Cannot submit an empty order");
        if (status == OrderStatus.CANCELLED) throw new IllegalStateException("Cancelled order cannot be submitted");
        status = OrderStatus.SUBMITTED;
    }

    void cancel() {
        if (status == OrderStatus.SUBMITTED) throw new IllegalStateException("Submitted order cannot be cancelled");
        status = OrderStatus.CANCELLED;
    }

    UUID id() { return id; }
    String customerId() { return customerId; }
    List<OrderItem> items() { return List.copyOf(items); }
    OrderStatus status() { return status; }
}

interface OrderGateway {
    void save(Order order);
    Optional<Order> findById(UUID orderId);
}

interface ProductCatalogGateway {
    Optional<Product> findByProductId(String productId);
}

record Product(String productId, String name, long unitPrice, boolean inStock) {}

interface OrderOutputBoundary {
    void present(SubmitOrderResponse response);
    void presentError(String message);
}

record SubmitOrderResponse(UUID orderId, OrderStatus status, long total) {}

class CreateOrderUseCase {
    private final OrderGateway orders;

    CreateOrderUseCase(OrderGateway orders) {
        this.orders = orders;
    }

    UUID execute(String customerId) {
        var order = new Order(UUID.randomUUID(), customerId);
        orders.save(order);
        return order.id();
    }
}

class SubmitOrderUseCase {
    private final OrderGateway orders;
    private final ProductCatalogGateway catalog;
    private final OrderOutputBoundary presenter;

    SubmitOrderUseCase(OrderGateway orders, ProductCatalogGateway catalog, OrderOutputBoundary presenter) {
        this.orders = orders;
        this.catalog = catalog;
        this.presenter = presenter;
    }

    void execute(UUID orderId) {
        var order = orders.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        for (var item : order.items()) {
            var product = catalog.findByProductId(item.productId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown product: " + item.productId()));
            if (!product.inStock()) {
                presenter.presentError("Product out of stock: " + product.name());
                return;
            }
        }
        order.submit();
        orders.save(order);
        presenter.present(new SubmitOrderResponse(order.id(), order.status(), order.total()));
    }
}

class CancelOrderUseCase {
    private final OrderGateway orders;

    CancelOrderUseCase(OrderGateway orders) {
        this.orders = orders;
    }

    void execute(UUID orderId) {
        var order = orders.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.cancel();
        orders.save(order);
    }
}

class ConsoleOrderPresenter implements OrderOutputBoundary {
    @Override
    public void present(SubmitOrderResponse response) {
        System.out.println("Order " + response.orderId() + " submitted, status="
            + response.status() + ", total=" + response.total());
    }

    @Override
    public void presentError(String message) {
        System.err.println("ERROR: " + message);
    }
}

class OrderController {
    private final CreateOrderUseCase createOrder;
    private final SubmitOrderUseCase submitOrder;
    private final CancelOrderUseCase cancelOrder;

    OrderController(CreateOrderUseCase createOrder, SubmitOrderUseCase submitOrder, CancelOrderUseCase cancelOrder) {
        this.createOrder = createOrder;
        this.submitOrder = submitOrder;
        this.cancelOrder = cancelOrder;
    }

    UUID createOrderRequest(String customerId) {
        return createOrder.execute(customerId);
    }

    void submitOrderRequest(UUID orderId) {
        submitOrder.execute(orderId);
    }

    void cancelOrderRequest(UUID orderId) {
        cancelOrder.execute(orderId);
    }
}

class InMemoryOrderGateway implements OrderGateway {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.id(), order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}

class InMemoryProductCatalogGateway implements ProductCatalogGateway {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    InMemoryProductCatalogGateway() {
        products.put("P1", new Product("P1", "Laptop", 1200, true));
        products.put("P2", new Product("P2", "Headphones", 150, false));
    }

    @Override
    public Optional<Product> findByProductId(String productId) {
        return Optional.ofNullable(products.get(productId));
    }
}
```

## Complexity Analysis

- **Create order**: O(1) — single gateway write. **Submit order**: O(I + P) where I = items, P = product lookups per item (each O(1) in-memory); presentation O(1).
- **Space**: O(N) for persisted orders/products.
- **Layering cost**: every boundary adds an interface + adapter; the payoff is that the use-case layer is tested in milliseconds without a database, and frameworks can be swapped without touching business rules.

## Test Cases

| Scenario | Expected |
|---|---|
| Create then submit order with in-stock item | Presented `SUBMITTED` with correct total |
| Submit empty order | `IllegalStateException("Cannot submit an empty order")` |
| Submit with out-of-stock product | `presentError` called; order stays `DRAFT` |
| Cancel draft order | Status `CANCELLED` |
| Cancel submitted order | `IllegalStateException("Submitted order cannot be cancelled")` |
| Submit cancelled order | `IllegalStateException("Cancelled order cannot be submitted")` |

Example run (order contains no items since demo controller never adds items — hence the error path is exercised):

```
ERROR: Cannot submit an empty order
```

## Follow-Up Questions
1. **Clean vs hexagonal — when do you choose which?** They are nearly interchangeable; choose hexagonal when adapters/sides map naturally to your infrastructure, Clean when you want the explicit ring vocabulary to communicate with stakeholders.
2. **Do use cases return DTOs or use presenters?** Both are valid; presenters (output boundaries) shine with multiple UIs (REST + UI + tests), return values are fine for a single consumer.
3. **Where do transactions go?** Around the use case — a decorator pattern keeps the use case focused on rules.
4. **How do you avoid the 'use case explosion'?** Group related commands into one use case class (e.g., `ManageOrderUseCase` with methods) when they share collaborators and steps.
5. **Does the entity layer ever use frameworks?** Never. If an entity needs JSON or JPA, the mapping happens in the adapter layer.
6. **How is the dependency rule verified?** ArchUnit `ArchRule`s (e.g., `classes in package ..usecase.. may only depend on classes in ..entity.. and java.*`).
7. **What breaks the rule most often in practice?** Annotations on entities (JPA), static utility calls to infrastructure (Log4j is usually fine; `ConnectionManager` is not), and use cases returning web-specific types.
