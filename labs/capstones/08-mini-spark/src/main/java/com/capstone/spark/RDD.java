package com.capstone.spark;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class RDD<T> {
    private final List<T> data;
    private final List<String> dependencies;
    private final String partitioner;
    private final int numPartitions;

    public RDD(List<T> data) {
        this(data, "none", 1);
    }

    public RDD(List<T> data, int numPartitions) {
        this(data, "none", numPartitions);
    }

    private RDD(List<T> data, String partitioner, int numPartitions) {
        this.data = new CopyOnWriteArrayList<>(data);
        this.dependencies = new ArrayList<>();
        this.partitioner = partitioner;
        this.numPartitions = numPartitions;
    }

    public <R> RDD<R> map(Function<T, R> f) {
        List<R> result = data.parallelStream().map(f).collect(Collectors.toList());
        RDD<R> newRdd = new RDD<>(result, partitioner, numPartitions);
        newRdd.dependencies.add("map");
        return newRdd;
    }

    public RDD<T> filter(Predicate<T> f) {
        List<T> result = data.parallelStream().filter(f).collect(Collectors.toList());
        RDD<T> newRdd = new RDD<>(result, partitioner, numPartitions);
        newRdd.dependencies.add("filter");
        return newRdd;
    }

    public <R> RDD<R> flatMap(Function<T, List<R>> f) {
        List<R> result = data.parallelStream()
            .map(f)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        RDD<R> newRdd = new RDD<>(result, partitioner, numPartitions);
        newRdd.dependencies.add("flatMap");
        return newRdd;
    }

    public <K, V> PairRDD<K, V> mapToPair(Function<T, Map.Entry<K, V>> f) {
        Map<K, List<V>> pairs = new ConcurrentHashMap<>();
        data.parallelStream().map(f).forEach(e ->
            pairs.computeIfAbsent(e.getKey(), k -> new CopyOnWriteArrayList<>()).add(e.getValue()));
        return new PairRDD<>(pairs, numPartitions);
    }

    public RDD<T> distinct() {
        List<T> result = data.parallelStream().distinct().collect(Collectors.toList());
        return new RDD<>(result, numPartitions);
    }

    public RDD<T> union(RDD<T> other) {
        List<T> result = new ArrayList<>(this.data);
        result.addAll(other.data);
        return new RDD<>(result, numPartitions);
    }

    public RDD<T> intersection(RDD<T> other) {
        Set<T> otherSet = new ConcurrentHashSet<>();
        other.data.forEach(otherSet::add);
        List<T> result = data.parallelStream().filter(otherSet::contains).collect(Collectors.toList());
        return new RDD<>(result, numPartitions);
    }

    public List<T> collect() { return List.copyOf(data); }

    public long count() { return data.size(); }

    public Optional<T> reduce(BinaryOperator<T> op) {
        return data.parallelStream().reduce(op);
    }

    public T first() {
        if (data.isEmpty()) throw new NoSuchElementException("RDD is empty");
        return data.get(0);
    }

    public List<T> take(int n) {
        return data.stream().limit(n).collect(Collectors.toList());
    }

    public void foreach(Consumer<T> f) { data.forEach(f); }

    public RDD<T> cache() {
        return this;
    }

    public RDD<T> repartition(int numPartitions) {
        return new RDD<>(new ArrayList<>(data), numPartitions);
    }

    public RDD<T> coalesce(int numPartitions) {
        return new RDD<>(new ArrayList<>(data), Math.min(numPartitions, this.numPartitions));
    }

    public List<String> getDependencies() { return List.copyOf(dependencies); }

    public int getNumPartitions() { return numPartitions; }

    public long count() { return data.size(); }

    public boolean isEmpty() { return data.isEmpty(); }

    private static class ConcurrentHashSet<T> {
        private final ConcurrentHashMap<T, Boolean> map = new ConcurrentHashMap<>();
        void add(T t) { map.put(t, Boolean.TRUE); }
        boolean contains(T t) { return map.containsKey(t); }
    }
}
