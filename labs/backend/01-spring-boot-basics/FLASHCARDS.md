# Flashcards

## Q: What does @SpringBootApplication include?
**A:** @Configuration + @EnableAutoConfiguration + @ComponentScan

## Q: How to change Spring Boot server port?
**A:** Set `server.port=8081` in application.properties

## Q: What is a Spring Boot Starter?
**A:** A curated set of dependencies for a specific feature (e.g., spring-boot-starter-web)

## Q: How auto-configuration is triggered?
**A:** By classpath presence of certain classes, evaluated via @Conditional annotations

## Q: What file lists auto-configuration classes?
**A:** `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Q: How to run Spring Boot app from command line?
**A:** `mvn spring-boot:run` or `java -jar app.jar`

## Q: What actuator endpoint shows health?
**A:** GET /actuator/health

## Q: How to exclude auto-configuration?
**A:** `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`

## Q: What is spring-boot-starter-parent?
**A:** A Maven POM providing default configurations, dependency management, and plugins

## Q: How to access command line arguments in Spring Boot?
**A:** Inject `ApplicationArguments` bean or implement `CommandLineRunner`
