# Vert.x Learning Projects

This directory contains projects focusing on Vert.x, the reactive, polyglot toolkit for building distributed applications. These projects help you master event-driven architecture and non-blocking I/O with Vert.x.

## Mini-Project: Vert.x REST API with Event Bus (2-4 hours)

### Overview

Build a reactive REST API using Vert.x that demonstrates event bus communication, verticle deployment, and asynchronous message handling. This project showcases Vert.x's powerful event-driven programming model.

### Project Structure

```
vertx-rest-api/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── vertx/
        │           └── learning/
        │               ├── MainVerticle.java
        │               ├── api/
        │               │   ├── ProductApiVerticle.java
        │               │   └── Product.java
        │               ├── service/
        │               │   ├── ProductService.java
        │               │   └── ProductRepository.java
        │               └── config/
        │                   └── AppConfig.java
        └── resources/
            └── application.conf
```

### Implementation

```java
package com.vertx.learning;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
    
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        logger.info("Starting Main Verticle");
        
        vertx.deployVerticle(new ProductApiVerticle())
            .onSuccess(id -> {
                logger.info("ProductApiVerticle deployed successfully with ID: " + id);
                startPromise.complete();
            })
            .onFailure(err -> {
                logger.error("Failed to deploy ProductApiVerticle", err);
                startPromise.fail(err);
            });
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        logger.info("Stopping Main Verticle");
        stopPromise.complete();
    }
}
```

```java
package com.vertx.learning.api;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProductApiVerticle extends AbstractVerticle {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductApiVerticle.class);
    private static final String EVENT_BUS_ADDRESS = "product.service";
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private long nextId = 1;

    @Override
    public void start(Promise<Void> startPromise) {
        initializeSampleData();
        
        Router router = Router.router(vertx);
        
        router.route().handler(BodyHandler.create());
        router.route("/api/*").handler(rc -> {
            rc.response().putHeader("Content-Type", "application/json");
            rc.next();
        });
        
        router.get("/api/products").handler(this::getAllProducts);
        router.get("/api/products/:id").handler(this::getProductById);
        router.post("/api/products").handler(this::createProduct);
        router.put("/api/products/:id").handler(this::updateProduct);
        router.delete("/api/products/:id").handler(this::deleteProduct);
        
        router.get("/health").handler(rc -> rc.response()
            .end(JsonObject.mapWriter().write(new JsonObject().put("status", "UP"))));
        
        int port = config().getInteger("http.port", 8080);
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port, result -> {
                if (result.succeeded()) {
                    logger.info("HTTP server started on port " + port);
                    startPromise.complete();
                } else {
                    logger.error("Failed to start HTTP server", result.cause());
                    startPromise.fail(result.cause());
                }
            });
    }

    private void initializeSampleData() {
        addProduct(new Product("Laptop", "High-performance laptop", 999.99, "Electronics", 50));
        addProduct(new Product("Smartphone", "Latest model smartphone", 699.99, "Electronics", 100));
        addProduct(new Product("Coffee Maker", "Automatic coffee maker", 89.99, "Appliances", 30));
    }

    private Product addProduct(Product product) {
        product.setId(nextId++);
        products.put(product.getId(), product);
        return product;
    }

    private void getAllProducts(RoutingContext rc) {
        List<Product> productList = new ArrayList<>(products.values());
        rc.response()
            .setStatusCode(200)
            .end(Json.encode(productList));
    }

    private void getProductById(RoutingContext rc) {
        String idParam = rc.pathParam("id");
        Long id = Long.parseLong(idParam);
        
        Product product = products.get(id);
        if (product != null) {
            rc.response()
                .setStatusCode(200)
                .end(Json.encode(product));
        } else {
            rc.response()
                .setStatusCode(404)
                .end(Json.encode(new JsonObject().put("error", "Product not found")));
        }
    }

    private void createProduct(RoutingContext rc) {
        JsonObject body = rc.getBodyAsJson();
        
        if (body == null || !body.containsKey("name") || !body.containsKey("price")) {
            rc.response()
                .setStatusCode(400)
                .end(Json.encode(new JsonObject().put("error", "Invalid request")));
            return;
        }
        
        Product product = new Product(
            body.getString("name"),
            body.getString("description"),
            body.getDouble("price"),
            body.getString("category"),
            body.getInteger("stock", 0)
        );
        
        product = addProduct(product);
        
        vertx.eventBus().send(EVENT_BUS_ADDRESS, 
            JsonObject.mapWriter().write(new JsonObject()
                .put("action", "create")
                .put("product", Json.encode(product))));
        
        logger.info("Product created: " + product.getId());
        
        rc.response()
            .setStatusCode(201)
            .end(Json.encode(product));
    }

    private void updateProduct(RoutingContext rc) {
        String idParam = rc.pathParam("id");
        Long id = Long.parseLong(idParam);
        JsonObject body = rc.getBodyAsJson();
        
        Product existing = products.get(id);
        if (existing == null) {
            rc.response()
                .setStatusCode(404)
                .end(Json.encode(new JsonObject().put("error", "Product not found")));
            return;
        }
        
        if (body.containsKey("name")) existing.setName(body.getString("name"));
        if (body.containsKey("description")) existing.setDescription(body.getString("description"));
        if (body.containsKey("price")) existing.setPrice(body.getDouble("price"));
        if (body.containsKey("category")) existing.setCategory(body.getString("category"));
        if (body.containsKey("stock")) existing.setStock(body.getInteger("stock"));
        
        vertx.eventBus().send(EVENT_BUS_ADDRESS, 
            JsonObject.mapWriter().write(new JsonObject()
                .put("action", "update")
                .put("product", Json.encode(existing))));
        
        rc.response()
            .setStatusCode(200)
            .end(Json.encode(existing));
    }

    private void deleteProduct(RoutingContext rc) {
        String idParam = rc.pathParam("id");
        Long id = Long.parseLong(idParam);
        
        Product removed = products.remove(id);
        if (removed != null) {
            vertx.eventBus().send(EVENT_BUS_ADDRESS, 
                JsonObject.mapWriter().write(new JsonObject()
                    .put("action", "delete")
                    .put("productId", id)));
            
            rc.response()
                .setStatusCode(200)
                .end(Json.encode(new JsonObject().put("message", "Product deleted")));
        } else {
            rc.response()
                .setStatusCode(404)
                .end(Json.encode(new JsonObject().put("error", "Product not found")));
        }
    }
}
```

