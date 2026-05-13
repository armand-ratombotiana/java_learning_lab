# Spring Core Module - PROJECTS.md

---

# Mini-Projects Overview

| Concept | Duration | Description |
|---------|----------|-------------|
| Bean Definition | 2 hours | @Bean, @Component, Java config |
| Dependency Injection | 2 hours | Constructor, setter, field injection |
| Bean Scopes | 2 hours | Singleton, prototype, custom scopes |
| AOP Basics | 2 hours | Aspects, pointcuts, advice, proxies |
| Real-world: Custom Spring Framework | 8+ hours | Advanced DI with post-processors, events |

---

# Mini-Project: Spring DI Container Demo

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Dependency Injection, Bean Configuration, Bean Scopes, Spring Container, Component Scanning

This project demonstrates Spring's core dependency injection functionality without Spring Boot.

---

## Project Structure

```
14-spring-core/src/main/java/com/learning/
├── Main.java
├── config/
│   ├── AppConfig.java
│   └── BeanConfig.java
├── service/
│   ├── MessageService.java
│   ├── EmailService.java
│   ├── NotificationService.java
│   └── UserService.java
├── repository/
│   ├── UserRepository.java
│   └── InMemoryUserRepository.java
├── model/
│   └── User.java
└── controller/
    └── UserController.java
```

---

## Step 1: Maven POM

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>spring-core-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring.version>6.1.3</spring.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
            <version>2.1.1</version>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model Classes

```java
// model/User.java
package com.learning.model;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    
    public User() {}
    
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
```

---

## Step 3: Service Layer

```java
// service/MessageService.java
package com.learning.service;

public interface MessageService {
    String sendMessage(String message);
}

public class EmailMessageService implements MessageService {
    @Override
    public String sendMessage(String message) {
        return "Email sent: " + message;
    }
}

public class SmsMessageService implements MessageService {
    @Override
    public String sendMessage(String message) {
        return "SMS sent: " + message;
    }
}
```

```java
// service/NotificationService.java
package com.learning.service;

public class NotificationService {
    private final MessageService messageService;
    private final UserService userService;
    
    public NotificationService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }
    
    public void notifyUser(Long userId, String message) {
        var user = userService.findById(userId);
        if (user != null) {
            String result = messageService.sendMessage(
                "To: " + user.getEmail() + " - " + message);
            System.out.println(result);
        }
    }
    
    public void notifyAllUsers(String message) {
        var users = userService.findAll();
        for (var user : users) {
            String result = messageService.sendMessage(
                "To: " + user.getEmail() + " - " + message);
            System.out.println(result);
        }
    }
}
```

```java
// service/UserService.java
package com.learning.service;

import com.learning.model.User;
import repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
    
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
```

---

## Step 4: Repository Layer

```java
// repository/UserRepository.java
package com.learning.repository;

import com.learning.model.User;
import java.util.List;

public interface UserRepository {
    User save(User user);
    User findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
}
```

```java
// repository/InMemoryUserRepository.java
package com.learning.repository;

import com.learning.model.User;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public User findById(Long id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
}
```

---

## Step 5: Java Configuration

```java
// config/AppConfig.java
package com.learning.config;

import com.learning.service.*;
import com.learning.repository.*;
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {
    
    @Bean
    public MessageService messageService() {
        return new EmailMessageService();
    }
    
    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }
    
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
    
    @Bean
    public NotificationService notificationService(
            MessageService messageService,
            UserService userService) {
        return new NotificationService(messageService, userService);
    }
}
```

```java
// config/BeanConfig.java
package com.learning.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import com.learning.service.*;

@Configuration
@ComponentScan(basePackages = "com.learning")
public class BeanConfig {
    
    @Bean
    @Scope("singleton")
    public MessageService smsService() {
        return new SmsMessageService();
    }
}
```

---

## Step 6: Component Scanning

```java
// service/UserService.java (with annotations)
package com.learning.service;

import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
    
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}
```

```java
// repository/InMemoryUserRepository.java
package com.learning.repository;

import com.learning.model.User;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public User findById(Long id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new java.util.ArrayList<>(users.values());
    }
    
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
}
```

---

## Step 7: Main Application

