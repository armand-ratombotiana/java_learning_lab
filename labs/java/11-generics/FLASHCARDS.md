# Generics — Flashcards

## Card 1
**Q:** What is a generic class?
**A:** A class with one or more type parameters, e.g., `class Box<T> { ... }`, allowing type-safe operation on various types.

## Card 2
**Q:** What is type erasure?
**A:** The compiler process that removes generic type parameters and replaces them with their bounds or Object, inserting casts where needed.

## Card 3
**Q:** What is PECS?
**A:** Producer Extends, Consumer Super. Use `? extends T` when you only read values (producer). Use `? super T` when you only write values (consumer). Use exact `T` if you do both.

## Card 4
**Q:** Can you create `new T()` in a generic class?
**A:** No. Type erasure removes `T` at runtime, so the JVM doesn't know what to instantiate.

## Card 5
**Q:** What is the difference between `List<String>` and `List<? extends String>`?
**A:** `List<String>` is an exact type (can read and write Strings). `List<? extends String>` is read-only — you can only read Strings, cannot add (except null).

## Card 6
**Q:** What is a bounded type parameter?
**A:** A type parameter with a constraint, e.g., `<T extends Number>` restricts T to Number or its subclasses.

## Card 7
**Q:** What is a bridge method?
**A:** A synthetic method the compiler generates to maintain polymorphism when a class implements a generic interface, e.g., `compareTo(Object)` bridging to `compareTo(Name)`.

## Card 8
**Q:** What is an unbounded wildcard?
**A:** `?` — represents an unknown type. Used when the type parameter doesn't matter, e.g., `List<?>`.

## Card 9
**Q:** What is a raw type?
**A:** Using a generic class without type parameters, e.g., `List` instead of `List<String>`. Discouraged — bypasses all type checking.

## Card 10
**Q:** What is heap pollution?
**A:** When a variable of a parameterized type refers to an object not of that type, causing ClassCastException on retrieval.

## Card 11
**Q:** Can you use `instanceof` with parameterized types?
**A:** No. `list instanceof List<String>` is a compile error. Use `list instanceof List<?>` instead.

## Card 12
**Q:** What does the diamond operator `<>` do?
**A:** Tells the compiler to infer type arguments from context. `List<String> list = new ArrayList<>();`

## Card 13
**Q:** Can enums have type parameters?
**A:** No. Enums cannot declare type parameters.

## Card 14
**Q:** What is a recursive type bound?
**A:** A type parameter that refers to itself, e.g., `<T extends Comparable<T>>`, requiring T to be comparable to itself.

## Card 15
**Q:** How do you get runtime type information for a generic class?
**A:** Pass a `Class<T>` token to the constructor, or use reflection to inspect the `Signature` attribute of the class file.
