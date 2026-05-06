# Exercises: Collections Framework

<div align="center">

![Module](https://img.shields.io/badge/Module-03-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-30-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**30 comprehensive exercises for Collections Framework module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-10)](#easy-exercises-1-10)
2. [Medium Exercises (11-20)](#medium-exercises-11-20)
3. [Hard Exercises (21-25)](#hard-exercises-21-25)
4. [Interview Exercises (26-30)](#interview-exercises-26-30)
5. [Solutions Summary](#solutions-summary)

---

## 🟢 Easy Exercises (1-10)

### Exercise 1: ArrayList Basics
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** ArrayList, add, remove, iterate

**Pedagogic Objective:**
Understand ArrayList as a dynamic array that grows automatically.

**Problem:**
Create an ArrayList of strings, add elements, remove one, and print all.

**Complete Solution:**

import java.util.ArrayList;
import java.util.List;

public class ArrayListBasics {
    public static void main(String[] args) {
        // Create ArrayList
        List<String> fruits = new ArrayList<>();
        
        // Add elements
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");
        fruits.add("Mango");
        
        System.out.println("Original list: " + fruits);
        System.out.println("Size: " + fruits.size());
        
        // Remove element
        fruits.remove("Banana");
        System.out.println("After removal: " + fruits);
        
        // Iterate
        System.out.println("Iterating:");
        for (String fruit : fruits) {
            System.out.println("  - " + fruit);
        }
    }
}


**Key Concepts:**
- ArrayList is a resizable array
- O(1) access by index
- O(n) insertion/deletion in middle
- Thread-unsafe (use Collections.synchronizedList for thread safety)

---

### Exercise 2: LinkedList vs ArrayList
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** LinkedList, ArrayList, performance comparison

**Pedagogic Objective:**
Understand when to use LinkedList vs ArrayList.

**Complete Solution:**

import java.util.*;

public class ListComparison {
    public static void main(String[] args) {
        // ArrayList - fast access, slow insertion
        List<Integer> arrayList = new ArrayList<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            arrayList.add(i);
        }
        long arrayListTime = System.nanoTime() - startTime;
        
        // LinkedList - slow access, fast insertion
        List<Integer> linkedList = new LinkedList<>();
        startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            linkedList.add(i);
        }
        long linkedListTime = System.nanoTime() - startTime;
        
        System.out.println("ArrayList add time: " + arrayListTime + " ns");
        System.out.println("LinkedList add time: " + linkedListTime + " ns");
        
        // Access performance
        startTime = System.nanoTime();
        int value = arrayList.get(50000);
        long arrayListAccessTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        value = linkedList.get(50000);
        long linkedListAccessTime = System.nanoTime() - startTime;
        
        System.out.println("ArrayList access time: " + arrayListAccessTime + " ns");
        System.out.println("LinkedList access time: " + linkedListAccessTime + " ns");
    }
}


**When to Use:**
- **ArrayList:** Frequent access, few insertions/deletions
- **LinkedList:** Frequent insertions/deletions, few accesses

---

### Exercise 3: HashSet Basics
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** HashSet, uniqueness, no duplicates

**Pedagogic Objective:**
Understand HashSet for storing unique elements.

**Complete Solution:**

import java.util.*;

public class HashSetBasics {
    public static void main(String[] args) {
        Set<String> colors = new HashSet<>();
        
        // Add elements
        colors.add("Red");
        colors.add("Green");
        colors.add("Blue");
        colors.add("Red"); // Duplicate - ignored
        
        System.out.println("Colors: " + colors);
        System.out.println("Size: " + colors.size()); // 3, not 4
        
        // Check membership
        System.out.println("Contains Red: " + colors.contains("Red"));
        System.out.println("Contains Yellow: " + colors.contains("Yellow"));
        
        // Remove
        colors.remove("Green");
        System.out.println("After removal: " + colors);
    }
}


**Key Concepts:**
- No duplicates allowed
- No guaranteed order
- O(1) average add/remove/contains
- Uses hash function for storage

---

### Exercise 4: TreeSet Ordering
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** TreeSet, sorting, natural ordering

**Pedagogic Objective:**
Understand TreeSet for sorted unique elements.

**Complete Solution:**

import java.util.*;

public class TreeSetOrdering {
    public static void main(String[] args) {
        Set<Integer> numbers = new TreeSet<>();
        
        numbers.add(5);
        numbers.add(2);
        numbers.add(8);
        numbers.add(1);
        numbers.add(9);
        
        System.out.println("TreeSet (sorted): " + numbers);
        
        // Sorted operations
        System.out.println("First: " + ((TreeSet<Integer>) numbers).first());
        System.out.println("Last: " + ((TreeSet<Integer>) numbers).last());
        System.out.println("Head set (< 5): " + ((TreeSet<Integer>) numbers).headSet(5));
        System.out.println("Tail set (>= 5): " + ((TreeSet<Integer>) numbers).tailSet(5));
    }
}


**Key Concepts:**
- Maintains sorted order
- O(log n) add/remove/contains
- Uses Red-Black tree internally
- Implements NavigableSet interface

---

### Exercise 5: HashMap Basics
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** HashMap, key-value pairs, lookup

**Pedagogic Objective:**
Understand HashMap for key-value storage.

**Complete Solution:**

import java.util.*;

public class HashMapBasics {
    public static void main(String[] args) {
        Map<String, Integer> ages = new HashMap<>();
        
        // Put elements
        ages.put("Alice", 25);
        ages.put("Bob", 30);
        ages.put("Charlie", 35);
        
        System.out.println("Map: " + ages);
        
        // Get value
        System.out.println("Alice's age: " + ages.get("Alice"));
        System.out.println("David's age: " + ages.get("David")); // null
        
        // Check key/value
        System.out.println("Contains Alice: " + ages.containsKey("Alice"));
        System.out.println("Contains 25: " + ages.containsValue(25));
        
        // Iterate
        System.out.println("All entries:");
        for (Map.Entry<String, Integer> entry : ages.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }
    }
}


**Key Concepts:**
- Key-value pairs
- Keys must be unique
- O(1) average get/put/remove
- Null key allowed (only one)

---

### Exercise 6: TreeMap Ordering
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** TreeMap, sorted maps, navigation

**Pedagogic Objective:**
Understand TreeMap for sorted key-value storage.

**Complete Solution:**

import java.util.*;

public class TreeMapOrdering {
    public static void main(String[] args) {
        Map<String, Integer> scores = new TreeMap<>();
        
        scores.put("Charlie", 85);
        scores.put("Alice", 90);
        scores.put("Bob", 88);
        
        System.out.println("TreeMap (sorted by key): " + scores);
        
        // Sorted operations
        System.out.println("First key: " + ((TreeMap<String, Integer>) scores).firstKey());
        System.out.println("Last key: " + ((TreeMap<String, Integer>) scores).lastKey());
        System.out.println("Head map (< Bob): " + ((TreeMap<String, Integer>) scores).headMap("Bob"));
    }
}


---

### Exercise 7: Queue and Deque
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Queue, Deque, FIFO, LIFO

**Pedagogic Objective:**
Understand Queue (FIFO) and Deque (double-ended).

**Complete Solution:**

import java.util.*;

public class QueueDequeBasics {
    public static void main(String[] args) {
        // Queue - FIFO
        Queue<String> queue = new LinkedList<>();
        queue.add("First");
        queue.add("Second");
        queue.add("Third");
        
        System.out.println("Queue: " + queue);
        System.out.println("Poll (remove first): " + queue.poll());
        System.out.println("Peek (view first): " + queue.peek());
        
        // Deque - Double-ended
        Deque<String> deque = new LinkedList<>();
        deque.addFirst("Front");
        deque.addLast("Back");
        deque.addFirst("New Front");
        
        System.out.println("\nDeque: " + deque);
        System.out.println("Remove first: " + deque.removeFirst());
        System.out.println("Remove last: " + deque.removeLast());
    }
}


**Key Concepts:**
- Queue: FIFO (First In First Out)
- Deque: Can add/remove from both ends
- PriorityQueue: Elements ordered by priority
- BlockingQueue: Thread-safe queue

---

### Exercise 8: Collections Utility Methods
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Collections class, sorting, searching

**Pedagogic Objective:**
Understand Collections utility methods.

**Complete Solution:**

import java.util.*;

public class CollectionsUtility {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));
        
        // Sort
        Collections.sort(numbers);
        System.out.println("Sorted: " + numbers);
        
        // Reverse
        Collections.reverse(numbers);
        System.out.println("Reversed: " + numbers);
        
        // Shuffle
        Collections.shuffle(numbers);
        System.out.println("Shuffled: " + numbers);
        
        // Binary search
        Collections.sort(numbers);
        int index = Collections.binarySearch(numbers, 5);
        System.out.println("Index of 5: " + index);
        
        // Min/Max
        System.out.println("Min: " + Collections.min(numbers));
        System.out.println("Max: " + Collections.max(numbers));
        
        // Frequency
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "apple");
        System.out.println("Frequency of 'apple': " + Collections.frequency(words, "apple"));
    }
}


