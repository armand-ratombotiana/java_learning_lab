# Mock Interview Transcript: Control Flow

## Interviewer: Senior SWE, Meta
## Candidate: Entry-level Java developer
## Time: 30 minutes
## Focus: Loops, conditionals, branching, optimization

---

**Q1: Write a method that returns the first non-repeating character in a string.**

**Candidate**: 
```java
char firstNonRepeating(String s) {
    int[] count = new int[26];
    for (char c : s.toCharArray()) count[c - 'a']++;
    for (char c : s.toCharArray()) {
        if (count[c - 'a'] == 1) return c;
    }
    return ' ';
}
```

**Interviewer**: What if the string contains uppercase or non-alphabetic characters?

**Candidate**: I'd use a HashMap instead:
```java
char firstNonRepeating(String s) {
    Map<Character, Integer> count = new LinkedHashMap<>();
    for (char c : s.toCharArray()) count.merge(c, 1, Integer::sum);
    for (var e : count.entrySet()) {
        if (e.getValue() == 1) return e.getKey();
    }
    return ' ';
}
```

**Interviewer**: Why `LinkedHashMap` instead of `HashMap`?

**Candidate**: `LinkedHashMap` maintains insertion order, so the first entry with count 1 will be the first non-repeating character. With `HashMap`, the iteration order is unpredictable.

**Interviewer**: Good. Now, let's talk about `for-each` loops. Does this work?

```java
for (int i = 0; i < list.size(); i++) {
    list.remove(i);
}
```

**Candidate**: This works functionally but is bug-prone. After removing at index i, the elements shift left. If you remove index 0, then 1, then 2, you'll skip elements. It's better to iterate backward or use `Iterator.remove()`.

**Interviewer**: Show me the correct way.

**Candidate**: 
```java
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    it.next();
    it.remove();
}
```
Or with enhanced for-each... actually, that will throw `ConcurrentModificationException`.

**Interviewer**: Why does the for-each throw CME but the indexed loop doesn't?

**Candidate**: For-each uses an Iterator internally. The Iterator checks `modCount` against `expectedModCount` on each `next()` call. List.remove() increments `modCount` but doesn't update the Iterator's `expectedModCount`, so the next check fails. The indexed loop doesn't use an Iterator at all.

**Interviewer**: Good. Now, what's the `switch` bytecode for dense vs sparse values? Let's consider:

```java
switch(x) {
    case 1: break;
    case 2: break;
    case 3: break;
}
```

**Candidate**: For dense values (1, 2, 3), the compiler generates a `tableswitch` — an O(1) jump table. For sparse values like `case 1, case 100, case 1000`, it generates `lookupswitch` — a sorted key-value table with O(log n) binary search.

**Interviewer**: What would change if we used `String` in the switch?

**Candidate**: The compiler generates code that computes the String's hashCode and uses a `tableswitch` or `lookupswitch` on the hash, then does `.equals()` for collision check. This is more expensive than an int switch.

**Interviewer**: One last question. How does the `?:` ternary operator compare to `if-else` in terms of bytecode?

**Candidate**: The ternary operator compiles to the same bytecode as an equivalent `if-else` with same return type. There's no performance difference. The choice is purely stylistic — ternary is more concise for simple assignments; if-else is clearer for complex logic.

---

## Feedback

**Strengths**:
- Good problem-solving approach
- Knows collection iteration mechanics and pitfalls
- Understands bytecode generation for control flow

**Areas for Improvement**:
- Initial solution didn't handle edge cases (uppercase, non-alpha)
- Should have mentioned `IntStream.range()` as alternative

**Score**: 3.5/5 — Solid for entry-level
