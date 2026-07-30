# LEETCODE_SOLUTION — 49. Group Anagrams

## Problem
Group a list of strings into anagrams.

## Collectors Solution
```java
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        return new ArrayList<>(Arrays.stream(strs)
            .collect(Collectors.groupingBy(s -> {
                char[] c = s.toCharArray();
                Arrays.sort(c);
                return new String(c);
            }))
            .values());
    }
}
```

## Key Insight
`groupingBy()` classifies each string by its sorted character array.

## Complexity
- Time: O(n * k log k) where k is max string length
- Space: O(n)
