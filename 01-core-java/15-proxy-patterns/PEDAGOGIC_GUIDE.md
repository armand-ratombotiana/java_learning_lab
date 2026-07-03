# Pedagogic Guide: Proxy Patterns & Dynamic Proxies

## 1. Module Overview
This module transitions learners from static object-oriented design patterns to dynamic, runtime behavior modification. Understanding proxies is the "aha!" moment for many Java developers, as it demystifies how "magic" frameworks like Spring (AOP, `@Transactional`) and Hibernate (Lazy Loading) actually work.

## 2. Learning Paths

### Path A: The Application Developer (Focus: Usage & Pitfalls)
**Target Audience**: Developers who use Spring/Hibernate and want to stop making AOP mistakes.
*   **Focus**: `EDGE_CASES.md` (Self-Invocation) and `DEEP_DIVE.md` (AOP concepts).
*   **Key Takeaway**: Understanding *why* calling a method from within the same class bypasses transactions, and knowing the difference between interface-based and class-based proxying.

### Path B: The Framework Author (Focus: Mechanics)
**Target Audience**: Senior developers, library authors, or those preparing for system design/expert interviews.
*   **Focus**: `MINI_PROJECT.md` (Building a custom `@Retry` proxy) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering `InvocationHandler`, understanding `java.lang.reflect.Proxy`, and knowing when to reach for CGLIB vs. JDK Proxies.

## 3. Teaching Strategies

### The "Magic" Demystification Approach
Start by showing a piece of Spring code with `@Transactional`. Ask the learner: *"How does Java know to start a database transaction here? We didn't write `connection.commit()`."*
Use this curiosity gap to introduce the concept of a Proxy intercepting the call.

### The "Boilerplate" Problem Approach
Show a static proxy implementation (as seen in `DEEP_DIVE.md`). Ask the learner to imagine writing that proxy class for 50 different services to add logging. Then introduce JDK Dynamic Proxies as the solution to the DRY (Don't Repeat Yourself) violation.

## 4. Common Mental Blocks & Clarifications

### Block 1: "A Proxy is just a Wrapper"
*   **Clarification**: While structurally similar to the Decorator pattern, emphasize the *intent*. A decorator adds features; a proxy controls access (lifecycle, security, transactions). Furthermore, dynamic proxies do this *generically* at runtime, whereas decorators are usually statically typed.

### Block 2: "Self-Invocation Confusion"
*   **Clarification**: This is the hardest concept for beginners. Use a visual analogy: The Proxy is a bouncer at a club. If you walk in the front door (external call), the bouncer checks your ID (starts the transaction). If you are already inside the club and walk to the bar (internal `this.method()` call), you don't pass the bouncer again.

### Block 3: ClassCastExceptions with Proxies
*   **Clarification**: Explain that a JDK Proxy implements `InterfaceA`, but it is *not* an instance of `ImplementationA`. It is a dynamically generated class (e.g., `$Proxy0`). Therefore, casting the proxy to the concrete implementation class will fail.

## 5. Assessment Strategy
*   **Formative**: Have the learner write a basic `InvocationHandler` that just prints "Hello" before delegating the call.
*   **Summative**: The `MINI_PROJECT.md` provides an excellent summative assessment, requiring the learner to combine annotations, reflection, and dynamic proxies into a cohesive framework feature.