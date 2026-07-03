# How It Works — Reflection

## Class Loading
1. JVM reads `.class` file
2. Creates `Class<?>` object in metaspace
3. Stores method table, field table, annotations, constant pool
4. `Class.forName()` triggers loading if not already loaded

## Method Invocation (Reflective)
```java
method.invoke(obj, args)
  →  MethodAccessor.invoke()
    →  NativeMethodAccessorImpl (for first 15 calls)
    →  GeneratedMethodAccessor (Java-generated after threshold)
```
The Java-generated accessor is faster because it avoids native transitions.

## Field Access
```java
field.setAccessible(true);
// Sets accessible flag in Field object; modifies access checks in FieldAccessor
```

## Proxy Implementation
```java
Proxy.newProxyInstance()
  →  generates bytecode for $Proxy0 class
    →  extends Proxy implements MyInterface
    →  each method call → InvocationHandler.invoke()
```

## Module Encapsulation (Java 9+)
Reflection on private members from another module requires:
- `--add-opens module/package=caller` or
- `--illegal-access=permit` (Java 9-16, removed in Java 17)
