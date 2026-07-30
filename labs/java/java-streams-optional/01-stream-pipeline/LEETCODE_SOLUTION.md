# LEETCODE_SOLUTION — 125. Valid Palindrome

## Problem
Determine if a string is a palindrome, alphanumeric only, ignoring case.

## Stream Solution
```java
class Solution {
    public boolean isPalindrome(String s) {
        String cleaned = s.chars()
            .filter(Character::isLetterOrDigit)
            .map(Character::toLowerCase)
            .collect(StringBuilder::new,
                     StringBuilder::appendCodePoint,
                     StringBuilder::append)
            .toString();
        return cleaned.contentEquals(new StringBuilder(cleaned).reverse());
    }
}
```

## Key Insight
Streams make the cleaning step declarative: filter + map + collect.

## Complexity
- Time: O(n)
- Space: O(n)
