# Messaging Exercises

## Exercise 1: Simple Kafka Producer and Consumer

### Task
Create a Kafka producer that sends messages and a consumer that reads them.

### Solution

```java
// Producer
public class SimpleProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        
        Producer<String, String> producer = new KafkaProducer<>(props);
        
        for (int i = 0; i < 10; i++) {
            String key = "key-" + i;
            String value = "message-" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>("test-topic", key, value);
            
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Sent: " + value + " to partition " + 
                        metadata.partition() + " at offset " + metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            });
        }
        
        producer.close();
    }
}

// Consumer
public class SimpleConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "my-consumer-group");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("test-topic"));
        
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received: key=" + record.key() + 
                    ", value=" + record.value() + 
                    ", partition=" + record.partition());
            }
        }
    }
}
```

---

## Exercise 2: RabbitMQ Producer with Exchange Types

### Task
Implement producers using different exchange types (direct, topic, fanout).

### Solution

```java
public class RabbitMQExchanges {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    // Direct Exchange - exact routing key match
    public void sendDirect(String routingKey, String message) {
        rabbitTemplate.convertAndSend("direct-exchange", routingKey, message);
    }
    
    // Topic Exchange - wildcard matching
    public void sendTopic(String routingKey, String message) {
        rabbitTemplate.convertAndSend("topic-exchange", routingKey, message);
    }
    
    // Fanout Exchange - broadcast to all queues
    public void sendFanout(String message) {
        rabbitTemplate.convertAndSend("fanout-exchange", "", message);
    }
    
    // Configuration
    @Configuration
    public static class ExchangeConfig {
        
        @Bean
        public Queue queue1() { return new Queue("queue1"); }
        
        @Bean
        public Queue queue2() { return new Queue("queue2"); }
        
        @Bean
        public DirectExchange directExchange() { return new DirectExchange("direct-exchange"); }
        
        @Bean
        public TopicExchange topicExchange() { return new TopicExchange("topic-exchange"); }
        
        @Bean
        public FanoutExchange fanoutExchange() { return new FanoutExchange("fanout-exchange"); }
        
        @Bean
        public Binding directBinding() {
            return BindingBuilder.bind(queue1()).to(directExchange()).with("routing.key1");
        }
        
        @Bean
        public Binding topicBinding() {
            return BindingBuilder.bind(queue2()).to(topicExchange()).with("orders.#");
        }
        
        @Bean
        public Binding fanoutBinding1() {
            return BindingBuilder.bind(queue1()).to(fanoutExchange());
        }
    }
}
```

---

## Exercise 3: Request-Reply Pattern

### Task
Implement request-reply pattern using RabbitMQ.

### Solution

```java
public class RequestReplyClient {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public String sendRequest(String request) {
        String correlationId = UUID.randomUUID().toString();
        
        MessageProperties props = new MessageProperties();
        props.setCorrelationId(correlationId);
        props.setReplyTo("reply-queue");
        
        Message requestMsg = MessageBuilder.withBody(request.getBytes())
            .setProperties(props)
            .build();
        
        Message reply = rabbitTemplate.sendAndReceive("request-exchange", "request-key", requestMsg);
        
        return new String(reply.getBody());
    }
}

@Service
public class RequestReplyServer {
    
    @RabbitListener(queues = "request-queue")
    public Message handleRequest(Message message) {
        String requestBody = new String(message.getBody());
        String correlationId = new String(message.getMessageProperties().getCorrelationId());
        
        // Process request
        String responseBody = "Processed: " + requestBody;
        
        MessageProperties props = new MessageProperties();
        props.setCorrelationId(correlationId);
        
        return MessageBuilder.withBody(responseBody.getBytes())
            .setProperties(props)
            .build();
    }
}
```

---

## Exercise 4: Kafka Partition Strategy

### Task
Implement custom partition strategy for Kafka producer.

### Solution

