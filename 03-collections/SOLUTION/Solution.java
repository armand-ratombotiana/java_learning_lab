package com.learning.lab.module03.solution;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Solution {
    public static void main(String[] args) {
        System.out.println("=== Module 03: Collections - Complete Solution ===\n");

        demonstrateListImplementations();
        demonstrateSetImplementations();
        demonstrateMapImplementations();
        demonstrateQueueImplementations();
        demonstrateCollectionOperations();
        demonstrateStreamFromCollections();
        demonstrateCustomCollections();
    }

    private static void demonstrateListImplementations() {
        System.out.println("=== List Implementations ===\n");

        demonstrateArrayList();
        demonstrateLinkedList();
        demonstrateVector();
        demonstrateStack();
    }

    private static void demonstrateArrayList() {
        System.out.println("--- ArrayList ---");

        List<String> arrayList = new ArrayList<>();

        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Cherry");
        arrayList.add(1, "Blueberry");

        System.out.println("Elements: " + arrayList);
        System.out.println("Size: " + arrayList.size());
        System.out.println("Contains 'Banana': " + arrayList.contains("Banana"));
        System.out.println("Get index 2: " + arrayList.get(2));

        arrayList.remove("Banana");
        arrayList.remove(0);

        System.out.println("After removal: " + arrayList);

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("Initial list: " + numbers);

        numbers.removeIf(n -> n % 2 == 0);
        System.out.println("After removeIf (evens): " + numbers);

        Collections.sort(arrayList);
        System.out.println("Sorted: " + arrayList);

        arrayList.clear();
        System.out.println("After clear: " + arrayList + "\n");
    }

    private static void demonstrateLinkedList() {
        System.out.println("--- LinkedList ---");

        LinkedList<String> linkedList = new LinkedList<>();

        linkedList.add("First");
        linkedList.add("Second");
        linkedList.addFirst("New First");
        linkedList.addLast("New Last");

        System.out.println("Elements: " + linkedList);
        System.out.println("First: " + linkedList.getFirst());
        System.out.println("Last: " + linkedList.getLast());

        linkedList.removeFirst();
        linkedList.removeLast();

        System.out.println("After removals: " + linkedList);

        linkedList.push("New Push");
        System.out.println("After push: " + linkedList);
        System.out.println("Pop: " + linkedList.pop() + "\n");
    }

    private static void demonstrateVector() {
        System.out.println("--- Vector (Synchronized) ---");

        Vector<Integer> vector = new Vector<>(10, 5);

        for (int i = 1; i <= 5; i++) {
            vector.add(i * 10);
        }

        System.out.println("Elements: " + vector);
        System.out.println("Capacity: " + vector.capacity());
        System.out.println("Size: " + vector.size());

        Enumeration<Integer> enumeration = vector.elements();
        System.out.print("Enumeration: ");
        while (enumeration.hasMoreElements()) {
            System.out.print(enumeration.nextElement() + " ");
        }
        System.out.println("\n");
    }

    private static void demonstrateStack() {
        System.out.println("--- Stack ---");

        Stack<String> stack = new Stack<>();

        stack.push("First");
        stack.push("Second");
        stack.push("Third");

        System.out.println("Stack: " + stack);
        System.out.println("Top: " + stack.peek());
        System.out.println("Pop: " + stack.pop());
        System.out.println("After pop: " + stack);
        System.out.println("Empty: " + stack.empty());

        stack.clear();
        for (int i = 1; i <= 5; i++) {
            stack.push(i);
        }

        System.out.println("Search for 3: " + stack.search(3));
        System.out.println("Search for 10: " + stack.search(10) + "\n");
    }

    private static void demonstrateSetImplementations() {
        System.out.println("=== Set Implementations ===\n");

        demonstrateHashSet();
        demonstrateLinkedHashSet();
        demonstrateTreeSet();
        demonstrateEnumSet();
    }

    private static void demonstrateHashSet() {
        System.out.println("--- HashSet (Hash Table) ---");

        Set<String> hashSet = new HashSet<>();

        hashSet.add("Apple");
        hashSet.add("Banana");
        hashSet.add("Cherry");
        hashSet.add("Apple");

        System.out.println("Elements (no duplicates): " + hashSet);
        System.out.println("Size: " + hashSet.size());
        System.out.println("Contains 'Banana': " + hashSet.contains("Banana"));

        hashSet.remove("Banana");
        System.out.println("After removal: " + hashSet);

        hashSet.clear();
        System.out.println("After clear: " + hashSet.isEmpty() + "\n");
    }

    private static void demonstrateLinkedHashSet() {
        System.out.println("--- LinkedHashSet (Ordered) ---");

        Set<String> linkedHashSet = new LinkedHashSet<>();

        linkedHashSet.add("First");
        linkedHashSet.add("Second");
        linkedHashSet.add("Third");

        System.out.println("Elements (insertion order): " + linkedHashSet);

        linkedHashSet.add("First");
        System.out.println("After duplicate add: " + linkedHashSet + "\n");
    }

    private static void demonstrateTreeSet() {
        System.out.println("--- TreeSet (Sorted) ---");

        TreeSet<Integer> treeSet = new TreeSet<>();

        treeSet.add(50);
        treeSet.add(20);
        treeSet.add(40);
        treeSet.add(10);
        treeSet.add(30);

        System.out.println("Elements (sorted): " + treeSet);
        System.out.println("First: " + treeSet.first());
        System.out.println("Last: " + treeSet.last());
        System.out.println("Lower than 30: " + treeSet.lower(30));
        System.out.println("Higher than 30: " + treeSet.higher(30));
        System.out.println("SubSet [20, 40): " + treeSet.subSet(20, 40));
        System.out.println("HeadSet (less than 40): " + treeSet.headSet(40));
        System.out.println("TailSet (greater than 20): " + treeSet.tailSet(20) + "\n");
    }

    private static void demonstrateEnumSet() {
        System.out.println("--- EnumSet ---");

        enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

        EnumSet<Day> workDays = EnumSet.of(Day.MONDAY, Day.TUESDAY, Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY);
        EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);

        System.out.println("Work days: " + workDays);
        System.out.println("Weekend: " + weekend);

        EnumSet<Day> allDays = EnumSet.allOf(Day.class);
        System.out.println("All days: " + allDays);

        EnumSet<Day> none = EnumSet.noneOf(Day.class);
        System.out.println("Empty set: " + none + "\n");
    }

    private static void demonstrateMapImplementations() {
        System.out.println("=== Map Implementations ===\n");

        demonstrateHashMap();
        demonstrateLinkedHashMap();
        demonstrateTreeMap();
        demonstrateHashtable();
        demonstrateConcurrentHashMap();
    }

    private static void demonstrateHashMap() {
        System.out.println("--- HashMap ---");

        Map<String, Integer> hashMap = new HashMap<>();

        hashMap.put("Apple", 10);
        hashMap.put("Banana", 20);
        hashMap.put("Cherry", 30);
        hashMap.put("Apple", 15);

        System.out.println("Elements: " + hashMap);
        System.out.println("Size: " + hashMap.size());
        System.out.println("Get 'Banana': " + hashMap.get("Banana"));
        System.out.println("Contains 'Cherry': " + hashMap.containsKey("Cherry"));
        System.out.println("Contains 20: " + hashMap.containsValue(20));

        hashMap.remove("Banana");
        System.out.println("After removal: " + hashMap);

        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        hashMap.clear();
        System.out.println("After clear: " + hashMap.isEmpty() + "\n");
    }

    private static void demonstrateLinkedHashMap() {
        System.out.println("--- LinkedHashMap (Ordered) ---");

        Map<String, String> linkedMap = new LinkedHashMap<>();

        linkedMap.put("first", "One");
        linkedMap.put("second", "Two");
        linkedMap.put("third", "Three");

        System.out.println("Elements (insertion order): " + linkedMap);

        linkedMap.get("first");
        System.out.println("After access: " + linkedMap + "\n");
    }

    private static void demonstrateTreeMap() {
        System.out.println("--- TreeMap (Sorted) ---");

        TreeMap<Integer, String> treeMap = new TreeMap<>();

        treeMap.put(3, "Three");
        treeMap.put(1, "One");
        treeMap.put(5, "Five");
        treeMap.put(2, "Two");
        treeMap.put(4, "Four");

        System.out.println("Elements (sorted): " + treeMap);
        System.out.println("First key: " + treeMap.firstKey());
        System.out.println("Last key: " + treeMap.lastKey());
        System.out.println("Lower than 3: " + treeMap.lowerKey(3));
        System.out.println("Higher than 3: " + treeMap.higherKey(3));
        System.out.println("SubMap [2, 4]: " + treeMap.subMap(2, 5));
        System.out.println("HeadMap (< 4): " + treeMap.headMap(4));
        System.out.println("TailMap (>= 3): " + treeMap.tailMap(3) + "\n");
    }

    private static void demonstrateHashtable() {
        System.out.println("--- Hashtable (Synchronized) ---");

        Hashtable<String, Integer> hashtable = new Hashtable<>();

        hashtable.put("One", 1);
        hashtable.put("Two", 2);
        hashtable.put("Three", 3);

        System.out.println("Elements: " + hashtable);
        System.out.println("Get 'Two': " + hashtable.get("Two"));

        try {
            hashtable.put(null, 0);
        } catch (NullPointerException e) {
            System.out.println("Cannot put null key (expected)");
        }

        System.out.println();
    }

    private static void demonstrateConcurrentHashMap() {
        System.out.println("--- ConcurrentHashMap (Thread-Safe) ---");

        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();

        concurrentMap.put("A", 1);
        concurrentMap.put("B", 2);
        concurrentMap.putIfAbsent("C", 3);

        System.out.println("Elements: " + concurrentMap);

        concurrentMap.computeIfAbsent("D", k -> 4);
        System.out.println("After computeIfAbsent: " + concurrentMap);

        concurrentMap.forEach((k, v) -> System.out.println(k + ":" + v));

        Integer removed = concurrentMap.remove("B");
        System.out.println("Removed 'B': " + removed + "\n");
    }

    private static void demonstrateQueueImplementations() {
        System.out.println("=== Queue Implementations ===\n");

        demonstratePriorityQueue();
        demonstrateArrayDeque();
        demonstrateBlockingQueue();
    }

    private static void demonstratePriorityQueue() {
        System.out.println("--- PriorityQueue (Heap) ---");

        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

        priorityQueue.add(30);
        priorityQueue.add(10);
        priorityQueue.add(50);
        priorityQueue.add(20);

        System.out.println("Elements: " + priorityQueue);
        System.out.println("Peek: " + priorityQueue.peek());
        System.out.println("Poll: " + priorityQueue.poll());
        System.out.println("After poll: " + priorityQueue);

        PriorityQueue<Integer> reversed = new PriorityQueue<>(Comparator.reverseOrder());
        reversed.addAll(List.of(30, 10, 50, 20));
        System.out.println("Reversed order: " + reversed + "\n");
    }

    private static void demonstrateArrayDeque() {
        System.out.println("--- ArrayDeque (Double-ended Queue) ---");

        ArrayDeque<String> deque = new ArrayDeque<>();

        deque.addFirst("First");
        deque.addLast("Last");
        deque.push("New First");

        System.out.println("Elements: " + deque);
        System.out.println("First: " + deque.getFirst());
        System.out.println("Last: " + deque.getLast());
        System.out.println("Poll first: " + deque.pollFirst());
        System.out.println("After operations: " + deque + "\n");
    }

    private static void demonstrateBlockingQueue() {
        System.out.println("--- BlockingQueue (Thread-safe) ---");

        BlockingQueue<String> blockingQueue = new java.util.concurrent.ArrayBlockingQueue<>(3);

        try {
            blockingQueue.put("One");
            blockingQueue.put("Two");
            blockingQueue.put("Three");
            System.out.println("Queue full: " + blockingQueue.offer("Four"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Elements: " + blockingQueue);

        try {
            System.out.println("Take: " + blockingQueue.take());
            System.out.println("After take: " + blockingQueue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
    }

    private static void demonstrateCollectionOperations() {
        System.out.println("=== Collection Operations ===\n");

        List<Integer> list = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3));

        System.out.println("Original: " + list);
        Collections.sort(list);
        System.out.println("Sorted: " + list);

        Collections.reverse(list);
        System.out.println("Reversed: " + list);

        Collections.shuffle(list);
        System.out.println("Shuffled: " + list);

        List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
        Set<Integer> syncSet = Collections.synchronizedSet(new HashSet<>());
        Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
        System.out.println("Synchronized wrappers created\n");

        List<Integer> unmodifiable = Collections.unmodifiableList(list);
        try {
            unmodifiable.add(100);
        } catch (UnsupportedOperationException e) {
            System.out.println("Unmodifiable list prevents modifications (expected)\n");
        }
    }

    private static void demonstrateStreamFromCollections() {
        System.out.println("=== Stream from Collections ===\n");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("Filter evens: " + numbers.stream().filter(n -> n % 2 == 0).toList());
        System.out.println("Map doubled: " + numbers.stream().map(n -> n * 2).toList());
        System.out.println("Reduce sum: " + numbers.stream().reduce(0, Integer::sum));

        Set<String> fruits = Set.of("apple", "banana", "cherry", "date");
        System.out.println("Uppercase: " + fruits.stream().map(String::toUpperCase).toList());

        Map<String, Integer> map = Map.of("a", 1, "b", 2, "c", 3);
        System.out.println("Map entries: " + map.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).toList());
    }

    private static void demonstrateCustomCollections() {
        System.out.println("\n=== Custom Collections ===\n");

        demonstrateCustomList();
        demonstrateCustomMap();
    }

    private static void demonstrateCustomList() {
        System.out.println("--- Custom List Implementation ---");

        CustomList<String> customList = new ArrayListCustom<>();
        customList.add("First");
        customList.add("Second");
        customList.add(1, "Inserted");

        System.out.println("Elements: " + customList);
        System.out.println("Size: " + customList.size());
        System.out.println("Get(1): " + customList.get(1));
        System.out.println("Contains 'First': " + customList.contains("First"));

        customList.remove("Second");
        System.out.println("After removal: " + customList + "\n");
    }

    private static void demonstrateCustomMap() {
        System.out.println("--- Custom Map Implementation ---");

        CustomMap<String, Integer> customMap = new HashMapCustom<>();
        customMap.put("One", 1);
        customMap.put("Two", 2);
        customMap.put("Three", 3);

        System.out.println("Elements: " + customMap);
        System.out.println("Get 'Two': " + customMap.get("Two"));
        System.out.println("Contains 'Three': " + customMap.containsKey("Three"));
        System.out.println("Contains 1: " + customMap.containsValue(1));

        customMap.remove("Two");
        System.out.println("After removal: " + customMap + "\n");
    }
}

interface CustomList<E> {
    void add(E element);
    void add(int index, E element);
    E get(int index);
    E remove(int index);
    boolean remove(E element);
    int size();
    boolean contains(E element);
    void clear();
}

class ArrayListCustom<E> implements CustomList<E> {
    private Object[] elements = new Object[10];
    private int size = 0;

    public void add(E element) {
        ensureCapacity();
        elements[size++] = element;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        ensureCapacity();
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (E) elements[index];
    }

    @SuppressWarnings("unchecked")
    public E remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        E removed = (E) elements[index];
        System.arraycopy(elements, index + 1, elements, index, --size - index);
        return removed;
    }

    public boolean remove(E element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public int size() { return size; }

    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) return true;
        }
        return false;
    }

    public void clear() {
        elements = new Object[10];
        size = 0;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            Object[] newArray = new Object[elements.length * 2];
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}

