# Interview Preparation: Advanced Pattern Matching

This document covers advanced questions related to Java 21 pattern matching, flow typing, and the Visitor pattern replacement.

## Q1: Explain the concept of "Flow Scoping" with Pattern Variables in Java.
**Answer:**
Flow scoping means that a pattern variable (e.g., `s` in `obj instanceof String s`) is only in scope in the exact lines of code where the compiler can mathematically guarantee that the `instanceof` check evaluated to `true`.
For example, in `if (obj instanceof String s && s.length() > 0)`, `s` is in scope on the right side of the `&&` because the right side only executes if the left side is true.
If you use early returns, like `if (!(obj instanceof String s)) return;`, `s` is in scope for the rest of the method block below the `if` statement, because the only way to reach that code is if the condition was false (meaning `obj` *is* a String).

## Q2: What is "Dominance" in a pattern-matching `switch` statement, and why does the compiler enforce it?
**Answer:**
Dominance occurs when a more general `case` label appears before a more specific `case` label in a `switch` statement.
For example, if `case Object o` appears before `case String s`.
Because the `switch` evaluates top-to-bottom, the `Object` case will catch everything, including Strings. The `String s` case is therefore "dead code" and can never be reached. The Java compiler enforces dominance checking and throws a compilation error to prevent developers from writing unreachable code. Cases must be ordered from most specific to most general.

## Q3: How do Record Patterns (`case User(String name, int age)`) improve upon standard type patterns?
**Answer:**
Standard type patterns (`case User u`) require you to manually extract the data you need from the object on the right side of the arrow (e.g., `u.name()`).
Record patterns allow you to declaratively deconstruct the record directly in the `case` label. The variables `name` and `age` are automatically extracted and made available in the scope of the `case` block. This reduces boilerplate and makes complex nested data structures much easier to read and process.

## Q4: How does Pattern Matching with Sealed Classes replace the traditional Visitor Pattern?
**Answer:**
The Visitor Pattern was traditionally used to perform operations on a hierarchy of classes (like an Abstract Syntax Tree) without modifying the classes themselves. It required complex double-dispatch logic (`accept` and `visit` methods) scattered across the entire hierarchy.
With Sealed Classes, the hierarchy is closed. A `switch` expression over a sealed interface is exhaustively checked by the compiler. You can write a single, centralized method with a `switch` that handles every possible subclass using pattern matching. This achieves the exact same goal as the Visitor Pattern (externalizing operations) but in a much more readable, concise, and maintainable way.

## Q5: How does a Java 21 `switch` expression handle a `null` input compared to older Java versions?
**Answer:**
Historically, passing `null` into a `switch` statement immediately threw a `NullPointerException` before any cases were evaluated.
In Java 21, `switch` allows you to explicitly handle null using `case null -> ...`.
If you provide this case, the NPE is avoided. However, if you *do not* provide a `case null` (and the input is null), the switch will still throw an NPE. It does *not* fall through to the `default` branch. This preserves backward compatibility while offering a safe way to handle nulls.