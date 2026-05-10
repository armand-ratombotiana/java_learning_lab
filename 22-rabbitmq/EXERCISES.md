# RabbitMQ - Exercises

---

## Exercise Set 1: RabbitMQ Fundamentals

### Exercise 1.1: Queue Declaration
**Task**: Create queues with various configurations.

```java
@Configuration
public class QueueConfig {
    
    // Simple durable queue
    @Bean
    public Queue simpleQueue() {
        return QueueBuilder.durable("simple.queue").build();
    }
    
    // Queue with TTL
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable("ttl.queue")
            .ttl(60000) // 1 minute
            .build();
    }
    
    // Exclusive auto-delete queue
    @Bean
    public Queue tempQueue() {
        return QueueBuilder.nonDurable()
            .exclusive()
            .autoDelete()
            .build();
    }
}
```

---

### Exercise 1.2: Exchange Declaration
**Task**: Create all exchange types.

```java
@Configuration
public class ExchangeConfig {
    
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct.exchange");
    }
    
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout.exchange");
    }
    
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic.exchange");
    }
    
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange("headers.exchange");
    }
}
```

---

### Exercise 1.3: Bindings
**Task**: Create various binding configurations.

```java
@Configuration
public class BindingConfig {
    
    // Direct binding with exact key
    @Bean
    public Binding directBinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
            .to(exchange)
            .with("order.created");
    }
    
    // Topic binding with pattern
    @Bean
    public Binding topicBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue)
            .to(exchange)
            .with("order.*"); // matches order.created, order.updated
    }
    
    // Headers binding
    @Bean
    public Binding headersBinding(Queue queue, HeadersExchange exchange) {
        return BindingBuilder.bind(queue)
            .to(exchange)
            .where("format").is("json")
            .where("version").exists();
    }
}
```

---

## Exercise Set 2: Producer Patterns

### Exercise 2.1: JSON Message Converter
**Task**: Configure Jackson for JSON serialization.

```java
@Configuration
public class MessageConfig {
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = 
            new Jackson2JsonMessageConverter();
        converter.setJavaTimeModule(new JavaTimeModule());
        return converter;
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, 
                                         MessageConverter mc) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(mc);
        return template;
    }
}
```

---

### Exercise 2.2: Publisher Confirms
**Task**: Implement publisher confirms for reliability.

```java
@Service
public class ReliableOrderProducer {
    
    private final RabbitTemplate template;
    
    public void sendOrder(Order order) {
        RabbitTemplate template = createConfirmTemplate();
        
        template.convertAndSend("order.exchange", "order.created", order,
            message -> {
                message.getMessageProperties()
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
        
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("Order confirmed: " + order.getId());
            } else {
                System.out.println("Order failed: " + cause);
                // Retry logic
            }
        });
    }
}
```

---

### Exercise 2.3: Mandatory and Returns
**Task**: Handle unroutable messages.

```java
@Configuration
public class ReturnHandlingConfig {
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMandatory(true);
        
        template.setReturnsCallback(returned -> {
            System.out.println("Message returned: " + returned.getMessage());
            System.out.println("Reply code: " + returned.getReplyCode());
            System.out.println("Reply text: " + returned.getReplyText());
            System.out.println("Exchange: " + returned.getExchange());
            System.out.println("Routing key: " + returned.getRoutingKey());
        });
        
        return template;
    }
}
```

---

## Exercise Set 3: Consumer Patterns

### Exercise 3.1: Manual Acknowledgment
**Task**: Implement manual acknowledgment with retry.

```java
@Service
public class OrderProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(OrderProcessor.class);
    
    @RabbitListener(queues = "order.queue", ackMode = Manual)
    public void processOrder(Message message, Channel channel, 
                            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            String body = new String(message.getBody());
            Order order = objectMapper.readValue(body, Order.class);
            
            // Process order
            processOrder(order);
            
            // Acknowledge successful processing
            channel.basicAck(tag, false);
            
        } catch (Exception e) {
            log.error("Failed to process order", e);
            // Reject and requeue for retry
            channel.basicNack(tag, false, true);
        }
    }
}
```

---

### Exercise 3.2: Prefetch and Concurrency
**Task**: Configure consumer for high throughput.

```java
@Bean
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory cf) {
    SimpleRabbitListenerContainerFactory factory = 
        new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(cf);
    
    // Process 10 messages at a time
    factory.setPrefetchCount(10);
    
    // 5 concurrent consumers
    factory.setConcurrentConsumers(5);
    factory.setMaxConcurrentConsumers(10);
    
    return factory;
}
```