```java
package com.vertx.learning.api;

import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.json.JsonEncoder;

public class Product implements JsonEncoder {
    
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int stock;

    public Product() {
    }

    public Product(String name, String description, double price, String category, int stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public void encodeToJson(JsonObject json) {
        if (id != null) json.put("id", id);
        json.put("name", name);
        json.put("description", description);
        json.put("price", price);
        json.put("category", category);
        json.put("stock", stock);
    }
}
```

### application.conf

```conf
# Vert.x Configuration
http.port = 8080

# Database Configuration (for reference)
database.host = localhost
database.port = 5432
database.name = products_db

# Event Bus Configuration
eventbus.address = product.service
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.vertx.learning</groupId>
    <artifactId>vertx-rest-api</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <vertx.version>4.5.1</vertx.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-core</artifactId>
            <version>${vertx.version}</version>
        </dependency>
        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-web</artifactId>
            <version>${vertx.version}</version>
        </dependency>
        <dependency>
            <groupId>io.vertx</groupId>
            <artifactId>vertx-web-client</artifactId>
            <version>${vertx.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>io.reactiverse</groupId>
                <artifactId>vertx-maven-plugin</artifactId>
                <version>2.1.0</version>
                <executions>
                    <execution>
                        <id>run</id>
                        <goals>
                            <goal>start</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <vertxArgs>conf application.conf</vertxArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Build and Run

```bash
cd vertx-rest-api
mvn clean compile
mvn vertx:run
```

### Test Endpoints

```bash
# Get all products
curl http://localhost:8080/api/products

