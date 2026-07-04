# Spring Boot Basics

Spring Boot simplifies Spring application development by providing auto-configuration, embedded servers, and opinionated defaults.

## Topics Covered
- Auto-configuration and @EnableAutoConfiguration
- Spring Boot Starters
- Application Properties and YAML configuration
- Embedded Tomcat/Jetty/Undertow
- Spring Boot Actuator
- Custom banners and logging

## Prerequisites
- Java 17+
- Maven or Gradle
- Basic Spring Framework knowledge

## Getting Started
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
