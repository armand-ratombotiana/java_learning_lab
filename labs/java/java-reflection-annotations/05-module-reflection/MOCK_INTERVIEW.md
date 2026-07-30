# MOCK_INTERVIEW — Module Reflection

## Scenario
Your team needs to call a method on a class inside a library module that does NOT export the relevant package. How do you proceed?

## Interviewer Notes
- Candidate should explain the problem (module system encapsulation)
- Solution: `--add-opens` or `--add-exports` JVM flags
- Discuss alternatives: asking the library maintainer, using `unsafe` (not recommended)

## Expected Solution Sketch
```java
// JVM args: --add-opens com.example.lib/com.example.lib.internal=ALL-UNNAMED
Module libModule = InternalClass.class.getModule();
// Now we can call setAccessible and reflect
```

## Follow‑Up
- How does `addOpens` differ for named vs unnamed modules?
- Can a module grant access to specific modules only?
