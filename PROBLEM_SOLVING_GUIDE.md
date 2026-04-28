# ELITE JAVA - PROBLEM SOLVING GUIDE
## Real Interview Problems & Solutions

---

## 🎯 PROBLEM-SOLVING FRAMEWORK

### The ACEQL Approach

1. **Ask** - Clarify requirements
2. **Code** - Implement solution
3. **Explain** - Discuss approach
4. **Verify** - Test edge cases
5. **Optimize** - Improve complexity

---

## 🔴 PROBLEM SET 1: ARRAY & LIST PROBLEMS

### Problem 1.1: Two Sum (LeetCode)

**Difficulty**: Easy  
**Companies**: Google, Amazon, Meta

**Problem**: Given an array of integers and a target sum, find two numbers that add up to the target.

```java
// SOLUTION 1: Brute Force - O(n²) time, O(1) space
public int[] twoSumBruteForce(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++) {
        for (int j = i + 1; j < nums.length; j++) {
            if (nums[i] + nums[j] == target) {
                return new int[]{i, j};
            }
        }
    }
    return new int[]{};
}

// SOLUTION 2: Hash Map - O(n) time, O(n) space ✅ OPTIMAL
public int[] twoSumOptimal(int[] nums, int target) {
    // Module 03 - Collections: Use HashMap for O(1) lookups
    Map<Integer, Integer> map = new HashMap<>();  
    
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        
        // Module 03 - HashMap: Check if complement exists
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        
        // Module 03 - HashMap: Store current number
        map.put(nums[i], i);
    }
    
    return new int[]{};
}
```

**Interview Discussion**:
- Explain why HashMap is better (O(n) vs O(n²))
- Discuss space-time tradeoff
- Handle edge cases (empty array, no solution)
- Ask: "What if array is sorted?" → Two pointer approach

---

### Problem 1.2: Group Anagrams

**Difficulty**: Medium  
**Companies**: Amazon, Meta, Microsoft

**Problem**: Group words that are anagrams of each other.

```java
// Input: ["eat","tea","tan","ate","nat","bat"]
// Output: [["eat","tea","ate"],["tan","nat"],["bat"]]

// SOLUTION: Stream pipeline with Collections
// Module 04 - Streams, Module 03 - Collections, Module 01 - Strings
public List<List<String>> groupAnagrams(String[] strs) {
    // Key insight: Anagrams have same sorted characters
    // "eat" and "ate" → "aet" (after sorting)
    
    // Module 03 - HashMap for grouping
    Map<String, List<String>> map = new HashMap<>();
    
    for (String str : strs) {
        // Module 01 - String: Sort characters
        char[] chars = str.toCharArray();
        Arrays.sort(chars);                    // Module 01 - Arrays
        String key = new String(chars);
        
        // Module 03 - Collections: Add to group
        map.computeIfAbsent(key, k -> new ArrayList<>())
           .add(str);
    }
    
    // Module 04 - Streams: Collect results
    return new ArrayList<>(map.values());
}

// ALTERNATIVE: Using Streams
public List<List<String>> groupAnagramsStreams(String[] strs) {
    return Arrays.stream(strs)                 // Module 04 - Streams
        .collect(Collectors.groupingBy(s -> {  // Module 03 - Collections
            char[] c = s.toCharArray();        // Module 01 - Arrays
            Arrays.sort(c);
            return new String(c);
        }))
        .values()
        .stream()
        .collect(Collectors.toList());
}
```

**Time Complexity Analysis**:
- HashMap put/get: O(1) average
- String sorting: O(k log k) where k = length of string
- Overall: O(n * k log k) where n = number of strings

**Space Complexity**: O(n * k) for storing all strings

---

### Problem 1.3: LRU Cache (Hard)

**Difficulty**: Hard  
**Companies**: Google, Amazon, Microsoft, Meta

**Problem**: Implement an LRU (Least Recently Used) cache with get and put operations.