---

### Exercise 9: Comparable and Comparator
**Difficulty:** Easy  
**Time:** 25 minutes  
**Topics:** Comparable, Comparator, custom sorting

**Pedagogic Objective:**
Understand how to sort custom objects.

**Complete Solution:**

import java.util.*;

// Comparable - natural ordering
public class Student implements Comparable<Student> {
    private String name;
    private int score;
    
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.score, other.score);
    }
    
    @Override
    public String toString() {
        return name + " (" + score + ")";
    }
}

public class ComparableComparator {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 85));
        students.add(new Student("Bob", 90));
        students.add(new Student("Charlie", 78));
        
        // Sort using Comparable
        Collections.sort(students);
        System.out.println("Sorted by score: " + students);
        
        // Sort using Comparator
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.toString().compareTo(s2.toString());
            }
        });
        System.out.println("Sorted by name: " + students);
        
        // Lambda comparator (Java 8+)
        Collections.sort(students, (s1, s2) -> s1.toString().compareTo(s2.toString()));
        System.out.println("Sorted by name (lambda): " + students);
    }
}


**Key Differences:**
- **Comparable:** Natural ordering, implement in class
- **Comparator:** Custom ordering, separate class/lambda

---

### Exercise 10: Immutable Collections
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Immutable collections, unmodifiable views

