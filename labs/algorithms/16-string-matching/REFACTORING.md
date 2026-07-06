# Refactoring — String Matching

## Strategy Pattern for Matching Algorithms

`java
interface StringMatcher {
    List<Integer> search(String text, String pattern);
}
`

This allows easy switching between algorithms and benchmarking.

## Extracting Hash Function

`java
class RollingHash {
    private final long hash;
    private final long power;
    public RollingHash next(char out, char in) { ... }
}
`

## Generic Character Sequences

Support CharSequence instead of String for flexibility with StringBuilder and other character sources.

## Parallel Search

For very large texts, split the text into chunks (with overlap of m-1 characters for boundary patterns) and search in parallel.

## Memory Optimization

For Aho-Corasick, replace arrays with ArrayLists for sparse alphabets, or use HashMap transitions to reduce memory when the alphabet is large but usage is sparse.
