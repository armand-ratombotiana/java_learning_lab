# RabbitMQ Module - PROJECTS.md

---

# Mini-Project: Message-Driven Order Processing

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: RabbitMQ Exchanges, Queues, Bindings, Message Converters, Message Acknowledgment, Dead Letter Queues

This mini-project demonstrates RabbitMQ fundamentals with message-driven order processing system.

---

## Project Structure

```
22-rabbitmq/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   └── RabbitConfig.java
│   ├── producer/
│   │   └── OrderMessageProducer.java
│   ├── consumer/
│   │   ├── OrderConsumer.java
│   │   └── NotificationConsumer.java
│   ├── model/
│   │   ├── Order.java
│   │   └── Notification.java
│   └── service/
│       └── MessageService.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>rabbitmq-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Configuration

```java
// config/RabbitConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    public static final String ORDER_QUEUE = "order.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String ORDER_DLQ = "order.dlq";
    
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    
    public static final String ORDER_ROUTING_KEY = "order.created";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";
    
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", ORDER_DLQ)
            .build();
    }
    
    @Bean
    public Queue orderDLQ() {
        return QueueBuilder.durable(ORDER_DLQ).build();
    }
    
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }
    
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }
    
    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
```

---

## Step 3: Model Classes

```java
// model/Order.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private String orderId;
    private Long customerId;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    
    public Order() {}
    
    public Order(String orderId, Long customerId, String customerEmail, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.status = "CREATED";
        this.createdAt = LocalDateTime.now();
    }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

```java
// model/Notification.java
package com.learning.model;

import java.time.LocalDateTime;

public class Notification {
    private String notificationId;
    private String recipient;
    private String subject;
    private String message;
    private String type;
    private LocalDateTime timestamp;
    
    public Notification() {}
    
    public Notification(String recipient, String subject, String message, String type) {
        this.notificationId = "NOTIF-" + System.currentTimeMillis();
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

---

## Step 4: Producers and Consumers

```java
// producer/OrderMessageProducer.java
package com.learning.producer;

import com.learning.config.RabbitConfig;
import com.learning.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderMessageProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public OrderMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendOrder(Order order) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.ORDER_EXCHANGE,
            RabbitConfig.ORDER_ROUTING_KEY,
            order
        );
        System.out.println("Order sent: " + order.getOrderId());
    }
}
```

```java
// consumer/OrderConsumer.java
package com.learning.consumer;

import com.learning.config.RabbitConfig;
import com.learning.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    
    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void processOrder(Order order) {
        System.out.println("Processing order: " + order.getOrderId());
        System.out.println("Customer: " + order.getCustomerEmail());
        System.out.println("Amount: $" + order.getTotalAmount());
        
        order.setStatus("PROCESSED");
        System.out.println("Order status: " + order.getStatus());
    }
}
```

```java
// consumer/NotificationConsumer.java
package com.learning.consumer;

import com.learning.config.RabbitConfig;
import com.learning.model.Notification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    
    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void sendNotification(Notification notification) {
        System.out.println("Sending notification: " + notification.getNotificationId());
        System.out.println("To: " + notification.getRecipient());
        System.out.println("Subject: " + notification.getSubject());
        System.out.println("Message: " + notification.getMessage());
    }
}
```

---

## Step 5: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.Order;
import com.learning.model.Notification;
import com.learning.producer.OrderMessageProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;

@SpringBootApplication
public class Main {
    
    private final OrderMessageProducer orderMessageProducer;
    private final RabbitTemplate rabbitTemplate;
    
    public Main(OrderMessageProducer orderMessageProducer, RabbitTemplate rabbitTemplate) {
        this.orderMessageProducer = orderMessageProducer;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            Order order = new Order("ORD-001", 1L, "customer@example.com", new BigDecimal("299.99"));
            orderMessageProducer.sendOrder(order);
            
            Notification notification = new Notification(
                "customer@example.com",
                "Order Confirmation",
                "Your order has been placed",
                "EMAIL"
            );
            rabbitTemplate.convertAndSend("notification.exchange", "notification.send", notification);
            
            Thread.sleep(2000);
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
# Start RabbitMQ
docker run -p 5672:5672 -p 15672:15672 rabbitmq:3-management

cd 22-rabbitmq
mvn spring-boot:run
```

---

# Real-World Project: Enterprise Message Platform

---

## Project Overview

**Duration**: 12+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Complex Routing, Publisher Confirms, Consumer Ack, Delayed Messages, Message Priorities

This comprehensive project implements an enterprise messaging platform for e-commerce with multiple exchange types, delayed messages, and priority queues.

---

## Project Structure

```
22-rabbitmq/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   ├── consumer/
│   ├── producer/
│   └── service/
└── src/main/resources/
    └── application.yml
```

---

## Complete Implementation