# Create product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Test","price":99.99,"category":"Test","stock":10}'

# Get product by ID
curl http://localhost:8080/api/products/1

# Health check
curl http://localhost:8080/health
```

---

## Real-World Project: Event-Driven Order Processing System (8+ hours)

### Overview

Build a comprehensive event-driven order processing system using Vert.x that demonstrates distributed messaging, cluster management, database integration, circuit breaker pattern, and reactive streams. This project simulates a real-world e-commerce platform architecture.

### Architecture

```
vertx-order-system/
├── order-service/           # Main order processing
├── inventory-service/       # Inventory management
├── notification-service/    # Email/SMS notifications
├── api-gateway/             # HTTP Gateway
└── common/                 # Shared modules
```

### Implementation

```java
package com.vertx.order;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

import com.vertx.order.service.OrderService;
import com.vertx.order.service.OrderRepository;
import com.vertx.order.messaging.EventBusService;
import com.vertx.order.messaging.OrderEventHandler;

public class OrderServiceVerticle extends AbstractVerticle {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceVerticle.class);
    
    private OrderService orderService;
    private EventBusService eventBusService;

    @Override
    public void start(Promise<Void> startPromise) {
        logger.info("Starting Order Service Verticle");
        
        try {
            initializeServices();
            registerEventHandlers();
            startHttpServer();
            
            logger.info("Order Service started successfully");
            startPromise.complete();
        } catch (Exception e) {
            logger.error("Failed to start Order Service", e);
            startPromise.fail(e);
        }
    }

    private void initializeServices() {
        OrderRepository repository = new OrderRepository();
        orderService = new OrderService(repository);
        
        eventBusService = new EventBusService(vertx);
        
        new ServiceBinder(vertx)
            .setAddress("order.service")
            .register(OrderService.class, orderService);
        
        logger.info("Services initialized");
    }

    private void registerEventHandlers() {
        vertx.eventBus().consumer("order.created", message -> {
            OrderEventHandler.handleOrderCreated(message, orderService, eventBusService);
        });
        
        vertx.eventBus().consumer("order.updated", message -> {
            OrderEventHandler.handleOrderUpdated(message, orderService);
        });
        
        vertx.eventBus().consumer("order.cancelled", message -> {
            OrderEventHandler.handleOrderCancelled(message, orderService, eventBusService);
        });
        
        vertx.eventBus().consumer("inventory.check", message -> {
            OrderEventHandler.handleInventoryCheck(message);
        });
        
        logger.info("Event handlers registered");
    }

    private void startHttpServer() {
        Router router = Router.router(vertx);
        
        router.route().handler(BodyHandler.create());
        
        router.get("/api/orders").handler(this::getAllOrders);
        router.get("/api/orders/:id").handler(this::getOrderById);
        router.post("/api/orders").handler(this::createOrder);
        router.put("/api/orders/:id/status").handler(this::updateOrderStatus);
        router.delete("/api/orders/:id").handler(this::cancelOrder);
        
        router.get("/health").handler(rc -> 
            rc.response().endJson(new io.vertx.core.json.JsonObject()
                .put("status", "UP")
                .put("service", "order-service")));
        
        int port = config().getInteger("http.port", 8081);
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port, result -> {
                if (result.succeeded()) {
                    logger.info("Order Service HTTP server listening on port " + port);
                } else {
                    logger.error("Failed to start HTTP server", result.cause());
                }
            });
    }

    private void getAllOrders(RoutingContext rc) {
        orderService.getAllOrders()
            .onSuccess(orders -> 
                rc.response().setStatusCode(200).endJson(orders))
            .onFailure(err -> 
                rc.response().setStatusCode(500)
                    .endJson(new io.vertx.core.json.JsonObject()
                        .put("error", err.getMessage())));
    }

    private void getOrderById(RoutingContext rc) {
        String id = rc.pathParam("id");
        
        orderService.getOrderById(Long.parseLong(id))
            .onSuccess(order -> {
                if (order != null) {
                    rc.response().setStatusCode(200).endJson(order);
                } else {
                    rc.response().setStatusCode(404)
                        .endJson(new io.vertx.core.json.JsonObject()
                            .put("error", "Order not found"));
                }
            })
            .onFailure(err -> 
                rc.response().setStatusCode(500)
                    .endJson(new io.vertx.core.json.JsonObject()
                        .put("error", err.getMessage())));
    }

    private void createOrder(RoutingContext rc) {
        JsonObject body = rc.getBodyAsJson();
        
        if (body == null) {
            rc.response().setStatusCode(400)
                .endJson(new io.vertx.core.json.JsonObject()
                    .put("error", "Invalid request body"));
            return;
        }
        
        createOrderFromJson(body)
            .onSuccess(order -> {
                eventBusService.publish("order.created", order);
                rc.response().setStatusCode(201).endJson(order);
            })
            .onFailure(err -> 
                rc.response().setStatusCode(400)
                    .endJson(new io.vertx.core.json.JsonObject()
                        .put("error", err.getMessage())));
    }

    private Future<Order> createOrderFromJson(JsonObject body) {
        Promise<Order> promise = Promise.promise();
        
        try {
            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setCustomerId(body.getLong("customerId"));
            order.setCustomerEmail(body.getString("customerEmail"));
            order.setStatus(OrderStatus.PENDING);
            
            JsonArray items = body.getJsonArray("items");
            if (items != null) {
                for (Object item : items) {
                    JsonObject itemJson = (JsonObject) item;
                    OrderItem orderItem = new OrderItem(
                        itemJson.getLong("productId"),
                        itemJson.getString("productName"),
                        itemJson.getInteger("quantity"),
                        itemJson.getDouble("price")
                    );
                    order.addItem(orderItem);
                }
            }
            
            order.calculateTotal();
            orderService.createOrder(order, result -> {
                if (result.succeeded()) {
                    promise.complete(result.result());
                } else {
                    promise.fail(result.cause());
                }
            });
        } catch (Exception e) {
            promise.fail(e);
        }
        
        return promise.future();
    }

    private void updateOrderStatus(RoutingContext rc) {
        String id = rc.pathParam("id");
        JsonObject body = rc.getBodyAsJson();
        
        String status = body.getString("status");
        
        orderService.updateOrderStatus(Long.parseLong(id), OrderStatus.valueOf(status.toUpperCase()))
            .onSuccess(order -> {
                eventBusService.publish("order.updated", order);
                rc.response().setStatusCode(200).endJson(order);
            })
            .onFailure(err -> 
                rc.response().setStatusCode(400)
                    .endJson(new io.vertx.core.json.JsonObject()
                        .put("error", err.getMessage())));
    }

    private void cancelOrder(RoutingContext rc) {
        String id = rc.pathParam("id");
        
        orderService.cancelOrder(Long.parseLong(id))
            .onSuccess(order -> {
                eventBusService.publish("order.cancelled", order);
                rc.response().setStatusCode(200)
                    .endJson(new io.vertx.core.json.JsonObject()
                        .put("message", "Order cancelled successfully"));
            })
            .onFailure(err -> 
                rc.response().setStatusCode(400)
                    .endJson(new io.vertx.core.json.JsonObject()
                        .put("error", err.getMessage())));
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + 
            (int)(Math.random() * 1000);
    }
}
```

```java
package com.vertx.order.service;

