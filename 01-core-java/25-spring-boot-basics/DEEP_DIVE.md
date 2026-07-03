# Module 25: Spring Boot Basics - Deep Dive

**Difficulty Level**: Intermediate  
**Prerequisites**: Modules 01-24, Java 17+, Maven/Gradle  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [What is Spring Boot?](#whatis)
2. [Auto-Configuration](#autoconfig)
3. [Spring Boot Starters](#starters)
4. [Creating a REST API](#restapi)
5. [Application Properties](#properties)

---

## 1. What is Spring Boot? <a name="whatis"></a>
Spring Boot is an extension of the Spring framework that eliminates boilerplate configurations required for setting up a Spring application. It takes an opinionated view of the Spring platform to get developers up and running quickly.

---

## 2. Auto-Configuration <a name="autoconfig"></a>
Spring Boot auto-configuration attempts to automatically configure your Spring application based on the jar dependencies that you have added.

```java
@SpringBootApplication // Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## 3. Spring Boot Starters <a name="starters"></a>
Starters are a set of convenient dependency descriptors that you can include in your application. You get a one-stop-shop for all the Spring and related technology that you need without having to hunt through sample code and copy-paste loads of dependency descriptors.
- `spring-boot-starter-web`: Builds web, including RESTful, applications using Spring MVC. Uses Tomcat as the default embedded container.
- `spring-boot-starter-data-jpa`: Starter for using Spring Data JPA with Hibernate.

---

## 4. Creating a REST API <a name="restapi"></a>
```java
@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping
    public String sayHello() {
        return "Hello from Spring Boot!";
    }
    
    @PostMapping
    public String createGreeting(@RequestBody String name) {
        return "Hello " + name;
    }
}
```

---

## 5. Application Properties <a name="properties"></a>
Spring Boot externalizes configuration so you can work with the same application code in different environments. You can use properties files, YAML files, environment variables, and command-line arguments.

```properties
# application.properties
server.port=8081
spring.application.name=MySpringBootApp
```