**Pedagogic Objective:**
Understand immutable and unmodifiable collections.

**Complete Solution:**

import java.util.*;

public class ImmutableCollections {
    public static void main(String[] args) {
        // Unmodifiable view
        List<String> original = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> unmodifiable = Collections.unmodifiableList(original);
        
        System.out.println("Unmodifiable list: " + unmodifiable);
        
        try {
            unmodifiable.add("D"); // Throws UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify unmodifiable list");
        }
        
        // Immutable collections (Java 9+)
        List<String> immutable = List.of("X", "Y", "Z");
        System.out.println("Immutable list: " + immutable);
        
        Set<String> immutableSet = Set.of("Red", "Green", "Blue");
        System.out.println("Immutable set: " + immutableSet);
        
        Map<String, Integer> immutableMap = Map.of("A", 1, "B", 2);
        System.out.println("Immutable map: " + immutableMap);
    }
}


**Key Concepts:**
- Unmodifiable: Wrapper that prevents modification
- Immutable: Cannot be changed after creation
- List.of(), Set.of(), Map.of() create immutable collections
- Null elements not allowed in immutable collections

---

## 🟡 Medium Exercises (11-20)

### Exercise 11: Custom Collection Implementation
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Implementing Collection interface

**Complete Solution:**

import java.util.*;

public class SimpleList<T> implements Iterable<T> {
    private Object[] elements;
    private int size;
    
    public SimpleList() {
        this.elements = new Object[10];
        this.size = 0;
    }
    
