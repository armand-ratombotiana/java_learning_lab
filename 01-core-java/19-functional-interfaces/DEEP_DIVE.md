# Deep Dive: Functional Interfaces & SAM

## 1. The Single Abstract Method (SAM) Concept
Before Java 8, passing behavior (code) as an argument to a method required creating an anonymous inner class. This was verbose and clunky. Java 8 introduced Lambda Expressions to solve this, but it needed a type system to support them. 

Instead of creating a new function type (like `(int, int) -> int`), Java's designers cleverly repurposed existing interfaces. Any interface with exactly **one abstract method** is considered a **Functional Interface** (or SAM type). 

Because there is only one abstract method, the compiler can automatically infer that a lambda expression is intended to implement that specific method.

## 2. The `@FunctionalInterface` Annotation
While any interface with a single abstract method is implicitly a functional interface, it is best practice to annotate it with `@FunctionalInterface`.

```java
@FunctionalInterface
public interface StringProcessor {
    String process(String input);
    
    // It can still have default or static methods!
    default void print(String input) {
        System.out.println(process(input));
    }
}
```
*   **Purpose**: It tells the compiler to enforce the SAM rule. If another developer accidentally adds a second abstract method, the compiler will throw an error, preventing them from breaking all lambda expressions that implement the interface.

## 3. The `java.util.function` Package
Java provides a rich set of built-in functional interfaces so you rarely have to define your own. They are categorized by their inputs and outputs.

### The "Big Four"
1.  **`Predicate<T>`**: Takes a `T`, returns a `boolean`. (Used for filtering).
    *   *Method*: `boolean test(T t)`
2.  **`Function<T, R>`**: Takes a `T`, returns an `R`. (Used for mapping/transforming).
    *   *Method*: `R apply(T t)`
3.  **`Consumer<T>`**: Takes a `T`, returns `void`. (Used for side-effects like printing).
    *   *Method*: `void accept(T t)`
4.  **`Supplier<T>`**: Takes nothing, returns a `T`. (Used for lazy generation/factories).
    *   *Method*: `T get()`

### Primitive Variations
To avoid the performance cost of autoboxing (e.g., converting `int` to `Integer`), Java provides primitive-specific interfaces:
*   `IntPredicate`, `LongConsumer`, `DoubleSupplier`, `ToIntFunction<T>`, etc.

### Bi-Variations
Interfaces that take two arguments:
*   `BiPredicate<T, U>`, `BiFunction<T, U, R>`, `BiConsumer<T, U>`.

## 4. Method References
Method references (`::`) are shorthand for lambdas that do nothing but call an existing method. They make code cleaner and more readable.

1.  **Static Method**: `ClassName::methodName` (e.g., `Math::max`)
2.  **Instance Method of a particular object**: `instanceRef::methodName` (e.g., `System.out::println`)
3.  **Instance Method of an arbitrary object of a particular type**: `ClassName::methodName` (e.g., `String::toLowerCase`)
4.  **Constructor**: `ClassName::new` (e.g., `ArrayList::new`)

## 5. Type Inference and Target Typing
When you write a lambda like `x -> x.length()`, the compiler doesn't know what `x` is immediately. It determines the type of the lambda by looking at the context where it is used. This is called **Target Typing**.

```java
// Target type is Predicate<String>. Compiler infers 's' is a String.
Predicate<String> isLong = s -> s.length() > 5; 

// Target type is Function<String, Integer>. 
Function<String, Integer> lengthFunc = s -> s.length();
```
Because of target typing, the exact same lambda expression can represent different functional interfaces depending on where it is assigned, provided the signatures match.