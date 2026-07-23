# Mock Interview: Spring Boot Internals (Lab 26)

**Role:** Backend Engineer (Staff/Principal)  
**Duration:** 55 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How does the `SpringApplication` class work? Walk through the startup process.

**Candidate:** `SpringApplication.run()` executes:
1. **Determine web application type:** Checks if reactive (`WebApplicationType.REACTIVE`), servlet (`SERVLET`), or none (`NONE`) based on available classes
2. **Load ApplicationContextInitializers:** From `spring.factories` and builder
3. **Load ApplicationListeners:** From `spring.factories` (e.g., `LoggingApplicationListener`)
4. **Determine main class:** Stack trace analysis to find `main()` method
5. **Environment preparation:** Load properties from all sources (application.yml, env vars, system properties)
6. **Print banner:** ASCII art or text banner
7. **Create ApplicationContext:** `AnnotationConfigServletWebServerApplicationContext`, `AnnotationConfigReactiveWebServerApplicationContext`, or generic
8. **Prepare context:** Register bean post-processors, configure environment
9. **Refresh context:** Standard Spring context refresh — bean instantiation, auto-configuration
10. **Call runners:** `ApplicationRunner` and `CommandLineRunner` beans

**Interviewer:** How does Spring Boot determine the embedded server to use?

**Candidate:** Spring Boot checks the classpath for server implementations:
- If `tomcat-embed-core` is present → Tomcat (default)
- If `jetty-embed-server` is present → Jetty (when Tomcat excluded)
- If `undertow-embed` is present → Undertow

Each has a corresponding auto-configuration class: `TomcatServletWebServerFactoryCustomizer`, `JettyServletWebServerFactoryCustomizer`, `UndertowServletWebServerFactoryCustomizer`. The factory bean creates and configures the embedded server based on `server.*` properties.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Spring Boot's `ApplicationContext` differ from a standard Spring `ApplicationContext`?

**Candidate:** Spring Boot's `AnnotationConfigServletWebServerApplicationContext` extends `AnnotationConfigApplicationContext` and adds:
- **Web server management:** Starts and stops the embedded web server during context lifecycle
- **Servlet container integration:** Registers servlets, filters, and listeners
- **Auto-configuration integration:** Post-processes beans for auto-configuration support
- **Graceful shutdown:** Waits for active requests to complete before shutting down

Key difference: The web server starts during `refresh()` (specifically the `onRefresh()` phase) and stops during `close()`. This means by the time `run()` returns, the server is already accepting requests.

**Interviewer:** Explain the role of `BeanFactoryPostProcessor` and `BeanPostProcessor` in Spring Boot auto-configuration.

**Candidate:** 
- **`BeanFactoryPostProcessor`:** Runs before bean instantiation, modifies bean definitions (metadata). Example: `PropertySourcesPlaceholderConfigurer` resolves `${...}` placeholders. `ConfigurationClassPostProcessor` processes `@Configuration` classes and `@Bean` methods.
- **`BeanPostProcessor`:** Runs during bean instantiation, intercepts bean creation. Methods: `postProcessBeforeInitialization` (before `@PostConstruct`/`InitializingBean`), `postProcessAfterInitialization` (after initialization). Example: `AutowiredAnnotationBeanPostProcessor` processes `@Autowired`, `PersistenceExceptionTranslationPostProcessor` adds JPA exception translation.

In Spring Boot, auto-configuration uses `@ConditionalOnBean` (checked by `ConfigurationClassPostProcessor` at definition time) and `@ConditionalOnMissingBean` for conditional bean creation.

---

## Round 3: Hard (15-30 minutes)

**Interviewer:** Deep dive: Walk through the complete flow of how `@RestController` with `@Autowired` dependency gets instantiated, proxied, and invoked when an HTTP request arrives. Include the DispatcherServlet chain.

**Candidate:** 

**Bean creation phase:**

1. **Component scanning:** `ClassPathBeanDefinitionScanner` scans `@SpringBootApplication`'s package for `@RestController`, `@Service`, `@Repository` annotations
2. **Bean definition:** Each class becomes a `ScannedGenericBeanDefinition` with bean name (default: camelCase class name)
3. **`ConfigurationClassPostProcessor`** processes any `@Configuration` classes found
4. **Auto-configuration:** `AutoConfigurationImportSelector` loads auto-configuration classes from `AutoConfiguration.imports`, evaluates conditions
5. **Instantiation:** `AbstractAutowireCapableBeanFactory.createBean()`:
   - `BeanPostProcessor.postProcessBeforeInstantiation` → AOP checks (none for simple `@RestController`)
   - Constructor resolution → `@Autowired` constructors preferred
   - `BeanPostProcessor.postProcessAfterInstantiation` → check if property injection needed
   - `AutowiredAnnotationBeanPostProcessor` processes `@Autowired` fields/methods
   - `initMethod` → `@PostConstruct` / `InitializingBean.afterPropertiesSet()`
   - `BeanPostProcessor.postProcessAfterInitialization` → **AOP proxy creation**

