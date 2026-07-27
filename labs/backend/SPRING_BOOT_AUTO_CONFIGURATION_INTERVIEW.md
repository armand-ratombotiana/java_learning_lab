# Spring Boot Auto-Configuration Deep Interview Guide — Wave 6

> Target: 350+ lines covering @EnableAutoConfiguration, @Conditional, spring.factories, custom starters, debugging, metadata, overrides

---

## 1. Fundamentals

### Q: What is `@EnableAutoConfiguration` and how does it work?

**Answer:**
`@EnableAutoConfiguration` is the core of Spring Boot. It triggers auto-configuration by scanning for `AutoConfiguration` classes listed in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (previously `META-INF/spring.factories` in Boot < 2.7).

`@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { ... })
public @interface SpringBootApplication { }
```

**Auto-configuration flow:**
1. `@EnableAutoConfiguration` imports `AutoConfigurationImportSelector`
2. This selector reads `AutoConfiguration.imports` file
3. Filters by `@Conditional` annotations
4. Applies ordering via `@AutoConfigureOrder`, `@AutoConfigureBefore`, `@AutoConfigureAfter`
5. Creates beans only when conditions match

```java
// Example: auto-configuration for DataSource
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(properties.getType())
                .build();
    }
}
```

**Company Frequency:** Pivotal/VMware (essential), Google (high), Amazon (high)

**Follow-ups:**
- What changed between `spring.factories` and `AutoConfiguration.imports`?
- How does Spring Boot avoid applying all auto-config classes at startup?

---

### Q: spring.factories vs AutoConfiguration.imports

**Answer:**

| Aspect | spring.factories (Boot < 2.7) | AutoConfiguration.imports (Boot 2.7+) |
|--------|-------------------------------|---------------------------------------|
| Format | Key-value properties file | Single line per class |
| File name | `META-INF/spring.factories` | `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` |
| Other uses | Loads `ApplicationContextInitializer`, `SpringApplicationRunListener`, `FailureAnalyzer` | Auto-configuration only |
| Key | `org.springframework.boot.autoconfigure.EnableAutoConfiguration` | N/A (filename is the key) |
| Migration | Backward compatible (Boot 2.7 supports both) | New standard |

**Old vs new format:**
```properties
# spring.factories (old)
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.MyAutoConfiguration,\
com.example.AnotherAutoConfiguration
```

```text
# AutoConfiguration.imports (new)
com.example.MyAutoConfiguration
com.example.AnotherAutoConfiguration
```

**Company Frequency:** Pivotal/VMware (essential), Netflix (high), Google (medium)

---

## 2. @Conditional Annotations

### Q: All @Conditional annotations explained

**Answer:**

```java
// @ConditionalOnClass — auto-configure only if class is on classpath
@ConditionalOnClass(name = "org.postgresql.Driver")
public class PostgresAutoConfiguration { }

// @ConditionalOnMissingClass — invert
@ConditionalOnMissingClass("com.mysql.cj.jdbc.Driver")

// @ConditionalOnBean — only if bean exists in context
@ConditionalOnBean(JdbcTemplate.class)
public class JdbcTemplateMetrics { }

// @ConditionalOnMissingBean — default impl if no custom bean
@ConditionalOnMissingBean(DataSource.class)
public DataSource defaultDataSource() { }

// @ConditionalOnProperty — based on application property
@ConditionalOnProperty(
    value = "app.feature.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class FeatureXConfig { }

// @ConditionalOnExpression — SpEL expression
@ConditionalOnExpression(
    "${app.region:dev} == 'prod' && ${app.replica-count:1} > 1")
public class ProdMultiReplicaConfig { }

// @ConditionalOnSingleCandidate — single primary bean
@ConditionalOnSingleCandidate(DataSource.class)
public class DataSourceHealthIndicatorAutoConfiguration { }

// @ConditionalOnResource — file exists on classpath
@ConditionalOnResource(resources = "classpath:logback-spring.xml")

// @ConditionalOnJndi — JNDI InitialContext available
@ConditionalOnJndi("java:comp/env/DataSource")

// @ConditionalOnCloudPlatform — cloud platform detection
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
public class KubernetesConfig { }

// @ConditionalOnJava — JDK version range
@ConditionalOnJava(range = JavaRange.EQUAL_OR_NEWER, value = JavaVersion.SEVENTEEN)
public class VirtualThreadConfig { }
```

