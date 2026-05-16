# Interview Questions: Java Annotations

## Basic Questions

### Q1: What is the difference between @Component and @Service?
**A**: @Service is a specialized @Component for the service layer. They function identically but provide semantic clarity and IDE tooling benefits.

### Q2: How do custom annotations work?
**A**: You define an annotation using @interface, specify @Target and @Retention, then process it either at compile time with AnnotationProcessor or at runtime via reflection.

### Q3: Explain @Target and @Retention
**A**: @Target specifies where an annotation can be applied (METHOD, FIELD, TYPE, etc.). @Retention specifies when annotation metadata is available (SOURCE, CLASS, or RUNTIME).

### Q4: What is annotation processing in Java?
**A**: A compile-time mechanism where classes implementing AnnotationProcessor can read annotations and generate new code, report errors, or perform validation.

## Intermediate Questions

### Q5: How would you create a custom validation annotation?
**A**: 
1. Create annotation with @Retention(RUNTIME) and @Target(FIELD)
2. Implement a validation processor using Bean Validation API
3. Use reflection to read annotation and validate at runtime

### Q6: What is the difference between getAnnotation() and getAnnotations()?
**A**: getAnnotation(class) returns specific annotation type. getAnnotations() returns all annotations including inherited ones. getDeclaredAnnotations() returns only directly present annotations.

### Q7: How do repeatable annotations work in Java 8+?
**A**: Use @Repeatable meta-annotation pointing to a container annotation. Apply multiple instances of the repeatable annotation to the same element.

### Q8: When would you use SOURCE retention vs RUNTIME?
**A**: SOURCE for linters and documentation tools. RUNTIME for frameworks needing reflection access (Spring, JPA). CLASS for bytecode manipulation tools.

## Advanced Questions

### Q9: How does Spring use annotations for dependency injection?
**A**: Spring scans classpath for @Component (or specializations like @Service, @Repository), creates beans, and uses @Autowired to inject dependencies by type or name.

### Q10: How would you implement an annotation processor?
**A**: Extend AbstractProcessor, implement process() method, use ProcessingEnvironment to generate files with Filer, report errors with Messager, and navigate elements with Trees.

### Q11: How do you handle annotation inheritance?
**A**: Use @Inherited meta-annotation on your annotation. It only applies to class-level annotations and causes subclasses to inherit the annotation from parent.

### Q12: What are type annotations in Java 8?
**A**: Annotations that can be applied to any type use—not just declarations. Useful for null checking, type safety, and integration with tools like null checkers.

### Q13: Explain the annotation processing pipeline
**A**: 
1. Compiler identifies all annotations
2. Processors run in rounds
3. Each processor can generate new files
4. New files are processed in subsequent rounds
5. Processing continues until no new files generated

### Q14: How would you create a composed annotation in Spring?
**A**: 
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RestController // Compose Spring annotations
@RequestMapping
public @interface JsonPostMapping {}
```

## Scenario-Based Questions

### Q15: How do you read an annotation value at runtime?
**A**:
```java
Class<?> clazz = MyClass.class;
MyAnnotation ann = clazz.getAnnotation(MyAnnotation.class);
if (ann != null) {
    String value = ann.value();
}
```

### Q16: How do you process annotations across multiple classes at compile-time?
**A**: Use RoundEnvironment.getElementsAnnotatedWith() to get all annotated elements, iterate through them, and process each based on Element type.