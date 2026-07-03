# How It Works — Annotations

## Annotation Compilation
1. `@interface` defines an annotation type
2. Compiler generates a synthetic interface extending `java.lang.annotation.Annotation`
3. Attributes become abstract methods in the interface

## Retention Policy Handling
- **SOURCE:** annotation erased during compilation, not in .class
- **CLASS:** stored in the `RuntimeVisibleAnnotations` or `RuntimeInvisibleAnnotations` attribute of the class file
- **RUNTIME:** same as CLASS, but also marked with `RuntimeVisibleAnnotations`

## Reflection Retrieval
```java
// Class.getAnnotation() reads from class file's annotation attribute
Loggable a = method.getAnnotation(Loggable.class);
// Internally: searches the RuntimeVisibleAnnotations attribute for Loggable
```

## Annotation Processor (javac)
1. javac runs processors registered via META-INF/services
2. Processor receives `RoundEnvironment` with annotated elements
3. Processor can generate new source files, resources, or fail compilation

## Marker Annotations
An annotation with no attributes — used as a boolean flag:
```java
@Retention(RUNTIME) @interface Auditable {}
```