**Company Frequency:** Google (very high), Amazon (high), Pivotal (essential)

**Follow-ups:**
- What order are conditions evaluated? (Class conditions first, then bean conditions)
- Can you create custom `@Conditional` annotations? (Yes — implement `Condition` interface)

---

### Q: Create a custom @Conditional annotation

**Answer:**

```java
// Step 1: Create annotation
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnSystemPropertyCondition.class)
public @interface ConditionalOnSystemProperty {
    String name();
    String value();
}

// Step 2: Implement Condition interface
public class OnSystemPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(
            ConditionalOnSystemProperty.class.getName());
        String propName = (String) attributes.get("name");
        String propValue = (String) attributes.get("value");
        return propValue.equals(System.getProperty(propName));
    }
}

// Step 3: Use
@Configuration
@ConditionalOnSystemProperty(name = "app.env", value = "production")
public class ProductionOnlyConfig { }
```

**Company Frequency:** Google (often), Netflix (medium), Pivotal (high)

---

## 3. Custom Auto-Configuration / Starter

### Q: Create a custom Spring Boot Starter

**Answer:**

Structure:
```
my-starter/
├── pom.xml
└── src/main/
    └── resources/
        └── META-INF/
            └── spring/
                └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

```java
// 1. Properties class
@ConfigurationProperties(prefix = "greeting")
public class GreetingProperties {
    private String prefix = "Hello";
    private String suffix = "!";
    // getters/setters
}

// 2. Service
public class GreetingService {
    private final String prefix;
    private final String suffix;

    public GreetingService(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String greet(String name) {
        return prefix + " " + name + suffix;
    }
}

// 3. Auto-Configuration
@AutoConfiguration
@ConditionalOnClass(GreetingService.class)
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GreetingService greetingService(GreetingProperties props) {
        return new GreetingService(props.getPrefix(), props.getSuffix());
    }
}

// 4. AutoConfiguration.imports (file content)
// com.example.starter.GreetingAutoConfiguration

// 5. Enable auto-config in spring.factories (Boot < 2.7)
// org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
// com.example.starter.GreetingAutoConfiguration
```

**Additional artifacts for a complete starter:**
```
my-starter-autoconfigure/  (auto-config + properties)
my-starter-starter/         (empty pom, just depends on autoconfigure)
```

**Company Frequency:** Google (often), Netflix (high), Pivotal (essential)

**Follow-ups:**
- Why two modules (autoconfigure + starter)? (Separation of concerns; third-party may want autoconfigure without starter)
- How to test auto-configuration? (Using `ApplicationContextRunner`)

---

### Q: Testing auto-configuration with ApplicationContextRunner

**Answer:**

```java
class GreetingAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GreetingAutoConfiguration.class));

    @Test
    void defaultGreetingService() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(GreetingService.class);
            GreetingService service = context.getBean(GreetingService.class);
            assertThat(service.greet("John")).isEqualTo("Hello John!");
        });
    }

    @Test
    void customProperties() {
        contextRunner
            .withPropertyValues("greeting.prefix=Hi", "greeting.suffix=!!!")
            .run(context -> {
                GreetingService service = context.getBean(GreetingService.class);
                assertThat(service.greet("John")).isEqualTo("Hi John!!!");
            });
    }

    @Test
    void disabledWhenClassMissing() {
        // Use FilteredClassLoader to simulate missing dependency
        contextRunner
            .withClassLoader(new FilteredClassLoader(GreetingService.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean(GreetingService.class);
            });
    }

    @Test
    void customBeanOverride() {
        contextRunner
            .withBean(GreetingService.class, () -> new GreetingService("Yo", "."))
            .run(context -> {
                GreetingService service = context.getBean(GreetingService.class);
                assertThat(service.greet("John")).isEqualTo("Yo John.");
            });
    }
}
```

**Company Frequency:** Google (high), Pivotal (essential), Netflix (medium)

---

## 4. Debugging Auto-Configuration

### Q: How to debug auto-configuration?

**Answer:**

**Method 1: `--debug` flag**
```bash
java -jar myapp.jar --debug
# or in application.properties:
debug=true
```

This prints an **auto-configuration report** showing:
- `CONDITIONS EVALUATION REPORT` — which auto-config classes matched/unmatched and why
- `Positive matches` — conditions that passed
- `Negative matches` — conditions that failed (with reason)
- `Exclusions` — explicitly excluded configs
- `Unconditional classes` — classes matched without conditions

**Method 2: actuator endpoint**
```properties
management.endpoints.web.exposure.include=conditions
```

```bash
GET /actuator/conditions
```

**Method 3: Logging auto-configuration**
```yaml
logging:
  level:
    org.springframework.boot.autoconfigure: DEBUG
