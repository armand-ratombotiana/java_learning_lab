# Gatling - Projects

This document contains two complete projects demonstrating Gatling: a mini-project for learning Scala-based load testing and a real-world project implementing production-scale scenario simulations.

## Mini-Projects by Concept

### 1. Scenario Definition (2 hours)
Define Scala-based test scenarios with feeders, session management, and conditional logic. Use built-in protocols.

### 2. Injection Profiles (2 hours)
Configure load injection with ramps, constant, and peak users. Define duration and termination strategies.

### 3. Checks & Assertions (2 hours)
Implement response validation with checks for status, body, and headers. Set response time assertions.

### 4. Reports & Analysis (2 hours)
Generate Gatling reports with request statistics, response time distribution, and active user graphs.

### Real-world: Load Testing Platform
Build production-scale load testing platform with complex scenarios, data feeders, and detailed analytics.

---

## Project 1: Gatling Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Gatling concepts including scenario definition, injection profiles, feeders, checks, and assertions. It serves as a learning starting point for Scala-based load testing.

### Project Structure

```
gatling-basics/
├── pom.xml
└── src/
    └── test/
        └── scala/
            └── com/
                └── learning/
                    └── gatling/
                        └── BasicsSimulation.scala
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>gatling-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <gatling.version>3.9.5</gatling.version>
        <scala.version>2.13.12</scala.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.gatling</groupId>
            <artifactId>gatling-core</artifactId>
            <version>${gatling.version}</version>
        </dependency>
        <dependency>
            <groupId>io.gatling</groupId>
            <artifactId>gatling-http</artifactId>
            <version>${gatling.version}</version>
        </dependency>
    </dependencies>
</project>
```

### BasicsSimulation.scala

```scala
package com.learning.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicsSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val scenario1 = scenario("Browse Products")
    .exec(
      http("Get All Products")
        .get("/api/products")
        .check(status.is(200))
        .check(jsonPath("$.products").exists)
    )
    .pause(200.milliseconds)
    .exec(
      http("Get Product Detail")
        .get("/api/products/1")
        .check(status.is(200))
    )

  val scenario2 = scenario("Place Order")
    .exec(
      http("Get Products")
        .get("/api/products")
        .check(status.is(200))
    )
    .pause(100.milliseconds)
    .exec(
      http("Create Order")
        .post("/api/orders")
        .body(StringBody("""{
          "productId": "PROD-001",
          "quantity": 1,
          "customerId": "CUST-001"
        }"""))
        .check(status.is(201))
        .check(jsonPath("$.orderId").saveAs("orderId"))
    )

  val injectProfile1 = rampUsers(10).during(5.seconds)
  val injectProfile2 = constantUsersPerSec(2).during(8.seconds)

  setUp(
    scenario1.inject(injectProfile1),
    scenario2.inject(injectProfile2)
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.percentile(95).lt(500),
     global.successfulRequests.percent.gt(99)
   )
}
```

### GatlingLab.scala