```java
public class CustomPartitioner implements Partitioner {
    
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, 
                         Object value, byte[] valueBytes, Cluster cluster) {
        
        if (keyBytes == null) {
            // Round-robin for null keys
            List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
            int numPartitions = partitions.size();
            return Math.abs(new Random().nextInt() % numPartitions);
        }
        
        // Hash-based partition for string keys
        String keyString = (String) key;
        if (keyString.startsWith("vip-")) {
            // VIP users to partition 0
            return 0;
        }
        
        // Default: hash-based
        return Math.abs(keyString.hashCode() % cluster.partitionsForTopic(topic).size());
    }
    
    @Override
    public void close() {}
    
    @Override
    public void configure(Map<String, ?> configs) {}
}

// Usage
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", StringSerializer.class.getName());
props.put("value.serializer", StringSerializer.class.getName());
props.put("partitioner.class", CustomPartitioner.class.getName());
```

---

## Exercise 5: Message Serialization with JSON

### Task
Implement JSON message serialization for Kafka.

### Solution

```java
// Model class
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String id;
    private String customerName;
    private List<OrderItem> items;
    private BigDecimal total;
    private LocalDateTime timestamp;
    
    // constructors, getters, setters
}

public class OrderItem {
    private String productId;
    private int quantity;
    private BigDecimal price;
}

// JSON Serializer
public class JsonSerializer<T> implements Serializer<T> {
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}
    
    @Override
    public byte[] serialize(String topic, T data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }
    
    @Override
    public void close() {}
}

// Usage
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", StringSerializer.class.getName());
props.put("value.serializer", JsonSerializer.class.getName());
props.put(JsonSerializer.TYPE_CONFIG, Order.class.getName());

Producer<String, Order> producer = new KafkaProducer<>(props);
Order order = new Order("order-123", "John", items, total, LocalDateTime.now());
producer.send(new ProducerRecord<>("orders", order.getId(), order));
```

---

## Exercise 6: Kafka Streams Processing

### Task
Implement simple Kafka Streams word count application.

### Solution

```java
public class WordCountApplication {
    
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        
        // Read from source topic
        KStream<String, String> source = builder.stream("text-input");
        
        // Transform: flatMapValues to split into words
        KTable<String, Long> wordCounts = source
            .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
            .groupBy((key, word) -> word)
            .count(Materialized.as("counts-store"));
        
        // Output to sink topic
        wordCounts.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));
        
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
```

---

## Exercise 7: Dead Letter Queue Handling

### Task
Implement dead letter queue for handling failed messages.

### Solution

```java
// Configuration
@Configuration
public class DLQConfig {
    
    @Bean
    public Queue mainQueue() {
        return QueueBuilder.durable("main-queue")
            .withArgument("x-dead-letter-exchange", "dlx-exchange")
            .withArgument("x-dead-letter-routing-key", "dlq-queue")
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dead-letter-queue").build();
    }
    
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx-exchange");
    }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(dlxExchange())
            .with("dlq-queue");
    }
}

// Processing with retry
@Service
public class MessageProcessor {
    
    @RabbitListener(queues = "main-queue")
    public void processMessage(Message message) {
        try {
            String body = new String(message.getBody());
            // Process message
            process(body);
        } catch (Exception e) {
            // Reject and send to DLQ
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
    
    private void process(String body) {
        // Business logic
    }
}

// DLQ Consumer
@Service
public class DLQProcessor {
    
    @RabbitListener(queues = "dead-letter-queue")
    public void handleDLQ(Message message) {
        System.out.println("Failed message: " + new String(message.getBody()));
        // Log, alert, or reprocess
    }
}
```

---

## Exercise 8: Message Filtering

### Task
Implement message filtering using Kafka Streams.

### Solution

