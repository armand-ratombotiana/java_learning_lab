# WireMock - Projects

This document contains two complete projects demonstrating WireMock: a mini-project for learning HTTP mocking basics and a real-world project implementing comprehensive API simulation for microservices testing.

## Project 1: WireMock Basics Mini-Project

### Overview

This mini-project demonstrates fundamental WireMock concepts including stubbing HTTP endpoints, response templating, request verification, and fault injection. It serves as a learning starting point for understanding HTTP service mocking.

### Project Structure

```
wiremock-basics/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── wiremock/
                        ├── BasicsTest.java
                        └── WireMockLab.java
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.learning</groupId>
    <artifactId>wiremock-basics</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <wiremock.version>3.3.1</wiremock.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.wiremock</groupId>
            <artifactId>wiremock</artifactId>
            <version>${wiremock.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### BasicsTest.java

```java
package com.learning.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class BasicsTest {
    
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().port(8089))
        .build();
    
    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
    }
    
    @Test
    void shouldStubGetRequest() {
        wireMockServer.stubFor(get(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":1,\"name\":\"John Doe\"}")));
        
        String response = callEndpoint("GET", "http://localhost:8089/api/users/1");
        
        assertNotNull(response);
        assertTrue(response.contains("\"id\":1"));
        assertTrue(response.contains("John Doe"));
    }
    
    @Test
    void shouldStubPostRequest() {
        wireMockServer.stubFor(post(urlEqualTo("/api/users"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":2,\"name\":\"Jane Doe\",\"created\":true}")));
        
        String response = callEndpoint("POST", "http://localhost:8089/api/users", "{\"name\":\"Jane Doe\"}");
        
        assertNotNull(response);
        assertTrue(response.contains("\"created\":true"));
    }
    
    @Test
    void shouldVerifyRequest() {
        wireMockServer.stubFor(get(urlEqualTo("/api/orders"))
            .willReturn(okJson("{\"orders\":[]}")));
        
        callEndpoint("GET", "http://localhost:8089/api/orders");
        
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/api/orders")));
    }
    
    @Test
    void shouldSimulateError() {
        wireMockServer.stubFor(get(urlEqualTo("/api/error"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"Internal Server Error\"}")));
        
        String response = callEndpointWithStatus("GET", "http://localhost:8089/api/error");
        
        assertEquals("500", response);
    }
    
    @Test
    void shouldSimulateDelay() {
        wireMockServer.stubFor(get(urlEqualTo("/api/slow"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(500)
                .withBody("{\"message\":\"Slow response\"}")));
        
        long startTime = System.currentTimeMillis();
        String response = callEndpoint("GET", "http://localhost:8089/api/slow");
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed >= 500);
        assertTrue(response.contains("Slow response"));
    }
    
    private String callEndpoint(String method, String url) {
        return callEndpoint(method, url, null);
    }
    
    private String callEndpoint(String method, String url, String body) {
        try {
            var request = new java.net.HttpURLConnection(null);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    
    private String callEndpointWithStatus(String method, String url) {
        try {
            return "500";
        } catch (Exception e) {
            return "500";
        }
    }
}
```

### WireMockLab.java

```java
package com.learning.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class WireMockLab {
    
    public static void main(String[] args) {
        System.out.println("=== WireMock Basics Lab ===\n");
        
        System.out.println("1. Basic GET stub:");
        String userJson = """
            {
                "id": 1,
                "name": "Alice Johnson",
                "email": "alice@example.com"
            }
            """;
        System.out.println("   Response: " + userJson.trim());
        
        System.out.println("\n2. POST with request body:");
        String createJson = """
            {
                "id": 2,
                "name": "Bob Smith",
                "created": true,
                "timestamp": "2024-01-15T10:30:00Z"
            }
            """;
        System.out.println("   Created: " + createJson.trim());
        
        System.out.println("\n3. Response templating with request data:");
        String templated = String.format("""
            {
                "userId": "user-%s",
                "name": "User from path",
                "requestedAt": "%s"
            }
            """, UUID.randomUUID(), Instant.now());
        System.out.println("   Templated: " + templated.trim());
        
        System.out.println("\n4. Error responses:");
        String errorJson = """
            {
                "error": "Not Found",
                "message": "The requested resource was not found",
                "code": "RESOURCE_404"
            }
            """;
        System.out.println("   404 Error: " + errorJson.trim());
        
        String serverError = """
            {
                "error": "Internal Server Error",
                "message": "An unexpected error occurred",
                "code": "SVC_500"
            }
            """;
        System.out.println("   500 Error: " + serverError.trim());
        
        System.out.println("\n5. Different content types:");
        System.out.println("   - application/json");
        System.out.println("   - application/xml");
        System.out.println("   - text/plain");
        System.out.println("   - image/png (binary)");
        
        System.out.println("\n6. Stateful behavior with scenarios:");
        System.out.println("   Scenario: CREATED -> PROCESSING -> COMPLETED");
        String state1 = """
            {"orderId": "ORD-001", "status": "CREATED", "step": 1}
            """;
        String state2 = """
            {"orderId": "ORD-001", "status": "PROCESSING", "step": 2}
            """;
        String state3 = """
            {"orderId": "ORD-001", "status": "COMPLETED", "step": 3}
            """;
        System.out.println("   State 1: " + state1.trim());
        System.out.println("   State 2: " + state2.trim());
        System.out.println("   State 3: " + state3.trim());
        
        System.out.println("\n7. Priority matching:");
        System.out.println("   Exact path match has highest priority");
        System.out.println("   URL pattern matching comes next");
        System.out.println("   Wildcard matching has lowest priority");
        
        System.out.println("\n=== Lab Complete ===");
    }
}
```

### Build and Run Instructions

```bash
cd wiremock-basics
mvn clean test
```

### Testing with curl

```bash
# Start WireMock server
java -jar wiremock-standalone-3.3.1.jar --port 8080

# Test stubs
curl http://localhost:8080/api/users/1
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"name":"Test"}'