```java
// Module 02 - OOP: Design with encapsulation
// Module 03 - Collections: Use Map and LinkedList
class LRUCache {
    private int capacity;
    private Map<Integer, Integer> cache;
    
    // Track access order - LinkedHashMap maintains insertion order
    // When we remove the head, it's the least recently used
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        // Module 03 - LinkedHashMap: Maintains insertion order
        this.cache = new LinkedHashMap<Integer, Integer>(
            capacity, 0.75f, true) {  // true = access-order
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > capacity;
            }
        };
    }
    
    // Module 01 - Method: Get with O(1) complexity
    public int get(int key) {
        // LinkedHashMap automatically moves accessed item to end
        return cache.getOrDefault(key, -1);
    }
    
    // Module 01 - Method: Put with O(1) complexity
    public void put(int key, int value) {
        cache.put(key, value);  // O(1) average
        // removeEldestEntry automatically called if over capacity
    }
}

// MANUAL IMPLEMENTATION (Shows Module 02 + Module 03 mastery):
class LRUCacheManual {
    class Node {
        int key;
        int value;
        Node prev;
        Node next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private int capacity;
    private Map<Integer, Node> map;  // O(1) key lookup
    private Node head;               // Most recently used
    private Node tail;               // Least recently used
    
    public LRUCacheManual(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        // Create dummy nodes to simplify edge cases
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        
        Node node = map.get(key);
        moveToHead(node);  // Mark as recently used
        return node.value;
    }
    
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToHead(node);
        } else {
            if (map.size() == capacity) {
                removeLRU();
            }
            
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addToHead(newNode);
        }
    }
    
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeLRU() {
        Node lru = tail.prev;
        removeNode(lru);
        map.remove(lru.key);
    }
}
```

**Interview Discussion**:
- Time complexity: O(1) for all operations
- Space complexity: O(capacity)
- Explain LinkedHashMap access-order mode
- Discuss manual implementation benefits (learning)
- Handle edge case: update existing key

---

## 🔵 PROBLEM SET 2: DESIGN & OOP PROBLEMS

### Problem 2.1: Design Parking Lot System

**Difficulty**: Hard  
**Companies**: Amazon, Microsoft, Google

**Problem**: Design a parking lot system with multiple levels and spot types.

```java
// Module 02 - OOP: Encapsulation, polymorphism
// Module 03 - Collections: Store and manage spots

abstract class ParkingSpot {  // Abstract base class
    public abstract boolean canFitVehicle(Vehicle v);
    public abstract void assignVehicle(Vehicle v);
    public abstract void removeVehicle();
}

class CompactSpot extends ParkingSpot {
    private Vehicle vehicle;
    
    @Override
    public boolean canFitVehicle(Vehicle v) {
        return v instanceof Motorcycle || v instanceof Compact;
    }
    
    @Override
    public void assignVehicle(Vehicle v) {
        if (canFitVehicle(v)) {
            this.vehicle = v;
        }
    }
    
    @Override
    public void removeVehicle() {
        this.vehicle = null;
    }
}

public class ParkingLot {
    private List<Level> levels;  // Module 03 - Collections
    
    public ParkingLot(int floors, int spotsPerFloor) {
        this.levels = new ArrayList<>();
        for (int i = 0; i < floors; i++) {
            levels.add(new Level(i, spotsPerFloor));
        }
    }
    
    // Module 04 - Streams: Find available spot
    public boolean parkVehicle(Vehicle vehicle) {
        return levels.stream()  // Module 04 - Streams
            .filter(level -> level.availableSpots() > 0)
            .findFirst()        // Module 04 - Optional
            .map(level -> level.parkVehicle(vehicle))
            .orElse(false);
    }
    
    public void unparkVehicle(Vehicle vehicle) {
        levels.stream()
            .filter(level -> level.hasVehicle(vehicle))
            .findFirst()
            .ifPresent(level -> level.unparkVehicle(vehicle));
    }
}

class Level {
    private int floor;
    private List<ParkingSpot> spots;  // Module 03 - Collections
    
    public Level(int floor, int numSpots) {
        this.floor = floor;
        this.spots = new ArrayList<>();
        // Create different spot types
        // Module 02 - OOP: Polymorphism
        for (int i = 0; i < numSpots / 2; i++) {
            spots.add(new CompactSpot());
        }
        for (int i = 0; i < numSpots / 4; i++) {
            spots.add(new LargeSpot());
        }
    }
    
    public boolean parkVehicle(Vehicle vehicle) {
        // Module 03 - Collections: Stream for finding spot
        return spots.stream()  // Module 04 - Streams
            .filter(ParkingSpot::isEmpty)
            .filter(spot -> spot.canFitVehicle(vehicle))
            .findFirst()       // Module 04 - Optional
            .map(spot -> {
                spot.assignVehicle(vehicle);
                return true;
            })
            .orElse(false);
    }
}
```