```java
// config/AdvancedRabbitConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdvancedRabbitConfig {
    
    public static final String PRIORITY_QUEUE = "priority.queue";
    public static final String DELAYED_QUEUE = "delayed.queue";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    
    @Bean
    public Queue priorityQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);
        return new Queue(PRIORITY_QUEUE, true, false, false, args);
    }
    
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }
    
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delayed.exchange", "x-delayed-message", true, false, args);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

---

## Build Instructions

```bash
cd 22-rabbitmq
mvn clean compile
mvn spring-boot:run
```

This project demonstrates advanced RabbitMQ patterns including priority queues, delayed message exchanges, and publisher confirms.

---

# Production Patterns: Advanced Messaging

## Reliable Publishing with Confirms

```java
// config/ReliableRabbitConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReliableRabbitConfig {
    
    public static final String RELIABLE_ORDER_QUEUE = "reliable.order.queue";
    public static final String RELIABLE_ORDER_EXCHANGE = "reliable.order.exchange";
    public static final String RELIABLE_ORDER_DLX = "reliable.order.dlx";
    
    @Bean
    public Queue reliableOrderQueue() {
        return QueueBuilder.durable(RELIABLE_ORDER_QUEUE)
            .withArgument("x-dead-letter-exchange", RELIABLE_ORDER_DLX)
            .withArgument("x-dead-letter-routing-key", "order.failed")
            .withArgument("x-message-ttl", 300000) // 5 minutes
            .build();
    }
    
    @Bean
    public Queue reliableOrderDLQ() {
        return QueueBuilder.durable("reliable.order.dlq")
            .withArgument("x-message-ttl", 86400000) // 1 day
            .build();
    }
    
    @Bean
    public DirectExchange reliableOrderExchange() {
        return new DirectExchange(RELIABLE_ORDER_EXCHANGE);
    }
    
    @Bean
    public DirectExchange reliableOrderDLX() {
        return new DirectExchange(RELIABLE_ORDER_DLX);
    }
    
    @Bean
    public Binding reliableOrderBinding() {
        return BindingBuilder.bind(reliableOrderQueue())
            .to(reliableOrderExchange())
            .with("order.created");
    }
    
    @Bean
    public Binding reliableOrderDLQBinding() {
        return BindingBuilder.bind(reliableOrderDLQ())
            .to(reliableOrderDLX())
            .with("order.failed");
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setJavaTimeModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return converter;
    }
}
```

## Publisher Confirms Service

```java
// service/ReliableOrderProducer.java
package com.learning.service;

import com.learning.config.ReliableRabbitConfig;
import com.learning.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReliableOrderProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public ReliableOrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        configureConfirms();
    }
    
    private void configureConfirms() {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("Message confirmed: " + 
                    correlationData.getId());
            } else {
                System.out.println("Message NOT confirmed: " + cause);
                handleFailedDelivery(correlationData);
            }
        });
        
        rabbitTemplate.setReturnsCallback(returned -> {
            System.out.println("Message returned: " + returned.getMessage());
            System.out.println("Exchange: " + returned.getExchange());
            System.out.println("Routing Key: " + returned.getRoutingKey());
            handleUnroutableMessage(returned);
        });
    }
    
    public void sendOrderWithConfirmation(Order order) {
        rabbitTemplate.convertAndSend(
            ReliableRabbitConfig.RELIABLE_ORDER_EXCHANGE,
            "order.created",
            order,
            message -> {
                message.getMessageProperties()
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties()
                    .setCorrelationId(order.getOrderId());
                return message;
            });
        
        System.out.println("Order sent: " + order.getOrderId());
    }
    
    private void handleFailedDelivery(org.springframework.amqp.core.CorrelationData data) {
        System.out.println("Implement retry logic for: " + data.getId());
    }
    
    private void handleUnroutableMessage(org.springframework.amqp.core.ReturnedMessage m) {
        System.out.println("Unroutable message: " + m.getMessage());
    }
}
```

## Manual Ack Consumer with Retry

```java
// consumer/ReliableOrderConsumer.java
package com.learning.consumer;

