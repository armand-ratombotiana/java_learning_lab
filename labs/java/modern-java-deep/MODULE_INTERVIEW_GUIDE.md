# Modern Java Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "How do virtual threads work under the hood? Explain mounting, unmounting, and pinning."
- "Compare records vs Lombok's @Data. When would you choose one over the other?"
- "How does pattern matching in switch compile? What invokedynamic tricks are used?"

### Amazon
- "How would you use structured concurrency to simplify a microservice orchestration?"
- "Records are great for DTOs — but what are the memory tradeoffs vs POJOs?"
- "How does sealed classes improve domain modeling in a payment processing system?"

### Meta
- "How would you migrate a large codebase from Java 11 to 21? What are the breaking changes?"
- "Compare Optional.orElse() vs orElseGet(). When does the argument get evaluated?"
- "How does text blocks improve code quality for multi-line SQL/JSON?"

### Apple
- "How do records reduce memory footprint compared to classes? What about the accessor methods?"
- "How does pattern matching for switch help with enum-heavy code like state machines?"
- "Explain how ScopedValue is more memory-efficient than ThreadLocal."

### Oracle
- "Walk through the entire evolution from Java 8 to 21. What were the key JEPs?"
- "What preview features are in Java 21? Which ones are expected to become permanent?"
- "Explain the JEP process. How does a feature go from JEP draft to final?"

## LeetCode Problems

| Problem | Modern Java Feature |
|---------|-------------------|
| 1 Two Sum | Record for result: `record Pair(int index1, int index2) {}` |
| 49 Group Anagrams | Stream API grouping with pattern matching |
| 139 Word Break | Sealed interface for DP states |
| 208 Implement Trie | Record for TrieNode |
| 297 Serialize Binary Tree | Pattern matching for node types |
| 394 Decode String | Switch expression with pattern matching |
| 438 Find All Anagrams | Sealed class for character matching strategies |
| 678 Valid Parenthesis String | Pattern matching for runtime enum state |

## FAANG Interview Stories

**Story 1: Google — Virtual Threads Deep Dive**
> *"I was asked about virtual thread pinning. I explained that synchronized blocks cause pinning. The interviewer asked: 'How would you find pinning in production?' I said: JFR events 'jdk.VirtualThreadPinned'. They asked: 'What's the performance impact of pinning?' I explained that the carrier thread is blocked, reducing parallelism."* — L5 SWE, Google

**Story 2: Amazon — Sealed Classes for Payment Domain**
> *"We modeled a payment system. The interviewer asked me to use sealed classes for payment methods: CreditCard, PayPal, Crypto. Then pattern matching for fee calculation. The sealed class ensured that adding a new payment method required updating all switch statements — compiler-enforced domain coverage."* — SDE III, Amazon

**Story 3: Meta — Migration from Java 8 to 17**
> *"The migration uncovered hidden issues. Records changed equals/hashCode behavior. The new switch expressions exposed fall-through bugs. Module system broke some internal JARs. The lesson: migration is not 'just compile with new JDK' — test everything."* — Software Engineer, Meta

## Senior vs Staff Deep Dive

### Senior-Level
- "Compare virtual threads with platform threads. When would you still choose platform threads?"
- "How do records interact with JPA/Hibernate? What are the limitations?"
- "Explain sealed classes vs enums. When do you need both?"

### Staff-Level
- "Design a virtual thread scheduler. How does it compare to the built-in ForkJoinPool scheduler?"
- "How would you implement pattern matching for switch at the JVM level? What invokedynamic support is needed?"
- "Evaluate records with Wrapper types vs primitive classes (Valhalla) — how would Project Valhalla change records?"
- "Design a structured concurrency API for a distributed system with multi-datacenter orchestration."

## System Design Connections

| System | Modern Java Feature |
|--------|-------------------|
| Payment engine | Sealed classes for payment types, pattern matching for processing |
| Event store | Records for immutable events, sealed classes for event types |
| API layer | Records for DTOs, pattern matching for response handling |
| Microservice orchestrator | Structured concurrency, virtual threads |
| State machine | Sealed classes for states, pattern matching for transitions |

## Code Review Scenarios

**Scenario 1**: Switch that doesn't cover all cases.
```java
// Old way — runtime error if enum grows
switch (paymentType) {
    case CREDIT_CARD: ...
    case PAYPAL: ...
    default: throw new RuntimeException();
}
// New way — compiler error if new enum added
return switch (paymentType) {
    case CREDIT_CARD -> processCredit();
    case PAYPAL -> processPayPal();
    case CRYPTO -> processCrypto();
};
```

**Scenario 2**: Optional.orElse() called with expensive computation.
```java
// Bad: methods are evaluated even if Optional is present
return opt.orElse(expensiveDefault());
// Good: lazy evaluation
return opt.orElseGet(() -> expensiveDefault());
```

**Scenario 3**: Using records with JPA.
```java
// Issue: JPA requires no-arg constructor, mutable fields
record User(String name, int age) { }  // Can't use with JPA
// Fix: Use class for entities, records for DTOs only
```

## Debugging Scenarios

**Scenario 1**: Virtual thread stuck (pinned).
- Detect: JFR event `jdk.VirtualThreadPinned`
- Cause: synchronized block or native method inside virtual thread
- Fix: Replace synchronized with ReentrantLock, avoid native calls

**Scenario 2**: Pattern matching exhaustive check fails.
- Root cause: New enum constant added to sealed hierarchy, but switch not updated
- Fix: Compiler error in Java 21 — ensure all builds catch it
- Detection: `-Xlint:all` or enable preview features

**Scenario 3**: Record serialization incompatibility.
- Issue: Records use `RecordCodec` for serialization, not standard Serialization
- Cause: Migrating from Serializable POJO to Record breaks backward compat
- Fix: Add `serialVersionUID` explicitly to records, test serialization compat
