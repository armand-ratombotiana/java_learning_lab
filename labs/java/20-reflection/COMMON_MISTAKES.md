# Common Mistakes — Reflection

## 1. Calling getMethod Instead of getDeclaredMethod
`getMethod()` only returns public methods. `getDeclaredMethod()` returns all methods regardless of access.

## 2. Forgetting setAccessible(true) for Private Members
```java
Field f = clazz.getDeclaredField("privateField");
f.get(instance); // IllegalAccessException
```

## 3. Ignoring InvocationTargetException
```java
try { method.invoke(obj); }
catch (InvocationTargetException e) {
    // e contains the real exception thrown by the method
    throw e.getCause();
}
```

## 4. Performance Hotspots
Calling `getMethod()` in a tight loop instead of caching the `Method` object.

## 5. Breaking Module Encapsulation (Java 9+)
Private reflection on JDK internal APIs will fail. Use `--add-opens` or avoid.

## 6. Type Confusion with Primitives
```java
Class<?> intClass = Integer.TYPE; // not Integer.class!
Method m = clazz.getMethod("setId", int.class); // use int.class
```

## 7. Not Handling SecurityManager (Legacy)
In restricted environments, `setAccessible(true)` may throw `SecurityException`.
