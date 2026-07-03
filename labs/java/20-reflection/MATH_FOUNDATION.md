# Mathematical Foundation — Reflection

## Reflection as Metaprogramming
Reflection is a form of **metaprogramming** — a program operating on itself as data.

## Type System Limits
Reflection breaks the type system:
- `Class.forName(name)` → unchecked cast
- `method.invoke(obj)` → returns `Object`, unchecked cast
- `field.set(obj, value)` → no compile-time type check

```java
// No type safety at compile time
Class<?> clazz = Class.forName(userInput);
Method m = clazz.getMethod("execute");
m.invoke(clazz.newInstance()); // 3 unchecked operations
```

## Proxy as Functor
`InvocationHandler` is a functor — it maps a method invocation to a return value:
```
handler: (proxy, method, args) → Object
```

## Reflection vs Direct Invocation Cost
```
Direct:           O(1) — virtual dispatch
Method.invoke:    O(n) — argument boxing, access checks, unwrap
MethodHandle:     O(1) — after binding
```
