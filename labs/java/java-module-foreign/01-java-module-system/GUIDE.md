# Deep Dive: Java Module System

## 1. Module Descriptor (module-info.java)

Every module has a descriptor at the root of its source tree:

```java
// src/main/java/module-info.java
module com.example.myapp {
    requires java.sql;
    requires transitive com.example.common;
    exports com.example.myapp.api;
    exports com.example.myapp.internal to com.example.test;
    provides com.example.spi.SpellChecker with com.example.myapp.EnglishSpellChecker;
    uses com.example.spi.DictionaryService;
}
```

## 2. Exports

### Regular Export
```java
exports com.example.myapp.api; // accessible to all modules
```

### Qualified Export
```java
exports com.example.myapp.internal to com.example.test; // only test module
```

## 3. Requires

| Directive | Meaning |
|-----------|---------|
| `requires java.sql` | Read dependency at compile + runtime |
| `requires transitive M` | Anyone who reads my module also reads M |
| `requires static M` | Compile-only dependency (optional at runtime) |
| `requires transitive static M` | Compile-only, but transitive |

## 4. Services

### Service Provider Interface
```java
// In module com.example.spi
public interface SpellChecker {
    List<String> suggest(String word);
}
```

### Service Implementation
```java
// In module com.example.dictionary
public class EnglishSpellChecker implements SpellChecker { ... }
```

```java
// module-info for provider
module com.example.dictionary {
    requires com.example.spi;
    provides com.example.spi.SpellChecker with com.example.dictionary.EnglishSpellChecker;
}
```

### Service Consumer
```java
// In consuming code
ServiceLoader<SpellChecker> loaders = ServiceLoader.load(SpellChecker.class);
for (SpellChecker checker : loaders) {
    // use each implementation
}
```

```java
// module-info for consumer
module com.example.app {
    uses com.example.spi.SpellChecker;
}
```

## 5. Open Modules and Packages

```java
open module com.example.app { // allows deep reflection on all packages
    ...
}
// or per-package:
opens com.example.app.model;
// or qualified:
opens com.example.app.model to com.example.test;
```

## 6. Migration Strategies

| Approach | When to use |
|----------|------------|
| **Automatic modules** | Named JAR on module path → module name derived from JAR name |
| **Unnamed module** | Classpath code that reads all named modules |
| **`--add-exports`** | Quick workaround for inaccessible packages |
| **`--add-opens`** | For reflective access (frameworks) |

## 7. Common Module Names (JDK)

| Module | Contents |
|--------|----------|
| `java.base` | java.lang, java.util, java.io — always required |
| `java.sql` | JDBC |
| `java.net.http` | HttpClient |
| `java.compiler` | Compiler API |
| `jdk.incubator.vector` | Vector API (incubator) |
| `jdk.incubator.concurrent` | Structured concurrency (incubator) |
