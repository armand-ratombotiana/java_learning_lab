# Java Master Lab - Phase 2 Final Labs Implementation Guide

## 📚 Complete Implementation Guide for Labs 21-25

**Purpose**: Detailed implementation guide for final Phase 2 labs  
**Target Audience**: Developers, instructors, learners  
**Focus**: Implementation, code examples, testing strategies  

---

## 🎯 PHASE 2 COMPLETION OVERVIEW

### Labs 21-25 Summary

```
Lab 21: Design Patterns (Creational)
├─ Duration: 5 hours
├─ Content: 4,500+ lines
├─ Examples: 150+ code examples
├─ Tests: 150+ unit tests
└─ Projects: 1 portfolio project

Lab 22: Design Patterns (Structural)
├─ Duration: 5 hours
├─ Content: 4,500+ lines
├─ Examples: 150+ code examples
├─ Tests: 150+ unit tests
└─ Projects: 1 portfolio project

Lab 23: Design Patterns (Behavioral)
├─ Duration: 5 hours
├─ Content: 4,500+ lines
├─ Examples: 150+ code examples
├─ Tests: 150+ unit tests
└─ Projects: 1 portfolio project

Lab 24: Regular Expressions
├─ Duration: 4 hours
├─ Content: 3,500+ lines
├─ Examples: 100+ code examples
├─ Tests: 100+ unit tests
└─ Projects: 1 portfolio project

Lab 25: Date & Time API
├─ Duration: 4 hours
├─ Content: 3,500+ lines
├─ Examples: 100+ code examples
├─ Tests: 100+ unit tests
└─ Projects: 1 portfolio project

TOTAL PHASE 2 COMPLETION
├─ Duration: 23 hours
├─ Content: 20,500+ lines
├─ Examples: 650+ code examples
├─ Tests: 650+ unit tests
└─ Projects: 5 portfolio projects
```

---

## 📖 LAB 21: DESIGN PATTERNS (CREATIONAL)

### Learning Objectives
- [ ] Understand creational design patterns
- [ ] Implement Singleton pattern
- [ ] Implement Factory pattern
- [ ] Implement Abstract Factory pattern
- [ ] Implement Builder pattern
- [ ] Implement Prototype pattern
- [ ] Apply patterns to real-world scenarios
- [ ] Write comprehensive tests

### Key Topics

#### 1. Singleton Pattern
```java
// Thread-safe Singleton
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    
    private DatabaseConnection() {
        // Private constructor
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
}
```

#### 2. Factory Pattern
```java
// Simple Factory
public class DatabaseFactory {
    public static Database createDatabase(String type) {
        switch (type) {
            case "mysql":
                return new MySQLDatabase();
            case "postgresql":
                return new PostgreSQLDatabase();
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }
}
```

#### 3. Builder Pattern
```java
// Builder for complex objects
public class UserBuilder {
    private String name;
    private String email;
    private int age;
    
    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public User build() {
        return new User(name, email, age);
    }
}
```

### Code Examples (150+)
- Singleton implementations
- Factory implementations
- Abstract Factory implementations
- Builder implementations
- Prototype implementations
- Real-world pattern applications
- Anti-patterns and pitfalls
- Performance considerations

### Unit Tests (150+)
- Singleton thread safety tests
- Factory creation tests
- Builder pattern tests
- Prototype cloning tests
- Pattern combination tests
- Performance tests
- Edge case tests

### Portfolio Project
**Project**: Design Pattern Library
- Implement all creational patterns
- Create reusable pattern implementations
- Build comprehensive documentation
- Write extensive test suite
- Create usage examples

---

## 📖 LAB 22: DESIGN PATTERNS (STRUCTURAL)

### Learning Objectives
- [ ] Understand structural design patterns
- [ ] Implement Adapter pattern
- [ ] Implement Bridge pattern
- [ ] Implement Composite pattern
- [ ] Implement Decorator pattern
- [ ] Implement Facade pattern
- [ ] Implement Flyweight pattern
- [ ] Implement Proxy pattern

### Key Topics

#### 1. Adapter Pattern
```java
// Adapter for incompatible interfaces
public class LegacyDatabaseAdapter implements Database {
    private LegacyDatabase legacyDb;
    
    public LegacyDatabaseAdapter(LegacyDatabase legacyDb) {
        this.legacyDb = legacyDb;
    }
    
    @Override
    public void connect() {
        legacyDb.openConnection();
    }
}
```

#### 2. Decorator Pattern
```java
// Decorator for adding functionality
public class EncryptedDataDecorator implements Data {
    private Data data;
    
    public EncryptedDataDecorator(Data data) {
        this.data = data;
    }
    
    @Override
    public String getData() {
        return encrypt(data.getData());
    }
}
```

#### 3. Facade Pattern
```java
// Facade for complex subsystems
public class DatabaseFacade {
    private Connection connection;
    private QueryBuilder queryBuilder;
    private ResultProcessor processor;
    
    public List<User> getUsers() {
        // Simplified interface to complex operations
        connection.connect();
        String query = queryBuilder.buildUserQuery();
        ResultSet results = connection.execute(query);
        return processor.process(results);
    }
}
```

