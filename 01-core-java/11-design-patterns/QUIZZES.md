# Module 11: Design Patterns - Quizzes

**Total Questions**: 24  
**Difficulty Levels**: Beginner (6), Intermediate (8), Advanced (6), Expert (4)  
**Time Estimate**: 90-120 minutes

---

## 🟢 Beginner Level (6 Questions)

### Q1: What is a Design Pattern?
**Question**: Which statement best describes a design pattern?

A) A specific implementation of a feature in Java  
B) A reusable solution to a common problem in software design  
C) A Java class that must be extended  
D) A type of exception handling mechanism  

**Answer**: B  
**Explanation**: Design patterns are proven, reusable solutions to common problems in software design. They provide templates for writing maintainable and scalable code, not specific implementations.

---

### Q2: Singleton Pattern Purpose
**Question**: What is the primary purpose of the Singleton pattern?

A) To create multiple instances of a class  
B) To ensure a class has only one instance and provide global access  
C) To allow inheritance of a class  
D) To implement multiple interfaces  

**Answer**: B  
**Explanation**: The Singleton pattern restricts instantiation to a single object and provides a global point of access to that instance.

---

### Q3: Factory Pattern Benefit
**Question**: What is a key benefit of using the Factory pattern?

A) It makes code run faster  
B) It reduces memory usage  
C) It decouples object creation from usage  
D) It eliminates the need for constructors  

**Answer**: C  
**Explanation**: The Factory pattern decouples the creation of objects from their usage, allowing you to change how objects are created without affecting client code.

---

### Q4: Builder Pattern Use Case
**Question**: When is the Builder pattern most useful?

A) When creating simple objects with few properties  
B) When constructing complex objects with many optional parameters  
C) When you need to create arrays  
D) When implementing inheritance  

**Answer**: B  
**Explanation**: The Builder pattern is ideal for constructing complex objects with many optional parameters, providing a clean and readable way to build objects step by step.

---

### Q5: Observer Pattern Relationship
**Question**: What type of relationship does the Observer pattern establish?

A) One-to-one  
B) Many-to-one  
C) One-to-many  
D) Many-to-many  

**Answer**: C  
**Explanation**: The Observer pattern establishes a one-to-many relationship where one subject notifies multiple observers of state changes.

---

### Q6: Strategy Pattern Flexibility
**Question**: What does the Strategy pattern allow you to do?

A) Create multiple instances of a class  
B) Change the algorithm used at runtime  
C) Inherit from multiple classes  
D) Implement static methods  

**Answer**: B  
**Explanation**: The Strategy pattern encapsulates different algorithms and allows you to select and change the algorithm at runtime.

---

## 🟡 Intermediate Level (8 Questions)

### Q7: Singleton Thread Safety
**Question**: Which Singleton implementation is thread-safe without using synchronization?

```java
// Option A
public class Singleton {
    private static Singleton instance;
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

// Option B
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Option C
public class Singleton {
    private static volatile Singleton instance;
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

A) Option A  
B) Option B  
C) Option C  
D) None of the above  

**Answer**: B  
**Explanation**: Option B uses eager initialization with a static final field, which is thread-safe by default due to Java's class loading mechanism. Options A and C have issues (A is not thread-safe, C uses synchronization).

---

### Q8: Adapter Pattern Implementation
**Question**: What is the primary role of an Adapter in the Adapter pattern?

A) To create new objects  
B) To convert one interface to another  
C) To manage object lifecycle  
D) To handle exceptions  

**Answer**: B  
**Explanation**: The Adapter pattern's primary role is to convert the interface of a class into another interface that clients expect, allowing incompatible interfaces to work together.

---

### Q9: Decorator vs Inheritance
**Question**: What is an advantage of using the Decorator pattern over inheritance?

A) Decorators are faster  
B) Decorators use less memory  
C) Decorators allow dynamic addition of responsibilities  
D) Decorators are easier to understand  

**Answer**: C  
**Explanation**: The Decorator pattern allows you to add responsibilities to objects dynamically at runtime, whereas inheritance is static and determined at compile time.

---

### Q10: Facade Pattern Complexity
**Question**: What problem does the Facade pattern solve?

A) Creating objects efficiently  
B) Simplifying complex subsystems by providing a unified interface  
C) Managing object state  
D) Handling exceptions  

**Answer**: B  
**Explanation**: The Facade pattern provides a unified, simplified interface to a complex subsystem, making it easier for clients to use.

---

### Q11: Command Pattern Undo
**Question**: How does the Command pattern support undo functionality?

A) By storing previous states  
B) By encapsulating requests as objects with execute() and undo() methods  
C) By using a stack of objects  
D) By reverting to previous versions  

**Answer**: B  
**Explanation**: The Command pattern encapsulates requests as objects, allowing you to implement undo by providing an undo() method alongside execute().

---

### Q12: State Pattern vs Strategy
**Question**: What is the key difference between State and Strategy patterns?

A) State is for objects, Strategy is for primitives  
B) State changes behavior based on internal state; Strategy allows client to choose algorithm  
C) State is faster than Strategy  
D) There is no difference  

**Answer**: B  
**Explanation**: The State pattern changes an object's behavior based on its internal state, while the Strategy pattern allows the client to choose which algorithm to use.

---

### Q13: Repository Pattern Benefit
**Question**: What is the primary benefit of the Repository pattern?

A) It makes code run faster  
B) It encapsulates data access logic and provides a clean abstraction  
C) It eliminates the need for databases  
D) It reduces the number of classes  

**Answer**: B  
**Explanation**: The Repository pattern encapsulates data access logic, providing a clean abstraction that makes it easier to test and maintain code.

---

### Q14: MVC Pattern Separation
**Question**: In the MVC pattern, what is the responsibility of the Controller?

A) To display data to the user  
B) To store application data  
C) To handle user input and update the Model  
D) To manage database connections  

**Answer**: C  
**Explanation**: The Controller handles user input, processes it, and updates the Model accordingly. The View displays data, and the Model stores data.

---

## 🔴 Advanced Level (6 Questions)

### Q15: Double-Checked Locking
**Question**: Why is the `volatile` keyword necessary in double-checked locking?

```java
private static volatile Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

