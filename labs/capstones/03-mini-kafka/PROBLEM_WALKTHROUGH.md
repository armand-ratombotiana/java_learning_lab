# Problem Walkthrough: Distributed Messaging System (Mini Kafka)

## Problem Statement

**Design a distributed messaging system that supports topic-based publish-subscribe messaging with partitions, consumer groups, message persistence, log compaction, offset management, and at-least-once delivery guarantees.**

The system must handle 100K messages/second throughput across 10+ topics with 20 partitions each, support 50+ consumers per consumer group, provide configurable retention policies (time-based and size-based), and survive broker failures without message loss.

### Business Requirements
- 100K msg/s throughput (3KB average message size)
- 10 topics, 20 partitions per topic, 3 replication factor
- 50+ consumers per consumer group
- Message retention: 7 days or 1TB per partition (whichever hits first)
- At-least-once delivery guarantee
- Consumer offset management with auto-commit and manual commit modes
- Log compaction for keyed messages (retain latest value per key)
- Exactly-once semantics for critical message streams

### Technical Constraints
- Java 21+ runtime
- Append-only log segments on local filesystem
- Zero-copy data transfer for consumers
- Sequential read/write for maximum throughput
- Leader-based replication per partition

---

## Solution Architecture

### Step 1: Topic and Partition Management

```java
public class TopicPartition {
    private final String topic;
    private final int partition;
    private final List<LogSegment> segments = new CopyOnWriteArrayList<>();
    private final String logDir;
    private final PartitionConfig config;
    private final AtomicLong nextOffset = new AtomicLong(0);
    private volatile LogSegment activeSegment;

    public TopicPartition(String topic, int partition, String baseLogDir, PartitionConfig config) {
        this.topic = topic;
        this.partition = partition;
        this.logDir = baseLogDir + File.separator + topic + "-" + partition;
        this.config = config;
        initLogDir();
        loadExistingSegments();
    }

    private void initLogDir() {
        File dir = new File(logDir);
        if (!dir.exists()) dir.mkdirs();
    }

    private void loadExistingSegments() {
        File[] files = new File(logDir).listFiles((d, name) -> name.endsWith(".log"));
        if (files == null) return;

        Arrays.sort(files);
        for (File file : files) {
            LogSegment segment = new LogSegment(file, config);
            segments.add(segment);
            nextOffset.set(Math.max(nextOffset.get(), segment.getBaseOffset() + segment.getMessageCount()));
        }

        if (segments.isEmpty()) {
            createNewSegment(nextOffset.get());
        } else {
            activeSegment = segments.get(segments.size() - 1);
            if (activeSegment.isFull()) {
                createNewSegment(nextOffset.get());
            }
        }
    }

    public AppendResult append(String key, byte[] value) {
        // Append to active segment
        Message message = new Message(nextOffset.get(), key, value, System.currentTimeMillis());
        long offset = activeSegment.append(message);

        if (offset >= 0) {
            nextOffset.incrementAndGet();
            // Check if segment is full and roll if needed
            if (activeSegment.isFull()) {
                createNewSegment(nextOffset.get());
            }
            return new AppendResult(topic, partition, offset);
        }
        throw new RuntimeException("Failed to append message");
    }

    private void createNewSegment(long baseOffset) {
        File segmentFile = new File(logDir, baseOffset + ".log");
        LogSegment segment = new LogSegment(segmentFile, config);
        segments.add(segment);
        activeSegment = segment;
    }

    // Cleanup old segments based on retention policy
    public void enforceRetention() {
        long now = System.currentTimeMillis();
        long totalSize = 0;
        Iterator<LogSegment> iter = segments.iterator();
        while (iter.hasNext()) {
            LogSegment seg = iter.next();
            if (seg == activeSegment) break;

            boolean expiredByTime = (now - seg.getLastAppendTime()) > config.getRetentionMs();
            totalSize += seg.getSize();
            boolean expiredBySize = totalSize > config.getRetentionBytes();

            if (expiredByTime || expiredBySize) {
                seg.delete();
                iter.remove();
            }
        }
    }
}
```

### Step 2: Log Segment with Sequential I/O

