# Micronaut

Micronaut is a modern JVM-based framework for building modular microservices.

## Topics
- Compile-time DI (no reflection)
- AOT compilation benefits
- HTTP server and client annotations
- Reactive support (RxJava, Reactor)
- Micronaut Data for data access
- Native image support (GraalVM)
- Service discovery with Consul/Eureka

## Example
```java
@Controller("/api/products")
public class ProductController {
    @Get("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }
}
```
