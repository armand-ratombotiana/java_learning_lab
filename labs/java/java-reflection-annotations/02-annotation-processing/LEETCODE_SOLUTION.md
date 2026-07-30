# LEETCODE_SOLUTION — 384. Shuffle an Array

## Problem
Shuffle a set of numbers without duplicates.

## Annotation Approach
Use a `@Shuffle` annotation to mark methods as shuffling strategies.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Shuffle {
    String name();
}

public class Solution {
    private int[] original;

    public Solution(int[] nums) { original = nums.clone(); }

    @Shuffle(name = "fisherYates")
    public int[] shuffle() {
        int[] a = original.clone();
        Random r = new Random();
        for (int i = a.length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return a;
    }

    public int[] reset() { return original; }
}
```

## Key Insight
Annotations document intent. A test runner could discover all `@Shuffle` strategies.

## Complexity
- Time: O(n)
- Space: O(n)
