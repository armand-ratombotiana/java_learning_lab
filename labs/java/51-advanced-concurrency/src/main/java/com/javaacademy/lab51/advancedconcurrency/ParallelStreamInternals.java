package com.javaacademy.lab51.advancedconcurrency;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

/**
 * Reveals how parallel streams internally use Spliterator for
 * partitioning and work-stealing. A custom Spliterator demonstrates
 * the mechanics of splitting, tryAdvance, and characteristics
 * that drive parallel performance.
 */
public class ParallelStreamInternals {

    public static void main(String[] args) {
        System.out.println("=== Parallel Stream Internals ===");

        // Standard parallel stream — uses built-in Spliterator
        long sum = IntStream.range(0, 10_000_000)
            .parallel()
            .map(i -> i * 2)
            .sum();
        System.out.println("Parallel sum: " + sum);

        // Custom Spliterator demo
        var data = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) data.add(i);

        var spliterator = new RangeSpliterator(data, 0, data.size());

        System.out.println("Spliterator characteristics: " + spliterator.characteristics());
        System.out.println("Estimated size: " + spliterator.estimateSize());

        // Show splitting
        var splits = new ArrayList<Spliterator<Integer>>();
        splits.add(spliterator);
        while (!splits.isEmpty()) {
            var current = splits.remove(0);
            var child = current.trySplit();
            if (child != null) {
                System.out.println("Split: parent=" + current.estimateSize()
                    + " child=" + child.estimateSize());
                splits.add(current);
                splits.add(child);
            } else {
                // Process leaf
                var list = new java.util.ArrayList<Integer>();
                current.forEachRemaining(list::add);
                System.out.println("Leaf batch: " + list.size() + " elements");
            }
        }

        // Using custom Spliterator with parallel stream
        long customSum = StreamSupport.stream(
            new RangeSpliterator(data, 0, data.size()), true)
            .mapToInt(Integer::intValue)
            .sum();
        System.out.println("Custom parallel sum: " + customSum);
    }

    static class RangeSpliterator extends Spliterators.AbstractSpliterator<Integer> {
        private final List<Integer> source;
        private int start, end;

        RangeSpliterator(List<Integer> source, int start, int end) {
            super(end - start, SIZED | SUBSIZED | ORDERED | IMMUTABLE);
            this.source = source;
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Integer> action) {
            if (start >= end) return false;
            action.accept(source.get(start++));
            return true;
        }

        @Override
        public Spliterator<Integer> trySplit() {
            int mid = (start + end) >>> 1;
            if (mid == start) return null;
            var split = new RangeSpliterator(source, start, mid);
            start = mid;
            return split;
        }

        @Override
        public long estimateSize() { return end - start; }
    }
}
