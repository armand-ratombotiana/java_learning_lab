# Auto-Configuration Code Deep Dive

This lab demonstrates how to write a custom Spring Boot Starter by creating an Auto-Configuration class that registers a bean conditionally.

## 💻 Pure Java Implementation

```java file="labs/spring-boot/01-auto-configuration/SOLUTION/CustomAutoConfigurationDemo.java"
package springboot.autoconfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * A demonstration of a custom Spring Boot Auto-Configuration class.
 * 
 * To make Spring Boot discover this class, you must create a file at:
 * src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 * 
 * And add this line to the file:
 * springboot.autoconfiguration.CustomAutoConfigurationDemo
 */
@AutoConfiguration
// Only run this configuration if the 'com.fasterxml.jackson.databind.ObjectMapper' class is on the classpath
@ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
// Only run this configuration if the user has set 'custom.feature.enabled=true' in application.yml
@ConditionalOnProperty(prefix = "custom.feature", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CustomAutoConfigurationDemo {

    public CustomAutoConfigurationDemo() {
        System.out.println("[AUTO-CONFIG] CustomAutoConfigurationDemo evaluated and loaded!");
    }

    /**
     * A simulated service that our starter provides.
     */
    public static class CustomNotificationService {
        public void send(String message) {
            System.out.println("[NotificationService] Sending: " + message);
        }
    }

    /**
     * The Auto-Configured Bean.
     */
    @Bean
    // MAGIC: Only create this default bean if the developer hasn't created their own!
    @ConditionalOnMissingBean(CustomNotificationService.class)
    public CustomNotificationService defaultNotificationService() {
        System.out.println("[AUTO-CONFIG] Creating Default CustomNotificationService Bean.");
        return new CustomNotificationService();
    }
}
```

## 🔍 Key Takeaways
1. **The `@AutoConfiguration` Annotation**: Introduced in Spring Boot 2.7, this replaces `@Configuration` for auto-configuration classes. It signals to the framework that this is an auto-config class and allows for strict ordering (e.g., `@AutoConfiguration(after = DataSourceAutoConfiguration.class)`).
2. **The Discovery File**: The code above will do absolutely nothing unless the fully qualified class name is registered in the `META-INF/spring/...AutoConfiguration.imports` file. Spring Boot will not component-scan for it.
3. **The Yielding Behavior**: Look at `@ConditionalOnMissingBean`. If a developer using your starter decides they want to send notifications via Kafka instead of your default implementation, they just write `@Bean public CustomNotificationService myKafkaService() { ... }` in their own code. When Spring Boot processes your auto-configuration, it sees their bean, the condition fails, and your default bean is gracefully skipped. This is the essence of Spring Boot's flexibility.