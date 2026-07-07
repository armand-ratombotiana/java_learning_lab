# Flashcards: XOR and Unrolled Linked Lists

## Card 1
**Q**: What is the average-case time complexity for insert in 
**A**: O(log n)

## Card 2
**Q**: What problem does XOR and Unrolled Linked Lists solve?
**A**: It provides efficient storage and retrieval of key-value pairs with guaranteed performance bounds.

## Card 3
**Q**: What is a load factor?
**A**: The ratio of stored elements to total capacity, which determines when rehashing occurs.

## Card 4
**Q**: What is the space complexity of 
**A**: O(n) where n is the number of stored elements.

## Card 5
**Q**: What happens during a rehash operation?
**A**: The internal storage is resized and all existing elements are reinserted into the new structure.

## Card 6
**Q**: How does the structure handle hash collisions?
**A**: Through a specific collision resolution strategy designed for the variant.

## Card 7
**Q**: What is the main trade-off in 
**A**: Between space usage (load factor) and operational speed (collision probability).

## Card 8
**Q**: What is a common performance optimization?
**A**: Tuning the load factor and using an efficient hash function.

## Card 9
**Q**: What security consideration applies to hash-based structures?
**A**: Hash collision denial-of-service attacks where an attacker crafts colliding inputs.

## Card 10
**Q**: How does XOR and Unrolled Linked Lists compare to standard library implementations?
**A**: It offers different trade-offs in terms of constant factors and specific performance guarantees.
