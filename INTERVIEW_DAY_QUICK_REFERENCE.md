# ELITE JAVA - INTERVIEW DAY QUICK REFERENCE
## Lightning Guide for Fast Problem Solving

---

## ⚡ 30-SECOND DECISION TREE

```
Problem involves...
├─ Numbers/Array
│  ├─ Find/Search → HashMap O(1)
│  ├─ Order → TreeSet or sort
│  ├─ Frequency → HashMap<T, Integer>
│  └─ Sorted array → Binary search
├─ String
│  ├─ Build string → StringBuilder (not "")
│  ├─ Anagrams → Sort and compare
│  ├─ Palindrome → Two pointers
│  └─ Pattern → Regex or char map
├─ Graph/Tree
│  ├─ Find path → BFS/DFS
│  ├─ Shortest path → Dijkstra
│  └─ All pairs → Floyd-Warshall
├─ Design
│  ├─ Cache → LRU with LinkedHashMap
│  ├─ Pub-Sub → Observer pattern
│  └─ Factory → Abstract base + subclasses
└─ Data transformation
   └─ Stream pipeline
```

---

## 🎯 COMPLEXITY QUICK LOOKUP

| Operation | ArrayList | HashMap | TreeSet | LinkedList |
|-----------|-----------|---------|---------|-----------|
| Get       | O(1)      | O(1)    | O(log n)| O(n)      |
| Add       | O(1)*     | O(1)    | O(log n)| O(1)      |
| Remove    | O(n)      | O(1)    | O(log n)| O(n)*     |
| Find      | O(n)      | O(1)    | O(log n)| O(n)      |

*Add to ArrayList = O(n) at beginning; O(1) at end  
*Remove from LinkedList = O(n) to find; O(1) to remove

---

## 💡 PATTERN IMPLEMENTATION (Copy-Paste Ready)

### Two Pointer
```java
int left = 0, right = arr.length - 1;
while (left < right) {
    if (condition) {
        left++;
    } else {
        right--;
    }
}
```

### Sliding Window
```java
int left = 0;
for (int right = 0; right < arr.length; right++) {
    // Add arr[right] to window
    while (condition) {
        // Remove arr[left] from window
        left++;
    }
    // Update result with current window
}
```

### Binary Search
```java
int left = 0, right = arr.length - 1;
while (left <= right) {
    int mid = left + (right - left) / 2;  // Avoid overflow
    if (arr[mid] == target) {
        return mid;
    } else if (arr[mid] < target) {
        left = mid + 1;
    } else {
        right = mid - 1;
    }
}
```

### HashMap Grouping
```java
Map<String, List<Integer>> map = new HashMap<>();
for (Integer num : arr) {
    String key = getKey(num);
    map.computeIfAbsent(key, k -> new ArrayList<>()).add(num);
}
```

### Stream Grouping
```java
Map<String, Long> counts = list.stream()
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));
```

### Stream Top K
```java
list.stream()
    .sorted(Collections.reverseOrder())
    .limit(k)
    .collect(Collectors.toList());
```

### BFS Template
```java
Queue<Node> queue = new LinkedList<>();
Set<Node> visited = new HashSet<>();
queue.offer(start);
visited.add(start);

while (!queue.isEmpty()) {
    Node current = queue.poll();
    // Process current
    for (Node neighbor : current.getNeighbors()) {
        if (!visited.contains(neighbor)) {
            visited.add(neighbor);
            queue.offer(neighbor);
        }
    }
}
```

### DFS Template (Recursive)
```java
void dfs(Node node, Set<Node> visited) {
    visited.add(node);
    // Process node
    for (Node neighbor : node.getNeighbors()) {
        if (!visited.contains(neighbor)) {
            dfs(neighbor, visited);
        }
    }
}
```

---

## 🚀 GO-TO SOLUTIONS

### "Find duplicate in array"
```java
// O(n) time, O(n) space - best
Set<Integer> seen = new HashSet<>();
for (int num : arr) {
    if (!seen.add(num)) return num;
}
```

### "Most frequent element"
```java
jar.stream()
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ))
    .entrySet().stream()
    .max(Map.Entry.comparingByValue())
    .map(Map.Entry::getKey);
```

### "K closest points"
```java
arr.stream()
    .sorted((a, b) -> Integer.compare(
        distance(a), distance(b)
    ))
    .limit(k)
    .collect(Collectors.toList());
```

### "Valid parentheses"
```java
Stack<Character> stack = new Stack<>();
for (char c : s.toCharArray()) {
    if (isOpen(c)) {
        stack.push(c);
    } else {
        if (stack.isEmpty() || !matches(stack.pop(), c)) {
            return false;
        }
    }
}
return stack.isEmpty();
```

### "Reverse string"
```java
char[] chars = s.toCharArray();
int left = 0, right = chars.length - 1;
while (left < right) {
    char temp = chars[left];
    chars[left] = chars[right];
    chars[right] = temp;
    left++;
    right--;
}
```

