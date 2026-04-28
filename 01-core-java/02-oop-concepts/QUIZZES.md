# 📝 OOP Concepts - Quizzes & Practice Questions

## Table of Contents
1. [Beginner Quizzes](#beginner-quizzes)
2. [Intermediate Quizzes](#intermediate-quizzes)
3. [Advanced Quizzes](#advanced-quizzes)
4. [Interview Tricky Questions](#interview-tricky-questions)

---

## Beginner Quizzes

### Q1: Class vs Object
What is the difference between a class and an object?

```java
public class Car {
    private String color;
    private String model;
    
    public Car(String color, String model) {
        this.color = color;
        this.model = model;
    }
}

// Which statement is correct?
A) Car is an object, car1 is a class
B) Car is a class, car1 is an object
C) Both Car and car1 are classes
D) Both Car and car1 are objects

Car car1 = new Car("red", "Tesla");
Car car2 = new Car("blue", "BMW");
```

**Answer**: B

**Explanation**:
- **Class**: Blueprint/template (Car) - defines structure
- **Object**: Instance of a class (car1, car2) - actual data
- car1 and car2 are different objects with different states
- Both car1 and car2 are instances of the Car class

**Key Lesson**: A class is like a cookie cutter, objects are the actual cookies

---

### Q2: Constructor Purpose
What does a constructor do?

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

Person p = new Person("Alice", 30);
```

**Answer**: A constructor initializes an object's state when it's created

**Explanation**:
- Constructors run automatically when `new` is called
- They set initial values for fields
- They guarantee the object is in a valid state
- If no constructor is defined, Java provides a default one
- Constructors have the same name as the class
- Constructors don't have a return type

**Why It Matters**: Without constructors, objects could be created in invalid states

---

### Q3: Encapsulation Principle
What is the main purpose of encapsulation?

```java
public class BankAccount {
    private double balance;  // Private
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() {
        return balance;
    }
}

// vs

public class BadAccount {
    public double balance;  // Public - BAD!
}
```

**Answer**: To hide internal details and control access to data

**Explanation**:
- **Encapsulation** = bundling data + methods + controlling access
- **Private fields**: Hide implementation details
- **Public methods**: Provide controlled interface
- **Validation**: Setters can validate data before accepting it
- **Flexibility**: Can change internal implementation without affecting users

**Real-World Impact**:
```java
// With encapsulation
account.deposit(-1000);  // Rejected by validation

// Without encapsulation
badAccount.balance = -1000;  // Accepted! Invalid state
```

---

### Q4: Inheritance and super()
What does `super()` do?

```java
public class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
        System.out.println("Animal constructor");
    }
}

public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, String breed) {
        super(name);  // What does this do?
        this.breed = breed;
        System.out.println("Dog constructor");
    }
}

Dog dog = new Dog("Buddy", "Golden");
```

**Answer**: `super(name)` calls the parent class constructor

**Output**:
```
Animal constructor
Dog constructor
```

**Explanation**:
- `super()` must be the first statement in a constructor
- It calls the parent class's constructor
- Ensures parent class is properly initialized
- If you don't call `super()`, Java calls the default parent constructor automatically
- You can only call `super()` once per constructor

**Why It Matters**: Parent class fields must be initialized before child class uses them

---

### Q5: Method Overriding
What is method overriding?

```java
public class Animal {
    public void makeSound() {
        System.out.println("Some sound");
    }
}

public class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }
}

Animal animal = new Dog();
animal.makeSound();  // Output: ?
```

**Answer**: `Woof!`

**Explanation**:
- **Overriding**: Child class provides its own implementation of parent's method
- Same method name, same parameters
- `@Override` annotation helps catch mistakes
- The actual object type determines which method runs (Dog)
- This is **polymorphism** in action

**Key Difference**:
```java
// OVERLOADING (same class, different parameters)
public void makeSound() { }
public void makeSound(int volume) { }

