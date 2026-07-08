package com.distributed.queues;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryPartitionedQueue<T> implements DistributedQueue<T> {
    private final int partitionCount;
    private final List<ConcurrentLinkedQueue<Message<T>>> partitions;
    private final ConcurrentHashMap<String, Message<T>> messages = new ConcurrentHashMap<>();
    private final AtomicLong messageIdGen = new AtomicLong(0);
    private final AtomicInteger partitionCounter = new AtomicInteger(0);

    public record Message<T>(String id, T payload, int partition) {}

    public InMemoryPartitionedQueue(int partitionCount) {
        this.partitionCount = partitionCount;
        this.partitions = new ArrayList<>(partitionCount);
        for (int i = 0; i < partitionCount; i++) {
            partitions.add(new ConcurrentLinkedQueue<>());
        }
    }

    @Override
    public String send(T message) {
        int partition = Math.abs(partitionCounter.getAndIncrement() % partitionCount);
        String msgId = "msg-" + messageIdGen.incrementAndGet();
        Message<T> msg = new Message<>(msgId, message, partition);
        messages.put(msgId, msg);
        partitions.get(partition).add(msg);
        return msgId;
    }

    public String sendWithKey(T message, String key) {
        int partition = Math.abs(key.hashCode() % partitionCount);
        String msgId = "msg-" + messageIdGen.incrementAndGet();
        Message<T> msg = new Message<>(msgId, message, partition);
        messages.put(msgId, msg);
        partitions.get(partition).add(msg);
        return msgId;
    }

    @Override
    public Optional<T> receive(String consumerId) {
        int consumerPartition = Math.abs(consumerId.hashCode() % partitionCount);
        Message<T> msg = partitions.get(consumerPartition).poll();
        if (msg == null) {
            for (int i = 0; i < partitionCount; i++) {
                msg = partitions.get(i).poll();
                if (msg != null) break;
            }
        }
        return Optional.ofNullable(msg).map(Message::payload);
    }

    @Override
    public void acknowledge(String messageId) {
        messages.remove(messageId);
    }

    @Override
    public void nack(String messageId) {
        Message<T> msg = messages.get(messageId);
        if (msg != null) {
            partitions.get(msg.partition()).add(msg);
        }
    }

    @Override
    public int getSize() {
        return messages.size();
    }

    public int getPartitionCount() { return partitionCount; }
    public int getPartitionSize(int partition) { return partitions.get(partition).size(); }
}
