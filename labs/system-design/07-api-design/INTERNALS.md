# API Design - INTERNALS

## HTTP Method Semantics

### Safe vs Idempotent vs Neither
| Method | Safe | Idempotent | Use Case |
|--------|------|-----------|----------|
| GET | Yes | Yes | Read resource |
| HEAD | Yes | Yes | Read headers only |
| PUT | No | Yes | Full replacement |
| DELETE | No | Yes | Remove resource |
| POST | No | No | Create / action |
| PATCH | No | No | Partial update |

## Content Negotiation

### Server-Driven
```java
// Client sends Accept header
Accept: application/json
// Server responds with appropriate format
@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "application/xml"})
public Product getProduct(@PathVariable String id) { /* ... */ }
```

### Spring Content Negotiation
```yaml
spring:
  contentnegotiation:
    favor-parameter: true
    parameter-name: format
    media-types:
      json: application/json
      xml: application/xml
```
Client: `GET /products/123?format=xml`

## CORS

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

## Conditional Requests

```java
@GetMapping("/{id}")
public ResponseEntity<Product> getProduct(@PathVariable String id,
        @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
    Product product = productService.findById(id);
    String eTag = computeETag(product);

    if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    return ResponseEntity.ok().eTag(eTag).body(product);
}
```

## ETag Filter

```java
@Component
public class ETagFilter extends ShallowEtagHeaderFilter {
    // Automatically generates ETags for 200 OK responses
    // Returns 304 Not Modified if client sends matching If-None-Match
}
```

## OpenAPI Documentation Generation

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Product API")
                .version("1.0")
                .description("API for managing products"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new Components()
                .addSecuritySchemes("Bearer", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")));
    }
}
```