// OVERRIDING (parent vs child, same signature)
// Parent: public void makeSound() { }
// Child: public void makeSound() { }  // Override
```

---

## Intermediate Quizzes

### Q6: Access Modifiers
What is printed?

```java
public class Parent {
    public int pub = 1;
    protected int prot = 2;
    int pkg = 3;
    private int priv = 4;
}

public class Child extends Parent {
    public void test() {
        System.out.println(pub);    // ?
        System.out.println(prot);   // ?
        System.out.println(pkg);    // ?
        System.out.println(priv);   // ?
    }
}
```

**Output**:
```
1
2
3
Compile error: priv has private access
```

**Explanation**:
- **public**: Accessible everywhere
- **protected**: Accessible in same package + subclasses
- **package-private** (default): Accessible in same package only
- **private**: Accessible only in the class itself

**Visibility from Child Class**:
- ✅ public: Yes
- ✅ protected: Yes (inherited)
- ✅ package-private: Yes (if same package)
- ❌ private: No (not inherited)

---

### Q7: Polymorphic Collections
What is printed?

```java
public abstract class Shape {
    public abstract double getArea();
}

public class Circle extends Shape {
    private double radius;
    public Circle(double radius) { this.radius = radius; }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width, height;
    public Rectangle(double w, double h) { width = w; height = h; }
    
    @Override
    public double getArea() {
        return width * height;
    }
}

List<Shape> shapes = new ArrayList<>();
shapes.add(new Circle(5));
shapes.add(new Rectangle(4, 6));

double total = 0;
for (Shape shape : shapes) {
    total += shape.getArea();
}

System.out.println((int)total);  // ?
```

**Answer**: `103` (approximately)

**Calculation**:
- Circle area: π × 5² ≈ 78.54
- Rectangle area: 4 × 6 = 24
- Total: 78.54 + 24 = 102.54 → (int) = 102

**Explanation**:
- List holds parent type (Shape)
- Each element is actually a different child type
- `getArea()` calls the correct overridden method
- This is **polymorphism**: same interface, different implementations

---

### Q8: Type Casting
What happens?

```java
Animal animal = new Dog();

// Option A
Dog dog = (Dog) animal;  // ?

// Option B
Cat cat = (Cat) animal;  // ?

// Option C
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;  // ?
}
```

**Answer**:
- A: ✅ Works (animal is actually a Dog)
- B: ❌ ClassCastException (animal is Dog, not Cat)
- C: ✅ Works (checked with instanceof first)

**Explanation**:
- **Upcasting** (Dog → Animal): Always safe, implicit
- **Downcasting** (Animal → Dog): Unsafe, requires explicit cast
- **instanceof**: Check type before downcasting
- **ClassCastException**: Thrown if cast is invalid

**Safe Pattern**:
```java
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;  // Safe
    dog.bark();
}
```

---

### Q9: Abstract Classes
Which statement is correct?

```java
public abstract class Vehicle {
    public abstract void start();
    
    public void stop() {
        System.out.println("Vehicle stopped");
    }
}

A) Vehicle v = new Vehicle();  // Create instance
B) Vehicle v = new Car();      // Car extends Vehicle
C) Abstract classes cannot have concrete methods
D) All methods in abstract classes must be abstract
```

**Answer**: B

**Explanation**:
- ❌ A: Cannot instantiate abstract classes directly
- ✅ B: Can create reference to abstract class, pointing to concrete subclass
- ❌ C: Abstract classes CAN have concrete methods
- ❌ D: Abstract classes can mix abstract and concrete methods

**Abstract Class Rules**:
- Cannot be instantiated with `new`
- Can be used as a reference type
- Subclasses must implement all abstract methods
- Can have concrete methods (shared implementation)
- Can have instance variables

---

### Q10: Interface Implementation
What is printed?

```java
public interface Drawable {
    void draw();
    
