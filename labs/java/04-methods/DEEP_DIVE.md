# Methods — Ultra Deep Dive

## 1. Method Dispatch: The Four invoke Instructions

The JVM has four distinct bytecode instructions for method invocation, each with different dispatch mechanics:

| Instruction | Target | Dispatch | Receiver | Resolution |
|------------|--------|----------|----------|------------|
| `invokestatic` | Static methods | Static (compile-time) | None | Early |
| `invokespecial` | Constructors, super, private | Static (compile-time) | Push receiver | Early |
| `invokevirtual` | Instance methods (public/protected/package) | Dynamic (vtable) | Push receiver | Late |
| `invokeinterface` | Interface methods | Dynamic (itable) | Push receiver | Late |
| `invokedynamic` | User-defined call sites | Custom bootstrap | Depends on bootstrap | Late |

### Bytecode Example

```java
class Example {
    static void staticMethod() {}
    void instanceMethod() {}
    private void privateMethod() {}
}

class Caller {
    void test() {
        Example.staticMethod();      // invokestatic
        new Example();                // invokespecial <init>
        Example e = new Example();
        e.instanceMethod();           // invokevirtual
        super.toString();             // invokespecial (super call)
        e.privateMethod();            // invokespecial (private)
    }
}
```

## 2. VTable Layout and Dynamic Dispatch

### Virtual Method Table (VTable)

Each class in the JVM has a vtable — an array of method pointers. The layout:

```
Object vtable:
[0] hashcode()
[1] equals(Object)
[2] toString()
[3] clone()
[4] finalize()
[5] notify()
[6] notifyAll()
[7] registerNatives()

Example vtable (extends Object):
[0] hashcode()          → Object.hashcode
[1] equals(Object)       → Object.equals
[2] toString()           → Example.toString  (overridden)
[3] clone()              → Object.clone
[4] finalize()           → Object.finalize
[5] notify()             → Object.notify
[6] notifyAll()          → Object.notifyAll
[7] registerNatives()    → Object.registerNatives
[8] instanceMethod()     → Example.instanceMethod
```

**Dispatch mechanism**: `invokevirtual` uses the receiver's class to find the vtable, then indexes into it using the method's vtable index (computed at class load time). This is O(1).

### VTable Index Assignment

The JVM assigns vtable indices during class loading:
1. Inherited methods keep their parent's vtable index
2. Overriding methods replace the entry at the same index
3. New virtual methods are appended at the end

This ensures that `invokevirtual #index` works for any class in the hierarchy.

## 3. Interface Method Table (ITable)

Interface dispatch is more complex because a class can implement multiple interfaces:

```java
interface A { void a(); }
interface B { void b(); }
class Impl implements A, B {
    public void a() {}
    public void b() {}
}
```

Each class maintains an itable — a table of interface-method pairs. Dispatch uses:
1. Look up the interface in the itable (linear or binary search by interface ID)
2. Find the method in that interface's slot
3. Jump to implementation

### ITable Optimization

HotSpot uses a two-level itable structure:
- **ITable**: An array of interfaces (sorted by interface index) with each entry pointing to...
- **Method table**: The methods for that interface

`invokeinterface#index` passes the interface and method indices. The runtime:
1. Gets the receiver's itable
2. Binary-searches for the interface
3. Indexes into the method table at the method's itable offset

## 4. Method Handle (java.lang.invoke)

Method handles provide a modern, type-safe alternative to reflection:

```java
MethodHandles.Lookup lookup = MethodHandles.lookup();
MethodType mt = MethodType.methodType(void.class);
MethodHandle mh = lookup.findVirtual(Example.class, "instanceMethod", mt);
mh.invoke(exampleInstance);  // Calls Example.instanceMethod()
```

### Method Handle vs Reflection

| Aspect | `Method.invoke()` | `MethodHandle.invoke()` |
|--------|------------------|------------------------|
| Access checking | At each call | At lookup time |
| Inlining | Blocked by JIT | Fully inlineable |
| Type checking | Dynamic | Static via `MethodType` |
| Bytecode | `invokevirtual` on Method | `invokedynamic` (or synthetic) |
| Performance | ~10-100x slower than direct | ~1-2x slower than direct |
| Security | Full access check | Granular via Lookup |

### Method Handle Transformations

Method handles support powerful combinators:
```java
MethodHandle mh = ...;
// Filter return value: convert String return to int length
MethodHandle length = MethodHandles.filterReturnValue(mh,
    MethodHandles.publicLookup().findVirtual(String.class, "length",
        MethodType.methodType(int.class)));

// Bind first argument
MethodHandle bound = MethodHandles.insertArguments(mh, 0, fixedArg);

// Spread arguments from array
MethodHandle spread = mh.asSpreader(Object[].class);
```

## 5. invokedynamic (JSR 292)

`invokedynamic` allows the language implementer to define custom dispatch logic:

```java
// Bootstrapping:
CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    MethodHandle target = lookup.findStatic(MyClass.class, name, type);
    return new ConstantCallSite(target);
}
```

### How invokedynamic Works

1. **Bootstrap method**: Called once by the JVM when the instruction is first executed
2. **CallSite**: The bootstrap returns a CallSite containing a MethodHandle
3. **MethodHandle**: The actual target is invoked

The JVM can *relink* a `MutableCallSite` at any time, enabling dynamic language features.

### Lambda Compilation via invokedynamic