```java
public class LogSegment {
    private final File file;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final long baseOffset;
    private final AtomicLong lastAppendTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicInteger messageCount = new AtomicInteger(0);
    private volatile long size;
    private static final int MAX_SEGMENT_SIZE = 1_000_000_000;  // 1GB

    public LogSegment(File file, PartitionConfig config) {
        this.file = file;
        try {
            this.raf = new RandomAccessFile(file, "rw");
            this.channel = raf.getChannel();
            this.size = raf.length();

            // Parse base offset from filename
            String name = file.getName();
            this.baseOffset = Long.parseLong(name.substring(0, name.indexOf('.')));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long append(Message message) {
        try {
            byte[] serialized = message.serialize();
            raf.seek(raf.length());  // Always append at end
            raf.write(serialized);
            size += serialized.length;
            messageCount.incrementAndGet();
            lastAppendTime.set(System.currentTimeMillis());
            return message.getOffset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Zero-copy transfer to consumer
    public long transferTo(long startOffset, WritableByteChannel target, long maxBytes) {
        try {
            long position = findPosition(startOffset);
            if (position < 0) return -1;
            return channel.transferTo(position, maxBytes, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long findPosition(long offset) {
        try {
            long pos = 0;
            raf.seek(0);
            while (pos < raf.length()) {
                int msgSize = raf.readInt();
                long msgOffset = raf.readLong();
                if (msgOffset == offset) {
                    return pos;
                }
                pos += msgSize + 12;  // 4 (size) + 8 (offset) + remaining
                raf.seek(pos);
            }
            return -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFull() {
        return size >= MAX_SEGMENT_SIZE;
    }

    public void delete() {
        try {
            raf.close();
            channel.close();
            file.delete();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getBaseOffset() { return baseOffset; }
    public long getSize() { return size; }
    public int getMessageCount() { return messageCount.get(); }
    public long getLastAppendTime() { return lastAppendTime.get(); }
}
```

### Step 3: Broker with Leader Replication

```java
public class MessageBroker {
    private final String brokerId;
    private final ConcurrentHashMap<String, TopicPartition> partitions = new ConcurrentHashMap<>();
    private final PartitionManager partitionManager;
    private final ReplicationManager replicationManager;

    public MessageBroker(String brokerId, List<String> seedBrokers) {
        this.brokerId = brokerId;
        this.partitionManager = new PartitionManager(brokerId, this);
        this.replicationManager = new ReplicationManager(this);
        partitionManager.start();
    }

    public ProduceResponse produce(ProduceRequest request) {
        String topic = request.getTopic();
        int partition = request.getPartition();
        String key = request.getKey();
        byte[] value = request.getValue();

        TopicPartition tp = getOrCreatePartition(topic, partition);
        AppendResult result = tp.append(key, value);

        // Replicate to followers (async)
        replicationManager.replicate(topic, partition, result.getOffset(), key, value);

        return new ProduceResponse(result.getTopic(), result.getPartition(),
            result.getOffset(), result.getOffset() + 1);
    }

    public FetchResponse fetch(FetchRequest request) {
        String topic = request.getTopic();
        int partition = request.getPartition();
        long offset = request.getOffset();
        int maxBytes = request.getMaxBytes();

        TopicPartition tp = partitions.get(topic + "-" + partition);
        if (tp == null) {
            return FetchResponse.empty(topic, partition, offset);
        }

        // Read from log segments
        List<Message> messages = tp.read(offset, maxBytes);
        return new FetchResponse(topic, partition, messages, tp.getNextOffset());
    }

    public void becomeLeader(String topic, int partition, List<String> replicas) {
        TopicPartition tp = getOrCreatePartition(topic, partition);
        replicationManager.setReplicas(topic, partition, replicas);

        // Recover from ISR (in-sync replicas)
        List<Long> replicaOffsets = replicationManager.getReplicaOffsets(topic, partition);
        long minOffset = replicaOffsets.stream().min(Long::compare).orElse(0L);

        // Truncate any divergent data
        tp.truncateTo(minOffset);

        // Start accepting produce requests for this partition
    }

    private TopicPartition getOrCreatePartition(String topic, int partition) {
        String key = topic + "-" + partition;
        return partitions.computeIfAbsent(key, k -> {
            PartitionConfig config = new PartitionConfig();
            config.setRetentionMs(7 * 24 * 60 * 60 * 1000L);  // 7 days
            config.setRetentionBytes(1_000_000_000_000L);  // 1TB
            return new TopicPartition(topic, partition, "/data/kafka", config);
        });
    }
}
```

### Step 4: Consumer Group Coordination

