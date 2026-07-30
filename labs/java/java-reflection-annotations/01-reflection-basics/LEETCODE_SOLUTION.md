# LEETCODE_SOLUTION — 341. Flatten Nested List Iterator

## Problem
Given a nested list of integers, implement an iterator to flatten it.

## Reflection Approach
Use reflection to inspect the internal `List` field of `NestedInteger`.

```java
public class NestedIterator implements Iterator<Integer> {
    private final List<Integer> flat;
    private int index;

    public NestedIterator(List<NestedInteger> nestedList) {
        flat = flatten(nestedList);
        index = 0;
    }

    private List<Integer> flatten(List<NestedInteger> list) {
        List<Integer> result = new ArrayList<>();
        for (NestedInteger ni : list) {
            if (ni.isInteger()) {
                result.add(ni.getInteger());
            } else {
                result.addAll(flatten(ni.getList()));
            }
        }
        return result;
    }

    @Override public Integer next() { return flat.get(index++); }
    @Override public boolean hasNext() { return index < flat.size(); }
}
```

## Key Insight
Reflection can unwrap internal structures but is overkill here — recursion is cleaner.

## Complexity
- Time: O(n)
- Space: O(n)
