# Mock Interview Transcript: Annotations

## Interviewer: Senior SWE, Microsoft
## Candidate: Mid-level Java developer
## Time: 30 minutes
## Focus: Annotation processing, retention policies, custom annotations

---

**Q1: Explain the three annotation retention policies.**

**Candidate**: `RetentionPolicy.SOURCE` — discarded by the compiler, not in .class files (e.g., `@Override`, `@SuppressWarnings`). `RetentionPolicy.CLASS` — kept in .class files but not loaded by JVM at runtime (default). `RetentionPolicy.RUNTIME` — available via reflection at runtime (e.g., `@Test`, `@Deprecated`).

**Interviewer**: What's the practical difference between CLASS and RUNTIME?

**Candidate**: CLASS retention is for compile-time processing (like Lombok's `@Getter` — generates code during compilation). RUNTIME retention is for frameworks that use reflection (Spring's `@Autowired`, JUnit's `@Test`). CLASS annotations are visible in bytecode but not in the running application.

**Interviewer**: What annotations can be applied to annotations themselves?

**Candidate**: Meta-annotations: `@Retention`, `@Target`, `@Documented`, `@Inherited`, `@Repeatable`. `@Target` specifies where the annotation can be used (TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE, TYPE_PARAMETER, TYPE_USE).

**Interviewer**: Create a custom annotation for logging method execution time.

**Candidate**: 
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogExecutionTime {
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
```

**Interviewer**: How would you process this annotation at runtime?

**Candidate**: Using reflection with a proxy or AOP (Spring AOP/AspectJ):
```java
public class TimingAspect {
    
    @Around("@annotation(LogExecutionTime)")
    public Object logTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        Object result = pjp.proceed();
        long elapsed = System.nanoTime() - start;
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        LogExecutionTime ann = method.getAnnotation(LogExecutionTime.class);
        long converted = ann.unit().convert(elapsed, TimeUnit.NANOSECONDS);
        System.out.println(method.getName() + " took " + converted + " " + ann.unit());
        return result;
    }
}
```

**Interviewer**: How does compile-time annotation processing work (like Lombok)?

**Candidate**: Using `AbstractProcessor` in the `javax.annotation.processing` package. The processor runs during compilation (`javac`), generates new source files or modifies AST. Lombok uses internal javac APIs (not standard processing) to directly modify the AST tree because `@Getter` needs to add methods to existing classes, which standard annotation processing can't do.

**Interviewer**: What's a repeatable annotation?

**Candidate**: 
```java
@Repeatable(Schedules.class)
public @interface Schedule { String day(); }

public @interface Schedules { Schedule[] value(); }

@Schedule(day="Mon") @Schedule(day="Wed")
void task() { }
```
Java 8+ allows multiple annotations of the same type on a single element. The container annotation (`Schedules`) holds the array.

**Interviewer**: How does `@Inherited` work?

**Candidate**: `@Inherited` makes an annotation apply to subclasses of an annotated class. If class `Parent` has `@Inherited @MyAnnotation`, then `Child extends Parent` also has `@MyAnnotation` (when checked via `getAnnotation()`). However, `@Inherited` only works for class-level annotations, not for methods or fields.

---

## Feedback

**Strengths**:
- Complete understanding of retention policies and use cases
- Correct custom annotation design with default value
- Knows AOP-based annotation processing
- Understands Lombok's AST-level processing

**Areas for Improvement**:
- Could discuss `@Native` annotation and its role
- Mention that `@Inherited` doesn't work with interfaces

**Score**: 4/5 — Strong annotation knowledge
