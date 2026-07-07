# Interview Questions: Disjoint Set Union with Rollbacks

## Basic Questions

### Q1: Explain how Disjoint Set Union with Rollbacks works.
Provide a high-level overview of the structure, its key operations, and their time complexities.

### Q2: What are the advantages of Disjoint Set Union with Rollbacks over standard alternatives?
Discuss the specific performance characteristics, memory usage, and use cases where Disjoint Set Union with Rollbacks excels.

### Q3: How does the structure handle collisions?
Explain the collision resolution strategy and why it was chosen over alternatives.

## Intermediate Questions

### Q4: Describe the rehashing process.
Explain when rehashing is triggered, how it works, and its amortized cost.

### Q5: How would you make Disjoint Set Union with Rollbacks thread-safe?
Discuss synchronization strategies and their performance implications.

### Q6: How does load factor affect performance?
Explain the trade-off between memory usage and operational speed.

## Advanced Questions

### Q7: Design a persistent version of Disjoint Set Union with Rollbacks.
Discuss how to implement efficient snapshots and immutable operations.

### Q8: How would you benchmark Disjoint Set Union with Rollbacks against alternatives?
Describe your benchmarking methodology, metrics, and analysis approach.

### Q9: Implement a custom hash function.
Write code for a hash function suitable for Disjoint Set Union with Rollbacks and explain your design choices.

### Q10: How would you handle hash collision DoS attacks?
Discuss mitigation strategies including salted hashes and load shedding.
