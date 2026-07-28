# Lab 04: Streaming Optimization — Implementation Guide

## Step 1: Producer with Exactly-Once

```java
Properties props = new Properties();
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
props.put(ProducerConfig.ACKS_CONFIG, "all");
props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
```

## Step 2: Kafka Streams EOS Config

```java
Properties props = new Properties();
props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
```

## Step 3: Custom Partition Routing

```java
public class SkewAwarePartitioner implements Partitioner {
    private final Map<String, Integer> keyDistribution = new ConcurrentHashMap<>();

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        if (key == null) return ThreadLocalRandom.current().nextInt(cluster.partitionCountForTopic(topic));
        String k = key.toString();
        keyDistribution.merge(k, 1, Integer::sum);
        // Route high-skew keys across multiple sub-keys
        if (keyDistribution.get(k) > 1000) {
            String subKey = k + "#" + (keyDistribution.get(k) % 10);
            return Utils.toPositive(Utils.murmur2(subKey.getBytes(StandardCharsets.UTF_8))) % cluster.partitionCountForTopic(topic);
        }
        return Utils.toPositive(Utils.murmur2(keyBytes)) % cluster.partitionCountForTopic(topic);
    }
}
```

## Step 4: Windowing Optimization

```java
// Tumbling window with caching
KStream<String, Long> aggregated = stream
    .groupByKey()
    .windowedBy(TimeWindows.of(Duration.ofMinutes(5)).grace(Duration.ofMinutes(1)))
    .aggregate(() -> 0L, (key, value, agg) -> agg + value,
        Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("agg-store")
            .withCachingEnabled()
            .withRetention(Duration.ofHours(2)));
```

## Step 5: Performance Tuning

```java
props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "4");
props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "100");
props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "10485760"); // 10MB
props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, "3");
```
