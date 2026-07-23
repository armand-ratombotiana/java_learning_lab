# Mock Interview: Spring Boot Basics (Lab 01)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Let's start simple. What is Spring Boot and how is it different from the Spring Framework?

**Candidate:** Spring Boot is an extension of the Spring Framework that simplifies bootstrapping and development. It eliminates boilerplate configuration by providing auto-configuration, embedded servers, and a wide range of starter dependencies. The main difference is that Spring Framework requires manual configuration of XML or annotation-based setups, while Spring Boot uses opinionated defaults via auto-configuration, embedded Tomcat/Jetty/Undertow, and starter POMs that bundle compatible dependencies.

**Interviewer:** Good. What does the `@SpringBootApplication` annotation actually do?

**Candidate:** `@SpringBootApplication` is a composite annotation that combines three others:
1. `@Configuration` — marks the class as a source of bean definitions
2. `@EnableAutoConfiguration` — tells Spring Boot to automatically configure beans based on classpath dependencies
3. `@ComponentScan` — enables component scanning in the current package and sub-packages

It's equivalent to declaring all three separately. One important detail is that the component scan scope defaults to the package of the annotated class, which is why you typically place it at the root of your package hierarchy.

**Interviewer:** Perfect. Let's go a bit deeper.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does auto-configuration work internally? Walk me through what happens when Spring Boot starts.

**Candidate:** When Spring Boot starts, it looks for `spring.factories` files in the `META-INF` directory of all JARs on the classpath. This file lists auto-configuration classes under the `org.springframework.boot.autoconfigure.EnableAutoConfiguration` key.

Each auto-configuration class is annotated with `@Conditional` conditions that determine whether it should be applied. These conditions include:
- `@ConditionalOnClass` — checks if certain classes are on the classpath
- `@ConditionalOnMissingBean` — checks if a bean hasn't already been defined
- `@ConditionalOnProperty` — checks application properties
- `@ConditionalOnWebApplication` — checks if it's a web application

For example, if `DataSource` and H2 classes are on the classpath, `DataSourceAutoConfiguration` creates an in-memory database. But if you define your own `DataSource` bean, `@ConditionalOnMissingBean` prevents the auto-configured one from being created.

**Interviewer:** How would you create a custom Spring Boot starter?

**Candidate:** A custom starter requires:
1. An auto-configuration class with `@Configuration` and appropriate `@Conditional` annotations
2. A `spring.factories` file in `src/main/resources/META-INF/` listing the auto-configuration class
3. An optional `application.properties` for default configuration values
4. A properties class annotated with `@ConfigurationProperties` for user-configurable settings

The naming convention is `*-spring-boot-autoconfigure` for the auto-configuration module and `*-spring-boot-starter` for the starter that aggregates the autoconfigure module and dependencies.

For example, a notification starter would have `NotificationAutoConfiguration`, `NotificationProperties`, and if the user doesn't define a `NotificationService` bean, the starter auto-configures a default one.

**Interviewer:** What happens during conditional evaluation failures? How would you debug auto-configuration issues?

**Candidate:** Spring Boot generates a **Condition Evaluation Report** that can be accessed through:
- The `/actuator/conditions` endpoint if Spring Actuator is enabled
- Debug logging: setting `--debug` or `logging.level.org.springframework.boot.autoconfigure=DEBUG`
- The auto-configuration report shows which conditions matched and which didn't, along with the reason

For example, you might see `DataSourceAutoConfiguration matched: @ConditionalOnClass found classes 'javax.sql.DataSource', 'org.h2.Driver'` and `JmxAutoConfiguration did not match: @ConditionalOnClass required classes 'org.springframework.jmx.export.MBeanExporter' not found`.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your Spring Boot application takes 30 seconds to start. How would you diagnose and fix this?

**Candidate:** I would approach this systematically:

**Diagnosis:**
1. Enable `--debug` to see auto-configuration report — identifies unnecessary auto-configurations
2. Enable `--logging.level.org.springframework.boot=DEBUG` to see bean creation timing
3. Use Spring Boot 3.x Startup Reporter (clickhouse or startup-report) for timeline analysis
4. Enable `spring.profiling.startup` to instrument startup events
5. Add `-XX:+PrintClassLoading` to JVM arguments to see class loading overhead

