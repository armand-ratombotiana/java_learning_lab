# Spring Boot Quiz

## Section 1: Spring Boot Basics

**Question 1:** What does the `@SpringBootApplication` annotation combine?

A) @Configuration and @Bean
B) @Configuration, @EnableAutoConfiguration, and @ComponentScan
C) @Component and @Service
D) @Repository and @Controller

**Answer:** B) @Configuration, @EnableAutoConfiguration, and @ComponentScan

---

**Question 2:** Which file is the primary configuration file in Spring Boot?

A) application.xml
B) application.properties or application.yml
C) config.xml
D) settings.properties

**Answer:** B) application.properties or application.yml

---

**Question 3:** What is the purpose of Spring Boot starters?

A) To start the Spring application
B) To provide convenient dependency descriptors that include all required dependencies
C) To initialize the application context
D) To configure the embedded server

**Answer:** B) To provide convenient dependency descriptors that include all required dependencies

---

**Question 4:** How do you exclude specific auto-configuration classes?

A) Using @ExcludeAutoConfiguration
B) Using spring.autoconfigure.exclude property
C) Using @DisableAutoConfiguration
D) Using exclude parameter in @SpringBootApplication

**Answer:** B) Using spring.autoconfigure.exclude property (or exclude attribute in @SpringBootApplication)

---

**Question 5:** What is the default embedded server in Spring Boot?

A) Jetty
B) Undertow
C) Tomcat
D) Netty

**Answer:** C) Tomcat

---

## Section 2: Auto-Configuration

**Question 6:** Which annotation conditionally creates a bean only if a specific class is on the classpath?

A) @ConditionalOnMissingBean
B) @ConditionalOnClass
C) @ConditionalOnProperty
D) @ConditionalOnWebApplication

**Answer:** B) @ConditionalOnClass

---

**Question 7:** What does `@ConditionalOnMissingBean` do?

A) Creates a bean if it doesn't already exist
B) Removes existing beans
C) Checks for missing configuration
D) Validates bean dependencies

**Answer:** A) Creates a bean if it doesn't already exist

---

**Question 8:** The auto-configuration runs in which phase of Spring Boot application startup?

A) Before the application starts
B) After the application context is created but before any beans are instantiated
C) After all beans are instantiated
D) During the build process

**Answer:** B) After the application context is created but before any beans are instantiated

---

**Question 9:** How can you define custom auto-configuration?

A) Create a class annotated with @AutoConfiguration
B) Register auto-configuration in META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports (Spring Boot 3.x)
C) Both A and B
D) Auto-configuration cannot be custom

**Answer:** C) Both A and B

---

**Question 10:** What is the order of precedence for property sources (highest to lowest)?

A) application.properties, environment variables, command-line arguments
B) command-line arguments, environment variables, application.properties
C) environment variables, application.properties, command-line arguments
D) They all have the same precedence

**Answer:** B) command-line arguments, environment variables, application.properties

---

## Section 3: Spring Boot Actuator

**Question 11:** Which actuator endpoint provides the health status of the application?

A) /actuator/info
B) /actuator/health
C) /actuator/metrics
D) /actuator/beans

**Answer:** B) /actuator/health

---

**Question 12:** To expose all actuator endpoints, which configuration property is used?

A) management.endpoints.web.exposure.include=*
B) management.endpoints.web.exposure.include=all
C) management.endpoints.enabled=true
D) actuator.expose.all=true

**Answer:** A) management.endpoints.web.exposure.include=*

---

**Question 13:** How do you create a custom health indicator?

A) Implement HealthIndicator interface
B) Extend HealthCheck class
C) Annotate a method with @HealthCheck
D) Register a HealthChecker bean

**Answer:** A) Implement HealthIndicator interface

---

**Question 14:** Which annotation is used to create a custom management endpoint?

A) @ManagementEndpoint
B) @Endpoint
C) @ActuatorEndpoint
D) @AdminEndpoint

**Answer:** B) @Endpoint

---

**Question 15:** What is the purpose of the `/actuator/env` endpoint?

A) To view environment variables
B) To view all Spring beans
C) To view configuration properties and environment variables
D) To view application metrics

**Answer:** C) To view configuration properties and environment variables

---

## Section 4: Configuration and Profiles

**Question 16:** How do you activate a specific profile in Spring Boot?

A) spring.profile=development
B) spring.profiles.active=development
C) profiles.active=development
D) environment=development

**Answer:** B) spring.profiles.active=development

---

**Question 17:** What is the naming convention for profile-specific properties files?

A) application-{profile}.properties
B) {profile}-application.properties
C) profile.properties
D) application-{profile}.yml

**Answer:** A) application-{profile}.properties (also applies to .yml)

---

**Question 18:** Which annotation is used to bind configuration properties to a POJO?

A) @Properties
B) @ConfigurationProperties
C) @PropertyBinding
D) @BindProperties

**Answer:** B) @ConfigurationProperties

---

