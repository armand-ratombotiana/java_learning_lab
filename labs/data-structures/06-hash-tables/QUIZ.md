# Quiz: Hash Tables

**Question 1**: What is the average time complexity of get() in a well-implemented hash table?
- A) O(1)  ✓
- B) O(log n)
- C) O(n)
- D) O(n²)

**Question 2**: What is the default load factor of Java's HashMap?
- A) 0.50
- B) 0.75  ✓
- C) 1.00
- D) 0.25

**Question 3**: What happens when the load factor threshold is exceeded in HashMap?
- A) Buckets are merged
- B) Capacity is doubled and entries rehashed  ✓
- C) Capacity is halved
- D) Old entries are removed

**Question 4**: Which collision resolution method stores entries in the array itself, probing for empty slots?
- A) Separate chaining
- B) Open addressing  ✓
- C) Robin Hood hashing
- D) Cuckoo hashing

**Question 5**: In Java 8+, what happens when a HashMap bucket chain exceeds 8 entries?
- A) Chain is removed
- B) List is converted to a Red-Black tree  ✓
- C) Capacity is doubled
- D) Chain is sorted

**Question 6**: Which of the following is NOT a requirement for a valid hashCode implementation?
- A) If a.equals(b), then a.hashCode() == b.hashCode()
- B) hashCode should be consistent within one execution
- C) Different objects must have different hash codes  ✓
- D) hashCode should use fields used in equals

**Question 7**: Why does HashMap require power-of-2 capacity?
- A) Simplifies resize
- B) Enables fast index computation using bitwise AND  ✓
- C) Reduces memory fragmentation
- D) Enables treeification

**Question 8**: Which map implementation maintains insertion order?
- A) HashMap
- B) LinkedHashMap  ✓
- C) TreeMap
- D) ConcurrentHashMap
