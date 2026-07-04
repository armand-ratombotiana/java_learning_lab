# Distributed Messaging: Step by Step

## Creating a Kafka Producer-Consumer

### Step 1: Add dependencies
```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.5.0</version>
</dependency>
```

### Step 2: Create producer
Configure bootstrap servers, serializers, acks, and batching.

### Step 3: Send messages
```java
producer.send(new ProducerRecord<>("orders", orderId, orderJson));
```

### Step 4: Create consumer
Configure group id, deserializers, auto offset reset.

### Step 5: Poll and process
```java
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(100);
    for (ConsumerRecord<String, String> record : records) {
        processOrder(record.value());
    }
    consumer.commitAsync();
}
```
