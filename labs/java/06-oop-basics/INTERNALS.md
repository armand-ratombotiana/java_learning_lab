# OOP Basics — Internal Mechanics

## Object Layout (HotSpot JVM, 64-bit)

With compressed OOPs (default for heaps < 32GB):
```
[Header: 12 bytes] [Instance data: ...] [Padding: ...]
- Mark word: 8 bytes (identity hash, GC info, lock state)
- Klass pointer: 4 bytes (points to Class metadata in Metaspace)
- Instance fields: size varies (aligned to 8-byte boundary)
```

## this Keyword Implementation

`this` is a hidden first parameter in non-static methods. In bytecode:
```java
public void setName(String name) {
    this.name = name;
}
// Bytecode:
// aload_0        (load 'this' into stack)
// aload_1        (load 'name' parameter)
// putfield #5    (set field 'name')
```

Local variable index 0 = `this` for instance methods. Index 0 = first parameter for static methods.

## Static Field Storage

Static fields are stored in the class's `Class` object (heap, Metaspace). They're not part of any instance object.

## Inner Class Implementation

Inner classes compile to separate class files (e.g., `Outer$Inner.class`). The compiler:
1. Adds a reference to the outer class instance (as hidden field)
2. Constructor takes outer instance as parameter
3. Access to outer's private fields generates synthetic accessor methods

## Instance Initialization Blocks

```java
class Example {
    { /* instance init block */ }
    static { /* static init block */ }
}
```

Instance init blocks are copied into every constructor by the compiler, after `super()` call but before constructor body. Static init blocks run during class loading.
