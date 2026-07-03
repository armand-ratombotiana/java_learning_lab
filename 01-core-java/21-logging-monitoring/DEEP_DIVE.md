# Module 21: Logging & Monitoring - Deep Dive

**Difficulty Level**: Intermediate  
**Prerequisites**: Modules 01-20  
**Estimated Reading Time**: 45 minutes  

---

## 📚 Table of Contents

1. [Introduction to Logging in Java](#intro)
2. [Logging Frameworks (SLF4J & Logback)](#frameworks)
3. [Log Levels and Configuration](#levels)
4. [Introduction to Monitoring (JMX)](#monitoring)

---

## 1. Introduction to Logging in Java <a name="intro"></a>
Logging is essential for understanding application behavior, debugging issues, and auditing activities. Using `System.out.println` is discouraged in production applications because it cannot be easily configured, filtered, or routed to different outputs.

---

## 2. Logging Frameworks (SLF4J & Logback) <a name="frameworks"></a>
**SLF4J (Simple Logging Facade for Java)** acts as a facade or abstraction for various logging frameworks, allowing you to plug in the desired implementation at deployment time. **Logback** is the natively implemented successor to Log4j and is widely used.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyService {
    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    public void doWork() {
        logger.info("Starting work...");
        try {
            // ...
            logger.debug("Work in progress");
        } catch (Exception e) {
            logger.error("An error occurred during work", e);
        }
    }
}
```

---

## 3. Log Levels and Configuration <a name="levels"></a>
Standard log levels (from least to most severe):
- **TRACE**: Fine-grained informational events.
- **DEBUG**: Fine-grained informational events that are most useful to debug an application.
- **INFO**: Informational messages that highlight the progress of the application at coarse-grained level.
- **WARN**: Potentially harmful situations.
- **ERROR**: Error events that might still allow the application to continue running.

Configuration in `logback.xml`:
```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="info">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

---

## 4. Introduction to Monitoring (JMX) <a name="monitoring"></a>
Java Management Extensions (JMX) provides a standard architecture for managing and monitoring applications. It involves creating MBeans (Managed Beans) that expose attributes and operations to a JMX console (like JConsole).