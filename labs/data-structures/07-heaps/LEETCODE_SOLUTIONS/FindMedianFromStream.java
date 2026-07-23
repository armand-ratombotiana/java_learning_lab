package com.leetcode.heaps;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * LeetCode 295: Find Median from Data Stream
 * https://leetcode.com/problems/find-median-from-data-stream/
 *
 * Design a data structure that supports adding integers and finding the median.
 *
 * Time Complexity: O(log n) for addNum, O(1) for findMedian
 * Space Complexity: O(n)
 */
public class FindMedianFromStream {

    private final PriorityQueue<Integer> maxHeap; // left half (smaller numbers)
    private final PriorityQueue<Integer> minHeap; // right half (larger numbers)

    public FindMedianFromStream() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    /**
     * Approach: Two Heaps
     * Max-heap stores the smaller half, min-heap stores the larger half.
     * Balance so that maxHeap has either the same count or one more.
     */
    public void addNum(int num) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }

        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }

    public static void main(String[] args) {
        FindMedianFromStream fm = new FindMedianFromStream();
        fm.addNum(1);
        fm.addNum(2);
        System.out.println("Median after [1,2]: " + fm.findMedian() + " (expected: 1.5)");
        fm.addNum(3);
        System.out.println("Median after [1,2,3]: " + fm.findMedian() + " (expected: 2.0)");

        FindMedianFromStream fm2 = new FindMedianFromStream();
        fm2.addNum(-1);
        System.out.println("Median after [-1]: " + fm2.findMedian() + " (expected: -1.0)");
        fm2.addNum(-2);
        System.out.println("Median after [-1,-2]: " + fm2.findMedian() + " (expected: -1.5)");
    }
}
