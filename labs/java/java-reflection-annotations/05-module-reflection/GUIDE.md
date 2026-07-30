# GUIDE — Module Reflection

## Step 1: Module Basics
- `module-info.java` declares `exports`, `opens`, `requires`
- `--add-opens` JVM flag for reflective access

## Step 2: Module API
```java
Module module = MyClass.class.getModule();
module.isNamed();           // true if named module
module.getDescriptor();     // ModuleDescriptor
module.getPackages();       // Set<String>
```

## Step 3: Opening Packages
```java
// At runtime
module.addOpens("com.example.internal", targetModule);
// Or via CLI: --add-opens com.example/com.example.internal=ALL-UNNAMED
```

## Step 4: Module Layers
- `ModuleLayer.boot()` — the boot layer
- `ModuleLayer.defineModules()` — custom layers with own class loaders

## Step 5: Exercises
1. List all modules in the boot layer
2. Open a package reflectively and access a private class
3. Create a custom module layer with isolated dependencies
