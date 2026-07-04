# Refactoring 04 Spring Data Jpa

## Extract Configuration
```java
// Before: Hardcoded values
@Service
public class MyService {
    private static final int TIMEOUT = 30;
}

// After: Externalized configuration
@Service
public class MyService {
    private final int timeout;
    public MyService(@Value("${04springdatajpa.timeout}") int timeout) {
        this.timeout = timeout;
    }
}
```

## Separate Concerns
```java
// Before: Mixed responsibilities
@Service
public class MyService {
    public void doWork() {
        // business logic + logging + error handling
    }
}

// After: Separated concerns
@Service
public class MyService {
    private final AuditService audit;
    public void doWork() {
        audit.log("Starting work");
        // business logic only
    }
}
```

## Use Profiles
```properties
# application-dev.properties
04springdatajpa.url=http://localhost:8080

# application-prod.properties
04springdatajpa.url=https://api.example.com
```

