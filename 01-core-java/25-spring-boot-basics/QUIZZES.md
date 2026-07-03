# Module 25: Spring Boot Basics - Quizzes

---

## Q1: @SpringBootApplication
What three annotations are implicitly combined within `@SpringBootApplication`?

A) `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan`
B) `@Controller`, `@Service`, `@Repository`
C) `@AutoWired`, `@Bean`, `@Component`
D) `@EnableWebMvc`, `@Configuration`, `@Bean`

**Answer**: A
**Explanation**: `@SpringBootApplication` acts as a shortcut for `@Configuration` (allows registering extra beans), `@EnableAutoConfiguration` (enables Spring Boot's auto-configuration mechanism), and `@ComponentScan` (enables component scanning on the package where the application is located).

---

## Q2: Embedded Servers
What is the default embedded web server provided by `spring-boot-starter-web`?

A) Jetty
B) Undertow
C) Tomcat
D) GlassFish

**Answer**: C
**Explanation**: By default, Spring Boot uses an embedded Apache Tomcat server. It can be easily excluded and replaced with Jetty or Undertow if desired.

---

## Q3: Externalized Configuration
Which of the following files is picked up by default by Spring Boot to read configuration properties?

A) `config.xml`
B) `settings.json`
C) `application.properties` (or `application.yml`)
D) `boot.config`

**Answer**: C
**Explanation**: Spring Boot automatically loads properties from `application.properties` or `application.yml` located in the classpath (typically `src/main/resources`).