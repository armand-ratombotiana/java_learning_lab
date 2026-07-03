# Polymorphism — Evolution Across Java Versions

## Java 1.0 (1996)

Dynamic method dispatch: all non-static methods are virtual. Overloading resolved at compile time. Overriding resolved at runtime via vtable. Interface polymorphism — any interface can have multiple implementations. No generics — polymorphic containers required casts.

## Java 5 (2004)

- **Covariant return types**: `@Override` method can return subtype — more flexible polymorphism
- **Generics**: `List<T>` — parametric polymorphism. Type-safe containers without casts.
- **For-each loop**: Polymorphic iteration over arrays and Iterables
- **Autoboxing**: Polymorphic numeric operations across primitives and wrappers

## Java 8 (2014)

- **Default methods**: Interface evolution without breaking implementations — retroactive polymorphism
- **Lambdas**: Functional interface polymorphism — single abstract method with multiple lambda implementations
- **Method references**: `ClassName::methodName` — compact polymorphic method binding
- **Stream API**: Polymorphic data processing pipeline

## Java 14 (2020)

- **Pattern matching for instanceof**: `if (obj instanceof String s)` — polymorphic type checking with binding
- **Switch expressions (standard)**: Polymorphic branching with values

## Java 16 (2021)

- **Records** (standard): Transparent carriers with polymorphic accessors

## Java 17 (2021) — LTS

- **Sealed classes**: Controlled polymorphic hierarchies — the set of subtypes is known
- **Pattern matching for switch (preview)**: Polymorphic dispatch on type patterns

## Java 21 (2023)

- **Pattern matching for switch (standard)**: Full type pattern switching
- **Record patterns**: Polymorphic deconstruction of records
- **Unnamed patterns**: Ignoring parts of patterns

## Polymorphism Trends

Java's polymorphism has evolved from purely object-oriented (virtual methods, interfaces) to include functional (lambdas) and structural (pattern matching) polymorphism. The trend is toward more flexible, safer polymorphic code with less boilerplate.
