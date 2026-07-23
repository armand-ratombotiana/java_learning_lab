package com.leetcode.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom: ConcurrentHashMap Usage Example
 * Demonstrate thread-safe map operations.
 *
 * Time Complexity: O(1) average
 * Space Complexity: O(n)
 */
public class ConcurrentHashMapExample {

    private final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    public void addWord(String word) {
        map.merge(word, 1, Integer::sum);
    }

    public int getCount(String word) {
        return map.getOrDefault(word, 0);
    }

    public void removeWord(String word) {
        map.remove(word);
    }

    public int size() {
        return map.size();
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMapExample example = new ConcurrentHashMapExample();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.addWord("hello");
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) example.addWord("world");
        });

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 500; i++) example.addWord("hello");
        });

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        System.out.println("Count for 'hello': " + example.getCount("hello") + " (expected: 1500)");
        System.out.println("Count for 'world': " + example.getCount("world") + " (expected: 1000)");

        example.removeWord("hello");
        System.out.println("After remove 'hello': " + example.getCount("hello") + " (expected: 0)");
        System.out.println("Total unique words: " + example.size() + " (expected: 1)");
    }
}