import com.learning.config.ReliableRabbitConfig;
import com.learning.model.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class ReliableOrderConsumer {
    
    private static final int MAX_RETRIES = 3;
    
    @RabbitListener(queues = ReliableRabbitConfig.RELIABLE_ORDER_QUEUE,
                   ackMode = "MANUAL")
    public void processOrderWithRetry(Order order, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        try {
            System.out.println("Processing order: " + order.getOrderId());
            
            int retryCount = getRetryCount(order);
            
            if (retryCount < MAX_RETRIES) {
                processOrderWithPotentialFailure(order, retryCount);
            }
            
            channel.basicAck(deliveryTag, false);
            System.out.println("Order processed successfully: " + order.getOrderId());
            
        } catch (Exception e) {
            handleProcessingFailure(order, channel, deliveryTag, e);
        }
    }
    
    private void processOrderWithPotentialFailure(Order order, int retryCount) 
            throws Exception {
        System.out.println("Processing order attempt " + (retryCount + 1));
        
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated processing failure");
        }
    }
    
    private int getRetryCount(Order order) {
        return 0;
    }
    
    private void handleProcessingFailure(Order order, Channel channel,
            long deliveryTag, Exception e) {
        try {
            System.out.println("Failed to process order: " + order.getOrderId());
            System.out.println("Error: " + e.getMessage());
            
            channel.basicNack(deliveryTag, false, true);
            
        } catch (Exception ackError) {
            System.out.println("Failed to nack message: " + ackError.getMessage());
        }
    }
}
```

## Priority Queue Producer

```java
// service/PriorityOrderProducer.java
package com.learning.service;

import com.learning.model.Order;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PriorityOrderProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public PriorityOrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendOrderWithPriority(Order order, int priority) {
        rabbitTemplate.convertAndSend("order.priority", order, message -> {
            MessageProps props = message.getMessageProperties();
            props.setPriority(Math.min(priority, 10));
            props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            props.setHeader("orderType", order.getType());
            props.setTimestamp(new Date());
            return message;
        });
    }
    
    public void sendVIPOrder(Order order) {
        sendOrderWithPriority(order, 10);
    }
    
    public void sendStandardOrder(Order order) {
        sendOrderWithPriority(order, 1);
    }
    
    public void sendRushOrder(Order order) {
        sendOrderWithPriority(order, 5);
    }
}
```

## Delayed Message Exchange

```java
// config/DelayedMessageConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedMessageConfig {
    
    public static final String DELAYED_EXCHANGE = "delayed.exchange";
    public static final String DELAYED_NOTIFICATION_QUEUE = "delayed.notification.queue";
    public static final String SCHEDULED_RETRY_QUEUE = "scheduled.retry.queue";
    
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", 
            true, false, args);
    }
    
    @Bean
    public Queue delayedNotificationQueue() {
        return QueueBuilder.durable(DELAYED_NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Queue scheduledRetryQueue() {
        return QueueBuilder.durable(SCHEDULED_RETRY_QUEUE)
            .withArgument("x-dead-letter-exchange", DELAYED_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", "notification.send")
            .build();
    }
    
    @Bean
    public Binding delayedNotificationBinding() {
        return BindingBuilder.bind(delayedNotificationQueue())
            .to(delayedExchange())
            .with("notification.send")
            .noargs();
    }
    
    @Bean
    public Binding scheduledRetryBinding() {
        return BindingBuilder.bind(scheduledRetryQueue())
            .to(delayedExchange())
            .with("retry.send")
            .noargs();
    }
}
```

## Scheduled Notification Service

```java
// service/ScheduledNotificationService.java
package com.learning.service;

import com.learning.config.DelayedMessageConfig;
import com.learning.model.Notification;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ScheduledNotificationService {
    
    private final RabbitTemplate rabbitTemplate;
    
    public ScheduledNotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendDelayedNotification(Notification notification, Duration delay) {
        long delayMillis = delay.toMillis();
        
        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setHeader("x-delay", delayMillis);
            return message;
        };
        
        rabbitTemplate.convertAndSend(
            DelayedMessageConfig.DELAYED_EXCHANGE,
            "notification.send",
            notification,
            messagePostProcessor);
        
        System.out.println("Scheduled notification for " + delay + " later");
    }
    
    public void sendReminder(String userId, String message, int minutesFromNow) {
        Notification notification = new Notification(
            userId,
            "Reminder",
            message,
            "REMINDER"
        );
        
        sendDelayedNotification(notification, Duration.ofMinutes(minutesFromNow));
    }
    
    public void scheduleRetry(Object message, int secondsDelay) {
        MessagePostProcessor messagePostProcessor = message1 -> {
            message1.getMessageProperties().setHeader("x-delay", secondsDelay * 1000L);
            message1.getMessageProperties().setHeader("retry-attempt", 
                getRetryCount(message) + 1);
            return message1;
        };
        
        rabbitTemplate.convertAndSend(
            DelayedMessageConfig.DELAYED_EXCHANGE,
            "retry.send",
            message,
            messagePostProcessor);
    }
    
    private int getRetryCount(Object message) {
        return 0;
    }
}
```

## Fanout for Event Broadcasting

```java
// config/EventBroadcastConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBroadcastConfig {
    
    public static final String EVENT_EXCHANGE = "event.fanout";
    public static final String EMAIL_NOTIFICATION_QUEUE = "email.notification.queue";
    public static final String SMS_NOTIFICATION_QUEUE = "sms.notification.queue";
    public static final String PUSH_NOTIFICATION_QUEUE = "push.notification.queue";
    
    @Bean
    public FanoutExchange eventExchange() {
        return new FanoutExchange(EVENT_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(PUSH_NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(eventExchange());
    }
    
    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(eventExchange());
    }
    
    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue()).to(eventExchange());
    }
}
```

## Event Broadcasting Service

```java
// service/EventBroadcastService.java
package com.learning.service;

