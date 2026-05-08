package com.learning.datastructures;

import java.util.*;
import java.util.function.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Data Structures & Algorithms Lab ===\n");

        customLinkedList();
        customHashMap();
        binarySearchTree();
        sortingAlgorithms();
        graphBfsDfs();
    }

    static class Node { int data; Node next; Node(int d) { data = d; } }

    static void customLinkedList() {
        class LinkedList {
            Node head;
            void add(int v) {
                if (head == null) { head = new Node(v); return; }
                Node c = head; while (c.next != null) c = c.next; c.next = new Node(v);
            }
            void reverse() {
                Node p = null, c = head;
                while (c != null) { Node n = c.next; c.next = p; p = c; c = n; }
                head = p;
            }
            String print() {
                var sb = new StringBuilder("[");
                Node c = head;
                while (c != null) { sb.append(c.data); if (c.next != null) sb.append(" -> "); c = c.next; }
                return sb + "]";
            }
        }
        var list = new LinkedList();
        for (int i = 1; i <= 5; i++) list.add(i);
        System.out.println("  LinkedList: " + list.print());
        list.reverse();
        System.out.println("  Reversed:   " + list.print());
    }

    static void customHashMap() {
        class Entry { String k; int v; Entry n; Entry(String k, int v) { this.k = k; this.v = v; } }
        class HashMap {
            Entry[] b = new Entry[16];
            int hash(String k) { return Math.abs(k.hashCode()) % b.length; }
            void put(String k, int v) {
                int i = hash(k); Entry e = b[i];
                while (e != null) { if (e.k.equals(k)) { e.v = v; return; } e = e.n; }
                var ne = new Entry(k, v); ne.n = b[i]; b[i] = ne;
            }
            Optional<Integer> get(String k) {
                Entry e = b[hash(k)];
                while (e != null) { if (e.k.equals(k)) return Optional.of(e.v); e = e.n; }
                return Optional.empty();
            }
        }
        var m = new HashMap();
        m.put("apple", 5); m.put("banana", 3); m.put("cherry", 8);
        System.out.println("  HashMap: get('apple')=" + m.get("apple").orElse(-1)
            + ", get('grape')=" + m.get("grape").orElse(-1));
    }

    static void binarySearchTree() {
        class Node { int v; Node l, r; Node(int v) { this.v = v; } }
        class BST {
            Node root;
            void insert(int v) { root = ins(root, v); }
            Node ins(Node n, int v) {
                if (n == null) return new Node(v);
                if (v < n.v) n.l = ins(n.l, v); else if (v > n.v) n.r = ins(n.r, v);
                return n;
            }
            boolean contains(int v) {
                Node c = root;
                while (c != null) { if (v == c.v) return true; c = v < c.v ? c.l : c.r; }
                return false;
            }
            void inorder(Node n, List<Integer> r) {
                if (n == null) return; inorder(n.l, r); r.add(n.v); inorder(n.r, r);
            }
        }
        var t = new BST();
        for (int v : new int[]{8, 3, 10, 1, 6, 14, 4, 7, 13}) t.insert(v);
        var r = new ArrayList<Integer>(); t.inorder(t.root, r);
        System.out.println("  BST inorder: " + r + " | contains(6)=" + t.contains(6) + " contains(9)=" + t.contains(9));
    }

    static void sortingAlgorithms() {
        int[] data = {42, 17, 8, 99, 23, 56, 11, 34, 77, 5, 63, 28};
        var a1 = data.clone(); var a2 = data.clone();

        // Bubble sort
        for (int i = 0; i < a1.length - 1; i++) {
            boolean sw = false;
            for (int j = 0; j < a1.length - 1 - i; j++)
                if (a1[j] > a1[j + 1]) { int t = a1[j]; a1[j] = a1[j + 1]; a1[j + 1] = t; sw = true; }
            if (!sw) break;
        }

        // Quick sort
        java.util.Arrays.sort(a2);

        System.out.println("  Bubble: " + java.util.Arrays.toString(a1));
        System.out.println("  Quick:  " + java.util.Arrays.toString(a2));
        System.out.println("  Complexity: Bubble O(n^2), Quick O(n log n), Merge O(n log n)");
    }

    static void graphBfsDfs() {
        class Graph {
            Map<Integer, List<Integer>> adj = new HashMap<>();
            void addEdge(int u, int v) {
                adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
                adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
            }
            String search(int start, boolean useStack) {
                var visited = new HashSet<Integer>();
                var coll = new ArrayDeque<Integer>();
                coll.addLast(start);
                var sb = new StringBuilder();
                while (!coll.isEmpty()) {
                    int v = useStack ? coll.removeLast() : coll.removeFirst();
                    if (visited.add(v)) {
                        sb.append(v).append(" ");
                        adj.getOrDefault(v, List.of()).stream()
                            .filter(n -> !visited.contains(n)).forEach(n -> coll.addLast(n));
                    }
                }
                return sb.toString().trim();
            }
        }
        var g = new Graph();
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3); g.addEdge(1, 4); g.addEdge(2, 5); g.addEdge(2, 6);
        System.out.println("  DFS: " + g.search(0, true));
        System.out.println("  BFS: " + g.search(0, false));
    }
}
