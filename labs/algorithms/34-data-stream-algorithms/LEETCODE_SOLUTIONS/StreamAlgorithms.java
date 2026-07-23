package com.algorithms.stream;

import java.util.*;

/**
 * Custom: Data Stream Algorithms
 * Reservoir sampling, heavy hitters, moving average.
 *
 * Time Complexity: O(1) per element
 * Space Complexity: O(k) for reservoir, O(1) for others
 */
public class StreamAlgorithms {

    // Reservoir Sampling (select k random items from a stream of unknown length)
    public int[] reservoirSample(Iterator<Integer> stream, int k) {
        int[] reservoir = new int[k];
        int i = 0;
        Random rand = new Random();
        while (stream.hasNext()) {
            int val = stream.next();
            if (i < k) {
                reservoir[i] = val;
            } else {
                int j = rand.nextInt(i + 1);
                if (j < k) reservoir[j] = val;
            }
            i++;
        }
        return reservoir;
    }

    // Moving Average of last N elements
    public static class MovingAverage {
        private final Queue<Integer> window;
        private final int size;
        private int sum;

        public MovingAverage(int size) {
            this.size = size;
            this.window = new LinkedList<>();
            this.sum = 0;
        }

        public double next(int val) {
            window.offer(val);
            sum += val;
            if (window.size() > size) sum -= window.poll();
            return (double) sum / window.size();
        }
    }

    public static void main(String[] args) {
        // Test Moving Average
        MovingAverage ma = new MovingAverage(3);
        System.out.println("MA after 1: " + ma.next(1) + " (expected: 1.0)");
        System.out.println("MA after 10: " + ma.next(10) + " (expected: 5.5)");
        System.out.println("MA after 3: " + ma.next(3) + " (expected: ~4.67)");
        System.out.println("MA after 5: " + ma.next(5) + " (expected: 6.0)");

        // Test Reservoir Sampling
        StreamAlgorithms sa = new StreamAlgorithms();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) list.add(i);
        int[] sample = sa.reservoirSample(list.iterator(), 10);
        System.out.println("Reservoir sample (10 from 1000): " + Arrays.toString(sample));
    }
}
