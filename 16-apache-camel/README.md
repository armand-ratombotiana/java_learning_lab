# 🐫 Apache Camel - Enterprise Integration Patterns

<div align="center">

![Apache Camel](https://img.shields.io/badge/Apache_Camel-4.0+-red?style=for-the-badge&logo=apache-camel&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)

**Master Enterprise Integration Patterns with Apache Camel**

</div>

---

## 🎯 Overview

Apache Camel is a powerful open-source integration framework based on Enterprise Integration Patterns (EIP). It provides a rule-based routing and mediation engine with extensive component library for integrating various systems.

### Why Apache Camel?

- **200+ Components** - Connect to almost anything
- **EIP Implementation** - Industry-standard patterns
- **Multiple DSLs** - Java, XML, YAML, Groovy
- **Cloud-Native** - Kubernetes, Quarkus, Spring Boot
- **Battle-Tested** - Used by thousands of enterprises

---

## 📚 Module List

### Module 01: Camel Basics & Routes
**Duration:** 4-6 hours | **Difficulty:** 🟢 Beginner

Learn Camel fundamentals:
- Camel architecture and concepts
- Routes, endpoints, and processors
- Java DSL basics
- File and HTTP components
- Simple transformations

**Project:** File processing application

---

### Module 02: Enterprise Integration Patterns
**Duration:** 6-8 hours | **Difficulty:** 🟡 Intermediate

Master EIP patterns:
- Content-Based Router
- Message Filter
- Splitter and Aggregator
- Recipient List
- Wire Tap
- Dead Letter Channel

**Project:** Order processing system

---

### Module 03: Component Integration
**Duration:** 6-8 hours | **Difficulty:** 🟡 Intermediate

Integrate various systems:
- Database (JPA, JDBC)
- Message Brokers (Kafka, RabbitMQ)
- REST APIs
- FTP/SFTP
- Email (SMTP, IMAP)
- Cloud services (AWS, Azure)

**Project:** Multi-system integration hub

---

### Module 04: Error Handling & Retry
**Duration:** 4-6 hours | **Difficulty:** 🟡 Intermediate

Handle failures gracefully:
- Error handlers
- Retry policies
- Dead letter queues
- Exception handling
- Circuit breaker
- Compensation patterns

**Project:** Resilient message processor

---

### Module 05: Testing Camel Applications
**Duration:** 4-6 hours | **Difficulty:** 🟡 Intermediate

Test integration routes:
- Unit testing with CamelTestSupport
- Mock endpoints
- Advice with
- Integration testing
- Performance testing

**Project:** Fully tested integration suite

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Apache Camel                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐         │
│  │  Source  │───▶│  Route   │───▶│  Target  │         │
│  │Endpoint  │    │Processing│    │ Endpoint │         │
│  └──────────┘    └──────────┘    └──────────┘         │
│                                                          │
│  ┌────────────────────────────────────────────┐        │
│  │         Enterprise Integration             │        │
│  │              Patterns (EIP)                │        │
│  │  • Router  • Filter  • Splitter           │        │
│  │  • Aggregator  • Transformer  • Enricher  │        │
│  └────────────────────────────────────────────┘        │
│                                                          │
│  ┌────────────────────────────────────────────┐        │
│  │           200+ Components                  │        │
│  │  File • HTTP • JMS • Kafka • Database     │        │
│  │  FTP • Email • AWS • Azure • REST         │        │
│  └────────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

```bash
☕ Java 21+
📦 Maven 3.8+
🐳 Docker Desktop
🔧 IDE (IntelliJ IDEA recommended)
```

### Quick Start

```bash
# Navigate to module
cd 16-apache-camel/01-camel-basics

# Run with Maven
mvn clean install
mvn camel:run

# Or with Docker
docker-compose up
```

---

## 💻 Basic Example

### Simple File Route

```java
import org.apache.camel.builder.RouteBuilder;

public class FileRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("file:input?noop=true")
            .log("Processing file: ${header.CamelFileName}")
            .transform(body().append("\nProcessed by Camel"))
            .to("file:output");
    }
}
```

### REST API Integration

```java
public class RestRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("timer:fetch?period=60000")
            .to("rest:get:users")
            .unmarshal().json(JsonLibrary.Jackson, User[].class)
            .split(body())
            .to("jpa:com.learning.User")
            .end();
    }
}
```

### Message Transformation

```java
public class TransformRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("kafka:orders")
            .unmarshal().json(JsonLibrary.Jackson, Order.class)
            .process(exchange -> {
                Order order = exchange.getIn().getBody(Order.class);
                order.setStatus("PROCESSING");
                exchange.getIn().setBody(order);
            })
            .marshal().json()
            .to("kafka:processed-orders");
    }
}
```

---

## 🎯 Key Concepts

### 1. Routes
Routes define the flow of messages from source to destination.

```java
from("source-endpoint")
    .process(...)
    .transform(...)
    .to("target-endpoint");
```

### 2. Endpoints
Endpoints represent communication channels.

```
file:directory
http://api.example.com
kafka:topic-name
jms:queue:orders
```

### 3. Processors
Processors transform or manipulate messages.

```java
.process(exchange -> {
    Message message = exchange.getIn();
    // Process message
});
```

### 4. Components
Components provide connectivity to external systems.

- **file** - File system
- **http** - HTTP/HTTPS
- **kafka** - Apache Kafka
- **jms** - JMS messaging
- **sql** - Database queries

---

## 📊 Enterprise Integration Patterns

### Content-Based Router

```java
from("direct:orders")
    .choice()
        .when(simple("${body.amount} > 1000"))
            .to("direct:high-value")
        .when(simple("${body.amount} > 100"))
            .to("direct:medium-value")
        .otherwise()
            .to("direct:low-value")
    .end();
```

### Splitter & Aggregator

```java
from("file:orders")
    .unmarshal().json(JsonLibrary.Jackson, OrderBatch.class)
    .split(simple("${body.orders}"))
        .to("direct:process-order")
    .end()
    .aggregate(constant(true), new ArrayListAggregationStrategy())
        .completionSize(10)
        .to("direct:batch-complete");
```

### Wire Tap

```java
from("direct:orders")
    .wireTap("direct:audit")
    .to("direct:process");

from("direct:audit")
    .to("log:audit?level=INFO");
```

---

## 🧪 Testing

### Unit Test Example

```java
@Test
public void testFileRoute() throws Exception {
    MockEndpoint mock = getMockEndpoint("mock:result");
    mock.expectedMessageCount(1);
    
    template.sendBody("direct:start", "Test Message");
    
    assertMockEndpointsSatisfied();
}
```

### Integration Test

```java
@CamelSpringBootTest
@SpringBootTest
public class IntegrationTest {
    
    @Produce("direct:start")
    private ProducerTemplate producer;
    
    @EndpointInject("mock:result")
    private MockEndpoint mock;
    
    @Test
    public void testRoute() throws Exception {
        mock.expectedBodiesReceived("Expected Result");
        producer.sendBody("Input");
        mock.assertIsSatisfied();
    }
}
```

---

## 🔧 Configuration

### application.yml

```yaml
camel:
  springboot:
    name: CamelApp
    main-run-controller: true
  component:
    kafka:
      brokers: localhost:9092
    file:
      auto-create: true
```

### Route Configuration

```java
@Configuration
public class CamelConfig {
    
    @Bean
    public RouteBuilder myRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("timer:hello?period=5000")
                    .setBody(constant("Hello Camel!"))
                    .to("log:hello");
            }
        };
    }
}
```

---

## 📈 Performance Tips

1. **Use Direct Endpoints** for in-memory routing
2. **Enable Streaming** for large files
3. **Configure Thread Pools** appropriately
4. **Use Async Processing** when possible
5. **Monitor with JMX** for insights

---

## 🎓 Learning Path

```mermaid
graph LR
    A[Camel Basics] --> B[EIP Patterns]
    B --> C[Component Integration]
    C --> D[Error Handling]
    D --> E[Testing]
    E --> F[Production Ready]
    
    style A fill:#4CAF50
    style F fill:#FF9800
```

---

## 📖 Additional Resources

### Official Documentation
- [Apache Camel Documentation](https://camel.apache.org/manual/)
- [Component Reference](https://camel.apache.org/components/)
- [EIP Patterns](https://camel.apache.org/components/latest/eips/enterprise-integration-patterns.html)

### Books
- "Camel in Action" by Claus Ibsen
- "Enterprise Integration Patterns" by Gregor Hohpe

### Community
- [Apache Camel Users Mailing List](https://camel.apache.org/community/mailing-list/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/apache-camel)
- [GitHub](https://github.com/apache/camel)

---

## ✅ Module Progress

- [ ] Module 01: Camel Basics & Routes
- [ ] Module 02: Enterprise Integration Patterns
- [ ] Module 03: Component Integration
- [ ] Module 04: Error Handling & Retry
- [ ] Module 05: Testing Camel Applications

---

<div align="center">

**Ready to master Apache Camel?**

[Start with Module 01 →](./01-camel-basics)

**Integrate Everything!** 🐫

</div>