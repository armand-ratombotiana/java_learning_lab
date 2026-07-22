# Interview Questions: OOP Basics

## Company-Specific Focus

### Google
- Why composition over inheritance in large-scale systems
- Favoring interfaces over abstract classes for extensibility 
- Encapsulation and defensive copying to prevent unintended state change

### Microsoft
- Java OOP vs C#: Virtual/override vs final/overriding semantics
- Abstract classes for frameworks: template method pattern in library design
- Access modifier differences between Java and C# (e.g., internal vs package-private)

### Amazon
- Encapsulation in microservices: what is the best data-hiding strategy for shared libraries
- Object life cycle, GC eligibility, and reference types (strong, soft, weak, phantom)
- Cohesion and coupling: OOP design principles for maintainable code at scale

### Meta
- Single Responsibility Principle: how to avoid god classes
- OOP as a scalability tool: modeling complex domains using classes and objects
- Immutability as a paradigm for safe state management

### Apple
- Encapsulation: the importance of private fields and providing controlled access through methods
- Factory methods over constructors for better API design
- The builder pattern for constructing complex objects

### Oracle
- JLS 6: Declarations, scope, and access control
- The JVM object header: how marks and klass pointers are laid out in memory
- Object's special status: the root of the class hierarchy, wait/notify, and monitor association
- Cloning: the `Cloneable` marker interface and its shallow-copy semantics

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 146 LRU Cache | Medium | Google, Amazon, Apple, Microsoft | Object-oriented design, encapsulation |
| 348 Design Tic-Tac-Toe | Medium | Microsoft, Google, Amazon | Class structure for game logic |
| 380 Insert Delete GetRandom O(1) | Medium | Amazon, Facebook, Apple | Class with data hiding, getRandom sampling |
| 355 Design Twitter | Medium | Amazon, Google, Microsoft | OO modeling of users, tweets, and feeds |
| 535 Encode and Decode TinyURL | Medium | Google, Amazon, Apple | Simple object-oriented encapsulation pattern |

## Real Production Scenarios
- **Google**: Antipattern of using a deep inheritance hierarchy for GUI components — replaced with delegation and composition, reducing method-override depth from 8 to 2
- **Uber**: God class of 15K lines in a logistics system — refactored into 20 cohesive objects using SRP
- **Netflix**: Public field exposure caused unexpected mutation from library user code — changed to private with getter methods

## Interview Patterns & Tips
- **Abstraction**: Use interfaces for contracts, not concrete implementations. "Code to an interface, not to an implementation"
- **Instantiation order**: When creating a derived class object, the parent class constructor runs first. If the parent constructor calls an overridden method, it invokes the child method before the child fields are initialized
- **Defensive copying**: When returning a reference to an internal object (e.g., a Date or a List), return a copy to avoid the caller modifying internal state
- **Cloning**: `Object.clone()` creates a shallow copy. If an object has reference fields, shallow copy makes both objects share the same inner object
- **Hiding vs Overriding**: Static methods in derived class hide, not override. The method invocation is resolved at compile time based on the type of reference

## Deep Dive Questions
- **Object header**: How many bytes are the mark word and klass pointer in a 64-bit JVM? What is the difference with compressed OOPs enabled?
- **Monitor**: When an object is used with `synchronized`, how does the JVM store and manage the monitor? What is the state machine of the lock (biasing, lightweight, heavyweight)?
- **GC**: How does the OOP-Klass model facilitate instance creation and method dispatch? How is the vtable stored and referenced from the klass?
- **JIT**: How does the JIT compiler handle the case where a single call site of an interface method is found to only call one type? What optimization is applied?
- **Java 21+**: With the addition of records and sealed classes, what OOP concepts are superseded? Can records replace simple immutable classes entirely?