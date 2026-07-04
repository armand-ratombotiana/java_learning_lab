# How Distributed ID Generation Works

## Twitter Snowflake Implementation

```java
public class SnowflakeIdGenerator {
    private final long workerId;
    private final long datacenterId;
    private final long sequence;
    
    // Bit allocations
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;
    
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampShift = sequenceBits + workerIdBits + datacenterIdBits;
    
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    
    private long lastTimestamp = -1L;
    private long currentSequence = 0L;
    
    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = 0L;
    }
    
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        
        if (timestamp < lastTimestamp) {
            throw new ClockDriftException("Clock moved backwards");
        }
        
        if (timestamp == lastTimestamp) {
            currentSequence = (currentSequence + 1) & sequenceMask;
            if (currentSequence == 0) {
                // Sequence exhausted, wait for next millisecond
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            currentSequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        return ((timestamp - EPOCH) << timestampShift)
             | (datacenterId << datacenterIdShift)
             | (workerId << workerIdShift)
             | currentSequence;
    }
}
```

## UUID v7 Generation

```java
import java.util.UUID;
import java.security.SecureRandom;

public class UUIDv7Generator {
    private static final SecureRandom random = new SecureRandom();
    
    public static UUID generate() {
        long timestamp = System.currentTimeMillis();
        long msb = (timestamp << 16) | (0x7 << 12) | (random.nextLong() & 0xFFF);
        long lsb = (0x80L << 56) | (random.nextLong() & 0x3FFFFFFFFFFFFFFFL);
        return new UUID(msb, lsb);
    }
}
```
