# Apache Camel Projects - Module 16

This module covers Apache Camel integration framework, enterprise integration patterns, route definitions, and building integration solutions.

## Mini-Project: Simple Integration Routes (2-4 hours)

### Overview
Create basic Apache Camel integration routes for file processing, HTTP endpoints, and message transformations.

### Project Structure
```
camel-demo/
├── pom.xml
├── src/main/java/com/learning/camel/
│   ├── CamelDemoApplication.java
│   ├── router/
│   │   ├── FileRouter.java
│   │   ├── HttpRouter.java
│   │   └── TransformRouter.java
│   ├── processor/
│   │   ├── LoggingProcessor.java
│   │   └── TransformationProcessor.java
│   └── service/
│       └── DataService.java
└── src/main/resources/
    └── application.properties
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>camel-demo</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <camel.version>4.2.0</camel.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-spring-boot-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-stream-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-file-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-http-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-jackson-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-log-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### Main Application
```java
package com.learning.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CamelDemoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CamelDemoApplication.class, args);
    }
}
```

#### File Router
```java
package com.learning.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("file:input?noop=true")
            .routeId("file-input-route")
            .log("Processing file: ${file:name}")
            .to("log:INFO?logName=fileLogger")
            .to("file:output");
        
        from("file:input/orders")
            .routeId("orders-processing")
            .log("Processing order file: ${file:name}")
            .to("direct:processOrders");
        
        from("direct:processOrders")
            .split(body().tokenize("\n"))
            .log("Processing line: ${body}")
            .to("direct:validateOrder");
        
        from("direct:validateOrder")
            .choice()
                .when(body().contains("ERROR"))
                    .log("Invalid order: ${body}")
                    .to("file:output/errors")
                .otherwise()
                    .log("Valid order: ${body}")
                    .to("file:output/valid");
    }
}
```

#### HTTP Router
```java
package com.learning.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HttpRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        rest("/api")
            .get("/users")
                .to("direct:getUsers")
            .post("/users")
                .to("direct:createUser")
            .get("/users/{id}")
                .to("direct:getUserById")
            .put("/users/{id}")
                .to("direct:updateUser")
            .delete("/users/{id}")
                .to("direct:deleteUser");
        
        from("direct:getUsers")
            .log("Fetching all users")
            .to("mock:users");
        
        from("direct:createUser")
            .log("Creating user: ${body}")
            .to("mock:userCreated");
        
        from("direct:getUserById")
            .log("Fetching user by ID: ${header.id}")
            .to("mock:user");
        
        from("direct:updateUser")
            .log("Updating user ${header.id}: ${body}")
            .to("mock:userUpdated");
        
        from("direct:deleteUser")
            .log("Deleting user: ${header.id}")
            .to("mock:userDeleted");
        
        from("timer:ping?period=5000")
            .setBody(constant("Hello from Camel timer"))
            .log("${body}");
    }
}
```

#### Transformation Router
```java
package com.learning.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class TransformRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("direct:jsonInput")
            .routeId("json-transform")
            .log("Input JSON: ${body}")
            .unmarshal().json()
            .log("Unmarshaled: ${body}")
            .process(exchange -> {
                var body = exchange.getIn().getBody(Map.class);
                body.put("processed", true);
                body.put("timestamp", System.currentTimeMillis());
                exchange.getIn().setBody(body);
            })
            .marshal().json()
            .log("Output JSON: ${body}")
            .to("direct:jsonOutput");
        
        from("direct:csvInput")
            .routeId("csv-transform")
            .log("Input CSV: ${body}")
            .convertBodyTo(String.class)
            .split(body().tokenize("\n"))
            .process(exchange -> {
                String line = exchange.getIn().getBody(String.class);
                String[] fields = line.split(",");
                String json = String.format(
                    "{\"name\":\"%s\",\"age\":\"%s\",\"city\":\"%s\"}",
                    fields[0], fields[1], fields[2]
                );
                exchange.getIn().setBody(json);
            })
            .to("direct:jsonOutput");
        
        from("direct:xmlInput")
            .routeId("xml-transform")
            .log("Input XML: ${body}")
            .to("xslt:transform.xsl")
            .log("Transformed XML: ${body}")
            .to("direct:jsonOutput");
        
        from("direct:aggregateInput")
            .routeId("aggregation")
            .aggregate(header("correlationId"), new org.apache.camel.processor.aggregate.UseOriginalAggregationStrategy())
                .completionSize(3)
                .completionTimeout(5000)
                .log("Aggregated: ${body}")
                .to("mock:aggregated");
    }
}
```

#### Logging Processor
```java
package com.learning.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingProcessor implements Processor {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingProcessor.class);
    
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        String headers = exchange.getIn().getHeaders().toString();
        
        logger.info("Processing message: {}", body);
        logger.debug("Headers: {}", headers);
        
        exchange.getIn().setHeader("processedBy", "LoggingProcessor");
        exchange.getIn().setHeader("processedAt", System.currentTimeMillis());
    }
}
```

#### Transformation Processor
```java
package com.learning.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class TransformationProcessor implements Processor {
    
    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody();
        
        if (body instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) body;
            
            Map<String, Object> transformed = new HashMap<>();
            transformed.put("id", data.get("id"));
            transformed.put("name", data.get("name"));
            transformed.put("status", "PROCESSED");
            transformed.put("processedAt", System.currentTimeMillis());
            
            exchange.getIn().setBody(transformed);
        }
        
        if (body instanceof String && body.toString().contains(",")) {
            String csv = body.toString();
            String[] parts = csv.split(",");
            
            Map<String, Object> data = new HashMap<>();
            data.put("field1", parts[0].trim());
            data.put("field2", parts[1].trim());
            data.put("field3", parts[2].trim());
            
            exchange.getIn().setBody(data);
        }
    }
}
```

#### Data Service
```java
package com.learning.camel.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataService {
    
    private final Map<String, Map<String, Object>> users = new ConcurrentHashMap<>();
    
    public DataService() {
        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", "1");
        user1.put("name", "John Doe");
        user1.put("email", "john@example.com");
        users.put("1", user1);
        
        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", "2");
        user2.put("name", "Jane Smith");
        user2.put("email", "jane@example.com");
        users.put("2", user2);
    }
    
    public List<Map<String, Object>> getAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    public Map<String, Object> getUserById(String id) {
        return users.get(id);
    }
    
    public Map<String, Object> createUser(Map<String, Object> user) {
        String id = String.valueOf(users.size() + 1);
        user.put("id", id);
        users.put(id, user);
        return user;
    }
    
    public Map<String, Object> updateUser(String id, Map<String, Object> user) {
        if (users.containsKey(id)) {
            user.put("id", id);
            users.put(id, user);
            return user;
        }
        return null;
    }
    
    public void deleteUser(String id) {
        users.remove(id);
    }
}
```

### Build and Run Instructions
```bash
# Build the project
cd camel-demo
mvn clean package

