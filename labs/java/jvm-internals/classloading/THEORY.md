# Classloading Theory & Lifecycle

## 💡 Dynamic Loading
Unlike C++, where the entire program is compiled and linked into a single massive executable binary before it runs, Java is **dynamically loaded**.
When you start a Java application, the JVM does not load all 10,000 `.class` files into memory at once. It only loads the class containing the `main` method.
Other classes are loaded into memory *only when they are referenced for the first time* during execution.

## 🔄 The Class Lifecycle
When the JVM encounters a new class (e.g., `new User()`), it puts the class through three distinct phases:

### 1. Loading
The JVM finds the `.class` file on the file system (or network, or inside a JAR) and reads its binary data. It creates a `java.lang.Class` object in the Heap to represent this class, and stores the actual bytecode and metadata in the Metaspace.

### 2. Linking
Linking connects the newly loaded class to the rest of the JVM runtime state. It has three sub-steps:
- **Verification**: The JVM checks the bytecode to ensure it doesn't violate security rules or Java language semantics (e.g., jumping to an invalid memory address). This prevents malicious compiled code from crashing the JVM.
- **Preparation**: The JVM allocates memory in the Metaspace for static variables and initializes them to their default values (e.g., `0` for int, `null` for objects). *Note: It does not execute your initialization code yet.*
- **Resolution**: The JVM replaces symbolic references in the constant pool (like a string "com/example/User") with direct memory addresses.

### 3. Initialization
This is the final phase. The JVM executes the class's `<clinit>` method, which contains all the static block initializers and static variable assignments you wrote in your code.
```java
class User {
    static int count = 5; // Executed during Initialization, not Preparation!
    static {
        System.out.println("User class initialized!");
    }
}
```
Because of this lazy initialization, the static block above will not print until the exact millisecond the `User` class is first used.