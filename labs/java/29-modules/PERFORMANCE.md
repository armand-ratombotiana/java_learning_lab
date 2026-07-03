# Performance: Modules

## Startup Performance
- **Module path resolution**: The module system builds the module graph at startup, which adds ~20-50ms overhead
- **Class loading**: Module accessibility checks add minimal overhead per class load
- **jlink**: Custom runtimes reduce startup time by excluding unused modules (fewer classes to scan)
- **AOT compilation**: Module-aware AOT (jaotc) can produce better optimized code

## Runtime Performance
- **Module boundary checks**: Add minimal overhead (single check during class loading, cached)
- **ServiceLoader**: Module-aware ServiceLoader is faster than the legacy META-INF/services approach
- **Linking**: Module-based linking enables more aggressive optimizations
- **Memory**: Module metadata adds ~1-5% memory overhead over classpath

## Module Path Performance
- **No classpath scanning**: Module path avoids filesystem scanning for classes
- **Explicit dependencies**: No wasted class loading for unrelated classes
- **Optimized loading**: Module system knows exactly which classes to load

## jlink Advantages
- **Smaller image**: Custom runtime is 80-90% smaller than full JDK
- **Faster startup**: Fewer modules to initialize
- **Lower memory**: Only required classes loaded
- **Simplified deployment**: Self-contained runtime with application

## Performance Tips
- Use jlink for production deployments
- Keep module graph shallow (fewer levels of transitive dependencies)
- Use requires transitive sparingly (it increases the read graph)
- Avoid qualified opens and exports (they require per-module tracking)
- Prefer provides/uses over META-INF/services
