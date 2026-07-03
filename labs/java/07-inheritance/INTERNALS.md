# Inheritance — Internal Mechanics

## VTable Structure

Each class has a vtable (virtual method table) — an array of method pointers. When class B extends A:
1. B's vtable starts with A's method entries
2. Overridden methods replace entries at same indices
3. New methods append to the end

```
Object vtable:   [toString, hashCode, equals, ...]
Animal vtable:   [toString, hashCode, equals, toString←Animal, sound←Animal]
Dog vtable:      [toString, hashCode, equals, toString←Animal, sound←Dog, bark←Dog]
```

## Object Header and Klass Pointer

Every object has a klass pointer to its Class metadata. `instanceof` uses this pointer to walk the class hierarchy. The klass contains the vtable pointer.

## Final Fields in JMM

`final` fields in the Java Memory Model guarantee safe publication — a thread that sees a reference to an object will see properly initialized final fields (even without synchronization).

## Class Loading Delegation

Inheritance affects class loading. When Dog.class is loaded, the JVM ensures Animal.class and Object.class are loaded first (parent-first delegation model). Interfaces are also loaded but initialization is deferred.

## Compiler-Generated Bridge Methods

Generics sometimes require bridge methods for type safety. When a subclass overrides a generic method with a more specific type, the compiler generates a bridge method:

```java
class Parent { Comparable<Parent> clone(); }
class Child { Comparable<Child> clone(); }
// Compiler adds: Comparable<Parent> clone() { return this.clone(); } // bridge
```