```

**Method 4: IDE integration**
- Spring Boot tools plugin in IntelliJ/Eclipse shows auto-config report
- Spring Boot Dashboard in VS Code

**Sample report output:**
```
============================
CONDITIONS EVALUATION REPORT
============================

Positive matches:
-----------------
DataSourceAutoConfiguration matched:
  - @ConditionalOnClass found required classes 'javax.sql.DataSource', 'org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType' (OnClassCondition)
  - @ConditionalOnMissingBean (types: javax.sql.DataSource; SearchStrategy: all) found no beans (OnBeanCondition)

Negative matches:
-----------------
ActiveMQAutoConfiguration:
  Did not match:
    - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory' (OnClassCondition)
```

**Company Frequency:** All companies (universal skill)

**Follow-ups:**
- How to exclude specific auto-configuration? (`exclude = {DataSourceAutoConfiguration.class}` or `spring.autoconfigure.exclude`)
- What is the order of auto-config application? (Controlled by `@AutoConfigureOrder`, `@AutoConfigureBefore`, `@AutoConfigureAfter`)

---

### Q: Overriding auto-configuration — exclude and excludeName

**Answer:**

```java
// Method 1: @SpringBootApplication exclude
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class MyApplication { }

// Method 2: exclude by fully qualified name
@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
public class MyApplication { }

// Method 3: application.properties
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

// Method 4: Conditional override (provide custom bean before auto-config)
@Configuration
public class CustomDataSourceConfig {
    @Bean
    @Primary
    public DataSource customDataSource() {
        return new HikariDataSource(); // Override auto-configured DataSource
    }
}

// Method 5: spring.factories exclude in test
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
```

**Company Frequency:** Amazon (very high), Google (high), all companies

**Follow-ups:**
- What happens if you exclude an auto-config that other auto-configs depend on?
- How to see which auto-config classes are active at runtime? (actuator/conditions)

---

## 5. Configuration Metadata

### Q: spring-configuration-metadata.json

**Answer:**
Spring Boot generates `spring-configuration-metadata.json` from `@ConfigurationProperties` classes to provide IDE auto-completion (IntelliJ, VS Code, Eclipse).

```json
{
  "groups": [
    {
      "name": "greeting",
      "type": "com.example.GreetingProperties",
      "sourceType": "com.example.GreetingProperties"
    }
  ],
  "properties": [
    {
      "name": "greeting.prefix",
      "type": "java.lang.String",
      "description": "Prefix for greeting message",
      "sourceType": "com.example.GreetingProperties",
      "defaultValue": "Hello"
    },
    {
      "name": "greeting.suffix",
      "type": "java.lang.String",
      "description": "Suffix for greeting message",
      "sourceType": "com.example.GreetingProperties",
      "defaultValue": "!"
    }
  ],
  "hints": [
    {
      "name": "greeting.prefix",
      "values": [
        { "value": "Hello", "description": "Formal greeting" },
        { "value": "Hi", "description": "Casual greeting" },
        { "value": "Yo", "description": "Slang greeting" }
      ]
    }
  ]
}
```

**Generate metadata automatically:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

**Manual metadata:** Place in `META-INF/spring-configuration-metadata.json` (for third-party jars without source access).

**Company Frequency:** Pivotal/VMware (essential), Google (high), all teams

---

## 6. Common Interview Questions

### Q: How does Spring Boot know which DataSource to configure when multiple drivers are on classpath?

**Answer:**
Spring Boot's `DataSourceAutoConfiguration` checks:

1. `@ConditionalOnClass` matches for `DataSource`, `EmbeddedDatabaseType`
2. It tries to create a DataSource using these strategies in order:
   - **HikariCP** (if available) — preferred
   - **Tomcat JDBC** (if available)
   - **Commons DBCP2** (if available)
   - **Oracle UCP** (if available)
   - **Simple DriverDataSource** (fallback)
3. `spring.datasource.type` property can force a specific implementation
4. If `spring.datasource.url` is set, it's used. If not, Boot tries embedded database (H2, HSQL, Derby)

```java
// From DataSourceBuilder
if (type == null) {
    try {
        // Check HikariCP first
        type = (Class<? extends DataSource>)
            ClassUtils.forName("com.zaxxer.hikari.HikariDataSource", classLoader);
    } catch (...) {
        // Fall through to tomcat-jdbc, dbcp2, etc.
    }
}
```

**Company Frequency:** Amazon (high), Google (medium), Netflix (medium)

---

### Q: How to conditionally enable a bean based on a property value?

**Answer:**

```java
@Configuration
public class FeatureConfig {

