package com.learning.lab21;

import java.util.*;

/**
 * Demonstrates sequenced collections (new in Java 21) — SequencedCollection, SequencedSet, SequencedMap.
 */
public class SequencedCollectionsExample {

    public static void showSequencedCollections() {
        System.out.println("=== Sequenced Collections ===");

        SequencedCollection<String> list = new ArrayList<>();
        list.addFirst("First");
        list.addLast("Last");
        list.add("Middle");
        System.out.println("Sequenced list: " + list);
        System.out.println("First: " + list.getFirst() + ", Last: " + list.getLast());
        System.out.println("Reversed: " + list.reversed());

        SequencedSet<String> set = new LinkedHashSet<>();
        set.add("A");
        set.add("B");
        set.add("C");
        System.out.println("Sequenced set: " + set);
        System.out.println("Set reversed: " + set.reversed());

        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.putFirst("one", 1);
        map.putLast("three", 3);
        map.put("two", 2);
        System.out.println("Sequenced map: " + map);
        System.out.println("First entry: " + map.firstEntry() + ", Last entry: " + map.lastEntry());
    }
}
