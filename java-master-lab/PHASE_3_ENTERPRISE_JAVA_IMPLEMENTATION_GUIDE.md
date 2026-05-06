# Java Master Lab - Phase 3 Enterprise Java Implementation Guide

## 🏢 Phase 3 Enterprise Java - Detailed Implementation Guide

**Phase**: Phase 3 (Enterprise Java)  
**Labs**: 26-40 (15 labs)  
**Duration**: 8 weeks (Weeks 9-16)  
**Content**: 60,000+ lines of code  
**Tests**: 2,250+ unit tests  
**Projects**: 15 portfolio projects  

---

## 📋 PHASE 3 OVERVIEW

### Phase Objectives

```
✅ Master enterprise Java frameworks
✅ Implement Spring Boot applications
✅ Master database operations (JPA/Hibernate)
✅ Implement REST APIs
✅ Master microservices architecture
✅ Implement security and authentication
✅ Master testing strategies
✅ Build enterprise portfolio projects
✅ Achieve 80%+ code coverage
✅ Maintain 100% test pass rate
✅ Complete on schedule (Week 16)
```

### Phase Structure

```
WEEK 9-10: Spring Framework Basics (Labs 26-28)
├─ Lab 26: Spring Core & Dependency Injection
├─ Lab 27: Spring MVC & Web Applications
├─ Lab 28: Spring Boot Fundamentals
└─ Status: PLANNED

WEEK 11-12: Data Access & Persistence (Labs 29-31)
├─ Lab 29: JPA & Hibernate
├─ Lab 30: Spring Data JPA
├─ Lab 31: Database Transactions
└─ Status: PLANNED

WEEK 13-14: REST APIs & Web Services (Labs 32-34)
├─ Lab 32: Building REST APIs
├─ Lab 33: API Documentation & Swagger
├─ Lab 34: API Security & Authentication
└─ Status: PLANNED

WEEK 15-16: Advanced Enterprise Topics (Labs 35-40)
├─ Lab 35: Microservices Architecture
├─ Lab 36: Service Communication
├─ Lab 37: Caching Strategies
├─ Lab 38: Monitoring & Logging
├─ Lab 39: Testing Enterprise Applications
├─ Lab 40: Deployment & DevOps
└─ Status: PLANNED
```

---

## 🌱 LAB 26: SPRING CORE & DEPENDENCY INJECTION

### Lab Objectives

```
✅ Understand Spring Framework architecture
✅ Master dependency injection
✅ Implement bean configuration
✅ Master Spring annotations
✅ Implement application context
✅ Master bean lifecycle
✅ Write comprehensive tests
✅ Create portfolio project
```

### Key Topics

```
SPRING CORE CONCEPTS:
├─ IoC Container
├─ Dependency Injection
├─ Bean Definition
├─ Bean Lifecycle
├─ Application Context
├─ Property Injection
├─ Constructor Injection
├─ Setter Injection
├─ Autowiring
└─ Bean Scopes

SPRING ANNOTATIONS:
├─ @Component
├─ @Service
├─ @Repository
├─ @Controller
├─ @Autowired
├─ @Qualifier
├─ @Primary
├─ @Scope
├─ @Bean
├─ @Configuration
├─ @Value
├─ @PropertySource
└─ @Profile

BEAN CONFIGURATION:
├─ XML Configuration
├─ Java Configuration
├─ Annotation Configuration
├─ Property Files
├─ YAML Configuration
├─ Environment Variables
├─ Profiles
└─ Conditional Beans

BEAN LIFECYCLE:
├─ Instantiation
├─ Property Setting
├─ Initialization
├─ Usage
├─ Destruction
├─ Init Methods
├─ Destroy Methods
├─ PostConstruct
└─ PreDestroy
```

### Implementation Examples

