# How Distributed Messaging Works

## Kafka Producer

```java
import org.apache.kafka.clients.producer.*;
import java.util.Properties;

public class KafkaMessageProducer {
    private final KafkaProducer<String, String> producer;
    
    public KafkaMessageProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", 
            "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", 
            "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "all");
        props.put("linger.ms", 5);
        this.producer = new KafkaProducer<>(props);
    }
    
    public void send(String topic, String key, String value) {
        ProducerRecord<String, String> record = 
            new ProducerRecord<>(topic, key, value);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Send failed: " + exception.getMessage());
            }
        });
    }
    
    public void close() {
        producer.close();
    }
}
```

## Kafka Consumer

```java
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.List;

public class KafkaMessageConsumer {
    private final KafkaConsumer<String, String> consumer;
    
    public KafkaMessageConsumer(String bootstrapServers, String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("key.deserializer", 
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", 
            "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", false);
        this.consumer = new KafkaConsumer<>(props);
    }
    
    public void consume(String topic, MessageProcessor processor) {
        consumer.subscribe(List.of(topic));
        
        while (true) {
            ConsumerRecords<String, String> records = 
                consumer.poll(Duration.ofMillis(100));
            
            for (ConsumerRecord<String, String> record : records) {
                processor.process(record.key(), record.value());
            }
            
            consumer.commitSync(); // At-least-once semantics
        }
    }
    
    interface MessageProcessor {
        void process(String key, String value);
    }
}
```
