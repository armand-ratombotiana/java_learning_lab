# Performance — Annotations

## Reflection Cost
```java
method.getAnnotation(MyAnnotation.class); // ~0.5-2 µs per call
```
- Annotation instances are cached per element after first retrieval
- Repeated calls on the same element are faster (cache hit)

## Class File Size
Annotations add to .class file size. Large annotation attributes (embedded JSON, strings) inflate class files.

## Processor Overhead
Compile-time annotation processing adds to build time. For large codebases with heavy annotation processing, incremental compilation is essential.

## Runtime Proxy Creation
Dynamic proxies for annotations are created once and cached. The cost is a few microseconds per annotation type.

## Best Practices
- Cache annotation lookups in hot paths
- Avoid loading annotation classes at startup unless necessary
- Prefer compile-time processing (APT) over runtime reflection for performance-sensitive frameworks
