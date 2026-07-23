package com.leetcode.skiplist;

import java.util.Random;

/**
 * LeetCode 1206: Design Skiplist
 * https://leetcode.com/problems/design-skiplist/
 *
 * Implement a skiplist with search, add, and erase operations.
 *
 * Time Complexity: O(log n) average per operation
 * Space Complexity: O(n log n)
 */
public class DesignSkiplist {

    private static class Node {
        int val;
        Node[] next;
        Node(int val, int level) {
            this.val = val;
            this.next = new Node[level + 1];
        }
    }

    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;
    private final Node head;
    private int level;
    private final Random random;

    public DesignSkiplist() {
        head = new Node(-1, MAX_LEVEL);
        level = 0;
        random = new Random();
    }

    private int randomLevel() {
        int lvl = 0;
        while (random.nextDouble() < P && lvl < MAX_LEVEL) lvl++;
        return lvl;
    }

    public boolean search(int target) {
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < target) {
                cur = cur.next[i];
            }
        }
        cur = cur.next[0];
        return cur != null && cur.val == target;
    }

    public void add(int num) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < num) {
                cur = cur.next[i];
            }
            update[i] = cur;
        }
        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level + 1; i <= newLevel; i++) update[i] = head;
            level = newLevel;
        }
        Node newNode = new Node(num, newLevel);
        for (int i = 0; i <= newLevel; i++) {
            newNode.next[i] = update[i].next[i];
            update[i].next[i] = newNode;
        }
    }

    public boolean erase(int num) {
        Node[] update = new Node[MAX_LEVEL + 1];
        Node cur = head;
        for (int i = level; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < num) {
                cur = cur.next[i];
            }
            update[i] = cur;
        }
        cur = cur.next[0];
        if (cur == null || cur.val != num) return false;

        for (int i = 0; i <= level; i++) {
            if (update[i].next[i] != cur) break;
            update[i].next[i] = cur.next[i];
        }
        while (level > 0 && head.next[level] == null) level--;
        return true;
    }

    public static void main(String[] args) {
        DesignSkiplist sl = new DesignSkiplist();
        sl.add(1);
        sl.add(2);
        sl.add(3);
        System.out.println("Search 0: " + sl.search(0) + " (expected: false)");
        System.out.println("Search 1: " + sl.search(1) + " (expected: true)");
        System.out.println("Erase 0: " + sl.erase(0) + " (expected: false)");
        System.out.println("Erase 1: " + sl.erase(1) + " (expected: true)");
        System.out.println("Search 1 after erase: " + sl.search(1) + " (expected: false)");

        // Duplicate add
        DesignSkiplist sl2 = new DesignSkiplist();
        sl2.add(1);
        sl2.add(1);
        System.out.println("Search 1 (duplicates): " + sl2.search(1) + " (expected: true)");
        sl2.erase(1);
        System.out.println("Search 1 after one erase: " + sl2.search(1) + " (expected: true)");
        sl2.erase(1);
        System.out.println("Search 1 after two erases: " + sl2.search(1) + " (expected: false)");
    }
}