    @Bean
    @ConditionalOnProperty(name = "app.feature.new-checkout", havingValue = "true", matchIfMissing = false)
    public CheckoutService newCheckoutService() {
        return new NewCheckoutService();
    }

    @Bean
    @ConditionalOnProperty(name = "app.feature.new-checkout", havingValue = "false", matchIfMissing = true)
    public CheckoutService legacyCheckoutService() {
        return new LegacyCheckoutService();
    }
}
```

**Or using SpEL:**
```java
@Bean
@ConditionalOnExpression("${app.feature.ratio:0} > 0.5")
public CheckoutService canaryService() { return new CanaryCheckoutService(); }
```

**Company Frequency:** Amazon (very high), Google (high), all companies

---

### Q: What is the difference between `@ConditionalOnBean` and `@ConditionalOnMissingBean`?

**Answer:**

| Annotation | Behavior | Use Case |
|------------|----------|----------|
| `@ConditionalOnBean` | Match if bean **exists** in context | Add features requiring existing bean (e.g., metrics when JdbcTemplate exists) |
| `@ConditionalOnMissingBean` | Match if bean **does NOT exist** | Provide default implementation (overridable by user) |

```java
@Configuration
public class JdbcMetricsConfig {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)  // only if JdbcTemplate is configured
    public JdbcTemplateMetrics jdbcMetrics(JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateMetrics(jdbcTemplate);
    }
}

@Configuration
public class DefaultCacheConfig {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)  // only if user hasn't defined one
    public CacheManager defaultCacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
```

**Important: `@ConditionalOnBean` uses `BeanFactory` state at the time of processing. Order matters!**

---

### Q: Auto-configuration ordering — @AutoConfigureBefore, @AutoConfigureAfter, @AutoConfigureOrder

**Answer:**

```java
@AutoConfiguration
@AutoConfigureAfter(DataSourceAutoConfiguration.class) // runs after DataSource is configured
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class) // runs before JPA
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
public class MyCustomAutoConfiguration { }

// Alternate: implement Ordered
public class MyAutoConfiguration implements Ordered {
    @Override
    public int getOrder() {
        return 0;
    }
}
```

**Default ordering:**
1. `@AutoConfigureOrder` value (lowest first)
2. `@AutoConfigureBefore` / `@AutoConfigureAfter` relationships
3. Within same order, order of discovery is undefined

**Company Frequency:** Pivotal/VMware (essential), Netflix (high)

---

## 7. Company-Specific Questions

### Q: [Google] Describe how auto-configuration could be optimized for startup time in cloud-native apps.

**Answer:**
Google Cloud Run / GKE environments care deeply about cold start latency.

1. **Enable auto-configuration report** to identify unused configs
2. **Exclude unnecessary auto-configs:**
   ```properties
   spring.autoconfigure.exclude=\
     org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
     org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
   ```
3. **Use `spring.jmx.enabled=false`** (saves MBean registration time)
4. **Lazy initialization** (Spring Boot 2.2+):
   ```properties
   spring.main.lazy-initialization=true
   ```
5. **Use `spring-context-indexer`** to speed up component scanning:
   ```xml
   <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-context-indexer</artifactId>
       <optional>true</optional>
   </dependency>
   ```
6. **Project CRaC** (Coordinated Restore at Checkpoint) — checkpoint app after startup, restore instantly
7. **AOT processing** and GraalVM native-image (Spring Boot 3.x)
8. **Optimize auto-config conditional evaluation** — classpath checks are cheaper than bean checks, so put class-based conditions first

**Google-style answer:** Use startup time profiling, trim classpath, apply lazy-init, and ultimately compile to native image with GraalVM.

---

### Q: [Amazon] You need to modify an auto-configured bean's behavior without excluding it. How?

**Answer:**
Amazon values practical, minimal-overhead solutions.

```java
// 1. Override the bean — @ConditionalOnMissingBean ensures user beans win
@Configuration
public class CustomDataSourceConfig {
    @Bean
    @Primary
    public DataSource dataSource() {
        // Custom HikariConfig — override only specific settings
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(5000);
        return new HikariDataSource(config);
    }
}

