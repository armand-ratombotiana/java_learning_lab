# Lab 26 — Spring Boot Internals Deep Dive

**Duration: 6-8 hours** | **Difficulty: Expert**

Explore the inner workings of Spring Boot: auto-configuration mechanism, conditional annotations, the dispatcher servlet pipeline, actuator internals, custom starters, and embedded container architecture.

## What You'll Learn

- How `@EnableAutoConfiguration` loads auto-configuration candidates
- The `spring.factories` vs `AutoConfiguration.imports` mechanism
- Conditional annotation evaluation strategy
- `BeanFactoryPostProcessor` and `BeanDefinitionRegistryPostProcessor` lifecycle
- `ApplicationContextInitializer` and `EnvironmentPostProcessor` hooks
- Custom `FailureAnalyzer` for readable startup errors
- Building a production-grade custom starter
- Actuator internals: health indicators, metrics backends, custom endpoints
- Spring MVC dispatcher chain: `HandlerMapping`, `HandlerAdapter`, `HandlerInterceptor`
- `HandlerMethodArgumentResolver` and `HandlerMethodReturnValueHandler` strategy
- Content negotiation and `HttpMessageConverter` registration
- Embedded Tomcat vs Jetty vs Undertow
- Classloader hierarchy in Spring Boot
- `WebApplicationType` detection logic

## Labs

| # | Topic | Files |
|---|-------|-------|
| 1 | Auto-Configuration Loader | HOW_IT_MATTERS.md, CODE_DEEP_DIVE.md |
| 2 | Conditional Evaluation | INTERNALS.md, THEORY.md |
| 3 | Post Processor Chain | STEP_BY_STEP.md |
| 4 | Custom Starter Creation | MINI_PROJECT, src/ |
| 5 | Actuator Deep Dive | ARCHITECTURE.md |
| 6 | Dispatcher Servlet Pipeline | DEBUGGING.md |
| 7 | Embedded Container Comparison | PERFORMANCE.md |