    public void add(T element) {
        if (size == elements.length) {
            resize();
        }
        elements[size++] = element;
    }
    
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (T) elements[index];
    }
    
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        size--;
    }
    
    public int size() {
        return size;
    }
    
    private void resize() {
        Object[] newElements = new Object[elements.length * 2];
        System.arraycopy(elements, 0, newElements, 0, size);
        elements = newElements;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < size;
            }
            
            @Override
            public T next() {
                return get(index++);
            }
        };
    }
    
    public static void main(String[] args) {
        SimpleList<String> list = new SimpleList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        
        System.out.println("Size: " + list.size());
        for (String element : list) {
            System.out.println(element);
        }
    }
}


---

### Exercise 12: Frequency Counter
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** HashMap, frequency counting

**Complete Solution:**

import java.util.*;

public class FrequencyCounter {
    public static Map<String, Integer> countFrequency(String[] words) {
        Map<String, Integer> frequency = new HashMap<>();
        
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }
        
        return frequency;
    }
    
    public static void main(String[] args) {
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};
        Map<String, Integer> frequency = countFrequency(words);
        
        System.out.println("Frequency: " + frequency);
        
        // Sort by frequency
        frequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}


---

### Exercise 13: Group By Operation
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** HashMap, grouping, streams

**Complete Solution:**

import java.util.*;
import java.util.stream.Collectors;

public class StudentGroupBy {
    private String name;
    private String grade;
    private int score;
    
    public StudentGroupBy(String name, String grade, int score) {
        this.name = name;
        this.grade = grade;
        this.score = score;
    }
    
    public String getGrade() { return grade; }
    public int getScore() { return score; }
    
    @Override
    public String toString() {
        return name + " (" + score + ")";
    }
}

public class GroupByExample {
    public static void main(String[] args) {
        List<StudentGroupBy> students = Arrays.asList(
            new StudentGroupBy("Alice", "A", 90),
            new StudentGroupBy("Bob", "B", 80),
            new StudentGroupBy("Charlie", "A", 92),
            new StudentGroupBy("David", "B", 85)
        );
        
        // Group by grade
        Map<String, List<StudentGroupBy>> byGrade = students.stream()
                .collect(Collectors.groupingBy(StudentGroupBy::getGrade));
        
        System.out.println("Grouped by grade:");
        byGrade.forEach((grade, studentList) -> {
            System.out.println(grade + ": " + studentList);
        });
    }
}


---

### Exercise 14: Intersection and Union
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Set operations

**Complete Solution:**

import java.util.*;

public class SetOperations {
    public static void main(String[] args) {
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        
        // Union
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        System.out.println("Union: " + union);
        
        // Intersection
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("Intersection: " + intersection);
        
        // Difference
        Set<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("Difference (set1 - set2): " + difference);
    }
}


---

### Exercise 15: LRU Cache Implementation
**Difficulty:** Medium  
**Time:** 35 minutes  
**Topics:** LinkedHashMap, cache design

**Complete Solution:**

import java.util.*;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // Access-order LinkedHashMap
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
    
    public static void main(String[] args) {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println("Cache: " + cache);
        
        cache.get("A"); // Access A
        cache.put("D", 4); // B is removed (least recently used)
        System.out.println("After adding D: " + cache);
    }
}


---

### Exercise 16: Duplicate Removal
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Set, removing duplicates

**Complete Solution:**

import java.util.*;

public class DuplicateRemoval {
    public static <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }
    
    public static void main(String[] args) {
        List<String> list = Arrays.asList("A", "B", "A", "C", "B", "D");
        System.out.println("Original: " + list);
        System.out.println("Without duplicates: " + removeDuplicates(list));
    }
}


---

### Exercise 17: Sorting Custom Objects
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Comparator, sorting

**Complete Solution:**

import java.util.*;

public class PersonSort {
    private String name;
    private int age;
    