### Code Examples (150+)
- Adapter implementations
- Bridge implementations
- Composite implementations
- Decorator implementations
- Facade implementations
- Flyweight implementations
- Proxy implementations
- Real-world applications

### Unit Tests (150+)
- Adapter compatibility tests
- Decorator stacking tests
- Composite tree tests
- Facade simplification tests
- Flyweight memory tests
- Proxy delegation tests
- Integration tests

### Portfolio Project
**Project**: Structural Pattern Framework
- Implement all structural patterns
- Create pattern combinations
- Build real-world applications
- Write comprehensive tests
- Create documentation

---

## 📖 LAB 23: DESIGN PATTERNS (BEHAVIORAL)

### Learning Objectives
- [ ] Understand behavioral design patterns
- [ ] Implement Chain of Responsibility pattern
- [ ] Implement Command pattern
- [ ] Implement Iterator pattern
- [ ] Implement Mediator pattern
- [ ] Implement Memento pattern
- [ ] Implement Observer pattern
- [ ] Implement State pattern
- [ ] Implement Strategy pattern
- [ ] Implement Template Method pattern
- [ ] Implement Visitor pattern

### Key Topics

#### 1. Observer Pattern
```java
// Observer for event handling
public class EventPublisher {
    private List<EventListener> listeners = new ArrayList<>();
    
    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }
    
    public void publish(Event event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}
```

#### 2. Strategy Pattern
```java
// Strategy for algorithm selection
public class PaymentProcessor {
    private PaymentStrategy strategy;
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void process(Payment payment) {
        strategy.pay(payment);
    }
}
```

#### 3. State Pattern
```java
// State for object behavior changes
public class OrderContext {
    private OrderState state;
    
    public void setState(OrderState state) {
        this.state = state;
    }
    
    public void process() {
        state.process(this);
    }
}
```

### Code Examples (150+)
- Chain of Responsibility implementations
- Command implementations
- Iterator implementations
- Mediator implementations
- Memento implementations
- Observer implementations
- State implementations
- Strategy implementations
- Template Method implementations
- Visitor implementations

### Unit Tests (150+)
- Observer notification tests
- Strategy switching tests
- State transition tests
- Command execution tests
- Iterator traversal tests
- Mediator communication tests
- Memento restoration tests
- Visitor traversal tests

### Portfolio Project
**Project**: Behavioral Pattern Application
- Implement all behavioral patterns
- Create complex pattern combinations
- Build real-world application
- Write comprehensive tests
- Create detailed documentation

---

## 📖 LAB 24: REGULAR EXPRESSIONS

### Learning Objectives
- [ ] Understand regex syntax
- [ ] Create regex patterns
- [ ] Use Pattern and Matcher classes
- [ ] Validate input with regex
- [ ] Extract data with regex
- [ ] Replace text with regex
- [ ] Handle special characters
- [ ] Optimize regex performance

### Key Topics

#### 1. Basic Patterns
```java
// Email validation
String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
Pattern pattern = Pattern.compile(emailPattern);
Matcher matcher = pattern.matcher("user@example.com");
boolean isValid = matcher.matches();

// Phone number validation
String phonePattern = "^\\d{3}-\\d{3}-\\d{4}$";
Pattern pattern = Pattern.compile(phonePattern);
Matcher matcher = pattern.matcher("123-456-7890");
boolean isValid = matcher.matches();
```

#### 2. Data Extraction
```java
// Extract data from text
String text = "Name: John, Age: 30, Email: john@example.com";
Pattern pattern = Pattern.compile("Name: (\\w+), Age: (\\d+), Email: ([\\w.]+@[\\w.]+)");
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    String name = matcher.group(1);
    int age = Integer.parseInt(matcher.group(2));
    String email = matcher.group(3);
}
```

#### 3. Text Replacement
```java
// Replace patterns
String text = "The price is $19.99";
String result = text.replaceAll("\\$(\\d+\\.\\d{2})", "USD $1");

// Multiple replacements
String text = "Hello   World";
String result = text.replaceAll("\\s+", " ");
```

### Code Examples (100+)
- Pattern matching examples
- Validation patterns
- Data extraction patterns
- Text replacement patterns
- Complex regex patterns
- Performance optimization
- Common patterns library
- Edge cases and pitfalls

### Unit Tests (100+)
- Pattern matching tests
- Validation tests
- Extraction tests
- Replacement tests
- Performance tests
- Edge case tests
- Unicode tests
- Multiline tests

### Portfolio Project
**Project**: Regex Utility Library
- Create reusable regex patterns
- Build validation utilities
- Create data extraction tools
- Write comprehensive tests
- Create pattern documentation

---

## 📖 LAB 25: DATE & TIME API

### Learning Objectives
- [ ] Understand Java 8+ Date/Time API
- [ ] Work with LocalDate, LocalTime, LocalDateTime
- [ ] Handle time zones with ZonedDateTime
- [ ] Calculate durations and periods
- [ ] Format and parse dates
- [ ] Handle date arithmetic
- [ ] Work with legacy Date classes
- [ ] Optimize date operations