# Run the application
java -jar target/camel-demo-1.0.0.jar

# Test the application
curl http://localhost:8080/api/users

# Create a test file in input folder and watch processing
echo "1,John,25,NYC" > input/orders/test.csv
```

---

## Real-World Project: Enterprise Integration Platform (8+ hours)

### Overview
Build a complete enterprise integration platform using Apache Camel with multiple systems integration, complex routing, error handling, and monitoring.

### Project Structure
```
enterprise-integration/
├── pom.xml
├── src/main/java/com/learning/integration/
│   ├── EnterpriseIntegrationApplication.java
│   ├── router/
│   │   ├── OrderRouter.java
│   │   ├── PaymentRouter.java
│   │   ├── InventoryRouter.java
│   │   └── NotificationRouter.java
│   ├── service/
│   │   ├── OrderService.java
│   │   ├── PaymentService.java
│   │   ├── InventoryService.java
│   │   └── NotificationService.java
│   ├── config/
│   │   ├── CamelConfig.java
│   │   ├── JmsConfig.java
│   │   └── DatabaseConfig.java
│   ├── error/
│   │   ├── ErrorHandler.java
│   │   └── RetryProcessor.java
│   └── model/
│       ├── Order.java
│       ├── Payment.java
│       └── Inventory.java
├── src/main/resources/
│   └── application.yml
└── docker-compose.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>enterprise-integration</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <camel.version>4.2.0</camel.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-spring-boot-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-jms-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-sql-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-activemq-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-kafka-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-rest-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-swagger-java-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-micrometer-starter</artifactId>
            <version>${camel.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### Order Model
```java
package com.learning.integration.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    
    private String orderId;
    private String customerId;
    private String customerEmail;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String shippingAddress;
    private String paymentMethod;
    
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}

class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

#### Payment Model
```java
package com.learning.integration.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    
    private String paymentId;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime processedAt;
    private String failureReason;
    
    public Payment() {
        this.processedAt = LocalDateTime.now();
    }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
```

#### Order Router
```java
package com.learning.integration.router;

import com.learning.integration.error.ErrorHandler;
import com.learning.integration.model.Order;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class OrderRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json);
        
        rest("/orders")
            .post()
                .to("direct:createOrder")
            .get("/{orderId}")
                .to("direct:getOrder")
            .get()
                .to("direct:getAllOrders")
            .put("/{orderId}/cancel")
                .to("direct:cancelOrder");
        
        from("direct:createOrder")
            .routeId("create-order")
            .log("Creating order: ${body}")
            .to("validataion:bean")
            .to("direct:validateOrder")
            .to("direct:processPayment")
            .to("direct:reserveInventory")
            .to("direct:notifyCustomer")
            .to("direct:saveOrder")
            .setHeader("CamelHttpResponseCode", constant(201))
            .setBody(simple("{\"status\":\"Order created successfully\"}"));
        
        from("direct:getOrder")
            .routeId("get-order")
            .log("Getting order: ${header.orderId}")
            .to("sql:SELECT * FROM orders WHERE order_id = :#${header.orderId}?dataSource=#dataSource")
            .convertBodyTo(String.class);
        
        from("direct:getAllOrders")
            .routeId("get-all-orders")
            .to("sql:SELECT * FROM orders?dataSource=#dataSource")
            .convertBodyTo(String.class);
        
        from("direct:cancelOrder")
            .routeId("cancel-order")
            .log("Cancelling order: ${header.orderId}")
            .to("direct:refundPayment")
            .to("direct:releaseInventory")
            .to("sql:UPDATE orders SET status = 'CANCELLED' WHERE order_id = :#${header.orderId}?dataSource=#dataSource")
            .setBody(simple("{\"status\":\"Order cancelled\"}"));
        
        from("direct:validateOrder")
            .routeId("validate-order")
            .choice()
                .when(body().isNull())
                    .log("Order body is null")
                    .throwException(new IllegalArgumentException("Order cannot be null"))
                .when(simple("${body.items.size} == 0"))
                    .log("Order has no items")
                    .throwException(new IllegalArgumentException("Order must have at least one item"))
                .otherwise()
                    .log("Order validation passed");
        
        from("direct:saveOrder")
            .routeId("save-order")
            .to("sql:INSERT INTO orders (order_id, customer_id, total_amount, status, created_at) VALUES (:#${body.orderId}, :#${body.customerId}, :#${body.totalAmount}, :#${body.status}, :#${body.createdAt})?dataSource=#dataSource")
            .log("Order saved to database");
    }
}
```

#### Payment Router
```java
package com.learning.integration.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PaymentRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("direct:processPayment")
            .routeId("process-payment")
            .log("Processing payment for order: ${body.orderId}")
            .to("direct:validatePayment")
            .to("direct:authorizePayment")
            .to("direct:capturePayment")
            .choice()
                .when(header("paymentStatus").isEqualTo("SUCCESS"))
                    .log("Payment successful: ${header.transactionId}")
                    .setHeader("orderStatus", constant("CONFIRMED"))
                .otherwise()
                    .log("Payment failed")
                    .throwException(new RuntimeException("Payment failed"))
            .end();
        
        from("direct:validatePayment")
            .routeId("validate-payment")
            .process(exchange -> {
                Order order = exchange.getIn().getBody(Order.class);
                
                if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Invalid payment amount");
                }
                
                if (order.getPaymentMethod() == null || order.getPaymentMethod().isEmpty()) {
                    throw new IllegalArgumentException("Payment method is required");
                }
                
                exchange.getIn().setHeader("validated", true);
            });
        
        from("direct:authorizePayment")
            .routeId("authorize-payment")
            .log("Authorizing payment")
            .delay(100)
            .setHeader("transactionId", simple("TXN-${date:now:yyyyMMddHHmmss}"))
            .choice()
                .when(simple("${random(100)} > 10"))
                    .setHeader("paymentStatus", constant("SUCCESS"))
                    .log("Payment authorized")
                .otherwise()
                    .setHeader("paymentStatus", constant("FAILED"))
                    .log("Payment authorization failed");
        
        from("direct:capturePayment")
            .routeId("capture-payment")
            .log("Capturing payment: ${header.transactionId}")
            .choice()
                .when(header("paymentStatus").isEqualTo("SUCCESS"))
                    .to("jms:queue:payment-success")
                    .log("Payment captured successfully")
                .otherwise()
                    .to("jms:queue:payment-failed")
                    .throwException(new RuntimeException("Failed to capture payment"));
        
        from("direct:refundPayment")
            .routeId("refund-payment")
            .log("Processing refund for order")
            .to("sql:SELECT * FROM payments WHERE order_id = :#${header.orderId}?dataSource=#dataSource")
            .process(exchange -> {
                exchange.getIn().setHeader("refundId", "REF-" + System.currentTimeMillis());
            })
            .to("jms:queue:refund-queue")
            .log("Refund initiated");
    }
}
```

#### Inventory Router
```java
package com.learning.integration.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class InventoryRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("direct:reserveInventory")
            .routeId("reserve-inventory")
            .log("Reserving inventory for order")
            .split().simple("${body.items}")
                .log("Checking inventory for product: ${body.productId}")
                .to("direct:checkStock")
                .choice()
                    .when(header("stockAvailable").isEqualTo(true))
                        .to("direct:reserveStock")
                        .log("Stock reserved for product")
                    .otherwise()
                        .log("Insufficient stock for product")
                        .throwException(new IllegalArgumentException("Insufficient stock"))
                .end()
            .log("All inventory reserved")
            .setHeader("inventoryStatus", constant("RESERVED"));
        
        from("direct:checkStock")
            .routeId("check-stock")
            .to("sql:SELECT stock_quantity FROM products WHERE product_id = :#${body.productId}?dataSource=#dataSource")
            .process(exchange -> {
                int availableStock = 100;
                
                try {
                    var result = exchange.getIn().getBody();
                    if (result instanceof java.util.List list && !list.isEmpty()) {
                        Object row = list.get(0);
                        if (row instanceof java.util.Map map && map.containsKey("stock_quantity")) {
                            availableStock = ((Number) map.get("stock_quantity")).intValue();
                        }
                    }
                } catch (Exception e) {
                    availableStock = 100;
                }
                
                boolean available = availableStock >= 1;
                exchange.getIn().setHeader("stockAvailable", available);
                exchange.getIn().setHeader("availableStock", availableStock);
            });
        
        from("direct:reserveStock")
            .routeId("reserve-stock")
            .log("Reserving stock for product")
            .to("sql:UPDATE products SET stock_quantity = stock_quantity - 1 WHERE product_id = :#${body.productId}?dataSource=#dataSource")
            .to("jms:queue:inventory-reserved")
            .log("Stock reservation completed");
        
        from("direct:releaseInventory")
            .routeId("release-inventory")
            .log("Releasing inventory for order")
            .to("sql:UPDATE products p SET stock_quantity = stock_quantity + (SELECT quantity FROM order_items WHERE product_id = p.product_id) WHERE product_id IN (SELECT product_id FROM order_items)?dataSource=#dataSource")
            .to("jms:queue:inventory-released")
            .log("Inventory released");
        
        from("jms:queue:low-stock-alert")
            .routeId("low-stock-alert")
            .log("Low stock alert received: ${body}")
            .to("direct:notifyAdmin");
    }
}
```

#### Notification Router
```java
package com.learning.integration.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationRouter extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        
        from("direct:notifyCustomer")
            .routeId("notify-customer")
            .log("Sending notification to customer")
            .to("direct:sendEmail")
            .to("direct:sendSms")
            .choice()
                .when(header("notificationStatus").isEqualTo("SENT"))
                    .log("Customer notified successfully")
                .otherwise()
                    .log("Failed to notify customer")
            .end();
        
        from("direct:sendEmail")
            .routeId("send-email")
            .process(exchange -> {
                Order order = exchange.getIn().getBody(Order.class);
                
                String subject = "Order Confirmation - " + order.getOrderId();
                String body = String.format(
                    "Dear Customer,\n\nYour order %s has been confirmed.\n\nTotal: $%s\n\nThank you for shopping with us!",
                    order.getOrderId(),
                    order.getTotalAmount()
                );
                
                exchange.getIn().setHeader("emailTo", order.getCustomerEmail());
                exchange.getIn().setHeader("emailSubject", subject);
                exchange.getIn().setBody(body);
            })
            .to("smtp:localhost?username=test&password=test")
            .setHeader("notificationStatus", constant("SENT"))
            .onException(Exception.class)
                .setHeader("notificationStatus", constant("FAILED"))
                .continueOn();
        
        from("direct:sendSms")
            .routeId("send-sms")
            .log("Sending SMS notification")
            .delay(50)
            .setHeader("notificationStatus", constant("SENT"));
        
        from("direct:notifyAdmin")
            .routeId("notify-admin")
            .log("Sending admin notification")
            .process(exchange -> {
                String message = "Low stock alert: " + exchange.getIn().getBody();
                exchange.getIn().setBody(message);
            })
            .to("smtp:admin@example.com")
            .log("Admin notified");
        
        from("jms:queue:payment-success")
            .routeId("payment-success-notification")
            .log("Sending payment success notification")
            .to("direct:sendEmail");
        
        from("jms:queue:payment-failed")
            .routeId("payment-failed-notification")
            .log("Sending payment failed notification")
            .process(exchange -> {
                exchange.getIn().setBody("Your payment failed. Please try again.");
            })
            .to("direct:sendEmail");
    }
}
```

#### Camel Configuration
```java
package com.learning.integration.config;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.ErrorHandlerBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.sql.SqlComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;