    public PersonSort(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    
    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}

public class SortingCustomObjects {
    public static void main(String[] args) {
        List<PersonSort> people = Arrays.asList(
            new PersonSort("Alice", 30),
            new PersonSort("Bob", 25),
            new PersonSort("Charlie", 35)
        );
        
        // Sort by age
        people.sort(Comparator.comparingInt(PersonSort::getAge));
        System.out.println("Sorted by age: " + people);
        
        // Sort by name
        people.sort(Comparator.comparing(PersonSort::getName));
        System.out.println("Sorted by name: " + people);
        
        // Sort by age descending
        people.sort(Comparator.comparingInt(PersonSort::getAge).reversed());
        System.out.println("Sorted by age (desc): " + people);
    }
}


---

### Exercise 18: Map Iteration Patterns
**Difficulty:** Medium  
**Time:** 20 minutes  
**Topics:** Map iteration, entry set

**Complete Solution:**

import java.util.*;

public class MapIteration {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        
        // Iterate over entries (most efficient)
        System.out.println("Entry set:");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        
        // Iterate over keys
        System.out.println("\nKey set:");
        for (String key : map.keySet()) {
            System.out.println(key + " -> " + map.get(key));
        }
        
        // Iterate over values
        System.out.println("\nValues:");
        for (Integer value : map.values()) {
            System.out.println(value);
        }
        
        // Using forEach
        System.out.println("\nUsing forEach:");
        map.forEach((key, value) -> System.out.println(key + " -> " + value));
    }
}


---

### Exercise 19: Priority Queue
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** PriorityQueue, heap, ordering

**Complete Solution:**

import java.util.*;

public class PriorityQueueExample {
    public static void main(String[] args) {
        // Min-heap (default)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.add(5);
        minHeap.add(2);
        minHeap.add(8);
        minHeap.add(1);
        
        System.out.println("Min-heap extraction:");
        while (!minHeap.isEmpty()) {
            System.out.println(minHeap.poll());
        }
        
        // Max-heap
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.add(5);
        maxHeap.add(2);
        maxHeap.add(8);
        maxHeap.add(1);
        
        System.out.println("\nMax-heap extraction:");
        while (!maxHeap.isEmpty()) {
            System.out.println(maxHeap.poll());
        }
    }
}


---

### Exercise 20: Concurrent Collections
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Thread-safe collections, ConcurrentHashMap

**Complete Solution:**

import java.util.*;
import java.util.concurrent.*;

public class ConcurrentCollectionsExample {
    public static void main(String[] args) throws InterruptedException {
        // ConcurrentHashMap - thread-safe without full synchronization
        Map<String, Integer> map = new ConcurrentHashMap<>();
        
        // Multiple threads adding to map
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.put("Key" + i, i);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 1000; i < 2000; i++) {
                map.put("Key" + i, i);
            }
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("Final map size: " + map.size());
        
        // CopyOnWriteArrayList - thread-safe for reads
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        
        System.out.println("CopyOnWriteArrayList: " + list);
    }
}


---

## 🔴 Hard Exercises (21-25)

### Exercise 21: Custom HashMap Implementation
**Difficulty:** Hard  
**Time:** 45 minutes  
**Topics:** Hash table, collision handling, load factor

**Complete Solution:**

public class SimpleHashMap<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Entry<K, V>[] table;
    private int size;
    
    @SuppressWarnings("unchecked")
    public SimpleHashMap() {
        table = new Entry[INITIAL_CAPACITY];
        size = 0;
    }
    
    public void put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }
        
        int index = hash(key);
        Entry<K, V> entry = table[index];
        
        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }
        
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = table[index];
        table[index] = newEntry;
        size++;
    }
    
    public V get(K key) {
        int index = hash(key);
        Entry<K, V> entry = table[index];
        
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        
        return null;
    }
    
    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;
        
        for (Entry<K, V> entry : oldTable) {
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }
    
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public static void main(String[] args) {
        SimpleHashMap<String, Integer> map = new SimpleHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        
        System.out.println("A: " + map.get("A"));
        System.out.println("B: " + map.get("B"));
        System.out.println("D: " + map.get("D"));
    }
}


