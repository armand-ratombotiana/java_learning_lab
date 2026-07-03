# Interview Questions: Modules

## Q1: What problem does JPMS solve?
It solves classpath hell (no explicit dependencies, missing classes at runtime only), weak encapsulation (all public classes visible), and unreliable configuration. JPMS provides strong encapsulation, explicit dependencies, and reliable configuration verified at compile/startup time.

## Q2: What is a module descriptor?
module-info.java is the module descriptor. It declares the module name, required modules (requires), exported packages (exports), opened packages (opens), and service declarations (provides/uses).

## Q3: exports vs opens?
exports makes packages accessible for compile-time and runtime access. opens makes packages accessible for reflection only (runtime). opens is needed by frameworks like Hibernate, Spring, and Jackson that use reflection to access private fields.

## Q4: What is the difference between module path and classpath?
Modules on the module path have explicit dependencies checked at startup. They enjoy strong encapsulation. Classpath (unnamed module) is a catch-all where all packages are accessible but without module guarantees. Named modules cannot access unnamed modules directly.

## Q5: How does transitive require work?
`requires transitive` means that any module that requires your module also gets access to the transitively required module. It's used when your module exports types from another module.

## Q6: What is an automatic module?
A non-modular JAR placed on the module path automatically becomes an automatic module. It reads all other modules and exports all its packages. This provides backward compatibility for libraries not yet modularized.

## Q7: How do you migrate a large application to modules?
Start by identifying module boundaries. Use jdeps to analyze dependencies. Add module-info.java to bottom-level modules first, working up. Use --add-reads, --add-exports, --add-opens flags for unresolved dependencies during migration.

## Q8: What is jlink used for?
jlink creates a custom runtime image containing only the modules your application needs. This dramatically reduces the distribution size (from ~300MB JDK to ~30-50MB custom runtime).