    default void erase() {
        System.out.println("Erasing");
    }
}

public class Circle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing circle");
    }
}

Circle circle = new Circle();
circle.draw();    // ?
circle.erase();   // ?
```

**Output**:
```
Drawing circle
Erasing
```

**Explanation**:
- **Abstract method** (draw): Must be implemented
- **Default method** (erase): Inherited from interface, can be overridden
- Default methods provide shared implementation
- Interfaces support multiple inheritance of type

---

## Advanced Quizzes

### Q11: Liskov Substitution Principle
Which violates LSP?

```java
public class Bird {
    public void fly() {
        System.out.println("Flying");
    }
}

// Option A: Correct
public class Eagle extends Bird {
    @Override
    public void fly() {
        System.out.println("Eagle flying high");
    }
}

// Option B: Violates LSP
public class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Penguins cannot fly");
    }
}

// Usage
Bird bird = new Penguin();
bird.fly();  // Throws exception!
```

**Answer**: Option B violates LSP

**Explanation**:
- **Liskov Substitution Principle**: Subclass should be usable wherever parent is used
- Penguin breaks the contract: it's a Bird but can't fly
- This violates the IS-A relationship
- Better design: Separate Flyable interface

**Better Design**:
```java
public interface Flyable {
    void fly();
}

public class Eagle implements Flyable {
    public void fly() { }
}

public class Penguin {  // Doesn't implement Flyable
    public void swim() { }
}
```

---

### Q12: Diamond Problem with Interfaces
What is printed?

```java
public interface A {
    default void method() {
        System.out.println("A");
    }
}

public interface B extends A {
    @Override
    default void method() {
        System.out.println("B");
    }
}

public interface C extends A {
    @Override
    default void method() {
        System.out.println("C");
    }
}

public class D implements B, C {
    @Override
    public void method() {
        System.out.println("D");
    }
}

D d = new D();
d.method();  // ?
```

**Output**: `D`

**Explanation**:
- D implements both B and C (both have default method)
- D must provide its own implementation to resolve ambiguity
- If D didn't override, compile error: "Ambiguous method"
- Can call parent implementations with `B.super.method()`

---

### Q13: Constructor Chaining
What is printed?

```java
public class A {
    public A() {
        System.out.println("A");
    }
}

public class B extends A {
    public B() {
        super();
        System.out.println("B");
    }
}

public class C extends B {
    public C() {
        super();
        System.out.println("C");
    }
}

C c = new C();
```

**Output**:
```
A
B
C
```

**Explanation**:
- C() calls super() → B()
- B() calls super() → A()
- A() executes first (no parent)
- Then B() continues
- Then C() continues
- Execution order: A → B → C

---

### Q14: Method Overloading vs Overriding
What is printed?

```java
public class Parent {
    public void method(int x) {
        System.out.println("Parent int");
    }
}

public class Child extends Parent {
    public void method(int x) {  // Override
        System.out.println("Child int");
    }
    
    public void method(double x) {  // Overload
        System.out.println("Child double");
    }
}

Child child = new Child();
child.method(5);      // ?
child.method(5.0);    // ?

Parent parent = new Child();
parent.method(5);     // ?
parent.method(5.0);   // ?
```

**Output**:
```
Child int
Child double
Child int
Parent int
```

**Explanation**:
- `child.method(5)`: Exact match → Child.method(int)
- `child.method(5.0)`: Exact match → Child.method(double)
- `parent.method(5)`: Polymorphic → Child.method(int)
- `parent.method(5.0)`: No override for double → Parent.method(int) with widening

---

### Q15: Immutability
Which class is truly immutable?

```java
// Option A
public class Immutable1 {
    private final String name;
    
    public Immutable1(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}

// Option B
public class Immutable2 {
    private final List<String> items;
    
    public Immutable2(List<String> items) {
        this.items = items;
    }
    
