# Module 29: Spring Boot Advanced - Quizzes

---

## Q1: Actuator Endpoints
Which of the following Actuator endpoints provides a snapshot of the JVM heap?

A) `/actuator/metrics`
B) `/actuator/env`
C) `/actuator/heapdump`
D) `/actuator/health`

**Answer**: C
**Explanation**: The `/actuator/heapdump` endpoint returns an HPROF heap dump file, which can be analyzed to diagnose memory leaks.

---

## Q2: Test Slicing
If you want to test ONLY your REST controllers without loading the entire application context or database, which annotation should you use?

A) `@SpringBootTest`
B) `@DataJpaTest`
C) `@WebMvcTest`
D) `@ControllerTest`

**Answer**: C
**Explanation**: `@WebMvcTest` disables full auto-configuration and applies only the configuration relevant to MVC tests (e.g., `@Controller`, `@ControllerAdvice`), while mocking away the service layer.

---

## Q3: Profiles
How can you quickly activate a specific profile named `dev` when running a Spring Boot JAR application?

A) `java -jar app.jar --profile=dev`
B) `java -jar app.jar --spring.profiles.active=dev`
C) `java -jar app.jar -env dev`
D) You must recompile the application for the `dev` profile.

**Answer**: B
**Explanation**: Spring Boot allows overriding configuration properties via command-line arguments. Setting `--spring.profiles.active=dev` activates the `dev` profile at startup.