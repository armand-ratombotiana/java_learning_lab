# Serverless Projects - Module 58

This module covers AWS Lambda, Azure Functions, and serverless patterns for Java applications.

## Mini-Project: AWS Lambda Function with Spring Boot (2-4 hours)

### Overview
Build and deploy a serverless Java function using AWS Lambda and Spring Boot with proper configuration and testing.

### Project Structure
```
serverless-demo/
├── src/
│   └── main/
│       ├── java/com/learning/serverless/
│       │   ├── ServerlessApplication.java
│       │   ├── handler/ProductHandler.java
│       │   ├── model/Product.java
│       │   └── config/SpringConfig.java
│       └── resources/
│           └── application.yml
├── src/test/java/.../
├── pom.xml
├── aws-lambda.json
└── Dockerfile
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
    <artifactId>serverless-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <aws.lambda.java.core.version>2.1.0</aws.lambda.java.core.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-core</artifactId>
            <version>${aws.lambda.java.core.version}</version>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-events</artifactId>
            <version>3.11.0</version>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-log4j2</artifactId>
            <version>1.5.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j2-impl</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent</mainClass>
                                </transformer>
                            </transformers>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Lambda Handler Implementation
```java
// ServerlessApplication.java
package com.learning.serverless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerlessApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerlessApplication.class, args);
    }
}

