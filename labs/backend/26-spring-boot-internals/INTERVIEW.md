# Interview Questions — Spring Boot Internals

## Beginner

1. What is `@SpringBootApplication` composed of?
2. How does Spring Boot auto-configuration work?
3. What is the `application.properties` priority order?
4. What is the default embedded server?

## Intermediate

5. Explain `@ConditionalOnMissingBean` vs `@ConditionalOnBean`
6. How do you exclude auto-configuration classes?
7. What is `EnvironmentPostProcessor` used for?
8. How does the DispatcherServlet handle a request?
9. What is `ContentNegotiation`?

## Advanced

10. Describe the `DeferredImportSelector` contract and why it's important for auto-configuration
11. How does `BeanDefinitionRegistryPostProcessor` differ from `BeanFactoryPostProcessor`?
12. Explain the classloader hierarchy in a Spring Boot fat JAR
13. How does the `HealthIndicator` registry aggregate health status?
14. What is the difference between `@Endpoint`, `@WebEndpoint`, `@JmxEndpoint`, and `@ServletEndpoint`?
15. How does Spring Boot detect if it should run in SERVLET, REACTIVE, or NONE mode?
16. Describe the `spring.factories` to `AutoConfiguration.imports` migration
17. How does `@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)` work internally?

## Hands-On

18. Debug an auto-configuration condition that doesn't match
19. Create a custom starter with proper `@ConditionalOnProperty` and `@ConfigurationProperties`
20. Write a custom `FailureAnalyzer` for a custom exception