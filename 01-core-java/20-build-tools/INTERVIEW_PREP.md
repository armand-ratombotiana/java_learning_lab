# Module 20: Build Tools (Maven & Gradle) - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between `<dependencies>` and `<dependencyManagement>` in a Maven POM?
**Answer**:
- `<dependencies>`: Used to actually declare dependencies. Any dependency listed here will be downloaded and added to the classpath of the project (and its children).
- `<dependencyManagement>`: Used purely to centrally manage dependency *versions*. It does not add dependencies to the classpath. Instead, if a child module declares a dependency without specifying a `<version>`, Maven looks up the version defined in the parent's `<dependencyManagement>` block. This prevents version mismatches across multi-module projects.

### Q2: What happens when you run `mvn install` vs `mvn deploy`?
**Answer**:
In the default Maven build lifecycle, phases are executed sequentially.
- `mvn install`: Executes all phases up to `install` (validate -> compile -> test -> package -> verify -> install). The `install` phase copies the packaged artifact (like the `.jar`) into the local Maven repository (usually `~/.m2/repository`), making it available as a dependency for other local projects on the same machine.
- `mvn deploy`: Executes all phases up to `deploy` (which includes `install`). The `deploy` phase pushes the final artifact to a remote repository (like Nexus or Artifactory) to be shared with other developers or deployed to production environments.

### Q3: Why might a team choose Gradle over Maven?
**Answer**:
- **Flexibility**: Gradle uses a Groovy or Kotlin DSL (Domain Specific Language) instead of XML. This allows developers to write custom build logic directly in the script, making it much more flexible than Maven's rigid plugin structure.
- **Performance**: Gradle is significantly faster for large codebases. It implements a Build Cache, an incremental build engine (only recompiling tasks whose inputs have changed), and a persistent background Daemon process that keeps the JVM warm, reducing startup times.
- **Android Standard**: Gradle is the official and deeply integrated build tool for Android development.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Resolving a Transitive Dependency Conflict
**Problem**: Your project depends on `Library A` and `Library B`. `Library A` internally depends on `Guava version 20.0`, while `Library B` internally depends on `Guava version 30.0`. This is causing a `NoSuchMethodError` at runtime. How do you fix this "Jar Hell" in Maven?

**Solution**:
1. Run `mvn dependency:tree` to identify the conflict.
2. Fix it by explicitly excluding the older dependency and defining the required version in your POM.

```xml
<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>library-a</artifactId>
        <version>1.0</version>
        <exclusions>
            <!-- Exclude the old version from Library A -->
            <exclusion>
                <groupId>com.google.guava</groupId>
                <artifactId>guava</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    
    <!-- Explicitly declare the desired version so Maven uses this one -->
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>30.0-jre</version>
    </dependency>
</dependencies>
```