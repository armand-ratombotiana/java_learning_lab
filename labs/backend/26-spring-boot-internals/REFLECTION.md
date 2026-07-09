# Reflection — Spring Boot Internals

## Key Insights

1. Auto-configuration is fundamentally a `DeferredImportSelector` pattern, not magic
2. The conditional evaluation is a strategy pattern — each `@Conditional*` annotation maps to a `Condition` implementation
3. `spring.factories` was a general-purpose SPI mechanism; `AutoConfiguration.imports` is specialized for auto-configuration
4. The embedded container is created during `onRefresh()`, not during SpringApplication bootstrapping
5. Actuator health indicators follow a composite pattern for aggregation

## What I Would Do Differently

- The classloader hierarchy for fat JARs adds complexity; a module-path approach would be cleaner
- `spring.factories` was too generic; the new `AutoConfiguration.imports` file is a better design
- Actuator endpoint discovery could be more dynamic
- Conditional evaluation is expensive at startup; AOT compilation improves this

## Deeper Questions

- How does the `ConfigurationClassPostProcessor` handle circular `@Configuration` references?
- When would you need `BeanDefinitionRegistryPostProcessor` over `BeanFactoryPostProcessor`?
- How does `WebApplicationType.NONE` handle web-related auto-configuration exclusion?
- What happens if two embedded server JARs are on the classpath simultaneously?