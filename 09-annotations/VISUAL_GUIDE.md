# Visual Guide to Annotations

## Annotation Syntax

```
@AnnotationName(element = "value", priority = 5)
```

```
┌─────────────────────────────────────────────┐
│ @ Symbol - Marks annotation application    │
└─────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│ AnnotationName - The annotation type        │
└─────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│ Elements - Key-value pairs in parentheses  │
│   - value = "string"                        │
│   - priority = 5                            │
│   - enabled() = true                        │
└─────────────────────────────────────────────┘
```

## Annotation Types Diagram

```
┌─────────────────────────────────────────────┐
│         Java Annotation Hierarchy           │
├─────────────────────────────────────────────┤
│                                             │
│  java.lang.annotation.Annotation            │
│           │                                 │
│           ▼                                 │
│  ┌───────────────┐                         │
│  │ @interface    │  <- Custom annotation   │
│  │ MyAnnotation  │     definition           │
│  └───────────────┘                         │
│                                             │
├─────────────────────────────────────────────┤
│         Meta-Annotations                     │
├─────────────────────────────────────────────┤
│  @Target    - Where annotation can apply    │
│  @Retention - When annotation is available │
│  @Documented - Include in javadoc          │
│  @Inherited - Allow subclass inheritance   │
│  @Repeatable - Allow multiple applications │
└─────────────────────────────────────────────┘
```

## Element Types Diagram

```
┌─────────────────────────────────────────────┐
│         @Target ElementType Values          │
├─────────────────────────────────────────────┤
│                                             │
│  TYPE      → Applied to classes/interface   │
│  FIELD     → Applied to instance variables │
│  METHOD    → Applied to methods            │
│  PARAMETER → Applied to method parameters │
│  CONSTRUCTOR → Applied to constructors    │
│  LOCAL_VARIABLE → Applied to local vars    │
│  PACKAGE   → Applied to package statements │
│  ANNOTATION_TYPE → Applied to annotations  │
│  TYPE_PARAMETER → Applied to type params  │
│  TYPE_USE  → Applied to any type usage    │
│                                             │
└─────────────────────────────────────────────┘
```

## Retention Timeline

```
Source Code (.java)
    │
    ▼
┌─────────────────────────────────────────────┐
│  SOURCE retention                           │
│  (Processed, then removed)                  │
│  ✓ Available during compile                │
│  ✗ Not in .class file                       │
│  ✗ Not available at runtime                │
└─────────────────────────────────────────────┘
    │
    ▼
Compiled (.class)
    │
    ▼
┌─────────────────────────────────────────────┐
│  CLASS retention                            │
│  (Stored in bytecode)                      │
│  ✓ Available during compile                │
│  ✓ In .class file                          │
│  ✗ Not available at runtime (via reflection)│
└─────────────────────────────────────────────┘
    │
    ▼
Runtime (JVM)
    │
    ▼
┌─────────────────────────────────────────────┐
│  RUNTIME retention                          │
│  (Available via reflection)                │
│  ✓ Available during compile                │
│  ✓ In .class file                          │
│  ✓ Available at runtime                    │
└─────────────────────────────────────────────┘
```

## Common Framework Annotations Map

```
┌──────────────────────────────────────────────────────────┐
│              Spring Framework                            │
├──────────────────────────────────────────────────────────┤
│  @Component    → Spring bean (generic)                   │
│  @Service      → Service layer bean                     │
│  @Repository   → Data access bean                       │
│  @Controller   → Web controller                         │
│  @Autowired    → Dependency injection                  │
│  @Transactional→ Transaction management                 │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│                  JPA/Hibernate                           │
├──────────────────────────────────────────────────────────┤
│  @Entity       → Database entity                         │
│  @Table        → Database table mapping                 │
│  @Id           → Primary key                            │
│  @Column       → Column mapping                          │
│  @OneToMany    → One-to-many relationship               │
│  @ManyToOne    → Many-to-one relationship               │
└──────────────────────────────────────────────────────────┘