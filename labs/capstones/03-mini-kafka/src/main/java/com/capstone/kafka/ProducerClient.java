package com.capstone.kafka;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ProducerClient {
    private final MessageBroker broker;
    private final ProducerConfig config;
    private final List<ProducerRecord> pendingBatch = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong(0);
    private ScheduledExecutorService scheduler;

    public record ProducerConfig(String clientId, boolean async, int batchSize, int lingerMs, String acks) {
        public ProducerConfig {
            if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("Client ID required");
            acks = acks == null ? "1" : acks;
        }
    }

    public record ProducerRecord(String topic, byte[] key, byte[] value, Map<String, String> headers,
                                  long timestamp, long sequence) {}

    public ProducerClient(MessageBroker broker, ProducerConfig config) {
        this.broker = broker;
        this.config = config;
        if (config.async()) startBatchScheduler();
    }

    public ProducerRecord send(String topic, byte[] key, byte[] value) {
        return send(topic, key, value, Map.of());
    }

    public ProducerRecord send(String topic, byte[] key, byte[] value, Map<String, String> headers) {
        ProducerRecord record = new ProducerRecord(topic, key, value, headers,
            System.currentTimeMillis(), sequence.incrementAndGet());
        if (config.async()) {
            pendingBatch.add(record);
            if (pendingBatch.size() >= config.batchSize()) flush();
        } else {
            broker.produce(topic, key, value, headers);
        }
        return record;
    }

    public synchronized int flush() {
        int count = pendingBatch.size();
        for (ProducerRecord r : pendingBatch) {
            broker.produce(r.topic(), r.key(), r.value(), r.headers());
        }
        pendingBatch.clear();
        return count;
    }

    public synchronized int pendingCount() { return pendingBatch.size(); }

    public ProducerConfig getConfig() { return config; }

    public void close() {
        flush();
        if (scheduler != null) scheduler.shutdown();
    }

    private void startBatchScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::flush, config.lingerMs(), config.lingerMs(), TimeUnit.MILLISECONDS);
    }
}