**Key Concepts Demonstrated**:
- Module 02: Polymorphism (ParkingSpot hierarchy)
- Module 02: Encapsulation (private fields)
- Module 03: ArrayList for managing spots
- Module 04: Streams for searching

---

### Problem 2.2: Observer Pattern (Pub-Sub)

**Difficulty**: Medium  
**Companies**: Meta, Microsoft, Apple

**Problem**: Implement a publisher-subscriber system.

```java
// Module 02 - OOP: Design Patterns

// Observer interface
interface Observer {  // Module 02 - Interface
    void update(String message);
}

// Concrete observer
class EmailSubscriber implements Observer {
    private String email;
    
    public EmailSubscriber(String email) {
        this.email = email;
    }
    
    @Override
    public void update(String message) {
        System.out.println("Email to " + email + ": " + message);
    }
}

// Publisher
class NewsPublisher {  // Module 02 - Encapsulation
    private List<Observer> subscribers = new ArrayList<>();  // Module 03
    
    public void subscribe(Observer observer) {
        subscribers.add(observer);
    }
    
    public void unsubscribe(Observer observer) {
        subscribers.remove(observer);
    }
    
    public void publishNews(String news) {
        // Module 04 - Streams: Notify all subscribers
        subscribers.stream()  
            .forEach(observer -> observer.update(news));
    }
}
```

---

## 🟡 PROBLEM SET 3: STREAM & FUNCTIONAL PROBLEMS

### Problem 3.1: Stream Data Processing

**Difficulty**: Medium  
**Companies**: Google, Amazon, Meta

**Problem**: Process a stream of user events and generate reports.

```java
// Module 04 - Streams: Complex pipeline
// Module 03 - Collections: Store and group results
// Module 01 - Exception: Handle errors
// Module 02 - OOP: Data classes

public List<Report> processUserEvents(List<UserEvent> events) {
    return events.stream()                          // Module 04 - Streams
        .filter(e -> isValidEvent(e))              // Filtering
        .map(this::enrichEvent)                    // Transformation
        .collect(Collectors.groupingBy(            // Module 03 - Collections
            UserEvent::getUserId,                  // Group by user
            Collectors.mapping(
                UserEvent::getEventType,           // Extract event types
                Collectors.toList()
            )
        ))
        .entrySet()
        .stream()                                  // Stream again
        .map(entry -> new Report(                  // Module 02 - OOP
            entry.getKey(),
            entry.getValue()
        ))
        .sorted(Comparator.comparing(Report::getUserId))
        .collect(Collectors.toList());
}

public List<String> findTopCategories(List<UserEvent> events) {
    return events.stream()
        .filter(e -> e.isValidPurchase())          // Module 04 - Filter
        .map(UserEvent::getCategory)               // Module 04 - Map
        .collect(Collectors.groupingBy(            // Module 03 - Group
            Function.identity(),
            Collectors.counting()                  // Count occurrences
        ))
        .entrySet()
        .stream()
        .sorted((e1, e2) -> Long.compare(          // Module 04 - Sort
            e2.getValue(),
            e1.getValue()
        ))
        .limit(10)                                 // Module 04 - Limit
        .map(Map.Entry::getKey)                    // Module 04 - Project
        .collect(Collectors.toList());
}
```

**Stream Pipeline Breakdown**:
1. `filter()` - Remove invalid events (Intermediate)
2. `map()` - Transform to target type (Intermediate)
3. `collect()` - Terminal operation, triggers execution
4. `groupingBy()` - Organize by category
5. `sorted()` - Order results
6. `collect()` - Final collection

---

### Problem 3.2: Optional Handling

**Difficulty**: Easy-Medium  
**Companies**: Google, Amazon, Meta

**Problem**: Handle nullable values safely with Optional.

