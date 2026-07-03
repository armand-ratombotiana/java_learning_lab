# Theory — Reflection

## What Is Reflection?
Reflection is the ability of a program to inspect and modify its own structure and behaviour at runtime.

## Class Objects
Every class loaded by the JVM has a corresponding `Class<?>` object:
```java
Class<?> clazz1 = String.class;
Class<?> clazz2 = "hello".getClass();
Class<?> clazz3 = Class.forName("java.lang.String");
```

## Inspecting Methods
```java
Class<?> clazz = MyClass.class;
Method[] methods = clazz.getDeclaredMethods();
for (Method m : methods) {
    System.out.println(m.getName() + ": " + m.getReturnType());
}
```

## Invoking Methods Dynamically
```java
Class<?> clazz = Class.forName("com.example.MyService");
Object instance = clazz.getDeclaredConstructor().newInstance();
Method method = clazz.getMethod("doSomething", String.class);
Object result = method.invoke(instance, "hello");
```

## Accessing Fields
```java
Field field = clazz.getDeclaredField("privateField");
field.setAccessible(true); // Bypass access checks
field.set(instance, newValue);
```

## Dynamic Proxies
```java
MyInterface proxy = (MyInterface) Proxy.newProxyInstance(
    classLoader,
    new Class[]{MyInterface.class},
    (p, method, args) -> {
        System.out.println("Before " + method.getName());
        Object result = method.invoke(target, args);
        System.out.println("After " + method.getName());
        return result;
    }
);
```
