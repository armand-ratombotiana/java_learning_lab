# Edge Cases & Pitfalls: Type Erasure

Type erasure is the root cause of almost all confusing behavior in Java Generics. It forces developers to deal with unchecked warnings, heap pollution, and reflection anomalies.

## 1. Heap Pollution
*   **The Scenario**: You mix raw types (pre-Java 5) with parameterized types (Java 5+).
    ```java
    List<String> strings = new ArrayList<>();
    List rawList = strings; // Allowed for backward compatibility
    rawList.add(8);         // Adds an Integer to a String list!
    ```
*   **The Pitfall**: The compiler allows this with only an "unchecked" warning. The list now contains an `Integer`, but its reference type is `List<String>`. This is called **Heap Pollution**.
*   **The Consequence**: When you later iterate over `strings`, the compiler has inserted hidden casts to `String`. When it hits the `Integer`, it throws a `ClassCastException` at runtime in code that looks perfectly type-safe.
*   **Mitigation**: Never use raw types. Always resolve "unchecked" compiler warnings.

## 2. The `instanceof` Limitation
*   **The Scenario**: You want to check if an object is a list of strings.
    ```java
    if (obj instanceof List<String>) { ... } // ERROR!
    ```
*   **The Pitfall**: At runtime, all generic type parameters are erased. The JVM only sees `List`. It cannot possibly know if the list was instantiated as a `List<String>` or a `List<Integer>`. Therefore, the compiler strictly forbids checking parameterized types with `instanceof`.
*   **Mitigation**: You can only check the raw type or use unbounded wildcards: `if (obj instanceof List<?>)`. If you need to verify the contents, you must iterate through the list and check each element individually.

## 3. Varargs and Generics (The Heap Pollution Warning)
*   **The Scenario**: You write a generic method that accepts a variable number of arguments (varargs).
    ```java
    public static <T> void printAll(T... items) { ... }
    ```
*   **The Pitfall**: Under the hood, varargs are implemented using arrays. `T...` becomes `T[]`. As we know, generic arrays are illegal because of type erasure. The compiler creates an `Object[]` instead, which can lead to heap pollution if you expose this array to other methods. The compiler will issue a "Possible heap pollution from parameterized vararg type" warning.
*   **Mitigation**: If you are certain your method does not store anything into the varargs array or let the array escape the method, you can suppress the warning by annotating the method with `@SafeVarargs`.

## 4. Method Overloading Conflicts
*   **The Scenario**: You try to overload a method based on different generic types.
    ```java
    public void process(List<String> strings) { ... }
    public void process(List<Integer> integers) { ... } // ERROR!
    ```
*   **The Pitfall**: Because of type erasure, both methods compile down to the exact same signature: `public void process(List list)`. The JVM cannot distinguish between them, so the compiler throws an error.
*   **Mitigation**: You must change the method names (e.g., `processStrings`, `processIntegers`).

## 5. Reflection and the "Super Type Token" Hack
*   **The Scenario**: You are writing a JSON deserializer (like Jackson or Gson). You need to deserialize a string into a `List<User>`.
*   **The Pitfall**: If you pass `List.class` as a type token, the deserializer only knows to create a `List`. It doesn't know what to put inside it, so it defaults to `Map`s or `Object`s.
*   **Mitigation**: While local variable generic types are erased, **class signatures are not**. If you create an anonymous subclass, the compiler embeds the generic type information in the `.class` file metadata. Libraries use the "Super Type Token" pattern to read this metadata at runtime via reflection.
    ```java
    // The {} creates an anonymous subclass, preserving the type metadata!
    TypeReference<List<User>> typeRef = new TypeReference<List<User>>() {}; 
    ```