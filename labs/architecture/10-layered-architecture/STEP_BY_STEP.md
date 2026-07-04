# Step-by-Step Layered Architecture

## Step 1: Create Entity
```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;
}
```

## Step 2: Create Repository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
```

## Step 3: Create Service
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        // Business validation
        User user = new User(request.getName(), request.getEmail());
        return UserResponse.from(userRepository.save(user));
    }
}
```

## Step 4: Create Controller
```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.createUser(req));
    }
}
```

## Step 5: Add Error Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

## Step 6: Add Validation
```java
public record CreateUserRequest(
    @NotBlank String name,
    @Email String email,
    @Size(min = 8) String password
) {}
```

## Step 7: Test
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "Alice", "email": "alice@test.com", "password": "pass1234"}
                """))
            .andExpect(status().isCreated());
    }
}
```
