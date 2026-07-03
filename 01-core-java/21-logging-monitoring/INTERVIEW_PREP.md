# Module 21: Logging & Monitoring - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is SLF4J, and why is it preferred over using Log4j or Logback directly?
**Answer**:
SLF4J (Simple Logging Facade for Java) is an abstraction layer (facade) for various logging frameworks (like java.util.logging, logback, log4j).
Using SLF4J is preferred because it decouples the application code from the underlying logging implementation. If a library is written using SLF4J, the end-user can choose to plug in Logback or Log4j2 at deployment time simply by changing the classpath dependencies, without rewriting any `import` statements or `Logger` code in the library itself.

### Q2: Why should you avoid string concatenation in logging statements?
**Answer**:
String concatenation (e.g., `logger.debug("User " + user.getName() + " logged in");`) occurs *before* the logger checks if the specific log level (DEBUG) is currently enabled. If the production environment is set to INFO, the JVM still spends CPU cycles and memory building the concatenated string, only to immediately discard it.
Using parameterized logging (e.g., `logger.debug("User {} logged in", user.getName());`) defers the string construction. The logger checks the enabled level first, and only builds the final string if DEBUG is active, significantly improving performance.

### Q3: What is JMX, and what are MBeans?
**Answer**:
JMX (Java Management Extensions) is a standard technology for managing and monitoring Java applications, system objects, devices, and service-oriented networks. 
An **MBean (Managed Bean)** is a Java object that represents a manageable resource. MBeans expose an interface containing attributes (values that can be read/written) and operations (methods that can be invoked). External monitoring tools (like JConsole or VisualVM) can connect to the JVM's MBeanServer to read these metrics or execute operations (e.g., triggering a cache clear or changing log levels) without stopping the application.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Preserving Exception Stack Traces
**Problem**: A junior developer wrote the following error handling block. During a production outage, the team couldn't figure out exactly where the code failed. Identify the mistake and fix it.

```java
try {
    processPayment(order);
} catch (PaymentException e) {
    logger.error("Payment failed with error: " + e.getMessage());
    throw e;
}
```

**Solution**:
The developer concatenated the exception message to the string. This prints a single line like `Payment failed with error: null` or `Payment failed with error: Network timeout`, but completely discards the **Stack Trace** (the lines showing exactly which class and method caused the crash).

The fix is to pass the `Throwable` object as the final argument to the logging method, which signals the logging framework to print the full stack trace.

```java
try {
    processPayment(order);
} catch (PaymentException e) {
    // Pass the exception object as the final argument
    logger.error("Payment failed", e);
    throw e;
}
```