@Configuration
public class CamelConfig {
    
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")
            .build();
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean
    public SqlComponent sqlComponent(DataSource dataSource) {
        SqlComponent sql = new SqlComponent();
        sql.setDataSource(dataSource);
        return sql;
    }
    
    @Bean
    public JmsComponent jmsComponent() {
        JmsComponent jms = JmsComponent.jmsConnectionFactory(
            new org.apache.activemq.ActiveMQConnectionFactory("vm://localhost")
        );
        return jms;
    }
    
    @Bean
    public ErrorHandlerBuilder errorHandler() {
        return new org.apache.camel.builder.DeadLetterChannelBuilder("jms:queue:dead-letter")
            .maximumRedeliveries(3)
            .redeliveryDelay(1000);
    }
}
```

#### Error Handler
```java
package com.learning.integration.error;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandler implements Processor {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    
    @Override
    public void process(Exchange exchange) throws Exception {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        
        logger.error("Error processing exchange: {}", exception.getMessage());
        
        String originalBody = exchange.getIn().getBody(String.class);
        
        exchange.getIn().setHeader("error", exception.getMessage());
        exchange.getIn().setHeader("originalBody", originalBody);
        exchange.getIn().setHeader("timestamp", System.currentTimeMillis());
        
        String errorMessage = String.format(
            "{\"error\":\"%s\",\"timestamp\":%d,\"originalBody\":\"%s\"}",
            exception.getMessage(),
            System.currentTimeMillis(),
            originalBody != null ? originalBody.replace("\"", "'") : ""
        );
        
        exchange.getIn().setBody(errorMessage);
    }
}
```

#### Retry Processor
```java
package com.learning.integration.error;

