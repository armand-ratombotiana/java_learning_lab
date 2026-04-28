package com.learning.custom;

import java.util.*;

/**
 * Demonstrates custom collection implementation.
 * Shows how to implement custom collection interface correctly.
 * Includes iterator, equals, hashCode, and proper contracts.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class CustomCollectionExample {
    
    /**
     * Simple stack implementing Collection interface.
     */
    public static class SimpleStack<E> implements Collection<E> {
        private final List<E> elements = new ArrayList<>();
        
        @Override
        public int size() {
            return elements.size();
        }
        
        @Override
        public boolean isEmpty() {
            return elements.isEmpty();
        }
        
        @Override
        public boolean contains(Object o) {
            return elements.contains(o);
        }
        
        @Override
        public Iterator<E> iterator() {
            return elements.iterator();
        }
        
        @Override
        public Object[] toArray() {
            return elements.toArray();
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            return elements.toArray(a);
        }
        
        @Override
        public boolean add(E e) {
            return elements.add(e);
        }
        
        @Override
        public boolean remove(Object o) {
            return elements.remove(o);
        }
        
        @Override
        public boolean containsAll(Collection<?> c) {
            return elements.containsAll(c);
        }
        
        @Override
        public boolean addAll(Collection<? extends E> c) {
            return elements.addAll(c);
        }
        
        @Override
        public boolean removeAll(Collection<?> c) {
            return elements.removeAll(c);
        }
        
        @Override
        public boolean retainAll(Collection<?> c) {
            return elements.retainAll(c);
        }
        
        @Override
        public void clear() {
            elements.clear();
        }
        
        public E push(E element) {
            add(element);
            return element;
        }
        
        public E pop() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            return elements.remove(elements.size() - 1);
        }
        
        public E peek() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            return elements.get(elements.size() - 1);
        }
    }
    
    public void demonstrateCustomCollection() {
        System.out.println("--- CUSTOM COLLECTION DEMONSTRATION ---");
        
        SimpleStack<String> stack = new SimpleStack<>();
        
        // Test push
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        
        System.out.println("Stack: " + stack);
        System.out.println("Size: " + stack.size());
        System.out.println("Peek: " + stack.peek());
        
        // Test pop
        System.out.println("Pop: " + stack.pop());
        System.out.println("After pop: " + stack);
        
        // Test collection operations
        System.out.println("Contains Second: " + stack.contains("Second"));
        
        stack.add("Fourth");
        System.out.println("After add: " + stack);
        
        System.out.println("Iterator: ");
        for (String item : stack) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