---

## 🔧 COLLECTION OPERATIONS CHEAT SHEET

### ArrayList
```java
list.add(element);              // O(1) amortized
list.add(index, element);       // O(n)
list.remove(index);             // O(n)
list.get(index);                // O(1)
list.contains(element);         // O(n)
Collections.sort(list);         // O(n log n)
```

### HashMap
```java
map.put(key, value);            // O(1)
map.get(key);                   // O(1)
map.containsKey(key);           // O(1)
map.remove(key);                // O(1)
map.values();                   // O(n)
map.entrySet();                 // iterate O(n)
```

### Stream Collectors
```java
Collectors.toList()             // → List
Collectors.toSet()              // → Set
Collectors.toMap()              // → Map
Collectors.groupingBy()         // → Map<K, List>
Collectors.mapping()            // → Transform & collect
Collectors.joining()            // → String concatenation
Collectors.counting()           // → Long count
Collectors.averagingInt()       // → Double average
```

---

## 🎭 DESIGN PATTERN TEMPLATES

### Singleton
```java
class Singleton {
    private static Singleton instance;
    
    private Singleton() {}
    
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

// Better: Double-checked locking
public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

### Factory
```java
abstract class Animal {
    abstract void speak();
}

class AnimalFactory {
    public static Animal create(String type) {
        switch(type) {
            case "dog": return new Dog();
            case "cat": return new Cat();
            default: throw new IllegalArgumentException();
        }
    }
}
```

### Builder
```java
class User {
    private String name;
    private int age;
    private String email;
    
    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
    }
    
    public static class Builder {
        private String name;
        private int age;
        private String email;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public User build() {
            return new User(this);
        }
    }
}

// Usage
User user = new User.Builder()
    .name("John")
    .age(30)
    .email("john@example.com")
    .build();
```

---

## ⚠️ COMMON MISTAKES & FIXES

| Mistake | Problem | Fix |
|---------|---------|-----|
| `result += str` in loop | O(n²) time | Use `StringBuilder` |
| `new int[1000000]` on stack | Stack overflow | Use `ArrayList` |
| `null` in HashMap | NullPointerException | Use `.getOrDefault()` |
| `.get()` on LinkedList | O(n) operation | Use `ArrayList` |
| `stream().forEach()` | Not lazy | Use `map()` then `collect()` |
| Lambda captures non-final | Won't compile | Make variable final |
| No null check in `.get()` | NPE | Use `.ifPresent()` with Optional |
| Mutable key in HashMap | Bug later | Use immutable types (String) |

---

## 🎯 5-MINUTE INTERVIEW SOLUTION TEMPLATE

```java
public ResultType solve(InputType input) {
    // 1. VALIDATE (30 seconds)
    if (input == null || input.isEmpty()) {
        return handleEdgeCase();
    }
    
    // 2. PLAN (1 minute)
    // Approach: [Describe algorithm in 1 sentence]
    // Time: O(n log n)
    // Space: O(n)
    
    // 3. CODE (3 minutes)
    // Use patterns from above templates
    
    // 4. TEST (1 minute)
    // Example: input = [1,2,3], expected = X
    
    return result;
}
```

---

## 💪 POWER MOVES IN INTERVIEWS

### Show Big-O Thinking
"This solution is O(n²) because... but we can optimize to O(n) using a HashMap to...""

### Discuss Tradeoffs
"HashMap is faster at O(1) lookup but uses O(n) space. TreeSet uses O(log n) but keeps data sorted."

### Mention Edge Cases
"We need to handle empty input, null values, and duplicate elements."

### Ask Clarifying Questions
"Can the array have negative numbers? Is it sorted? Do we need to modify the input?"

### Explain While Coding
"I'm iterating through the array, and for each element, I'm checking if its complement exists in the HashMap."

---

## 📊 QUICK STATS TO MEMORIZE

- Sorting: O(n log n)
- Hash table: O(1) average, O(n) worst
- Binary search: O(log n)
- Two pointers: O(n)
- Sliding window: O(n)
- BFS/DFS: O(n + m) where m = edges
- String comparison: O(n) where n = length

---

## 📱 BEFORE YOU WALK IN (Checklist)

✅ Know Big O notation cold  
✅ Know 5 common patterns (Two pointer, Sliding window, etc.)  
✅ Know Collections hierarchy by heart  
✅ Know 3 design patterns well  
✅ Practice 30 minutes on LeetCode  
✅ Get 8 hours sleep  
✅ Eat protein breakfast  
✅ Deep breathing exercises  
✅ Have water bottle  
✅ Smile and be confident  

---

**Last Updated**: March 6, 2026  
**Confidence Level**: ⭐⭐⭐⭐⭐ (5/5)  
**Ready to Interview**: ✅ ABSOLUTELY
