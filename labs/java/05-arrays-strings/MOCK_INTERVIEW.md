# Mock Interview Transcript: Arrays & Strings

## Interviewer: Staff Engineer, Apple
## Candidate: Mid-level Java developer
## Time: 40 minutes
## Focus: Array internals, String immutability, memory efficiency

---

**Q1: What's the memory layout of a String object in Java 21?**

**Candidate**: A String object in modern Java (9+) contains: a byte array (`byte[] value`), a byte coder (either LATIN1=0 or UTF16=1), and an int hash (cached hashCode). The `char[]` was replaced with `byte[]` in Java 9 (compact strings) to save memory for Latin-1 strings. The object header has a mark word and klass pointer.

**Interviewer**: How much memory does `new String("hello")"` take (assuming compact strings, 64-bit with compressed OOPs)?

**Candidate**: String object: 12 bytes header + 4 bytes `value` reference + 1 byte `coder` + 4 bytes `hash` + 3 bytes padding = 24 bytes. The internal byte[]: 16 bytes header + 5 bytes data + 3 padding = 24 bytes. Total: 48 bytes for a 5-character string.

**Interviewer**: Good. How would you optimize memory for 1 million unique short strings?

**Candidate**: Several strategies: (1) Use `String.intern()` for canonicalization — but weak references in the string pool can cause GC overhead. (2) Use a custom pool with `WeakHashMap<String, String>`. (3) Store as `byte[]` manually if you control the encoding. (4) Consider `-XX:+UseStringDeduplication` G1 flag which deduplicates backing char/byte arrays during GC.

**Interviewer**: Let's test with a problem. Find the longest palindrome substring efficiently.

**Candidate**: I'll implement the expand-around-center approach:
```java
String longestPalindrome(String s) {
    if (s == null || s.length() < 2) return s;
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        int len1 = expand(s, i, i);      // odd
        int len2 = expand(s, i, i + 1);  // even
        int len = Math.max(len1, len2);
        if (len > end - start) {
            start = i - (len - 1) / 2;
            end = i + len / 2;
        }
    }
    return s.substring(start, end + 1);
}

int expand(String s, int l, int r) {
    while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
        l--; r++;
    }
    return r - l - 1;
}
```

**Interviewer**: What's the time and space complexity?

**Candidate**: O(n²) time, O(1) space. Each expansion takes O(n) in worst case, and there are 2n centers. The Manacher algorithm gives O(n) time but is more complex.

**Interviewer**: The interviewer asks for O(n). Can you implement Manacher?

**Candidate**: Manacher's algorithm uses a palindrome radius array and symmetry. The key insight: if you know the palindrome centered at a position, you can use symmetry to skip expansions for positions within that palindrome. Let me implement it.

```java
String manacher(String s) {
    char[] t = new char[s.length() * 2 + 3];
    t[0] = '^'; t[1] = '#';
    for (int i = 0; i < s.length(); i++) {
        t[i * 2 + 2] = s.charAt(i);
        t[i * 2 + 3] = '#';
    }
    t[t.length - 1] = '$';
    
    int[] p = new int[t.length];
    int c = 0, r = 0;
    for (int i = 1; i < t.length - 1; i++) {
        int mir = 2 * c - i;
        p[i] = r > i ? Math.min(p[mir], r - i) : 0;
        while (t[i + p[i] + 1] == t[i - p[i] - 1]) p[i]++;
        if (i + p[i] > r) { c = i; r = i + p[i]; }
    }
    
    int maxLen = 0, center = 0;
    for (int i = 0; i < p.length; i++) {
        if (p[i] > maxLen) { maxLen = p[i]; center = i; }
    }
    int start = (center - maxLen) / 2;
    return s.substring(start, start + maxLen);
}
```

**Interviewer**: And the string builder — what does `s.substring()` do in Java 7+ vs Java 6?

**Candidate**: In Java 6, `substring()` shared the original `char[]` to avoid copying — but this could cause memory leaks if a small substring referenced a huge string. In Java 7+, `substring()` copies the data into a new char/byte array, so there's no memory leak but it costs O(n) time.

**Interviewer**: Good. That's the key knowledge for String handling.

---

## Feedback

**Strengths**:
- Deep understanding of String memory layout (compact strings, coder field)
- Knows Java 9+ compact strings optimization
- Implements both O(n²) and O(n) palindrome algorithms
- Knows substring() history and memory implications

**Areas for Improvement**:
- Initial answer could have mentioned String's `hash32` field for hash-based collections
- Could mention that Manacher's algorithm is rarely needed in practice (but good for interview)

**Score**: 4.5/5 — Excellent string and memory knowledge