import org.apache.camel.Processor;
import org.apache.camel.Exchange;

public class RetryProcessor implements Processor {
    
    private final int maxRetries;
    private final long retryDelayMs;
    
    public RetryProcessor(int maxRetries, long retryDelayMs) {
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }
    
    @Override
    public void process(Exchange exchange) throws Exception {
        int currentRetry = exchange.getProperty("CamelRetryCounter", 0, Integer.class);
        
        if (currentRetry < maxRetries) {
            exchange.setProperty("CamelRetryCounter", currentRetry + 1);
            
            System.out.println("Retry attempt " + (currentRetry + 1) + " of " + maxRetries);
            
            Thread.sleep(retryDelayMs);
            
            throw new org.apache.camel.RuntimeCamelException("Retry required");
        }
        
        exchange.removeProperty("CamelRetryCounter");
    }
}
```

#### Application Properties
```yaml
camel:
  spring:
    component:
      servlet:
        mapping:
          context-path: /api/*
  tracing:
    enabled: true
  health:
    enabled: true

server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create
  activemq:
    broker-url: vm://localhost

logging:
  level:
    org.apache.camel: INFO
    com.learning.integration: DEBUG
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
      - SPRING_ACTIVEMQ_BROKER_URL=vm://localhost

  activemq:
    image: apache/activemq-classic:5.18.0
    ports:
      - "61616:61616"
      - "8161:8161"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
```

### Build and Run Instructions
```bash
# Build the project
cd enterprise-integration
mvn clean package

# Run with Docker
docker-compose up -d

# Run the application
java -jar target/enterprise-integration-1.0.0.jar

# Test the API
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C001","customerEmail":"test@example.com","totalAmount":100.00,"paymentMethod":"CREDIT_CARD","items":[{"productId":"P001","quantity":2}]}'

# Test order retrieval
curl http://localhost:8080/api/orders/1

# Test order cancellation
curl -X PUT http://localhost:8080/api/orders/1/cancel
```

### Learning Outcomes
- Build enterprise integration routes with Apache Camel
- Implement EIP patterns (split, aggregate, enrich)
- Configure JMS and message queues
- Set up database integration with SQL component
- Implement error handling and retry mechanisms
- Create REST APIs with Camel REST DSL
- Add monitoring and metrics with Micrometer
- Build complex multi-system integration flows