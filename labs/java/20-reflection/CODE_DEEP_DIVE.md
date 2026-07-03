# Code Deep Dive — Reflection

## Dynamic Method Invocation
```java
public Object callMethod(String className, String methodName, Object... args) 
        throws Exception {
    Class<?> clazz = Class.forName(className);
    Object instance = clazz.getDeclaredConstructor().newInstance();
    
    Class<?>[] paramTypes = Arrays.stream(args)
        .map(Object::getClass)
        .toArray(Class<?>[]::new);
    
    Method method = clazz.getMethod(methodName, paramTypes);
    return method.invoke(instance, args);
}
```

## Dynamic Proxy for Logging
```java
public static <T> T loggingProxy(T target, Class<T> iface) {
    return (T) Proxy.newProxyInstance(
        iface.getClassLoader(),
        new Class<?>[]{iface},
        (proxy, method, args) -> {
            System.out.println("→ " + method.getName() 
                + "(" + Arrays.toString(args) + ")");
            long start = System.nanoTime();
            Object result = method.invoke(target, args);
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.println("← " + method.getName() 
                + " returned " + result + " (" + elapsed + "ms)");
            return result;
        }
    );
}
```

## Reflective Dependency Injection
```java
public static <T> T injectDependencies(T instance) throws Exception {
    for (Field field : instance.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Inject.class)) {
            field.setAccessible(true);
            Object dependency = field.getType().getDeclaredConstructor().newInstance();
            field.set(instance, dependency);
        }
    }
    return instance;
}
```
