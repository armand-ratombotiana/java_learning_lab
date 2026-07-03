# Debugging — Searching

## Trace Output
`java
System.out.printf("lo=%d, hi=%d, mid=%d, arr[mid]=%d%n", lo, hi, mid, arr[mid]);
`

## Boundary Tests
`java
assertEquals(-1, binarySearch(new int[]{}, 5));     // empty
assertEquals(0, binarySearch(new int[]{5}, 5));      // single match
assertEquals(-1, binarySearch(new int[]{5}, 3));     // single no match
assertEquals(0, binarySearch(new int[]{3,5,7}, 3));  // first
assertEquals(2, binarySearch(new int[]{3,5,7}, 7));  // last
`
"@

wf "REFACTORING.md" @"
# Refactoring — Searching

## Extract Comparator
`java
public static <T> int binarySearch(T[] arr, T target, Comparator<T> cmp) {
    // use cmp.compare(arr[mid], target)
}
`

## Search Result Record
`java
public record SearchResult(int index, int comparisons) {}
`
"@

wf "PERFORMANCE.md" @"
# Performance — Searching

| Algorithm | Best | Average | Worst | Space |
|-----------|------|---------|-------|-------|
| Linear | O(1) | O(n) | O(n) | O(1) |
| Binary | O(1) | O(log n) | O(log n) | O(1) |
| Interpolation | O(1) | O(log log n) | O(n) | O(1) |

## Benchmarks (n=1,000,000)
- Linear: ~0.5ms avg, ~5ms worst
- Binary: ~30ns
- Interpolation: ~15ns avg
