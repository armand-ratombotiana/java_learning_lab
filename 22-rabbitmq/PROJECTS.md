# RabbitMQ Module - PROJECTS.md

---

# Mini-Project 1: Queue Creation (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Queues, Declarations, Queue Properties, Dead Letter Queues, TTL, Priority

This mini-project focuses on creating and configuring RabbitMQ queues with various options.

---

## Project Structure

```
22-rabbitmq/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── QueueConfig.java
│   │   └── DurableQueueConfig.java
│   ├── producer/
│   │   └── MessageProducer.java
│   └── consumer/
│       └── MessageConsumer.java
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
    <artifactId>rabbitmq-queue-demo</artifactId>
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
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Queue Configuration

```java
// config/QueueConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueConfig {
    
    // Queue names
    public static final String BASIC_QUEUE = "basic.queue";
    public static final String WORK_QUEUE = "work.queue";
    public static final String PRIORITY_QUEUE = "priority.queue";
    public static final String TTL_QUEUE = "ttl.queue";
    public static final String AUTO_DELETE_QUEUE = "auto-delete.queue";
    
    // Exchange name
    public static final String DIRECT_EXCHANGE = "queue.demo.exchange";
    
    // Routing keys
    public static final String BASIC_ROUTING_KEY = "queue.basic";
    public static final String WORK_ROUTING_KEY = "queue.work";
    
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue basicQueue() {
        return QueueBuilder.durable(BASIC_QUEUE)
            .build();
    }
    
    @Bean
    public Queue workQueue() {
        return QueueBuilder.durable(WORK_QUEUE)
            .exclusive(false)
            .autoDelete(false)
            .build();
    }
    
    @Bean
    public Queue priorityQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);
        return new Queue(PRIORITY_QUEUE, true, false, false, args);
    }
    
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable(TTL_QUEUE)
            .ttl(30000)
            .build();
    }
    
    @Bean
    public Queue autoDeleteQueue() {
        return QueueBuilder.durable(AUTO_DELETE_QUEUE)
            .autoDelete(true)
            .build();
    }
    
    @Bean
    public Binding basicBinding() {
        return BindingBuilder.bind(basicQueue())
            .to(directExchange())
            .with(BASIC_ROUTING_KEY);
    }
    
    @Bean
    public Binding workBinding() {
        return BindingBuilder.bind(workQueue())
            .to(directExchange())
            .with(WORK_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
```

---

## Step 3: Durable Queue Configuration

```java
// config/DurableQueueConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DurableQueueConfig {
    
    public static final String DURABLE_QUEUE = "durable.queue";
    public static final String DLQ = "durable.queue.dlq";
    public static final String DLX = "durable.queue.dlx";
    public static final String DURABLE_EXCHANGE = "durable.exchange";
    
    @Bean
    public DirectExchange durableExchange() {
        return new DirectExchange(DURABLE_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue durableQueue() {
        return QueueBuilder.durable(DURABLE_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX)
            .withArgument("x-dead-letter-routing-key", "dlq.route")
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }
    
    @Bean
    public Binding durableBinding() {
        return BindingBuilder.bind(durableQueue())
            .to(durableExchange())
            .with("durable.route");
    }
    
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("dlq.route");
    }
}
```

---

## Step 4: Message Producer

```java
// producer/MessageProducer.java
package com.learning.producer;

import com.learning.config.DurableQueueConfig;
import com.learning.config.QueueConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class MessageProducer {
    
    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();
    
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendBasicMessage(String message) {
        rabbitTemplate.convertAndSend(
            QueueConfig.DIRECT_EXCHANGE,
            QueueConfig.BASIC_ROUTING_KEY,
            message
        );
        System.out.println("Sent to basic queue: " + message);
    }
    
    public void sendWorkMessage(String message) {
        rabbitTemplate.convertAndSend(
            QueueConfig.DIRECT_EXCHANGE,
            QueueConfig.WORK_ROUTING_KEY,
            message
        );
        System.out.println("Sent to work queue: " + message);
    }
    
    public void sendPriorityMessage(String message, int priority) {
        rabbitTemplate.convertAndSend(QueueConfig.PRIORITY_QUEUE, message, m -> {
            MessageProps props = m.getMessageProperties();
            props.setPriority(Math.min(priority, 10));
            props.setTimestamp(new Date());
            return m;
        });
        System.out.println("Sent to priority queue with priority " + priority + ": " + message);
    }
    
    public void sendWithTTL(String message, int ttlMillis) {
        rabbitTemplate.convertAndSend(QueueConfig.TTL_QUEUE, message, m -> {
            MessageProps props = m.getMessageProperties();
            props.setExpiration(String.valueOf(ttlMillis));
            return m;
        });
        System.out.println("Sent to TTL queue with expiration " + ttlMillis + "ms: " + message);
    }
    
    public void sendDurableMessage(String message) {
        rabbitTemplate.convertAndSend(
            DurableQueueConfig.DURABLE_EXCHANGE,
            "durable.route",
            message
        );
        System.out.println("Sent durable message: " + message);
    }
    
    public void sendMultipleMessages(int count) {
        for (int i = 0; i < count; i++) {
            String msg = "Message-" + i;
            rabbitTemplate.convertAndSend(
                QueueConfig.DIRECT_EXCHANGE,
                QueueConfig.BASIC_ROUTING_KEY,
                msg
            );
        }
        System.out.println("Sent " + count + " messages to basic queue");
    }
}
```

---

## Step 5: Message Consumer

```java
// consumer/MessageConsumer.java
package com.learning.consumer;

import com.learning.config.DurableQueueConfig;
import com.learning.config.QueueConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    
    private int basicCount = 0;
    private int workCount = 0;
    private int priorityCount = 0;
    private int ttlCount = 0;
    private int durableCount = 0;
    private int dlqCount = 0;
    
    @RabbitListener(queues = QueueConfig.BASIC_QUEUE)
    public void consumeBasicMessage(String message) {
        basicCount++;
        System.out.println("[Basic Queue] Message #" + basicCount + ": " + message);
    }
    
    @RabbitListener(queues = QueueConfig.WORK_QUEUE)
    public void consumeWorkMessage(String message) {
        workCount++;
        System.out.println("[Work Queue] Message #" + workCount + ": " + message);
    }
    
    @RabbitListener(queues = QueueConfig.PRIORITY_QUEUE)
    public void consumePriorityMessage(String message) {
        priorityCount++;
        System.out.println("[Priority Queue] Message #" + priorityCount + ": " + message);
    }
    
    @RabbitListener(queues = QueueConfig.TTL_QUEUE)
    public void consumeTTLMessage(String message) {
        ttlCount++;
        System.out.println("[TTL Queue] Message #" + ttlCount + ": " + message);
    }
    
    @RabbitListener(queues = QueueConfig.AUTO_DELETE_QUEUE)
    public void consumeAutoDeleteMessage(String message) {
        System.out.println("[Auto-Delete Queue] " + message);
    }
    
    @RabbitListener(queues = DurableQueueConfig.DURABLE_QUEUE)
    public void consumeDurableMessage(String message) {
        durableCount++;
        System.out.println("[Durable Queue] Message #" + durableCount + ": " + message);
        
        if (durableCount % 5 == 0) {
            throw new RuntimeException("Simulated failure for message: " + message);
        }
    }
    
    @RabbitListener(queues = DurableQueueConfig.DLQ)
    public void consumeDeadLetterMessage(String message) {
        dlqCount++;
        System.out.println("[Dead Letter Queue] Failed message #" + dlqCount + ": " + message);
    }
    
    public int getBasicCount() { return basicCount; }
    public int getWorkCount() { return workCount; }
    public int getPriorityCount() { return priorityCount; }
    public int getTtlCount() { return ttlCount; }
    public int getDurableCount() { return durableCount; }
    public int getDlqCount() { return dlqCount; }
}
```

---

## Step 6: Main Application

```java
// Main.java
package com.learning;

import com.learning.consumer.MessageConsumer;
import com.learning.producer.MessageProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    
    private final MessageProducer messageProducer;
    private final MessageConsumer messageConsumer;
    
    public Main(MessageProducer messageProducer, MessageConsumer messageConsumer) {
        this.messageProducer = messageProducer;
        this.messageConsumer = messageConsumer;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== RabbitMQ Queue Creation Demo ===\n");
            
            System.out.println("--- Sending basic messages ---");
            messageProducer.sendBasicMessage("Hello from queue demo!");
            messageProducer.sendBasicMessage("Another basic message");
            
            System.out.println("\n--- Sending work messages ---");
            for (int i = 1; i <= 5; i++) {
                messageProducer.sendWorkMessage("Work task #" + i);
            }
            
            System.out.println("\n--- Sending priority messages ---");
            messageProducer.sendPriorityMessage("Low priority", 1);
            messageProducer.sendPriorityMessage("Medium priority", 5);
            messageProducer.sendPriorityMessage("High priority", 10);
            
            System.out.println("\n--- Sending TTL messages ---");
            messageProducer.sendWithTTL("Short TTL", 5000);
            messageProducer.sendWithTTL("Long TTL", 15000);
            
            System.out.println("\n--- Sending durable messages ---");
            for (int i = 1; i <= 10; i++) {
                messageProducer.sendDurableMessage("Durable message #" + i);
                Thread.sleep(100);
            }
            
            Thread.sleep(5000);
            
            System.out.println("\n=== Message Counts ===");
            System.out.println("Basic: " + messageConsumer.getBasicCount());
            System.out.println("Work: " + messageConsumer.getWorkCount());
            System.out.println("Priority: " + messageConsumer.getPriorityCount());
            System.out.println("TTL: " + messageConsumer.getTtlCount());
            System.out.println("Durable: " + messageConsumer.getDurableCount());
            System.out.println("DLQ: " + messageConsumer.getDlqCount());
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Step 7: Application Properties

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: rabbitmq-queue-demo
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    listener:
      simple:
        acknowledge-mode: auto
        prefetch: 10
```

---

## Build Instructions

```bash
# Start RabbitMQ
docker run -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Access management UI at http://localhost:15672

cd 22-rabbitmq
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 2: Exchange Types (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Direct Exchange, Fanout Exchange, Topic Exchange, Headers Exchange, Routing

This mini-project focuses on implementing different RabbitMQ exchange types.

---

## Project Structure

```
22-rabbitmq/
├── src/main/java/com/learning/
│   ├── config/
│   │   ├── DirectExchangeConfig.java
│   │   ├── FanoutExchangeConfig.java
│   │   ├── TopicExchangeConfig.java
│   │   └── HeadersExchangeConfig.java
│   ├── producer/
│   │   └── ExchangeProducer.java
│   └── consumer/
│       └── ExchangeConsumer.java
```

---

## Step 1: Direct Exchange Configuration

```java
// config/DirectExchangeConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectExchangeConfig {
    
    public static final String DIRECT_EXCHANGE = "direct.exchange.demo";
    public static final String NORMAL_QUEUE = "normal.queue";
    public static final String EXPRESS_QUEUE = "express.queue";
    public static final String BULK_QUEUE = "bulk.queue";
    
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable(NORMAL_QUEUE).build();
    }
    
    @Bean
    public Queue expressQueue() {
        return QueueBuilder.durable(EXPRESS_QUEUE).build();
    }
    
    @Bean
    public Queue bulkQueue() {
        return QueueBuilder.durable(BULK_QUEUE).build();
    }
    
    @Bean
    public Binding normalBinding() {
        return BindingBuilder.bind(normalQueue())
            .to(directExchange())
            .with("shipment.normal");
    }
    
    @Bean
    public Binding expressBinding() {
        return BindingBuilder.bind(expressQueue())
            .to(directExchange())
            .with("shipment.express");
    }
    
    @Bean
    public Binding bulkBinding() {
        return BindingBuilder.bind(bulkQueue())
            .to(directExchange())
            .with("shipment.bulk");
    }
}
```

---

## Step 2: Fanout Exchange Configuration

```java
// config/FanoutExchangeConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutExchangeConfig {
    
    public static final String FANOUT_EXCHANGE = "fanout.exchange.demo";
    public static final String EMAIL_QUEUE = "email.notification.queue";
    public static final String SMS_QUEUE = "sms.notification.queue";
    public static final String PUSH_QUEUE = "push.notification.queue";
    public static final String LOG_QUEUE = "log.queue";
    
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }
    
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE).build();
    }
    
    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(PUSH_QUEUE).build();
    }
    
    @Bean
    public Queue logQueue() {
        return QueueBuilder.durable(LOG_QUEUE).build();
    }
    
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(fanoutExchange());
    }
    
    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(fanoutExchange());
    }
    
    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue()).to(fanoutExchange());
    }
    
    @Bean
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(fanoutExchange());
    }
}
```

---

## Step 3: Topic Exchange Configuration

```java
// config/TopicExchangeConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicExchangeConfig {
    
    public static final String TOPIC_EXCHANGE = "topic.exchange.demo";
    public static final String ORDER_QUEUE = "order.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String SHIPPING_QUEUE = "shipping.queue";
    public static final String ALL_NOTIFICATIONS_QUEUE = "all.notifications.queue";
    
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }
    
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }
    
    @Bean
    public Queue shippingQueue() {
        return QueueBuilder.durable(SHIPPING_QUEUE).build();
    }
    
    @Bean
    public Queue allNotificationsQueue() {
        return QueueBuilder.durable(ALL_NOTIFICATIONS_QUEUE).build();
    }
    
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
            .to(topicExchange())
            .with("order.*");
    }
    
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue())
            .to(topicExchange())
            .with("payment.*");
    }
    
    @Bean
    public Binding shippingBinding() {
        return BindingBuilder.bind(shippingQueue())
            .to(topicExchange())
            .with("shipping.*");
    }
    
    @Bean
    public Binding allNotificationsBinding() {
        return BindingBuilder.bind(allNotificationsQueue())
            .to(topicExchange())
            .with("#");
    }
}
```

---

## Step 4: Headers Exchange Configuration

```java
// config/HeadersExchangeConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HeadersExchangeConfig {
    
    public static final String HEADERS_EXCHANGE = "headers.exchange.demo";
    public static final String VIDEO_QUEUE = "video.processing.queue";
    public static final String AUDIO_QUEUE = "audio.processing.queue";
    public static final String IMAGE_QUEUE = "image.processing.queue";
    public static final String HIGH_PRIORITY_QUEUE = "high.priority.queue";
    
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue videoQueue() {
        return QueueBuilder.durable(VIDEO_QUEUE).build();
    }
    
    @Bean
    public Queue audioQueue() {
        return QueueBuilder.durable(AUDIO_QUEUE).build();
    }
    
    @Bean
    public Queue imageQueue() {
        return QueueBuilder.durable(IMAGE_QUEUE).build();
    }
    
    @Bean
    public Queue highPriorityQueue() {
        return QueueBuilder.durable(HIGH_PRIORITY_QUEUE).build();
    }
    
    @Bean
    public Binding videoBinding() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "video/*");
        headers.put("encoding", null);
        
        return BindingBuilder.bind(videoQueue())
            .to(headersExchange())
            .whereAll(headers)
            .match();
    }
    
    @Bean
    public Binding audioBinding() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "audio/*");
        
        return BindingBuilder.bind(audioQueue())
            .to(headersExchange())
            .where("content-type").exists()
            .and("encoding").exists()
            .match();
    }
    
    @Bean
    public Binding imageBinding() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "image/*");
        
        return BindingBuilder.bind(imageQueue())
            .to(headersExchange())
            .whereAny(headers.keySet())
            .match();
    }
    
    @Bean
    public Binding highPriorityBinding() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("priority", "high");
        
        return BindingBuilder.bind(highPriorityQueue())
            .to(headersExchange())
            .whereAll(headers)
            .match();
    }
}
```

---

## Step 5: Exchange Producer

```java
// producer/ExchangeProducer.java
package com.learning.producer;

import com.learning.config.DirectExchangeConfig;
import com.learning.config.FanoutExchangeConfig;
import com.learning.config.TopicExchangeConfig;
import com.learning.config.HeadersExchangeConfig;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public ExchangeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendDirectMessage(String routingKey, String message) {
        rabbitTemplate.convertAndSend(
            DirectExchangeConfig.DIRECT_EXCHANGE,
            routingKey,
            message
        );
        System.out.println("[Direct] Sent to " + routingKey + ": " + message);
    }
    
    public void sendFanoutMessage(String message) {
        rabbitTemplate.convertAndSend(
            FanoutExchangeConfig.FANOUT_EXCHANGE,
            "",
            message
        );
        System.out.println("[Fanout] Broadcasted: " + message);
    }
    
    public void sendTopicMessage(String routingKey, String message) {
        rabbitTemplate.convertAndSend(
            TopicExchangeConfig.TOPIC_EXCHANGE,
            routingKey,
            message
        );
        System.out.println("[Topic] Sent to " + routingKey + ": " + message);
    }
    
    public void sendHeadersMessage(Map<String, Object> headers, String message) {
        rabbitTemplate.convertAndSend(
            HeadersExchangeConfig.HEADERS_EXCHANGE,
            "",
            message,
            m -> {
                m.getMessageProperties().getHeaders().putAll(headers);
                return m;
            }
        );
        System.out.println("[Headers] Sent with headers " + headers + ": " + message);
    }
    
    public void sendVideoMessage(String message, String encoding) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "video/mp4");
        headers.put("encoding", encoding);
        headers.put("priority", "normal");
        
        sendHeadersMessage(headers, message);
    }
    
    public void sendHighPriorityMessage(String message) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "application/octet-stream");
        headers.put("priority", "high");
        
        sendHeadersMessage(headers, message);
    }
}
```

---

## Step 6: Exchange Consumer

```java
// consumer/ExchangeConsumer.java
package com.learning.consumer;

import com.learning.config.DirectExchangeConfig;
import com.learning.config.FanoutExchangeConfig;
import com.learning.config.TopicExchangeConfig;
import com.learning.config.HeadersExchangeConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ExchangeConsumer {
    
    @RabbitListener(queues = DirectExchangeConfig.NORMAL_QUEUE)
    public void consumeNormalQueue(String message) {
        System.out.println("[Direct-Normal] " + message);
    }
    
    @RabbitListener(queues = DirectExchangeConfig.EXPRESS_QUEUE)
    public void consumeExpressQueue(String message) {
        System.out.println("[Direct-Express] " + message);
    }
    
    @RabbitListener(queues = DirectExchangeConfig.BULK_QUEUE)
    public void consumeBulkQueue(String message) {
        System.out.println("[Direct-Bulk] " + message);
    }
    
    @RabbitListener(queues = FanoutExchangeConfig.EMAIL_QUEUE)
    public void consumeEmailQueue(String message) {
        System.out.println("[Fanout-Email] " + message);
    }
    
    @RabbitListener(queues = FanoutExchangeConfig.SMS_QUEUE)
    public void consumeSmsQueue(String message) {
        System.out.println("[Fanout-SMS] " + message);
    }
    
    @RabbitListener(queues = FanoutExchangeConfig.PUSH_QUEUE)
    public void consumePushQueue(String message) {
        System.out.println("[Fanout-Push] " + message);
    }
    
    @RabbitListener(queues = FanoutExchangeConfig.LOG_QUEUE)
    public void consumeLogQueue(String message) {
        System.out.println("[Fanout-Log] " + message);
    }
    
    @RabbitListener(queues = TopicExchangeConfig.ORDER_QUEUE)
    public void consumeOrderQueue(String message) {
        System.out.println("[Topic-Order] " + message);
    }
    
    @RabbitListener(queues = TopicExchangeConfig.PAYMENT_QUEUE)
    public void consumePaymentQueue(String message) {
        System.out.println("[Topic-Payment] " + message);
    }
    
    @RabbitListener(queues = TopicExchangeConfig.SHIPPING_QUEUE)
    public void consumeShippingQueue(String message) {
        System.out.println("[Topic-Shipping] " + message);
    }
    
    @RabbitListener(queues = TopicExchangeConfig.ALL_NOTIFICATIONS_QUEUE)
    public void consumeAllNotificationsQueue(String message) {
        System.out.println("[Topic-All] " + message);
    }
    
    @RabbitListener(queues = HeadersExchangeConfig.VIDEO_QUEUE)
    public void consumeVideoQueue(Message message) {
        System.out.println("[Headers-Video] " + message.getBody());
    }
    
    @RabbitListener(queues = HeadersExchangeConfig.AUDIO_QUEUE)
    public void consumeAudioQueue(Message message) {
        System.out.println("[Headers-Audio] " + message.getBody());
    }
    
    @RabbitListener(queues = HeadersExchangeConfig.IMAGE_QUEUE)
    public void consumeImageQueue(Message message) {
        System.out.println("[Headers-Image] " + message.getBody());
    }
    
    @RabbitListener(queues = HeadersExchangeConfig.HIGH_PRIORITY_QUEUE)
    public void consumeHighPriorityQueue(Message message) {
        System.out.println("[Headers-HighPriority] " + message.getBody());
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

---

# Mini-Project 3: Message Publishing (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Publisher Confirms, Return Callbacks, Message Properties, Persistent Messages, Correlation

This mini-project focuses on reliable message publishing patterns.

---

## Project Structure

```
22-rabbitmq/
├── src/main/java/com/learning/
│   ├── config/
│   │   └── ReliablePublisherConfig.java
│   ├── producer/
│   │   └── ReliablePublisher.java
│   └── model/
│       └── Order.java
```

---

## Step 1: Reliable Publisher Configuration

```java
// config/ReliablePublisherConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReliablePublisherConfig {
    
    public static final String RELIABLE_QUEUE = "reliable.queue";
    public static final String RELIABLE_EXCHANGE = "reliable.exchange";
    public static final String ROUTING_KEY = "reliable.order";
    
    @Bean
    public DirectExchange reliableExchange() {
        return new DirectExchange(RELIABLE_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue reliableQueue() {
        return QueueBuilder.durable(RELIABLE_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", RELIABLE_QUEUE + ".dlq")
            .build();
    }
    
    @Bean
    public Queue reliableDlq() {
        return QueueBuilder.durable(RELIABLE_QUEUE + ".dlq").build();
    }
    
    @Bean
    public Binding reliableBinding() {
        return BindingBuilder.bind(reliableQueue())
            .to(reliableExchange())
            .with(ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setJavaTimeModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return converter;
    }
}
```

---

## Step 2: Reliable Publisher Implementation

```java
// producer/ReliablePublisher.java
package com.learning.producer;

import com.learning.config.ReliablePublisherConfig;
import com.learning.model.Order;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ReliablePublisher {
    
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;
    
    public ReliablePublisher(RabbitTemplate rabbitTemplate, MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
        configurePublisher();
    }
    
    private void configurePublisher() {
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("[Confirm] Message confirmed: " + 
                    correlationData.getId());
            } else {
                System.err.println("[Confirm] Message NOT confirmed: " + cause);
                handleUnconfirmedMessage(correlationData);
            }
        });
        
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("[Return] Message returned!");
            System.err.println("  Exchange: " + returned.getExchange());
            System.err.println("  Routing Key: " + returned.getRoutingKey());
            System.err.println("  Reply Code: " + returned.getReplyCode());
            System.err.println("  Reply Text: " + returned.getReplyText());
            handleReturnedMessage(returned);
        });
    }
    
    public void sendOrderWithConfirmation(Order order) {
        String correlationId = UUID.randomUUID().toString();
        
        rabbitTemplate.convertAndSend(
            ReliablePublisherConfig.RELIABLE_EXCHANGE,
            ReliablePublisherConfig.ROUTING_KEY,
            order,
            message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setContentType("application/json");
                message.getMessageProperties().setTimestamp(new java.util.Date());
                return message;
            }
        );
        
        System.out.println("[Publisher] Order sent with correlation ID: " + correlationId);
    }
    
    public CompletableFuture<Boolean> sendOrderAsync(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                sendOrderWithConfirmation(order);
                return true;
            } catch (Exception e) {
                System.err.println("[Publisher] Failed to send order: " + e.getMessage());
                return false;
            }
        });
    }
    
    public boolean sendOrderBlocking(Order order, long timeoutSeconds) {
        try {
            sendOrderWithConfirmation(order);
            return true;
        } catch (Exception e) {
            System.err.println("[Publisher] Failed to send order: " + e.getMessage());
            return false;
        }
    }
    
    public void sendBatchOrders(Iterable<Order> orders) {
        int count = 0;
        for (Order order : orders) {
            sendOrderWithConfirmation(order);
            count++;
            
            if (count % 10 == 0) {
                rabbitTemplate.execute(channel -> {
                    try {
                        channel.waitForConfirmsOrDie(5, TimeUnit.SECONDS);
                        System.out.println("[Publisher] Batch of 10 messages confirmed");
                    } catch (Exception e) {
                        System.err.println("[Publisher] Batch confirmation failed: " + e.getMessage());
                    }
                    return null;
                });
            }
        }
    }
    
    private void handleUnconfirmedMessage(CorrelationData correlationData) {
        System.out.println("[Publisher] Implement retry logic for: " + correlationData.getId());
    }
    
    private void handleReturnedMessage(ReturnedMessage returned) {
        System.out.println("[Publisher] Implement handling for unroutable message");
    }
}
```

---

## Step 3: Order Model

```java
// model/Order.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private String orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    
    public Order() {}
    
    public Order(String orderId, Long customerId, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = "CREATED";
        this.createdAt = LocalDateTime.now();
    }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

