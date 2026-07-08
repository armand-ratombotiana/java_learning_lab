package com.databases.sharding;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RangePartitioner<K extends Comparable<K>> {
    private final NavigableMap<K, String> rangeMap = new TreeMap<>();
    private final Map<String, ShardInfo> shards = new ConcurrentHashMap<>();

    public static class ShardInfo {
        private final String shardId;
        private final long maxSize;
        private long currentSize;
        public ShardInfo(String id, long max) { shardId = id; maxSize = max; }
        public String getShardId() { return shardId; }
        public double getUtilization() { return (double) currentSize / maxSize; }
        public void addData(long s) { currentSize += s; }
    }

    public void addRange(K end, String shardId, long maxSize) {
        rangeMap.put(end, shardId);
        shards.put(shardId, new ShardInfo(shardId, maxSize));
    }

    public String getShardForKey(K key) {
        Map.Entry<K, String> e = rangeMap.ceilingEntry(key);
        if (e == null) throw new IllegalArgumentException("No shard for key: " + key);
        return e.getValue();
    }

    public List<String> getShardsForRange(K start, K end) {
        Set<String> affected = new LinkedHashSet<>();
        for (Map.Entry<K, String> e : rangeMap.entrySet()) {
            if (e.getKey().compareTo(start) >= 0 && e.getKey().compareTo(end) <= 0)
                affected.add(e.getValue());
        }
        return new ArrayList<>(affected);
    }

    public ShardInfo getShardInfo(String id) { return shards.get(id); }
    public Collection<ShardInfo> getAllShards() { return shards.values(); }
}
