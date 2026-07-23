# Mock Interview Transcript: Module System

## Interviewer: Staff Engineer, Oracle
## Candidate: Senior Java developer
## Time: 40 minutes
## Focus: JPMS, module-info, encapsulation, jlink, migration

---

**Q1: What problem does the Java Module System (JPMS) solve?**

**Candidate**: JPMS provides: (1) Strong encapsulation — modules explicitly export packages, preventing access to internal APIs. (2) Reliable configuration — module dependencies are declared and validated at startup, preventing classpath issues. (3) Scalable — jlink creates custom runtimes with only needed modules. (4) Security — internal JDK APIs (like `sun.misc.Unsafe`) are hidden by default.

**Interviewer**: Write a `module-info.java` for a web framework.

**Candidate**: 
```java
module com.example.framework {
    requires transitive com.example.common;
    requires jakarta.inject;
    requires static lombok;
    requires java.sql;  // only if needed
    
    exports com.example.framework.api;
    exports com.example.framework.spi;
    
    opens com.example.framework.internal to com.example.test;
    
    uses com.example.framework.spi.Plugin;
    provides com.example.framework.spi.LoggingProvider 
        with com.example.framework.logging.LogbackProvider;
}
```

**Interviewer**: Explain `requires`, `requires transitive`, `requires static`.

**Candidate**: `requires` — the module depends on another module. `requires transitive` — any module that reads your module also reads the transitive dependency (used when types from that module are exposed in your API). `requires static` — compile-time only dependency (optional at runtime, e.g., Lombok).

**Interviewer**: What's the difference between `exports` and `opens`?

**Candidate**: `exports pkg` — allows compile-time and run-time access to public types in the package. `opens pkg` — allows deep reflection (access to private members) at runtime only (not compile-time). `opens pkg to module` — restricted opening. Used by frameworks like Spring, Hibernate that need reflective access.

**Interviewer**: How does the module system affect reflection?

**Candidate**: By default, reflection can only access exported packages and only for public members. Non-exported packages throw `InaccessibleObjectException` for reflective access. `--add-opens` JVM flag opens packages for reflection. `--add-exports` exports packages. This was a major change for frameworks in the Java 9 migration.

**Interviewer**: What is `jlink` and when would you use it?

**Candidate**: `jlink` creates a custom JVM runtime with only the modules you need. For example, a simple CLI app might only need `java.base`. The resulting runtime is smaller (from ~300MB to ~30-50MB). This is useful for containers, embedded devices, and distributing self-contained applications.

**Interviewer**: What happens with unnamed modules and the classpath?

**Candidate**: Code on the classpath is in the "unnamed module". The unnamed module can access all exported JDK modules. Named modules can't access the unnamed module (unless `--add-reads` is used). This ensures module code doesn't accidentally depend on classpath code.

**Interviewer**: Migration strategy: how to move a monolithic JAR to modules?

**Candidate**: (1) Start with `java --list-modules` to see available modules. (2) Create `module-info.java` for your code. (3) Use `jdeprscan` to find deprecated API usage. (4) Use `jdeps` to find dependencies. (5) Fix encapsulation issues (`--add-opens` temporarily). (6) Name unnamed packages (all packages must be in named modules). (7) Test modular vs classpath. (8) Use jlink for distribution.

**Interviewer**: What's the difference between `java.se` and `java.se.ee`?

**Candidate**: `java.se` — core SE modules. `java.se.ee` — includes CORBA, XML, etc. (removed in Java 11). Java 9 deprecated the EE modules; they were removed in Java 11. Applications using CORBA or JAX-WS needed to add separate dependencies.

**Interviewer**: Final: How does the module system interact with the class loader hierarchy?

**Candidate**: (1) Named modules use the application class loader. (2) Built-in modules (java.base) use the bootstrap class loader. (3) Module layer replaces the flat classpath with a graph of module-to-module relationships. (4) Each module has a unique class loader in some configurations (GraalVM). (5) The delegation is graph-based (module-to-module), not the traditional parent-first delegation.

---

## Feedback

**Strengths**:
- Comprehensive module-info.java syntax
- Clear on exports vs opens
- Migration strategy is practical and complete
- Understands module system impact on class loaders

**Areas for Improvement**:
- Could discuss incubator modules (jdk.incubator.*)
- Mention `--illegal-access=permit` removal in Java 17

**Score**: 4.5/5 — Strong modular system understanding
