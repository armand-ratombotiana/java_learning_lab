# Refactoring: Modules

## Refactoring Pattern 1: Non-Modular to Modular

### Before (classpath)
```
my-lib.jar: com.example.my lib.* classes
```

### After (modular JAR)
```
module-info.java:
module com.example.my {
    exports com.example.my.api;
    exports com.example.my.spi;
}
```

## Refactoring Pattern 2: Internal Package Hiding

### Before
```java
// All public classes visible to everyone
public class InternalHelper {
    public void doInternalStuff() { ... }
}
```

### After
```java
// Module only exports API packages
module com.example.my {
    exports com.example.my.api;
    // com.example.my.internal is NOT exported
}
```

## Refactoring Pattern 3: Service Provider Introduction

### Before
```java
// META-INF/services/com.example.spi.Service
```

### After
```java
module com.example.impl {
    provides com.example.spi.Service
        with com.example.impl.ServiceImpl;
}
```

## Refactoring Pattern 4: Multi-Module Project

### Before
```
app.jar, lib-a.jar, lib-b.jar all on classpath
```

### After
```
com.example.app/ requires com.example.lib.a, requires com.example.lib.b
com.example.lib.a/ exports com.example.lib.a.api
com.example.lib.b/ exports com.example.lib.b.api
```

## Migration Strategy
1. Use jdeps to analyze existing dependencies
2. Start with leaf modules (no dependents)
3. Add module-info.java incrementally
4. Use automatic modules for unmodularized dependencies
5. Replace automatic modules with named modules as libraries upgrade
6. Use --add-exports and --add-opens temporarily
7. Test with module path before switching completely