```java
// Main.java
package com.learning;

import com.learning.config.*;
import com.learning.model.*;
import com.learning.service.*;
import org.springframework.context.annotation.*;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        testBeanCreation(context);
        testScopes(context);
        testDependencyInjection(context);
        
        context.close();
    }
    
    private static void testBeanCreation(AnnotationConfigApplicationContext context) {
        System.out.println("=== Bean Creation ===");
        
        UserService userService = context.getBean(UserService.class);
        
        User user1 = new User("John Doe", "john@example.com", "password123");
        userService.save(user1);
        
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        userService.save(user2);
        
        System.out.println("Users: " + userService.findAll());
    }
    
    private static void testScopes(AnnotationConfigApplicationContext context) {
        System.out.println("\n=== Bean Scopes ===");
        
        var userService1 = context.getBean(UserService.class);
        var userService2 = context.getBean(UserService.class);
        
        System.out.println("Same bean? " + (userService1 == userService2));
    }
    
    private static void testDependencyInjection(AnnotationConfigApplicationContext context) {
        System.out.println("\n=== Dependency Injection ===");
        
        NotificationService notificationService = 
            context.getBean(NotificationService.class);
        
        notificationService.notifyUser(1L, "Welcome to Spring!");
    }
}
```

---

# Real-World Project: Full Spring DI Container

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Advanced Spring DI, Bean Post-Processors, Factory Beans, Custom Scopes, Profiles, Events, AOP Integration

This project implements a complete dependency injection container with advanced Spring features.

---

## Project Structure

```
14-spring-core/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── AdvancedConfig.java
│   │   ├── ProfilesConfig.java
│   │   └── CustomScopeConfig.java
│   ├── service/
│   │   ├── OrderService.java
│   │   ├── ProductService.java
│   │   └── AdvancedUserService.java
│   ├── processor/
│   │   ├── CustomBeanFactoryPostProcessor.java
│   │   └── CustomBeanPostProcessor.java
│   ├── scope/
│   │   └── ThreadScope.java
│   └── events/
│       ├── AppEvent.java
│       └── EventListener.java
└── src/main/resources/
    └── application.properties
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>spring-core-advanced</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring.version>6.1.3</spring.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
            <version>2.1.1</version>
        </dependency>
    </dependencies>
</project>
```

---

## Advanced Configuration

```java
// config/ProfilesConfig.java
package com.learning.config;

import org.springframework.context.annotation.*;
import com.learning.service.*;

@Configuration
@Profile("development")
public class ProfilesConfig {
    
    @Bean
    public MessageService devMessageService() {
        return () -> "Dev message: " + System.getenv("USER");
    }
}

@Configuration
@Profile("production")
public class ProductionConfig {
    
    @Bean
    public MessageService prodMessageService() {
        return () -> "Production message";
    }
}

@Configuration
@Profile("!test")
public class DefaultConfig {
    
    @Bean
    public MessageService defaultMessageService() {
        return message -> "Default: " + message;
    }
}
```

---

## Custom Bean Post-Processor

```java
// processor/CustomBeanPostProcessor.java
package com.learning.processor;

import org.springframework.beans.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.*;

public class CustomBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) 
            throws BeansException {
        System.out.println("Before initialization: " + beanName);
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) 
            throws BeansException {
        System.out.println("After initialization: " + beanName);
        
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                throw new BeansException("Error initializing bean", e) {};
            }
        }
        
        return bean;
    }
}
```

---

## Custom Scope

```java
// scope/ThreadScope.java
package com.learning.scope;

import org.springframework.beans.factory.config.*;
import java.util.HashMap;
import java.util.Map;

public class ThreadScope implements Scope {
    private final ThreadLocal<Map<String, Object>> threadScope = 
        ThreadLocal.withInitial(HashMap::new);
    
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> scope = threadScope.get();
        Object object = scope.get(name);
        
        if (object == null) {
            object = objectFactory.getObject();
            scope.put(name, object);
        }
        
        return object;
    }
    
    @Override
    public Object remove(String name) {
        return threadScope.get().remove(name);
    }
    
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
    }
    
    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
}
```

