# Debugging Layered Architecture

## Common Issues

### 1. Layer Leakage
```java
// Check for direct repository injection in controllers
@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository; // LEAK! Use service instead
}
```

### 2. Transaction Boundaries
```java
// Ensure @Transactional is on service layer
@Service
public class UserService {
    @Transactional // CORRECT: transaction at business layer
    public void createUserWithProfile(CreateUserRequest req) {
        userRepository.save(new User(req));
        profileRepository.save(new Profile(req)); // Same transaction
    }
}
```

### 3. Lazy Loading Issues
```yaml
# If using lazy loading, handle in service layer
spring:
  jpa:
    open-in-view: false  # Avoid LazyInitializationException
```

### 4. Cross-Cutting Debug
```java
@Aspect
@Component
public class DebugAspect {
    @Before("execution(* com.company.service.*.*(..))")
    public void logEntry(JoinPoint jp) {
        log.debug("Entering service: {}.{} with args: {}",
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            jp.getArgs());
    }
}
```

### 5. Testing Isolation
```java
// Test each layer independently
@WebMvcTest(UserController.class)  // Presentation only
public class UserControllerTest { }

@SpringBootTest                     // Full context
public class UserServiceIntegrationTest { }

@DataJpaTest                       // Persistence only
public class UserRepositoryTest { }
```