// ProductHandler.java
package com.learning.serverless.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.serverless.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductHandler.class);
    private static final Map<Long, Product> products = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ConfigurableApplicationContext context;
    
    static {
        products.put(1L, new Product(1L, "Laptop Pro", 1299.99, "High-performance laptop"));
        products.put(2L, new Product(2L, "Wireless Mouse", 49.99, "Ergonomic wireless mouse"));
        products.put(3L, new Product(3L, "4K Monitor", 599.99, "27-inch 4K display"));
    }
    
    static ConfigurableApplicationContext getContext() {
        if (context == null) {
            context = SpringApplication.run(ServerlessApplication.class);
        }
        return context;
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request, 
            Context context) {
        
        logger.info("Lambda function invoked. Request: {}", request.getPath());
        logger.info("AWS Request ID: {}", context.getAwsRequestId());
        
        String httpMethod = request.getHttpMethod();
        String path = request.getPath();
        
        try {
            if ("GET".equals(httpMethod)) {
                if (path.matches(".*/products/\\d+.*")) {
                    return getProduct(request);
                }
                return getAllProducts();
            } else if ("POST".equals(httpMethod)) {
                return createProduct(request);
            } else if ("PUT".equals(httpMethod)) {
                return updateProduct(request);
            } else if ("DELETE".equals(httpMethod)) {
                return deleteProduct(request);
            }
            
            return response(404, "{\"error\":\"Not found\"}");
            
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return response(500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private APIGatewayProxyResponseEvent getAllProducts() {
        return response(200, objectMapper.writeValueAsString(products.values()));
    }
    
    private APIGatewayProxyResponseEvent getProduct(APIGatewayProxyRequestEvent request) {
        String pathParam = request.getPathParameters().get("id");
        Long id = Long.parseLong(pathParam);
        
        Product product = products.get(id);
        if (product != null) {
            return response(200, objectMapper.writeValueAsString(product));
        }
        return response(404, "{\"error\":\"Product not found\"}");
    }
    
    private APIGatewayProxyResponseEvent createProduct(APIGatewayProxyRequestEvent request) 
            throws Exception {
        Product newProduct = objectMapper.readValue(request.getBody(), Product.class);
        newProduct.setId(System.currentTimeMillis());
        products.put(newProduct.getId(), newProduct);
        
        logger.info("Created product: {}", newProduct.getId());
        return response(201, objectMapper.writeValueAsString(newProduct));
    }
    
    private APIGatewayProxyResponseEvent updateProduct(APIGatewayProxyRequestEvent request) 
            throws Exception {
        String pathParam = request.getPathParameters().get("id");
        Long id = Long.parseLong(pathParam);
        
        Product existing = products.get(id);
        if (existing == null) {
            return response(404, "{\"error\":\"Product not found\"}");
        }
        
        Product updates = objectMapper.readValue(request.getBody(), Product.class);
        existing.setName(updates.getName());
        existing.setPrice(updates.getPrice());
        existing.setDescription(updates.getDescription());
        
        return response(200, objectMapper.writeValueAsString(existing));
    }
    
    private APIGatewayProxyResponseEvent deleteProduct(APIGatewayProxyRequestEvent request) {
        String pathParam = request.getPathParameters().get("id");
        Long id = Long.parseLong(pathParam);
        
        if (products.remove(id) != null) {
            return response(204, "");
        }
        return response(404, "{\"error\":\"Product not found\"}");
    }
    
    private APIGatewayProxyResponseEvent response(int status, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(status);
        response.setBody(body);
        response.setHeaders(Map.of(
            "Content-Type", "application/json",
            "Access-Control-Allow-Origin", "*"
        ));
        return response;
    }
}

// Product.java
package com.learning.serverless.model;

public class Product {
    private Long id;
    private String name;
    private Double price;
    private String description;
    
    public Product() {}
    
    public Product(Long id, String name, Double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

#### AWS Configuration
```json
// aws-lambda.json
{
  "FunctionName": "ProductService",
  "Runtime": "java21",
  "Handler": "com.learning.serverless.handler.ProductHandler::handleRequest",
  "MemorySize": 512,
  "Timeout": 30,
  "Description": "Serverless Product API"
}
```

### Build and Deploy Instructions
```bash
# Build the application
mvn clean package -DskipTests

# Verify the JAR contains the handler
jar -tf target/serverless-demo-1.0.0.jar | grep ProductHandler

# Create Lambda function using AWS CLI
aws lambda create-function \
    --function-name ProductService \
    --runtime java21 \
    --handler com.learning.serverless.handler.ProductHandler::handleRequest \
    --role arn:aws:iam::123456789012:role/lambda-execution-role \
    --zip-file fileb://target/serverless-demo-1.0.0.jar \
    --memory-size 512 \
    --timeout 30

# Update function code
aws lambda update-function-code \
    --function-name ProductService \
    --zip-file fileb://target/serverless-demo-1.0.0.jar

# Create API Gateway
aws apigatewayv2 create-api \
    --name product-api \
    --protocol-type HTTP \
    --target arn:aws:lambda:us-east-1:123456789012:function:ProductService

# Test locally with SAM CLI
sam local invoke ProductFunction --event events/api-request.json

# Test via API Gateway
curl https://{api-id}.execute-api.us-east-1.amazonaws.com/prod/products

# View CloudWatch logs
aws logs filter-log-events --log-group-name /aws/lambda/ProductService
```

---

## Real-World Project: Multi-Provider Serverless E-Commerce (8+ hours)

### Overview
Build a complete serverless e-commerce backend using AWS Lambda, Azure Functions, and Google Cloud Functions with unified API gateway, event-driven architecture, and polyglot services.

### Architecture
- **AWS Lambda**: Product catalog, order processing
- **Azure Functions**: Payment processing, notifications
- **Google Cloud Functions**: Analytics, recommendations
- **AWS API Gateway**: Main API entry point
- **EventBridge**: Cross-cloud event routing
- **DynamoDB**: Product data store
- **Azure Cosmos DB**: Customer data
- **Redis**: Caching layer

### Project Structure
```
serverless-ecommerce/
├── aws-lambda/
│   ├── product-service/
│   ├── order-service/
│   └── pom.xml
├── azure-functions/
│   ├── payment-service/
│   └── notification-service/
├── gcp-functions/
│   ├── analytics-service/
│   └── recommendation-service/
├── api-gateway/
│   ├── openapi.yaml
│   └── integration.yaml
├── events/
│   └── event-bridge-rules.yaml
├── infrastructure/
│   ├── terraform/
│   └── serverless.yml
└── tests/
```

### Implementation

#### AWS Lambda Product Service
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>product-service</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-dynamodb</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb-enhanced</artifactId>
            <version>2.20.0</version>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-events</artifactId>
            <version>3.11.0</version>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-log4j2</artifactId>
            <version>1.5.1</version>
        </dependency>
    </dependencies>
</project>

// ProductServiceApplication.java
package com.learning.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}

// ProductController.java
package com.learning.ecommerce.controller;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import com.learning.ecommerce.service.EventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;
    
    public ProductController(ProductRepository productRepository, 
                            EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor) {
        
        if (category != null) {
            return productRepository.findByCategory(category, limit);
        }
        return productRepository.findAll(limit);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        eventPublisher.publish("product.created", saved);
        return ResponseEntity.status(201).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id, 
            @RequestBody Product product) {
        
        return productRepository.findById(id)
            .map(existing -> {
                existing.setName(product.getName());
                existing.setPrice(product.getPrice());
                existing.setStock(product.getStock());
                existing.setCategory(product.getCategory());
                Product updated = productRepository.save(existing);
                eventPublisher.publish("product.updated", updated);
                return ResponseEntity.ok(updated);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (productRepository.delete(id)) {
            eventPublisher.publish("product.deleted", Map.of("id", id));
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

// Product.java
package com.learning.ecommerce.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Product {
    private String id;
    private String name;
    private Double price;
    private Integer stock;
    private String category;
    private String description;
    private String imageUrl;
    private Long createdAt;
    private Long updatedAt;
    
    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    @DynamoDbSortKey
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}

// ProductRepository.java
package com.learning.ecommerce.repository;

import com.learning.ecommerce.model.Product;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {
    
    private final DynamoDbTable<Product> productTable;
    
    public ProductRepository(DynamoDbEnhancedClient dynamoDbClient) {
        this.productTable = dynamoDbClient.table("products", TableSchema.fromBean(Product.class));
    }
    
    public Optional<Product> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(productTable.getItem(key));
    }
    
    public List<Product> findAll(int limit) {
        return productTable.scan().items().stream()
            .limit(limit)
            .toList();
    }
    
    public List<Product> findByCategory(String category, int limit) {
        return productTable.query(
                QueryConditional.keyEqualTo(Key.builder().partitionValue(category).build()))
            .items().stream()
            .limit(limit)
            .toList();
    }
    
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(java.util.UUID.randomUUID().toString());
            product.setCreatedAt(System.currentTimeMillis());
        }
        product.setUpdatedAt(System.currentTimeMillis());
        productTable.putItem(product);
        return product;
    }
    
    public boolean delete(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Product item = productTable.getItem(key);
        if (item != null) {
            productTable.deleteItem(key);
            return true;
        }
        return false;
    }
}

// EventPublisher.java
package com.learning.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    private final EventBridgeClient eventBridgeClient;
    private final ObjectMapper objectMapper;
    
    public EventPublisher(EventBridgeClient eventBridgeClient) {
        this.eventBridgeClient = eventBridgeClient;
        this.objectMapper = new ObjectMapper();
    }
    
    public void publish(String eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            PutEventsRequestEntry entry = PutEventsRequestEntry.builder()
                .source("com.ecommerce.product-service")
                .detailType(eventType)
                .detail(jsonPayload)
                .build();
            
            PutEventsRequest request = PutEventsRequest.builder()
                .entries(entry)
                .build();
            
            eventBridgeClient.putEvents(request);
            logger.info("Published event: {}", eventType);
            
        } catch (Exception e) {
            logger.error("Failed to publish event: {}", eventType, e);
        }
    }
}
```

#### Azure Functions Payment Service
```java
// pom.xml for Azure Functions
<dependency>
    <groupId>com.microsoft.azure.functions</groupId>
    <artifactId>azure-functions-java-library</artifactId>
    <version>3.0.0</version>
</dependency>

// PaymentFunction.java
package com.learning.ecommerce;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PaymentFunction {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentFunction.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @FunctionName("ProcessPayment")
    public HttpResponseMessage processPayment(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, 
                        route = "payments") HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String orderId,
            ExecutionContext context) {
        
        logger.info("Processing payment for order: {}", orderId);
        
        Optional<String> body = request.getBody();
        if (body.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("{\"error\":\"Request body required\"}")
                .build();
        }
        
        try {
            PaymentRequest paymentRequest = objectMapper.readValue(body.get(), 
                PaymentRequest.class);
            
            PaymentResult result = processPaymentInternal(orderId, paymentRequest);
            
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(result))
                .build();
                
        } catch (Exception e) {
            logger.error("Payment processing failed", e);
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    private PaymentResult processPaymentInternal(String orderId, PaymentRequest request) {
        // Simulate payment processing
        String transactionId = "TXN-" + System.currentTimeMillis();
        boolean success = request.getAmount() > 0 && request.getAmount() < 10000;
        
        return new PaymentResult(
            orderId,
            transactionId,
            success ? "SUCCESS" : "FAILED",
            request.getAmount(),
            System.currentTimeMillis()
        );
    }
    
    record PaymentRequest(Double amount, String currency, String cardToken) {}
    record PaymentResult(String orderId, String transactionId, String status, 
                        Double amount, Long timestamp) {}
}
```

#### Google Cloud Functions Analytics Service
```java
// AnalyticsFunction.java
package com.learning.analytics;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyticsFunction implements HttpFunction {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsFunction.class);
    private static final Map<String, AnalyticsEvent> events = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        
        if (request.getMethod().equals("POST") && path.equals("/track")) {
            trackEvent(request, response);
        } else if (request.getMethod().equals("GET") && path.equals("/analytics")) {
            getAnalytics(response);
        } else {
            response.setStatusCode(404);
            response.getWriter().write("{\"error\":\"Not found\"}");
        }
    }
    
    private void trackEvent(HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody().asString();
        AnalyticsEvent event = objectMapper.readValue(body, AnalyticsEvent.class);
        
        events.put(event.getEventId(), event);
        logger.info("Tracked event: {}", event.getEventType());
        
        response.setStatusCode(201);
        response.getWriter().write("{\"status\":\"tracked\"}");
    }
    
    private void getAnalytics(HttpResponse response) throws Exception {
        Map<String, Long> byType = events.values().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                AnalyticsEvent::getEventType,
                java.util.stream.Collectors.counting()
            ));
        
        response.setStatusCode(200);
        response.getWriter().write(objectMapper.writeValueAsString(byType));
    }
}

