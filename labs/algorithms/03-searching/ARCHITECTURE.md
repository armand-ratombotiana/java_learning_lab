# Architecture — Searching

## Java Standard Library
`java
java.util.Arrays.binarySearch(int[], int)
java.util.Arrays.binarySearch(Object[], Object)
java.util.Collections.binarySearch(List, T)
`

## Beyond Arrays
- TreeMap/TreeSet: Red-black tree O(log n)
- HashMap: O(1) average
- ConcurrentSkipListMap: O(log n) concurrent
