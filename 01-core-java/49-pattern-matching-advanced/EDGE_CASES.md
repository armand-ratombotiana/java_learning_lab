# Edge Cases & Pitfalls: Advanced Pattern Matching

Pattern matching introduces new syntax and compiler rules. Misunderstanding these rules can lead to compilation errors, shadowing bugs, and broken exhaustiveness.

## 1. Pattern Variable Scope (Flow Scoping)
*   **The Scenario**: You use `instanceof` with a pattern variable, and then try to use that variable later in the method.
*   **The Pitfall**: The scope of a pattern variable is determined by *flow typing*. The variable is only in scope where the compiler can absolutely guarantee the `instanceof` check was true.
    ```java
    if (!(obj instanceof String s)) {
        System.out.println(s); // ERROR: 's' is not in scope here!
        return;
    }
    // 's' IS in scope here, because the only way to reach this line is if obj WAS a String.
    System.out.println(s.length()); 
    ```
*   **Mitigation**: Pay close attention to logical operators (`&&`, `||`) and early returns. For example, `if (obj instanceof String s || s.length() > 0)` will not compile because the right side of the `||` might execute when the left side is false, meaning `s` is undefined.

## 2. Dominance in Switch Cases
*   **The Scenario**: You have a `switch` statement with multiple type patterns, including a base class and a subclass.
    ```java
    switch (shape) {
        case Shape s -> "A generic shape";
        case Circle c -> "A circle"; // ERROR!
    }
    ```
*   **The Pitfall**: The Java compiler evaluates `switch` cases top-to-bottom. Because `Circle` is a `Shape`, the first case (`Shape s`) will catch *every* circle. The second case is "dominated" by the first and is therefore unreachable code. The compiler will throw an error.
*   **Mitigation**: Always order your `switch` cases from most specific (subclasses, guarded patterns) to most general (base classes).

## 3. The `null` Case Trap
*   **The Scenario**: You write a `switch` expression over an object that might be null.
*   **The Pitfall**: Historically, switching on `null` immediately threw a `NullPointerException`. In Java 21, you can explicitly handle nulls with `case null -> ...`. However, if you omit the `case null`, and the object is null, the `switch` will still throw an NPE. It does *not* fall into the `default` branch!
*   **Mitigation**: If the input to a pattern-matching `switch` can legitimately be null, you must explicitly include `case null -> ...`. (You can also combine it: `case null, default -> ...`).

## 4. Breaking Exhaustiveness with Generics
*   **The Scenario**: You switch over a sealed interface that uses generic type parameters.
    ```java
    sealed interface Result<T> permits Success, Failure {}
    record Success<T>(T data) implements Result<T> {}
    record Failure<T>(Exception e) implements Result<T> {}
    ```
*   **The Pitfall**: If you switch on `Result<String>`, and you provide cases for `Success<String>` and `Failure<String>`, the compiler might still complain that the switch is not exhaustive. Due to type erasure, the compiler sometimes struggles to prove exhaustiveness when generic type bounds are involved.
*   **Mitigation**: You may be forced to use raw types in the pattern (`case Success s`) or add an "unreachable" `default` branch that throws an exception, though this defeats the primary benefit of sealed classes.

## 5. Record Pattern Nullability
*   **The Scenario**: You use a Record Pattern to deconstruct a nested record.
    ```java
    record Address(String city) {}
    record User(Address address) {}
    
    if (obj instanceof User(Address(String city))) { ... }
    ```
*   **The Pitfall**: What happens if `obj` is a `User`, but its `address` field is `null`? The pattern `Address(String city)` will fail to match the null address. The `if` statement will evaluate to `false`.
*   **Mitigation**: Record patterns perform implicit null checks on the nested components. If you need to handle the case where the user exists but the address is null, you must use a separate pattern or a guard clause: `case User(Address a) when a == null -> ...`.