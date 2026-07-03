# Collections — Guided Self-Reflection

## Comprehension Check

1. Before this lab, which collection types did you use most? Were you choosing them for the right reasons?

2. How did you handle duplicate removal before knowing about HashSet? Did you write manual loops?

3. What surprised you about the internal workings of HashMap?

## Code Review

Review a project you've worked on:
- Are collections used with generic type parameters?
- Are there any raw collection types?
- Are there ConcurrentModificationException risks?
- Could any Maps benefit from computeIfAbsent/merge?
- Are any Vectors, Hashtables, or Stacks being used?

## Design Thinking

4. You're building a real-time analytics dashboard. Data arrives as events (timestamp, metric, value). What collection(s) would you use for:
   - Storing the last 1000 events?
   - Looking up events by timestamp range?
   - Aggregating metrics by type?

5. Design a thread-safe event bus. What collections would back the subscriber registry? The event queue?

## Challenges Encountered

6. Have you ever encountered ConcurrentModificationException? What was the cause and how did you fix it?

7. Have you had a production issue related to collection choice (e.g., O(n) lookup on large list)?

## Application

8. How does understanding time complexity change how you write code? Do you think about Big-O when choosing a collection?

9. What's your decision process for choosing between ArrayList and LinkedList?

## Future Learning

10. What collections topics do you want to explore further?
    - Concurrent collections (ConcurrentLinkedQueue, DelayQueue)
    - Guava collections (Multimap, BiMap, Table)
    - Eclipse Collections primitive collections
    - Java 21 SequencedCollection in detail
