# Deep Dive: Type Inference

## 1. What is Type Inference?
Type inference is a Java compiler's ability to look at each method invocation and corresponding declaration to determine the type argument (or arguments) that make the invocation applicable. It reduces verbosity by allowing the compiler to deduce types from the context, so the developer doesn't have to type them out explicitly.

## 2. The Evolution of Type Inference in Java

*   **Java 5 (Generics)**: Introduced basic type inference for generic methods. `Collections.<String>emptyList()` could often be written as `Collections.emptyList()`.
*   **Java 7 (The Diamond Operator `<>`)**: Allowed the compiler to infer the generic type arguments of a constructor based on the variable declaration.
    ```java
    // Pre-Java 7
    Map<String, List<String>> map = new HashMap<String, List<String>>();
    // Java 7+
    Map<String, List<String>> map = new HashMap<>();
    ```
*   **Java 8 (Lambdas)**: Allowed the compiler to infer the parameter types of a lambda expression based on the target functional interface.
    ```java
    // Explicit
    Predicate<String> p = (String s) -> s.isEmpty();
    // Inferred
    Predicate<String> p = s -> s.isEmpty();
    ```
*   **Java 10 (`var`)**: Introduced Local Variable Type Inference.

## 3. Local Variable Type Inference (`var`)
Introduced in Java 10, the `var` keyword allows you to omit the explicit type declaration for local variables. The compiler infers the type from the initializer on the right-hand side.

```java
// Explicit
BufferedReader reader = new BufferedReader(new FileReader("data.txt"));

// Inferred
var reader = new BufferedReader(new FileReader("data.txt"));
```

### Critical Rules for `var`:
1.  **It is NOT dynamic typing**: Java is still strictly, statically typed. Once the compiler infers the type, it is locked in. If `var x = 10;`, `x` is an `int`. You cannot later do `x = "Hello"`.
2.  **Local Variables Only**: You can only use `var` for local variables inside methods, for-loop indices, and try-with-resources variables. You **cannot** use `var` for class fields, method parameters, or method return types.
3.  **Must be initialized**: You cannot write `var x;`. The compiler needs the right-hand side to infer the type.
4.  **Cannot be null**: You cannot write `var x = null;`. The compiler has no idea what type `null` is supposed to represent.

## 4. Method Type Inference and Target Typing
When you call a generic method, the compiler uses a complex set of rules to determine the type parameters. It looks at:
1.  The types of the arguments passed to the method.
2.  The type of the variable the result is being assigned to (**Target Typing**).

```java
public static <T> List<T> makeList(T item) { ... }

// The compiler infers T is String based on the argument "Hello"
List<String> list1 = makeList("Hello"); 

// The compiler infers T is Object based on the Target Type (List<Object>)
// even though the argument is a String.
List<Object> list2 = makeList("Hello"); 
```

### The Java 8 "Target Typing" Enhancement
Before Java 8, target typing was limited. If you passed the result of a generic method directly into another method, the compiler would often fail to infer the type and default to `Object`. Java 8 significantly improved the compiler's ability to infer types across nested method calls and lambda expressions.

## 5. Best Practices for `var`
Just because you *can* use `var` everywhere doesn't mean you *should*. The official OpenJDK guidelines suggest:
*   **Use it when the type is obvious**: `var list = new ArrayList<String>();` (Good).
*   **Avoid it when the type is obscure**: `var result = processor.process();` (Bad: What is `result`? A String? A Boolean? A custom object? The reader has to jump to the `process` method to find out).
*   **Use it to shorten massive generic types**: `var map = new ConcurrentHashMap<String, List<Map<String, String>>>();` (Good).