    public List<String> getItems() {
        return items;
    }
}
```

**Answer**: Option A is immutable, Option B is not

**Explanation**:
- Option A: String is immutable, no way to change it
- Option B: List is mutable, caller can modify it:
  ```java
  Immutable2 obj = new Immutable2(list);
  obj.getItems().add("new item");  // Oops! Modified!
  ```

**Fix for Option B**:
```java
public List<String> getItems() {
    return Collections.unmodifiableList(items);
}

// Or
public List<String> getItems() {
    return new ArrayList<>(items);  // Return copy
}
```

---

## Interview Tricky Questions

### Q16: What's Wrong With This Code?
```java
public class Parent {
    public void method() {
        System.out.println("Parent");
    }
}

public class Child extends Parent {
    public void method(int x) {  // What's wrong?
        System.out.println("Child");
    }
}

Parent p = new Child();
p.method();  // Output: ?
```

**Answer**: Output is `Parent`, not `Child`

**The Problem**:
- Child.method(int) is **overloading**, not **overriding**
- Parent.method() still exists and is called
- This is likely a bug (forgot the parameter)

**Fix**:
```java
public class Child extends Parent {
    @Override  // This would cause compile error if signature wrong
    public void method() {  // Remove parameter
        System.out.println("Child");
    }
}
```

**Lesson**: Use `@Override` annotation to catch these mistakes!

---

### Q17: Shallow vs Deep Copy
What is printed?

```java
public class Person {
    public String name;
    public Address address;
    
    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }
}

public class Address {
    public String city;
    
    public Address(String city) {
        this.city = city;
    }
}

Address addr = new Address("NYC");
Person p1 = new Person("Alice", addr);
Person p2 = p1;  // Shallow copy

p2.name = "Bob";
p2.address.city = "LA";

System.out.println(p1.name);           // ?
System.out.println(p1.address.city);   // ?
```

**Output**:
```
Alice
LA
```

**Explanation**:
- `p2 = p1` creates a shallow copy (same references)
- p1 and p2 point to the same Address object
- Changing p2.address.city affects p1.address.city
- Changing p2.name doesn't affect p1.name (String is immutable)

**Deep Copy Solution**:
```java
Person p2 = new Person(p1.name, new Address(p1.address.city));
p2.address.city = "LA";  // Doesn't affect p1
```

---

### Q18: Static vs Instance
What is printed?

```java
public class Counter {
    public static int staticCount = 0;
    public int instanceCount = 0;
    
    public void increment() {
        staticCount++;
        instanceCount++;
    }
}

Counter c1 = new Counter();
Counter c2 = new Counter();

c1.increment();
c2.increment();

System.out.println(c1.staticCount);      // ?
System.out.println(c1.instanceCount);    // ?
System.out.println(c2.staticCount);      // ?
System.out.println(c2.instanceCount);    // ?
```

**Output**:
```
2
1
2
1
```

**Explanation**:
- **staticCount**: Shared by all instances (class variable)
- **instanceCount**: Each instance has its own (instance variable)
- c1.increment() → staticCount=1, c1.instanceCount=1
- c2.increment() → staticCount=2, c2.instanceCount=1
- Both c1 and c2 see staticCount=2 (shared)

---

### Q19: Inheritance and Field Shadowing
What is printed?

```java
public class Parent {
    public String value = "Parent";
    
    public void display() {
        System.out.println(value);
    }
}

public class Child extends Parent {
    public String value = "Child";  // Shadows parent's field
    
    @Override
    public void display() {
        System.out.println(value);
    }
}

Parent p = new Child();
System.out.println(p.value);   // ?
p.display();                   // ?
```

**Output**:
```
Parent
Child
```

**Explanation**:
- `p.value`: Reference type is Parent → accesses Parent.value
- `p.display()`: Object type is Child → calls Child.display()
- Child.display() accesses Child.value
- This is confusing! Avoid field shadowing

**Better Design**:
```java
public class Parent {
    protected String value = "Parent";  // Use protected
}

