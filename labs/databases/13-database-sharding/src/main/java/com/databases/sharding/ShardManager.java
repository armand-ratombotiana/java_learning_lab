package com.databases.sharding;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ShardManager {
    private final ConsistentHashRing<String> hashRing;
    private final Map<String, ShardStats> stats = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean rebalancing = false;

    public static class ShardStats {
        private final String id;
        private final AtomicLong dataSize = new AtomicLong();
        private final AtomicLong queryCount = new AtomicLong();
        private final AtomicLong errorCount = new AtomicLong();
        public ShardStats(String id) { this.id = id; }
        public String getId() { return id; }
        public long getDataSize() { return dataSize.get(); }
        public long getQueryCount() { return queryCount.get(); }
        public long getErrorCount() { return errorCount.get(); }
        public void recordWrite(long b) { dataSize.addAndGet(b); queryCount.incrementAndGet(); }
        public void recordRead() { queryCount.incrementAndGet(); }
        public void recordError() { errorCount.incrementAndGet(); }
    }

    public ShardManager(int vnodes) {
        this.hashRing = new ConsistentHashRing<>(vnodes);
        scheduler.scheduleAtFixedRate(() -> {
            if (!rebalancing && getSkewFactor() > 0.3) rebalance();
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void addShard(String id) { hashRing.addNode(id); stats.put(id, new ShardStats(id)); }
    public void removeShard(String id) { hashRing.removeNode(id); stats.remove(id); }

    public String routeKey(String key) {
        String shard = hashRing.getNode(key);
        stats.get(shard).recordRead();
        return shard;
    }

    public void recordWrite(String shard, long bytes) { stats.get(shard).recordWrite(bytes); }
    public void recordError(String shard) { stats.get(shard).recordError(); }
    public ShardStats getStats(String id) { return stats.get(id); }
    public Map<String, ShardStats> getAllStats() { return Collections.unmodifiableMap(stats); }

    public double getSkewFactor() {
        var vals = stats.values();
        if (vals.isEmpty()) return 0;
        double avg = vals.stream().mapToLong(ShardStats::getDataSize).average().orElse(0);
        double max = vals.stream().mapToLong(ShardStats::getDataSize).max().orElse(0);
        return avg == 0 ? 0 : (max / avg) - 1;
    }

    public CompletableFuture<Void> rebalance() {
        rebalancing = true;
        return CompletableFuture.runAsync(() -> {
            try { Thread.sleep(100); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { rebalancing = false; }
        });
    }

    public boolean isRebalancing() { return rebalancing; }
    public void shutdown() { scheduler.shutdown(); }

    public List<List<String>> getAllocations(Set<String> keys) {
        Map<String, List<String>> alloc = new LinkedHashMap<>();
        for (String k : keys) alloc.computeIfAbsent(routeKey(k), x -> new ArrayList<>()).add(k);
        return new ArrayList<>(alloc.values());
    }
}
