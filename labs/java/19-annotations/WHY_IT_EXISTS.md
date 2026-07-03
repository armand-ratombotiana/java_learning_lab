# Why Annotations Exist

## Declarative Metadata
Instead of configuration files or marker interfaces, annotations embed metadata directly in source code.

## Compile-Time Safety
```java
@Override  // Compiler catches typos in method names
```

## Framework Integration
Annotations power:
- Spring (`@Autowired`, `@Component`, `@Transactional`)
- JPA/Hibernate (`@Entity`, `@Column`, `@Id`)
- JAX-RS (`@GET`, `@Path`, `@Produces`)
- Testing (`@Test`, `@BeforeEach`)
- Dependency injection

## Eliminate Boilerplate
Before annotations, XML configuration was the norm. Annotations reduce configuration overhead.