interface CustomMap<K, V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
    boolean containsKey(K key);
    boolean containsValue(V value);
    int size();
    void clear();
}

class HashMapCustom<K, V> implements CustomMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private Node<K, V>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMapCustom() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
    }

    public V put(K key, V value) {
        int index = hash(key);
        Node<K, V> node = table[index];

        while (node != null) {
            if (Objects.equals(node.key, key)) {
                V old = node.value;
                node.value = value;
                return old;
            }
            node = node.next;
        }

        Node<K, V> newNode = new Node<>(key, value, table[index]);
        table[index] = newNode;
        size++;

        if (size > table.length * LOAD_FACTOR) {
            resize();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        int index = hash(key);
        Node<K, V> node = table[index];

        while (node != null) {
            if (Objects.equals(node.key, key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        Node<K, V> node = table[index];
        Node<K, V> prev = null;

        while (node != null) {
            if (Objects.equals(node.key, key)) {
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return node.value;
            }
            prev = node;
            node = node.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = hash(key);
        Node<K, V> node = table[index];

        while (node != null) {
            if (Objects.equals(node.key, key)) return true;
            node = node.next;
        }
        return false;
    }

    public boolean containsValue(V value) {
        for (Node<K, V>[] tab : table) {
            Node<K, V> node = tab;
            while (node != null) {
                if (Objects.equals(node.value, value)) return true;
                node = node.next;
            }
        }
        return false;
    }

    public int size() { return size; }

    public void clear() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        size = 0;
    }

    private int hash(K key) {
        return key == null ? 0 : Math.abs(key.hashCode() % table.length);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[oldTable.length * 2];
        size = 0;

        for (Node<K, V> node : oldTable) {
            while (node != null) {
                put(node.key, node.value);
                node = node.next;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Node<K, V> node : table) {
            while (node != null) {
                if (!first) sb.append(", ");
                sb.append(node.key).append("=").append(node.value);
                first = false;
                node = node.next;
            }
        }
        return sb.append("}").toString();
    }

    static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}