# Reflection — Reflection

## Why This Lab Matters
Reflection is the foundation of frameworks, tools, and dynamic programming in Java. Understanding it demystifies how Spring, Hibernate, JUnit, and Mockito work.

## What I Learned
- Class loading and `Class<?>` objects
- Inspecting methods, fields, and constructors
- Dynamic invocation and access control
- Dynamic proxies and method handles
- Module system restrictions (Java 9+)

## Questions I Still Have
- Will `MethodHandle` eventually replace reflection for framework internals?
- How does GraalVM's SubstrateVM handle reflection in native images?

## Personal Application
- Use dynamic proxies for cross-cutting concerns (logging, metrics)
- Write annotation processors instead of runtime reflection when possible
- Cache reflective objects for performance

## Key Takeaways
1. Reflection is powerful but slow — cache lookups
2. `setAccessible` bypasses encapsulation — use responsibly
3. Dynamic proxies enable AOP without bytecode manipulation
4. Java 9+ modules restrict reflection — know your `--add-opens`
5. `MethodHandle` is a faster alternative for hot paths
