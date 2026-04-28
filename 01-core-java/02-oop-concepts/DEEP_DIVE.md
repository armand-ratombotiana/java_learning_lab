# 🔍 OOP Concepts - Deep Dive

## Table of Contents
1. [Classes & Objects Fundamentals](#classes--objects-fundamentals)
2. [Encapsulation Deep Dive](#encapsulation-deep-dive)
3. [Inheritance Mechanics](#inheritance-mechanics)
4. [Polymorphism in Detail](#polymorphism-in-detail)
5. [Abstraction Patterns](#abstraction-patterns)
6. [Interface Design](#interface-design)

---

## Classes & Objects Fundamentals

### What is a Class?

A **class** is a blueprint or template for creating objects. It defines:
- **State** (data/fields): What the object knows
- **Behavior** (methods): What the object can do
- **Identity**: Each object is unique

### Class Anatomy

```java
public class Person {
    // 1. FIELDS (State)
    private String name;           // Instance variable
    private int age;
    private static int totalPeople = 0;  // Class variable
    
    // 2. CONSTRUCTORS (Initialization)
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        totalPeople++;
    }
    
    // 3. METHODS (Behavior)
    public void displayInfo() {
        System.out.println(name + " is " + age);
    }
    
    // 4. GETTERS/SETTERS (Controlled Access)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
```

### Memory Layout: Stack vs Heap

```
┌─────────────────────────────────────────────────────────┐
│                        MEMORY                           │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  STACK (Method calls, local variables)                 │
│  ┌──────────────────────────────────────────────────┐  │
│  │ main() method frame                              │  │
│  │  person1 ──────────────┐                         │  │
│  │  person2 ──────────┐   │                         │  │
│  └──────────────────────────────────────────────────┘  │
│                       │   │                             │
│  HEAP (Objects)       │   │                             │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Person@1a2b3c4d  ◄─┘   │                         │  │
│  │  name: "Alice"         │                         │  │
│  │  age: 30               │                         │  │
│  │                        │                         │  │
│  │ Person@5e6f7g8h  ◄─────┘                         │  │
│  │  name: "Bob"                                     │  │
│  │  age: 25                                         │  │
│  └──────────────────────────────────────────────────┘  │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Key Points**:
- **Stack**: Stores references (memory addresses)
- **Heap**: Stores actual objects
- **Garbage Collection**: Removes unreferenced objects from heap
- **Reference**: A variable that points to an object

### Object Lifecycle

```
1. DECLARATION
   Person person;  // Reference created on stack, null value

2. INSTANTIATION
   person = new Person("Alice", 30);  // Object created on heap

3. INITIALIZATION
   Constructor runs, fields set to initial values

4. USAGE
   person.displayInfo();  // Methods called

5. GARBAGE COLLECTION
   person = null;  // Reference removed, object eligible for GC
   // Object removed from heap when no references exist
```

### Constructors: The Initialization Contract

```java
public class Person {
    private String name;
    private int age;
    
    // 1. DEFAULT CONSTRUCTOR (no parameters)
    public Person() {
        this.name = "Unknown";
        this.age = 0;
    }
    
    // 2. PARAMETERIZED CONSTRUCTOR
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // 3. COPY CONSTRUCTOR
    public Person(Person other) {
        this.name = other.name;
        this.age = other.age;
    }
}

// Usage
Person p1 = new Person();              // Default
Person p2 = new Person("Alice", 30);   // Parameterized
Person p3 = new Person(p2);            // Copy
```

**Why Constructors Matter**:
- Guarantee object is in valid state
- Initialize all required fields
- Prevent partially constructed objects
- Enable different creation patterns

### Instance vs Static Variables

```java
public class Counter {
    private int instanceCount = 0;      // Each object has its own
    private static int totalCount = 0;  // Shared by all objects
    
    public void increment() {
        instanceCount++;
        totalCount++;
    }
    
    public static void main(String[] args) {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        
        c1.increment();  // c1.instanceCount=1, totalCount=1
        c2.increment();  // c2.instanceCount=1, totalCount=2
        
        System.out.println(c1.instanceCount);  // 1
        System.out.println(c2.instanceCount);  // 1
        System.out.println(Counter.totalCount); // 2
    }
}
```

**Memory Visualization**:
```
┌─────────────────────────────────────────┐
│ Counter Class (Shared)                  │
│  static totalCount = 2                  │
└─────────────────────────────────────────┘
         ▲                    ▲
         │                    │
    ┌────┴──────┐        ┌────┴──────┐
    │ c1 object │        │ c2 object │
    │ instance  │        │ instance  │
    │ Count=1   │        │ Count=1   │
    └───────────┘        └───────────┘
```

---

## Encapsulation Deep Dive

### The Encapsulation Principle

**Encapsulation** = Bundling data + methods + controlling access

```
┌─────────────────────────────────────┐
│         BankAccount                 │
├─────────────────────────────────────┤
│ PRIVATE (Hidden)                    │
│  - balance: double                  │
│  - accountNumber: String            │
│  - transactions: List               │
├─────────────────────────────────────┤
│ PUBLIC (Controlled Access)          │
│  + deposit(amount)                  │
│  + withdraw(amount)                 │
│  + getBalance()                     │
│  + transfer(to, amount)             │
└─────────────────────────────────────┘
```

### Access Modifiers: The Visibility Spectrum

```java
public class AccessModifierDemo {
    
    // PUBLIC: Accessible everywhere
    public int publicField = 1;
    
    // PROTECTED: Accessible in same package + subclasses
    protected int protectedField = 2;
    
    // PACKAGE-PRIVATE (default): Accessible in same package only
    int packagePrivateField = 3;
    
    // PRIVATE: Accessible only in this class
    private int privateField = 4;
}
```

**Visibility Matrix**:
```
                 | Same Class | Same Package | Subclass | Other
─────────────────┼────────────┼──────────────┼──────────┼──────
public           |     ✅     |      ✅      |    ✅    |  ✅
protected        |     ✅     |      ✅      |    ✅    |  ❌
package-private  |     ✅     |      ✅      |    ❌    |  ❌
private          |     ✅     |      ❌      |    ❌    |  ❌
```

### The Getter/Setter Pattern

```java
public class BankAccount {
    private double balance;
    
    // GETTER: Read-only access
    public double getBalance() {
        return balance;
    }
    
    // SETTER: Controlled write access with validation
    public void setBalance(double newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = newBalance;
    }
    
    // BUSINESS METHOD: Complex operation
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        this.balance += amount;
        logTransaction("DEPOSIT", amount);
    }
}
```

**Why Not Public Fields?**

```java
// ❌ BAD: Direct access, no validation
public class BadAccount {
    public double balance;  // Anyone can set to negative!
}

BadAccount bad = new BadAccount();
bad.balance = -1000;  // Oops! Invalid state

// ✅ GOOD: Controlled access with validation
public class GoodAccount {
    private double balance;
    
    public void setBalance(double amount) {
        if (amount < 0) throw new IllegalArgumentException();
        this.balance = amount;
    }
}

GoodAccount good = new GoodAccount();
good.setBalance(-1000);  // Exception! Invalid state prevented
```

### Data Validation: The Guardian Pattern

```java
public class Person {
    private String name;
    private int age;
    private String email;
    
    public void setName(String name) {
        // Validation: Not null, not empty
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
    
    public void setAge(int age) {
        // Validation: Reasonable range
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be 0-150");
        }
        this.age = age;
    }
    
    public void setEmail(String email) {
        // Validation: Email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }
}
```

### Immutability: The Ultimate Encapsulation

```java
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    // Constructor: Only way to set values
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Getters only, no setters
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }
    
    // No modification methods
    // Once created, cannot change
}

// Usage
ImmutablePerson person = new ImmutablePerson("Alice", 30);
// person.name = "Bob";  // Compile error! final field
// person.setName("Bob"); // No such method!
```

**Benefits of Immutability**:
- ✅ Thread-safe (no synchronization needed)
- ✅ Can be shared freely
- ✅ Predictable behavior
- ✅ Can be cached
- ✅ Hashable (safe for HashMap keys)

---

## Inheritance Mechanics

### The IS-A Relationship

```
┌──────────────────────────────────────────────────────┐
│                    Animal (Parent)                   │
│  ┌────────────────────────────────────────────────┐  │
│  │ Fields: name, age                             │  │
│  │ Methods: eat(), sleep(), makeSound()          │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
         ▲                              ▲
         │ IS-A                         │ IS-A
         │                              │
    ┌────┴──────────┐            ┌─────┴──────────┐
    │ Dog (Child)   │            │ Cat (Child)    │
    │ ┌──────────┐  │            │ ┌──────────┐   │
    │ │ breed    │  │            │ │ color    │   │
    │ │ bark()   │  │            │ │ meow()   │   │
    │ └──────────┘  │            │ └──────────┘   │
    └───────────────┘            └────────────────┘
```

### Constructor Chaining with super()

```java
public class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("Animal constructor called");
    }
}

public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor FIRST
        this.breed = breed;
        System.out.println("Dog constructor called");
    }
}

