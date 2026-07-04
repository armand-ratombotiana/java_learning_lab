# Layered Architecture Performance

## Performance Considerations

### Layer Overhead
```java
// Each layer adds minimal overhead
@RestController
public class ProductController {
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        long start = System.nanoTime();
        ProductResponse response = productService.getProduct(id);
        long duration = System.nanoTime() - start;
        if (duration > 1_000_000_000) { // >1 second
            log.warn("Slow request: {} took {}ms", id, duration / 1_000_000);
        }
        return response;
    }
}
```

### Caching at Service Layer
```java
@Service
public class ProductService {

    @Cacheable("products")
    public ProductResponse getProduct(Long id) {
        return ProductResponse.from(
            productRepository.findById(id).orElseThrow()
        );
    }
}
```

### Database Optimization
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
}
```

## Performance by Layer
| Layer | Typical Latency | Optimization |
|-------|----------------|--------------|
| Controller | 1-5ms | Keep thin |
| Service | 10-100ms | Cache, batch |
| Repository | 10-50ms | Index, join fetch |
| Database | 20-200ms | Query optimization |

## Monitoring
```java
@Aspect
@Component
public class LayerPerformanceAspect {
    @Around("execution(* com.company.service.*.*(..))")
    public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
        return Timer.start().record(() -> pjp.proceed());
    }
}
```
