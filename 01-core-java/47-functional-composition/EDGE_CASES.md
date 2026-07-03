# Edge Cases & Pitfalls: Functional Composition

While functional composition leads to elegant code, it introduces unique challenges regarding exception handling, null safety, and debugging.

## 1. The Null Pointer Trap in Composition
*   **The Scenario**: You compose a chain of functions: `Function<String, String> pipeline = trim.andThen(toUpperCase).andThen(substring);`. You pass `null` to the pipeline.
*   **The Pitfall**: Functional interfaces like `Function` and `Predicate` do not have built-in null safety. If `trim` receives `null`, it throws a `NullPointerException`. Even worse, if `trim` somehow returns `null`, `toUpperCase` will throw the NPE. The pipeline fails abruptly.
*   **Mitigation**: 
    1. Validate inputs before starting the pipeline.
    2. Use `Optional` to wrap the input and safely map it through the pipeline: `Optional.ofNullable(input).map(trim).map(toUpperCase)...`

## 2. Breaking the Monad Laws (Stateful Lambdas)
*   **The Scenario**: You use `Optional.map()` or `Stream.map()` and pass a lambda that modifies an external variable (side-effect).
    ```java
    List<String> logs = new ArrayList<>();
    Optional<String> result = Optional.of("Data").map(d -> {
        logs.add("Processed: " + d); // Side-effect!
        return d.toUpperCase();
    });
    ```
*   **The Pitfall**: You have broken the mathematical purity of the Functor/Monad. `map` and `flatMap` are expected to be pure transformations. If you introduce side-effects, your code becomes unpredictable. For example, if you do this in a Java Stream and later switch it to `.parallel()`, `logs.add()` will throw a `ConcurrentModificationException`.
*   **Mitigation**: Never use side-effects inside `map` or `flatMap`. If you need a side-effect, use a terminal operation like `forEach`, or explicitly use the `peek()` method (in Streams or Reactive frameworks) which is specifically designed for side-effects.

## 3. The Debugging Black Hole
*   **The Scenario**: You have a 10-stage composed function: `f1.andThen(f2).andThen(f3)...`. An exception is thrown at stage 6.
*   **The Pitfall**: Because the functions are composed dynamically at runtime into a massive lambda chain, the stack trace will be incredibly cryptic. It will consist of internal JDK lambda routing methods, making it nearly impossible to figure out which specific function in your chain actually failed. You cannot easily place a breakpoint *between* `f5` and `f6`.
*   **Mitigation**: During complex debugging, you may need to temporarily unroll the composition into standard imperative steps, or insert logging functions into the chain: `f1.andThen(log).andThen(f2)...`.

## 4. `flatMap` vs `map` Confusion
*   **The Scenario**: You have a method `findUser(id)` that returns `Optional<User>`. You have another method `findAddress(user)` that returns `Optional<Address>`. You want to find the address for an ID.
*   **The Pitfall**: Developers frequently use `map` by mistake: `Optional<Optional<Address>> result = findUser(1).map(this::findAddress);`. They are then forced to do awkward unwrapping: `result.get().get()`.
*   **Mitigation**: Recognize when a transformation returns a container. If the function returns a container (like `Optional` or `Stream`), you almost always want to use `flatMap` to prevent nesting.

## 5. Type Inference Failures in Complex Chains
*   **The Scenario**: You attempt to chain deeply nested generics or complex method references.
*   **The Pitfall**: The Java compiler's type inference engine is powerful, but it has limits. If you chain too many `andThen` calls with generic types, or mix lambdas with overloaded method references, the compiler might throw a cryptic "Target type cannot be inferred" error.
*   **Mitigation**: You may need to explicitly specify the types. Instead of `x -> x.process()`, you might have to write `(Data x) -> x.process()`, or break the chain into intermediate variables with explicit type declarations to help the compiler.