// Execution order:
Dog dog = new Dog("Buddy", 5, "Golden Retriever");
// Output:
// Animal constructor called
// Dog constructor called
```

**Constructor Execution Flow**:
```
1. Dog constructor called
   ↓
2. super(name, age) → Animal constructor
   ├─ Set name
   ├─ Set age
   └─ Print "Animal constructor called"
   ↓
3. Back to Dog constructor
   ├─ Set breed
   └─ Print "Dog constructor called"
```

### Method Overriding: Customizing Behavior

```java
public class Animal {
    public void makeSound() {
        System.out.println("Some generic sound");
    }
}

public class Dog extends Animal {
    @Override  // Annotation: Explicitly override
    public void makeSound() {
        System.out.println("Woof! Woof!");
    }
}

public class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}

// Usage
Animal dog = new Dog();
Animal cat = new Cat();

dog.makeSound();  // Output: Woof! Woof!
cat.makeSound();  // Output: Meow!
```

**Overriding Rules**:
```
✅ VALID OVERRIDES:
  - Same method name
  - Same parameters
  - Same or covariant return type
  - Same or less restrictive access modifier
  - Can throw same or fewer exceptions

❌ INVALID OVERRIDES:
  - Different parameters (that's overloading)
  - More restrictive access modifier
  - Throws more checked exceptions
  - Changes return type incompatibly
```

### Method Resolution Order (MRO)

```java
public class Animal {
    public void display() {
        System.out.println("I am an Animal");
    }
}

public class Dog extends Animal {
    @Override
    public void display() {
        System.out.println("I am a Dog");
    }
}

// Method resolution
Dog dog = new Dog();
dog.display();  // Calls Dog.display() - DYNAMIC BINDING

Animal animal = new Dog();  // Reference is Animal, object is Dog
animal.display();  // Still calls Dog.display() - POLYMORPHIC!
```

**Resolution Process**:
```
1. Check actual object type (Dog)
2. Look for method in Dog class
3. If not found, look in parent (Animal)
4. If not found, look in grandparent, etc.
5. If still not found, compile error
```

### The super Keyword

```java
public class Animal {
    public void eat() {
        System.out.println("Animal eating");
    }
}

public class Dog extends Animal {
    @Override
    public void eat() {
        super.eat();  // Call parent's eat()
        System.out.println("Dog eating dog food");
    }
}

// Usage
Dog dog = new Dog();
dog.eat();
// Output:
// Animal eating
// Dog eating dog food
```

---

## Polymorphism in Detail

### Compile-Time vs Runtime Polymorphism

```java
// COMPILE-TIME POLYMORPHISM (Method Overloading)
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
}

