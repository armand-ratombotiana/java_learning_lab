# Edge Cases & Pitfalls: Type Inference

Type inference makes code cleaner, but when the compiler guesses differently than the developer intended, it leads to confusing compilation errors or subtle runtime bugs.

## 1. The `var` and Diamond Operator Trap
*   **The Scenario**: You want to create an `ArrayList` of Strings using `var` and the diamond operator `<>`.
    ```java
    var list = new ArrayList<>();
    list.add("Hello");
    ```
*   **The Pitfall**: The compiler infers the type of `list` from the right side. The right side is `new ArrayList<>()`. Without an explicit target type on the left, the compiler has no information to infer the generic type inside the diamond. Therefore, it falls back to the lowest common denominator: `Object`. The inferred type is `ArrayList<Object>`.
*   **Mitigation**: When using `var`, you must provide explicit generic types on the right side if they cannot be inferred from constructor arguments.
    ```java
    var list = new ArrayList<String>(); // Safe
    ```

## 2. The Anonymous Class Subtyping Trap
*   **The Scenario**: You use `var` to capture an anonymous inner class.
    ```java
    var obj = new Object() {
        String name = "Custom";
        void print() { System.out.println(name); }
    };
    obj.print(); // This actually compiles!
    ```
*   **The Pitfall**: Normally, if you assign an anonymous class to an `Object` reference (`Object obj = new Object() {...}`), you cannot call `obj.print()` because the type is `Object`. However, `var` infers the *exact, highly specific, anonymous type*. While this seems cool, it breaks completely if you try to return `obj` from a method or pass it to another method, because anonymous types cannot be named or referenced outside their immediate scope.
*   **Mitigation**: Be extremely careful when using `var` with anonymous inner classes. The resulting type is an unnameable "non-denotable" type.

## 3. Poly Expression Ambiguity
*   **The Scenario**: You try to assign a lambda expression or a method reference directly to `var`.
    ```java
    var greeter = (String s) -> "Hello " + s; // ERROR
    var printer = System.out::println;        // ERROR
    ```
*   **The Pitfall**: Lambdas and method references are "poly expressions." Their type depends entirely on the Target Type (the functional interface they are assigned to). Because `var` tells the compiler to infer the type from the right side, but the right side needs the left side to know what type it is, you create a circular dependency. The compiler throws an error.
*   **Mitigation**: You must cast the lambda to explicitly provide the target type.
    ```java
    var greeter = (Function<String, String>) (s -> "Hello " + s);
    ```

## 4. Upcasting and Interface Inference
*   **The Scenario**: You have a method that returns an `ArrayList`. You assign it to `var`.
    ```java
    var list = getArrayList(); 
    ```
*   **The Pitfall**: The inferred type is strictly `ArrayList`, not `List`. If you later try to assign a `LinkedList` to that variable, it will fail to compile. `var` binds to the exact concrete type returned by the initializer, not the broader interface you might have manually chosen.
*   **Mitigation**: If you intend to reassign a variable to different implementations of an interface, do not use `var`. Use explicit interface typing (`List<String> list = ...`).

## 5. Primitive vs Wrapper Inference
*   **The Scenario**: You write `var num = 10;` or `var flag = true;`.
*   **The Pitfall**: The compiler infers primitives (`int`, `boolean`), not wrappers (`Integer`, `Boolean`). If you later try to pass `num` to a generic method that expects an object (e.g., `process(T item)`), autoboxing will occur. This is usually fine, but if you expected the variable to be an `Integer` so you could call methods on it or assign `null` to it, the compiler will throw an error.
*   **Mitigation**: If you need the wrapper type, explicitly instantiate it or cast it: `var num = (Integer) 10;`.