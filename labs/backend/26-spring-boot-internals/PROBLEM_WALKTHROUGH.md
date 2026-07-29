# PROBLEM WALKTHROUGH: Design a Simplified Spring Boot Auto-Configuration Framework

## Problem Statement

Implement a minimal auto-configuration framework that mimics Spring Boot's `@EnableAutoConfiguration` mechanism. The framework should scan the classpath for `META-INF/spring.factories` files, load auto-configuration classes, apply `@Conditional` annotations, and register matching beans into a simple application context.

**Constraints:**
- No external dependencies (pure Java 21+)
- Support `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`
- Support `@AutoConfiguration` ordering
- Support overriding via `@EnableAutoConfiguration` exclusion

**Example usage:**
```java
public class MyApp {
    public static void main(String[] args) {
        SimpleApplicationContext context = SimpleSpringBoot.run(MyApp.class, args);
        DataSource ds = context.getBean(DataSource.class);
        // Auto-configured if H2 is on classpath
    }
}
```

---

## Step-by-Step Solution

### Step 1: Core Annotations

Define the building blocks: `@AutoConfiguration`, `@Conditional`, and the specific conditional annotations.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoConfiguration {
    int order() default Integer.MAX_VALUE;
    String[] exclude() default {};
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalOnClass {
    String[] value();
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalOnMissingBean {
    Class<?>[] value() default {};
    String[] name() default {};
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalOnProperty {
    String name();
    String havingValue() default "true";
    boolean matchIfMissing() default false;
}
```

### Step 2: Spring Factories Loader

Parse `META-INF/spring.factories` files from the classpath. The format is:
```
com.example.autoconfig.EnableAutoConfiguration=\
  com.example.autoconfigure.DataSourceAutoConfiguration,\
  com.example.autoconfigure.WebAutoConfiguration
```

```java
public class SpringFactoriesLoader {

    private static final String FACTORIES_RESOURCE = "META-INF/spring.factories";

    public static List<Class<?>> loadFactoryClasses(Class<?> factoryType, ClassLoader classLoader) {
        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE);
            List<String> classNames = new ArrayList<>();

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                try (InputStream is = url.openStream()) {
                    properties.load(is);
                }
                String value = properties.getProperty(factoryType.getName());
                if (value != null && !value.isBlank()) {
                    for (String className : value.split(",")) {
                        classNames.add(className.trim());
                    }
                }
            }

            return classNames.stream()
                .distinct()
                .map(name -> loadClass(name, classLoader))
                .filter(Objects::nonNull)
                .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load spring.factories", e);
        }
    }

    private static Class<?> loadClass(String name, ClassLoader classLoader) {
        try {
            return Class.forName(name, false, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
```

### Step 3: Conditional Evaluation Engine

Evaluate `@ConditionalOnClass`, `@ConditionalOnMissingBean`, and `@ConditionalOnProperty` on each auto-configuration class.

```java
public class ConditionalEvaluator {

    private final ClassLoader classLoader;
    private final BeanRegistry beanRegistry;

    public ConditionalEvaluator(ClassLoader classLoader, BeanRegistry beanRegistry) {
        this.classLoader = classLoader;
        this.beanRegistry = beanRegistry;
    }

    public boolean matches(Class<?> configClass) {
        return matchesOnClass(configClass)
            && matchesOnMissingBean(configClass)
            && matchesOnProperty(configClass);
    }

    boolean matchesOnClass(Class<?> configClass) {
        ConditionalOnClass annotation = configClass.getAnnotation(ConditionalOnClass.class);
        if (annotation == null) return true;
        return Arrays.stream(annotation.value())
            .allMatch(className -> {
                try {
                    Class.forName(className, false, classLoader);
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            });
    }

    boolean matchesOnMissingBean(Class<?> configClass) {
        ConditionalOnMissingBean annotation = configClass.getAnnotation(ConditionalOnMissingBean.class);
        if (annotation == null) return true;
        for (Class<?> beanType : annotation.value()) {
            if (beanRegistry.containsBean(beanType)) return false;
        }
        return true;
    }

    boolean matchesOnProperty(Class<?> configClass) {
        ConditionalOnProperty annotation = configClass.getAnnotation(ConditionalOnProperty.class);
        if (annotation == null) return true;
        String value = System.getProperty(annotation.name());
        if (value == null) return annotation.matchIfMissing();
        return annotation.havingValue().equals(value);
    }
}
```

### Step 4: Simple Bean Registry

A minimal registry that stores bean instances keyed by type and name.

```java
public class BeanRegistry {

    private final Map<Class<?>, Map<String, Object>> beansByType = new ConcurrentHashMap<>();
    private final Map<String, Object> beansByName = new ConcurrentHashMap<>();
    private final Map<Class<?>, Set<String>> namesByType = new ConcurrentHashMap<>();

    public void registerBean(String name, Object bean, Class<?>... types) {
        beansByName.put(name, bean);
        for (Class<?> type : types) {
            beansByType.computeIfAbsent(type, k -> new ConcurrentHashMap<>())
                .put(name, bean);
            namesByType.computeIfAbsent(type, k -> ConcurrentHashMap.newKeySet())
                .add(name);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Map<String, Object> typedBeans = beansByType.get(type);
        if (typedBeans == null || typedBeans.isEmpty()) {
            throw new RuntimeException("No bean of type: " + type);
        }
        if (typedBeans.size() > 1) {
            throw new RuntimeException("Multiple beans of type: " + type + ": " + typedBeans.keySet());
        }
        return (T) typedBeans.values().iterator().next();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getBeans(Class<T> type) {
        Map<String, Object> typedBeans = beansByType.getOrDefault(type, Map.of());
        return List.copyOf((Collection<T>) typedBeans.values());
    }

    public boolean containsBean(Class<?> type) {
        return beansByType.containsKey(type) && !beansByType.get(type).isEmpty();
    }

    public boolean containsBean(String name) {
        return beansByName.containsKey(name);
    }

    public Set<String> getBeanNames(Class<?> type) {
        return namesByType.getOrDefault(type, Set.of());
    }

    public Map<String, Object> getAllBeans() {
        return Collections.unmodifiableMap(beansByName);
    }

    public int beanCount() {
        return beansByName.size();
    }
}
```

### Step 5: Auto-Configuration Processor

Processes auto-configuration classes sorted by order, evaluating conditions and registering beans.

```java
public class AutoConfigurationProcessor {

    private final BeanRegistry beanRegistry;
    private final ConditionalEvaluator conditionalEvaluator;

    public AutoConfigurationProcessor(BeanRegistry beanRegistry, ClassLoader classLoader) {
        this.beanRegistry = beanRegistry;
        this.conditionalEvaluator = new ConditionalEvaluator(classLoader, beanRegistry);
    }

    public void process(Class<?> source, List<Class<?>> autoConfigClasses) {
        // Parse exclusions from @EnableAutoConfiguration or source
        Set<String> exclusions = parseExclusions(source);

        List<Class<?>> sortedConfigs = autoConfigClasses.stream()
            .filter(config -> !excluded(config, exclusions))
            .sorted(Comparator.comparingInt(this::getOrder))
            .toList();

        for (Class<?> configClass : sortedConfigs) {
            if (conditionalEvaluator.matches(configClass)) {
                try {
                    Object configInstance = configClass.getDeclaredConstructor().newInstance();
                    registerBeansFromConfig(configInstance);
                    System.out.println("[INFO] Applied auto-configuration: " + configClass.getSimpleName());
                } catch (Exception e) {
                    System.err.println("[WARN] Failed to apply: " + configClass.getSimpleName() + ": " + e.getMessage());
                }
            } else {
                System.out.println("[DEBUG] Skipped " + configClass.getSimpleName() + " (conditions not met)");
            }
        }
    }

    void registerBeansFromConfig(Object configInstance) throws Exception {
        for (Method method : configInstance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Bean.class)) {
                Object bean = method.invoke(configInstance);
                String beanName = extractBeanName(method);
                Class<?> returnType = method.getReturnType();
                if (returnType == void.class) continue;
                beanRegistry.registerBean(beanName, bean, returnType);
                // Also register interfaces
                for (Class<?> iface : returnType.getInterfaces()) {
                    beanRegistry.registerBean(beanName + "#" + iface.getSimpleName(), bean, iface);
                }
            }
        }
    }

    String extractBeanName(Method method) {
        Bean annotation = method.getAnnotation(Bean.class);
        if (annotation != null && !annotation.name().isBlank()) {
            return annotation.name();
        }
        return method.getName();
    }

    int getOrder(Class<?> configClass) {
        AutoConfiguration annotation = configClass.getAnnotation(AutoConfiguration.class);
        return annotation != null ? annotation.order() : Integer.MAX_VALUE;
    }

    Set<String> parseExclusions(Class<?> source) {
        Set<String> exclusions = new HashSet<>();
        AutoConfiguration annotation = source.getAnnotation(AutoConfiguration.class);
        if (annotation != null) {
            exclusions.addAll(List.of(annotation.exclude()));
        }
        EnableAutoConfiguration enableAnnotation = source.getAnnotation(EnableAutoConfiguration.class);
        if (enableAnnotation != null) {
            exclusions.addAll(Arrays.asList(enableAnnotation.exclude()));
        }
        return exclusions;
    }

    boolean excluded(Class<?> configClass, Set<String> exclusions) {
        return exclusions.contains(configClass.getName())
            || exclusions.contains(configClass.getSimpleName());
    }
}
```

### Step 6: Simple Application Context

The entry point that scans factories, processes auto-configurations, and provides bean access.

```java
public class SimpleApplicationContext implements AutoCloseable {

    private final BeanRegistry beanRegistry;
    private final ClassLoader classLoader;

    SimpleApplicationContext(BeanRegistry beanRegistry, ClassLoader classLoader) {
        this.beanRegistry = beanRegistry;
        this.classLoader = classLoader;
    }

    public <T> T getBean(Class<T> type) {
        return beanRegistry.getBean(type);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getBeans(Class<T> type) {
        return beanRegistry.getBeans(type);
    }

    public Map<String, Object> getAllBeans() {
        return beanRegistry.getAllBeans();
    }

    public int getBeanDefinitionCount() {
        return beanRegistry.beanCount();
    }

    @Override
    public void close() {
        // Cleanup resources that implement AutoCloseable
        beanRegistry.getAllBeans().values().stream()
            .filter(AutoCloseable.class::isInstance)
            .map(AutoCloseable.class::cast)
            .forEach(closeable -> {
                try { closeable.close(); }
                catch (Exception e) { System.err.println("Error closing bean: " + e.getMessage()); }
            });
    }
}
```

### Step 7: SimpleSpringBoot (Entry Point)

```java
public class SimpleSpringBoot {

    public static SimpleApplicationContext run(Class<?> source, String... args) {
        ClassLoader classLoader = source.getClassLoader();
        BeanRegistry beanRegistry = new BeanRegistry();

        // 1. Register the source class itself as a bean
        beanRegistry.registerBean("application", source, Class.class);

        // 2. Load auto-configuration factories
        List<Class<?>> autoConfigClasses = SpringFactoriesLoader.loadFactoryClasses(
            EnableAutoConfiguration.class, classLoader);

        System.out.println("[INFO] Found " + autoConfigClasses.size() + " auto-configuration candidates");

        // 3. Process auto-configurations
        AutoConfigurationProcessor processor = new AutoConfigurationProcessor(beanRegistry, classLoader);
        processor.process(source, autoConfigClasses);

        System.out.println("[INFO] Application started with " + beanRegistry.beanCount() + " beans");

        return new SimpleApplicationContext(beanRegistry, classLoader);
    }
}
```

### Step 8: Sample Auto-Configurations

```java
// META-INF/spring.factories
// com.example.autoconfig.EnableAutoConfiguration=\
//   com.example.autoconfig.DataSourceAutoConfiguration,\
//   com.example.autoconfig.WebAutoConfiguration

@AutoConfiguration(order = 1)
@ConditionalOnClass("org.h2.Driver")
public class DataSourceAutoConfiguration {

    @Bean
    public DataSource dataSource() {
        return new DataSource("jdbc:h2:mem:testdb", "sa", "");
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}

@AutoConfiguration(order = 10)
@ConditionalOnProperty(name = "server.port", matchIfMissing = true)
public class WebAutoConfiguration {

    @Bean
    public EmbeddedWebServer embeddedWebServer() {
        int port = Integer.parseInt(System.getProperty("server.port", "8080"));
        return new EmbeddedWebServer(port);
    }
}
```

### Step 9: Supporting Types

```java
public class DataSource {
    private final String url;
    private final String username;
    private final String password;

    public DataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() {
        System.out.println("[DataSource] Connecting to " + url);
        return new Connection(url);
    }

    public String getUrl() { return url; }
}

public record Connection(String url) implements AutoCloseable {
    public void execute(String sql) {
        System.out.println("[DB] Executing: " + sql);
    }
    @Override
    public void close() {
        System.out.println("[DB] Closing connection to " + url);
    }
}

public class JdbcTemplate {
    private final DataSource dataSource;
    public JdbcTemplate(DataSource dataSource) { this.dataSource = dataSource; }
    public void query(String sql) { dataSource.getConnection().execute(sql); }
}

public class EmbeddedWebServer implements AutoCloseable {
    private final int port;
    private volatile boolean running;

    public EmbeddedWebServer(int port) {
        this.port = port;
        this.running = true;
        System.out.println("[WebServer] Starting on port " + port);
    }

    public void stop() { this.running = false; System.out.println("[WebServer] Stopped"); }

    @Override
    public void close() { stop(); }
}
```

### Step 10: End-to-End Test

```java
public class AutoConfigurationDemo {
    public static void main(String[] args) {
        System.setProperty("server.port", "9090");
        try (SimpleApplicationContext ctx = SimpleSpringBoot.run(AutoConfigurationDemo.class)) {
            System.out.println("=== Beans: " + ctx.getBeanDefinitionCount() + " ===");
            ctx.getAllBeans().forEach((name, bean) ->
                System.out.println("  " + name + " -> " + bean.getClass().getSimpleName()));

            DataSource ds = ctx.getBean(DataSource.class);
            System.out.println("DataSource URL: " + ds.getUrl());

            JdbcTemplate jt = ctx.getBean(JdbcTemplate.class);
            jt.query("SELECT 1");

            EmbeddedWebServer server = ctx.getBean(EmbeddedWebServer.class);
            System.out.println("WebServer configured on port 9090");
        }
    }
}
```

---

## Complexity Analysis

| Aspect | Complexity | Notes |
|--------|------------|-------|
| **Factories loading** | O(N * M) | N = JARs, M = factories entries |
| **Condition evaluation** | O(C) per config class | C = number of conditions |
| **Bean registration** | O(1) average | Map-based lookup |
| **Bean retrieval** | O(1) average | Type + name map lookup |
| **Configuration sorting** | O(K log K) | K = auto-config classes |
| **Memory** | O(B) | B = number of registered beans |

**Comparison with Spring Boot:**
- Spring Boot uses `AnnotationMetadata` for richer conditional evaluation
- Spring Boot supports `@ConditionalOnBean`, `@ConditionalOnCloudPlatform`
- Spring Boot has sophisticated bean definition merging and aliasing
- Spring Boot's `ConditionEvaluator` handles `@Conditional` meta-annotations

---

## Follow-Up Questions

1. **How would you add `@ConditionalOnBean` support?** — Track bean types being registered, and before applying a config class, check if required bean types already exist in the registry.

2. **How do Spring Boot starter dependencies work?** — Each starter POM includes `spring-boot-starter` + library + auto-config module. Classpath presence triggers auto-config. Spring Boot's maven plugin generates `spring.provides`.

3. **What happens with circular auto-configuration dependencies?** — Config A depends on bean from Config B, and vice versa. Solution: deferred initialization, `@Lazy` proxies, or phase-based ordering.

4. **How would you support `@ConfigurationProperties` binding?** — Create a `PropertyBindingPostProcessor` that reads `@ConfigurationProperties`, instantiates the bean, binds properties via setters/records, and registers it.

5. **How does Spring Boot handle auto-configuration conflicts?** — `@AutoConfigureBefore`, `@AutoConfigureAfter`, `@AutoConfigureOrder` annotations determine precedence. `@ConditionalOnMissingBean` ensures user-defined beans override auto-configured ones.

---

## Test Cases

```java
class AutoConfigurationFrameworkTest {

    @Test
    void shouldLoadFactoryClasses() {
        List<Class<?>> factories = SpringFactoriesLoader.loadFactoryClasses(
            EnableAutoConfiguration.class,
            getClass().getClassLoader());
        assertThat(factories).isNotEmpty();
        assertThat(factories).allMatch(c -> c.isAnnotationPresent(AutoConfiguration.class));
    }

    @Test
    void shouldEvaluateConditionalOnClass() {
        BeanRegistry registry = new BeanRegistry();
        ConditionalEvaluator evaluator = new ConditionalEvaluator(getClass().getClassLoader(), registry);
        assertThat(evaluator.matchesOnClass(DataSourceAutoConfiguration.class)).isTrue();
    }

    @Test
    void shouldSkipWhenClassNotFound() {
        @ConditionalOnClass("com.nonexistent.Foo")
        class InvalidConfig {}
        BeanRegistry registry = new BeanRegistry();
        ConditionalEvaluator evaluator = new ConditionalEvaluator(getClass().getClassLoader(), registry);
        assertThat(evaluator.matchesOnClass(InvalidConfig.class)).isFalse();
    }

    @Test
    void shouldRespectOrder() {
        @AutoConfiguration(order = 5) class ConfigA {}
        @AutoConfiguration(order = 1) class ConfigB {}
        @AutoConfiguration(order = 10) class ConfigC {}
        var configs = List.of(ConfigA.class, ConfigB.class, ConfigC.class);
        var processor = new AutoConfigurationProcessor(new BeanRegistry(), getClass().getClassLoader());
        var sorted = configs.stream()
            .sorted(Comparator.comparingInt(processor::getOrder))
            .toList();
        assertThat(sorted).containsExactly(ConfigB.class, ConfigA.class, ConfigC.class);
    }

    @Test
    void shouldRegisterBeansFromConfiguration() {
        BeanRegistry registry = new BeanRegistry();
        AutoConfigurationProcessor processor = new AutoConfigurationProcessor(registry, getClass().getClassLoader());
        processor.registerBeansFromConfig(new DataSourceAutoConfiguration());
        assertThat(registry.containsBean(DataSource.class)).isTrue();
        assertThat(registry.containsBean(JdbcTemplate.class)).isTrue();
    }

    @Test
    void shouldExcludeConfiguration() {
        BeanRegistry registry = new BeanRegistry();
        List<Class<?>> configs = List.of(DataSourceAutoConfiguration.class, WebAutoConfiguration.class);
        @AutoConfiguration(exclude = "DataSourceAutoConfiguration")
        class App {}
        AutoConfigurationProcessor processor = new AutoConfigurationProcessor(registry, getClass().getClassLoader());
        processor.process(App.class, configs);
        assertThat(registry.containsBean(DataSource.class)).isFalse();
        assertThat(registry.containsBean(EmbeddedWebServer.class)).isTrue();
    }

    @Test
    void shouldHandleConditionalOnProperty() {
        System.setProperty("server.port", "8080");
        BeanRegistry registry = new BeanRegistry();
        ConditionalEvaluator evaluator = new ConditionalEvaluator(getClass().getClassLoader(), registry);
        assertThat(evaluator.matchesOnProperty(WebAutoConfiguration.class)).isTrue();
        System.clearProperty("server.port");
    }

    @Test
    void shouldStartApplicationContext() {
        try (SimpleApplicationContext ctx = SimpleSpringBoot.run(getClass())) {
            assertThat(ctx.getBeanDefinitionCount()).isPositive();
            assertThat(ctx.getAllBeans()).isNotEmpty();
        }
    }
}
```

---

## Summary

This simplified auto-configuration framework demonstrates the core concepts behind Spring Boot's auto-configuration:
- **Factories loading** via `META-INF/spring.factories`
- **Conditional evaluation** with `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`
- **Ordered application** of configuration classes
- **Bean registration** into a simple context
- **Exclusion support** via `@AutoConfiguration(exclude = ...)`

The full Spring Boot implementation is significantly more sophisticated, supporting meta-annotations, `AutoConfiguration.imports`, conditional phase evaluation, bean definition merging, and `@ConfigurationProperties` binding.