// Compiler decides which method to call
Calculator calc = new Calculator();
calc.add(5, 10);           // Calls add(int, int)
calc.add(5.5, 10.5);       // Calls add(double, double)
calc.add(5, 10, 15);       // Calls add(int, int, int)
```

```java
// RUNTIME POLYMORPHISM (Method Overriding)
public abstract class Shape {
    public abstract double calculateArea();
}

public class Circle extends Shape {
    private double radius;
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    
    @Override
    public double calculateArea() {
        return width * height;
    }
}

// Runtime decides which method to call
Shape shape1 = new Circle(5);
Shape shape2 = new Rectangle(4, 6);

shape1.calculateArea();  // Calls Circle.calculateArea()
shape2.calculateArea();  // Calls Rectangle.calculateArea()
```

### Polymorphic Collections

```java
// Create collection of parent type
List<Shape> shapes = new ArrayList<>();

// Add different child types
shapes.add(new Circle(5));
shapes.add(new Rectangle(4, 6));
shapes.add(new Triangle(3, 4, 5));

// Iterate and call overridden methods
double totalArea = 0;
for (Shape shape : shapes) {
    totalArea += shape.calculateArea();  // Polymorphic call
}

System.out.println("Total area: " + totalArea);
```

**Polymorphic Behavior**:
```
shapes[0] → Circle object → calculateArea() → π * r²
shapes[1] → Rectangle object → calculateArea() → w * h
shapes[2] → Triangle object → calculateArea() → (b * h) / 2
```

### Type Casting and instanceof

```java
public class AnimalDemo {
    public static void main(String[] args) {
        Animal animal = new Dog();
        
        // Check type before casting
        if (animal instanceof Dog) {
            Dog dog = (Dog) animal;  // Safe cast
            dog.bark();
        } else if (animal instanceof Cat) {
            Cat cat = (Cat) animal;
            cat.meow();
        }
    }
}
```

**Casting Hierarchy**:
```
UPCASTING (Safe, implicit)
Dog dog = new Dog();
Animal animal = dog;  // Always safe

DOWNCASTING (Unsafe, explicit)
Animal animal = new Dog();
Dog dog = (Dog) animal;  // Safe if animal is actually Dog
Cat cat = (Cat) animal;  // ClassCastException if animal is Dog!
```

---

## Abstraction Patterns

### Abstract Classes: Partial Implementation

```java
public abstract class Vehicle {
    // Concrete method: shared implementation
    public void displayInfo() {
        System.out.println("This is a vehicle");
    }
    
