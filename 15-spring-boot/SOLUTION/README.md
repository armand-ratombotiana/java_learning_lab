# Spring Boot Solution

This module provides comprehensive reference implementations for Spring Boot concepts including auto-configuration, starters, and actuator.

## Concepts Covered

### Auto-Configuration
- Automatic bean configuration
- Conditional bean loading
- Classpath-based configuration
- Property-based conditions

### Starters
- Pre-configured dependency sets
- Autoconfiguration classes
- Starter POMs

### Actuator
- Health monitoring
- Info endpoint
- Metrics collection
- Custom endpoints

## Key Components

### SpringApplication
```java
SpringApplication app = new SpringApplication(Application.class);
app.setProperty("server.port", "8080");
app.run(args);
```

### AutoConfiguration
```java
AutoConfiguration config = new AutoConfiguration();
config.registerAutoConfiguration(Properties.class, instance);
config.getAutoConfiguredBean(Properties.class);
```

### Actuator Endpoints
```java
HealthEndpoint health = new HealthEndpoint();
health.invoke(); // Returns UP status

MetricsEndpoint metrics = new MetricsEndpoint();
metrics.recordMetric("http.requests", 100.0);
```

### Conditional Loading
```java
OnPropertyCondition cond = new OnPropertyCondition("debug", "true");
OnClassCondition classCond = new OnClassCondition("org.springframework.boot");
```

## Implementation Details

### Embedded Server
- Configurable port (default 8080)
- Start/stop lifecycle management

### DevTools
- Automatic restart
- Live reload support
- Configurable features

### Configuration Properties
- Type-safe properties
- Default values
- Custom binding

### Spring Boot Banner
- ASCII art banner
- Version display

## Annotations

- `@SpringBootApplication`: Main application annotation
- `@Configuration`: Configuration class
- `@ComponentScan`: Component scanning
- `@EnableAutoConfiguration`: Auto-config enable
- `@Profile`: Profile-specific beans
- `@Conditional`: Conditional bean registration
- `@ConfigurationProperties`: Properties binding
- `@EnableConfigurationProperties`: Enable config props

## Test Coverage

50+ test cases covering:
- Auto-configuration registration
- Conditional evaluation
- Endpoint invocation
- Server lifecycle
- Properties binding
- Async execution

## Running Tests

```bash
# Compile
javac -cp . -d out src/**/*.java

# Run tests
java -cp junit-5.9.3.jar:out org.junit.platform.console.ConsoleLauncher --scan-classpath
```