### Key Topics

#### 1. LocalDate and LocalTime
```java
// Working with dates and times
LocalDate today = LocalDate.now();
LocalDate birthday = LocalDate.of(1990, 5, 15);
LocalTime now = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();

// Date arithmetic
LocalDate nextWeek = today.plusWeeks(1);
LocalDate lastMonth = today.minusMonths(1);
LocalTime later = now.plusHours(2);
```

#### 2. Time Zones
```java
// Working with time zones
ZonedDateTime utcTime = ZonedDateTime.now(ZoneId.of("UTC"));
ZonedDateTime estTime = ZonedDateTime.now(ZoneId.of("America/New_York"));

// Converting between zones
ZonedDateTime converted = utcTime.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
```

#### 3. Formatting and Parsing
```java
// Formatting dates
LocalDate date = LocalDate.now();
String formatted = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
String custom = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

// Parsing dates
LocalDate parsed = LocalDate.parse("2024-05-15");
LocalDate customParsed = LocalDate.parse("15/05/2024", 
    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
```

### Code Examples (100+)
- LocalDate examples
- LocalTime examples
- LocalDateTime examples
- ZonedDateTime examples
- Duration and Period examples
- Formatting examples
- Parsing examples
- Date arithmetic examples
- Legacy Date conversion examples
- Performance optimization examples

### Unit Tests (100+)
- Date creation tests
- Date arithmetic tests
- Time zone tests
- Formatting tests
- Parsing tests
- Duration tests
- Period tests
- Edge case tests
- Leap year tests
- DST tests

### Portfolio Project
**Project**: Date/Time Utility Library
- Create date/time utilities
- Build formatting utilities
- Create calculation utilities
- Write comprehensive tests
- Create documentation

---

## 🎯 PHASE 2 COMPLETION CHECKLIST

### Lab 21: Design Patterns (Creational)
- [ ] All 5 creational patterns implemented
- [ ] 150+ code examples created
- [ ] 150+ unit tests written
- [ ] Portfolio project completed
- [ ] Documentation complete
- [ ] Code review passed
- [ ] All tests passing (100%)
- [ ] Code coverage: 80%+

### Lab 22: Design Patterns (Structural)
- [ ] All 7 structural patterns implemented
- [ ] 150+ code examples created
- [ ] 150+ unit tests written
- [ ] Portfolio project completed
- [ ] Documentation complete
- [ ] Code review passed
- [ ] All tests passing (100%)
- [ ] Code coverage: 80%+

### Lab 23: Design Patterns (Behavioral)
- [ ] All 11 behavioral patterns implemented
- [ ] 150+ code examples created
- [ ] 150+ unit tests written
- [ ] Portfolio project completed
- [ ] Documentation complete
- [ ] Code review passed
- [ ] All tests passing (100%)
- [ ] Code coverage: 80%+

### Lab 24: Regular Expressions
- [ ] All regex concepts covered
- [ ] 100+ code examples created
- [ ] 100+ unit tests written
- [ ] Portfolio project completed
- [ ] Documentation complete
- [ ] Code review passed
- [ ] All tests passing (100%)
- [ ] Code coverage: 80%+

### Lab 25: Date & Time API
- [ ] All Date/Time concepts covered
- [ ] 100+ code examples created
- [ ] 100+ unit tests written
- [ ] Portfolio project completed
- [ ] Documentation complete
- [ ] Code review passed
- [ ] All tests passing (100%)
- [ ] Code coverage: 80%+

### Phase 2 Completion
- [ ] All 15 labs completed (100%)
- [ ] 61,500+ lines of code
- [ ] 1,300+ code examples
- [ ] 2,250+ unit tests
- [ ] 15 portfolio projects
- [ ] All documentation complete
- [ ] All code reviews passed
- [ ] All tests passing (100%)
- [ ] Code coverage: 80%+
- [ ] Phase 2 Certificate earned

---

## 📊 SUCCESS METRICS

### Code Quality
- Code coverage: 80%+
- Test pass rate: 100%
- Quality score: 80/100
- SOLID principles: Implemented
- Design patterns: Applied
- Error handling: Comprehensive
- Security: Best practices
- Performance: Optimized

### Documentation
- Code documentation: 100%
- API documentation: 100%
- User guides: Complete
- Developer guides: Complete
- Examples: Comprehensive
- Best practices: Documented
- Troubleshooting: Complete

### Learning Outcomes
- All concepts understood
- All patterns mastered
- All code examples working
- All tests passing
- All projects completed
- All documentation reviewed
- All skills verified
- Certificate earned

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Phase 2 Final Labs Implementation |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Implementation |

---

**Java Master Lab - Phase 2 Final Labs Implementation Guide**

*Complete Guide to Completing Phase 2*

**Status: Ready | Focus: Implementation | Impact: Completion**

---

*Complete Phase 2 with confidence!* 🚀