---

## Build Instructions

```bash
cd 22-rabbitmq
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 4: Consumer Patterns (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Manual Acknowledgment, Prefetch, Concurrency, Retry Patterns, Error Handling

This mini-project focuses on implementing reliable consumer patterns.

---

## Project Structure

```
22-rabbitmq/
├── src/main/java/com/learning/
│   ├── config/
│   │   └── ConsumerConfig.java
│   ├── consumer/
│   │   ├── ManualAckConsumer.java
│   │   ├── ConcurrentConsumer.java
│   │   └── RetryConsumer.java
│   └── model/
│       └── Order.java
```

---

## Step 1: Consumer Configuration

```java
// config/ConsumerConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {
    
    public static final String MANUAL_ACK_QUEUE = "manual.ack.queue";
    public static final String CONCURRENT_QUEUE = "concurrent.queue";
    public static final String RETRY_QUEUE = "retry.queue";
    
    public static final String CONSUMER_EXCHANGE = "consumer.exchange";
    
    @Bean
    public DirectExchange consumerExchange() {
        return new DirectExchange(CONSUMER_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue manualAckQueue() {
        return QueueBuilder.durable(MANUAL_ACK_QUEUE).build();
    }
    
    @Bean
    public Queue concurrentQueue() {
        return QueueBuilder.durable(CONCURRENT_QUEUE).build();
    }
    
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RETRY_QUEUE)
            .withArgument("x-dead-letter-exchange", CONSUMER_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", "manual.ack")
            .withArgument("x-message-ttl", 5000)
            .build();
    }
    
    @Bean
    public Binding manualAckBinding() {
        return BindingBuilder.bind(manualAckQueue())
            .to(consumerExchange())
            .with("manual.ack");
    }
    
    @Bean
    public Binding concurrentBinding() {
        return BindingBuilder.bind(concurrentQueue())
            .to(consumerExchange())
            .with("concurrent");
    }
    
    @Bean
    public Binding retryBinding() {
        return BindingBuilder.bind(retryQueue())
            .to(consumerExchange())
            .with("retry");
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
```

---

## Step 2: Manual Acknowledgment Consumer

```java
// consumer/ManualAckConsumer.java
package com.learning.consumer;

import com.learning.config.ConsumerConfig;
import com.learning.model.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ManualAckConsumer {
    
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    
    private static final int MAX_RETRIES = 3;
    
    @RabbitListener(queues = ConsumerConfig.MANUAL_ACK_QUEUE, ackMode = "MANUAL")
    public void processOrderWithManualAck(
            Order order,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        try {
            System.out.println("[ManualAck] Processing order: " + order.getOrderId());
            
            simulateProcessing(order);
            
            channel.basicAck(deliveryTag, false);
            successCount.incrementAndGet();
            
            System.out.println("[ManualAck] Order processed: " + order.getOrderId());
            
        } catch (Exception e) {
            handleFailure(order, channel, deliveryTag, e);
        }
    }
    
    private void simulateProcessing(Order order) throws Exception {
        Thread.sleep(100);
        
        if (Math.random() < 0.2) {
            throw new RuntimeException("Simulated processing failure");
        }
    }
    
    private void handleFailure(Order order, Channel channel, long deliveryTag, Exception e) {
        try {
            System.err.println("[ManualAck] Failed to process order: " + order.getOrderId());
            System.err.println("[ManualAck] Error: " + e.getMessage());
            
            channel.basicNack(deliveryTag, false, true);
            failureCount.incrementAndGet();
            
        } catch (IOException ioException) {
            System.err.println("[ManualAck] Failed to nack message: " + ioException.getMessage());
        }
    }
    
    public int getSuccessCount() { return successCount.get(); }
    public int getFailureCount() { return failureCount.get(); }
}
```

---

## Step 3: Concurrent Consumer

```java
// consumer/ConcurrentConsumer.java
package com.learning.consumer;

import com.learning.config.ConsumerConfig;
import com.learning.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ConcurrentConsumer {
    
    private final AtomicInteger totalProcessed = new AtomicInteger(0);
    private final ConcurrentMap<Thread, AtomicInteger> threadCounts = new ConcurrentHashMap<>();
    private final AtomicInteger threadId = new AtomicInteger(0);
    
    @RabbitListener(queues = ConsumerConfig.CONCURRENT_QUEUE, concurrency = "3-10")
    public void processConcurrently(Order order) {
        int currentThreadId = threadId.incrementAndGet();
        Thread currentThread = Thread.currentThread();
        
        threadCounts.computeIfAbsent(currentThread, k -> new AtomicInteger(0)).incrementAndGet();
        totalProcessed.incrementAndGet();
        
        System.out.println("[Concurrent] Thread-" + currentThreadId + 
            " processing order: " + order.getOrderId());
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("[Concurrent] Thread-" + currentThreadId + 
            " completed order: " + order.getOrderId());
    }
    
    public int getTotalProcessed() { return totalProcessed.get(); }
    
    public void printThreadStats() {
        System.out.println("\n=== Thread Processing Stats ===");
        threadCounts.forEach((thread, count) -> 
            System.out.println("Thread " + thread.getName() + ": " + count.get() + " messages"));
        System.out.println("Total processed: " + totalProcessed.get());
    }
}
```

---

## Step 4: Retry Consumer

```java
// consumer/RetryConsumer.java
package com.learning.consumer;

import com.learning.config.ConsumerConfig;
import com.learning.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RetryConsumer {
    
    private final AtomicInteger attemptCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger maxRetryCount = new AtomicInteger(0);
    
    private static final int MAX_ALLOWED_RETRIES = 3;
    
    @RabbitListener(queues = ConsumerConfig.RETRY_QUEUE)
    public void processWithRetry(Order order) {
        int attempt = attemptCount.incrementAndGet();
        
        System.out.println("[Retry] Processing order: " + order.getOrderId() + 
            " (attempt " + attempt + ")");
        
        try {
            processOrder(order);
            successCount.incrementAndGet();
            System.out.println("[Retry] Successfully processed: " + order.getOrderId());
            
        } catch (Exception e) {
            handleRetry(order, attempt, e);
        }
    }
    
    private void processOrder(Order order) throws Exception {
        Thread.sleep(100);
        
        if (Math.random() < 0.4) {
            throw new RuntimeException("Transient failure");
        }
    }
    
    private void handleRetry(Order order, int attempt, Exception e) {
        if (attempt >= MAX_ALLOWED_RETRIES) {
            maxRetryCount.incrementAndGet();
            System.err.println("[Retry] Max retries exceeded for order: " + order.getOrderId());
            System.err.println("[Retry] Sending to DLQ for manual intervention");
            return;
        }
        
        System.err.println("[Retry] Retrying order " + order.getOrderId() + 
            " after failure: " + e.getMessage());
    }
    
    public int getAttemptCount() { return attemptCount.get(); }
    public int getSuccessCount() { return successCount.get(); }
    public int getMaxRetryCount() { return maxRetryCount.get(); }
}
```

---

## Build Instructions

```bash
cd 22-rabbitmq
mvn clean compile
mvn spring-boot:run
```

---

# Real-World Project: Order Processing System

## Project Overview

**Duration**: 15+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Saga Pattern, Choreography, Orchestration, CQRS, Event Sourcing

This comprehensive project implements a complete order processing system using RabbitMQ.

---

## Project Structure

```
22-rabbitmq/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── OrderExchangeConfig.java
│   │   ├── SagaConfig.java
│   │   └── CompensationConfig.java
│   ├── service/
│   │   ├── OrderSagaOrchestrator.java
│   │   ├── InventoryService.java
│   │   ├── PaymentService.java
│   │   └── ShippingService.java
│   ├── consumer/
│   │   └── SagaConsumers.java
│   └── model/
│       ├── Order.java
│       ├── OrderEvent.java
│       └── CompensationEvent.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Saga Configuration

```java
// config/SagaConfig.java
package com.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfig {
    
    // Order Saga Exchanges
    public static final String ORDER_CREATED_EXCHANGE = "saga.order.created";
    public static final String INVENTORY_RESERVED_EXCHANGE = "saga.inventory.reserved";
    public static final String PAYMENT_PROCESSED_EXCHANGE = "saga.payment.processed";
    public static final String SHIPMENT_SCHEDULED_EXCHANGE = "saga.shipment.scheduled";
    public static final String ORDER_COMPLETED_EXCHANGE = "saga.order.completed";
    public static final String ORDER_FAILED_EXCHANGE = "saga.order.failed";
    
    // Compensation Exchanges
    public static final String INVENTORY_RELEASE_EXCHANGE = "compensation.inventory.release";
    public static final String PAYMENT_REFUND_EXCHANGE = "compensation.payment.refund";
    public static final String SHIPMENT_CANCEL_EXCHANGE = "compensation.shipment.cancel";
    
    // Queues
    public static final String SAGA_ORCHESTRATOR_QUEUE = "saga.orchestrator.queue";
    public static final String INVENTORY_QUEUE = "saga.inventory.queue";
    public static final String PAYMENT_QUEUE = "saga.payment.queue";
    public static final String SHIPPING_QUEUE = "saga.shipping.queue";
    public static final String COMPENSATION_QUEUE = "saga.compensation.queue";
    
    @Bean
    public TopicExchange orderCreatedExchange() {
        return new TopicExchange(ORDER_CREATED_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange inventoryReservedExchange() {
        return new TopicExchange(INVENTORY_RESERVED_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange paymentProcessedExchange() {
        return new TopicExchange(PAYMENT_PROCESSED_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange shipmentScheduledExchange() {
        return new TopicExchange(SHIPMENT_SCHEDULED_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange orderCompletedExchange() {
        return new TopicExchange(ORDER_COMPLETED_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange orderFailedExchange() {
        return new TopicExchange(ORDER_FAILED_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange inventoryReleaseExchange() {
        return new DirectExchange(INVENTORY_RELEASE_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange paymentRefundExchange() {
        return new DirectExchange(PAYMENT_REFUND_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange shipmentCancelExchange() {
        return new DirectExchange(SHIPMENT_CANCEL_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue sagaOrchestratorQueue() {
        return QueueBuilder.durable(SAGA_ORCHESTRATOR_QUEUE).build();
    }
    
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(INVENTORY_QUEUE).build();
    }
    
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }
    
    @Bean
    public Queue shippingQueue() {
        return QueueBuilder.durable(SHIPPING_QUEUE).build();
    }
    
    @Bean
    public Queue compensationQueue() {
        return QueueBuilder.durable(COMPENSATION_QUEUE).build();
    }
}
```

---

## Step 2: Saga Orchestrator

```java
// service/OrderSagaOrchestrator.java
package com.learning.service;

import com.learning.config.SagaConfig;
import com.learning.model.Order;
import com.learning.model.OrderEvent;
import com.learning.model.CompensationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderSagaOrchestrator {
    
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, SagaState> activeSagas = new ConcurrentHashMap<>();
    
    public OrderSagaOrchestrator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void startSaga(Order order) {
        String sagaId = "SAGA-" + order.getOrderId();
        SagaState state = new SagaState(sagaId, order);
        activeSagas.put(sagaId, state);
        
        System.out.println("[Saga] Starting saga: " + sagaId);
        
        state.setInventoryStatus("PENDING");
        sendInventoryReservationCommand(order);
    }
    
    private void sendInventoryReservationCommand(Order order) {
        OrderEvent event = new OrderEvent(
            "RESERVE_INVENTORY",
            order.getOrderId(),
            order
        );
        
        rabbitTemplate.convertAndSend(
            SagaConfig.ORDER_CREATED_EXCHANGE,
            "inventory.reserve",
            event
        );
        
        System.out.println("[Saga] Sent inventory reservation command for: " + order.getOrderId());
    }
    
    public void handleInventoryReserved(OrderEvent event) {
        SagaState state = activeSagas.get("SAGA-" + event.getOrderId());
        if (state == null) return;
        
        System.out.println("[Saga] Inventory reserved for: " + event.getOrderId());
        state.setInventoryStatus("RESERVED");
        state.setPaymentStatus("PENDING");
        
        sendPaymentCommand(state.getOrder());
    }
    
    public void handleInventoryFailed(OrderEvent event) {
        SagaState state = activeSagas.get("SAGA-" + event.getOrderId());
        if (state == null) return;
        
        System.out.println("[Saga] Inventory reservation failed for: " + event.getOrderId());
        compensateInventory(state);
    }
    
    private void sendPaymentCommand(Order order) {
        OrderEvent event = new OrderEvent(
            "PROCESS_PAYMENT",
            order.getOrderId(),
            order
        );
        
        rabbitTemplate.convertAndSend(
            SagaConfig.INVENTORY_RESERVED_EXCHANGE,
            "payment.process",
            event
        );
        
        System.out.println("[Saga] Sent payment command for: " + order.getOrderId());
    }
    
    public void handlePaymentProcessed(OrderEvent event) {
        SagaState state = activeSagas.get("SAGA-" + event.getOrderId());
        if (state == null) return;
        
        System.out.println("[Saga] Payment processed for: " + event.getOrderId());
        state.setPaymentStatus("PROCESSED");
        state.setShippingStatus("PENDING");
        
        sendShippingCommand(state.getOrder());
    }
    
    public void handlePaymentFailed(OrderEvent event) {
        SagaState state = activeSagas.get("SAGA-" + event.getOrderId());
        if (state == null) return;
        
        System.out.println("[Saga] Payment failed for: " + event.getOrderId());
        compensateInventory(state);
    }
    
    private void sendShippingCommand(Order order) {
        OrderEvent event = new OrderEvent(
            "SCHEDULE_SHIPMENT",
            order.getOrderId(),
            order
        );
        
        rabbitTemplate.convertAndSend(
            SagaConfig.PAYMENT_PROCESSED_EXCHANGE,
            "shipping.schedule",
            event
        );
        
        System.out.println("[Saga] Sent shipping command for: " + order.getOrderId());
    }
    
    public void handleShipmentScheduled(OrderEvent event) {
        SagaState state = activeSagas.get("SAGA-" + event.getOrderId());
        if (state == null) return;
        
        System.out.println("[Saga] Shipment scheduled for: " + event.getOrderId());
        state.setShippingStatus("SCHEDULED");
        state.setStatus("COMPLETED");
        
        completeSaga(state);
    }
    
    public void handleShipmentFailed(OrderEvent event) {
        SagaState state = activeSagas.get("SAGA-" + event.getOrderId());
        if (state == null) return;
        
        System.out.println("[Saga] Shipping failed for: " + event.getOrderId());
        compensatePayment(state);
        compensateInventory(state);
    }
    
    private void compensateInventory(SagaState state) {
        CompensationEvent event = new CompensationEvent(
            "RELEASE_INVENTORY",
            state.getOrder().getOrderId()
        );
        
        rabbitTemplate.convertAndSend(
            SagaConfig.INVENTORY_RELEASE_EXCHANGE,
            "inventory.release",
            event
        );
        
        System.out.println("[Saga] Compensating inventory for: " + state.getOrder().getOrderId());
        failSaga(state);
    }
    
    private void compensatePayment(SagaState state) {
        if (!"PROCESSED".equals(state.getPaymentStatus())) return;
        
        CompensationEvent event = new CompensationEvent(
            "REFUND_PAYMENT",
            state.getOrder().getOrderId()
        );
        
        rabbitTemplate.convertAndSend(
            SagaConfig.PAYMENT_REFUND_EXCHANGE,
            "payment.refund",
            event
        );
        
        System.out.println("[Saga] Compensating payment for: " + state.getOrder().getOrderId());
    }
    
    private void completeSaga(SagaState state) {
        System.out.println("[Saga] Saga completed successfully: " + state.getSagaId());
        activeSagas.remove(state.getSagaId());
    }
    
    private void failSaga(SagaState state) {
        System.out.println("[Saga] Saga failed: " + state.getSagaId());
        activeSagas.remove(state.getSagaId());
    }
    
    public static class SagaState {
        private final String sagaId;
        private final Order order;
        private String status = "STARTED";
        private String inventoryStatus = "NOT_STARTED";
        private String paymentStatus = "NOT_STARTED";
        private String shippingStatus = "NOT_STARTED";
        private final AtomicInteger stepCount = new AtomicInteger(0);
        
        public SagaState(String sagaId, Order order) {
            this.sagaId = sagaId;
            this.order = order;
        }
        
        public String getSagaId() { return sagaId; }
        public Order getOrder() { return order; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getInventoryStatus() { return inventoryStatus; }
        public void setInventoryStatus(String status) { this.inventoryStatus = status; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String status) { this.paymentStatus = status; }
        public String getShippingStatus() { return shippingStatus; }
        public void setShippingStatus(String status) { this.shippingStatus = status; }
    }
}
```

---

## Build Instructions

```bash
# Start RabbitMQ with high availability
docker run -p 5672:5672 -p 15672:15672 rabbitmq:3-management

cd 22-rabbitmq
mvn clean compile
mvn spring-boot:run
```