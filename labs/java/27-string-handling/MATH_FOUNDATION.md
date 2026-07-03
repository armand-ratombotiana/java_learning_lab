# Mathematical Foundation: String Handling

## Time Complexity

### String Operations
| Operation | Complexity | Notes |
|-----------|-----------|-------|
| charAt(i) | O(1) | Direct array access |
| length() | O(1) | Stored in field |
| substring (Java 6) | O(1) | Shared backing array (removed in Java 7+) |
| substring (Java 7+) | O(n) | Copies character array |
| equals() | O(n) | Compares each character |
| compareTo() | O(n) | Lexicographic comparison |
| indexOf() | O(n*m) | Naive search, worst-case O(n*m) |
| matches(regex) | O(2^m) potentially | Backtracking in regex |

### StringBuilder Operations
| Operation | Amortized | Worst Case |
|-----------|-----------|------------|
| append(c) | O(1) amortized | O(n) when resize needed |
| append(str) | O(k) amortized | O(n+k) when resize needed |
| toString() | O(n) | Copies internal array |

## Amortized Analysis of StringBuilder.append()
Let n be final string length.
- Appending n characters one at a time: O(n) amortized
- Resizing cost: 2 + 4 + 8 + ... + n/2 + n = O(n)
- Total cost: O(n) amortized, O(1) per append

## String Hash Code
```java
s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
```
The multiplier 31 is chosen because:
- It's an odd prime (better distribution)
- 31 * i = (i << 5) - i (fast bit shift optimization)
- Historical reasons (from Java 1.0)

## Substring Memory (pre-Java 7)
In Java 6, substring(length) = O(1) but kept reference to original char array, causing memory leaks. Java 7+ copies the array: O(n) time, O(1) memory leak prevention.
