package com.learning.lab12;

import java.util.*;

/**
 * Demonstrates ArrayList and LinkedList — their creation, manipulation, and iteration.
 */
public class ListExample {

    public static void showLists() {
        System.out.println("=== ArrayList & LinkedList ===");

        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Cherry");
        System.out.println("ArrayList: " + arrayList);
        System.out.println("get(1): " + arrayList.get(1));
        arrayList.remove(0);
        System.out.println("After remove(0): " + arrayList);

        List<Integer> linkedList = new LinkedList<>();
        linkedList.add(10);
        linkedList.add(20);
        linkedList.add(30);
        linkedList.addFirst(5);
        linkedList.addLast(35);
        System.out.println("LinkedList: " + linkedList);
        System.out.println("First: " + linkedList.getFirst() + ", Last: " + linkedList.getLast());

        System.out.print("Iterating with for-each: ");
        for (String s : arrayList) {
            System.out.print(s + " ");
        }
        System.out.println();

        System.out.print("Iterating with iterator: ");
        Iterator<Integer> it = linkedList.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();
    }
}
