# Debugging — Annotations

## Checking Annotation Presence
```java
if (method.isAnnotationPresent(LogExecution.class)) {
    LogExecution log = method.getAnnotation(LogExecution.class);
}
```

## Debugging Annotation Processors
Add `-XprintRounds` and `-XprintProcessorInfo` to javac:
```bash
javac -XprintRounds -XprintProcessorInfo -processor MyProcessor MyFile.java
```

## Common Issues
- `NullPointerException` from `getAnnotation()` → check `isAnnotationPresent()` first
- `AnnotationFormatError` → class file corrupted, recompile
- Processor not running → check `META-INF/services/javax.annotation.processing.Processor`

## Runtime Inspection Dump
```java
for (Annotation a : method.getDeclaredAnnotations()) {
    System.out.println(a.annotationType().getName() + ": " + a);
}
```

## IDE Support
- IntelliJ: Structure view shows annotations
- Eclipse: Outline view with annotation column
- VS Code: hover to see annotations
