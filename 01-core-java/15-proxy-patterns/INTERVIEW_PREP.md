# Interview Preparation: Proxy Patterns & Dynamic Proxies

This document covers advanced questions related to the Proxy design pattern, JDK Dynamic Proxies, CGLIB, and how frameworks like Spring utilize them.

## Q1: Explain the difference between the Decorator pattern and the Proxy pattern.
**Answer:**
*   **Intent**: Both patterns use composition to wrap an object and delegate calls to it. However, their intents are different.
*   **Proxy**: Controls *access* to the object. It is often created automatically by a framework (e.g., lazy loading in Hibernate, transaction management in Spring). The client usually doesn't know it's interacting with a proxy. The proxy often manages the lifecycle of the real object.
*   **Decorator**: Adds *behavior* or responsibilities to an object dynamically at runtime. The client explicitly wraps the object (e.g., `new BufferedInputStream(new FileInputStream(...))`).

## Q2: You annotated a method with Spring's `@Transactional`, but when you call it, the transaction doesn't start. What is the most likely architectural reason for this?
**Answer:**
This is the classic **"Self-Invocation"** problem.
Spring AOP uses proxies. When Bean A calls Bean B, Bean A is actually calling Bean B's proxy, which starts the transaction. 
However, if a method *inside* Bean B calls another method *inside* Bean B (using `this.methodName()`), the call happens directly on the target object, completely bypassing the proxy. Therefore, the AOP advice (like `@Transactional` or `@Cacheable`) is ignored.

## Q3: What is the difference between JDK Dynamic Proxies and CGLIB? When does Spring use which?
**Answer:**
*   **JDK Dynamic Proxies**: Built into the JVM. They can only proxy **Interfaces**. They work by generating a class that implements the target's interfaces and delegates to an `InvocationHandler`.
*   **CGLIB (Code Generation Library)**: A third-party library (repackaged inside Spring). It can proxy **concrete classes**. It works by generating a subclass of the target class at runtime and overriding its methods. It cannot proxy `final` classes or methods.
*   **Spring's Usage**: Historically, Spring used JDK proxies if the bean implemented an interface, and CGLIB if it didn't. However, since Spring Boot 1.4, **CGLIB is the default** for all proxies (via `proxyTargetClass=true`) to avoid `ClassCastExceptions` when developers try to inject the concrete class instead of the interface.

## Q4: How does Hibernate use proxies for Lazy Loading?
**Answer:**
When you load an entity (e.g., `Author`) that has a One-to-Many relationship with another entity (e.g., `List<Book>`), Hibernate does not load the books immediately to save memory and DB queries.
Instead, it injects a **Proxy** collection (like `PersistentBag`) or a CGLIB proxy for single entity relations. 
This proxy holds the database ID of the related entity. When you finally call a method on the proxy (e.g., `author.getBooks().size()`), the proxy intercepts the call, executes the SQL query to fetch the data, populates itself, and then returns the result. If you do this outside of an active Hibernate Session, it throws a `LazyInitializationException`.

## Q5: If you are writing a custom `InvocationHandler` for a JDK proxy, what special care must you take regarding `equals()`, `hashCode()`, and `toString()`?
**Answer:**
By default, the `InvocationHandler` intercepts *all* method calls defined on the interface, including those inherited from `java.lang.Object` if they are part of the proxy interface.
If you don't explicitly check for these methods, your custom logic (e.g., logging, retrying, or remote RPC calls) will execute when someone simply puts the proxy into a `HashMap` or prints it. 
You must explicitly intercept these methods and usually delegate them directly to the target object, or implement specific logic (e.g., ensuring a proxy's hashcode matches the target's hashcode).