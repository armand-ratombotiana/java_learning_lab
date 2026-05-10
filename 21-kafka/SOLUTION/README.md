# Kafka Solution

Reference implementation for Kafka Producer, Consumer, and Streams.

## Producer
- `KafkaProducer` interface
- Synchronous and asynchronous send
- `RecordMetadata` for acknowledgment

## Consumer
- `KafkaConsumer` for subscribing
- `ConsumerRecords` for polling
- `ConsumerRecord` for message handling

## Streams
- `StreamsBuilder` for pipeline
- `KafkaStreams` for stream processing

## Admin
- `KafkaAdminClient` for topic management
- `createTopic`, `deleteTopic`, `listTopics`

## Serializers
- `StringSerializer` / `StringDeserializer`
- Extensible `Serializer<T>` interface

## Test Coverage: 25+ tests