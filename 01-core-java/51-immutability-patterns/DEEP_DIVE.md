# Deep Dive: Immutability Patterns

## 1. The Core Philosophy of Immutability
Immutability is the cornerstone of functional programming and robust concurrent design. An immutable object is an object whose internal state cannot be modified after it is constructed. 

### Why Immutability?
*   **Thread Safety**: Immutable objects are inherently thread-safe. They can be shared freely across threads without synchronization, locks, or fear of race conditions.
*   **Failure Atomicity**: If a method throws an exception while operating on an immutable object, the object is never left in a corrupted, half-modified state.
*   **Cacheability**: Immutable objects make excellent keys for `HashMap`s and elements for `HashSet`s because their hash codes will never change.

## 2. Designing Immutable Objects in Java
To create a truly immutable class, you must follow strict rules:

1.  **Declare the class as `final`**: Prevents subclasses from overriding methods to return mutable state.
2.  **Declare all fields as `private` and `final`**: Ensures fields are assigned only once during construction.
3.  **Provide no mutator methods (setters)**.
4.  **Ensure exclusive access to mutable fields**: If a field references a mutable object (e.g., `java.util.Date`, `ArrayList`), you must:
    *   Make a **defensive copy** of the object in the constructor.
    *   Make a **defensive copy** of the object in the getter.

*(Note: Java Records natively enforce rules 1, 2, and 3, but you must still manually handle rule 4 using compact constructors).*

## 3. The "Copy-on-Write" Pattern (Wither Methods)
If an object is immutable, how do you change its state? You don't. You create a *new* object that represents the new state.

This is typically implemented using "Wither" methods (e.g., `withName`, `withAge`).

```java
public final class User {
    private final String name;
    private final int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Wither method: Returns a new instance with the updated field
    public User withAge(int newAge) {
        // Optimization: If the value hasn't changed, return the current instance
        if (this.age == newAge) return this; 
        
        // Copy the unchanged fields (name) and apply the new field (newAge)
        return new User(this.name, newAge);
    }
}
```

## 4. The Builder Pattern (For Complex Immutability)
If an immutable class has many fields, writing a constructor with 15 arguments is unreadable and error-prone. The Builder pattern separates the mutable construction phase from the immutable runtime phase.

```java
public final class ServerConfig {
    private final String host;
    private final int port;
    private final List<String> modules;

    private ServerConfig(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        // Defensive copy during construction!
        this.modules = List.copyOf(builder.modules); 
    }

    public static class Builder {
        private String host = "localhost";
        private int port = 8080;
        private List<String> modules = new ArrayList<>();

        public Builder host(String host) { this.host = host; return this; }
        public Builder port(int port) { this.port = port; return this; }
        public Builder addModule(String module) { this.modules.add(module); return this; }
        
        public ServerConfig build() { return new ServerConfig(this); }
    }
}
```

## 5. Persistent Data Structures
When dealing with large collections, creating a full copy every time an element is added (like `CopyOnWriteArrayList` does) is incredibly slow ($O(N)$).

As discussed in the Functional Data Structures module, **Persistent Data Structures** solve this by using structural sharing (e.g., Hash Array Mapped Tries). When you "add" an element to a persistent map, it returns a new map that shares 99% of its internal tree structure with the old map, achieving $O(1)$ or $O(\log N)$ copy performance.

*   *Java Standard Library*: `CopyOnWriteArrayList` and `CopyOnWriteArraySet` (Full array copy on every write - only suitable for mostly-read workloads).
*   *Functional Libraries*: Vavr or Eclipse Collections provide true persistent, structural-sharing data structures (`io.vavr.collection.List`, `io.vavr.collection.HashMap`).