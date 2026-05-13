# Spring Boot Cheat Sheet

## Common Annotations

```
┌─────────────────────────────────────────────────────────────────┐
│  CORE ANNOTATIONS                                              │
├─────────────────────────────────────────────────────────────────┤
│  @SpringBootApplication    Main class, enables auto-config     │
│  @ComponentScan           Scan for components                  │
│  @EnableAutoConfiguration Enable Spring Boot auto-config       │
│  @Configuration           Define beans via Java config        │
│  @Component               Generic component                    │
│  @Service                 Service layer                        │
│  @Repository              Data access layer                    │
│  @Controller              MVC controller                       │
│  @RestController          REST endpoint (body + response)     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  INJECTION                                                     │
├─────────────────────────────────────────────────────────────────┤
│  @Autowired           Inject by type                           │
│  @Qualifier           Specify bean name                       │
│  @Primary             Default bean when multiple candidates    │
│  @Value               Inject properties/environment vars       │
│  @Inject              JSR-330 (alternative to @Autowired)      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  REQUEST HANDLING                                              │
├─────────────────────────────────────────────────────────────────┤
│  @GetMapping          HTTP GET                                 │
│  @PostMapping         HTTP POST                                │
│  @PutMapping          HTTP PUT                                 │
│  @DeleteMapping       HTTP DELETE                              │
│  @PatchMapping        HTTP PATCH                               │
│  @RequestMapping      Any HTTP method                          │
│  @RequestParam        Query parameter                          │
│  @PathVariable        URL path segment                        │
│  @RequestBody         Request body to object                  │
│  @ResponseBody        Return as response body                  │
└─────────────────────────────────────────────────────────────────┘
```

## Application Properties

```yaml
# Server
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
logging.level.package=DEBUG
logging.pattern.console=%d{yyyy-MM-dd} %-5level %msg%n

# Datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=user
spring.datasource.password=pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true

# Profiles
spring.profiles.active=dev

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

## REST Controller Example

```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid UserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request) {
        return userService.update(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

## Dependency Injection Types

```java
// Constructor injection (recommended)
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// Field injection (avoid)
@Service
public class BadService {
    @Autowired
    private Repository repo;
}

// Setter injection (rarely used)
@Service
public class SetterService {
    private Repository repo;
    
    @Autowired
    public void setRepo(Repository repo) {
        this.repo = repo;
    }
}
```

## Exception Handling

```java
// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleValidation(MethodArgumentNotValidException ex) {
        // extract field errors
    }
}
```

## Testing

```java
// Unit test
@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserRepository repository;

    @Autowired
    private UserService service;

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        User result = service.findById(1L);
        assertThat(result.getName()).isEqualTo("John");
    }
}

// Web test
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
```

## Common Starter Dependencies

| Dependency | Use |
|------------|-----|
| spring-boot-starter-web | REST, MVC |
| spring-boot-starter-data-jpa | JPA/Hibernate |
| spring-boot-starter-data-rest | REST repositories |
| spring-boot-starter-validation | Bean validation |
| spring-boot-starter-security | Security |
| spring-boot-starter-test | Testing |
| spring-boot-starter-actuator | Production features |
| spring-boot-devtools | Hot reload |