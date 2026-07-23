package com.algorithms.online;

import java.util.*;

/**
 * Custom: Online Algorithms
 * Competitive analysis examples: ski rental, secretary problem, paging.
 *
 * Time Complexity: O(1) per decision
 * Space Complexity: O(k) for cache
 */
public class OnlineAlgorithms {

    // Ski Rental Problem: rent = 1, buy = B, don't know how many days
    // Optimal deterministic: rent for B-1 days, buy on day B (2 - 1/B competitive)
    public int skiRentalDecision(int day, int buyCost) {
        return (day % buyCost == 0) ? buyCost : 1; // 1 = rent, buyCost = buy
    }

    // Secretary Problem: hire the best candidate from sequential interviews
    public int secretaryProblem(int[] candidates) {
        int n = candidates.length;
        int stopIdx = n / (int) Math.E; // 37% rule
        int bestSoFar = Integer.MIN_VALUE;
        for (int i = 0; i < stopIdx; i++) bestSoFar = Math.max(bestSoFar, candidates[i]);

        for (int i = stopIdx; i < n; i++) {
            if (candidates[i] > bestSoFar) return i;
        }
        return n - 1;
    }

    // LRU Paging: O(k) per page access
    public int lruPageFaults(int[] pages, int cacheSize) {
        Set<Integer> cache = new HashSet<>();
        Map<Integer, Integer> lruMap = new HashMap<>();
        int faults = 0;

        for (int i = 0; i < pages.length; i++) {
            if (!cache.contains(pages[i])) {
                faults++;
                if (cache.size() >= cacheSize) {
                    int lru = Collections.min(lruMap.entrySet(), Map.Entry.comparingByValue()).getKey();
                    cache.remove(lru);
                    lruMap.remove(lru);
                }
                cache.add(pages[i]);
            }
            lruMap.put(pages[i], i);
        }
        return faults;
    }

    public static void main(String[] args) {
        OnlineAlgorithms oa = new OnlineAlgorithms();
        System.out.println("Ski rental day 4, buy=$10: " + oa.skiRentalDecision(4, 10));

        int[] candidates = {5, 3, 8, 4, 9, 2, 7, 6, 1, 10};
        System.out.println("Secretary best at index: " + oa.secretaryProblem(candidates));

        int[] pages = {1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};
        System.out.println("LRU page faults (cache=3): " + oa.lruPageFaults(pages, 3) + " (expected: ~7-10)");
    }
}
