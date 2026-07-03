# Module 69: Data Governance & Privacy - Mini Project

**Project Name**: Logback PII Scrubbing Filter  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Implement a custom log masking filter using Logback in a Spring Boot application to automatically detect and obfuscate Personally Identifiable Information (PII), such as Credit Card numbers and Social Security Numbers, before they are printed to the console or log files.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Spring Boot Web application.
   - It will use `logback` natively as its default logging framework.

2. **The Custom Layout / Filter**:
   - Create a Java class `PiiMaskingPatternLayout` that extends `ch.qos.logback.classic.PatternLayout`.
   - Override the `doLayout(ILoggingEvent event)` method.
   - Define Regular Expressions for:
     - Credit Cards (e.g., `\b(?:\d[ -]*?){13,16}\b`)
     - Social Security Numbers (e.g., `\b\d{3}-\d{2}-\d{4}\b`)
   - Inside `doLayout`, retrieve the original log message string. Use a `Matcher` to find PII patterns and replace the sensitive portions with asterisks (e.g., `XXXX-XXXX-XXXX-1234`), preserving the last 4 digits.
   - Return the masked string.

3. **Logback Configuration (`logback-spring.xml`)**:
   - Create a `logback-spring.xml` file in `src/main/resources`.
   - Configure a `ConsoleAppender`.
   - Set its layout to use your custom `PiiMaskingPatternLayout`.

4. **The REST Controller**:
   - Create a `TestController` mapped to `/api/test`.
   - Implement an endpoint that logs a string containing a fake credit card and SSN:
     `log.info("Processing payment for User 123-45-6789 with Card 4532-1234-5678-9012");`
   
5. **Execution**:
   - Run the application. Hit the endpoint.
   - Observe the console output. It must read: `Processing payment for User XXX-XX-6789 with Card XXXX-XXXX-XXXX-9012`. The raw PII should never appear in the console or file.

---

## 💡 Solution Blueprint

1. **The Masking Layout**:
   ```java
   import ch.qos.logback.classic.PatternLayout;
   import ch.qos.logback.classic.spi.ILoggingEvent;
   import java.util.regex.Matcher;
   import java.util.regex.Pattern;

   public class PiiMaskingPatternLayout extends PatternLayout {

       private Pattern creditCardPattern = Pattern.compile("\\b(?:\\d[ -]*?){13,16}\\b");
       private Pattern ssnPattern = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");

       @Override
       public String doLayout(ILoggingEvent event) {
           String message = super.doLayout(event);
           message = maskCard(message);
           message = maskSsn(message);
           return message;
       }

       private String maskCard(String message) {
           Matcher matcher = creditCardPattern.matcher(message);
           return matcher.replaceAll("XXXX-XXXX-XXXX-$1"); // Simplified for brevity
       }

       private String maskSsn(String message) {
           Matcher matcher = ssnPattern.matcher(message);
           if (matcher.find()) {
               String ssn = matcher.group();
               String masked = "XXX-XX-" + ssn.substring(ssn.length() - 4);
               return message.replace(ssn, masked);
           }
           return message;
       }
   }
   ```

2. **Logback XML**:
   ```xml
   <configuration>
       <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
           <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
               <layout class="com.example.logging.PiiMaskingPatternLayout">
                   <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
               </layout>
           </encoder>
       </appender>

       <root level="info">
           <appender-ref ref="CONSOLE" />
       </root>
   </configuration>
   ```