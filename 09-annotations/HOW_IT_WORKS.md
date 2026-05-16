# How Annotations Work

## The Annotation Interface

At its core, an annotation is just an interface that extends `Annotation`:

```java
public interface MyAnnotation extends Annotation {
    String value();
    int priority() default 0;
}
```

When you define `@interface MyAnnotation`, the Java compiler generates an interface that implements `java.lang.annotation.Annotation`.

## Retention Policies

Annotations have three retention levels determining when they are available:

### SOURCE
Only available in source code. Discarded during compilation.
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Override // Built-in annotation
```
Used for: Linters, documentation tools, source-level code generation.

### CLASS
Available in compiled bytecode but not at runtime. Requires bytecode analysis tools.
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
```
Used for: Compile-time validation, bytecode manipulation frameworks.

### RUNTIME
Available via reflection at runtime. This is what most frameworks use.
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Entity // JPA annotation
```
Used for: Dependency injection, serialization, validation frameworks.

## Processing Pipeline

### Compile-Time Processing
1. Source code with annotations is compiled
2. Annotation processors (implementing `AnnotationProcessor`) run
3. Processors can:
   - Generate new source files
   - Report errors/warnings
   - Process annotations in round phases

### Runtime Processing (Reflection)
1. Load class with `Class.forName()`
2. Use `getAnnotations()` or `getDeclaredAnnotations()`
3. Read annotation values via annotation type interface
4. Take action based on metadata

## Example: Custom Validation Processor

```java
@SupportedAnnotationTypes("com.example.NotNull")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class NotNullProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(NotNull.class)) {
            if (!isValidField(element)) {
                processingEnv.getMessager().printError(
                    "@NotNull applied to non-field: " + element.getSimpleName());
            }
        }
        return true;
    }
}
```

## Annotation Inheritance

- Annotations on classes don't automatically apply to subclasses
- Use `@Inherited` meta-annotation to enable inheritance
- Only works for classes, not interfaces or methods