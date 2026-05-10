# Spring Core Solution

This module provides comprehensive reference implementations for Spring Core concepts including DI, BeanFactory, AOP, and IoC.

## Concepts Covered

### Dependency Injection (DI)
- Manual DI container implementation
- Singleton and prototype beans
- Constructor and setter injection
- `@Autowired` annotation processing

### BeanFactory
- Bean lifecycle management
- Bean registration and retrieval
- BeanPostProcessor for initialization hooks
- BeanFactoryPostProcessor for configuration

### Inversion of Control (IoC)
- IoC container implementation
- Dependency resolution
- Bean scopes (SINGLETON, PROTOTYPE, REQUEST, SESSION)

### Aspect-Oriented Programming (AOP)
- Aspect implementation
- Proxy-based AOP
- Before/after advice
- Logging and performance aspects

## Key Components

### DI Container
```java
SimpleDIContainer container = new SimpleDIContainer();
container.registerSingleton(String.class, "test");
container.getBean(String.class);
```

### AOP Proxy
```java
AOPProxy proxy = new AOPProxy(target);
proxy.addAspect(new LoggingAspect());
proxy.invoke("methodName");
```

### Event System
```java
EventPublisher publisher = new EventPublisher();
publisher.addListener(event -> handle(event));
publisher.publishEvent(new ApplicationEvent("data"));
```

### Environment Configuration
```java
PropertySource props = new PropertySource();
props.setProperty("db.url", "jdbc:mysql://...");
Environment env = new Environment(props);
```

## Implementation Details

### Bean Lifecycle
1. Instantiation
2. Populate properties
3. Initialize (BeanPostProcessor)
4. Post-initialization
5. Ready for use
6. Destruction

### Custom Annotations
- `@Autowired`: Field injection
- `@Component`: Component registration
- `@Service`: Service layer
- `@Repository`: Data access layer
- `@Qualifier`: Specific bean selection
- `@Primary`: Default bean
- `@Order`: Bean ordering

### SpEL Parser
Simple expression language for property resolution:
```java
SpELParser parser = new SpELParser();
parser.evaluate("${name} = ${value}", variables);
```

## Test Coverage

50+ test cases covering:
- DI container functionality
- Bean factory operations
- AOP aspect execution
- Event publishing
- Property resolution
- Environment profiles

## Running Tests

```bash
# Compile
javac -cp . -d out src/**/*.java src/**/Solution.java src/**/Test.java

# Run with JUnit
java -cp junit-5.9.3.jar:out org.junit.platform.console.ConsoleLauncher --scan-classpath
```