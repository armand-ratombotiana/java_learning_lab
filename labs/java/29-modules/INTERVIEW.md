# Interview Questions: Modular System (JPMS)

## Company-Specific Focus

### Google
- Module descriptor (module-info.java): exports, requires, opens, provides
- Module graph resolution at compile and runtime
- ServiceLoader based on provides/uses: inter-module service discovery

### Microsoft
- Java modules vs .NET assemblies: concept comparison
- Module system for enterprise security: what used to be accessible via reflection is now controlled
- Strong encapsulation: what happens with illegal access

### Amazon
- Modularization for large monoliths: breaking an application into modules with clear boundaries
- Module linking: jlink to create custom runtime images for microservices; reducing size
- Lamda deployment with jlink: 200MB JDK to 40MB runtime

### Meta
- Module system migration: what breaks and how to handle sun.misc.Unsafe
- Multi-module project: badge for backward compatability via --add-exports
- Module compatibility for open source libraries

### Apple
- Creating modules for the MacOS runtime
- Reducing attack surface: only exposed packages
- Using jlink to create optimized, smaller JVMs

### Oracle
- JEP 261: Module System (Project Jigsaw)
- JLS: The module declaration syntax
- The JDK itself is modularized: java.base, java.sql, java.xml
- JVM: how does the module system change class loading and accessibility checks

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 146 LRU Cache | Medium | Google, Apple, Amazon, Microsoft | Class design in a modular structure |
| 208 Implement Trie | Medium | Amazon, Google | Interface and implementation separation |
| 380 Insert Delete GetRandom O(1) | Medium | Amazon, Apple | Module packaging for algorithm |
| 211 Design Add and Search Words Data Structure | Medium | Google, Amazon | Module level shield of details |
| 348 Design Tic-Tac-Toe | Medium | Google, Microsoft | Design in modular approach |

## Real Production Scenarios
- **Amazon**: JHipster monolith split into 12 modules — reduced build time from 15min to 3min
- **LinkedIn**: JDK 9 migration required 200+ --add-exports JVM args for internal sun.misc usage
- **Google**: jlink for the Android bridge service — reduced from 40MB JDK to 12MB runtime

## Interview Patterns & Tips
- **module-info.java**: The most compact file in Java land and the most disruptive
- **Open packages**: Needs for reflection (for frameworks) — module must `opens` the package
- **jlink**: A very common mention — produce custom runtime images for small deployables.
- **Automatic modules**: library placed on the classpath becomes an automatic module.
- **multi-release JARs**: one JAR that can have module info for JDK 9+ and not for JDK 8

## Deep Dive Questions
- **Class loading**: How does the module system get integrated in the JVM's class loading architecture?
- **Memory**: How does the module metadata use metaspace?
- **Reflection**: How are accessibility checks tightened?
- **JVM startup**: How does the module graph resolution impact JVM startup time?
- **Performance**: Is performance improved by having fewer types to scan for the JIT?