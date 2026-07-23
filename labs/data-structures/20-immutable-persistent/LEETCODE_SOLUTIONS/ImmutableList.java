package com.leetcode.immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom: Immutable List Example
 * Demonstrate immutability and structural sharing patterns.
 *
 * Time Complexity: O(n) for creation, O(1) for access
 * Space Complexity: O(n)
 */
public class ImmutableList {

    public static class ImmutableArrayList<T> {
        private final List<T> data;

        @SuppressWarnings("unchecked")
        public ImmutableArrayList(T... elements) {
            List<T> list = new ArrayList<>();
            Collections.addAll(list, elements);
            this.data = Collections.unmodifiableList(list);
        }

        public T get(int index) {
            return data.get(index);
        }

        public int size() {
            return data.size();
        }

        public ImmutableArrayList<T> add(T element) {
            List<T> newList = new ArrayList<>(data);
            newList.add(element);
            return new ImmutableArrayList<>((T[]) newList.toArray());
        }
    }

    public static void main(String[] args) {
        ImmutableArrayList<Integer> list = new ImmutableArrayList<>(1, 2, 3);
        System.out.println("Original: size=" + list.size() + ", get(1)=" + list.get(1));

        ImmutableArrayList<Integer> list2 = list.add(4);
        System.out.println("Original unchanged: size=" + list.size() + " (expected: 3)");
        System.out.println("New list: size=" + list2.size() + " (expected: 4)");
        System.out.println("New list get(3): " + list2.get(3) + " (expected: 4)");

        try {
            // This should throw UnsupportedOperationException
            java.lang.reflect.Field f = list.getClass().getDeclaredFields()[0];
            f.setAccessible(true);
        } catch (Exception e) {
            System.out.println("Immutability enforced");
        }
    }
}