import io.vertx.core.json.JsonObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {
    
    private final OrderRepository repository;
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void createOrder(Order order, Handler<AsyncResult<Order>> resultHandler) {
        checkInventory(order)
            .onSuccess(available -> {
                if (available) {
                    repository.save(order);
                    resultHandler.handle(Future.succeededFuture(order));
                } else {
                    resultHandler.handle(Future.failedFuture("Insufficient inventory"));
                }
            })
            .onFailure(resultHandler::handle);
    }

    public Future<Boolean> checkInventory(Order order) {
        Promise<Boolean> promise = Promise.promise();
        
        CircuitBreaker breaker = getCircuitBreaker("inventory");
        
        breaker.executeWithFallback(promise -> {
            JsonObject request = new JsonObject()
                .put("items", JsonObject.mapFrom(order.getItems()));
            
            vertx.eventBus().<JsonObject>request("inventory.check", request, reply -> {
                if (reply.succeeded()) {
                    promise.complete(reply.result().body().getBoolean("available"));
                } else {
                    promise.fail(reply.cause());
                }
            });
        }, throwable -> {
            return Future.succeededFuture(true);
        });
        
        return promise.future();
    }

    private CircuitBreaker getCircuitBreaker(String name) {
        return circuitBreakers.computeIfAbsent(name, n -> 
            CircuitBreaker.create(n, vertx)
                .setMaxRetries(3)
                .setMaxFailures(5)
                .setTimeout(5000)
                .setResetTimeout(30000)
        );
    }

    public void getAllOrders(Handler<AsyncResult<List<Order>>> resultHandler) {
        repository.findAll(resultHandler);
    }

    public void getOrderById(Long id, Handler<AsyncResult<Order>> resultHandler) {
        repository.findById(id, resultHandler);
    }

    public void getOrdersByCustomer(Long customerId, Handler<AsyncResult<List<Order>>> resultHandler) {
        repository.findByCustomerId(customerId, resultHandler);
    }

    public Future<Order> updateOrderStatus(Long id, OrderStatus status) {
        Promise<Order> promise = Promise.promise();
        
        repository.findById(id, result -> {
            if (result.succeeded() && result.result() != null) {
                Order order = result.result();
                order.setStatus(status);
                repository.update(order, updateResult -> {
                    if (updateResult.succeeded()) {
                        promise.complete(order);
                    } else {
                        promise.fail(updateResult.cause());
                    }
                });
            } else {
                promise.fail(new RuntimeException("Order not found"));
            }
        });
        
        return promise.future();
    }

    public Future<Order> cancelOrder(Long id) {
        return updateOrderStatus(id, OrderStatus.CANCELLED);
    }
}
```

```java
package com.vertx.order.service;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderRepository {
    
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public void save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
    }

    public void update(Order order) {
        if (order.getId() != null) {
            orders.put(order.getId(), order);
        }
    }

    public void findAll(Handler<AsyncResult<List<Order>>> handler) {
        handler.handle(Future.succeededFuture(new ArrayList<>(orders.values())));
    }

    public void findById(Long id, Handler<AsyncResult<Order>> handler) {
        Order order = orders.get(id);
        if (order != null) {
            handler.handle(Future.succeededFuture(order));
        } else {
            handler.handle(Future.succeededFuture(null));
        }
    }

    public void findByCustomerId(Long customerId, Handler<AsyncResult<List<Order>>> handler) {
        List<Order> customerOrders = orders.values().stream()
            .filter(o -> o.getCustomerId().equals(customerId))
            .toList();
        handler.handle(Future.succeededFuture(customerOrders));
    }
}
```

```java
package com.vertx.order.messaging;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class EventBusService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventBusService.class);
    private final Vertx vertx;

    public EventBusService(Vertx vertx) {
        this.vertx = vertx;
    }

    public void publish(String address, Object message) {
        JsonObject event = new JsonObject()
            .put("address", address)
            .put("timestamp", Instant.now().toString())
            .put("payload", message instanceof JsonObject 
                ? (JsonObject) message 
                : JsonObject.mapFrom(message));
        
        vertx.eventBus().publish(address, event);
        logger.info("Event published to " + address);
    }

    public <T> Future<T> request(String address, Object message, Class<T> responseType) {
        Promise<T> promise = Promise.promise();
        
        JsonObject event = new JsonObject()
            .put("address", address)
            .put("timestamp", Instant.now().toString())
            .put("payload", message instanceof JsonObject 
                ? (JsonObject) message 
                : JsonObject.mapFrom(message));
        
        vertx.eventBus().<JsonObject>request(address, event, reply -> {
            if (reply.succeeded()) {
                promise.complete((T) reply.result().body());
            } else {
                promise.fail(reply.cause());
            }
        });
        
        return promise.future();
    }

    public void registerHandler(String address, Handler<Message<JsonObject>> handler) {
        vertx.eventBus().consumer(address, handler);
        logger.info("Handler registered for " + address);
    }

    public void send(String address, Object message) {
        JsonObject event = JsonObject.mapFrom(message);
        vertx.eventBus().send(address, event);
    }
}
```

```java
package com.vertx.order.messaging;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

