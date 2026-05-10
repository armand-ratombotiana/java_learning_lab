# Spring Core - Exercises

## Exercise 1: Basic DI Container
**Objective**: Implement a basic Spring DI container without Spring Boot.

### Task
Create a simple application that demonstrates:
1. XML-based bean configuration
2. Constructor injection
3. Property file loading
4. Bean lifecycle callbacks

### Solution Requirements
- Create `ApplicationContext` using `ClassPathXmlApplicationContext`
- Define beans in `applicationContext.xml`
- Use constructor injection for dependencies
- Implement `InitializingBean` and `DisposableBean`

### Expected Output
```
Bean created: EmailService
Bean created: NotificationService
Sending notification to: user@example.com
Message: Welcome to our service!
Cleaning up resources
```

---

## Exercise 2: Java Configuration
**Objective**: Replace XML with Java-based configuration.

### Task
Convert XML configuration to `@Configuration` classes:
1. Create `@Configuration` class
2. Define `@Bean` methods
3. Use `@ComponentScan` for automatic detection
4. Inject beans using `@Autowired`

### Code Template
```java
@Configuration
@ComponentScan(basePackages = "com.learning.service")
public class AppConfig {
    
    @Bean
    public MessageService emailService() {
        return new EmailService();
    }
    
    @Bean
    public NotificationService notificationService(MessageService messageService) {
        return new NotificationService(messageService);
    }
}
```

---

## Exercise 3: Bean Scopes
**Objective**: Understand different bean scopes in Spring.

### Task
Create a service with multiple scopes:
1. Singleton scope (default)
2. Prototype scope
3. Custom thread scope

### Implementation
```java
@Service
@Scope("prototype")
public class OrderService {
    private String orderId;
    
    public String createOrder() {
        orderId = UUID.randomUUID().toString();
        return orderId;
    }
}

// Custom thread scope
@Bean
public Scope threadScope() {
    return new ThreadScope();
}
```

### Verification
- Get the same bean twice - should be same instance (singleton)
- Get the same bean twice from prototype - should be different instances

---

## Exercise 4: Profile-based Configuration
**Objective**: Use Spring profiles for environment-specific beans.

### Task
Create configuration for different environments:
1. Development profile with mock services
2. Production profile with real services
3. Test profile with in-memory implementations

### Implementation
```java
@Configuration
@Profile("development")
public class DevConfig {
    @Bean
    public MessageService mockMessageService() {
        return () -> "DEV: " + System.getProperty("user.name");
    }
}

@Configuration
@Profile("production")
public class ProdConfig {
    @Bean
    public MessageService prodMessageService() {
        return new RealEmailService();
    }
}
```

### Testing
```java
var context = new AnnotationConfigApplicationContext();
context.getEnvironment().setActiveProfiles("development");
context.register(AppConfig.class, DevConfig.class);
context.refresh();
```

---

## Exercise 5: Custom Bean Post-Processor
**Objective**: Implement bean post-processing for custom initialization.

### Task
Create a `BeanPostProcessor` that:
1. Logs bean initialization
2. Wraps beans with lazy initialization proxies
3. Validates bean configuration

### Implementation
```java
@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("Initializing: " + beanName);
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("Initialized: " + beanName);
        return bean;
    }
}
```

---

## Exercise 6: Spring Events
**Objective**: Implement event-driven communication between beans.

### Task
Create an event system:
1. Define custom application events
2. Create event listeners
3. Publish events from services

### Implementation
```java
// Custom event
public class OrderPlacedEvent extends ApplicationEvent {
    private final String orderId;
    private final String customerEmail;
    
    public OrderPlacedEvent(Object source, String orderId, String customerEmail) {
        super(source);
        this.orderId = orderId;
        this.customerEmail = customerEmail;
    }
}

// Event listener
@Component
public class OrderEventListener {
    
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        System.out.println("Order placed: " + event.getOrderId());
        // Send confirmation email, update inventory, etc.
    }
}
```

---

## Exercise 7: AOP Basics
**Objective**: Implement cross-cutting concerns using AOP.

### Task
Create aspects for:
1. Logging method execution
2. Performance monitoring
3. Transaction management (simulated)

### Implementation
```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.learning.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Executing: " + joinPoint.getSignature());
    }
    
    @AfterReturning(pointcut = "execution(* com.learning.service.*.*(..))", 
                    returning = "result")
    public void logAfter(Object result) {
        System.out.println("Method returned: " + result);
    }
    
    @Around("execution(* com.learning.service.*.*(..))")
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " took " + duration + "ms");
        return result;
    }
}
```

---

## Exercise 8: Conditional Beans
**Objective**: Create beans conditionally based on environment.

### Task
Implement conditional bean creation:
1. Use `@Conditional` annotation
2. Create custom condition classes
3. Check system properties, environment variables

### Implementation
```java
@Bean
@Conditional(WindowsCondition.class)
public MessageService windowsMessageService() {
    return new WindowsNotificationService();
}

@Bean
@Conditional(LinuxCondition.class)
public MessageService linuxMessageService() {
    return new LinuxNotificationService();
}

public class WindowsCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}
```

---

## Exercise 9: Custom Scope
**Objective**: Implement a custom bean scope.

### Task
Create a "request" scope for non-web applications:
1. Implement `Scope` interface
2. Register custom scope in configuration
3. Use `@Scope("request")` annotation

### Implementation
```java
public class ThreadLocalScope implements Scope {
    private final ThreadLocal<Map<String, Object>> scope = 
        ThreadLocal.withInitial(HashMap::new);
    
    @Override
    public Object get(String name, ObjectFactory<?> factory) {
        return scope.get().computeIfAbsent(name, k -> factory.getObject());
    }
    
    @Override
    public Object remove(String name) {
        return scope.get().remove(name);
    }
    
    // Other required methods
}
```

---

## Exercise 10: Factory Bean
**Objective**: Use `FactoryBean` for complex object creation.

### Task
Create a `FactoryBean` implementation:
1. Create custom `FactoryBean`
2. Configure in Java config
3. Inject into services

### Implementation
```java
@Component
public class MessageServiceFactoryBean implements FactoryBean<MessageService> {
    
    @Value("${message.service.type:email}")
    private String serviceType;
    
    @Override
    public MessageService getObject() {
        return switch (serviceType) {
            case "sms" -> new SmsMessageService();
            case "email" -> new EmailMessageService();
            default -> new MockMessageService();
        };
    }
    
    @Override
    public Class<?> getObjectType() {
        return MessageService.class;
    }
}
```

---

## Bonus Exercise: Advanced AOP
**Objective**: Implement method interception and aspect ordering.

### Task
Create advanced AOP features:
1. Aspect with multiple advice types
2. Use `@Order` for aspect ordering
3. Implement `@Pointcut` expressions

### Implementation
```java
@Aspect
@Order(1)
public class SecurityAspect {
    
    @Pointcut("within(com.learning.service..*)")
    public void serviceLayer() {}
    
    @Pointcut("execution(* com.learning.service.*.delete*(..))")
    public void deleteOperations() {}
    
    @Before("serviceLayer() && !deleteOperations()")
    public void logAccess() {
        System.out.println("Accessing service method");
    }
}
```

---

## Running Tests
```bash
# Run all exercises
mvn test

# Run specific exercise
mvn exec:java -Dexec.mainClass="com.learning.Exercise1"
```