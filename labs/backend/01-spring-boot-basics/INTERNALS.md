# Internals

## SpringApplication Class
```java
public class SpringApplication {
    private ConfigurableApplicationContext run(String... args) {
        Startup startup = Startup.create();
        // 1. Configure headless property
        // 2. Get ApplicationContextInitializers
        // 3. Get ApplicationListeners (from spring.factories)
        // 4. Identify main application class
        // 5. Create environment
        // 6. Create ApplicationContext
        // 7. Prepare context (load sources, register listeners)
        // 8. Refresh context (auto-configuration runs here)
        // 9. Call runners
        return context;
    }
}
```

## AutoConfigurationImportSelector
This class is key. It:
1. Reads `AutoConfiguration.imports` file
2. Filters via `@Conditional` annotations
3. Applies ordering via `@AutoConfigureOrder`, `@AutoConfigureBefore`, `@AutoConfigureAfter`

## EnvironmentPostProcessor
Custom processors can modify the environment before beans are created:
```java
public class CustomEnvironmentProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        // Modify environment before bean creation
    }
}
```

## FailureAnalysisReporter
Provides readable error messages when auto-configuration fails.
- "Description: Failed to configure a DataSource"
- "Action: Consider the following: If you want an embedded database, put H2 on the classpath"