class AnalyticsEvent {
    private String eventId;
    private String eventType;
    private String userId;
    private String productId;
    private Long timestamp;
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
```

### Infrastructure as Code
```yaml
# serverless.yml
service: serverless-ecommerce

provider:
  name: aws
  runtime: java21
  environment:
    DYNAMO_TABLE: products
    EVENT_BUS: default

functions:
  productService:
    handler: com.learning.ecommerce.handler.ProductHandler::handleRequest
    events:
      - http:
          path: /products
          method: any
      - http:
          path: /products/{id+}
          method: any
    memorySize: 512
    timeout: 30
    environment:
      DYNAMO_TABLE: ${self:service}-products

  orderService:
    handler: com.learning.ecommerce.handler.OrderHandler::handleRequest
    events:
      - http:
          path: /orders
          method: post
      - http:
          path: /orders/{id}
          method: get
    memorySize: 512
    timeout: 30

resources:
  Resources:
    ProductsTable:
      Type: AWS::DynamoDB::Table
      Properties:
        TableName: ${self:service}-products
        BillingMode: PAY_PER_REQUEST
        AttributeDefinitions:
          - AttributeName: id
            AttributeType: S
          - AttributeName: category
            AttributeType: S
        KeySchema:
          - AttributeName: id
            KeyType: HASH
        GlobalSecondaryIndexes:
          - IndexName: category-index
            KeySchema:
              - AttributeName: category
                KeyType: HASH
            Projection:
              ProjectionType: ALL
```

### Build and Deployment
```bash
# Build all Lambda functions
cd aws-lambda/product-service
mvn clean package -DskipTests

cd aws-lambda/order-service
mvn clean package -DskipTests

# Deploy with Serverless Framework
serverless deploy --verbose

# Deploy Azure Functions
cd azure-functions/payment-service
mvn azure-functions:package
az functionapp deployment source config-local-git \
    --resource-group rg-ecommerce \
    --name payment-func-001

# Deploy Google Cloud Functions
gcloud functions deploy analytics-function \
    --runtime java21 \
    --trigger-http \
    --region us-central1

# Create API Gateway integration
aws apigatewayv2 create-integration \
    --name product-integration \
    --api-id <api-id> \
    --integration-type AWS_PROXY \
    --integration-uri arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:123456789:function:productService/invocations

# Test the complete system
curl -X POST https://api.example.com/products \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Product","price":99.99,"stock":10,"category":"electronics"}'

curl https://api.example.com/products
```

### Learning Outcomes
- Deploy Lambda functions with proper configuration
- Integrate with multiple cloud providers
- Implement event-driven architecture
- Configure API Gateway routing
- Set up cross-service communication
- Implement monitoring and logging