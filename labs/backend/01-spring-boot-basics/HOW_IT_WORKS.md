# How It Works

## Startup Sequence

1. **SpringApplication.run()** is called
2. **ApplicationContext** is detected (AnnotationConfigServletWebServerApplicationContext)
3. **Auto-configuration candidates** loaded from `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
4. **Conditional evaluation** - Each auto-configuration class has `@Conditional*` annotations
5. **Bean creation** - Matching configurations create beans
6. **ApplicationRunner/CommandLineRunner** executed after context refresh

```java
@SpringBootApplication  // Equivalent to:
// @EnableAutoConfiguration
// @ComponentScan
// @Configuration
```

## Auto-Configuration Mechanism

```java
// spring-boot-autoconfigure jar contains:
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
// Lists all auto-configuration classes like:
// org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
```

Each auto-configuration class is conditional:
```java
@AutoConfiguration
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebMvcAutoConfiguration { ... }
```
