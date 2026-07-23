# Mock Interview Transcript: Data Types

## Interviewer: Staff Engineer, Amazon
## Candidate: Junior Java developer
## Time: 30 minutes
## Focus: Primitives, wrappers, type conversion, memory

---

**Q1: How many bytes does an `int` take in Java?**

**Candidate**: 4 bytes, always. Unlike C, Java guarantees the size regardless of platform.

**Interviewer**: How about a `boolean`?

**Candidate**: The JVM specification doesn't define a precise size for `boolean`. In practice, HotSpot uses 1 byte for standalone booleans but may use 4 bytes in arrays for alignment. The JVM represents booleans as `int` values internally — 0 for false, 1 for true.

**Interviewer**: Interesting. What about memory layout — compare `int[]` and `Integer[]` for 1000 elements.

**Candidate**: `int[1000]` uses 4000 bytes (4 bytes per element) plus ~24 bytes for the array object header. `Integer[1000]` uses 8000 bytes for the references (8 bytes each on 64-bit with compressed OOPs) plus ~24 bytes for the header, AND each Integer object takes 16 bytes (header + int field). So `Integer[1000]` could use 24,000+ bytes — about 6x more than int[].

**Interviewer**: Good memory awareness. What does this print?

```java
double d = 0.1 + 0.2;
System.out.println(d == 0.3);
```

**Candidate**: It prints `false`. 0.1 and 0.2 can't be represented exactly in binary floating-point. The result is approximately 0.30000000000000004. You should compare with a tolerance: `Math.abs(d - 0.3) < 0.0001`.

**Interviewer**: How does `BigDecimal` solve this?

**Candidate**: `BigDecimal` uses a `BigInteger` unscaled value plus a scale factor (number of decimal places). So `0.1` is stored as `1` with scale `1`. Arithmetic is exact as long as you use the `String` constructor — `new BigDecimal("0.1")` not `new BigDecimal(0.1)`.

**Interviewer**: Why does `new BigDecimal(0.1)` still have the problem?

**Candidate**: Because `0.1` as a double is already approximated when passed to the constructor. The constructor converts the imprecise double value to BigDecimal. Always use the String constructor for exact representation.

**Interviewer**: Let's test type conversion. What's the result of `(int) 3.9`?

**Candidate**: `3`. Casting double to int truncates toward zero.

**Interviewer**: And `Math.round(3.9)`?

**Candidate**: `4L`. Math.round returns a long for double input, rounding to the nearest integer with ties rounding up.

**Interviewer**: What about overflow: `Integer.MAX_VALUE + 1`?

**Candidate**: That wraps to `Integer.MIN_VALUE` (-2147483648). Java integer arithmetic silently overflows. You can use `Math.addExact(x, y)` to get an exception on overflow, or `long` for larger ranges.

**Interviewer**: Good. One more: convert `"42"` to an int without using Integer.parseInt().

**Candidate**: 
```java
int toInt(String s) {
    int result = 0;
    for (int i = 0; i < s.length(); i++) {
        result = result * 10 + (s.charAt(i) - '0');
    }
    return result;
}
```

**Interviewer**: What about negative numbers?

**Candidate**: I'd need to check the first character for '-':
```java
int toInt(String s) {
    int result = 0, i = 0;
    boolean neg = s.charAt(0) == '-';
    if (neg) i = 1;
    for (; i < s.length(); i++) {
        result = result * 10 + (s.charAt(i) - '0');
    }
    return neg ? -result : result;
}
```

**Interviewer**: Close. What about overflow during parsing? What if the string is "9999999999999"?

**Candidate**: That would silently overflow. A robust implementation should check `if (result > (Integer.MAX_VALUE - digit) / 10)` before each multiplication to detect overflow.

---

## Feedback

**Strengths**:
- Knows primitive sizes and JVM representation
- Understands floating-point imprecision and BigDecimal
- Can implement utility functions from scratch

**Areas for Improvement**:
- Missed overflow detection in string-to-int
- Didn't mention `Integer.valueOf()` caching consequences for type conversion

**Score**: 3.5/5 — Good fundamentals, needs more production awareness
