# Refactoring Kafka Streams

## Before: Single Large Topology
```java
builder.stream("input").mapValues(v -> { /* complex */ })
    .filter((k,v) -> { /* more logic */ }).groupByKey().count().toStream().to("output");
```

## After: Modular Topologies
```java
KStream<String, Processed> processed = processInput(builder.stream("input"));
KTable<String, Long> counts = aggregate(processed);
outputResults(counts);
```

## Before: Hardcoded Topics
```java
builder.stream("orders");
```

## After: Configuration
```java
@Value("${kafka.topics.input}")
private String inputTopic;
```
