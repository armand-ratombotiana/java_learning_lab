package com.learning.lab.module03;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 03: Collections ===");
        listDemo();
        setDemo();
        mapDemo();
        queueDemo();
        collectionMethodsDemo();
    }

    static void listDemo() {
        System.out.println("\n--- List ---");
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Cherry");
        arrayList.add(1, "Mango");
        System.out.println("ArrayList: " + arrayList);
        System.out.println("Get element: " + arrayList.get(2));
        System.out.println("Contains: " + arrayList.contains("Apple"));

        List<String> linkedList = new LinkedList<>();
        linkedList.add("First");
        linkedList.add("Second");
        System.out.println("LinkedList: " + linkedList);
    }

    static void setDemo() {
        System.out.println("\n--- Set ---");
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Red");
        hashSet.add("Green");
        hashSet.add("Blue");
        hashSet.add("Red");
        System.out.println("HashSet: " + hashSet);

        Set<String> treeSet = new TreeSet<>();
        treeSet.add("Zebra");
        treeSet.add("Apple");
        treeSet.add("Mango");
        System.out.println("TreeSet (sorted): " + treeSet);

        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("1");
        linkedHashSet.add("2");
        linkedHashSet.add("3");
        System.out.println("LinkedHashSet: " + linkedHashSet);
    }

    static void mapDemo() {
        System.out.println("\n--- Map ---");
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Apple", 10);
        hashMap.put("Banana", 20);
        hashMap.put("Cherry", 30);
        System.out.println("HashMap: " + hashMap);
        System.out.println("Get value: " + hashMap.get("Banana"));
        System.out.println("Contains key: " + hashMap.containsKey("Apple"));

        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Z", 1);
        treeMap.put("A", 2);
        treeMap.put("M", 3);
        System.out.println("TreeMap (sorted): " + treeMap);
    }

    static void queueDemo() {
        System.out.println("\n--- Queue ---");
        Queue<String> queue = new LinkedList<>();
        queue.add("One");
        queue.add("Two");
        queue.add("Three");
        System.out.println("Queue: " + queue);
        System.out.println("Poll: " + queue.poll());
        System.out.println("After poll: " + queue);

        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(5);
        priorityQueue.add(1);
        priorityQueue.add(3);
        System.out.println("PriorityQueue: " + priorityQueue);
    }

    static void collectionMethodsDemo() {
        System.out.println("\n--- Collection Methods ---");
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Collections.sort(list);
        System.out.println("Sorted: " + list);
        Collections.reverse(list);
        System.out.println("Reversed: " + list);
        System.out.println("Max: " + Collections.max(list));
        System.out.println("Frequency of 2: " + Collections.frequency(list, 2));
    }
}