# Generics — References

## Official Documentation

- [Java Generics Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/java/generics/)
- [Java Language Specification — Chapter 4: Types, Values, and Variables](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html)
- [Java Language Specification — Generics](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.1.2)

## Books

- Bloch, Joshua. *Effective Java* (3rd ed.). Addison-Wesley, 2018.
  - Item 26: Don't use raw types
  - Item 27: Eliminate unchecked warnings
  - Item 28: Prefer lists to arrays
  - Item 29: Favor generic types
  - Item 30: Favor generic methods
  - Item 31: Use bounded wildcards to increase API flexibility
  - Item 32: Combine generics and varargs judiciously
  - Item 33: Consider typesafe heterogeneous containers

- Naftalin, Maurice and Philip Wadler. *Java Generics and Collections*. O'Reilly, 2006.
  - Comprehensive coverage of generics design and usage

## Articles

- Bracha, Gilad. "Generics in the Java Programming Language." 2004.
- "PECS: Producer Extends Consumer Super" — Stack Overflow (highly voted explanation)

## Videos

- "Java Generics Tutorial" — Jakob Jenkov (YouTube)
- "Java Generics: Past, Present, Future" — Brian Goetz (various conferences)

## Open Source Examples

- [Spring Framework](https://github.com/spring-projects/spring-framework) — extensive use of generics in `JpaRepository`, `RestTemplate`, type-safe converters
- [Google Guava](https://github.com/google/guava) — `Optional<T>`, `Supplier<T>`, `Function<F, T>`
- [Project Lombok](https://projectlombok.org/) — `val` and `var` with generic type inference

## Related Labs

- [12-collections](../12-collections/) — built on generics
- [14-lambdas](../14-lambdas/) — functional interfaces are generic
- [19-annotations](../19-annotations/) — `@SuppressWarnings("unchecked")`
- [20-reflection](../20-reflection/) — `ParameterizedType`, `TypeVariable`
