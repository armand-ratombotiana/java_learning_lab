# Module 49: Kotlin for Java Developers - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: How does Kotlin achieve interoperability with Java?
**Answer**:
Kotlin is compiled down to the exact same Java Bytecode (`.class` files) as Java. Because they both run on the JVM, they can call each other freely. The Kotlin compiler automatically bridges differences:
- Kotlin properties (which don't have explicit getters/setters in the source code) are compiled into private fields with standard `getXXX()` and `setXXX()` methods so Java can access them normally.
- Top-level Kotlin functions are compiled into static methods within a synthetic Java class (`FileNameKt`).

### Q2: What is a Coroutine, and how is it different from a Java Thread?
**Answer**:
A Java `Thread` is mapped 1:1 to a native OS thread. Creating them is expensive, and blocking them (e.g., waiting for I/O) wastes significant CPU and memory resources.
A **Coroutine** is a "lightweight thread" managed entirely by the Kotlin runtime, not the OS. When a coroutine hits a suspension point (like a network call via `delay()`), it "suspends" rather than blocking. The underlying OS thread is instantly freed up to execute other coroutines. This allows you to run 100,000 coroutines on a pool of just 4 OS threads, achieving massive scalability with code that still looks sequential and imperative (unlike reactive programming).

### Q3: Explain the `reified` keyword in Kotlin.
**Answer**:
Because Kotlin compiles to Java Bytecode, it suffers from the same **Type Erasure** limitation as Java (generic type information is lost at runtime). Therefore, you cannot normally do `if (myVar is T)`.
However, Kotlin has a feature called **Inline Functions**. If you mark a function as `inline`, the compiler literally copies the function's bytecode into the call site. If you also mark the generic parameter as `reified` (e.g., `inline fun <reified T> printIfType(obj: Any)`), the compiler replaces `T` with the actual concrete type being used at the call site. This allows you to perform type checks (`is T`) and get class references (`T::class.java`) at runtime, which is impossible in Java.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Singleton and Object Declaration
**Problem**: An interviewer asks you to write a thread-safe Singleton in Java using double-checked locking, and then asks you how you would write the exact same thing in Kotlin.

**Solution**:
Java requires a complex setup with a private constructor, a `volatile` instance variable, and a `synchronized` block to ensure thread safety.
Kotlin solves this at the language level using the `object` keyword. An `object` declaration creates a class and instantiates exactly one instance of it in a thread-safe manner automatically.

```kotlin
// Java
public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private DatabaseManager() {}
    public static DatabaseManager getInstance() { ... }
}

// Kotlin
object DatabaseManager {
    fun connect() { println("Connected") }
}
// Usage: DatabaseManager.connect()
```

### Scenario 2: Smart Casts
**Problem**: Given a variable `obj` of type `Any`, write a snippet that prints the length of the string if it is a String, or the integer value doubled if it is an Int. Do this without using explicit casting (`(String) obj`).

**Solution**:
Kotlin's compiler is smart enough to track `is` checks. Once the check passes, the variable is automatically cast to that type in that scope.

```kotlin
fun process(obj: Any) {
    when (obj) {
        is String -> println("String length: ${obj.length}") // Smart cast to String
        is Int -> println("Int doubled: ${obj * 2}")       // Smart cast to Int
        else -> println("Unknown type")
    }
}
```