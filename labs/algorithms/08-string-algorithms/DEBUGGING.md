# Debugging — String Algorithms

## Print Prefix Function
`java
System.out.println("π = " + Arrays.toString(pi));
`

## KMP Step by Step
`java
System.out.printf("i=%d, q=%d, char=%c%n", i, q, text.charAt(i));
`

## Rabin-Karp Hash Debug
`java
System.out.printf("Window [%d,%d): hash=%d, patternHash=%d%n", i, i+m, txtHash, patHash);
`
"@

wf "REFACTORING.md" @"
# Refactoring — String Algorithms

## KMP with Iterator
`java
class KmpMatcher implements Iterator<Integer> {
    // returns all match positions lazily
}
`

## Trie with Generic Alphabet
`java
class Trie<T> {
    Map<T, Trie<T>> children;
    boolean isEnd;
}
`
"@

wf "PERFORMANCE.md" @"
# Performance — String Algorithms

| Algorithm | Preprocess | Search | Space |
|-----------|-----------|--------|-------|
| Naive | O(1) | O(nm) | O(1) |
| KMP | O(m) | O(n) | O(m) |
| Rabin-Karp | O(m) | O(n) avg | O(1) |
| Trie | O(L) | O(m) | O(L × A) |

n = text length, m = pattern length, L = total string length, A = alphabet size

## Benchmarks (n=1,000,000, m=100)
| Algorithm | Time |
|-----------|------|
| Naive | ~50ms (avg), ~10s (worst) |
| KMP | ~2ms |
| Rabin-Karp | ~3ms |