```scala
package com.learning.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

object GatlingLab {

  def basicScenario = scenario("Basic API Test")
    .exec(http("GET /api/health").get("/actuator/health"))

  def feedScenario = scenario("Data-Driven Test")
    .feed(Iterator.continually(Map(
      "userId" -> java.util.UUID.randomUUID.toString,
      "product" -> s"PROD-${scala.util.Random.nextInt(100)}"
    )))
    .exec(http("Create Order").post("/api/orders")
      .body(StringBody("""{"userId":"${userId}","product":"${product}"}""")))

  def checkScenario = scenario("Validate Response")
    .exec(http("Get Products").get("/api/products")
      .check(
        status.in(200 to 210),
        jsonPath("$.products.length").gt(0),
        header("Content-Type").contains("application/json")
      ))

  def loopScenario = scenario("Repeat Test")
    .exec(http("Get Products").get("/api/products"))
    .pause(1)
    .repeat(5) {
      exec(http("Get Order").get("/api/orders/${orderId}"))
    }

  def exitScenario = scenario("Conditional Flow")
    .exec(http("Get Order").get("/api/orders/1")
      .check(jsonPath("$.status").saveAs("status")))
    .doIf("${status}".equals("COMPLETED")) {
      exec(http("Get Invoice").get("/api/orders/1/invoice"))
    }

  def main(args: Array[String]): Unit = {
    println("=== Gatling Basics Lab ===\n")
    
    println("1. Scenario Types:")
    println("   - BasicScenario: Simple HTTP requests")
    println("   - FeedScenario: Data-driven with feeders")
    println("   - CheckScenario: Response validation")
    println("   - LoopScenario: Repeated requests")
    println("   - ExitScenario: Conditional logic")
    
    println("\n2. Injection Profiles:")
    println("   rampUsers(n).during(duration)")
    println("   constantUsersPerSec(rate).during(duration)")
    println("   rampUsersPerSec(from).to(to).during(duration)")
    println("   atOnceUsers(n)")
    
    println("\n3. Feeders:")
    println("   - CSV feeder: csv(\"data.csv\").circular")
    println("   - JSON feeder: json(\"data.json\")")
    println("   - Redis feeder")
    println("   - JDBC feeder")
    
    println("\n4. Checks:")
    println("   status.is(200)")
    println("   jsonPath(path).exists")
    println("   regex(pattern).count.gt(n)")
    println("   header(name).is(value)")
    
    println("\n5. Assertions:")
    println("   global.responseTime.percentile(95).lt(500)")
    println("   global.successfulRequests.percent.gt(99)")
    println("   forAll.responseTime.max.lt(1000)")
    
    println("\n6. Session API:")
    println("   session(\"key\")")
    println("   session.set(\"key\", value)")
    println("   session.saveAs(\"name\")")
    
    println("\n=== Lab Complete ===")
  }
}
```

### Build and Run Instructions

```bash
cd gatling-basics
mvn gatling:test

# Run specific simulation
mvn gatling:test -Dgatling.simulationClass=com.learning.gatling.BasicsSimulation
```

### Test Execution

```bash
# Run with report
mvn gatling:test -Dgatling.reportsDirectory=results

# Run simulation directly
gatling.sh -s com.learning.gatling.BasicsSimulation
```

## Project 2: Production Scenario Simulation

### Overview

This real-world project implements enterprise-scale scenario simulations with complex user journeys, data-driven testing, and comprehensive analytics.

### Project Structure

