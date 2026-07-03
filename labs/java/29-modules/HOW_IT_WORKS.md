# How the Module System Works

## Module Descriptor (module-info.java)
```java
module com.example.myapp {
    requires java.sql;                          // Dependencies
    requires transitive com.example.common;     // Transitive dependency
    exports com.example.myapp.api;             // Exported packages
    exports com.example.myapp.spi to            // Qualified export
        com.example.plugin;
    opens com.example.myapp.model;             // Opened for reflection
    provides com.example.spi.Service            // Service provider
        with com.example.myapp.MyServiceImpl;
    uses com.example.spi.AnotherService;        // Service consumer
}
```

## Compilation and Execution
```bash
# Compile
javac -d mods/com.example.myapp src/com.example.myapp/module-info.java src/com.example.myapp/*.java

# Package
jar --create --file mods/com.example.myapp.jar -C mods/com.example.myapp .

# Run with module path
java --module-path mods --module com.example.myapp/com.example.myapp.Main
```

## Module Resolution
The module system resolves modules by:
1. Starting from the root module (specified via --module)
2. Following requires directives
3. Building a complete module graph
4. Checking for missing modules or cycles
5. Creating the configuration for class loading

## Service Loading
```java
// In module-info.java of service consumer:
uses com.example.spi.Service;

// In service consumer code:
ServiceLoader<Service> loader = ServiceLoader.load(Service.class);
for (Service service : loader) {
    service.execute();
}
```
