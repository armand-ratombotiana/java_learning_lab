# References: Optional

## Official Documentation

- **Optional<T> API** - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html
- **OptionalInt, OptionalLong, OptionalDouble** - Primitive Optional specializations
- **Java 8 Release Notes** - Lambda and Stream API documentation
- **JEP 186: Collection Library Enhancements** - Context for Optional's introduction

## Books

- *Java 8 in Action* by Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft — Optional overview
- *Modern Java in Action* by Raoul-Gabriel Urma — Updated Optional guidance
- *Functional Programming in Java* by Pierre-Yves Saumont — Monads and Optional
- *Effective Java* (3rd Edition) by Joshua Bloch — Item 55: Return Optionals Judiciously

## Articles

- **"Optional in Java: A Comprehensive Guide"** by Baeldung
- **"Uses for Optional"** by Brian Goetz — Original design rationale
- **"Optional: The Mother of All Anti-Patterns"** — Critical perspective
- **"Tired of Null Pointer Exceptions? Use Optional"** by Oracle
- **"Java 8 Optional: How to Use It"** by Stuart Marks
- **"Optional: A Monad in Java"** — Functional programming perspective

## Conference Talks

- **"Optional: The Good, The Bad, and The Ugly"** by Stuart Marks — Devoxx
- **"Java Optional API: Best Practices"** by Stephen Colebourne
- **"Monads in Java: Optional and Beyond"** by Mario Fusco

## Online Resources

- **Baeldung: Guide to Java Optional** - https://www.baeldung.com/java-optional
- **Oracle Optional Tutorial** - https://docs.oracle.com/javase/tutorial/java/javaOO/optional.html
- **Dev.java: Optional** - Oracle interactive tutorials
- **Stack Overflow** — [java-optional] tag

## Related Java Features

- **NullPointerException** — The problem Optional solves
- **Objects.requireNonNull()** — Null validation utility
- **Stream API** — findFirst, findAny return Optional
- **CompletableFuture** — Monadic container for async results
- **@Nullable annotations** — Complementary compile-time null checking

## Third-Party Libraries

- **Google Guava** — com.google.common.base.Optional (Java 7 compatible)
- **Vavr** — io.vavr.control.Option (full monad with pattern matching)
- **Functional Java** — fj.data.Option (early functional Optional)
- **Lombok** — @NonNull annotation for null checking

## Tools

- **IntelliJ IDEA** — Optional inspections: get() without isPresent(), Optional field warnings
- **Eclipse** — Null analysis integration
- **FindBugs/SpotBugs** — NP_NONNULL_RETURN_VIOLATION for Optional null returns
- **Checkstyle** — Optional usage rules
- **SonarQube** — Code quality rules for Optional misuse

## Academic References

- *"Monads for Functional Programming"* by Philip Wadler — The theoretical foundation
- *"The Maybe Monad in Java"* — Academic paper on Optional implementation
- *"Null References: The Billion Dollar Mistake"* by Tony Hoare — The original presentation

## Community Resources

- **Stack Overflow** — [java-optional] tag for common questions
- **r/java** on Reddit — Optional discussions and patterns
- **Java Discord** — #java-8-and-beyond channel
- **OpenJDK core-libs-dev mailing list** — Design discussions