**AOP proxy creation:**
6. For `@RestController` with `@Autowired`, Spring may create a proxy if:
   - `@Transactional` is present (CGLIB proxy)
   - Method security is enabled
   - Bean is `@Scope("proxyMode = ScopedProxyMode.TARGET_CLASS")`
7. Proxy wraps the target bean; method calls go through proxy interceptors

**DispatcherServlet request flow:**

8. **HTTP request arrives → Tomcat/Netty thread:** `HttpServlet.service()` → `ApplicationFilterChain.doFilter()`
9. **Spring Security filter chain:** Authentication, authorization
10. **DispatcherServlet.doService():** Core dispatch logic
11. **HandlerMapping:** `RequestMappingInfoHandlerMapping` matches `@GetMapping("/api/users/{id}")` based on URL, method, headers, params, produces/consumes media types. Returns `HandlerMethod` (controller method wrapper) with matched pattern variables
12. **HandlerAdapter:** `RequestMappingHandlerAdapter` prepares handler execution:
    - Resolves method arguments (`@PathVariable`, `@RequestParam`, `@RequestBody`)
    - Uses `HandlerMethodArgumentResolver` chain: `PathVariableMethodArgumentResolver`, `RequestParamMethodArgumentResolver`, `RequestResponseBodyMethodProcessor` (for `@RequestBody`, uses Jackson `ObjectMapper`)
    - Validates `@Valid` annotated parameters
13. **Controller method invocation:** Via proxy (if AOP enabled) or direct reflection
14. **Return value handling:** `HandlerMethodReturnValueResolver` chain processes return value:
    - `ResponseEntity` → status, headers, body extracted
    - `@ResponseBody` → `RequestResponseBodyMethodProcessor` writes via `HttpMessageConverter` (Jackson serializes to JSON)
15. **Response:** DispatcherServlet sends response to client. If view resolution needed, `ViewResolver` resolves template name to `View`, renders model

**Interviewer:** What happens if an exception occurs during step 13?

**Candidate:** Exception propagation:
1. If `@ExceptionHandler` exists on controller → handles within controller
2. If `@ControllerAdvice` with matching exception → `ExceptionHandlerExceptionResolver` handles
3. If `ResponseEntityExceptionHandler` → Spring Boot's default error handling
4. If unhandled → `BasicErrorController` renders `/error` (whitelabel error page or JSON)
5. If in filter chain → container error handling

**Interviewer:** How do you create a custom Spring Boot starter? Walk through the complete process, including testing.

**Candidate:** 

**1. Naming convention:**
- `myapp-spring-boot-starter` — starter POM
- `myapp-spring-boot-autoconfigure` — auto-configuration module

**2. Auto-configuration class:**
```java
@Configuration
@ConditionalOnClass(MyService.class)
@EnableConfigurationProperties(MyProperties.class)
public class MyServiceAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyProperties properties) {
        return new MyService(properties.getUrl(), properties.getTimeout());
    }
}
```

**3. Properties class:**
```java
@ConfigurationProperties(prefix = "myapp.service")
public class MyProperties {
    private String url = "http://localhost:8080";
    private Duration timeout = Duration.ofSeconds(30);
    // getters/setters
}
```

**4. `AutoConfiguration.imports`:**
```
com.example.myapp.autoconfigure.MyServiceAutoConfiguration
```

**5. Additional conditionals:**
```java
@ConditionalOnProperty(prefix = "myapp.service", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication // Only configure for web apps
@AutoConfigureAfter(DataSourceAutoConfiguration.class) // Order with other configs
```

**6. Testing the starter:**
```java
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
class MyServiceAutoConfigurationTest {
    
    @Test
    void defaultConfiguration() {
        // Verify default properties
        assertThat(context.getBean(MyService.class)).isNotNull();
    }
    
    @Test
    void customConfiguration() {
        // Override properties
        // Verify custom URL and timeout
    }
    
    @Test
    void conditionalOnProperty() {
        // Test with myapp.service.enabled=false → MyService should NOT be created
    }
}
```

**7. Auto-configuration test:**
```java
@SpringBootTest(classes = MyServiceAutoConfiguration.class)
class AutoConfigurationTest {
    @Test
    void autoConfigurationApplies() {
        assertThat(context).hasSingleBean(MyService.class);
    }
}
```

---

## Interviewer Feedback

**Strengths:** Deep understanding of Spring internals, clear bean lifecycle explanation, practical custom starter design  
**Areas to Improve:** Could discuss `MergedBeanDefinitionPostProcessor` and its role in `@Autowired` processing  
**Verdict:** Strong Hire (Principal level)

---

## Follow-Up Questions (Self-Study)

1. How does `@EnableAutoConfiguration` import auto-configuration classes? (`AutoConfigurationImportSelector` → `DeferredImportSelector`)
2. What is the difference between `ConfigurationClassPostProcessor` and `ComponentScanAnnotationParser`?
3. How does Spring Boot handle circular dependencies?
4. What is the `SmartInitializingSingleton` interface and when would you use it?
5. How does Spring Boot 3.x's AOT engine change the bean initialization flow?

---

*Lab 26 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
