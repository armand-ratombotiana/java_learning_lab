# Micronaut Framework Quiz

## Assessment Questions

### Question 1
What is the primary difference between Micronaut's DI system and Spring's?

A) Micronaut uses runtime reflection like Spring
B) Micronaut performs dependency injection at compile time
C) Micronaut doesn't support dependency injection
D) Micronaut only uses constructor injection

### Question 2
Which annotation is used to mark a class as a singleton bean in Micronaut?

A) @Component
B) @Bean
C) @Singleton
D) @Service

### Question 3
What is AOT compilation in Micronaut?

A) Aspect-Oriented Transformation
B) Ahead-Of-Time compilation
C) Annotation Order Transfer
D) Application Optimization Tool

### Question 4
Which GraalVM feature allows creating native executables from Java applications?

A) Graal Compiler
B) Truffle Framework
C) Native Image
D) Polyglot Runtime

### Question 5
What does Micronaut Data provide for database access?

A) Runtime ORM with reflection
B) Compile-time data access with type-safe queries
C) JDBC template-based access
D) XML-based query mapping

### Question 6
Which annotation is used to create an HTTP endpoint in Micronaut?

A) @RestController
B) @Controller
C) @Endpoint
D) @RequestMapping

### Question 7
What is the main benefit of Micronaut's compile-time DI?

A) More flexible configuration
B) Faster startup time and lower memory usage
C) Better runtime performance
D) Simpler code structure

### Question 8
How do you inject a bean by name in Micronaut?

A) @Inject(name="beanName")
B) @Inject @Named("beanName")
C) @Qualifier("beanName")
D) @Autowire("beanName")

### Question 9
Which HTTP client annotation is used to define a service client?

A) @FeignClient
B) @Client
C) @RestClient
D) @HttpClient

### Question 10
What is the purpose of @ConfigurationProperties in Micronaut?

A) To create custom configuration annotations
B) To bind external configuration to Java beans
C) To encrypt configuration values
D) To validate configuration

### Question 11
Which of these is NOT a scope available in Micronaut?

A) @Prototype
B) @Singleton
C) @Session
D) @Context

### Question 12
How do you enable security in Micronaut?

A) Add spring-security dependency
B) Configure micronaut.security.enabled=true
C) Add micronaut-security dependency
D) Both B and C

### Question 13
What reactive type does Micronaut use for single value emissions?

A) Flux
B) Mono
C) Single
D) Observable

### Question 14
What is the default server port in Micronaut?

A) 8080
B) 9090
C) 8443
D) 8888

### Question 15
Which annotation is used to define a query method in Micronaut Data repository?

A) @Query
B) @Select
C) Both A and B can be used
D) @Find

---

## Answer Key

1. **B** - Micronaut performs dependency injection at compile time
2. **C** - @Singleton marks a singleton bean
3. **B** - AOT stands for Ahead-Of-Time compilation
4. **C** - Native Image creates native executables
5. **B** - Micronaut Data provides compile-time data access
6. **B** - @Controller creates HTTP endpoints
7. **B** - Compile-time DI provides faster startup and lower memory
8. **B** - @Inject @Named("beanName") injects by name
9. **B** - @Client defines HTTP client interfaces
10. **B** - @ConfigurationProperties binds external config
11. **C** - @Session is not a Micronaut scope (that's servlet-specific)
12. **D** - Both B and C - security is enabled via config and dependency
13. **B** - Mono is used for single value emissions
14. **A** - 8080 is the default port
15. **C** - Both @Query and @Select can define query methods