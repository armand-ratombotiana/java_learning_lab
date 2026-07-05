package com.sd.consistency;

import java.util.*;
import java.util.concurrent.atomic.*;

public class CrdtImplementation {

    public interface CRDT<T extends CRDT<T>> {
        T merge(T other);
    }

    public static class GCounter implements CRDT<GCounter> {
        private final Map<String, Long> counters = new ConcurrentHashMap<>();

        public void increment(String node) {
            counters.merge(node, 1L, Long::sum);
        }

        public long value() {
            return counters.values().stream().mapToLong(Long::longValue).sum();
        }

        @Override
        public GCounter merge(GCounter other) {
            GCounter merged = new GCounter();
            Set<String> allKeys = new HashSet<>(counters.keySet());
            allKeys.addAll(other.counters.keySet());
            for (String key : allKeys) {
                long v1 = counters.getOrDefault(key, 0L);
                long v2 = other.counters.getOrDefault(key, 0L);
                merged.counters.put(key, Math.max(v1, v2));
            }
            return merged;
        }

        @Override
        public String toString() { return "GCounter=" + value(); }
    }

    public static class PNCounter implements CRDT<PNCounter> {
        private final GCounter positives = new GCounter();
        private final GCounter negatives = new GCounter();

        public void increment(String node) { positives.increment(node); }
        public void decrement(String node) { negatives.increment(node); }

        public long value() { return positives.value() - negatives.value(); }

        @Override
        public PNCounter merge(PNCounter other) {
            PNCounter merged = new PNCounter();
            merged.positives.merge(other.positives);
            merged.negatives.merge(other.negatives);
            return merged;
        }

        @Override
        public String toString() { return "PNCounter=" + value(); }
    }

    public static class GSet implements CRDT<GSet> {
        private final Set<String> elements = ConcurrentHashMap.newKeySet();

        public void add(String element) { elements.add(element); }
        public boolean contains(String e) { return elements.contains(e); }
        public Set<String> value() { return new HashSet<>(elements); }

        @Override
        public GSet merge(GSet other) {
            GSet merged = new GSet();
            merged.elements.addAll(elements);
            merged.elements.addAll(other.elements);
            return merged;
        }

        @Override
        public String toString() { return "GSet" + elements; }
    }

    public static void main(String[] args) {
        System.out.println("=== CRDT: G-Counter ===");
        GCounter c1 = new GCounter();
        c1.increment("A");
        c1.increment("A");
        c1.increment("B");
        System.out.println("c1: " + c1);

        GCounter c2 = new GCounter();
        c2.increment("B");
        c2.increment("B");
        c2.increment("C");
        System.out.println("c2: " + c2);

        GCounter merged = c1.merge(c2);
        System.out.println("Merged: " + merged);

        System.out.println("\n=== CRDT: PN-Counter ===");
        PNCounter pn = new PNCounter();
        pn.increment("A");
        pn.increment("A");
        pn.decrement("A");
        System.out.println("PNCounter: " + pn);

        System.out.println("\n=== CRDT: G-Set ===");
        GSet s1 = new GSet();
        s1.add("a"); s1.add("b");
        GSet s2 = new GSet();
        s2.add("b"); s2.add("c");
        System.out.println("s1: " + s1.value() + " s2: " + s2.value());
        System.out.println("Merged: " + s1.merge(s2).value());
    }
}