```java
public class MessageFilterStream {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "filter-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        
        KStream<String, String> source = builder.stream("raw-events");
        
        // Filter messages based on content
        KStream<String, String> filtered = source
            .filter((key, value) -> {
                try {
                    JsonNode node = new ObjectMapper().readTree(value);
                    String eventType = node.get("type").asText();
                    return "IMPORTANT".equals(eventType) && 
                           node.get("amount").asInt() > 1000;
                } catch (Exception e) {
                    return false;
                }
            })
            .mapValues(value -> {
                // Transform: add processing timestamp
                try {
                    JsonNode node = new ObjectMapper().readTree(value);
                    ((ObjectNode) node).put("processedAt", 
                        Instant.now().toString());
                    return node.toString();
                } catch (Exception e) {
                    return value;
                }
            });
        
        // Send to different topics based on condition
        filtered.branch(
            (key, value) -> value.contains("urgent"),
            KafkaStream.builder().to("urgent-events"),
            (key, value) -> true,
            KafkaStream.builder().to("filtered-events")
        );
        
        new KafkaStreams(builder.build(), props).start();
    }
}
```

---

## Exercise 9: Exactly-Once Semantics

### Task
Implement exactly-once processing in Kafka.

### Solution

```java
public class ExactlyOnceProcessor {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exactly-once-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Enable exactly-once semantics
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        
        // Transactional ID for producer
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        
        StreamsBuilder builder = new StreamsBuilder();
        
        KStream<String, String> source = builder.stream("input-topic");
        
        // Transform with exactly-once guarantee
        KStream<String, String> processed = source
            .transform(() -> new Transformer<String, String, KeyValue<String, String>>() {
                private ProcessorContext context;
                
                @Override
                public void init(ProcessorContext context) {
                    this.context = context;
                }
                
                @Override
                public KeyValue<String, String> transform(String key, String value) {
                    // Process with state stored in state store
                    String processedValue = processAndStore(key, value);
                    context.commit(); // Commit transaction
                    return KeyValue.pair(key, processedValue);
                }
                
                @Override
                public void close() {}
            }, "state-store");
        
        processed.to("output-topic", Produced.with(Serdes.String(), Serdes.String()));
        
        new KafkaStreams(builder.build(), props).start();
    }
    
    private String processAndStore(String key, String value) {
        // Business logic
        return value.toUpperCase();
    }
}
```

---

## Exercise 10: Distributed Tracing with Correlation IDs

### Task
Implement message tracing with correlation IDs.

### Solution

```java
public class TracingUtility {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    public static void addCorrelationId(Message message) {
        String correlationId = generateCorrelationId();
        MessageProperties props = message.getMessageProperties();
        if (props == null) {
            props = new MessageProperties();
            message.setMessageProperties(props);
        }
        props.setHeader(CORRELATION_ID_HEADER, correlationId);
    }
    
    public static String getCorrelationId(Message message) {
        return (String) message.getMessageProperties().getHeader(CORRELATION_ID_HEADER);
    }
}

// Producer with tracing
@Service
public class TracedProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void send(String message) {
        Message msg = MessageBuilder.withBody(message.getBytes())
            .setHeader(TracingUtility.CORRELATION_ID_HEADER, 
                TracingUtility.generateCorrelationId())
            .setHeader("X-Trace-Start-Time", System.currentTimeMillis())
            .build();
        
        rabbitTemplate.send("exchange", "routing-key", msg);
    }
}

// Consumer with tracing
@Service
public class TracedConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(TracedConsumer.class);
    
    @RabbitListener(queues = "my-queue")
    public void handle(Message message) {
        String correlationId = TracingUtility.getCorrelationId(message);
        long startTime = (Long) message.getMessageProperties().getHeader("X-Trace-Start-Time");
        
        logger.info("Processing message. CorrelationId: {}, Latency: {}ms",
            correlationId, System.currentTimeMillis() - startTime);
        
        try {
            process(message);
        } finally {
            logger.info("Completed processing. CorrelationId: {}", correlationId);
        }
    }
    
    private void process(Message message) {
        // Business logic
    }
}
```