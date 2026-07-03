# Debugging Modules

## Common Errors and Solutions

### Module Not Found
```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module com.example.myapp not found
```
- Check module path (-p option)
- Verify module name spelling
- Ensure module path exists

### Package Not Exported
```
error: package com.example.internal is not visible
  (package com.example.internal is declared in module com.example.lib, but it is not exported)
```
- Add exports to the provider module
- Or use --add-exports as temporary workaround

### Split Package
```
error: package com.example.shared exists in multiple modules: com.example.a, com.example.b
```
- Rename packages to be unique
- Merge the modules

### Reflection Access Denied
```
Unable to make field private final java.lang.String com.example.model.User.name accessible
```
- Add `opens com.example.model to required.module` in module-info.java
- Or use --add-opens as temporary workaround

## Debugging Flags

```bash
# Show module resolution details
java --show-module-resolution -p mods -m com.example.myapp

# List observable modules
java --list-modules

# Describe a module
java -p mods -d com.example.myapp

# Add exports for debugging
java --add-exports java.base/com.sun.crypto.provider=ALL-UNNAMED ...

# Add opens for debugging
java --add-opens com.example.myapp/com.example.model=ALL-UNNAMED ...
```

## Using jdeps
```bash
# Analyze dependencies
jdeps -s myapp.jar

# Show module dependencies
jdeps --module-path mods --module com.example.myapp

# Suggest module-info.java
jdeps --generate-module-info out myapp.jar
```
