# Reflection

## Before This Lab
- I thought Spring Boot was "just Spring, but easier."
- I didn't understand the conditional mechanism behind auto-configuration.
- I used @Value everywhere instead of @ConfigurationProperties.

## After This Lab
- Spring Boot is a sophisticated framework built on top of Spring with powerful auto-configuration.
- The `spring.factories` / `AutoConfiguration.imports` file is the key entry point.
- Conditional annotations are evaluated at runtime to decide which beans to create.
- @ConfigurationProperties provides type-safe, grouped configuration binding.
- The actuator is invaluable for debugging and production monitoring.

## Key Takeaways
1. Spring Boot's power comes from convention over configuration.
2. Understanding conditionals helps debug "why didn't my bean get created?"
3. Custom starters enable reusable, opinionated configurations.
4. Profiles enable environment-specific configuration.
5. Spring Boot is production-ready out of the box.