    // Abstract method: must be implemented by subclasses
    public abstract void start();
    public abstract void stop();
    public abstract int getMaxSpeed();
}

public class Car extends Vehicle {
    @Override
    public void start() {
        System.out.println("Car engine started");
    }
    
    @Override
    public void stop() {
        System.out.println("Car engine stopped");
    }
    
    @Override
    public int getMaxSpeed() {
        return 200;  // km/h
    }
}

// Usage
// Vehicle v = new Vehicle();  // Compile error! Cannot instantiate abstract class
Vehicle car = new Car();  // OK! Car is concrete
car.start();
```

**Abstract Class Characteristics**:
- ✅ Can have concrete methods
- ✅ Can have abstract methods
- ✅ Can have instance variables
- ✅ Can have constructors
- ❌ Cannot be instantiated directly
- ❌ Subclasses must implement all abstract methods

### Template Method Pattern

```java
public abstract class DataProcessor {
    // Template method: defines algorithm structure
    public final void process(String data) {
        String validated = validate(data);
        String transformed = transform(validated);
        save(transformed);
    }
    
    // Abstract methods: subclasses provide implementation
    protected abstract String validate(String data);
    protected abstract String transform(String data);
    protected abstract void save(String data);
}

public class CSVProcessor extends DataProcessor {
    @Override
    protected String validate(String data) {
        // CSV-specific validation
        return data;
    }
    
    @Override
    protected String transform(String data) {
        // CSV-specific transformation
        return data;
    }
    
    @Override
    protected void save(String data) {
        // Save to CSV file
    }
}
```

---

## Interface Design

### Interfaces: Pure Contracts

```java
public interface Flyable {
    // Abstract methods (implicitly public abstract)
    void fly();
    void land();
    int getMaxAltitude();
    
    // Default method (Java 8+)
    default void displayFlightInfo() {
        System.out.println("Max altitude: " + getMaxAltitude());
    }
    
    // Static method (Java 8+)
    static void printFlightRules() {
        System.out.println("Follow air traffic control rules");
    }
}

public class Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("Bird flying with wings");
    }
    
    @Override
    public void land() {
        System.out.println("Bird landing on branch");
    }
    
    @Override
    public int getMaxAltitude() {
        return 5000;  // meters
    }
}

public class Airplane implements Flyable {
    @Override
    public void fly() {
        System.out.println("Airplane flying with engines");
    }
    
    @Override
    public void land() {
        System.out.println("Airplane landing on runway");
    }
    
    @Override
    public int getMaxAltitude() {
        return 12000;  // meters
    }
}
```

### Multiple Interface Implementation

```java
public interface Swimmable {
    void swim();
}

public interface Flyable {
    void fly();
}

public class Duck implements Swimmable, Flyable {
    @Override
    public void swim() {
        System.out.println("Duck swimming");
    }
    
    @Override
    public void fly() {
        System.out.println("Duck flying");
    }
}

// Usage
Duck duck = new Duck();
duck.swim();  // Duck swimming
duck.fly();   // Duck flying

// Polymorphic usage
Swimmable swimmer = duck;
Flyable flyer = duck;
```

### Interface vs Abstract Class

```
                    | Interface | Abstract Class
────────────────────┼───────────┼────────────────
Multiple inheritance|    ✅     |      ❌
Instance variables  |    ❌     |      ✅
Constructors        |    ❌     |      ✅
Concrete methods    |    ✅*    |      ✅
Abstract methods    |    ✅     |      ✅
Access modifiers    |   public  |   any modifier

* Default methods (Java 8+)
```

**When to Use**:
- **Interface**: Define a contract/capability
- **Abstract Class**: Share code among related classes

---

## Key Takeaways

### OOP Principles Summary

1. **Encapsulation**: Hide internal details, expose controlled interface
2. **Inheritance**: Reuse code through IS-A relationships
3. **Polymorphism**: Same interface, different implementations
4. **Abstraction**: Define contracts without implementation details

### Design Principles

- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Subclass can replace parent class
- **Interface Segregation**: Many specific interfaces > one general
- **Dependency Inversion**: Depend on abstractions, not concretions

### Common Mistakes to Avoid

1. ❌ Making everything public (breaks encapsulation)
2. ❌ Deep inheritance hierarchies (hard to maintain)
3. ❌ Violating Liskov Substitution Principle
4. ❌ Mixing concerns in one class
5. ❌ Ignoring interface contracts

---

**Next**: Study QUIZZES.md to test your understanding!