# Internals: Module System

## Module Layer
The module system is implemented in the `java.lang.module` package. Key classes:
- `Module`: Represents a runtime module
- `ModuleDescriptor`: Describes a module's metadata
- `ModuleLayer`: A graph of resolved modules
- `Configuration`: Resolved module graph (DAG)
- `ModuleFinder`: Finds modules on the module path

## Module Resolution Process
1. Root modules are identified from `--module` or main class
2. ModuleFinder locates modules on the module path
3. ModuleReference resolves to ModuleReader
4. Configuration computes the transitive closure
5. ModuleLayer defines the resolved module graph
6. Class loaders enforce module boundaries

## Module Readability
Each module has a set of readable modules. By default, a module reads only:
- java.base
- Modules it requires
- Modules that export packages to it

## Module Accessibility
A class from module M can access a class from module N only if:
- M reads N
- The package is exported from N
- The class is public (or the package is open for access)

## JVM Module System
Since Java 9, the JVM enforces module boundaries at the class loading level:
- `ClassLoader::loadClass` checks module accessibility
- `AccessibleObject::setAccessible` checks module openness
- `Module::addOpens` and `Module::addExports` at runtime

## Implementation in HotSpot
The module system is deeply integrated into the HotSpot JVM:
- Module descriptors are loaded and verified at startup
- The module graph is created during VM initialization
- Class loading consults the module graph for accessibility
- JVMTI supports module events (MODULE_LOAD, etc.)
- Module system also affects `System::getProperty` and resource access
