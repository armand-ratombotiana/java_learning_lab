# LEETCODE_SOLUTION — 136. Single Number

## Problem
Find the unique element in an array where every other element appears twice.

## Functional Solution
```java
class Solution {
    public int singleNumber(int[] nums) {
        return Arrays.stream(nums)
            .reduce(0, (a, b) -> a ^ b);
    }
}
```

## Currying Context
A curried function can build specialized reduction strategies:
```java
// Curried reducer
Function<Integer, IntBinaryOperator> reducer = init -> (a, b) -> a ^ b;
int result = Arrays.stream(nums).reduce(0, reducer.apply(0));
```

## Complexity
- Time: O(n)
- Space: O(1)
