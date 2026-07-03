# Edge Cases & Pitfalls: Advanced Encapsulation

While Java provides strong encapsulation tools, developers often inadvertently bypass them through misunderstanding or misuse of the language features.

## 1. The "Leaky" Immutable Object
*   **The Scenario**: You create a class with all `final` fields, but one of the fields is a mutable collection (e.g., `List<String>`).
*   **The Pitfall**: If you pass the list into the constructor directly, or return it directly from a getter, external code can modify the contents of the list, mutating the "immutable" object's state.
    ```java
    // BAD: Leaky Encapsulation
    public class BadImmutable {
        private final List<String> tags;
        public BadImmutable(List<String> tags) { this.tags = tags; }
        public List<String> getTags() { return this.tags; }
    }
    ```
*   **Mitigation**: Always use defensive copying.
    ```java
    // GOOD: True Immutability
    public class GoodImmutable {
        private final List<String> tags;
        public GoodImmutable(List<String> tags) { 
            this.tags = List.copyOf(tags); // Java 10+ unmodifiable copy
        }
        public List<String> getTags() { return this.tags; }
    }
    ```

## 2. Records and Mutable Components
*   **The Scenario**: You use a Java Record to create a concise data carrier: `public record Employee(String name, Date hireDate) {}`.
*   **The Pitfall**: Records guarantee that the *reference* to the field won't change (the fields are `final`), but they do *not* guarantee deep immutability. Because `java.util.Date` is mutable, someone can call `employee.hireDate().setTime(...)`, altering the record's state.
*   **Mitigation**: Use immutable types inside Records (e.g., `LocalDate` instead of `Date`, `List.copyOf()` in a compact constructor).
    ```java
    public record Employee(String name, Date hireDate) {
        public Employee {
            hireDate = new Date(hireDate.getTime()); // Defensive copy in compact constructor
        }
        @Override
        public Date hireDate() {
            return new Date(hireDate.getTime()); // Defensive copy in accessor
        }
    }
    ```

## 3. Reflection Bypassing Private Fields
*   **The Scenario**: You have a highly sensitive class with a `private` field containing a secret key.
*   **The Pitfall**: A developer (or a malicious library) can use Reflection (`field.setAccessible(true)`) to read or modify the private field, completely bypassing your encapsulation.
*   **Mitigation**: 
    *   Pre-Java 9: Run with a `SecurityManager`.
    *   Java 9+: Use the Java Module System (`module-info.java`). If you do not `open` the package containing your sensitive class to the module performing reflection, `setAccessible(true)` will throw an `InaccessibleObjectException`.

## 4. The Serialization Trap
*   **The Scenario**: You carefully design an immutable class with complex invariant checks in its constructor (e.g., ensuring `startDate` is before `endDate`). You implement `Serializable`.
*   **The Pitfall**: Standard Java deserialization *bypasses constructors entirely*. It uses reflection to write data directly into the fields. An attacker could craft a malicious byte stream that creates an object with an invalid state (e.g., `endDate` before `startDate`), bypassing your encapsulation invariants.
*   **Mitigation**: Implement the `readResolve()` method or use the Serialization Proxy Pattern. Note: Java Records are immune to this pitfall; their deserialization *always* routes through the canonical constructor.

## 5. Sealed Classes in Different Packages
*   **The Scenario**: You declare a sealed class in package `com.core`, and you want to permit a subclass in package `com.plugins`.
*   **The Pitfall**: If you are not using Java Modules (named modules), sealed classes and their permitted subclasses *must* exist in the same package. The compiler will throw an error if they are in different packages in an unnamed module.
*   **Mitigation**: Either move them to the same package, or modularize your application using `module-info.java` (which allows permitted classes to be in different packages within the same module).