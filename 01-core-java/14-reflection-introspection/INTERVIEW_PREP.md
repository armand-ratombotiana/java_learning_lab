# Module 14: Reflection & Introspection - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is Reflection in Java and what are its main use cases?
**Answer**:
Reflection is an API that allows a Java program to examine or modify the runtime behavior of applications running in the Java Virtual Machine.
**Use Cases**:
- **Frameworks**: Spring and Hibernate use it to instantiate objects, inject dependencies, and map database rows to objects.
- **Testing**: JUnit uses it to find methods annotated with `@Test`.
- **Dynamic Proxies**: Creating proxy classes at runtime for AOP (Aspect-Oriented Programming).

### Q2: What are the drawbacks of using Reflection?
**Answer**:
1. **Performance Overhead**: Reflective operations are significantly slower than direct code execution because the JVM cannot perform static optimizations (like method inlining).
2. **Security Restrictions**: Reflection requires runtime permissions and can be blocked by a Security Manager or the Java Module System encapsulation.
3. **Exposure of Internals**: Reflection breaks encapsulation by allowing access to `private` fields and methods, making code brittle and harder to maintain.

### Q3: How do you bypass private access modifiers using Reflection?
**Answer**:
By retrieving the `Field` or `Method` object and calling `.setAccessible(true)`. This suppresses the Java language access checks. Note that in modern Java (9+), this might fail if the module does not explicitly `open` the package for reflective access.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Invoking a Private Method
**Problem**: Given a class `SecretProcessor` with a private method `private String process(String input)`, write the reflection code to invoke this method and print the result.

**Solution**:
```java
public void invokeSecretMethod(SecretProcessor processor) throws Exception {
    Method method = SecretProcessor.class.getDeclaredMethod("process", String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(processor, "Top Secret");
    System.out.println(result);
}
```