```java
// Spring Configuration
@Configuration
public class AppConfig {
    
    @Bean
    public UserService userService() {
        return new UserService(userRepository());
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
}

// Component with Dependency Injection
@Service
public class UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}

// Repository Component
@Repository
public class UserRepository {
    private final DataSource dataSource;
    
    @Autowired
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public User findById(Long id) {
        // Database query
        return null;
    }
}

// Property Injection
@Component
public class AppProperties {
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.version}")
    private String appVersion;
    
    public String getAppName() {
        return appName;
    }
}

// Bean Lifecycle
@Component
public class DataInitializer {
    
    @PostConstruct
    public void init() {
        System.out.println("Initializing data...");
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("Cleaning up resources...");
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Bean creation tests
├─ Dependency injection tests
├─ Configuration tests
├─ Lifecycle tests
├─ Property injection tests
├─ Autowiring tests
└─ Scope tests

INTEGRATION TESTS:
├─ Application context tests
├─ Bean interaction tests
├─ Configuration loading tests
├─ Profile tests
└─ Real-world scenario tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

---

## 🌐 LAB 27: SPRING MVC & WEB APPLICATIONS

### Lab Objectives

```
✅ Master Spring MVC architecture
✅ Implement controllers
✅ Master request handling
✅ Implement view rendering
✅ Master form handling
✅ Implement validation
✅ Write comprehensive tests
✅ Create portfolio project
```

### Key Topics

```
SPRING MVC ARCHITECTURE:
├─ DispatcherServlet
├─ Controllers
├─ Views
├─ Models
├─ Request Mapping
├─ Response Handling
├─ Exception Handling
├─ Interceptors
├─ Filters
└─ View Resolvers

CONTROLLERS:
├─ @Controller
├─ @RestController
├─ @RequestMapping
├─ @GetMapping
├─ @PostMapping
├─ @PutMapping
├─ @DeleteMapping
├─ @PatchMapping
├─ Path Variables
├─ Request Parameters
├─ Request Body
└─ Response Entity

VIEWS & TEMPLATES:
├─ JSP
├─ Thymeleaf
├─ FreeMarker
├─ Velocity
├─ Model Attributes
├─ View Resolution
├─ Template Rendering
└─ Static Resources

FORM HANDLING:
├─ Form Binding
├─ Data Binding
├─ Validation
├─ Error Handling
├─ Custom Validators
├─ Bean Validation
├─ JSR-303 Annotations
└─ Error Messages

REQUEST/RESPONSE:
├─ Request Mapping
├─ Content Negotiation
├─ HTTP Methods
├─ Status Codes
├─ Headers
├─ Cookies
├─ Redirects
└─ Forward
```

### Implementation Examples

```java
// Spring MVC Controller
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "users/detail";
    }
    
    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }
}

