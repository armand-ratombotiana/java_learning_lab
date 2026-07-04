# Quiz

1. What annotation is equivalent to @Configuration + @EnableAutoConfiguration + @ComponentScan?
2. What is the default embedded servlet container in Spring Boot?
3. How do you exclude a specific auto-configuration class?
4. What is the purpose of `spring.factories` file?
5. How can you debug which auto-configuration was applied?
6. What is the difference between `@Value` and `@ConfigurationProperties`?
7. How do you change the port in Spring Boot?
8. What does `@ConditionalOnMissingBean` do?
9. How do you add custom health check?
10. What actuator endpoint shows all beans in context?

## Answers
1. @SpringBootApplication
2. Tomcat
3. @SpringBootApplication(exclude = ClassName.class)
4. Registers auto-configuration classes
5. Set `debug=true` or GET /actuator/conditions
6. @Value is for single values, @ConfigurationProperties binds structured groups
7. server.port property
8. Creates bean only if no other bean of that type exists
9. Implement HealthIndicator interface
10. /actuator/beans
