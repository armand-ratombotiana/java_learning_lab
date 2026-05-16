# API Design - REAL WORLD PROJECT

Production REST API with:
- Versioning
- Rate limiting
- Caching headers
- OpenAPI docs

```java
@Configuration
public class ApiConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor);
    }
}
```