---

### Exercise 22: Bloom Filter
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Bit manipulation, hashing, probabilistic data structure

**Complete Solution:**

import java.util.BitSet;

public class BloomFilter {
    private BitSet bitSet;
    private int size;
    private int hashFunctions;
    
    public BloomFilter(int size, int hashFunctions) {
        this.bitSet = new BitSet(size);
        this.size = size;
        this.hashFunctions = hashFunctions;
    }
    
    public void add(String element) {
        for (int i = 0; i < hashFunctions; i++) {
            int hash = hash(element, i);
            bitSet.set(hash);
        }
    }
    
    public boolean contains(String element) {
        for (int i = 0; i < hashFunctions; i++) {
            int hash = hash(element, i);
            if (!bitSet.get(hash)) {
                return false;
            }
        }
        return true;
    }
    
    private int hash(String element, int seed) {
        return Math.abs((element.hashCode() + seed) % size);
    }
    
    public static void main(String[] args) {
        BloomFilter filter = new BloomFilter(100, 3);
        
        filter.add("apple");
        filter.add("banana");
        filter.add("cherry");
        
        System.out.println("Contains apple: " + filter.contains("apple"));
        System.out.println("Contains banana: " + filter.contains("banana"));
        System.out.println("Contains orange: " + filter.contains("orange"));
    }
}


---

### Exercise 23: Trie Data Structure
**Difficulty:** Hard  
**Time:** 45 minutes  
**Topics:** Tree structure, prefix search

**Complete Solution:**

import java.util.*;

public class Trie {
    private TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEndOfWord = true;
    }
    
    public boolean search(String word) {
        TrieNode node = find(word);
        return node != null && node.isEndOfWord;
    }
    
    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }
    
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = find(prefix);
        
        if (node != null) {
            dfs(node, prefix, results);
        }
        
        return results;
    }
    
    private TrieNode find(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;
        }
        return node;
    }
    
    private void dfs(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(prefix);
        }
        
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            dfs(entry.getValue(), prefix + entry.getKey(), results);
        }
    }
    
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }
    
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("apply");
        
        System.out.println("Search 'app': " + trie.search("app"));
        System.out.println("Search 'apple': " + trie.search("apple"));
        System.out.println("Autocomplete 'app': " + trie.autocomplete("app"));
    }
}


---

### Exercise 24: Graph Representation
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Graph, adjacency list, traversal

**Complete Solution:**

import java.util.*;

public class Graph<T> {
    private Map<T, List<T>> adjacencyList;
    
    public Graph() {
        adjacencyList = new HashMap<>();
    }
    
    public void addVertex(T vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }
    
    public void addEdge(T source, T destination) {
        addVertex(source);
        addVertex(destination);
        adjacencyList.get(source).add(destination);
    }
    
    public List<T> bfs(T start) {
        List<T> result = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);
            
            for (T neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return result;
    }
    
    public List<T> dfs(T start) {
        List<T> result = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        dfsHelper(start, visited, result);
        return result;
    }
    
    private void dfsHelper(T vertex, Set<T> visited, List<T> result) {
        visited.add(vertex);
        result.add(vertex);
        
        for (T neighbor : adjacencyList.get(vertex)) {
            if (!visited.contains(neighbor)) {
                dfsHelper(neighbor, visited, result);
            }
        }
    }
    
    public static void main(String[] args) {
        Graph<String> graph = new Graph<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        
        System.out.println("BFS from A: " + graph.bfs("A"));
        System.out.println("DFS from A: " + graph.dfs("A"));
    }
}


---

### Exercise 25: Interval Merge
**Difficulty:** Hard  
**Time:** 35 minutes  
**Topics:** Sorting, interval merging, algorithm

**Complete Solution:**

import java.util.*;

public class Interval {
    public int start;
    public int end;
    
    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String toString() {
        return "[" + start + "," + end + "]";
    }
}