**Common causes and fixes:**

1. **Too many auto-configurations:** Exclude unused ones with `@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, ...})`
2. **Eager bean initialization:** Enable lazy initialization globally: `spring.main.lazy-initialization=true` — but be careful with web endpoints
3. **Component scanning too wide:** Restrict scan to specific packages: `@ComponentScan(basePackages = {"com.example.service"})`
4. **JPA/Hibernate metadata scanning:** Limit with `spring.jpa.properties.hibernate.implicit_naming_strategy` and `@EntityScan`
5. **Classpath scanning:** Use Spring Boot 3.x's AOT engine for compile-time scanning

**Advanced optimization:**
- Use Spring Context Index: `spring.convert-xml-to-configuration=true` and `META-INF/spring.components`
- Enable class data sharing (CDS) for JVM warm-up: `-XX:ArchiveClassesAtExit=application.jsa`
- Use GraalVM native image for instant startup (Lab 25)

**Interviewer:** Walk me through how `AutoConfigurationImportSelector` processes auto-configuration classes.

**Candidate:** `AutoConfigurationImportSelector` is the core of the auto-configuration mechanism. Here's the flow:

1. `AutoConfigurationImportSelector` implements `DeferredImportSelector`, meaning it runs after all regular `@Configuration` classes have been processed
2. It loads entries from all `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` files (replacing the older `spring.factories` approach)
3. Each entry is an auto-configuration class name (e.g., `org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration`)
4. The selector applies **ordering and filtering**:
   - `@AutoConfigureOrder` and `@AutoConfigureAfter/@AutoConfigureBefore` annotations control ordering
   - Auto-configuration classes are sorted based on these annotations
   - Conditions (`@ConditionalOnClass`, `@ConditionalOnMissingBean`, etc.) are evaluated
5. **Condition evaluation** happens via `ConditionEvaluator` which checks:
   - Class conditions (are required classes present?)
   - Bean conditions (is a bean already defined?)
   - Property conditions (do properties match?)
6. The matching auto-configuration classes are imported as `@Configuration` classes
7. The final result is recorded in the `AutoConfigurationReport` for debugging via the Actuator conditions endpoint

A critical detail: auto-configuration classes are processed **after** user-defined `@Configuration` classes, ensuring that user beans take precedence via `@ConditionalOnMissingBean`.

**Interviewer:** How does Spring Boot handle the transition from `spring.factories` to `AutoConfiguration.imports` in version 3.x?

**Candidate:** In Spring Boot 3.x, the auto-configuration registration mechanism shifted from `META-INF/spring.factories` to `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. This was part of the migration to Jakarta EE namespace (javax to jakarta). The new file format is simpler — one auto-configuration class per line. Spring Boot 2.7.x supports both with a bridge mechanism, but 3.x requires the new format. The change improves startup performance by reducing file parsing overhead.

---

## Interviewer Feedback

**Strengths:**
- Strong understanding of auto-configuration internals
- Can articulate the conditional evaluation flow
- Good diagnostic approach for startup performance
- Knows the evolution from spring.factories to AutoConfiguration.imports

**Areas to Improve:**
- Mention the `@SpringBootApplication` excludes parameter and how it affects startup time
- Could discuss `AutoConfiguration.imports` vs `spring.factories` performance differences

**Verdict:** Strong Hire — deep understanding of Spring Boot fundamentals with practical debugging experience.

---

## Follow-Up Questions (Self-Study)

1. How would you configure different logging levels for different packages in Spring Boot?
2. What happens if two auto-configuration classes have a circular `@AutoConfigureAfter` dependency?
3. How does `@ConditionalOnClass` with `name` attribute differ from `value` attribute?
4. Explain the difference between `@ConditionalOnMissingBean` and `@ConditionalOnMissingClass`.
5. How would you create a custom failure analyzer for a specific configuration error?

---

*Lab 01 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
