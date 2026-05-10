# Java Collections - Quizzes

## Quiz 1: Collection Types

### Question 1.1
Which collection does NOT allow duplicates?

A) ArrayList  
B) HashSet  
C) LinkedList  
D) Vector  

**Answer: B**

---

### Question 1.2
Which maintains insertion order?

A) HashSet  
B) TreeSet  
C) LinkedHashSet  
D) EnumSet  

**Answer: C**

---

### Question 1.3
What is the time complexity of HashMap.get()?

A) O(n)  
B) O(log n)  
C) O(1)  
D) O(1) average, O(n) worst  

**Answer: D**

---

### Question 1.4
Which is NOT a valid way to create an immutable list?

A) List.of(1, 2, 3)  
B) Arrays.asList(1, 2, 3)  
C) new ArrayList<>(List.of(1, 2, 3))  
D) Collections.unmodifiableList(list)  

**Answer: C** (Creates mutable copy)

---

### Question 1.5
What does LinkedList implement?

A) List only  
B) Queue only  
C) List and Queue  
D) List, Queue, and Deque  

**Answer: D**

---

## Quiz 2: Map Operations

### Question 2.1
What happens if you add duplicate key to HashMap?

A) Throws exception  
B) Ignores new value  
C) Overwrites old value  
D) Creates new entry  

**Answer: C**

---

### Question 2.2
Which method returns default value if key not found?

A) get()  
B) getOrDefault()  
C) computeIfAbsent()  
D) find()  

**Answer: B**

---

### Question 2.3
What is returned by map.keySet()?

A) List  
B) Set  
C) Collection  
D) Iterator  

**Answer: B**

---

### Question 2.4
How do you create a synchronized Map?

A) new ConcurrentHashMap<>()  
B) Collections.synchronizedMap()  
C) Map.of()  
D) Both A and B  

**Answer: D**

---

### Question 2.5
What happens if you put null as key in TreeMap?

A) Allowed  
B) Throws NullPointerException  
C) Ignored  
D) Depends on Java version  

**Answer: B** (TreeMap doesn't allow null keys)

---

## Quiz 3: Set Operations

### Question 3.1
What is returned by set.retainAll(otherSet)?

A) Union  
B) Intersection  
C) Difference  
D) Symmetric difference  

**Answer: B**

---

### Question 3.2
Which Set is sorted?

A) HashSet  
B) LinkedHashSet  
C) TreeSet  
D) EnumSet  

**Answer: C**

---

### Question 3.3
What does addAll() return?

A) void  
B) boolean  
C) Set  
D) int (count added)  

**Answer: B**

---

### Question 3.4
How do you check if set is empty?

A) set.isEmpty()  
B) set.size() == 0  
C) set == null  
D) A or B  

**Answer: D**

---

### Question 3.5
What is required for objects in HashSet?

A) toString()  
B) equals() and hashCode()  
C) Comparable  
D) Serializable  

**Answer: B**

---

## Quiz 4: Queue Operations

### Question 4.1
What does poll() do on empty queue?

A) Blocks  
B) Returns null  
C) Throws exception  
D) Returns Optional  

**Answer: B**

---

### Question 4.2
What does take() do on empty queue?

A) Returns null  
B) Returns Optional  
C) Blocks until element available  
D) Throws exception  

**Answer: C**

---

### Question 4.3
Which is LIFO?

A) Queue  
B) Deque  
C) Stack  
D) PriorityQueue  

**Answer: C**

---

### Question 4.4
What is default order in PriorityQueue?

A) FIFO  
B) LIFO  
C) Min-heap (smallest first)  
D) Max-heap (largest first)  

**Answer: C**

---

### Question 4.5
ArrayDeque vs LinkedList for queue operations?

A) ArrayDeque is faster for most operations  
B) LinkedList is faster  
C) Same performance  
D) Depends on size  

**Answer: A**

---

## Quiz 5: Performance

### Question 5.1
ArrayList.get(0) time complexity?

A) O(1)  
B) O(n)  
C) O(log n)  
D) O(1) amortized  

**Answer: A**

---

### Question 5.2
LinkedList.get(0) time complexity?

A) O(1)  
B) O(n)  
C) O(log n)  
D) O(1) amortized  

**Answer: A** (First element is O(1))

---

### Question 5.3
TreeSet.contains() time complexity?

A) O(1)  
B) O(n)  
C) O(log n)  
D) O(1) amortized  

**Answer: C**

---

### Question 5.4
HashSet.add() worst case?

A) O(1)  
B) O(n)  
C) O(log n)  
D) O(1) average  

**Answer: B** (When hash collision)

---

### Question 5.5
ArrayList.add(0, element) time complexity?

A) O(1)  
B) O(n)  
C) O(log n)  
D) O(1) amortized  

**Answer: B**

---

## Answer Key

| Quiz | Answers |
|------|---------|
| Quiz 1 | 1. B, 2. C, 3. D, 4. C, 5. D |
| Quiz 2 | 1. C, 2. B, 3. B, 4. D, 5. B |
| Quiz 3 | 1. B, 2. C, 3. B, 4. D, 5. B |
| Quiz 4 | 1. B, 2. C, 3. C, 4. C, 5. A |
| Quiz 5 | 1. A, 2. A, 3. C, 4. B, 5. B |
