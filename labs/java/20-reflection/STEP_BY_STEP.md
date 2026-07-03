# Step by Step — Reflection

## Step 1: Get a Class Object
```java
Class<?> clazz = MyClass.class; // or Class.forName("com.example.MyClass");
```

## Step 2: Create an Instance
```java
Constructor<?> constructor = clazz.getDeclaredConstructor();
constructor.setAccessible(true); // if private
Object instance = constructor.newInstance();
```

## Step 3: Get a Method
```java
Method method = clazz.getDeclaredMethod("doWork", String.class, int.class);
// or for all methods:
Method[] allMethods = clazz.getDeclaredMethods();
```

## Step 4: Invoke the Method
```java
Object result = method.invoke(instance, "param1", 42);
```

## Step 5: Access a Field
```java
Field field = clazz.getDeclaredField("name");
field.setAccessible(true); // bypass private
String value = (String) field.get(instance);
field.set(instance, "new value");
```

## Step 6: Create a Proxy
```java
MyInterface proxy = (MyInterface) Proxy.newProxyInstance(
    MyInterface.class.getClassLoader(),
    new Class<?>[]{MyInterface.class},
    (p, method, args) -> {
        // intercept
        return method.invoke(realObject, args);
    }
);
```

## Step 7: Handle Exceptions
```java
try {
    method.invoke(instance, args);
} catch (InvocationTargetException e) {
    throw e.getCause(); // unwrap the real exception
} catch (IllegalAccessException | IllegalArgumentException e) {
    // handle
}
```
