# Quiz: Bloom Filters

**Question 1**: What property does a Bloom filter guarantee?
- A) No false positives
- B) No false negatives  ✓
- C) No errors
- D) O(1) memory

**Question 2**: What is the time complexity of a Bloom filter query?
- A) O(1)
- B) O(k) where k = number of hash functions  ✓
- C) O(log n)
- D) O(n)

**Question 3**: If a Bloom filter says an element is not present, what do we know?
- A) It might be present
- B) It is definitely not present  ✓
- C) It is probably present
- D) The filter is full

**Question 4**: What is the optimal number of hash functions k?
- A) k = m/n
- B) k = (m/n) × ln(2)  ✓
- C) k = n/m
- D) k = 7 always

**Question 5**: Which Java library provides a production Bloom filter?
- A) Apache Commons
- B) Guava  ✓
- C) Spring
- D) Jackson

**Question 6**: What happens when more elements are inserted than the expected n?
- A) Filter automatically resizes
- B) False positive rate increases  ✓
- C) False negatives appear
- D) Filter stops working

**Question 7**: What is a Counting Bloom filter used for?
- A) Reducing memory
- B) Supporting deletion  ✓
- C) Faster queries
- D) Better hash functions

**Question 8**: Who invented the Bloom filter?
- A) Burton Bloom  ✓
- B) Edsger Dijkstra
- C) Donald Knuth
- D) Robert Tarjan
