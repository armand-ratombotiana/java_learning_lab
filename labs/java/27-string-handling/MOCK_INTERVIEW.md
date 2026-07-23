# Mock Interview Transcript: String Handling

## Interviewer: Staff Engineer, Apple
## Candidate: Senior Java developer
## Time: 35 minutes
## Focus: String immutability, compact strings, performance, memory

---

**Q1: Why is String immutable in Java?**

**Candidate**: (1) Security — sensitive strings (passwords, file paths) can't be tampered with. (2) Caching — string pool works because nobody changes strings. (3) Thread safety — immutable objects are inherently thread-safe. (4) Class loading — strings are used to load classes (class names). (5) HashCode — can be cached (String caches hash after first computation).

**Interviewer**: How are strings stored in memory? Compare Java 8 and Java 9+.

**Candidate**: In Java 8, String stores a `char[]` — each char is 2 bytes. In Java 9+ (compact strings), String stores a `byte[]` and a `coder` byte (LATIN1=0, UTF16=1). If all characters fit in Latin-1, each char uses 1 byte. This saves ~50% memory for most strings (which are typically Latin-1).

**Interviewer**: What happens with `String.intern()` at the JVM level?

**Candidate**: `intern()` returns a canonical representation. If the string pool already contains an equal string, that pooled reference is returned. Otherwise, the string is added to the pool. The pool is stored in the heap (Java 7+) or `StringTable` (a HashMap in the JVM). `-XX:StringTableSize` controls table size. Too many interns can cause memory pressure.

**Interviewer**: Write a method that checks if two strings are anagrams.

**Candidate**: 
```java
boolean isAnagram(String s1, String s2) {
    if (s1.length() != s2.length()) return false;
    int[] count = new int[26];
    for (char c : s1.toCharArray()) count[c - 'a']++;
    for (char c : s2.toCharArray()) count[c - 'a']--;
    for (int c : count) if (c != 0) return false;
    return true;
}
```

**Interviewer**: How about Unicode support?

**Candidate**: The array-of-26 approach only works for lowercase ASCII. For full Unicode, use a HashMap: `s.codePoints().forEach(cp -> map.merge(cp, 1, Integer::sum))`. `codePoints()` handles supplementary characters (surrogate pairs) correctly, while `charAt()` doesn't.

**Interviewer**: Explain `StringBuilder` vs `StringBuffer` vs String concatenation.

**Candidate**: `StringBuilder` (non-synchronized, faster) for single-threaded use. `StringBuffer` (synchronized methods) for multi-threaded — but rarely needed since each buffering is typically thread-local. String concatenation with `+` is fine for 2-3 operands (compiler uses StringBuilder). In loops, always use StringBuilder explicitly.

**Interviewer**: How does the compiler optimize string concatenation in Java 9+?

**Candidate**: In Java 9+, string concatenation uses `invokedynamic` with `StringConcatFactory`, replacing StringBuilder. The bootstrap method selects the optimal strategy: (1) Simple concatenation — inline bytecode. (2) Complex — method handle combinator. This allows runtime optimization without recompilation. In Java 8, StringBuilder was the only mechanism.

**Interviewer**: Compare `String.split()` vs `Pattern.split()` for repeated calls.

**Candidate**: `String.split()` compiles the regex each call. For repeated splitting with the same pattern, pre-compile: `Pattern.compile(",").split(input)`. Also, `String.split()` creates an array of substrings; `Pattern.splitAsStream()` creates a lazy stream for large inputs.

**Interviewer**: What is string deduplication (G1)?

**Candidate**: `-XX:+UseStringDuplication` (G1 only) deduplicates String backing `byte[]` arrays during GC. If two strings have identical content, their `byte[]` arrays are merged (one array is shared). This reduces heap usage by 10-30% in many applications. The GC checks are concurrent and low-overhead.

---

## Feedback

**Strengths**:
- Deep understanding of String immutability rationale
- Knows compact strings (Java 9+ memory optimization)
- Correct Unicode handling with codePoints()
- Knows invokedynamic concatenation (Java 9+)

**Areas for Improvement**:
- Could discuss `TextBlock` (Java 13+) for multi-line strings
- Might mention `strip()` vs `trim()` (Unicode-aware)

**Score**: 4.5/5 — Excellent string internals knowledge