import com.learning.config.EventBroadcastConfig;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventBroadcastService {
    
    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange eventExchange;
    
    public EventBroadcastService(RabbitTemplate rabbitTemplate, 
                                  FanoutExchange eventExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventExchange = eventExchange;
    }
    
    public void broadcastOrderEvent(OrderEvent event) {
        rabbitTemplate.convertAndSend(eventExchange, "", event);
        System.out.println("Event broadcasted: " + event.getType());
    }
    
    public void broadcastCustomerEvent(CustomerEvent event) {
        rabbitTemplate.convertAndSend(eventExchange, "", event);
        System.out.println("Event broadcasted: " + event.getType());
    }
    
    public record OrderEvent(String orderId, String type, String status) {}
    public record CustomerEvent(String customerId, String type, String action) {}
}
```

## Topic Routing for Multi-Criteria Routing

```java
// config/TopicRoutingConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRoutingConfig {
    
    public static final String TOPIC_EXCHANGE = "order.topic.exchange";
    
    public static final String URGENT_QUEUE = "order.urgent.queue";
    public static final String INTERNATIONAL_QUEUE = "order.international.queue";
    public static final String BULK_QUEUE = "order.bulk.queue";
    public static final String ALL_ORDERS_QUEUE = "order.all.queue";
    
    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue urgentQueue() {
        return QueueBuilder.durable(URGENT_QUEUE).build();
    }
    
    @Bean
    public Queue internationalQueue() {
        return QueueBuilder.durable(INTERNATIONAL_QUEUE).build();
    }
    
    @Bean
    public Queue bulkQueue() {
        return QueueBuilder.durable(BULK_QUEUE).build();
    }
    
    @Bean
    public Queue allOrdersQueue() {
        return QueueBuilder.durable(ALL_ORDERS_QUEUE).build();
    }
    
    @Bean
    public Binding urgentBinding() {
        return BindingBuilder.bind(urgentQueue())
            .to(orderTopicExchange())
            .with("order.urgent.*");
    }
    
    @Bean
    public Binding internationalBinding() {
        return BindingBuilder.bind(internationalQueue())
            .to(orderTopicExchange())
            .with("order.#.international.*");
    }
    
    @Bean
    public Binding bulkBinding() {
        return BindingBuilder.bind(bulkQueue())
            .to(orderTopicExchange())
            .with("order.bulk.*");
    }
    
    @Bean
    public Binding allOrdersBinding() {
        return BindingBuilder.bind(allOrdersQueue())
            .to(orderTopicExchange())
            .with("#");
    }
}
```

## Message Transformation with Converter

```java
// config/MessageTransformationConfig.java
package com.learning.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageTransformationConfig {
    
    @Bean
    public ContentTypeDelegatingMessageConverter compositeConverter() {
        ContentTypeDelegatingMessageConverter converter = 
            new ContentTypeDelegatingMessageConverter();
        
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setJavaTimeModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        converter.addDelegate("application/json", jsonConverter);
        converter.addDelegate("application/xml", new SimpleMessageConverter());
        converter.setDefaultDelegate(jsonConverter);
        
        return converter;
    }
}
```

## Dead Letter Queue Consumer with Reprocessing

```java
// consumer/DeadLetterQueueConsumer.java
package com.learning.consumer;

import com.learning.config.ReliableRabbitConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueConsumer {
    
    private final ReliableOrderProducer producer;
    
    public DeadLetterQueueConsumer(ReliableOrderProducer producer) {
        this.producer = producer;
    }
    
    @RabbitListener(queues = "reliable.order.dlq")
    public void processFailedOrders(Message message, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        try {
            String originalMessage = new String(message.getBody());
            String failedRoutingKey = message.getMessageProperties()
                .getHeader("x-first-death-queue");
            
            int deathCount = message.getMessageProperties()
                .getHeader("x-death-count");
            
            System.out.println("Processing failed message, death count: " + deathCount);
            System.out.println("Original queue: " + failedRoutingKey);
            
            if (deathCount < 5) {
                reprocessMessage(originalMessage);
                System.out.println("Message requeued for retry");
            } else {
                archiveFailedMessage(originalMessage);
                System.out.println("Message archived after max retries");
            }
            
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            throw new RuntimeException("DLQ processing failed", e);
        }
    }
    
    private void reprocessMessage(String message) {
    }
    
    private void archiveFailedMessage(String message) {
    }
}
```