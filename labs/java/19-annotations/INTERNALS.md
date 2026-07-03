# Internals — Annotations

## Annotation Interface Structure
```java
// Compiled annotation looks like:
public interface MyAnnotation extends java.lang.annotation.Annotation {
    String value();
    int priority() default 0;
}
// The compiler creates a Proxy-based instance at runtime when getAnnotation() is called.
```

## Class File Storage
Annotations are stored in the `.class` file's `RuntimeVisibleAnnotations` attribute:
```
attribute_info {
    u2 attribute_name_index;         // "RuntimeVisibleAnnotations"
    u4 attribute_length;
    annotation annotations[attribute_length];
}
```

## Proxy Mechanism (Runtime)
```java
// getAnnotation() uses Proxy
public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
    return AnnotationParser.parseMemberAnnotation(...);
}
// Returns a dynamic proxy implementing annotationClass
```

## RetentionPolicy Enum
```java
enum RetentionPolicy {
    SOURCE,  // In source only
    CLASS,   // In class file, not accessible via reflection
    RUNTIME  // In class file, accessible via reflection
}
```

## ElementType Enum (Common)
```java
enum ElementType {
    TYPE, METHOD, FIELD, PARAMETER, CONSTRUCTOR,
    LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE,
    TYPE_PARAMETER, TYPE_USE  // Added in Java 8
}
```