public class Child extends Parent {
    // Don't shadow, just use parent's field
    
    @Override
    public void display() {
        System.out.println(value);  // Uses parent's field
    }
}
```

---

### Q20: Abstract Class vs Interface
When should you use each?

```java
// Abstract Class: Shared implementation
public abstract class Animal {
    protected String name;  // Instance variable
    
    public Animal(String name) {  // Constructor
        this.name = name;
    }
    
    public void eat() {  // Concrete method
        System.out.println(name + " eating");
    }
    
    public abstract void makeSound();  // Abstract method
}

// Interface: Contract only
public interface Drawable {
    void draw();
    
    default void erase() {  // Default method (Java 8+)
        System.out.println("Erasing");
    }
}
```

**When to Use**:
- **Abstract Class**: Related classes sharing code (IS-A)
- **Interface**: Unrelated classes with common capability (CAN-DO)

**Example**:
```java
// IS-A: Dog IS-A Animal
public class Dog extends Animal { }

// CAN-DO: Dog CAN-DO Drawable
public class Dog extends Animal implements Drawable { }
```

---

### Q21: Method Resolution with Multiple Inheritance
What is printed?

```java
public interface A {
    default void method() {
        System.out.println("A");
    }
}

public interface B {
    default void method() {
        System.out.println("B");
    }
}

public class C implements A, B {
    // Must override to resolve ambiguity
    @Override
    public void method() {
        A.super.method();  // Call A's default
        B.super.method();  // Call B's default
    }
}

C c = new C();
c.method();  // ?
```

**Output**:
```
A
B
```

**Explanation**:
- C implements both A and B (both have default method)
- Must override to resolve ambiguity
- Can call parent implementations with `Interface.super.method()`
- This is the "diamond problem" solution

---

### Q22: Polymorphism and Type Safety
What is the best design?

```java
// ❌ BAD: Type checking everywhere
public void processShape(Object obj) {
    if (obj instanceof Circle) {
        Circle c = (Circle) obj;
        c.draw();
    } else if (obj instanceof Rectangle) {
        Rectangle r = (Rectangle) obj;
        r.draw();
    }
}

// ✅ GOOD: Polymorphism
public void processShape(Shape shape) {
    shape.draw();  // Works for any Shape
}

// ✅ BETTER: Generic
public <T extends Shape> void processShape(T shape) {
    shape.draw();
}
```

**Why Polymorphism is Better**:
- Type-safe at compile time
- No casting needed
- Extensible (new shapes work automatically)
- Cleaner, more maintainable code

---

## Answer Summary

| Q# | Answer | Key Concept |
|----|--------|------------|
| 1 | B | Class vs Object |
| 2 | Initializes object state | Constructor purpose |
| 3 | Hide details, control access | Encapsulation |
| 4 | Calls parent constructor | super() keyword |
| 5 | Woof! | Method overriding |
| 6 | 1,2,3, error | Access modifiers |
| 7 | 102 | Polymorphic collections |
| 8 | Works, Exception, Works | Type casting |
| 9 | B | Abstract classes |
| 10 | Drawing circle, Erasing | Interface defaults |
| 11 | Option B | Liskov Substitution |
| 12 | D | Diamond problem |
| 13 | A, B, C | Constructor chaining |
| 14 | Child int, Child double, Child int, Parent int | Overloading vs Overriding |
| 15 | Option A | Immutability |
| 16 | Parent | @Override annotation |
| 17 | Alice, LA | Shallow vs deep copy |
| 18 | 2,1,2,1 | Static vs instance |
| 19 | Parent, Child | Field shadowing |
| 20 | Abstract for IS-A, Interface for CAN-DO | Design choice |
| 21 | A, B | Multiple inheritance |
| 22 | Polymorphism | Type safety |

---

**Next**: Study EDGE_CASES.md to learn from real bugs!