# Java Collections - Exercises

## Exercise Set 1: List Operations

### Exercise 1.1: List Manipulation
Implement these methods without using utility classes:

```java
public class ListExercises {
    
    // Reverse a list in place
    public static <T> void reverse(List<T> list) {
        int left = 0, right = list.size() - 1;
        while (left < right) {
            T temp = list.get(left);
            list.set(left++, list.get(right));
            list.set(right--, temp);
        }
    }
    
    // Remove duplicates from unsorted list
    public static <T> void removeDuplicates(List<T> list) {
        Set<T> seen = new HashSet<>();
        list.removeIf(item -> !seen.add(item));
    }
    
    // Find second largest element
    public static Integer findSecondLargest(List<Integer> list) {
        if (list.size() < 2) return null;
        
        Integer max = list.get(0), second = null;
        for (Integer num : list) {
            if (num > max) {
                second = max;
                max = num;
            } else if (num > second && num < max) {
                second = num;
            }
        }
        return second;
    }
}
```

### Exercise 1.2: ArrayList vs LinkedList
Create a program that measures operation times on both types.

---

## Exercise Set 2: Set Operations

### Exercise 2.1: Set Operations
Implement union, intersection, and difference:

```java
public class SetOperations {
    
    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }
    
    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }
    
    public static <T> Set<T> difference(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }
    
    public static <T> Set<T> symmetricDifference(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.addAll(b);
        Set<T> common = intersection(a, b);
        result.removeAll(common);
        return result;
    }
}
```

### Exercise 2.2: Anagram Checker
Check if two strings are anagrams using sets.

---

## Exercise Set 3: Map Operations

### Exercise 3.1: Word Frequency Counter
```java
public class WordFrequency {
    
    public static Map<String, Integer> countWords(String text) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : text.toLowerCase().split("\\s+")) {
            freq.merge(word, 1, Integer::sum);
        }
        return freq;
    }
    
    public static List<Map.Entry<String, Integer>> topWords(String text, int n) {
        return countWords(text).entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(n)
            .collect(Collectors.toList());
    }
}
```

### Exercise 3.2: Inverted Index
Create an inverted index mapping words to document IDs.

---

## Exercise Set 4: Queue Operations

### Exercise 4.1: Josephus Problem
```java
public class JosephusProblem {
    
    public static int josephus(int n, int k) {
        if (n == 1) return 0;
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            queue.offer(i);
        }
        
        while (queue.size() > 1) {
            for (int i = 0; i < k - 1; i++) {
                queue.offer(queue.poll());
            }
            queue.poll();  // Remove the k-th person
        }
        
        return queue.poll();
    }
}
```

### Exercise 4.2: Priority Queue Tasks
Implement a task scheduler using PriorityQueue.

---

## Exercise Set 5: Custom Collections

### Exercise 5.1: Custom Comparator
```java
public class CustomComparators {
    
    // Sort by length, then alphabetically
    public static Comparator<String> byLengthThenAlpha() {
        return Comparator.comparingInt(String::length)
            .thenComparing(Comparator.naturalOrder());
    }
    
    // Sort by last name, then first name
    public static Comparator<Person> byLastNameThenFirst() {
        return Comparator.comparing(Person::getLastName)
            .thenComparing(Person::getFirstName);
    }
    
    // Sort descending
    public static <T extends Comparable<T>> Comparator<T> reversed() {
        return Comparator.<T>naturalOrder().reversed();
    }
}
```

---

## Challenge Problems

### Challenge 1: LRU Cache
Implement a Least Recently Used cache using LinkedHashMap.

### Challenge 2: Graph Implementation
Implement a graph using adjacency list (Map of Sets).

### Challenge 3: Multi-Value Map
Implement a Map that allows multiple values per key.
