# Quick Reference: RabbitMQ

<div align="center">

![Module](https://img.shields.io/badge/Module-22-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-RabbitMQ-green?style=for-the-badge)

**Quick lookup guide for RabbitMQ message patterns**

</div>

---

## 📋 Core Concepts

### Exchange Types
| Type | Routing |
|------|----------|
| **Direct** | Exact match on routing key |
| **Fanout** | Broadcast to all queues |
| **Topic** | Wildcard matching |
| **Headers** | Attribute-based matching |

### Message Patterns
| Pattern | Description |
|---------|-------------|
| **Point-to-Point** | One consumer, one producer |
| **Pub/Sub** | Multiple consumers |
| **Work Queue** | Distribute among consumers |
| **Routing** | Selective delivery |

---

## 🔑 Key Concepts

### Basic Publish
```java
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("localhost");
Connection connection = factory.newConnection();
Channel channel = connection.createChannel();

channel.exchangeDeclare("exchange", "direct", true);
channel.basicPublish("exchange", "routing.key", 
    MessageProperties.PERSISTENT_TEXT_PLAIN, 
    "message".getBytes());
```

### Basic Consume
```java
channel.queueDeclare("queue", true, false, false, null);
channel.basicConsume("queue", true, (msg, delivery) -> {
    System.out.println(new String(msg.getBody()));
}, consumerTag -> {});
```

### Work Queues (Fair Dispatch)
```java
channel.basicQos(1);  // One message at a time
channel.basicConsume("queue", false, (msg, delivery) -> {
    try {
        process(msg);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    } catch (Exception e) {
        channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);
    }
});
```

### Pub/Sub (Fanout)
```java
// Publisher
channel.exchangeDeclare("logs", "fanout");
channel.basicPublish("logs", "", null, message.getBytes());

// Consumer
channel.exchangeDeclare("logs", "fanout");
String queue = channel.queueDeclare().getQueue();
channel.queueBind(queue, "logs", "");
channel.basicConsume(queue, true, callback);
```

### Routing (Direct)
```java
// Publisher - send to specific severity
channel.exchangeDeclare("direct_logs", "direct");
channel.basicPublish("direct_logs", "error", null, errorMsg.getBytes());

// Consumer - bind to specific severity
channel.queueBind("error_queue", "direct_logs", "error");
```

### Topics (Wildcard)
```java
// Routing keys: "kern.messages", "critical.errors", "auth.notice"
channel.exchangeDeclare("topic_logs", "topic");
channel.queueBind("queue", "topic_logs", "*.messages");

// Patterns: * (one word), # (zero or more words)
```

### Headers Exchange
```java
Map<String, Object> headers = new HashMap<>();
headers.put("format", "pdf");
headers.put("type", "report");
channel.basicPublish("headers_exchange", "", 
    new AMQP.BasicProperties.Builder().headers(headers).build(), 
    body);
```

---

## 💻 Spring AMQP

### Configuration
```java
@Configuration
public class RabbitConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        return factory;
    }
}
```

### Producer
```java
@Component
public class Producer {
    @Autowired
    private RabbitTemplate template;
    
    public void send(String msg) {
        template.convertAndSend("exchange", "key", msg);
    }
}
```

### Consumer
```java
@Component
public class Consumer {
    @RabbitListener(queues = "queue")
    public void listen(String msg) {
        System.out.println(msg);
    }
}
```

---

## 📊 Advanced Patterns

### RPC Pattern
```java
// Client
String correlationId = UUID.randomUUID().toString();
template.convertAndSend("rpc_queue", msg, 
    m -> { m.setReplyTo("reply_queue"); 
           m.setCorrelationId(correlationId); return m; });

// Server
@RabbitListener(queues = "rpc_queue")
public String handle(String request) {
    return process(request);
}
```

### Delayed Message
```java
// Using RabbitMQ Delayed Message Plugin
Map<String, Object> props = new HashMap<>();
props.put("x-delay", 5000);  // 5 seconds delay
template.convertAndSend("exchange", "key", msg, m -> {
    m.getMessageProperties().setHeaders(props);
    return m;
});
```

### Dead Letter Queue
```java
Map<String, Object> args = new HashMap<>();
args.put("x-dead-letter-exchange", "dlx.exchange");
args.put("x-dead-letter-routing-key", "dlq.key");
channel.queueDeclare("main.queue", true, false, false, args);
```

---

## ✅ Best Practices

- Use message persistence for durability
- Set prefetch count for fair dispatch
- Implement dead letter queues
- Use correlation IDs for RPC
- Handle exceptions with nack/requeue

### ❌ DON'T
- Don't send sensitive data unencrypted
- Don't forget to close connections
- Don't ignore message acknowledgement

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>