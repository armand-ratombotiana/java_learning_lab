# Quizzes: Proxy Patterns & Dynamic Proxies

Test your knowledge of static proxies, JDK Dynamic Proxies, and CGLIB.

## Quiz 1: Core Concepts

**Q1: What is the primary limitation of JDK Dynamic Proxies (`java.lang.reflect.Proxy`)?**
- A) They can only intercept `public` methods.
- B) They can only create proxies for classes that implement at least one Interface.
- C) They require bytecode manipulation libraries like ASM.
- D) They cannot be used in Spring Boot.
*Answer: B*

**Q2: How does CGLIB create a proxy for a concrete class?**
- A) It modifies the JVM source code.
- B) It generates a subclass of the target class at runtime and overrides its methods.
- C) It uses the Java Native Interface (JNI).
- D) It implements a hidden interface automatically.
*Answer: B*

## Quiz 2: Edge Cases and Pitfalls

**Q1: What is the "Self-Invocation" problem in proxy-based AOP (like Spring's `@Transactional`)?**
- A) When a proxy calls itself recursively, causing a StackOverflowError.
- B) When a method inside a class calls another method within the *same* class using `this.methodName()`, bypassing the proxy and its added behavior (like starting a transaction).
- C) When the garbage collector collects the proxy before the target object.
- D) When multiple proxies wrap the same object in an infinite loop.
*Answer: B*

**Q2: You apply a CGLIB proxy to a class, but one of the methods is marked as `final`. What happens when you call that method on the proxy?**
- A) The JVM throws a `VerifyError`.
- B) The proxy successfully intercepts the call.
- C) The call is not intercepted; it executes the original method directly because CGLIB cannot override `final` methods.
- D) The application crashes with a `NullPointerException`.
*Answer: C*

## Quiz 3: InvocationHandler

**Q1: When writing a custom `InvocationHandler` for a JDK Dynamic Proxy, which method must you implement?**
- A) `intercept()`
- B) `handle()`
- C) `invoke(Object proxy, Method method, Object[] args)`
- D) `execute()`
*Answer: C*