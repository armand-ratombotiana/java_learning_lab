# Module 14: Reflection & Introspection - Edge Cases & Pitfalls

---

## Pitfall 1: Bypassing Security and Module System

### ❌ Wrong
```java
Field field = String.class.getDeclaredField("value");
field.setAccessible(true); // Fails in Java 9+ due to Module encapsulation!
```

### ✅ Correct
```java
// Be aware of Module definitions (module-info.java) and add 'opens' or 'exports' 
// to allow reflection explicitly. Or use VarHandles/MethodHandles.
```

---

## Pitfall 2: ClassNotFoundException vs NoClassDefFoundError

### ❌ Wrong
```java
// Assuming Class.forName() throws the same error for everything
Class.forName("com.example.NonExistent");
```

### ✅ Correct
```java
try {
    Class.forName("com.example.MyClass");
} catch (ClassNotFoundException e) {
    // The class was not found in the classpath
} catch (NoClassDefFoundError e) {
    // The class was found at compile time but missing at runtime (or failed to initialize)
}
```

---

## Pitfall 3: Performance Overhead

### ❌ Wrong
```java
// Looking up the method in a loop
for (int i = 0; i < 10000; i++) {
    Method m = MyClass.class.getMethod("doWork");
    m.invoke(obj);
}
```

### ✅ Correct
```java
// Cache the method!
Method m = MyClass.class.getMethod("doWork");
for (int i = 0; i < 10000; i++) {
    m.invoke(obj);
}
```