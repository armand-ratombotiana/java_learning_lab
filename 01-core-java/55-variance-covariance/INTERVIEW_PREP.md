# Interview Preparation: Variance & Covariance

This document covers advanced questions related to subtyping rules, arrays vs generics, and the PECS acronym.

## Q1: What is the difference between Invariance, Covariance, and Contravariance?
**Answer:**
These terms describe how the subtyping of a container relates to the subtyping of its elements.
*   **Covariance**: Preserves the subtyping relationship. If `Dog` is a subtype of `Animal`, then `Container<Dog>` is a subtype of `Container<Animal>`. (Java Arrays are covariant).
*   **Contravariance**: Reverses the subtyping relationship. If `Dog` is a subtype of `Animal`, then `Container<Animal>` is a subtype of `Container<Dog>`.
*   **Invariance**: Ignores the subtyping relationship. `Container<Dog>` and `Container<Animal>` have no subtyping relationship whatsoever. (Java Generics are invariant by default).

## Q2: Why are Java Arrays covariant, and why is that considered a design flaw?
**Answer:**
Java arrays are covariant (e.g., `Object[] arr = new String[10];` is legal). This was a deliberate design choice in early Java (before generics existed) to allow developers to write utility methods that could sort or shuffle arrays of any type (e.g., `void sort(Object[] arr)`).
**The Flaw**: It compromises type safety at compile time. Because `arr` is an `Object[]` reference, the compiler allows `arr[0] = new Integer(5)`. However, the actual array in memory is a `String[]`. When the JVM tries to insert the `Integer`, it crashes with an `ArrayStoreException` at runtime.

## Q3: Explain the PECS rule. When and why do you use it?
**Answer:**
PECS stands for **Producer Extends, Consumer Super**. It is a mnemonic for remembering how to use wildcards to make generic APIs flexible.
Because generics are invariant, a method expecting `List<Animal>` cannot accept `List<Dog>`. To fix this, we use Use-Site Variance:
*   **Producer Extends**: If the method only *reads* from the collection (the collection produces data), use `? extends Animal`. This allows `List<Dog>` or `List<Cat>`. The compiler guarantees you will read an `Animal`, but forbids writing to the collection to maintain type safety.
*   **Consumer Super**: If the method only *writes* to the collection (the collection consumes data), use `? super Dog`. This allows `List<Dog>`, `List<Animal>`, or `List<Object>`. The compiler guarantees you can safely write a `Dog` into it, but restricts what you can read from it.

## Q4: Can you add `null` to a `List<? extends Animal>`? Why?
**Answer:**
Yes. The compiler forbids adding any specific object type to a `? extends` collection because it doesn't know the exact subtype of the list (e.g., you can't add a `Dog` because the list might be a `List<Cat>`).
However, `null` is a special literal in Java. It is a valid value for *every* reference type. Therefore, adding `null` is the only write operation the compiler allows on an upper-bounded wildcard collection. (Though doing so is usually a bad practice).

## Q5: What is the difference between `List<Object>` and `List<?>`?
**Answer:**
*   `List<Object>` is a specific, invariant generic type. It means "a list that is explicitly declared to hold Objects." You can put a `String` or an `Integer` into it. You *cannot* pass a `List<String>` to a method expecting `List<Object>`.
*   `List<?>` (an unbounded wildcard) means "a list of some specific, but unknown type." It is shorthand for `List<? extends Object>`. You *can* pass a `List<String>` to a method expecting `List<?>`. However, because the type is unknown, you cannot add anything (except `null`) to a `List<?>`.