# Interview Questions: TreeMap & TreeSet

## Company-Specific Focus

### Google
- TreeMap: Red-Black tree implementation of NavigableMap
- Ordering: natural ordering (Comparable) or custom Comparator
- Guaranteed O(log n) for containsKey, get, put, remove

### Microsoft
- TreeMap vs SortedDictionary in C#: similar Red-Black tree
- Range views: subMap, headMap, tailMap for efficient range queries

### Amazon
- Range queries for time-series data: subMap on timestamps
- NavigableMap: ceilingKey, floorKey, higherKey, lowerKey
- TreeSet for sorted unique elements

### Meta
- Red-Black tree balancing: insertion and deletion rebalancing rules
- Comparator consistency: must be consistent with equals
- Natural ordering vs Comparator: compareTo throws ClassCastException for incompatible types

### Apple
- TreeSet elements must implement Comparable or provide Comparator
- Immutable sorted sets: Collections.unmodifiableSortedSet
- NavigableSet descendingSet: reverse iteration

### Oracle
- TreeMap specification: Red-Black tree, NavigableMap, SortedMap
- Entry node: left, right, parent, color, key, value
- put: standard BST insertion + Red-Black rebalancing

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 729 My Calendar I | Medium | Google, Amazon | TreeMap for interval management |
| 732 My Calendar III | Hard | Amazon, Google | TreeMap for event counting |
| 846 Hand of Straights | Medium | Amazon, Google | TreeMap for card distribution |
| 853 Car Fleet | Medium | Amazon, Google, Apple | TreeMap for position-time sorting |
| 220 Contains Duplicate III | Hard | Amazon, Google | TreeSet for sliding window ordering |
| 352 Data Stream as Disjoint Intervals | Hard | Google, Amazon | TreeMap for interval merging |
| 363 Max Sum of Rectangle No Larger Than K | Hard | Google, Amazon | TreeSet for prefix sum search |

## Real Production Scenarios
- **Uber**: TreeMap for geospatial indexing — O(log n) nearest-neighbor queries
- **LinkedIn**: TreeSet for maintaining top K candidates by score in a leaderboard
- **Amazon**: NavigableMap for precisely tracking inventory by expiration date efficiently

## Interview Patterns & Tips
- **Red-Black tree**: Self-balancing BST, O(log n) guaranteed
- **Range view**: subMap returns a view backed by the original map
- **Comparator**: Must be consistent with equals for Set contract
- **NavigableMap**: ceiling/floor key methods are O(log n)

## Deep Dive Questions
- **Red-Black properties**: What are the 5 Red-Black tree invariants?
- **Insertion**: How are insertions rebalanced? (Cases involving uncle color)
- **Deletion**: Why is deletion more complex than insertion in Red-Black trees?
- **subMap**: How does subMap work as a view? What modifications are allowed?
- **Comparator vs Comparable**: What happens if Comparator is inconsistent with equals?