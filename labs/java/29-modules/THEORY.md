# Theory: Module System

## Module Definition
A module is a named, self-describing collection of code and data. The module descriptor (module-info.java) declares:
- Module name
- Required dependencies (requires)
- Exported packages (exports)
- Opened packages for reflection (opens)
- Services provided and consumed (provides/uses)

## Module Graph
Modules form a directed acyclic graph (DAG) of dependencies. The module system verifies at compile and startup time:
- All required modules exist
- No circular dependencies
- All packages are properly exported
- All services have implementations

## Strong Encapsulation
By default, packages are not accessible outside their module. Only explicitly exported packages are visible to other modules. This prevents accidental access to internal APIs and enables library maintainers to refactor internal code freely.

## Module Path vs Classpath
The module path replaces the classpath for modular applications. Modules on the module path:
- Have explicit dependencies checked at startup
- Cannot access packages from other modules unless exported
- Automatically resolve transitive dependencies
- Unnamed modules (classpath) can access named modules' exported packages

## Service Loading
Modules can provide service implementations and consume services through ServiceLoader. This replaces the traditional META-INF/services approach with module-declared services.