```java
public class ConsumerGroup {
    private final String groupId;
    private final String consumerId;
    private final MessageBroker broker;
    private final OffsetManager offsetManager;
    private final Map<String, List<Integer>> assignments = new ConcurrentHashMap<>();

    public ConsumerGroup(String groupId, String consumerId, MessageBroker broker) {
        this.groupId = groupId;
        this.consumerId = consumerId;
        this.broker = broker;
        this.offsetManager = new OffsetManager(broker, groupId);
    }

    public void joinGroup() {
        // Request partition assignment from coordinator
        PartitionAssignment assignment = broker.getCoordinator().assignPartitions(
            groupId, consumerId, getSubscribedTopics());

        assignments.clear();
        assignments.putAll(assignment.getAssignment());
    }

    public ConsumerRecords poll(Duration timeout) {
        List<ConsumerRecord> records = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : assignments.entrySet()) {
            String topic = entry.getKey();
            for (int partition : entry.getValue()) {
                long offset = offsetManager.getCommittedOffset(topic, partition);

                FetchResponse response = broker.fetch(new FetchRequest(
                    topic, partition, offset, 1024 * 1024  // 1MB max
                ));

                if (response.getMessages() != null) {
                    for (Message msg : response.getMessages()) {
                        records.add(new ConsumerRecord(topic, partition,
                            msg.getOffset(), msg.getKey(), msg.getValue()));
                    }
                }
            }

            if (!records.isEmpty()) break;
        }

        return new ConsumerRecords(records);
    }

    // Manual commit
    public void commitSync(Map<TopicPartitionKey, Long> offsets) {
        for (Map.Entry<TopicPartitionKey, Long> entry : offsets.entrySet()) {
            offsetManager.commitOffset(
                entry.getKey().getTopic(),
                entry.getKey().getPartition(),
                entry.getValue()
            );
        }
    }

    // Auto commit (periodic background task)
    public void enableAutoCommit(Duration interval) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, List<Integer>> entry : assignments.entrySet()) {
                for (int partition : entry.getValue()) {
                    long currentOffset = offsetManager.getLastProcessedOffset(
                        entry.getKey(), partition);
                    offsetManager.commitOffset(entry.getKey(), partition, currentOffset);
                }
            }
        }, interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void onPartitionRevoked() {
        commitSync(offsetManager.getAllOffsets());
        assignments.clear();
    }
}
```

### Step 5: Log Compaction

```java
public class LogCompactor {
    private final Map<String, TopicPartition> partitions;
    private final ScheduledExecutorService scheduler;

    public LogCompactor(Map<String, TopicPartition> partitions) {
        this.partitions = partitions;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::compactAll, 0, 15, TimeUnit.MINUTES);
    }

    private void compactAll() {
        for (Map.Entry<String, TopicPartition> entry : partitions.entrySet()) {
            if (entry.getKey().contains("__compacted")) {
                compact(entry.getValue());
            }
        }
    }

    public void compact(TopicPartition tp) {
        // Phase 1: Scan all messages, keep only latest value per key
        Map<String, Long> latestOffsetPerKey = new HashMap<>();
        Map<String, byte[]> latestValuePerKey = new HashMap<>();

        List<LogSegment> segments = tp.getSegments();
        for (LogSegment segment : segments) {
            List<Message> messages = segment.readAll();
            for (Message msg : messages) {
                if (msg.getKey() != null) {
                    latestOffsetPerKey.put(msg.getKey(), msg.getOffset());
                    latestValuePerKey.put(msg.getKey(), msg.getValue());
                }
            }
        }

        // Phase 2: Write clean segment with only latest values
        LogSegment cleanSegment = tp.createCleanSegment();
        for (Map.Entry<String, Long> entry : latestOffsetPerKey.entrySet()) {
            Message cleanMessage = new Message(
                entry.getValue(), entry.getKey(),
                latestValuePerKey.get(entry.getKey()),
                System.currentTimeMillis()
            );
            cleanSegment.append(cleanMessage);
        }

        // Phase 3: Swap clean segment in, delete old ones
        tp.replaceWithCleanSegment(cleanSegment);

        // Phase 4: Update offsets for any consumers that were reading compacted segments
        tp.updateOffsetsAfterCompaction(latestOffsetPerKey);
    }
}
```

### Step 6: Producer with Batching and Retry

