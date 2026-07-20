package com.capstone.spark;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class PairRDD<K, V> {
    private final Map<K, List<V>> pairs;
    private final int numPartitions;

    public PairRDD(Map<K, List<V>> pairs, int numPartitions) {
        this.pairs = new ConcurrentHashMap<>();
        for (Map.Entry<K, List<V>> e : pairs.entrySet()) {
            this.pairs.put(e.getKey(), new CopyOnWriteArrayList<>(e.getValue()));
        }
        this.numPartitions = numPartitions;
    }

    public PairRDD<K, V> reduceByKey(BinaryOperator<V> func) {
        Map<K, V> result = new ConcurrentHashMap<>();
        pairs.forEach((k, vList) -> {
            V reduced = vList.stream().reduce(func).orElse(null);
            if (reduced != null) result.put(k, reduced);
        });
        Map<K, List<V>> resultPairs = new ConcurrentHashMap<>();
        result.forEach((k, v) -> resultPairs.put(k, new CopyOnWriteArrayList<>(List.of(v))));
        return new PairRDD<>(resultPairs, numPartitions);
    }

    public PairRDD<K, V> groupByKey() {
        return this;
    }

    public PairRDD<K, V> sortByKey() {
        List<K> sortedKeys = new ArrayList<>(pairs.keySet());
        Collections.sort((List) sortedKeys);
        Map<K, List<V>> sorted = new LinkedHashMap<>();
        for (K key : sortedKeys) sorted.put(key, pairs.get(key));
        return new PairRDD<>(sorted, numPartitions);
    }

    public <W> PairRDD<K, AbstractMap.SimpleEntry<V, W>> join(PairRDD<K, W> other) {
        Map<K, List<AbstractMap.SimpleEntry<V, W>>> joined = new ConcurrentHashMap<>();
        pairs.forEach((k, vList) -> {
            List<W> otherValues = other.pairs.get(k);
            if (otherValues != null) {
                for (V v : vList) {
                    for (W w : otherValues) {
                        joined.computeIfAbsent(k, _k -> new CopyOnWriteArrayList<>())
                            .add(new AbstractMap.SimpleEntry<>(v, w));
                    }
                }
            }
        });
        return new PairRDD<>(joined, numPartitions);
    }

    public PairRDD<K, V> leftOuterJoin(PairRDD<K, V> other) {
        Map<K, List<V>> result = new ConcurrentHashMap<>(pairs);
        other.pairs.forEach((k, vList) -> {
            if (!result.containsKey(k)) result.put(k, vList);
        });
        return new PairRDD<>(result, numPartitions);
    }

    public RDD<Map.Entry<K, V>> toRDD() {
        List<Map.Entry<K, V>> entries = new ArrayList<>();
        pairs.forEach((k, vList) -> vList.forEach(v -> entries.add(new AbstractMap.SimpleEntry<>(k, v))));
        RDD<Map.Entry<K, V>> rdd = new RDD<>(entries, numPartitions);
        return rdd;
    }

    public Map<K, List<V>> collectAsMap() { return Map.copyOf(pairs); }

    public Set<K> keys() { return pairs.keySet(); }

    public long count() { return pairs.values().stream().mapToLong(List::size).sum(); }

    public int numPartitions() { return numPartitions; }

    public RDD<V> values() {
        List<V> allValues = pairs.values().stream().flatMap(List::stream).collect(Collectors.toList());
        return new RDD<>(allValues, numPartitions);
    }

    public RDD<K> keys() {
        List<K> allKeys = new ArrayList<>(pairs.keySet());
        return new RDD<>(allKeys, numPartitions);
    }
}
