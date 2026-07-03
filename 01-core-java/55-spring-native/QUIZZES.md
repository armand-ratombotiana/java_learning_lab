# Module 55: Spring Native & GraalVM - Quizzes

---

## Q1: The Closed World Assumption
What does GraalVM's "Closed World Assumption" dictate?

A) The application cannot make external network calls.
B) All bytecode that could ever be executed must be completely known at compile time, precluding the use of purely dynamic runtime features like `Class.forName()`.
C) The application must be closed source.
D) The application cannot connect to databases.

**Answer**: B
**Explanation**: To aggressively strip out dead code and compile Java directly to machine code, the AOT compiler must traverse every possible execution path. It assumes the world is "closed" at compile time. Dynamic code generation or loading classes that the compiler couldn't see violates this assumption.

---

## Q2: JIT vs AOT
Which of the following is typically a trade-off when moving from traditional JVM JIT compilation to GraalVM AOT Native Images?

A) Native Images use more memory but start up slower.
B) Native Images have instant startup times and low memory footprints, but may suffer from slightly lower peak throughput compared to a fully "warmed up" JIT JVM.
C) Native Images require the user to manually install the JDK on their servers.
D) Native Images run interpreted code instead of machine code.

**Answer**: B
**Explanation**: A JIT compiler analyzes the application *while* it runs, optimizing the machine code specifically for the actual runtime traffic patterns (e.g., heavily optimizing a frequently taken `if` branch). AOT compilation happens once during the build, so it cannot perform these dynamic, profile-guided optimizations, resulting in slightly lower peak performance.

---

## Q3: Spring AOT Engine
How does Spring Boot 3 make the Spring Framework compatible with GraalVM Native Images?

A) By rewriting the entire Spring Framework in C++.
B) By disabling Dependency Injection entirely.
C) By introducing the Spring AOT Engine, which analyzes the application context during the build phase and generates hardcoded Java classes and GraalVM metadata (JSON) to replace runtime reflection and dynamic proxies.
D) By running a mini JVM inside the native image.

**Answer**: C
**Explanation**: Spring relies heavily on reflection and dynamic proxies (which violate the Closed World Assumption). The AOT Engine bridges this gap by shifting the "startup" work (scanning the classpath, resolving `@Autowired` dependencies) to the build phase, generating code that natively wires the beans together.