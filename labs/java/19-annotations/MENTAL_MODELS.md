# Mental Models — Annotations

## Annotation as Label
An annotation is a sticky note attached to code. The note can be read by tools at compile time or runtime.

## Annotation as Configuration Object
An annotation is like a configuration object with named attributes, fixed at compile time:
```java
@Retention(RUNTIME) @Target(METHOD)  // ← meta-annotations configure configuration
@interface Loggable {
    String level() default "INFO";   // ← attribute with default
}
```

## Retention as Lifetime
- `SOURCE` — discard label before shipping
- `CLASS` — keep label in box but don't show at runtime
- `RUNTIME` — label visible during runtime via reflection

## Annotation Processor as Factory
A compile-time processor scans for annotations and generates code — like a factory reading labels on parts to assemble them.
