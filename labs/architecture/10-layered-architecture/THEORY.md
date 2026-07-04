# Layered Architecture Theory

## Layers

### Presentation Layer
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
}
```

### Business Layer
```java
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return UserDto.from(user);
    }

    public UserDto createUser(CreateUserRequest request) {
        // Business validation
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        User user = new User(request.getName(), request.getEmail());
        return UserDto.from(userRepository.save(user));
    }
}
```

### Persistence Layer
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### Cross-Cutting Layer
```java
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.company.service.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Entering: {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        log.info("Exiting: {}", joinPoint.getSignature());
        return result;
    }
}
```

## Layer Rules
- Presentation depends on Business
- Business depends on Persistence
- Cross-cutting applies to all layers
- No skipping layers (no controller -> repository direct calls)
- No upward dependencies (lower layers don't import higher layers)
