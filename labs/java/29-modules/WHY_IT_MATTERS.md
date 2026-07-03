# Why Modules Matter

The module system fundamentally changes Java application architecture:
- **Explicit dependencies**: No more missing JARs at deployment time
- **Smaller runtime**: jlink creates custom runtime images with only needed modules
- **Better API design**: Libraries clearly define their public API through exports
- **Encapsulation**: Internal packages can be refactored without breaking clients
- **Security**: Critical internal APIs are no longer accessible
- **Performance**: Module graph enables optimized startup and linking

For library authors, modules provide a way to hide implementation details that was previously impossible. For application developers, modules provide reliability and tooling support for dependency management.
