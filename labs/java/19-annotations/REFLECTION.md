# Reflection — Annotations

## Why This Lab Matters
Annotations are the backbone of modern Java frameworks — understanding them is essential for working with Spring, JPA, and more.

## What I Learned
- How to define and use custom annotations
- Retention policies and their impact
- Runtime annotation processing via reflection
- Compile-time annotation processing

## Questions I Still Have
- How do annotation processors integrate with incremental compilation?
- Will records and sealed classes affect how annotations are used?

## Personal Application
- Create domain-specific annotations (e.g., `@Auditable`, `@Cached`)
- Replace marker interfaces with annotations
- Write annotation processors for validation code generation

## Key Takeaways
1. `RUNTIME` retention is needed for reflection-based processing
2. Compile-time processing is faster than runtime
3. Annotations are no substitute for type safety
4. `@Inherited` only works for class-level annotations
5. Annotations reduce boilerplate when used with processors
