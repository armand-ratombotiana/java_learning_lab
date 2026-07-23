# Mock Interview Transcript: Java Syntax

## Interviewer: Senior SWE, Google
## Candidate: Mid-level Java developer
## Time: 45 minutes
## Focus: Java syntax fundamentals, compiler behavior, JVM compilation

---

**Q1: What is the difference between `int x = 5` and `Integer x = 5`?**

**Candidate**: The first stores the value 5 directly in the stack as a primitive. The second creates an Integer object on the heap, and autoboxing converts the primitive 5 to an Integer object.

**Interviewer**: Good. What about `Integer x = 5` — does it always create a new object?

**Candidate**: No, actually. Java caches Integer objects between -128 and 127. So `Integer x = 5` and `Integer y = 5` will return the same object. But `Integer x = 200` and `Integer y = 200` will be different objects.

**Interviewer**: Right. So what does this print?

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);
```

**Candidate**: That prints `true` because both refer to the same cached object.

**Interviewer**: And this?

```java
Integer a = 200;
Integer b = 200;
System.out.println(a == b);
```

**Candidate**: That prints `false`. The cache only covers -128 to 127, so 200 is outside that range and each autobox creates a new object.

**Interviewer**: Good. How could you make it print `true`?

**Candidate**: You could change the JVM flag `-XX:AutoBoxCacheMax=200` to extend the cache range. Or use `.equals()` instead of `==`.

**Interviewer**: Let's talk about strings. What's happening here?

```java
String s1 = "hello";
String s2 = "hello";
String s3 = new String("hello");
System.out.println(s1 == s2);
System.out.println(s1 == s3);
```

**Candidate**: `s1 == s2` is `true` because string literals are interned — they point to the same object in the string pool. `s1 == s3` is `false` because `new String("hello")` creates a new object on the heap, not from the pool.

**Interviewer**: What about `s1.equals(s3)`?

**Candidate**: That's `true`. `equals()` compares the character content, not reference identity.

**Interviewer**: A method has `String s = "hello" + name;`. What bytecode does the compiler generate?

**Candidate**: The compiler converts that to a StringBuilder chain:
```java
StringBuilder sb = new StringBuilder();
sb.append("hello");
sb.append(name);
String s = sb.toString();
```
The bytecode would show: `NEW StringBuilder`, `INVOKESPECIAL StringBuilder.<init>`, `LDC "hello"`, `INVOKEVIRTUAL StringBuilder.append`, `ALOAD name`, `INVOKEVIRTUAL StringBuilder.append`, `INVOKEVIRTUAL StringBuilder.toString`.

**Interviewer**: What if it's in a loop?

**Candidate**: In a loop, the compiler does NOT optimize to a single StringBuilder. Each iteration creates a new StringBuilder. You should manually use a StringBuilder declared outside the loop.

**Interviewer**: Exactly. Let's test that understanding:

```java
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;
}
```

What's the time complexity of this?

**Candidate**: It's O(n²). Each `+=` creates a new String object (and a StringBuilder internally), copies the old contents, and appends the new value. With 1000 iterations, we're doing roughly 500K character copies. Using StringBuilder would give O(n).

**Interviewer**: Right. So what's the best way to avoid this?

**Candidate**: Use `StringBuilder sb = new StringBuilder(estimatedSize)` outside the loop, then `sb.append(i)` inside, and `sb.toString()` after.

**Interviewer**: Let's talk about the `switch` statement. What types can switch on in Java?

**Candidate**: In Java 7+, switch works with `int`, `char`, `byte`, `short`, their wrappers, `String`, and `enum`. For primitive types, it compiles to a `tableswitch` or `lookupswitch` bytecode instruction. For String, it compiles to a hash-based lookup then equals check.

**Interviewer**: And in Java 21?

**Candidate**: In Java 21, switch has been significantly enhanced. It can now work with any type through pattern matching. You can use `switch` as an expression, match null, match types, and destructure records. The compiler ensures exhaustiveness for sealed types and enums.

**Interviewer**: Let's get practical then. Write a method that uses a switch expression to return a description of a day:

**Candidate**: 
```java
String describe(DayOfWeek day) {
    return switch (day) {
        case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Work day";
        case SATURDAY, SUNDAY -> "Weekend";
    };
}
```

**Interviewer**: Good. But what if someone passes `null`?

**Candidate**: In Java 17+, the switch expression would throw NullPointerException because it doesn't handle null by default. In Java 21, we can add a null case:
```java
return switch (day) {
    case null -> "No day";
    case MONDAY...FRIDAY -> "Work day";
    case SATURDAY, SUNDAY -> "Weekend";
};
```

**Interviewer**: Perfect. Let's wrap up with one JVM question. What does the `javap -c` command show?

**Candidate**: `javap -c` disassembles the bytecode of a compiled Java class. It shows the JVM instructions: pushes, pops, method calls, branches. It's useful for understanding what the compiler actually generates versus what you wrote in source.

**Interviewer**: And what would you look for if you wanted to check if a method is too large for JIT inlining?

**Candidate**: The default `MaxInlineSize` is 35 bytes of bytecode. I'd use `javap -c` to check the bytecode size, and if it's over 35 bytes, the JIT won't inline it by default. For hot methods over 35 bytes, the `FreqInlineSize` parameter (default 325 bytes) applies.

**Interviewer**: Good answer. That's the end of the interview.

---

## Feedback

**Strengths**:
- Solid understanding of autoboxing and caching
- Clear explanation of String interning and StringBuilder optimization
- Knows modern Java features (pattern matching switch, null handling)
- JVM/bytecode knowledge for language features

**Areas for Improvement**:
- Could have mentioned `javac` constant folding for compile-time constants
- Didn't mention `StringConcatFactory` (Java 9+) which uses `invokedynamic` for string concatenation

**Score**: 4/5 — Strong fundamentals, ready for follow-up rounds
