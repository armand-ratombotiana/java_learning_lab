# Module 14: Reflection & Introspection - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-13 (Core Java, OOP, Generics, Annotations)  
**Estimated Reading Time**: 60-75 minutes  
**Code Examples**: 80+

---

## 📚 Table of Contents

1. [Introduction to Reflection](#introduction)
2. [The Class Object](#classobject)
3. [Inspecting Class Members](#inspecting)
4. [Dynamic Instantiation](#instantiation)
5. [Method Invocation](#methodinvocation)
6. [Field Modification](#fieldaccess)
7. [Annotation Processing](#annotationprocessing)
8. [Performance & Security](#performance)

---

## 1. Introduction to Reflection <a name="introduction"></a>
Reflection is a feature in the Java programming language that allows an executing Java program to examine or "introspect" upon itself, and manipulate internal properties of the program.

### Why use Reflection?
- Frameworks like Spring and Hibernate rely heavily on reflection.
- IDEs use reflection to provide auto-completion.
- Testing frameworks like JUnit use it to discover and run tests.

---

## 2. The Class Object <a name="classobject"></a>
Every type in Java (including primitive types and arrays) has an associated `Class` object.

```java
// Method 1: .class syntax
Class<?> stringClass = String.class;

// Method 2: .getClass() on an instance
String str = "Hello";
Class<?> clazz2 = str.getClass();

// Method 3: Class.forName()
Class<?> clazz3 = Class.forName("java.lang.String");
```

---

## 3. Inspecting Class Members <a name="inspecting"></a>
You can dynamically discover constructors, methods, and fields.

```java
Class<?> clazz = String.class;
Method[] methods = clazz.getDeclaredMethods();
Field[] fields = clazz.getDeclaredFields();
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
```

---

## 4. Dynamic Instantiation <a name="instantiation"></a>
```java
Class<?> clazz = Class.forName("com.example.User");
Constructor<?> constructor = clazz.getConstructor(String.class);
Object user = constructor.newInstance("John Doe");
```

---

## 5. Method Invocation <a name="methodinvocation"></a>
```java
Method method = String.class.getMethod("substring", int.class);
String result = (String) method.invoke("Hello Reflection", 6);
// result = "Reflection"
```

---

## 6. Field Modification <a name="fieldaccess"></a>
```java
class Secret { private String data = "Hidden"; }

Secret secret = new Secret();
Field field = Secret.class.getDeclaredField("data");
field.setAccessible(true); // Bypass access control
field.set(secret, "Exposed");
```

---

## 7. Annotation Processing <a name="annotationprocessing"></a>
Reflection is key to reading runtime annotations.
```java
if (clazz.isAnnotationPresent(Entity.class)) {
    Entity entity = clazz.getAnnotation(Entity.class);
    System.out.println("Table name: " + entity.tableName());
}
```

---

## 8. Performance & Security <a name="performance"></a>
- **Performance:** Reflection is slower than direct code execution due to access checks and lack of compiler optimizations.
- **Security:** Using `setAccessible(true)` can be blocked by a Security Manager (or the Module System in modern Java).