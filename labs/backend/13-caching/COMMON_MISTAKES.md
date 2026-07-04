# Common Mistakes with 13 Caching

## 1. Missing Dependencies
```xml
<!-- WRONG: Missing required starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 2. Wrong Configuration
```properties
# WRONG: Incorrect property prefix
com.example.custom.property=value

# CORRECT: Use the correct prefix
13caching.property=value
```

## 3. Ignoring Lifecycle
```java
// WRONG: Accessing before initialization
@Autowired
private Service service; // May not be initialized yet

// CORRECT: Use @PostConstruct
@PostConstruct
public void init() {
    // Safe to use service here
}
```

## 4. Not Handling Exceptions
```java
// WRONG: Swallowing exceptions
try { riskyOperation(); } catch (Exception e) { }

// CORRECT: Log and handle
try { riskyOperation(); }
catch (Exception e) { log.error("Failed", e); throw e; }
```

