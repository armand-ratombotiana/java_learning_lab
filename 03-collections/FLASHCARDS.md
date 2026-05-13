# Collection Flashcards

## Q: When should I use ArrayList vs LinkedList?
**A:** ArrayList for frequent random access (O(1) get). LinkedList for frequent insertions/deletions (O(1) add/remove in middle).

## Q: What is the time complexity of HashMap get()?
**A:** O(1) average, O(n) worst if many collisions.

## Q: How does HashMap handle collisions?
**A:** Uses chaining (linked list), switches to balanced tree when bucket has 8+ entries (Java 8+).

## Q: What is the difference between HashSet and TreeSet?
**A:** HashSet uses hash table (O(1) operations), TreeSet uses red-black tree (O(log n), sorted).

## Q: When should I use ConcurrentHashMap?
**A:** When multiple threads need to access a map concurrently. Provides better concurrency than synchronized HashMap.

## Q: What is the load factor in HashMap?
**A:** Threshold for rehashing. Default is 0.75. When size exceeds capacity × load factor, rehash occurs.

## Q: How do you make a collection thread-safe?
**A:** Use Collections.synchronizedXxx() or use concurrent collections from java.util.concurrent.

## Q: What is the difference between List and Set?
**A:** List allows duplicates, maintains insertion order. Set doesn't allow duplicates, order depends on implementation.

## Q: What is a weak reference in WeakHashMap?
**A:** Keys are weakly referenced; entries are removed when keys are no longer referenced elsewhere.

## Q: How does ArrayList grow?
**A:** Grows by 50% when full (newCapacity = oldCapacity + oldCapacity/2).