public class OrderEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventHandler.class);

    public static void handleOrderCreated(Message<JsonObject> message, 
            OrderService orderService, EventBusService eventBusService) {
        JsonObject event = message.body();
        JsonObject orderJson = event.getJsonObject("payload");
        
        logger.info("Processing order created event: " + orderJson.getString("orderNumber"));
        
        eventBusService.send("notification.send", new JsonObject()
            .put("type", "ORDER_CONFIRMATION")
            .put("order", orderJson));
        
        eventBusService.send("inventory.reserve", orderJson);
    }

    public static void handleOrderUpdated(Message<JsonObject> message, 
            OrderService orderService) {
        JsonObject event = message.body();
        JsonObject orderJson = event.getJsonObject("payload");
        
        logger.info("Processing order updated event: " + orderJson.getString("orderNumber"));
        
        vertx.eventBus().send("notification.send", new JsonObject()
            .put("type", "ORDER_STATUS_UPDATE")
            .put("order", orderJson));
    }

    public static void handleOrderCancelled(Message<JsonObject> message, 
            OrderService orderService, EventBusService eventBusService) {
        JsonObject event = message.body();
        JsonObject orderJson = event.getJsonObject("payload");
        
        logger.info("Processing order cancelled event: " + orderJson.getString("orderNumber"));
        
        eventBusService.send("inventory.release", orderJson);
        
        vertx.eventBus().send("notification.send", new JsonObject()
            .put("type", "ORDER_CANCELLED")
            .put("order", orderJson));
    }

    public static void handleInventoryCheck(Message<JsonObject> message) {
        JsonObject event = message.body();
        JsonObject items = event.getJsonObject("payload").getJsonObject("items");
        
        boolean available = checkInventoryAvailability(items);
        
        message.reply(new JsonObject()
            .put("available", available));
    }

    private static boolean checkInventoryAvailability(JsonObject items) {
        return true;
    }
}
```

```java
package com.vertx.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItem> items = new ArrayList<>();
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void calculateTotal() {
        totalAmount = items.stream()
            .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

enum PaymentMethod {
    CREDIT_CARD, PAYPAL, BANK_TRANSFER
}

enum PaymentStatus {
    PENDING, AUTHORIZED, CAPTURED, FAILED
}
```

```java
package com.vertx.order.entity;

import java.math.BigDecimal;

public class OrderItem {
    
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    public OrderItem() {
    }

    public OrderItem(Long productId, String productName, Integer quantity, Double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = BigDecimal.valueOf(price);
    }

    public BigDecimal getSubtotal() {
        return price.multiply(new BigDecimal(quantity));
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
```

### Build and Run

```bash
cd vertx-order-system
mvn clean package
java -jar target/vertx-order-system-1.0.0.jar
```

### Test Order Creation

```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "customerEmail": "customer@example.com",
    "items": [
      {"productId": 1, "productName": "Laptop", "quantity": 1, "price": 999.99},
      {"productId": 2, "productName": "Mouse", "quantity": 2, "price": 29.99}
    ]
  }'
```

---

## Additional Learning Resources

- Vert.x Documentation: https://vertx.io/docs/
- Vert.x Core: https://vertx.io/docs/vertx-core/java/
- Vert.x Web: https://vertx.io/docs/vertx-web/java/
- Vert.x Circuit Breaker: https://vertx.io/docs/vertx-circuit-breaker/java/