public class IntervalMerge {
    public static List<Interval> merge(List<Interval> intervals) {
        if (intervals.isEmpty()) return intervals;
        
        // Sort by start time
        intervals.sort(Comparator.comparingInt(i -> i.start));
        
        List<Interval> merged = new ArrayList<>();
        Interval current = intervals.get(0);
        
        for (int i = 1; i < intervals.size(); i++) {
            Interval next = intervals.get(i);
            
            if (current.end >= next.start) {
                // Overlapping - merge
                current.end = Math.max(current.end, next.end);
            } else {
                // Non-overlapping - add current and move to next
                merged.add(current);
                current = next;
            }
        }
        
        merged.add(current);
        return merged;
    }
    
    public static void main(String[] args) {
        List<Interval> intervals = Arrays.asList(
            new Interval(1, 3),
            new Interval(2, 6),
            new Interval(8, 10),
            new Interval(15, 18)
        );
        
        System.out.println("Original: " + intervals);
        System.out.println("Merged: " + merge(intervals));
    }
}


---

## 🎯 Interview Exercises (26-30)

### Exercise 26: Two Sum in Sorted Array
**Difficulty:** Interview  
**Time:** 20 minutes

**Complete Solution:**

import java.util.Arrays;

public class TwoSumSorted {
    public static int[] twoSum(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int sum = nums[left] + nums[right];
            if (sum == target) {
                return new int[]{left, right};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{};
    }
    
    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        System.out.println("Indices: " + Arrays.toString(twoSum(nums, target)));
    }
}


---

### Exercise 27: 3Sum Problem
**Difficulty:** Interview  
**Time:** 25 minutes

**Complete Solution:**

import java.util.*;

public class ThreeSum {
    public static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            int left = i + 1, right = nums.length - 1;
            int target = -nums[i];
            
            while (left < right) {
                int sum = nums[left] + nums[right];
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        System.out.println("3Sum: " + threeSum(nums));
    }
}


---

### Exercise 28: Kth Largest Element
**Difficulty:** Interview  
**Time:** 25 minutes

**Complete Solution:**

import java.util.*;

public class KthLargest {
    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        for (int num : nums) {
            minHeap.add(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        
        return minHeap.peek();
    }
    
    public static void main(String[] args) {
        int[] nums = {3, 2, 1, 5, 6, 4};
        System.out.println("3rd largest: " + findKthLargest(nums, 3));
    }
}


---

### Exercise 29: LRU Cache Design
**Difficulty:** Interview  
**Time:** 30 minutes

**Complete Solution:**

import java.util.*;

public class LRUCacheDesign {
    private int capacity;
    private Map<Integer, Integer> cache;
    
    public LRUCacheDesign(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }
    
    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }
    
    public void put(int key, int value) {
        cache.put(key, value);
    }
    
    public static void main(String[] args) {
        LRUCacheDesign cache = new LRUCacheDesign(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println("Get 1: " + cache.get(1));
        cache.put(3, 3);
        System.out.println("Get 2: " + cache.get(2));
    }
}


---

### Exercise 30: Word Ladder
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**

import java.util.*;

public class WordLadder {
    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;
        
        Queue<String> queue = new LinkedList<>();
        queue.add(beginWord);
        int level = 1;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String word = queue.poll();
                if (word.equals(endWord)) return level;
                
                for (String neighbor : getNeighbors(word, wordSet)) {
                    queue.add(neighbor);
                    wordSet.remove(neighbor);
                }
            }
            level++;
        }
        
        return 0;
    }
    
    private static List<String> getNeighbors(String word, Set<String> wordSet) {
        List<String> neighbors = new ArrayList<>();
        char[] chars = word.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (chars[i] != c) {
                    chars[i] = c;
                    String neighbor = new String(chars);
                    if (wordSet.contains(neighbor)) {
                        neighbors.add(neighbor);
                    }
                    chars[i] = word.charAt(i);
                }
            }
        }
        
        return neighbors;
    }
    
    public static void main(String[] args) {
        List<String> wordList = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println("Ladder length: " + ladderLength("hit", "cog", wordList));
    }
}