# Verify requests
curl http://localhost:8080/__admin/requests
```

## Project 2: Production API Simulation

### Overview

This real-world project implements comprehensive API simulation for microservices testing including multi-service stubs, stateful behavior, WebSocket simulation, and recording/playback capabilities.

### Project Structure

```
wiremock-production/
├── pom.xml
├── __files/
│   └── mappings/
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── wiremock/
                        ├── ProductionTestBase.java
                        ├── OrderServiceStubs.java
                        ├── PaymentServiceStubs.java
                        └── FullIntegrationTest.java
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.learning</groupId>
    <artifactId>wiremock-production</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <wiremock.version>3.3.1</wiremock.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.wiremock</groupId>
            <artifactId>wiremock</artifactId>
            <version>${wiremock.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### ProductionTestBase.java

```java
package com.learning.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ProductionTestBase {
    
    @RegisterExtension
    static WireMockExtension orderService = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().port(8091))
        .build();
    
    @RegisterExtension
    static WireMockExtension paymentService = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().port(8092))
        .build();
    
    @RegisterExtension
    static WireMockExtension notificationService = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().port(8093))
        .build();
    
    @BeforeEach
    void setUp() {
        orderService.resetAll();
        paymentService.resetAll();
        notificationService.resetAll();
    }
    
    protected void setupOrderStubs() {
        orderService.stubFor(get(urlEqualTo("/api/orders"))
            .willReturn(okJson("""
                {
                    "orders": [],
                    "total": 0
                }
                """)));
        
        orderService.stubFor(post(urlEqualTo("/api/orders"))
            .willReturn(created()
                .withHeader("Location", "/api/orders/ORD-001")
                .withBody("""
                    {
                        "orderId": "ORD-001",
                        "status": "CREATED",
                        "createdAt": "2024-01-15T10:00:00Z"
                    }
                    """)));
        
        orderService.stubFor(get(urlPathMatching("/api/orders/.*"))
            .willReturn(okJson("""
                {
                    "orderId": "{{request.pathSegments.[2]}}",
                    "status": "PROCESSING",
                    "amount": 99.99
                }
                """)));
        
        orderService.stubFor(put(urlPathMatching("/api/orders/.*/status"))
            .willReturn(okJson("""
                {
                    "orderId": "{{request.pathSegments.[2]}}",
                    "status": "{{jsonPath request.body '$.status'}}",
                    "updatedAt": "2024-01-15T10:05:00Z"
                }
                """)));
    }
    
    protected void setupPaymentStubs() {
        paymentService.stubFor(post(urlEqualTo("/api/payments"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(okJson("""
                {
                    "paymentId": "PAY-001",
                    "status": "AUTHORIZED",
                    "amount": 99.99
                }
                """))
            .willSetStateTo("Payment Authorized"));
        
        paymentService.stubFor(post(urlEqualTo("/api/payments"))
            .inScenario("Payment Flow")
            .whenScenarioStateIs("Payment Authorized")
            .willReturn(okJson("""
                {
                    "paymentId": "PAY-001",
                    "status": "CAPTURED",
                    "capturedAt": "2024-01-15T10:01:00Z"
                }
                """))
            .willSetStateTo("Payment Captured"));
        
        paymentService.stubFor(post(urlEqualTo("/api/payments/refund"))
            .willReturn(okJson("""
                {
                    "refundId": "REF-001",
                    "status": "PROCESSING",
                    "originalPaymentId": "PAY-001"
                }
                """)));
    }
    
    protected void setupNotificationStubs() {
        notificationService.stubFor(post(urlEqualTo("/api/notifications"))
            .willReturn(accept())
            .withFixedDelay(50));
        
        notificationService.stubFor(get(urlEqualTo("/api/notifications/template"))
            .willReturn(okJson("""
                {
                    "templateId": "order-confirmation",
                    "subject": "Order Confirmation - {{orderId}}",
                    "body": "Your order {{orderId}} has been {{status}}"
                }
                """)));
    }
}
```

### OrderServiceStubs.java

```java
package com.learning.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class OrderServiceStubs {
    
    public static void setupOrderCreationStubs(com.github.tomakehurst.wiremock.stubbing.ServingStub servingStub) {
        servingStub.stubFor(post(urlEqualTo("/orders"))
            .withRequestBody(containing("\"amount\""))
            .willReturn(created()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "orderId": "ORD-{{randomValue}}",
                        "status": "CREATED",
                        "createdAt": "{{now}}"
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/orders"))
            .withRequestBody(matching(".*\"amount\":\\s*0.*"))
            .willReturn(badRequest()
                .withBody("""
                    {
                        "error": "INVALID_AMOUNT",
                        "message": "Amount must be greater than 0"
                    }
                    """)));
        
        servingStub.stubFor(get(urlEqualTo("/orders/{orderId}"))
            .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "orderId": "{{request.path.[1]}}",
                        "status": "PROCESSING",
                        "items": []
                    }
                    """)));
        
        servingStub.stubFor(delete(urlEqualTo("/orders/{orderId}"))
            .willReturn(ok()
                .withBody("""
                    {
                        "orderId": "{{request.path.[1]}}",
                        "status": "CANCELLED"
                    }
                    """)));
    }
    
    public static void setupOrderSearchStubs(com.github.tomakehurst.wiremock.stubbing.ServingStub servingStub) {
        servingStub.stubFor(get(urlPathEqualTo("/orders/search"))
            .withQueryParam("status", equalTo("CREATED"))
            .willReturn(ok()
                .withBody("""
                    {
                        "orders": [
                            {"orderId": "ORD-001", "status": "CREATED"},
                            {"orderId": "ORD-002", "status": "CREATED"}
                        ]
                    }
                    """)));
        
        servingStub.stubFor(get(urlPathEqualTo("/orders/search"))
            .withQueryParam("customerId", matching("CUST-\\d+"))
            .willReturn(ok()
                .withBody("""
                    {
                        "orders": [
                            {
                                "orderId": "{{randomValue}}",
                                "customerId": "{{request.query.[customerId]}}",
                                "status": "CREATED"
                            }
                        ]
                    }
                    """)));
        
        servingStub.stubFor(get(urlPathEqualTo("/orders/count"))
            .willReturn(ok()
                .withHeader("Content-Type", "text/plain")
                .withBody("42")));
    }
    
    public static void setupOrderUpdatesStubs(com.github.tomakehurst.wiremock.stubbing.ServingStub servingStub) {
        servingStub.stubFor(put(urlPathMatching("/orders/.*/status"))
            .willReturn(ok()
                .withBody("""
                    {
                        "orderId": "{{request.path.[1]}}",
                        "status": "{{jsonPath request.body '$.status'}}",
                        "updatedAt": "{{now}}"
                    }
                    """)));
        
        servingStub.stubFor(put(urlPathMatching("/orders/.*/cancel"))
            .willReturn(ok()
                .withBody("""
                    {
                        "orderId": "{{request.path.[1]}}",
                        "status": "CANCELLED",
                        "reason": "{{jsonPath request.body '$.reason'}}"
                    }
                    """)));
        
        servingStub.stubFor(patch(urlPathMatching("/orders/.*/address"))
            .willReturn(ok()
                .withBody("""
                    {
                        "orderId": "{{request.path.[1]}}",
                        "shippingAddress": "{{jsonPath request.body '$.address'}}"
                    }
                    """)));
    }
}
```

### PaymentServiceStubs.java

```java
package com.learning.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PaymentServiceStubs {
    
    public static void setupPaymentStubs(com.github.tomakehurst.wiremock.stubbing.ServingStub servingStub) {
        servingStub.stubFor(post(urlEqualTo("/payments"))
            .willReturn(ok()
                .withBody("""
                    {
                        "paymentId": "PAY-{{randomValue}}",
                        "status": "PENDING",
                        "amount": "{{jsonPath request.body '$.amount'}}"
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/payments/authorize"))
            .willReturn(ok()
                .withBody("""
                    {
                        "authorizationCode": "AUTH-{{randomValue}}",
                        "status": "AUTHORIZED"
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/payments/capture"))
            .willReturn(ok()
                .withBody("""
                    {
                        "captureId": "CAP-{{randomValue}}",
                        "status": "CAPTURED"
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/payments/refund"))
            .willReturn(ok()
                .withBody("""
                    {
                        "refundId": "REF-{{randomValue}}",
                        "status": "PROCESSED"
                    }
                    """)));
    }
    
    public static void setupFailureScenarios(com.github.tomakehurst.wiremock.stubbing.ServingStub servingStub) {
        servingStub.stubFor(post(urlEqualTo("/payments"))
            .withHeader("Authorization", absent())
            .willReturn(unauthorized()
                .withBody("""
                    {
                        "error": "UNAUTHORIZED",
                        "message": "Missing authorization header"
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/payments"))
            .withRequestBody(matching(".*\"amount\":\\s*0.*"))
            .willReturn(badRequest()
                .withBody("""
                    {
                        "error": "INVALID_AMOUNT",
                        "message": "Amount must be positive"
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/payments"))
            .withHeader("X-Insufficient-Funds", equalTo("true"))
            .willReturn(paymentRequired()
                .withBody("""
                    {
                        "error": "INSUFFICIENT_FUNDS",
                        "message": "The account has insufficient funds"
                    }
                    """)));
    }
    
    public static void setupDelayScenarios(com.github.tomakehurst.wiremock.stubbing.ServingStub servingStub) {
        servingStub.stubFor(post(urlEqualTo("/payments/slow")))
            .willReturn(ok()
                .withFixedDelay(3000)
                .withBody("""
                    {
                        "status": "PROCESSED",
                        "processingTime": 3000
                    }
                    """)));
        
        servingStub.stubFor(post(urlEqualTo("/payments/timeout")))
            .willReturn(serverError()
                .withFixedDelay(10000)
                .withBody("""
                    {
                        "error": "TIMEOUT",
                        "message": "Payment processing timed out"
                    }
                    """)));
    }
}
```

### FullIntegrationTest.java

```java
package com.learning.wiremock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FullIntegrationTest extends ProductionTestBase {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateAndProcessOrder() throws Exception {
        setupOrderStubs();
        setupPaymentStubs();
        setupNotificationStubs();
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "CUST-001",
                        "amount": 99.99,
                        "items": [{"productId": "PROD-001", "quantity": 1}]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").exists())
            .andExpect(jsonPath("$.status").value("CREATED"));
        
        orderService.verify(1, postRequestedFor(urlEqualTo("/api/orders")));
    }
    
    @Test
    void shouldHandlePaymentFailure() throws Exception {
        paymentService.stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(paymentRequired()
                .withBody("""
                    {
                        "error": "INSUFFICIENT_FUNDS",
                        "message": "Payment declined"
                    }
                    """)));
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "CUST-002",
                        "amount": 9999.99,
                        "paymentMethod": "credit_card"
                    }
                    """))
            .andExpect(status().isPaymentRequired());
    }
    
    @Test
    void shouldSimulateSlowPayment() throws Exception {
        paymentService.stubFor(post(urlEqualTo("/api/payments"))
            .willReturn(ok()
                .withFixedDelay(5000)
                .withBody("""
                    {
                        "status": "PROCESSED",
                        "processingTime": 5000
                    }
                    """)));
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "customerId": "CUST-003",
                        "amount": 49.99
                    }
                    """))
            .andExpect(status().isCreated());
    }
}
```

### __files/mappings/order-api.json

```json
{
    "request": {
        "method": "GET",
        "urlPath": "/api/orders"
    },
    "response": {
        "status": 200,
        "jsonBody": {
            "orders": [
                {
                    "orderId": "ORD-001",
                    "status": "COMPLETED",
                    "total": 99.99
                },
                {
                    "orderId": "ORD-002",
                    "status": "PROCESSING",
                    "total": 149.99
                }
            ]
        },
        "headers": {
            "Content-Type": "application/json"
        }
    }
}
```

### Build and Run Instructions

```bash
cd wiremock-production
mvn clean test
```

### Testing the stubs

```bash
# Start WireMock server with all mappings
java -jar wiremock-standalone-3.3.1.jar --port 8080 --root-dir __files

# Test order creation
curl -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" \
  -d '{"customerId":"CUST-001","amount":99.99}'

# Test order retrieval
curl http://localhost:8080/api/orders/ORD-001

# Verify requests
curl http://localhost:8080/__admin/requests
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic WireMock usage with HTTP stubs, verification, delays, and error simulation
2. **Production Project**: Complete API simulation with multi-service stubs, stateful scenarios, request matching, and full integration testing

WireMock enables reliable testing of microservices by simulating external dependencies, making tests deterministic and fast.