```java
// Source:
list.forEach(s -> System.out.println(s));

// Bytecode equivalent (desugared):
invokedynamic #lambda$0:(Ljava/util/List;)Ljava/util/function/Consumer;
// Bootstrap: LambdaMetafactory.metafactory()
```

The lambda is NOT compiled as an anonymous inner class. Instead, `invokedynamic` with `LambdaMetafactory` creates the function object at runtime. The JIT can inline the lambda target, eliminating the call site entirely.

### String Concatenation via invokedynamic (Java 9+)

```java
String s = a + b + c;
```

Bytecode (Java 9+):
```
invokedynamic #makeConcatWithConstants:(String, String, String) -> String
```

The bootstrap method (`StringConcatFactory`) generates the optimal concatenation strategy:
- Simple append (like before)
- StringBuilder with pre-sized array
- Direct byte array copy

## 6. JIT Inlining Decisions

The C2 JIT compiler uses heuristics to decide what to inline:

### Inlining Criteria

1. **Method size**: Methods smaller than `-XX:MaxInlineSize=35` bytes are inlined
2. **Call frequency**: Hot methods (high invocation count) are inlined regardless of size
3. **Depth**: Maximum inline depth controlled by `-XX:MaxInlineLevel=9`
4. **Recursion**: Max recursive inline depth: `-XX:MaxRecursiveInlineLevel=1`
5. **Trivial**: Methods that are empty, return constant, or just throw are always inlined

### Inlining Thresholds

| Flag | Default | Description |
|------|---------|-------------|
| `-XX:MaxInlineSize` | 35 | Max bytecode size for inlining |
| `-XX:FreqInlineSize` | 325 | Max size for frequently called methods |
| `-XX:InlineSmallCode` | 2000 | Max native code size after inlining |
| `-XX:MaxInlineLevel` | 9 | Max inline depth |
| `-XX:MaxTrivialSize` | 6 | Max size for trivial method |

### The Inlining Decision Tree

```
Is method called frequently?
  Yes → FreqInlineSize check
  No  → MaxInlineSize check
     → Is it a trivial method? (≤ 6 bytes)
        → Always inline
     → Is it a getter/setter? (single field access)
        → Always inline
     → Is it a hot method? (invocation count > threshold)
        → Inline if ≤ FreqInlineSize
     → Is final method with single implementation?
        → Inline optimistically with guard
```

### Inlining and Polymorphism

For polymorphic call sites, C2 uses **inline caching**:
- **Monomorphic**: One receiver type → inline with guard (check class, call method)
- **Bimorphic**: Two receiver types → inline both with class check
- **Megamorphic**: Three+ receiver types → use vtable dispatch (no inlining)

### Inline Cache Example

```java
// If only Dog and Cat types arrive:
interface Animal { void speak(); }

void process(Animal a) {
    a.speak();  // Inline cache with guard
}

// Compiled (optimistically monomorphic):
if (a.getClass() == Dog.class) {
    Dog.speak(a);  // Inlined
} else if (a.getClass() == Cat.class) {
    Cat.speak(a);  // Inlined
} else {
    a.speak();  // Full vtable dispatch (slow path)
}
```

## 7. Bridge Methods (Generics and Covariance)

The JVM has no concept of generic type arguments at runtime (erasure). Bridge methods are synthetic methods generated by the compiler to maintain polymorphism:

```java
class Parent {
    Object get() { return ""; }
}
class Child extends Parent {
    @Override
    String get() { return "hello"; }  // Covariant return type
}
```

The compiler generates:
```java
class Child {
    // Synthetic bridge:
    Object get() {  // bridge method
        return this.get();  // calls the String version
    }
    
    String get() { return "hello"; }
}
```

The bridge has ACC_BRIDGE and ACC_SYNTHETIC flags in the class file. The JIT can inline through bridge methods.

## 8. Default Methods and Interface Dispatch

Interface default methods require special dispatch:

```java
interface A {
    default void m() { System.out.println("A"); }
}
class B implements A {
    // Inherits A.m()
}
class C extends B implements A {
    @Override
    public void m() { System.out.println("C"); }
}
```

The `invokespecial` instruction can be used to call a specific interface's default:
```java
class D implements A {
    public void m() {
        A.super.m();  // invokespecial A.m()
    }
}
```

## 9. Synchronized Methods

A synchronized method uses the `ACC_SYNCHRONIZED` flag in the method's access_flags:
```java
synchronized void m() { ... }
```

This is equivalent to:
```java
void m() {
    synchronized (this) { ... }
}
```

But more efficient — the JVM enters the monitor on method entry and exits on method exit (including implicit exits on exceptions), without needing explicit `monitorenter`/`monitorexit` bytecodes.

## 10. Method Profiling and Tiered Compilation

HotSpot uses two compilers:
- **C1 (client)**: Fast compilation, less optimization
- **C2 (server)**: Slow compilation, aggressive optimization

Tiered compilation (default since Java 8):
1. **Tier 0**: Interpreter (profiling starts)
2. **Tier 1**: C1 simple (no profiling)
3. **Tier 2**: C1 limited profiling
4. **Tier 3**: C1 full profiling
5. **Tier 4**: C2 (aggressive optimization)

A method starts in the interpreter. When it becomes hot (~10,000-15,000 invocations), it's compiled by C1 with profiling. If it gets even hotter (~30,000-50,000 invocations), it's recompiled by C2. C2 uses the profiling data from C1 to make inlining and optimization decisions.