---

### Exercise 3.3: Retry Configuration
**Task**: Implement retry with dead letter queue.

```java
@Configuration
public class RetryConfig {
    
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx.exchange");
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("order.dlq").build();
    }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(dlxExchange())
            .with("order.failed");
    }
    
    @Bean
    public Queue orderQueueWithDLQ() {
        return QueueBuilder.durable("order.queue")
            .withArgument("x-dead-letter-exchange", "dlx.exchange")
            .withArgument("x-dead-letter-routing-key", "order.failed")
            .build();
    }
    
    @Bean
    public RetryInterceptor retryInterceptor() {
        RetryTemplate template = new RetryTemplate();
        template.setMaxAttempts(3);
        template.setBackOffPolicy(new ExponentialBackOffPolicy());
        
        return RetryInterceptorBuilder.stateless()
            .retryOperations(template)
            .build();
    }
}
```

---

## Exercise Set 4: Routing Patterns

### Exercise 4.1: Topic Routing
**Task**: Implement log routing with severity levels.

```java
@Bean
public TopicExchange logExchange() {
    return new TopicExchange("log.exchange");
}

@Bean
public Queue errorQueue() {
    return QueueBuilder.durable("log.error").build();
}

@Bean
public Queue warningQueue() {
    return QueueBuilder.durable("log.warning").build();
}

@Bean
public Queue allLogsQueue() {
    return QueueBuilder.durable("log.all").build();
}

@Bean
public Binding errorBinding() {
    return BindingBuilder.bind(errorQueue())
        .to(logExchange())
        .with("*.error");  // matches service.error
}

@Bean
public Binding warningBinding() {
    return BindingBuilder.bind(warningQueue())
        .to(logExchange())
        .with("*.warning");
}

@Bean
public Binding allBinding() {
    return BindingBuilder.bind(allLogsQueue())
        .to(logExchange())
        .with("#");  // matches everything
}
```

---

### Exercise 4.2: Priority Queue
**Task**: Create priority queue for urgent messages.

```java
@Bean
public Queue priorityQueue() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-max-priority", 10);
    return new Queue("order.priority", true, false, false, args);
}

@Service
public class OrderProducer {
    
    public void sendUrgentOrder(Order order) {
        rabbitTemplate.convertAndSend("order.exchange", "order.priority", 
            order, m -> {
                m.getMessageProperties().setPriority(10);
                return m;
            });
    }
}
```

---

### Exercise 4.3: Delayed Messages
**Task**: Implement scheduled message processing.

```java
@Bean
public CustomExchange delayedExchange() {
    Map<String, Object> args = new HashMap<>();
    args.put("x-delayed-type", "direct");
    return new CustomExchange("delayed.exchange", "x-delayed-message", true, false, args);
}

@Service
public class ScheduledNotificationService {
    
    public void scheduleReminder(String userId, String message, 
                                 Duration delay) {
        rabbitTemplate.convertAndSend("delayed.exchange", 
            "notification.reminder",
            Map.of("userId", userId, "message", message),
            m -> {
                m.getMessageProperties().setHeader("x-delay", 
                    delay.toMillis());
                return m;
            });
    }
}
```

---

## Challenge Problems

### Challenge 1: Message Ordering
**Difficulty**: Advanced
**Task**: Ensure message ordering in distributed consumer.

Requirements:
- Maintain order per customer ID
- Handle concurrent processing
- Implement sequence numbers

---

### Challenge 2: Fanout with Filtering
**Difficulty**: Advanced
**Task**: Build notification system with user preferences.

Requirements:
- Broadcast to all users
- Filter based on preferences
- Respect user opt-out

---

### Challenge 3: Message Compression
**Difficulty**: Expert
**Task**: Implement compressed message transfer.

Requirements:
- Compress large messages
- Auto-decompress on consumer
- Monitor compression ratio

---

## Solutions Guidance

For each exercise:
1. Understand RabbitMQ semantics first
2. Implement the simplest working solution
3. Add reliability features incrementally
4. Test failure scenarios

---

## Time Estimates

| Exercise | Estimated Time |
|----------|---------------|
| Set 1 | 1-2 hours |
| Set 2 | 2-3 hours |
| Set 3 | 2-3 hours |
| Set 4 | 3-4 hours |
| Challenges | 6+ hours each |

