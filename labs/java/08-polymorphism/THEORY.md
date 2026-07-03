# Polymorphism — Theoretical Foundation

## Definition

Polymorphism (Greek: "many forms") allows a single method call to behave differently depending on the runtime type of the object. Java supports two kinds:

1. **Compile-time polymorphism** (static binding) — method overloading
2. **Runtime polymorphism** (dynamic binding) — method overriding

## Dynamic Method Dispatch

When an overridden method is called on a reference variable, the JVM determines which version to execute based on the actual object type, not the reference type.

```java
Animal animal = new Dog();
animal.sound();  // Calls Dog's sound(), not Animal's
```

The JVM uses **virtual method dispatch** — each object has a virtual method table (vtable) that maps method signatures to actual implementations.

### How It Works

1. Compiler verifies the method exists in the reference type (compile-time check)
2. At runtime, the JVM looks at the object's actual class
3. Uses the vtable to find the correct method implementation
4. Executes the overridden version

## Method Overloading (Compile-Time Polymorphism)

Multiple methods with the same name but different parameters. The compiler chooses which to call based on argument types.

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    public double add(double a, double b) { return a + b; }
    public int add(int a, int b, int c) { return a + b + c; }
}
```

### Overloading Rules

- Must differ in parameter count, types, or both
- Return type alone is NOT sufficient for overloading
- Varargs counts as an array type
- Widening beats autoboxing beats varargs in overload resolution

```java
void method(int x) { }       // 1
void method(Integer x) { }   // 2
void method(int... x) { }    // 3

method(5);  // Calls 1 (widening preferred over autoboxing)
method(null); // Calls 2 (Integer is more specific than int[])
```

## Method Overriding vs Overloading

| Aspect | Overriding | Overloading |
|--------|------------|-------------|
| Binding | Runtime | Compile-time |
| Method signature | Same | Different |
| Return type | Same or covariant | Any |
| Access modifier | Same or wider | Any |
| `static` | Cannot override | Can overload |
| `final` | Cannot override | Can overload |
| `@Override` | Helps catch errors | Not applicable |

## Covariant Return Types

An overriding method can return a subtype of the overridden method's return type (Java 5+).

```java
class Animal {
    Animal reproduce() { return new Animal(); }
}

class Dog extends Animal {
    @Override
    Dog reproduce() { return new Dog(); }  // Covariant: Dog is subtype of Animal
}
```

## Polymorphism in Collections

```java
List<Animal> animals = new ArrayList<>();
animals.add(new Dog());
animals.add(new Cat());

for (Animal animal : animals) {
    animal.sound();  // Correct method called based on actual type
}
```

## Polymorphism and Parameters

```java
public void feed(Animal animal) {
    animal.eat();  // Works for any Animal subclass
}
```

This enables the Open/Closed Principle: code is open for extension (new Animal subtypes) but closed for modification (feed() doesn't change).

## Polymorphism and Interfaces

Interfaces provide the ultimate polymorphic abstraction:

```java
List<String> list = new ArrayList<>();  // Program to interface
List<String> list2 = new LinkedList<>();  // Swap implementation anytime
```

## The instanceof Operator

Used to check the runtime type of an object before casting:

```java
if (animal instanceof Dog dog) {  // Pattern matching (Java 16+)
    dog.bark();
}
```

Pattern matching eliminates the need for explicit cast after instanceof check.
