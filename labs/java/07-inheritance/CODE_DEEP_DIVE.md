# Inheritance — Code Deep Dive

## Example 1: Basic Inheritance

```java
// Superclass
class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    public String getName() { return name; }
}

// Subclass
class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Must call superclass constructor
        this.breed = breed;
    }
    
    // New method
    public void bark() {
        System.out.println(name + " says Woof!");
    }
    
    // Override
    @Override
    public void eat() {
        System.out.println(name + " the dog is eating kibble");
    }
}

class Cat extends Animal {
    public Cat(String name, int age) {
        super(name, age);
    }
    
    public void meow() {
        System.out.println(name + " says Meow!");
    }
    
    @Override
    public void eat() {
        System.out.println(name + " the cat is eating fish");
    }
    
    @Override
    public void sleep() {
        super.sleep();  // Call parent's version
        System.out.println(name + " is purring while sleeping");
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        Dog dog = new Dog("Buddy", 3, "Golden Retriever");
        Cat cat = new Cat("Whiskers", 2);
        
        dog.eat();    // Dog's override
        dog.bark();   // Dog-specific
        
        cat.eat();    // Cat's override
        cat.sleep();  // Cat's override (calls super.sleep() too)
        
        // Polymorphism
        Animal[] animals = {dog, cat};
        for (Animal a : animals) {
            a.eat();  // Correct version called based on actual type
        }
    }
}
```

## Example 2: The Object Class

```java
import java.util.Objects;

class Person implements Cloneable {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Override toString
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
    
    // Override equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }
    
    // Override hashCode (must match equals)
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
    
    // Override clone
    @Override
    public Person clone() {
        try {
            return (Person) super.clone();  // Shallow copy
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

public class ObjectDemo {
    public static void main(String[] args) {
        Person p1 = new Person("Alice", 30);
        Person p2 = new Person("Alice", 30);
        Person p3 = p1;
        
        System.out.println("p1: " + p1);  // toString
        System.out.println("p1 == p2: " + (p1 == p2));       // false (different refs)
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true (same content)
        System.out.println("p1.hashCode(): " + p1.hashCode());
        System.out.println("p2.hashCode(): " + p2.hashCode());
        
        // Clone
        Person p4 = p1.clone();
        System.out.println("p4: " + p4);
        System.out.println("p1.equals(p4): " + p1.equals(p4));
        System.out.println("p1 == p4: " + (p1 == p4));  // false (different refs)
    }
}
```

## Example 3: final Classes and Methods

```java
// final class — cannot be subclassed
final class ImmutablePoint {
    private final int x;
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}

// The following would not compile:
// class ExtendedPoint extends ImmutablePoint { }  // Error!

class Calculator {
    // final method — cannot be overridden
    public final double getPi() {
        return 3.141592653589793;
    }
    
    public int add(int a, int b) {
        return a + b;
    }
}

class ScientificCalculator extends Calculator {
    // Can't override getPi():
    // public double getPi() { return 3.14; }  // Error!
    
    // Can override add():
    @Override
    public int add(int a, int b) {
        System.out.println("Adding " + a + " + " + b);
        return super.add(a, b);  // Can still call parent version
    }
}
```

## Example 4: Constructor Chaining and super

```java
class A {
    A() { System.out.println("A constructor"); }
}

class B extends A {
    B() {
        super();  // Implicit if not written
        System.out.println("B constructor");
    }
}

class C extends B {
    C() {
        super();
        System.out.println("C constructor");
    }
    C(int x) {
        this();  // Calls C()
        System.out.println("C(int) constructor: " + x);
    }
}

public class ChainDemo {
    public static void main(String[] args) {
        System.out.println("new C():");
        new C();
        
        System.out.println("\nnew C(42):");
        new C(42);
    }
}
// Output:
// new C():
// A constructor
// B constructor
// C constructor
//
// new C(42):
// A constructor
// B constructor
// C constructor
// C(int) constructor: 42
```
