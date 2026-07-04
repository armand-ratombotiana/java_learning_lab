# Distributed ID Generation: Code Deep Dive

## Complete Snowflake Implementation

```java
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeGenerator {
    private static final long EPOCH = 1700000000000L;
    
    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;
    private final Object lock = new Object();
    
    public SnowflakeGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                "Worker ID must be between 0 and " + MAX_WORKER_ID);
        }
        this.workerId = workerId;
    }
    
    public long nextId() {
        synchronized (lock) {
            long timestamp = timeGen();
            
            if (timestamp < lastTimestamp) {
                long offset = lastTimestamp - timestamp;
                if (offset <= 5) {
                    // Wait for clock to catch up
                    while (timestamp < lastTimestamp) {
                        timestamp = timeGen();
                    }
                } else {
                    throw new IllegalStateException(
                        "Clock moved backwards by " + offset + "ms");
                }
            }
            
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                if (sequence == 0) {
                    // Wait for next millisecond
                    while (timestamp <= lastTimestamp) {
                        timestamp = timeGen();
                    }
                }
            } else {
                sequence = 0L;
            }
            
            lastTimestamp = timestamp;
            
            return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                 | (workerId << WORKER_ID_SHIFT)
                 | sequence;
        }
    }
    
    public long[] nextBatch(int count) {
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = nextId();
        }
        return ids;
    }
    
    private long timeGen() {
        return System.currentTimeMillis();
    }
    
    // Decode an ID back to its components
    public static Map<String, Long> parse(long id) {
        Map<String, Long> parts = new HashMap<>();
        long sequence = id & MAX_SEQUENCE;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;
        
        parts.put("timestamp", timestamp);
        parts.put("workerId", workerId);
        parts.put("sequence", sequence);
        return parts;
    }
}
```
