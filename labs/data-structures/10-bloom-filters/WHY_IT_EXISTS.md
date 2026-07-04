# Why Bloom Filters Exist

Bloom filters exist to solve the **space-efficiency problem** of membership testing — checking whether an element is in a set without storing the elements themselves.

## Problems with Exact Structures

- **HashSet/HashMap**: stores all elements, memory = O(n × element_size)
- **Sorted array**: O(n) memory for all elements
- **BST**: O(n × node_overhead)
- For large sets (billions of elements), exact membership testing is memory-prohibitive

## What Bloom Filters Provide

| Need | Bloom Filter Solution |
|------|----------------------|
| Space efficiency | Fixed m bits regardless of element size |
| No false negatives | Guaranteed "definitely not" answers |
| Tunable accuracy | Adjust m and k for desired false positive rate |
| Privacy | Never stores the actual elements |
| Speed | O(k) operations, independent of set size |

## Key Trade-Off

Bloom filters exchange **exact accuracy** (occasional false positives) for **dramatic space savings**. A Bloom filter with 1% false positive rate uses ~10 bits per element — compared to hundreds of bytes for a HashSet.

This trade-off is acceptable when:
- False positives can be handled (e.g., confirmed with a disk lookup)
- Memory is the bottleneck (e.g., database caches)
- Absolute certainty is not required (e.g., spell-check suggestions)
