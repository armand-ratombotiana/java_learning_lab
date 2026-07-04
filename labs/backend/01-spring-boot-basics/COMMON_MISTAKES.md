# Common Mistakes

## 1. Main Class Not in Root Package
```java
// ❌ Wrong - component scan won't find other packages
package com.example.demo.config;
@SpringBootApplication  // Scans only this package and subpackages
```

## 2. Forgetting @EnableConfigurationProperties
```java
// ❌ Properties class won't bind
@ConfigurationProperties(prefix = "app")
public class AppProperties {}

// ✅ Must be enabled
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Application {}
```

## 3. Multiple @SpringBootApplication Annotations
Only one per application. Use @Configuration for additional config classes.

## 4. Wrong Dependency Scope
```xml
<!-- ❌ Embedded server won't start -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

## 5. Ignoring the Banner
Not a mistake, but custom banners help identify deployments:
```properties
spring.banner.location=classpath:banner.txt
```

## 6. Using @Value Instead of @ConfigurationProperties
@Value is fine for simple values, but for structured config use typed properties.
