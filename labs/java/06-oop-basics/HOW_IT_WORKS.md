# OOP Basics — How It Works

## Step 1: Class Definition

The compiler processes a class declaration and produces a class file with:
- Constant pool (strings, class names, field/method references)
- Field descriptors (name, type, access flags)
- Method descriptors (name, parameter types, return type, bytecode)
- Attribute information (SourceFile, LineNumberTable, etc.)

## Step 2: Object Creation with `new`

```java
Person p = new Person("Alice", 30);
```

1. **Class loading**: JVM loads Person.class if not already loaded
2. **Memory allocation**: Allocates heap space for object (fields + header)
3. **Zero initialization**: All fields set to default values (0, null, false)
4. **Field initialization**: Any inline field initializers run
5. **Constructor call**: `invokespecial` — constructor bytecode executes
6. **Instance assignment**: Reference stored in variable `p`

## Step 3: Object Reference

```java
p.getName();
```

1. JVM loads reference from `p` (local variable slot 0 for `this`, or local variable slot)
2. Check for null reference (NullPointerException if null)
3. Call `invokevirtual` with the reference → vtable dispatch
4. Execute method body

## Step 4: static Member Access

```java
Math.max(5, 3);
```

No object needed. The method is resolved:
1. Locate Math class (load if needed)
2. Call `invokestatic` — direct method call, no vtable
3. No `this` reference available in method body

## Step 5: Constructor Chaining

```java
new Manager("Alice", 100000, 5000);
// → Manager(String, double, double) constructor called
//   → super("Alice", 100000) [Employee constructor]
//     → super() [Object constructor]
//     → Employee body runs
//   → Manager body runs (sets bonus)
```

## Step 6: Garbage Collection

When no references point to an object, GC marks it as eligible. The JVM decides when to collect — typically during a GC pause. `finalize()` is deprecated (Java 9+) — use `Cleaner` or `try-with-resources` instead.
