package com.ds.advanced.lab05;

import java.util.*;

public class SkipList {
    private static final double P = 0.5;
    private static final int MAX_LEVEL = 16;
    private final Node head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
    private int level = 1;
    private final Random rand = new Random();

    private static class Node {
        int val;
        Node[] next;
        Node(int val, int level) { this.val = val; this.next = new Node[level]; }
    }

    private int randomLevel() {
        int lvl = 1;
        while (rand.nextDouble() < P && lvl < MAX_LEVEL) lvl++;
        return lvl;
    }

    public void insert(int val) {
        Node[] update = new Node[MAX_LEVEL];
        Node cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < val) cur = cur.next[i];
            update[i] = cur;
        }
        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) update[i] = head;
            level = newLevel;
        }
        Node node = new Node(val, newLevel);
        for (int i = 0; i < newLevel; i++) {
            node.next[i] = update[i].next[i];
            update[i].next[i] = node;
        }
    }

    public boolean search(int val) {
        Node cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < val) cur = cur.next[i];
        }
        cur = cur.next[0];
        return cur != null && cur.val == val;
    }

    public boolean delete(int val) {
        Node[] update = new Node[MAX_LEVEL];
        Node cur = head;
        for (int i = level - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < val) cur = cur.next[i];
            update[i] = cur;
        }
        cur = cur.next[0];
        if (cur == null || cur.val != val) return false;
        for (int i = 0; i < level; i++) {
            if (update[i].next[i] != cur) break;
            update[i].next[i] = cur.next[i];
        }
        while (level > 1 && head.next[level - 1] == null) level--;
        return true;
    }
}