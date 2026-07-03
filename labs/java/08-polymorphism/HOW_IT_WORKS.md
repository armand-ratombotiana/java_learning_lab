# Polymorphism — How It Works

## Step 1: Compile-Time Resolution (Overloading)

```java
print(5);      // Which print? int version
print("Hi");   // Which print? String version
print(5, "Hi"); // Which print? two-param version
```

1. Compiler lists all methods named `print` in the class
2. Filters to those applicable to the argument types
3. Selects the most specific match (widening > autoboxing > varargs)
4. Compiles to invoke with that method's signature

## Step 2: Runtime Resolution (Overriding)

```java
Animal a = new Dog();
a.sound();  // Dog's sound() at runtime
```

1. Compiler: verifies `Animal` has `sound()` (or it's a compilation error)
2. Bytecode: `invokevirtual #12` (index 12 in Animal's constant pool → method reference)
3. JVM: loads actual class of `a` (Dog)
4. JVM: looks up `sound()` in Dog's vtable at the same index
5. Executes Dog's implementation

## Step 3: Interface Dispatch

```java
List<String> list = new ArrayList<>();
list.add("hello");  // ArrayList's add()
```

1. `invokeinterface` instruction (slower than invokevirtual)
2. JVM searches the implementing class's itable (interface method table)
3. Caches result for next call (polymorphic inline cache)

## Step 4: Covariant Return

```java
Dog d = new Dog();
Animal a = d.reproduce();  // Actually returns Dog, stored as Animal
Dog d2 = d.reproduce();    // Compiler knows Dog.reproduce() returns Dog
```

Compiler generates a bridge method that calls the covariant version and casts the result.

## Step 5: instanceof + Pattern Matching

```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

1. Check obj is String (instanceof check)
2. If true, bind to variable `s` (scope-limited to the if-block)
3. s is already typed as String — no cast needed
