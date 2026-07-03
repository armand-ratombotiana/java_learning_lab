# Inheritance — How It Works

## Step 1: Class Loading

When a class is first referenced, the JVM loads:
1. Superclass (recursively up to Object)
2. Interfaces (recursively)
3. The class itself

Loading includes verifying the class file, preparing static fields, and resolving symbolic references.

## Step 2: Method Resolution (Overriding)

```java
Dog dog = new Dog();
dog.sound();
```

1. Compiler checks Dog has `sound()` or inherits it
2. At runtime, JVM looks up Dog's vtable for `sound()`
3. If Dog overrides sound(), Dog's implementation is used
4. If not, parent's (Animal's) implementation is used

## Step 3: Constructor Chain

```
new Manager("Alice", 100000, 5000)
    → 1. Object() constructor runs
    → 2. Employee(String, double) runs (sets name, salary)
    → 3. Manager(String, double, double) runs (sets bonus)
```

If Employee's constructor calls `super()` (implicitly), Object() runs first. If Employee used `super(name)` explicitly, Object() still runs first.

## Step 4: super Keyword

```java
super.getSalary()  // Compiled to: aload_0; invokevirtual Employee.getSalary()
```

`super` bypasses the current class's vtable entry and directly invokes the parent's implementation.

## Step 5: instanceof Check

```java
if (obj instanceof Dog)
```

Bytecode: `instanceof` instruction checks the object's class against the target type. Returns boolean. Does NOT check superinterfaces of arrays.

## Step 6: final Methods

The compiler can inline final methods at compile time (if small) because they won't be overridden. The JIT also aggressively inlines final and private methods.
