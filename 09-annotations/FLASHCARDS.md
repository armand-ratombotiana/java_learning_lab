# Annotations Flashcards

## Q1: What is an annotation?
**A**: A metadata construct that can be applied to Java code elements (classes, methods, fields) to provide additional information without changing behavior.

---

## Q2: What are the three retention policies?
**A**: 
- SOURCE: Only in source, removed at compile time
- CLASS: In bytecode, not available at runtime
- RUNTIME: Available via reflection at runtime

---

## Q3: What does @Target define?
**A**: Specifies which Java elements an annotation can be applied to (TYPE, METHOD, FIELD, PARAMETER, etc.)

---

## Q4: What's the difference between getAnnotations() and getDeclaredAnnotations()?
**A**: getAnnotations() returns all annotations including inherited ones. getDeclaredAnnotations() returns only directly present annotations on the element.

---

## Q5: What is a meta-annotation?
**A**: An annotation that is applied to other annotations. Examples: @Target, @Retention, @Inherited, @Documented, @Repeatable

---

## Q6: How do you create a repeatable annotation?
**A**: 
1. Create the annotation with @Repeatable meta-annotation
2. Create a container annotation that holds an array of the repeatable annotation
```java
@Repeatable(Tags.class)
public @interface Tag { String value(); }

public @interface Tags { Tag[] value(); }
```

---

## Q7: What annotation processor should you extend?
**A**: AbstractProcessor from javax.annotation.processing

---

## Q8: What is the difference between @Component and @Service in Spring?
**A**: Functionally equivalent—@Service is a specialized form of @Component that indicates a service layer. The semantic difference improves code readability.

---

## Q9: What is @Inherited used for?
**A**: Makes an annotation inherited from parent class to child classes. When a class is annotated, its subclasses automatically have that annotation.

---

## Q10: What does RUNTIME retention enable?
**A**: Reading annotations via reflection at runtime. Required for frameworks like Spring, JPA, JUnit that need to process annotations at runtime.

---

## Q11: Can annotations have methods?
**A**: No, they have elements (which look like methods but aren't). Annotation elements must return a value, not void.

---

## Q12: What is the default retention policy?
**A**: CLASS. Annotations without explicit @Retention default to CLASS retention.

---

## Q13: What's the purpose of @Documented?
**A**: Causes the annotation to be included in the javadoc-generated documentation for elements that use it.

---

## Q14: What annotation should you use for compile-time validation?
**A**: RetentionPolicy.SOURCE - the annotation will be removed after compilation but available during annotation processing.

---

## Q15: What's a marker annotation?
**A**: An annotation with no elements—its presence alone carries meaning. Example: @Override, @Test

---

## Q16: What does @Valid trigger in Spring?
**A**: Triggers validation on the annotated object using Bean Validation (JSR-380) constraints.

---

## Q17: How do annotations differ from comments?
**A**: Annotations are structured, machine-readable metadata that can be processed by compilers, tools, and frameworks. Comments are human-readable and ignored by machines.

---

## Q18: What is a single-element annotation?
**A**: An annotation with a single element named "value" that allows shorthand syntax: @Component("myBean") instead of @Component(value="myBean")