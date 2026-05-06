# Java Master Lab - Best Practices Guide

This guide provides comprehensive best practices for Java development, covering code style, design patterns, performance optimization, and professional development standards.

## 📚 Table of Contents

1. [Code Style & Conventions](#code-style--conventions)
2. [Object-Oriented Design](#object-oriented-design)
3. [Error Handling](#error-handling)
4. [Testing](#testing)
5. [Performance Optimization](#performance-optimization)
6. [Security](#security)
7. [Documentation](#documentation)
8. [Version Control](#version-control)

---

## Code Style & Conventions

### Naming Conventions

Follow these naming conventions consistently:

```java
// Classes: PascalCase
public class StudentManagementSystem { }
public class PaymentProcessor { }

// Interfaces: PascalCase (often with "able" suffix)
public interface Serializable { }
public interface Comparable { }

// Methods: camelCase (usually verbs)
public void calculateGPA() { }
public String getStudentName() { }
public boolean isActive() { }

// Variables: camelCase
private String firstName;
private int studentAge;
private double gpa;

// Constants: UPPER_SNAKE_CASE
public static final int MAX_STUDENTS = 100;
public static final double PI = 3.14159;
public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/db";

// Enum constants: UPPER_SNAKE_CASE
public enum Status {
    ACTIVE,
    INACTIVE,
    PENDING
}
```

### Code Formatting

```java
// Use 4 spaces for indentation (not tabs)
public class Example {
    public void method() {
        if (condition) {
            // Code
        }
    }
}

// Opening brace on same line
public void method() {
    // Code
}

// One statement per line
int a = 1;
int b = 2;
int c = 3;

// Line length: max 120 characters
String longString = "This is a very long string that should be broken " +
                    "into multiple lines for readability";

// Spacing around operators
int result = a + b * c;
if (x > 0 && y < 10) {
    // Code
}
```

### Import Organization

```java
// 1. Standard library imports
import java.util.*;
import java.io.*;

// 2. Third-party imports
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

// 3. Project imports
import com.learning.util.*;
import com.learning.model.*;

// Avoid wildcard imports in production code
// Use specific imports instead
import java.util.List;
import java.util.ArrayList;
```

---

## Object-Oriented Design

### SOLID Principles

#### Single Responsibility Principle (SRP)

Each class should have one reason to change:

```java
// ❌ Bad: Multiple responsibilities
public class Student {
    private String name;
    private double gpa;
    
    public void calculateGPA() { }
    public void saveToDatabase() { }
    public void sendEmail() { }
}

// ✅ Good: Single responsibility
public class Student {
    private String name;
    private double gpa;
    
    public void calculateGPA() { }
}

public class StudentRepository {
    public void save(Student student) { }
}

public class EmailService {
    public void sendEmail(String to, String message) { }
}
```

#### Open/Closed Principle (OCP)

Classes should be open for extension, closed for modification:

```java
// ❌ Bad: Requires modification for new types
public class PaymentProcessor {
    public void process(String type, double amount) {
        if (type.equals("CREDIT_CARD")) {
            // Process credit card
        } else if (type.equals("PAYPAL")) {
            // Process PayPal
        }
    }
}

// ✅ Good: Open for extension
public interface PaymentMethod {
    void process(double amount);
}

public class CreditCardPayment implements PaymentMethod {
    @Override
    public void process(double amount) {
        // Process credit card
    }
}

public class PayPalPayment implements PaymentMethod {
    @Override
    public void process(double amount) {
        // Process PayPal
    }
}

public class PaymentProcessor {
    public void process(PaymentMethod method, double amount) {
        method.process(amount);
    }
}
```

#### Liskov Substitution Principle (LSP)

Subtypes must be substitutable for their base types:

```java
// ❌ Bad: Violates LSP
public class Bird {
    public void fly() { }
}

public class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Penguins cannot fly");
    }
}

// ✅ Good: Proper hierarchy
public interface Animal { }

public interface Flyable {
    void fly();
}

public class Bird implements Animal, Flyable {
    @Override
    public void fly() { }
}

public class Penguin implements Animal {
    // No fly method
}
```

#### Interface Segregation Principle (ISP)

Clients should not depend on interfaces they don't use:

```java
// ❌ Bad: Fat interface
public interface Worker {
    void work();
    void eat();
    void sleep();
}

// ✅ Good: Segregated interfaces
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class Human implements Workable, Eatable, Sleepable {
    @Override
    public void work() { }
    
    @Override
    public void eat() { }
    
    @Override
    public void sleep() { }
}

public class Robot implements Workable {
    @Override
    public void work() { }
}
```

#### Dependency Inversion Principle (DIP)

Depend on abstractions, not concrete implementations:

```java
// ❌ Bad: Depends on concrete class
public class UserService {
    private MySQLDatabase database = new MySQLDatabase();
    
    public void saveUser(User user) {
        database.save(user);
    }
}

// ✅ Good: Depends on abstraction
public interface Database {
    void save(User user);
}

public class UserService {
    private Database database;
    
    public UserService(Database database) {
        this.database = database;
    }
    
    public void saveUser(User user) {
        database.save(user);
    }
}
```

### Design Patterns

#### Singleton Pattern

```java
// Thread-safe singleton
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() { }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}

// Better: Eager initialization
public class DatabaseConnection {
    private static final DatabaseConnection instance = new DatabaseConnection();
    
    private DatabaseConnection() { }
    
    public static DatabaseConnection getInstance() {
        return instance;
    }
}
```

#### Factory Pattern

```java
public interface PaymentMethod {
    void pay(double amount);
}

public class PaymentFactory {
    public static PaymentMethod createPayment(String type) {
        return switch (type) {
            case "CREDIT_CARD" -> new CreditCardPayment();
            case "PAYPAL" -> new PayPalPayment();
            case "BITCOIN" -> new BitcoinPayment();
            default -> throw new IllegalArgumentException("Unknown payment type");
        };
    }
}
```

#### Builder Pattern

```java
public class Student {
    private final String name;
    private final int age;
    private final String email;
    private final double gpa;
    
    private Student(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
        this.gpa = builder.gpa;
    }
    
    public static class Builder {
        private String name;
        private int age;
        private String email;
        private double gpa;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder age(int age) {
            this.age = age;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder gpa(double gpa) {
            this.gpa = gpa;
            return this;
        }
        
        public Student build() {
            return new Student(this);
        }
    }
}

// Usage
Student student = new Student.Builder()
    .name("John Doe")
    .age(25)
    .email("john@example.com")
    .gpa(3.8)
    .build();
```

---

## Error Handling

### Exception Handling Best Practices

```java
// ❌ Bad: Catching generic Exception
try {
    // Code
} catch (Exception e) {
    e.printStackTrace();
}

// ✅ Good: Catch specific exceptions
try {
    // Code
} catch (FileNotFoundException e) {
    logger.error("File not found: " + e.getMessage(), e);
    // Handle file not found
} catch (IOException e) {
    logger.error("IO error: " + e.getMessage(), e);
    // Handle IO error
}

// ✅ Good: Use try-with-resources
try (Scanner scanner = new Scanner(System.in)) {
    String input = scanner.nextLine();
    // Use input
} catch (IOException e) {
    logger.error("Error reading input", e);
}

// ✅ Good: Custom exceptions
public class InvalidStudentException extends Exception {
    public InvalidStudentException(String message) {
        super(message);
    }
    
    public InvalidStudentException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Usage
if (age < 0) {
    throw new InvalidStudentException("Age cannot be negative");
}
```

### Null Handling

```java
// ❌ Bad: Null checks everywhere
if (student != null) {
    if (student.getGPA() != null) {
        // Use GPA
    }
}

// ✅ Good: Use Optional
Optional<Student> student = findStudent(id);
student.ifPresent(s -> {
    double gpa = s.getGPA();
    // Use GPA
});

// ✅ Good: Use Objects.requireNonNull
public Student(String name, double gpa) {
    this.name = Objects.requireNonNull(name, "Name cannot be null");
    this.gpa = gpa;
}
```

---

## Testing

### Unit Testing Best Practices

```java
// Test class naming: [ClassName]Test
public class StudentTest {
    
    private Student student;
    
    @BeforeEach
    void setUp() {
        student = new Student("John Doe", 25, "john@example.com");
    }
    
    // Test method naming: test[Scenario][Expected]
    @Test
    void testCalculateGPA_WithValidGrades_ReturnsCorrectGPA() {
        // Arrange
        double[] grades = {4.0, 3.5, 3.8};
        
        // Act
        double gpa = student.calculateGPA(grades);
        
        // Assert
        assertEquals(3.77, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPA_WithEmptyGrades_ThrowsException() {
        // Arrange
        double[] grades = {};
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            student.calculateGPA(grades);
        });
    }
    
    @Test
    void testGetName_ReturnsCorrectName() {
        // Act
        String name = student.getName();
        
        // Assert
        assertEquals("John Doe", name);
    }
}
```

### Mocking Best Practices

```java
public class StudentServiceTest {
    
    @Mock
    private StudentRepository repository;
    
    @InjectMocks
    private StudentService service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testSaveStudent_CallsRepository() {
        // Arrange
        Student student = new Student("John", 25, "john@example.com");
        when(repository.save(student)).thenReturn(true);
        
        // Act
        boolean result = service.saveStudent(student);
        
        // Assert
        assertTrue(result);
        verify(repository, times(1)).save(student);
    }
}
```

---

## Performance Optimization

### String Operations

```java
// ❌ Bad: String concatenation in loop
String result = "";
for (int i = 0; i < 1000; i++) {
    result += "Item " + i + "\n";  // Creates new String each iteration
}

// ✅ Good: Use StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append("Item ").append(i).append("\n");
}
String result = sb.toString();
```

### Collection Operations

```java
// ❌ Bad: Inefficient list operations
List<String> list = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    list.add(0, "Item " + i);  // O(n) operation
}

// ✅ Good: Use appropriate data structure
LinkedList<String> list = new LinkedList<>();
for (int i = 0; i < 10000; i++) {
    list.addFirst("Item " + i);  // O(1) operation
}

// ✅ Good: Use streams for filtering
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
```

### Caching

```java
public class StudentService {
    private final Map<Integer, Student> cache = new HashMap<>();
    
    public Student getStudent(int id) {
        return cache.computeIfAbsent(id, k -> fetchFromDatabase(id));
    }
    
    private Student fetchFromDatabase(int id) {
        // Database query
        return new Student("John", 25, "john@example.com");
    }
}
```

---

## Security

### Input Validation

```java
// ✅ Good: Validate all inputs
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Invalid age: " + age);
    }
    this.age = age;
}

public void setEmail(String email) {
    if (email == null || !email.contains("@")) {
        throw new IllegalArgumentException("Invalid email: " + email);
    }
    this.email = email;
}
```

### SQL Injection Prevention

```java
// ❌ Bad: SQL injection vulnerability
String query = "SELECT * FROM students WHERE name = '" + name + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(query);

// ✅ Good: Use prepared statements
String query = "SELECT * FROM students WHERE name = ?";
PreparedStatement pstmt = connection.prepareStatement(query);
pstmt.setString(1, name);
ResultSet rs = pstmt.executeQuery();
```

### Sensitive Data

```java
// ✅ Good: Don't log sensitive data
logger.info("User logged in: " + username);  // OK
logger.info("Password: " + password);        // ❌ Never log passwords

// ✅ Good: Clear sensitive data
char[] password = new char[100];
// Use password
Arrays.fill(password, ' ');  // Clear from memory
```

---

## Documentation

### JavaDoc Comments

```java
/**
 * Calculates the GPA for a student based on their grades.
 *
 * <p>The GPA is calculated as the average of all grades.
 * Grades should be between 0.0 and 4.0.</p>
 *
 * @param grades array of grade values (0.0 to 4.0)
 * @return the calculated GPA
 * @throws IllegalArgumentException if grades array is empty or contains invalid values
 * @throws NullPointerException if grades array is null
 *
 * @example
 * double[] grades = {4.0, 3.5, 3.8};
 * double gpa = student.calculateGPA(grades);  // Returns 3.77
 */
public double calculateGPA(double[] grades) {
    if (grades == null || grades.length == 0) {
        throw new IllegalArgumentException("Grades array cannot be null or empty");
    }
    
    double sum = 0;
    for (double grade : grades) {
        if (grade < 0 || grade > 4.0) {
            throw new IllegalArgumentException("Grade must be between 0.0 and 4.0");
        }
        sum += grade;
    }
    
    return sum / grades.length;
}
```

### Code Comments

```java
// ✅ Good: Explain the "why"
// We use LinkedList here because we frequently insert at the beginning
LinkedList<String> list = new LinkedList<>();

// ❌ Bad: Explain the "what" (obvious from code)
// Add 1 to i
i++;

// ✅ Good: Explain complex logic
// Use exponential backoff to avoid overwhelming the server
// Start with 1 second, double each retry, max 60 seconds
long delay = Math.min(1000 * (long) Math.pow(2, retryCount), 60000);
```

---

## Version Control

### Git Best Practices

```bash
# ✅ Good: Descriptive commit messages
git commit -m "feat: add student GPA calculation"
git commit -m "fix: correct age validation logic"
git commit -m "docs: update API documentation"

# ❌ Bad: Vague commit messages
git commit -m "fix stuff"
git commit -m "update"
git commit -m "changes"

# ✅ Good: Atomic commits
# One logical change per commit

# ✅ Good: Use branches for features
git checkout -b feature/student-management
git checkout -b bugfix/age-validation
```

### .gitignore

```
# IDE
.idea/
.vscode/
*.swp
*.swo

# Build
target/
build/
*.class
*.jar

# Dependencies
node_modules/
.m2/

# OS
.DS_Store
Thumbs.db

# Logs
*.log
logs/

# Sensitive
.env
secrets.properties
```

---

## Summary

Key takeaways:

1. **Follow conventions**: Consistent naming and formatting
2. **Apply SOLID principles**: Write maintainable, extensible code
3. **Handle errors properly**: Specific exceptions, proper logging
4. **Write tests**: Unit tests, integration tests, mocking
5. **Optimize performance**: Use appropriate data structures, avoid unnecessary operations
6. **Secure your code**: Validate input, prevent injection attacks
7. **Document well**: JavaDoc, meaningful comments
8. **Use version control**: Atomic commits, descriptive messages

---

**Remember**: Good code is not just code that works, it's code that is readable, maintainable, and secure.