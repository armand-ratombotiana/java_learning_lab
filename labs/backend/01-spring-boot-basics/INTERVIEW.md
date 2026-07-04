# Interview Questions

## Basic
1. What is Spring Boot and how does it differ from Spring Framework?
2. Explain the `@SpringBootApplication` annotation.
3. How does auto-configuration work internally?
4. What are Spring Boot starters? Give examples.
5. How do you configure a Spring Boot application?

## Intermediate
6. Explain conditional annotations used in auto-configuration.
7. How do you create a custom Spring Boot starter?
8. What is the difference between `application.properties` and `application.yml`?
9. Explain Spring Boot's property resolution order.
10. How do you use profiles in Spring Boot?

## Advanced
11. How would you optimize Spring Boot startup time?
12. Explain how `AutoConfigurationImportSelector` works.
13. How do you handle database migrations with Spring Boot?
14. What is the difference between `@EnableAutoConfiguration` and `@SpringBootApplication`?
15. How do you deploy Spring Boot as a WAR to external container?

## Scenario
"You have a Spring Boot app that takes 30 seconds to start. How would you diagnose and fix it?"
- Check auto-configuration exclusions
- Enable lazy initialization
- Use Spring Boot 3.2+ with CDS (Class Data Sharing)
- Profile startup with Spring Boot Startup Reporter