// 2. Use properties (avoid code changes)
// application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      connection-timeout: 5000

// 3. BeanPostProcessor — modify bean after auto-config creates it
@Component
public class DataSourcePostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof HikariDataSource ds) {
            ds.setMaximumPoolSize(100);
        }
        return bean;
    }
}

// 4. Use @ConditionalOnBean + @Configuration — add around auto-config beans
@Configuration
@ConditionalOnBean(HikariDataSource.class)
public class DataSourceCustomizer {
    @Bean
    public HikariDataSourceCustomizer customizer() {
        return ds -> ds.setMaximumPoolSize(100);
    }
}
```

**Amazon-style answer:** Properties > Bean Override > BeanPostProcessor > Custom Auto-Configuration. Prefer the simplest solution that doesn't break upgrades.

---

### Q: [Netflix] How do you create auto-configuration that works across multiple microservices in the same organization?

**Answer:**
Netflix built Spring Cloud — they think about shared internal frameworks.

```java
// 1. Internal starter with common defaults
@AutoConfiguration
@ConditionalOnClass(InternalServiceMarker.class) // marker class every service includes
public class InternalServiceAutoConfiguration {

    @Bean
    public Tracer tracer() {
        return new CustomTracer();
    }

    @Bean
    public StandardErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
    }
}

// 2. Feature flags via config
@Configuration
@ConditionalOnProperty("internal.features.metrics.enabled")
public class MetricsConfig { }

// 3. Versioned auto-config — support different Spring Boot versions
@AutoConfiguration(before = HibernateJpaAutoConfiguration.class)
@ConditionalOnClass(name = "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration")
public class JpaCustomConfigV3 { } // Boot 3.x only

// 4. Auto-configuration tests in CI ensure no regressions
// Use ApplicationContextRunner in every build
```

**Netflix-style answer:** Create shared internal starters, use marker classes, feature-flag everything, test auto-configurations with `ApplicationContextRunner`, maintain backward compatibility.

---

## 8. Auto-Configuration Pitfalls

### Q: What are common auto-configuration pitfalls?

**Answer:**

| Pitfall | Problem | Solution |
|---------|---------|----------|
| **ClassNotFoundException** | `@ConditionalOnClass` checks `className` string — typo causes silent failure | Use `ClassUtils.isPresent()` in condition |
| **Circular auto-config** | Two auto-configs depend on each other's beans | Use `@AutoConfigureAfter` / `@AutoConfigureBefore` explicitly |
| **Bean already defined** | User and auto-config both try to create same bean | Always use `@ConditionalOnMissingBean` on default beans |
| **Wrong order** | Auto-config runs before dependencies | Declare ordering annotations; use `@AutoConfigureOrder` |
| **Proxy issues** | Auto-configured beans that are `@Transactional` need AOP | Ensure `@EnableAspectJAutoProxy` is applied; prefer `proxyTargetClass=true` |
| **ConditionalOnBean failure** | Bean condition fails because the bean hasn't been processed yet | Use `@ConditionalOnBean` with `search = SearchStrategy.CURRENT` or `PARENTS` |
| **Too many auto-configs** | Slow startup, classpath scanning overhead | Use `--debug` to identify unused, exclude them |

---

### Q: How does auto-configuration handle `@ConfigurationProperties` binding?

**Answer:**

```java
// 1. Properties class with prefix
@ConfigurationProperties(prefix = "app.datasource")
public class AppDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private int maxPoolSize = 10;
    // getters/setters
}

// 2. Auto-config enables properties binding
@AutoConfiguration
@EnableConfigurationProperties(AppDataSourceProperties.class)
public class AppDataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(AppDataSourceProperties props) {
        return DataSourceBuilder.create()
            .url(props.getUrl())
            .username(props.getUsername())
            .password(props.getPassword())
            .build();
    }
}
```

**Company Frequency:** All companies (universal)

**Follow-ups:**
- What is `@ConfigurationPropertiesScan`?
- How does relaxed binding work? (`my-property` = `myProperty` = `MY_PROPERTY`)

---

> **End of SPRING_BOOT_AUTO_CONFIGURATION_INTERVIEW.md**
> Total questions: ~20+ covering fundamentals, @Conditional, custom starters, debugging, metadata, overrides, company-specific
