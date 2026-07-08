package com.distributed.idgeneration;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeIdGenerator implements IdGenerator<Long> {
    private final long epoch;
    private final long workerId;
    private final int workerIdBits;
    private final int sequenceBits;
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private final AtomicInteger sequence = new AtomicInteger(0);

    public SnowflakeIdGenerator(long epoch, long workerId, int workerIdBits, int sequenceBits) {
        long maxWorkerId = (1L << workerIdBits) - 1;
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + maxWorkerId);
        }
        this.epoch = epoch;
        this.workerId = workerId;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;
    }

    public SnowflakeIdGenerator(long workerId) {
        this(1700000000000L, workerId, 10, 12);
    }

    @Override
    public Long generate() {
        long timestamp = System.currentTimeMillis();
        long lastTs = lastTimestamp.get();

        if (timestamp < lastTs) {
            long diff = lastTs - timestamp;
            if (diff < 10) {
                while (System.currentTimeMillis() < lastTs) {
                    Thread.yield();
                }
                timestamp = System.currentTimeMillis();
            } else {
                throw new IllegalStateException("Clock moved backwards by " + diff + "ms");
            }
        }

        if (timestamp == lastTs) {
            int seq = sequence.incrementAndGet();
            int maxSeq = (1 << sequenceBits) - 1;
            if (seq > maxSeq) {
                while (System.currentTimeMillis() <= timestamp) {
                    Thread.yield();
                }
                timestamp = System.currentTimeMillis();
                sequence.set(0);
            }
        } else {
            sequence.set(0);
        }

        lastTimestamp.set(timestamp);

        long id = ((timestamp - epoch) << (workerIdBits + sequenceBits))
                | (workerId << sequenceBits)
                | sequence.get();
        return id;
    }

    @Override
    public long extractTimestamp(Long id) {
        return epoch + (id >> (workerIdBits + sequenceBits));
    }

    public long getWorkerId() { return workerId; }
}
