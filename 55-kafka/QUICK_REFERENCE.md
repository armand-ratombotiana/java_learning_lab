# 55 - Kafka Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Topic | Logical channel for messages |
| Partition | Ordered, immutable message sequence |
| Producer | Publishes messages to topics |
| Consumer | Subscribes to topics |
| Consumer Group | Coordinated consumption |
| Offset | Message position in partition |

## Producers

```java
// Basic producer
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

KafkaProducer<String, String> producer = new KafkaProducer<>(props);

ProducerRecord<String, String> record = 
    new ProducerRecord<>("user-events", "user-123", "{\"action\":\"login\"}");

producer.send(record, (metadata, exception) -> {
    if (exception != null) {
        exception.printStackTrace();
    } else {
        System.out.println("Sent to " + metadata.topic() + 
            " partition " + metadata.partition());
    }
});
producer.close();

// With keys for partitioning
ProducerRecord<String, User> record = 
    new ProducerRecord<>("users", user.getId(), user);
```

## Consumers

```java
// Basic consumer
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "my-consumer-group");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("auto.offset.reset", "earliest");
props.put("enable.auto.commit", "true");

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("user-events", "orders"));

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        System.out.println("Key: " + record.key() + 
            ", Value: " + record.value() + 
            ", Partition: " + record.partition());
    }
}
```

## Spring Kafka

```java
// Producer configuration
@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;
    
    @Bean
    public ProducerFactory<String, User> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
            StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
    
    @Bean
    public KafkaTemplate<String, User> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

// Sending messages
@Service
public class UserService {
    
    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;
    
    public void sendUserCreated(User user) {
        kafkaTemplate.send("user-events", user.getId(), user);
    }
}

// Consumer configuration
@KafkaListener(topics = "user-events", groupId = "my-group")
public void consumeUserEvent(ConsumerRecord<String, User> record) {
    User user = record.value();
    System.out.println("Received: " + user);
}
```

## Topic Management

```bash
# Create topic
kafka-topics.sh --create \
    --topic user-events \
    --partitions 3 \
    --replication-factor 1 \
    --bootstrap-servers localhost:9092

# List topics
kafka-topics.sh --list --bootstrap-servers localhost:9092

# Describe topic
kafka-topics.sh --describe --topic user-events \
    --bootstrap-servers localhost:9092

# Add partitions
kafka-topics.sh --alter \
    --topic user-events \
    --partitions 5 \
    --bootstrap-servers localhost:9092
```

## Schema Registry

```java
// Avro producer
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "StringSerializer");
props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
props.put("schema.registry.url", "http://localhost:8081");

KafkaProducer<String, User> producer = new KafkaProducer<>(props);
User user = User.newBuilder().setId(1).setName("John").build();
producer.send(new ProducerRecord<>("users-avro", user));

// Avro consumer
props.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
props.put("schema.registry.url", "http://localhost:8081");
```

## Streams

```java
// Kafka Streams
Properties props = new Properties();
props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount");
props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

StreamsBuilder builder = new StreamsBuilder();
builder.stream("text-input")
    .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
    .groupBy((key, value) -> value)
    .count(Materialized.as("counts-store"))
    .toStream()
    .to("word-count-output");

KafkaStreams streams = new KafkaStreams(builder.build(), props);
streams.start();
```

## Performance Tuning

| Property | Description | Recommended |
|----------|-------------|-------------|
| batch.size | Batch size in bytes | 16384 |
| linger.ms | Time to wait before sending | 10-100 |
| buffer.memory | Total memory for buffering | 32MB |
| compression.type | Message compression | snappy |
| acks | Acknowledgment level | 1 or all |
| retries | Number of retries | 3+ |

## Best Practices

Use appropriate partition count based on throughput needs. Set retention policy based on data requirements. Use key-based partitioning for ordering within partitions. Monitor consumer lag. Use exactly-once semantics for critical data. Configure appropriate acknowledgment levels.