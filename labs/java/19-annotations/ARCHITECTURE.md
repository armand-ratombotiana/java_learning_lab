# Architecture — Annotations

## Annotation Layers
```
Application Annotations
    @Entity, @Service, @RestController
         │
Framework-Specific Annotations
    Spring: @Autowired, @Component, @Transactional
    JPA: @Entity, @Column, @Id
    JAX-RS: @GET, @Path
         │
Meta-Annotations (Java Platform)
    @Retention, @Target, @Documented, @Inherited, @Repeatable
         │
Base Annotation Interface
    java.lang.annotation.Annotation
```

## APT Processing Pipeline
```
Source Files
    │
    ▼
javac (with annotation processors)
    │
    ├── First Round: Process annotations, generate new files
    ├── Second Round: Process generated files (if they contain annotations)
    └── Final Round: No more annotations → compile
    │
    ▼
Compiled Classes + Generated Sources
```

## Framework Annotation Discovery
1. Classpath scanning (Spring: `@ComponentScan`)
2. Reflection-based (JPA: `META-INF/persistence.xml`)
3. Compile-time code generation (Lombok: `@Getter`, `@Setter`)