```java
// Module 04 - Optional patterns
// Module 02 - OOP: Safe navigation

public class UserService {
    private Map<String, User> users = new HashMap<>();  // Module 03
    
    // ✅ GOOD: Safe Optional chaining
    public String getUserCity(String userId) {
        return users.entrySet().stream()           // Module 04
            .filter(e -> e.getKey().equals(userId))
            .findFirst()                           // returns Optional<Entry>
            .map(Map.Entry::getValue)              // unwrap User
            .map(User::getAddress)                 // User -> Address
            .map(Address::getCity)                 // Address -> City
            .orElse("Unknown");                    // Default value
    }
    
    // ✅ GOOD: Conditional with Optional
    public void sendNotification(String userId, String message) {
        users.values().stream()                    // Module 04
            .filter(u -> u.getId().equals(userId))
            .filter(User::isNotificationEnabled)   // Filter by status
            .findFirst()
            .ifPresent(user -> user.notify(message));  // Safe if-present
    }
    
    // ✅ GOOD: Complex Optional logic
    public User getOrCreateUser(String id, String name) {
        return users.containsKey(id)
            ? users.get(id)
            : new User(id, name);
        
        // Alternative with Optional:
        // return Optional.ofNullable(users.get(id))
        //     .orElseGet(() -> new User(id, name));
    }
}
```

---

## 🟢 PROBLEM SET 4: COMPLEXITY ANALYSIS PROBLEMS

### Problem 4.1: Optimize String Building

**Difficulty**: Easy  
**Companies**: Amazon, Google

**Before (O(n²))**:
```java
String result = "";                    // Module 01 - String
for (String item : items) {
    result += item;                    // O(n) string concatenation
}
// Total: O(n²) - creates n intermediate strings
```

**After (O(n))**:
```java
StringBuilder sb = new StringBuilder();  // Module 01 - StringBuilder
for (String item : items) {
    sb.append(item);                   // O(1) amortized
}
String result = sb.toString();         // O(n)
// Total: O(n)
```

**Interview Explanation**:
- String is immutable (Module 01)
- Each concatenation creates new String (O(n))
- StringBuilder has resizable internal array
- Append is O(1) amortized

---

### Problem 4.2: Choose Right Collection

**Difficult**: Easy-Medium  
**Companies**: Amazon, Microsoft

**Problem**: Access 1M integers 10M times per element

```java
// ❌ WRONG: LinkedList - O(n) access each
List<Integer> nums = new LinkedList<>(Arrays.asList(...));
for (int i = 0; i < 10_000_000; i++) {
    Integer val = nums.get(i % nums.size());  // O(n)
}
// Total time: O(n * m) where n = size, m = 10M

// ✅ CORRECT: ArrayList - O(1) access each
List<Integer> nums = new ArrayList<>(Arrays.asList(...));
for (int i = 0; i < 10_000_000; i++) {
    Integer val = nums.get(i % nums.size());  // O(1)
}
// Total time: O(m) where m = 10M
```

**Interview Explanation**:
- Module 03: Different collections suit different access patterns
- ArrayList: O(1) access, O(n) insert/delete
- LinkedList: O(n) access, O(1) insert/delete at head
- Choose based on use case

---

## 🔴🔵🟡🟢 INTERVIEW SCORING RUBRIC

### Excellent Answer (Position Secured)
✅ Correct solution  
✅ Optimal time/space complexity  
✅ Clean code structure  
✅ Handles edge cases  
✅ Explains tradeoffs  
✅ Discusses improvements  

### Good Answer (Likely Hire)
✅ Correct solution  
✅ Reasonable complexity  
✅ Readable code  
✅ Handles main cases  
✅ Some explanation  

### Acceptable Answer (Maybe Hire)
✅ Correct solution  
⚠️ Suboptimal complexity  
✅ Working code  
❌ Missing edge cases  

### Poor Answer (Unlikely Hire)
❌ Incorrect solution  
❌ Very poor complexity  
❌ Hard to follow code  
❌ Doesn't run  

---

## 📚 FINAL TIPS

### Code Quality Standards
1. **Naming**: Use clear, meaningful names
2. **Structure**: Logical method organization
3. **Comments**: Explain non-obvious logic
4. **Testing**: Verify with examples
5. **Error Handling**: Use specific exceptions

### Interview Success Factors
1. **Understand** the problem completely
2. **Discuss** your approach before coding
3. **Code** cleanly and confidently
4. **Test** with edge cases
5. **Optimize** with confidence

### Common Edge Cases
- Empty input
- Single element
- Duplicates
- Negative numbers
- Large numbers (overflow)
- Null values

---

**Created**: March 6, 2026  
**Total Problems**: 10+ with detailed solutions  
**Modules Integrated**: All 4 (Java Basics, OOP, Collections, Streams)  
**Ready for Interview**: ✅ YES
