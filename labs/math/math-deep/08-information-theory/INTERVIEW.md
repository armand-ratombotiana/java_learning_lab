# Interview: Information Theory

## Q1: Conceptual Understanding
**Q**: What is Shannon entropy and why is it important?
**A**: Entropy measures the average information content or uncertainty in a random variable. It gives the fundamental limit of lossless compression (source coding theorem) and the maximum rate of reliable communication (channel coding theorem).

## Q2: Implementation
**Q**: How would you implement Huffman coding?
**A**: Build a frequency map, create leaf nodes in a priority queue, repeatedly combine two lowest-frequency nodes, assign 0/1 to left/right branches, traverse to assign codes. O(n log n) with binary heap.

## Q3: System Design
**Q**: Design a lossless compression system for text.
**A**: Preprocess with Burrows-Wheeler transform and move-to-front, then apply Huffman or arithmetic coding. Or use Lempel-Ziv (LZ77/LZ78) for dictionary-based compression like gzip.

## Coding Challenge
Compute the entropy of a discrete probability distribution from an array of probabilities.
