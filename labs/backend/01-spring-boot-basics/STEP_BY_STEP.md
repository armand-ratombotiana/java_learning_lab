# Step by Step

## Creating a Spring Boot Application

### Step 1: Generate the project
Use Spring Initializr (start.spring.io) or your IDE:
- Project: Maven
- Language: Java 17
- Dependencies: Spring Web, Spring Boot Actuator

### Step 2: Define the main class
```java
package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### Step 3: Create a REST controller
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
}
```

### Step 4: Configure application.properties
```properties
server.port=8080
spring.application.name=demo-app
```

### Step 5: Run the application
```bash
mvn spring-boot:run
# or java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Step 6: Verify
```bash
curl http://localhost:8080/hello
# Returns: Hello from Spring Boot!
curl http://localhost:8080/actuator/health
# Returns: {"status":"UP"}
```