```
gatling-production/
├── pom.xml
├── data/
│   └── users.csv
└── src/
    └── test/
        └── scala/
            └── com/
                └── learning /
                    └── gatling/
                        ├── OrderServiceSimulation.scala
                        ├── OrderScenarios.scala
                        └── SessionFeeder.scala
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>gatling-production</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <gatling.version>3.9.5</gatling.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.gatling</groupId>
            <artifactId>gatling-core</artifactId>
            <version>${gatling.version}</version>
        </dependency>
        <dependency>
            <groupId>io.gatling</groupId>
            <artifactId>gatling-http</artifactId>
            <version>${gatling.version}</version>
        </dependency>
        <dependency>
            <groupId>io.gatling</groupId>
            <artifactId>gatling-recorder</artifactId>
            <version>${gatling.version}</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>io.gatling</groupId>
                <artifactId>gatling-maven-plugin</artifactId>
                <version>4.2.9</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### OrderServiceSimulation.scala

```scala
package com.learning.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class OrderServiceSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://api.example.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .wsBaseUrl("wss://ws.example.com")
    .userAgentHeader("Gatling/3.9.5")
    .shareConnections

  val userFeeder = csv("data/users.csv").random()
  val productFeeder = csv("data/products.csv").circular()

  val browseAndSearch = scenario("Browse and Search")
    .feed(userFeeder)
    .exec(
      http("Login")
        .post("/api/auth/login")
        .body(StringBody("""{"username":"${username}","password":"${password}"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("authToken"))
    )
    .exec(http("Get Products").get("/api/products")
      .check(status.is(200))
      .check(jsonPath("$.products").saveAs("products")))
    .pause(1, 3)
    .exec(http("Search").get("/api/products/search?q=laptop")
      .queryParam("category", "electronics")
      .header("Authorization", "Bearer ${authToken}")
      .check(status.is(200))))

  val addToCart = scenario("Add to Cart")
    .feed(productFeeder)
    .exec(
      http("Get Product").get("/api/products/${productId}")
        .check(status.is(200))
        .check(jsonPath("$.price").saveAs("price"))
        .check(jsonPath("$.stock").saveAs("stock"))
    )
    .doIf("${stock}".toInt > 0) {
      exec(
        http("Add to Cart").post("/api/cart")
          .header("Authorization", "Bearer ${authToken}")
          .body(StringBody(s"""{
            "productId": "${"$"}{productId}",
            "quantity": 1
          }"""))
          .check(status.is(201))
      )
    }

  val checkout = scenario("Full Checkout Flow")
    .exec(http("Get Cart").get("/api/cart")
      .header("Authorization", "Bearer ${authToken}")
      .check(status.is(200))
      .check(jsonPath("$.items").saveAs("cartItems")))
    .pause(2, 5)
    .exec(http("Validate Stock").post("/api/cart/validate")
      .header("Authorization", "Bearer ${authToken}")
      .check(jsonPath("$.available").is("true"))))
    .exec(http("Create Order").post("/api/orders")
      .header("Authorization", "Bearer ${authToken}")
      .body(StringBody("""{
        "shippingAddress": "123 Main St",
        "paymentMethod": "credit_card"
      }"""))
      .check(status.is(201))
      .check(jsonPath("$.orderId").saveAs("orderId"))
      .check(jsonPath("$.total").saveAs("orderTotal"))))
    .exec(http("Process Payment").post(s"/api/orders/${"$"}{orderId}/pay")
      .header("Authorization", "Bearer ${authToken}")
      .check(status.is(200))
      .check(jsonPath("$.paymentStatus").is("PAID"))))
    .exec(
      http("Order Confirmation").get(s"/api/orders/${"$"}{orderId}/confirmation")
        .header("Authorization", "Bearer ${authToken}")
    )

  val userWebSocket = scenario("Real-time Updates")
    .exec(ws("Connect").ws("/ws/updates")
      .onConnected(
        exec(ws("Send Subscribe")
          .sendText("""{"action":"subscribe","channel":"orders"}"""))
      ))
    )

  val steadyProfile = rampUsers(100).during(60.seconds)
  val spikeProfile = rampUsers(200).to(50).during(30.seconds)

  setUp(
    browseAndSearch.inject(steadyProfile),
    addToCart.inject(rampUsers(50).during(30.seconds)),
    checkout.inject(constantUsersPerSec(5).during(5.minutes)),
    userWebSocket.inject(constantUsersPerSec(10).during(10.minutes))
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.percentile(3).lt(500),
     global.successfulRequests.percent.gt(99.5),
     forAll.atLeast(1).requestsPerSec.gt(100)
   )
   .maxDuration(30.minutes)
}
```

### OrderScenarios.scala

```scala
package com.learning.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

object OrderScenarios {

  def browseProducts = scenario("Browse Products")
    .exec(
      http("Homepage")
        .get("/")
        .check(status.is(200))
    )
    .exec(
      http("Product List")
        .get("/api/products")
        .queryParam("page", "1")
        .queryParam("size", "20")
    )
    .pause(500.milliseconds, 2.seconds)
    .exec(
      http("Product Detail")
        .get("/api/products/${productId}")
    )
    .pause(300.milliseconds, 1.second)
    .exec(
      http("Related Products")
        .get(s"/api/products/${"$"}{productId}/related")
    )

  def searchProducts = scenario("Search")
    .exec(
      http("Search Products")
        .get("/api/products/search")
        .queryParam("q", "${searchTerm}")
        .queryParam("category", "${category}")
        .queryParam("minPrice", "${minPrice}")
        .queryParam("maxPrice", "${maxPrice}")
    )
    .pause(200.milliseconds)
    .exec(
      http("Filter").get("/api/products")
        .queryParam("sort", "price_asc")
    )

  def createOrder = scenario("Create Order")
    .exec(http("Add Item").post("/api/cart/items")
      .body(StringBody("""{
        "productId": "${productId}",
        "quantity": "${quantity}"
      }"""))
      .check(jsonPath("$.cartId").saveAs("cartId")))
    .exec(http("Update Quantity").put(s"/api/cart/${"$"}{cartId}/items/${"$"}{itemId}")
      .body(StringBody("""{"quantity":2}"""))
    )
    .exec(http("Remove Item").delete(s"/api/cart/${"$"}{cartId}/items/${"$"}{itemId}")
    )
    .exec(http("Checkout").post(s"/api/checkout/${"$"}{cartId}")
      .body(StringBody("""{
        "paymentMethod": "credit_card",
        "shippingAddress": "123 Main St, City, State 12345"
      }"""))
      .check(jsonPath("$.orderId").saveAs("orderId"))
      .check(jsonPath("$.total").saveAs("total"))
    )

  def cancelOrder = scenario("Cancel Order")
    .exec(http("Get Orders").get("/api/orders")
      .check(jsonPath("$[?(@.status=='PENDING')].orderId").saveAs("pendingOrderIds"))
    )
    .foreach("${pendingOrderIds}", "orderId") {
      exec(http("Cancel Order").delete(s"/api/orders/${"$"}{orderId}")
        .check(status.is(204))
      )
    }

  def orderHistory = scenario("Order History")
    .exec(http("Get Orders").get("/api/orders")
      .queryParam("status", "ALL")
      .queryParam("page", "0")
      .queryParam("size", "20")
    )
    .exec(http("Get Order Detail").get(s"/api/orders/${"$"}{orderId}")
    )
    .exec(http("Track Shipment").get(s"/api/orders/${"$"}{orderId}/tracking")
    )
    .exec(http("Get Invoice").get(s"/api/orders/${"$"}{orderId}/invoice")
      .check(status.is(200))
      .check(header("Content-Type").contains("application/pdf"))
    )
}
```

### SessionFeeder.scala

```scala
package com.learning.gatling

import io.gatling.core.Predef._
import io.gatling.core.feeder._
import scala.util.Random

object SessionFeeder {

  def userFeeder: FeederBuilder[String] = {
    Iterator.continually(Map(
      "userId" -> s"user-${Random.nextInt(10000)}",
      "username" -> s"testuser${Random.nextInt(10000)}",
      "email" -> s"user${Random.nextInt(10000)}@example.com",
      "role" -> Random.shuffle(List("admin", "user", "guest")).head
    ))
  }

  def productFeeder: FeederBuilder[String] = {
    val products = (1 to 100).map(i => Map(
      "productId" -> s"PROD-$i",
      "name" -> s"Product $i",
      "price" -> s"${Random.nextInt(1000)}",
      "category" -> Random.shuffle(List("electronics", "clothing", "books")).head
    ))
    Iterator.continually(products.toSeq(Random.nextInt(products.length)))
  }

  def batchFeeder(batchSize: Int): FeederBuilder[Array[String]] = {
    val batches = (1 to 1000).grouped(batchSize).map { batch =>
      Map("batch" -> batch.mkString(","))
    }
    Iterator.continually(batches.toSeq(Random.nextInt(batches.length)))
  }

  def jdbcFeeder(jdbcUrl: String, sql: String): FeederBuilder[Record] = {
    // Requires JDBC driver
    new RecordFeeder jdbcUrl, sql
  }

  def redisFeeder(host: String, port: Int, key: String): FeederBuilder[Record] = {
    // Requires Redis client
    new RecordFeeder s"redis://$host:$port", key
  }
}

class SessionFeeder {
  
  def createSession: Map[String, Any] = {
    Map(
      "sessionId" -> java.util.UUID.randomUUID.toString,
      "startTime" -> System.currentTimeMillis,
      "userId" -> s"user-${Random.nextInt(1000)}",
      "cartId" -> s"cart-${Random.nextInt(1000)}"
    )
  }
}
```

### users.csv (Test Data)

```csv
userId,username,password,email,role
1,alice,pass123,alice@example.com,user
2,bob,pass456,bob@example.com,admin
3,charlie,pass789,charlie@example.com,user
4,david,pass321,david@example.com,user
5,eve,pass654,eve@example.com,admin
```

### Build and Run Instructions

```bash
cd gatling-production
mvn clean gatling:test

# Run specific simulation
mvn gatling:test -Dgatling.simulationClass=com.learning.gatling.OrderServiceSimulation

# Generate HTML report
mvn gatling:report
```

### Viewing Results

```bash
# Open HTML report
ls target/gatling/

# View simulation log
cat target/gatling/logs/simulation.log
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic Gatling setup with scenarios, injection profiles, and assertions
2. **Production Project**: Complete enterprise-scale scenario simulation with data-driven testing, WebSocket support, and complex user flows

Gatling provides powerful Scala-based load testing with excellent reporting and integration capabilities.