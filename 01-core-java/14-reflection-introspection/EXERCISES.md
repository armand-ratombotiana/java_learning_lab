# Exercises: Reflection & Introspection

Practice exercises for Java reflection.

---

## Exercise 1: Class Introspection
```java
// Get class name, package, modifiers for String.class
```

## Exercise 2: Field Access
```java
// Use reflection to access private field of a class
```

## Exercise 3: Method Invocation
```java
// Use Method.invoke() to call private method
```

## Solutions

```java
// Exercise 1
Class<?> c = String.class;
System.out.println(c.getName());
System.out.println(c.getPackage());
System.out.println(Modifier.toString(c.getModifiers()));
```

```java
// Exercise 2
Field f = MyClass.class.getDeclaredField("privateField");
f.setAccessible(true);
Object value = f.get(instance);
```

---

- Review [README.md](./README.md) for details