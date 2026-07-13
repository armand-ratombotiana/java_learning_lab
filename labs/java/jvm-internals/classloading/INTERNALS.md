# Classloader Internals & The Delegation Model

## 🌳 The Delegation Hierarchy
Java does not have a single Classloader. It uses a hierarchy of classloaders that follow the **Parent-Delegation Model**.

When a classloader is asked to load a class (e.g., `java.lang.String`), it does *not* try to load it immediately. Instead, it delegates the request up to its parent. If the parent cannot find it, the child tries to load it.

The hierarchy in Java 9+ (Module System) looks like this:

1. **Bootstrap ClassLoader (The Parent)**: Written in native C++ code (not Java). It loads the absolute core Java classes (e.g., `java.base` module containing `java.lang.String`, `java.util.List`).
2. **Platform ClassLoader (The Middle Child)**: Loads extensions and platform-specific modules (e.g., `java.sql`).
3. **Application / System ClassLoader (The Bottom Child)**: Loads classes from the application's classpath (the code you wrote, and the dependencies in your `pom.xml`).

**Why Delegate? (Security)**: Imagine you write a malicious class and name it `java.lang.String`. You put it in your project. When your code uses `String`, the Application Classloader delegates the request to the Platform, which delegates to the Bootstrap. The Bootstrap loader finds the *real* `java.lang.String` in the JDK core and loads it. Your malicious class is completely ignored. This prevents core JDK spoofing.

## 💥 The Two Famous Exceptions

### 1. `ClassNotFoundException`
This is a checked exception. It occurs when you explicitly ask the JVM to load a class by its string name at runtime (e.g., using Reflection: `Class.forName("com.mysql.jdbc.Driver")`), but the classloader cannot find that `.class` file anywhere on the classpath.

### 2. `NoClassDefFoundError`
This is a fatal Error, not an Exception. It occurs when the JVM successfully compiled your code because the class existed at *compile time*, but when it tries to run the code, the class is missing at *runtime*.
- **Common Cause**: You compiled your app with `library-1.0.jar`, but you accidentally deployed it to production without that JAR. When the JVM hits the line of code that references the missing class, it throws this error.
- **Static Init Failure**: If a class's `static { }` block throws an unhandled exception during the Initialization phase, the class fails to load. The *next* time your code tries to use that class, the JVM won't try to initialize it again; it will instantly throw `NoClassDefFoundError`.

## 🎭 Classloader Isolation (Tomcat / Application Servers)
How can Tomcat run two different Spring Boot applications that use completely different, conflicting versions of the same library (e.g., App A uses `log4j-1.0` and App B uses `log4j-2.0`) without them crashing each other?

**Classloader Isolation**. Tomcat creates a brand new, custom Classloader for every single web application deployed on it. 
In Java, a class is uniquely identified not just by its fully qualified name (`com.foo.Logger`), but by its name **AND the classloader that loaded it**.
Because App A's classloader and App B's classloader are siblings (they don't share children), they cannot see each other's classes. The JVM treats `com.foo.Logger` loaded by App A as a completely different object than `com.foo.Logger` loaded by App B.