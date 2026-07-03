# Interview Preparation: Functional Interfaces & SAM

This document covers advanced questions related to functional interfaces, lambdas, method references, and variable scoping.

## Q1: What is a Functional Interface, and why did Java introduce the `@FunctionalInterface` annotation?
**Answer:**
A Functional Interface is an interface that contains exactly one abstract method (often called a SAM - Single Abstract Method type). It may contain any number of `default` or `static` methods, and it may override methods from `java.lang.Object`.
The `@FunctionalInterface` annotation is optional. It was introduced to explicitly declare the programmer's intent. If applied, the compiler will generate an error if a second abstract method is accidentally added to the interface, preventing the accidental breaking of all lambda expressions that implement it.

## Q2: Explain the difference between `Predicate`, `Function`, `Consumer`, and `Supplier`.
**Answer:**
These are the four core functional interfaces in `java.util.function`:
*   `Predicate<T>`: Takes an argument `T`, returns a `boolean`. Used for filtering or matching. (Method: `test(T t)`)
*   `Function<T, R>`: Takes an argument `T`, transforms it, and returns a result `R`. Used for mapping. (Method: `apply(T t)`)
*   `Consumer<T>`: Takes an argument `T`, returns `void`. Used for operations with side-effects (e.g., printing, saving to DB). (Method: `accept(T t)`)
*   `Supplier<T>`: Takes no arguments, returns a `T`. Used for lazy generation or factory methods. (Method: `get()`)

## Q3: What does "effectively final" mean in the context of lambda expressions?
**Answer:**
Lambdas can capture (close over) variables from their enclosing scope. However, Java requires that these local variables be either explicitly marked as `final` or be "effectively final."
A variable is effectively final if its value is never changed after it is initialized, even if the `final` keyword is omitted. If you try to reassign a captured variable inside or outside the lambda, the compiler will throw an error. This restriction exists to prevent race conditions and complex state management, as lambdas are often executed asynchronously or in parallel streams.

## Q4: How does the compiler resolve overloaded methods when passing a lambda expression?
**Answer:**
The compiler uses a process called **Target Typing**. It looks at the context (the method signature being called, the variable assignment) to determine the expected functional interface.
If there are overloaded methods (e.g., `void execute(Callable c)` and `void execute(Supplier s)`), the compiler tries to match the lambda's shape (arguments and return type) to the available interfaces. If both match (as with `Callable` and `Supplier`, which both take no arguments and return a value), the compiler will throw an "Ambiguous method call" error. You must resolve this by explicitly casting the lambda (e.g., `execute((Supplier) () -> "result")`).

## Q5: Can you throw a checked exception from a lambda expression?
**Answer:**
Yes, but *only if* the abstract method of the target functional interface declares that it throws that exception.
The standard interfaces in `java.util.function` (like `Consumer` or `Function`) do not declare any checked exceptions. Therefore, if you call a method like `Thread.sleep()` inside a `Consumer`, you must wrap it in a `try-catch` block and handle it (often by rethrowing it as a `RuntimeException`). Alternatively, you can define your own custom functional interface that declares `throws Exception`.