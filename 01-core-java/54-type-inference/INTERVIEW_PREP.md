# Interview Preparation: Type Inference

This document covers advanced questions related to the `var` keyword, target typing, and poly expressions.

## Q1: Does the `var` keyword make Java a dynamically typed language like JavaScript or Python?
**Answer:**
No. Java remains a strictly, statically typed language.
The `var` keyword simply instructs the compiler to infer the static type of the variable at compile time based on the initializer on the right-hand side.
Once the compiler infers the type (e.g., `var count = 0;` becomes `int`), that type is locked in. You cannot later assign a `String` to `count`.

## Q2: Why is it illegal to write `var myLambda = (String s) -> s.toUpperCase();`?
**Answer:**
Because lambdas (and method references) are **Poly Expressions**. Their type is not inherent; it depends entirely on the "Target Type" (the interface they are being assigned to).
The compiler looks at the left side of the assignment to figure out what type the lambda on the right side should be. However, `var` tells the compiler to look at the *right* side to figure out the type of the *left* side. This creates a circular dependency, and the compiler fails.
To fix this, you must cast the lambda to explicitly provide the target type: `var myLambda = (Function<String, String>) (s -> s.toUpperCase());`.

## Q3: What happens if you write `var list = new ArrayList<>();`?
**Answer:**
The compiler will infer the type as `ArrayList<Object>`.
Because the diamond operator `<>` is empty, the compiler relies on the left side of the assignment (the target type) to infer the generic parameter. Since the left side is `var`, there is no target type information. The compiler falls back to the lowest common denominator, which is `Object`.
To use `var` safely with generics, you must provide the generic type on the right side: `var list = new ArrayList<String>();`.

## Q4: Can you use `var` as a method return type or a class field? Why or why not?
**Answer:**
No, `var` is strictly for **Local Variable Type Inference**.
It cannot be used for method return types, method parameters, or class fields.
The designers of Java restricted it to local variables because local variables have a very limited, easily verifiable scope. If `var` were allowed in public method signatures, a developer changing the internal implementation of a method could accidentally change the return type, breaking the public API contract and causing cascading compilation failures across the entire codebase.

## Q5: What is a "Non-Denotable Type", and how does `var` interact with it?
**Answer:**
A non-denotable type is a type that exists in the JVM but cannot be explicitly named or written out in Java source code. The most common example is an Anonymous Inner Class.
If you do `Object obj = new Object() { void print() {} };`, you cannot call `obj.print()` because the reference type is `Object`.
However, if you use `var obj = new Object() { void print() {} };`, the compiler infers the exact, unnameable anonymous type. You *can* call `obj.print()`. This is a unique edge case where `var` allows you to do something that explicit typing cannot.