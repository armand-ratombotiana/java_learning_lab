# Theory — Annotations

## What Are Annotations?
Metadata attached to code elements (classes, methods, fields, etc.) that can be processed at compile-time or runtime.

## Built-in Annotations
```java
@Override  // Ensures method overrides a superclass/interface method
@Deprecated  // Marks element as obsolete
@SuppressWarnings("unchecked")  // Suppresses specified warnings
@FunctionalInterface  // Interface has exactly one abstract method
```

## Custom Annotation Definition
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Loggable {
    String level() default "INFO";
    boolean includeArgs() default false;
}
```

## Retention Policies
| Policy | Description |
|--------|-------------|
| `SOURCE` | Discarded by compiler (e.g., `@SuppressWarnings`) |
| `CLASS` | Stored in .class file but not accessible at runtime |
| `RUNTIME` | Accessible at runtime via reflection |

## Annotation Processing
Runtime retrieval via reflection:
```java
Method m = MyClass.class.getMethod("doSomething");
Loggable log = m.getAnnotation(Loggable.class);
if (log != null) System.out.println(log.level());
```

## Repeatable Annotations
```java
@Repeatable(Schedules.class)
@interface Schedule { int hour(); }

@Schedule(hour=9) @Schedule(hour=17)
void backup() {}
```