// REST Controller
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    private final UserService userService;
    
    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
            @RequestBody User user) {
        return ResponseEntity.ok(userService.update(id, user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

// Form Validation
@Data
public class UserForm {
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Min(value = 18, message = "Age must be at least 18")
    private int age;
    
    @Pattern(regexp = "^[0-9]{10}$", 
        message = "Phone must be 10 digits")
    private String phone;
}

// Exception Handling
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Controller tests
├─ Request mapping tests
├─ Response handling tests
├─ Validation tests
├─ Exception handling tests
└─ View resolution tests

INTEGRATION TESTS:
├─ MVC integration tests
├─ Form submission tests
├─ Validation tests
├─ Exception handling tests
└─ Real-world scenario tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

---

## 🚀 LAB 28: SPRING BOOT FUNDAMENTALS

### Lab Objectives

```
✅ Master Spring Boot
✅ Implement auto-configuration
✅ Master embedded servers
✅ Implement application properties
✅ Master actuators
✅ Implement health checks
✅ Write comprehensive tests
✅ Create portfolio project
```

### Key Topics

```
SPRING BOOT FEATURES:
├─ Auto-Configuration
├─ Embedded Servers
├─ Starter Dependencies
├─ Application Properties
├─ Profiles
├─ Actuators
├─ Health Checks
├─ Metrics
├─ Logging
└─ Monitoring

APPLICATION PROPERTIES:
├─ application.properties
├─ application.yml
├─ Environment Variables
├─ Command Line Arguments
├─ Property Profiles
├─ Property Binding
├─ Configuration Classes
├─ Custom Properties
└─ Property Validation

ACTUATORS:
├─ Health Endpoint
├─ Metrics Endpoint
├─ Info Endpoint
├─ Env Endpoint
├─ Beans Endpoint
├─ Mappings Endpoint
├─ Custom Endpoints
└─ Endpoint Security

EMBEDDED SERVERS:
├─ Tomcat
├─ Jetty
├─ Undertow
├─ Server Configuration
├─ Port Configuration
├─ SSL Configuration
├─ Compression
└─ Custom Server Configuration
```

### Implementation Examples

```java
// Spring Boot Application
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Application Properties
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private String name;
    private String version;
    private String description;
    private Server server;
    
    @Data
    public static class Server {
        private String host;
        private int port;
        private String contextPath;
    }
}

// Custom Actuator Endpoint
@Component
@Endpoint(id = "custom")
public class CustomEndpoint {
    
    @ReadOperation
    public Map<String, Object> getCustomInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("timestamp", System.currentTimeMillis());
        return info;
    }
    
    @WriteOperation
    public void updateCustomInfo(@Selector String key, String value) {
        // Update logic
    }
}

// Health Indicator
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // Check custom service health
            boolean isHealthy = checkServiceHealth();
            if (isHealthy) {
                return Health.up()
                    .withDetail("service", "running")
                    .build();
            } else {
                return Health.down()
                    .withDetail("service", "not responding")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
    
    private boolean checkServiceHealth() {
        // Health check logic
        return true;
    }
}

// Metrics
@Component
public class CustomMetrics {
    
    private final MeterRegistry meterRegistry;
    
    @Autowired
    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void recordUserCreation() {
        meterRegistry.counter("users.created").increment();
    }
    
    public void recordProcessingTime(long duration) {
        meterRegistry.timer("processing.time").record(duration, 
            TimeUnit.MILLISECONDS);
    }
}

// Application Configuration
@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Configuration tests
├─ Property binding tests
├─ Actuator tests
├─ Health indicator tests
├─ Metrics tests
└─ Custom endpoint tests

INTEGRATION TESTS:
├─ Application context tests
├─ Server startup tests
├─ Actuator endpoint tests
├─ Health check tests
└─ Real-world scenario tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

---

## 📊 PHASE 3 SUMMARY

### Deliverables (Labs 26-40)

```
TOTAL CONTENT: 60,000+ lines
├─ Labs 26-28: 12,000+ lines (Spring Basics)
├─ Labs 29-31: 15,000+ lines (Data Access)
├─ Labs 32-34: 15,000+ lines (REST APIs)
└─ Labs 35-40: 18,000+ lines (Advanced Topics)

TOTAL TESTS: 2,250+ unit tests
├─ Labs 26-28: 450+ tests
├─ Labs 29-31: 550+ tests
├─ Labs 32-34: 550+ tests
└─ Labs 35-40: 700+ tests

TOTAL PROJECTS: 15 portfolio projects
├─ Spring Core Framework
├─ Spring MVC Application
├─ Spring Boot Application
├─ JPA/Hibernate Framework
├─ Spring Data JPA Application
├─ Transaction Management System
├─ REST API Framework
├─ API Documentation System
├─ Security & Authentication System
├─ Microservices Platform
├─ Service Communication Framework
├─ Caching System
├─ Monitoring & Logging System
├─ Testing Framework
└─ Deployment & DevOps System

QUALITY METRICS:
├─ Code Coverage: 85%+
├─ Test Pass Rate: 100%
├─ Quality Score: 85/100
├─ Defect Density: <1 per 1000 LOC
└─ Security Score: 95/100
```

### Timeline

```
WEEK 9-10: Spring Framework Basics (Labs 26-28)
├─ Lab 26: Spring Core (3 days)
├─ Lab 27: Spring MVC (2 days)
├─ Lab 28: Spring Boot (2 days)
└─ Status: PLANNED

WEEK 11-12: Data Access & Persistence (Labs 29-31)
├─ Lab 29: JPA & Hibernate (3 days)
├─ Lab 30: Spring Data JPA (2 days)
├─ Lab 31: Transactions (2 days)
└─ Status: PLANNED

WEEK 13-14: REST APIs & Web Services (Labs 32-34)
├─ Lab 32: REST APIs (3 days)
├─ Lab 33: API Documentation (2 days)
├─ Lab 34: API Security (2 days)
└─ Status: PLANNED

WEEK 15-16: Advanced Enterprise Topics (Labs 35-40)
├─ Lab 35: Microservices (2 days)
├─ Lab 36: Service Communication (1 day)
├─ Lab 37: Caching (1 day)
├─ Lab 38: Monitoring & Logging (1 day)
├─ Lab 39: Testing (1 day)
└─ Lab 40: Deployment & DevOps (1 day)

TOTAL: 8 weeks, 15 labs, 60,000+ lines, 2,250+ tests, 15 projects
```

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Phase 3 Enterprise Java Implementation Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Implementation |

---

**Java Master Lab - Phase 3 Enterprise Java Implementation Guide**

*Detailed Implementation Guide for Labs 26-40*

**Status: ACTIVE | Focus: Implementation | Impact: Excellence**

---

*Master enterprise Java and build scalable applications!* 🚀