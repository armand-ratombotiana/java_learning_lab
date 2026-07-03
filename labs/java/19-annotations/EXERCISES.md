# Exercises — Annotations

## Beginner
1. Create a `@LogExecution` annotation with a `level()` attribute defaulting to "INFO".
2. Apply `@Deprecated` and `@SuppressWarnings` to old methods.
3. Write a runtime processor that reads `@LogExecution` and prints method execution time.

## Intermediate
4. Create `@NotNull` annotation for method parameters and validate them at runtime.
5. Implement `@Repeatable` annotation `@Schedule(hour=...)` for a backup method.
6. Build a simple DI container that uses `@Inject` annotation to wire dependencies.

## Advanced
7. Write an annotation processor (APT) that generates builder classes for `@Builder`-annotated classes.
8. Create a `@Cached` annotation that proxies method calls with a simple in-memory cache.
9. Implement a `@Route(path="/api")` annotation and a request dispatcher.

## Debugging
10. Examine the annotation attributes in a compiled `.class` file using `javap -verbose`.

## Reflection
11. Use reflection to list all annotations on a class and their attributes.