```java
// config/CustomScopeConfig.java
package com.learning.config;

import com.learning.scope.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.*;

@Configuration
public class CustomScopeConfig {
    
    @Bean
    public Scope threadScope() {
        return new ThreadScope();
    }
    
    @Bean
    public CustomBeanFactoryPostProcessor customBeanFactoryPostProcessor() {
        return new CustomBeanFactoryPostProcessor();
    }
}
```

---

## Custom Bean Factory Post-Processor

```java
// processor/CustomBeanFactoryPostProcessor.java
package com.learning.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;

public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) 
            throws BeansException {
        System.out.println("CustomBeanFactoryPostProcessor.postProcessBeanFactory");
        
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = 
                beanFactory.getBeanDefinition(beanName);
            String beanClassName = beanDefinition.getBeanClassName();
            
            if (beanClassName != null) {
                System.out.println("Found bean: " + beanName + 
                    " of class: " + beanClassName);
            }
        }
    }
}
```

---

## Event System

```java
// events/AppEvent.java
package com.learning.events;

import org.springframework.context.ApplicationEvent;

public class AppEvent extends ApplicationEvent {
    private final String message;
    
    public AppEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
```

```java
// events/EventListener.java
package com.learning.events;

import org.springframework.context.event.*;
import org.springframework.stereotype.Component;

@Component
public class EventListener {
    
    @EventListener
    public void handleAppEvent(AppEvent event) {
        System.out.println("Event received: " + event.getMessage());
    }
}
```

---

## Advanced Services

```java
// service/AdvancedUserService.java
package com.learning.service;

import jakarta.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import com.learning.model.User;

public class AdvancedUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.name:default}")
    private String appName;
    
    @Value("${app.version:1.0}")
    private String appVersion;
    
    @PostConstruct
    public void init() {
        System.out.println("AdvancedUserService initialized");
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("AdvancedUserService cleanup");
    }
    
    public User createUser(String name, String email) {
        User user = new User(name, email, "default");
        return userRepository.save(user);
    }
    
    @Profile("!test")
    public void testOnlyInNonTest() {
        System.out.println("This runs in non-test profiles only");
    }
}

interface MessageService {
    String send(String message);
}
```

---

## Conditionals

```java
// config/ConditionalConfig.java
package com.learning.config;

import org.springframework.context.annotation.*;
import com.learning.service.*;

@Configuration
public class ConditionalConfig {
    
    @Bean
    @Conditional(OperatingSystemCondition.class)
    public MessageService windowsMessageService() {
        return message -> "Windows: " + message;
    }
    
    @Bean
    @Conditional(OperatingSystemCondition.class)
    public MessageService linuxMessageService() {
        return message -> "Linux: " + message;
    }
}

class OperatingSystemCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, 
                          AnnotatedTypeMetadata metadata) {
        String os = System.getProperty("os.name");
        return os.toLowerCase().contains("windows") || 
               os.toLowerCase().contains("linux");
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.config.*;
import com.learning.model.*;
import com.learning.service.*;
import com.learning.processor.*;
import org.springframework.context.annotation.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        demonstrateProfiles();
        demonstrateScopes();
        demonstrateEvents();
    }
    
    private static void demonstrateProfiles() {
        System.out.println("=== Profile-based Configuration ===");
        
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("development");
        context.register(AppConfig.class, ProfilesConfig.class);
        context.refresh();
        
        MessageService service = context.getBean(MessageService.class);
        System.out.println("Service: " + service.send("Hello"));
        
        context.close();
    }
    
    private static void demonstrateScopes() {
        System.out.println("\n=== Custom Scopes ===");
        
        var context = new AnnotationConfigApplicationContext(
            AppConfig.class, CustomScopeConfig.class);
        
        System.out.println("Beans loaded successfully");
        
        context.close();
    }
    
    private static void demonstrateEvents() {
        System.out.println("\n=== Application Events ===");
        
        var context = new AnnotationConfigApplicationContext(
            AppConfig.class, EventListener.class);
        
        context.publishEvent(new AppEvent(this, "Test Event"));
        
        context.close();
    }
}
```

---

## Build Instructions

```bash
cd 14-spring-core
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

This project demonstrates advanced Spring DI container features including profiles, custom scopes, bean post-processors, events, and conditional bean creation.