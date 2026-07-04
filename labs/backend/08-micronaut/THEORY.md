# Theory: Micronaut

## Compile-Time DI
Unlike Spring's runtime reflection-based DI, Micronaut processes annotations at compile time.

### Benefits
- **Fast startup**: No reflection, no classpath scanning at runtime
- **Low memory**: Smaller heap footprint
- **GraalVM compatible**: AOT compilation works naturally
- **Compile-time validation**: Errors caught during compilation

### AOT Compilation
- Annotation processors generate BeanDefinition classes
- No runtime proxy generation (CGLIB/JDK proxies)
- Ahead-of-Time compilation reduces startup to milliseconds

## Key Annotations
- @Singleton: Singleton bean
- @Context: Eager singleton
- @Requires: Conditional bean (like @Conditional)
- @Factory: Factory for bean creation
- @Client: HTTP client declaration

## HTTP Server
Micuriont's Netty-based HTTP server is non-blocking by default.