A) To prevent garbage collection  
B) To ensure visibility of changes across threads  
C) To improve performance  
D) To prevent inheritance  

**Answer**: B  
**Explanation**: The `volatile` keyword ensures that changes to the `instance` variable are immediately visible to all threads, preventing issues with instruction reordering and caching.

---

### Q16: Factory Method vs Abstract Factory
**Question**: When would you use Abstract Factory instead of Factory Method?

A) When creating a single type of object  
B) When creating families of related objects  
C) When you need to create arrays  
D) When inheritance is not needed  

**Answer**: B  
**Explanation**: Abstract Factory is used when you need to create families of related objects, while Factory Method is used for creating a single type of object.

---

### Q17: Proxy vs Decorator
**Question**: What is the key difference between Proxy and Decorator patterns?

A) Proxy is faster  
B) Proxy controls access; Decorator adds functionality  
C) Decorator is for inheritance  
D) There is no difference  

**Answer**: B  
**Explanation**: The Proxy pattern controls access to another object (lazy loading, access control), while the Decorator pattern adds new functionality to an object.

---

### Q18: Observer Memory Leaks
**Question**: What is a potential issue with the Observer pattern?

A) It uses too much memory  
B) Observers that are not unsubscribed can cause memory leaks  
C) It doesn't support multiple observers  
D) It's not thread-safe  

**Answer**: B  
**Explanation**: If observers are not properly unsubscribed, they can remain in memory even after they're no longer needed, causing memory leaks.

---

### Q19: Chain of Responsibility
**Question**: What is the Chain of Responsibility pattern used for?

A) Creating objects  
B) Passing requests along a chain of handlers  
C) Managing object state  
D) Handling exceptions  

**Answer**: B  
**Explanation**: The Chain of Responsibility pattern passes requests along a chain of handlers, where each handler decides whether to process the request or pass it to the next handler.

---

### Q20: Template Method Pattern
**Question**: What does the Template Method pattern define?

A) The structure of a class  
B) The skeleton of an algorithm in a method, letting subclasses override specific steps  
C) How to create objects  
D) How to manage state  

**Answer**: B  
**Explanation**: The Template Method pattern defines the skeleton of an algorithm in a base class method, allowing subclasses to override specific steps without changing the algorithm's structure.

---

## 🟣 Expert Level (4 Questions)

### Q21: Pattern Composition
**Question**: In a complex application, you need to create a family of related objects (Abstract Factory), ensure single instances (Singleton), and add dynamic behavior (Decorator). How would you combine these patterns?

A) Use only one pattern  
B) Use Abstract Factory to create Singleton instances, then wrap with Decorators  
C) Use Singleton to create Abstract Factory, then use Decorators  
D) These patterns cannot be combined  

**Answer**: B  
**Explanation**: You can combine patterns by using Abstract Factory to create Singleton instances, then wrapping those instances with Decorators to add dynamic behavior.

---

### Q22: Anti-Pattern Recognition
**Question**: Which of the following is an anti-pattern?

A) Using design patterns appropriately  
B) Creating a "God Object" that handles too many responsibilities  
C) Separating concerns into different classes  
D) Using dependency injection  

**Answer**: B  
**Explanation**: A "God Object" that handles too many responsibilities is an anti-pattern. It violates the Single Responsibility Principle and makes code harder to maintain and test.

---

### Q23: Pattern Selection Complexity
**Question**: You're designing a system where different payment methods (credit card, PayPal, cryptocurrency) need to be supported, and the payment method can change at runtime. Which pattern is most appropriate?

A) Singleton  
B) Factory  
C) Strategy  
D) Observer  

**Answer**: C  
**Explanation**: The Strategy pattern is ideal here because it allows you to encapsulate different payment algorithms and switch between them at runtime.

---

### Q24: Architectural Pattern Trade-offs
**Question**: What is a potential drawback of using the Repository pattern?

A) It makes code slower  
B) It adds an extra layer of abstraction that might be unnecessary for simple applications  
C) It prevents the use of databases  
D) It eliminates the need for testing  

**Answer**: B  
**Explanation**: While the Repository pattern provides excellent abstraction and testability, it adds an extra layer that might be overkill for simple applications with straightforward data access needs.

---

## 📊 Quiz Statistics

| Difficulty | Count | Percentage |
|-----------|-------|-----------|
| Beginner | 6 | 25% |
| Intermediate | 8 | 33% |
| Advanced | 6 | 25% |
| Expert | 4 | 17% |

---

## 🎯 Scoring Guide

- **20-24 correct**: Expert level mastery
- **16-19 correct**: Advanced understanding
- **12-15 correct**: Intermediate proficiency
- **8-11 correct**: Beginner foundation
- **Below 8**: Review recommended

---

**Module 11 - Design Patterns Quizzes**  
*Test your understanding of proven design solutions*