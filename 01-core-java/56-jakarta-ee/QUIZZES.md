# Module 56: Jakarta EE - Quizzes

---

## Q1: History and Namespaces
Why did the foundational package namespace for Java Enterprise applications change from `javax.*` to `jakarta.*`?

A) Because a critical security flaw was found in the `javax` package.
B) Oracle transferred the Java EE project to the Eclipse Foundation but retained the trademark rights to the "Java" name, forcing the rename to Jakarta EE and the namespace migration.
C) To make the code compile faster on modern JVMs.
D) Jakarta is the new default web server replacing Tomcat.

**Answer**: B
**Explanation**: The Eclipse Foundation could not legally use the `javax` namespace for evolving the API specifications, leading to the necessary "Big Bang" namespace shift in Jakarta EE 9.

---

## Q2: JAX-RS Annotations
In JAX-RS (Jakarta RESTful Web Services), which annotation is used to extract a variable directly from the URL path?

A) `@PathVariable`
B) `@RequestParam`
C) `@PathParam`
D) `@Extract`

**Answer**: C
**Explanation**: `@PathParam` is the standard JAX-RS annotation for extracting URI path segments. (Note: `@PathVariable` is the equivalent annotation used in the Spring Framework, which is separate from the Jakarta EE specification).

---

## Q3: CDI Scopes
Which CDI (Contexts and Dependency Injection) scope ensures that a new instance of a bean is created for every single HTTP request, and destroyed when the HTTP response is sent?

A) `@ApplicationScoped`
B) `@SessionScoped`
C) `@RequestScoped`
D) `@Dependent`

**Answer**: C
**Explanation**: `@RequestScoped` ties the lifecycle of the bean precisely to a single HTTP request cycle, making it ideal for holding temporary data needed to process a single transaction safely in a multi-threaded web server.