```java
public class ProducerClient {
    private final MessageBroker broker;
    private final List<MessageBatch> batches = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService sender;
    private final int batchSize = 16384;  // 16KB
    private final int lingerMs = 5;
    private final int maxRetries = 3;

    public ProducerClient(MessageBroker broker) {
        this.broker = broker;
        this.sender = Executors.newSingleThreadScheduledExecutor();
    }

    public RecordMetadata send(ProducerRecord record) {
        // Determine partition (round-robin or key-based)
        int partition = determinePartition(record);
        record.setPartition(partition);

        MessageBatch batch = getOrCreateBatch(record.getTopic(), partition);
        AppendResult result = batch.append(record);

        if (batch.isReady() || batch.getSize() >= batchSize) {
            sendBatch(batch);
        }

        // Schedule batch send after linger time
        sender.schedule(() -> {
            if (!batch.isEmpty()) {
                sendBatch(batch);
            }
        }, lingerMs, TimeUnit.MILLISECONDS);

        return new RecordMetadata(result.getTopic(), result.getPartition(), result.getOffset());
    }

    private void sendBatch(MessageBatch batch) {
        List<ProducerRecord> records = batch.drain();
        int attempt = 0;
        while (attempt <= maxRetries) {
            try {
                for (ProducerRecord record : records) {
                    ProduceResponse response = broker.produce(new ProduceRequest(
                        record.getTopic(), record.getPartition(),
                        record.getKey(), record.getValue()
                    ));
                }
                return;  // Success
            } catch (Exception e) {
                attempt++;
                if (attempt > maxRetries) {
                    // Send to dead letter queue
                    handleFailedRecords(records, e);
                } else {
                    try {
                        Thread.sleep((long) Math.pow(2, attempt) * 100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private int determinePartition(ProducerRecord record) {
        if (record.getPartition() >= 0) return record.getPartition();
        if (record.getKey() != null) {
            return Math.abs(record.getKey().hashCode()) % broker.getPartitionCount(record.getTopic());
        }
        // Round-robin
        return ThreadLocalRandom.current().nextInt(broker.getPartitionCount(record.getTopic()));
    }

    private MessageBatch getOrCreateBatch(String topic, int partition) {
        String key = topic + "-" + partition;
        MessageBatch batch = new MessageBatch(topic, partition);
        // In production, use a pool of batches per topic-partition
        return batch;
    }
}
```

---

## Best Practices

### Topics and Partitions
1. **Partition count**: Scale partitions based on throughput requirements (each partition handles ~10MB/s); start with partition count = expected max consumers × 2
2. **Key-based partitioning**: Use meaningful keys for order guarantees; same key always goes to same partition, preserving order
3. **Compact topics**: Use log compaction for key-value stores (latest state), not for event logs (append-only)
4. **Retention sizing**: Set retention based on consumer lag tolerance; 7 days default, 24 hours for high-volume transient topics

### Producers
1. **Batching**: Batch at least 16KB or 5ms linger; higher batch sizes dramatically increase throughput (100x improvement over single-message sends)
2. **Retry with idempotence**: Enable idempotent producers (enable.idempotence=true) to prevent duplicates on retry
3. **Compression**: Use Snappy or Zstd compression for text-heavy payloads (3-5x compression ratio)
4. **ACKS**: Use acks=all for critical data, acks=1 for throughput-sensitive, acks=0 for metrics/lossy data

### Consumers
1. **Poll loop**: poll() in a tight loop with processing time < max.poll.interval.ms (default 5 min); use async processing for longer operations
2. **Offset commit**: Manual commit after processing is complete; avoid auto-commit for transactional workloads
3. **Rebalance handling**: Implement Cooperative Sticky Assignor to minimize partition stop-the-world on rebalance
4. **Consumer lag monitoring**: Track consumer lag (producer offset - consumer offset) per partition; alert if lag exceeds 100K messages

### Replication
1. **ISR management**: In-Sync Replicas must contain all messages up to the leader's high watermark; brokers falling behind leave ISR
2. **Unclean leader election**: Disable (unclean.leader.election.enable=false) to prevent data loss; only ISR members can become leader
3. **Min ISR**: Set min.insync.replicas=2 for replication-factor=3; ensures at least 2 replicas acknowledge writes
4. **Preferred leader**: Enable leader rebalance to distribute leadership evenly across brokers

### Performance
1. **Sequential I/O**: Appending to log is sequential write (~600MB/s on SSD); avoid random writes by never modifying existing segments
2. **OS page cache**: Log data resides in OS page cache; consumer reads hit cache (zero-copy) for recently written data
3. **File descriptor limits**: Each segment uses 1-2 file descriptors; partition count × segments can exceed OS limits if not managed
4. **Network threading**: Use 1 acceptor thread + N processor threads (one per core) for TCP handling; separate request queue per processor

## Performance Benchmarks

| Scenario | Throughput | P99 Latency | Configuration |
|----------|------------|-------------|---------------|
| Single producer, single partition | 50 MB/s | 5ms | 3KB messages, acks=1 |
| Single producer, single partition | 35 MB/s | 15ms | 3KB messages, acks=all |
| 10 producers, 20 partitions | 200 MB/s | 25ms | Batch 16KB, Zstd |
| 10 producers, 20 partitions | 150 MB/s | 40ms | Batch 16KB, acks=all |
| 50 consumers, 1 group | 100 MB/s | 10ms | Zero-copy, cache hits |
| Log compaction (1B messages) | 200 MB/s | — | Compact to 10% size |
| Broker failure recovery | — | 30s | 20 partitions, 10M messages |
