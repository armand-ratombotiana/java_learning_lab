# Math Foundation for REST APIs

## Pagination Math
`java
int totalPages = (int) Math.ceil((double) totalElements / pageSize);
int offset = page * pageSize;
// LIMIT pageSize OFFSET offset
`

## Rate Limiting
`
Tokens per minute = capacity / time_window_seconds * 60
Backoff = min_backoff * 2 ^ (retry_count - 1)
`

## Exponential Backoff
`
next_delay = initial_delay * multiplier^attempt
// e.g., 1s, 2s, 4s, 8s, 16s, 32s...
`

## Content Hashing (ETag)
`
ETag = MD5(response_body)
If-None-Match: {ETag} -> 304 Not Modified
`

## Leaky Bucket Algorithm
`
request_rate = requests / time_window
if request_rate > limit -> 429 Too Many Requests
`
"@

New-LabFile "02-rest-apis" "VISUAL_GUIDE.md" @"
# Visual Guide to REST APIs

## Request Flow

`
  Client                     Spring Boot                    Service
    |                            |                            |
    |---- GET /api/users/1 ----->|                            |
    |                            |---- findById(1L) --------->|
    |                            |<---- User object ---------|
    |<---- 200 OK {JSON} --------|                            |
`

## Status Code Decision Tree

`
Request received
  +-- Valid syntax?
       +-- No -> 400 Bad Request
       +-- Yes -> Process request
            +-- Resource exists?
            |    +-- No -> 404 Not Found
            |    +-- Yes -> Continue
            +-- Authorized?
            |    +-- No -> 401/403
            |    +-- Yes -> Return
            |         +-- GET -> 200 OK
            |         +-- POST -> 201 Created
            |         +-- DELETE -> 204 No Content
            |         +-- Error -> 500 Internal
`

## Spring MVC REST Dispatching

`
DispatcherServlet
    |
    v
HandlerMapping -> matches URL to @RequestMapping
    |
    v
HandlerAdapter -> invokes controller method
    |
    v
HttpMessageConverter -> serializes response body
    |
    v
Response sent to client
`
"@

New-LabFile "02-rest-apis" "CODE_DEEP_DIVE.md" @"
# Code Deep Dive: REST APIs

## Complete REST Controller Pattern
`java
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserDTO> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserDTO created = userService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO updated = userService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
`

## Global Exception Handler
`java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(400, "Validation failed", errors));
    }
}
`
"@

New-LabFile "02-rest-apis" "STEP_BY_STEP.md" @"
# Step by Step: Building a REST API

## Step 1: Define the Resource Model
`java
public class User {
    private Long id;
    private String name;
    private String email;
    // getters/setters/constructors
}
`

## Step 2: Create Service Layer
`java
@Service
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
`

## Step 3: Create REST Controller
`java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public List<User> getAll() { return userService.findAll(); }
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) { return userService.findById(id); }
}
`

## Step 4: Add Exception Handling
`java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
`

## Step 5: Add Validation
`xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
`
`java
public class CreateUserRequest {
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 2) private String name;
}
`

## Step 6: Test with curl
`ash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@test.com"}'

# Get all users
curl http://localhost:8080/api/users

# Get a single user
curl http://localhost:8080/api/users/1

# Update a user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john@test.com"}'

# Delete a user
curl -X DELETE http://localhost:8080/api/users/1
`
"@

New-LabFile "02-rest-apis" "COMMON_MISTAKES.md" @"
# Common Mistakes in REST APIs

## 1. Returning Domain Entities Directly
`java
// WRONG: Exposes internal structure, circular references, security issues
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) { ... }

// CORRECT: Use DTOs
@GetMapping("/user/{id}")
public UserDTO getUser(@PathVariable Long id) { ... }
`

## 2. Wrong HTTP Methods
`java
// WRONG: Using GET for deletion
@GetMapping("/deleteUser/{id}")

// CORRECT: Use DELETE
@DeleteMapping("/{id}")
`

## 3. Ignoring Status Codes
`java
// WRONG: Always returning 200
return ResponseEntity.ok(null); // confuses client

// CORRECT: Use appropriate codes
return ResponseEntity.noContent().build(); // 204
return ResponseEntity.notFound().build();  // 404
`

## 4. Not Validating Input
`java
// WRONG: Missing @Valid
@PostMapping
public User createUser(@RequestBody User user) { ... }

// CORRECT: With validation
@PostMapping
public User createUser(@Valid @RequestBody User user) { ... }
`

## 5. Inconsistent Error Format
Always use a consistent ErrorResponse structure across all endpoints.

## 6. Exposing Stack Traces
`java
// WRONG: Sends internal details to client
return ResponseEntity.status(500).body(ex.getStackTrace());

// CORRECT: Generic message, log details
return ResponseEntity.status(500).body("An internal error occurred");
`
"@

New-LabFile "02-rest-apis" "DEBUGGING.md" @"
# Debugging REST APIs

## Enable Request Logging
`properties
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.mvc.method.annotation=DEBUG
`

## View All Mappings
`ash
curl http://localhost:8080/actuator/mappings | jq
`

## Common Issues and Solutions

### 415 Unsupported Media Type
`ash
# Fix: Add Content-Type header
curl -X POST -H "Content-Type: application/json" -d '{}' http://...
`

### 400 Bad Request
Check validation annotations. Enable debug logging:
`properties
logging.level.org.springframework.web=TRACE
`

### 404 Not Found
- Check URL path vs @RequestMapping value
- Check if controller is scanned by component scan
- Check parameter names match @PathVariable("name")

### 405 Method Not Allowed
- Verify HTTP method matches annotation (GET vs POST)
- Check if method is exposed

## Testing Tools
- Postman/Insomnia for manual testing
- WireMock for mocking external APIs
- MockMvc for integration testing
- TestRestTemplate for full integration tests