**Question 19:** What does `@Profile("dev")` specify?

A) The class is for development only
B) The class is activated when "dev" profile is active
C) The class cannot be used in production
D) The class needs development tools

**Answer:** B) The class is activated when "dev" profile is active

---

**Question 20:** How do you set properties for a specific test class?

A) @TestConfiguration
B) @TestPropertySource
C) @PropertiesSource
D) @ConfigurationProperties

**Answer:** B) @TestPropertySource

---

## Section 5: Spring Boot Testing

**Question 21:** Which annotation is used for a full integration test in Spring Boot?

A) @IntegrationTest
B) @SpringBootTest
C) @BootTest
D) @ApplicationTest

**Answer:** B) @SpringBootTest

---

**Question 22:** Which annotation is used to test only the web layer (controllers)?

A) @SpringBootTest
B) @WebMvcTest
C) @ControllerTest
D) @RestControllerTest

**Answer:** B) @WebMvcTest

---

**Question 23:** What does `@MockBean` do?

A) Creates a mock of a Spring bean
B) Creates a mock of a Java class
C) Disables a bean
D) Replaces a bean with a mock

**Answer:** A) Creates a mock of a Spring bean (adds mock to context)

---

**Question 24:** Which testing annotation provides a TestRestTemplate?

A) @RestTest
B) @WebIntegrationTest
C) @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
D) @TemplateTest

**Answer:** C) @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

---

**Question 25:** What is the purpose of `@DataJpaTest`?

A) Test the entire application
B) Test only JPA components with embedded database
C) Test data access layer
D) Test database connections

**Answer:** B) Test only JPA components with embedded database

---

## Section 6: DevTools and Logging

**Question 26:** What does Spring Boot DevTools provide?

A) Code compilation
B) Automatic restart and LiveReload
C) Debugging
D) Performance monitoring

**Answer:** B) Automatic restart and LiveReload

---

**Question 27:** How do you disable DevTools in production?

A) Remove the dependency
B) Set spring.devtools.enabled=false
C) Use exclude in production profile
D) DevTools is automatically disabled in production

**Answer:** D) DevTools is automatically disabled in production (when running from jar)

---

**Question 28:** Which logging framework does Spring Boot use by default?

A) Log4j
B) SLF4j (as API, with Logback as implementation)
C) JUL (Java Util Logging)
D) Logback

**Answer:** B) SLF4j (as API, with Logback as implementation)

---

**Question 29:** How do you set the logging level for a specific package?

A) logging.level.package=DEBUG
B) logging.level.com.example.package=DEBUG
C) log.level.com.example.package=DEBUG
D) level.com.example.package=DEBUG

**Answer:** B) logging.level.com.example.package=DEBUG

---

**Question 30:** What is the default log level in Spring Boot?

A) DEBUG
B) INFO
C) WARN
D) ERROR

**Answer:** B) INFO

---

## Section 7: Advanced Topics

**Question 30:** What does `@EnableConfigurationProperties` do?

A) Enables property configuration
B) Registers @ConfigurationProperties annotated classes as beans
C) Enables external configuration
D) Validates configuration properties

**Answer:** B) Registers @ConfigurationProperties annotated classes as beans

---

**Question 31:** Which annotation is used to create a REST controller?

A) @Controller
B) @RestController
C) @RequestMapping
D) @GetMapping

**Answer:** B) @RestController

---

**Question 32:** What is the purpose of `@Value` annotation?

A) To inject property values
B) To set default values
C) To validate values
D) To convert values

**Answer:** A) To inject property values

---

**Question 33:** How do you inject a property with a default value using `@Value`?

A) @Value("${property.name:default}")
B) @Value("${property.name=-default}")
C) @Value("${property.name}")
D) @Value("property.name")

**Answer:** A) @Value("${property.name:default}")

---

**Question 34:** What is the purpose of `@ControllerAdvice`?

A) To define a controller
B) To handle exceptions globally
C) To provide advice to controllers
D) To validate controller input

**Answer:** B) To handle exceptions globally

---

**Question 35:** Which annotation is used for input validation?

A) @Valid
B) @Validate
C) @Check
D) @Verify

**Answer:** A) @Valid

---

## Answer Key

| Question | Answer |
|----------|--------|
| 1 | B |
| 2 | B |
| 3 | B |
| 4 | B |
| 5 | C |
| 6 | B |
| 7 | A |
| 8 | B |
| 9 | C |
| 10 | B |
| 11 | B |
| 12 | A |
| 13 | A |
| 14 | B |
| 15 | C |
| 16 | B |
| 17 | A |
| 18 | B |
| 19 | B |
| 20 | B |
| 21 | B |
| 22 | B |
| 23 | A |
| 24 | C |
| 25 | B |
| 26 | B |
| 27 | D |
| 28 | B |
| 29 | B |
| 30 | B |
| 31 | B |
| 32 | A |
| 33 | A |
| 34 | B |
| 35 | A |