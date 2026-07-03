# Java Master Lab - Phase 2B Advanced Implementation Guide

## 🚀 Phase 2B Advanced Java Completion - Detailed Implementation Guide

**Phase**: Phase 2B (Advanced Java Completion)  
**Labs**: 21-25 (Design Patterns, Regular Expressions, Date-Time API)  
**Duration**: 3 weeks (Weeks 6-8)  
**Content**: 20,500+ lines of code  
**Tests**: 650+ unit tests  
**Projects**: 5 portfolio projects  

---

## 📋 PHASE 2B OVERVIEW

### Phase Objectives

```
✅ Master design patterns (creational, structural, behavioral)
✅ Master regular expressions and pattern matching
✅ Master date-time API and temporal operations
✅ Implement advanced Java concepts
✅ Build portfolio projects
✅ Achieve 80%+ code coverage
✅ Maintain 100% test pass rate
✅ Complete on schedule (Week 8)
```

### Phase Structure

```
WEEK 6: Design Patterns (Labs 21-23)
├─ Lab 21: Creational Design Patterns
├─ Lab 22: Structural Design Patterns
├─ Lab 23: Behavioral Design Patterns
└─ Status: IN PROGRESS

WEEK 7: Regular Expressions & Utilities (Labs 24)
├─ Lab 24: Regular Expressions
└─ Status: PLANNED

WEEK 8: Date-Time API (Lab 25)
├─ Lab 25: Date-Time API
└─ Status: PLANNED
```

---

## 🏗️ LAB 21: CREATIONAL DESIGN PATTERNS

### Lab Objectives

```
✅ Understand creational design patterns
✅ Implement Singleton pattern
✅ Implement Factory pattern
✅ Implement Builder pattern
✅ Implement Prototype pattern
✅ Implement Abstract Factory pattern
✅ Write comprehensive tests
✅ Create portfolio project
```

### Implementation Structure

```
SINGLETON PATTERN:
├─ Eager initialization
├─ Lazy initialization
├─ Thread-safe singleton
├─ Enum singleton
├─ Double-checked locking
└─ Bill Pugh singleton

FACTORY PATTERN:
├─ Simple factory
├─ Factory method
├─ Abstract factory
├─ Factory with reflection
├─ Factory with configuration
└─ Factory with dependency injection

BUILDER PATTERN:
├─ Basic builder
├─ Fluent builder
├─ Immutable builder
├─ Builder with validation
├─ Builder with defaults
└─ Builder with optional parameters

PROTOTYPE PATTERN:
├─ Shallow copy
├─ Deep copy
├─ Cloneable interface
├─ Copy constructor
├─ Prototype registry
└─ Prototype with serialization

ABSTRACT FACTORY PATTERN:
├─ Family of objects
├─ Concrete factories
├─ Abstract factory interface
├─ Factory selection
├─ Configuration-based factory
└─ Plugin-based factory
```

### Code Examples

```java
// Singleton Pattern - Thread-Safe
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

// Builder Pattern - Fluent API
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
    
    public UserBuilder withAge(int age) {
        this.age = age;
        return this;
    }
    
    public User build() {
        return new User(name, email, age);
    }
}

// Factory Pattern
public interface ShapeFactory {
    Shape createShape();
}

public class CircleFactory implements ShapeFactory {
    @Override
    public Shape createShape() {
        return new Circle();
    }
}

// Abstract Factory Pattern
public interface UIFactory {
    Button createButton();
    TextField createTextField();
}

public class WindowsUIFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }
    
    @Override
    public TextField createTextField() {
        return new WindowsTextField();
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Singleton thread safety tests
├─ Factory creation tests
├─ Builder fluent API tests
├─ Prototype cloning tests
├─ Abstract factory tests
└─ Pattern correctness tests

INTEGRATION TESTS:
├─ Pattern interaction tests
├─ Configuration-based factory tests
├─ Dependency injection tests
└─ Real-world scenario tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

### Portfolio Project

```
PROJECT: Design Pattern Framework
├─ Objective: Build reusable design pattern framework
├─ Components:
│  ├─ Pattern implementations
│  ├─ Pattern registry
│  ├─ Pattern factory
│  ├─ Configuration system
│  └─ Example applications
├─ Features:
│  ├─ Multiple pattern implementations
│  ├─ Easy pattern selection
│  ├─ Configuration-driven patterns
│  ├─ Comprehensive documentation
│  └─ Real-world examples
└─ Deliverables:
   ├─ Source code (4,000+ lines)
   ├─ Unit tests (400+ tests)
   ├─ Documentation
   └─ Examples
```

---

## 🏗️ LAB 22: STRUCTURAL DESIGN PATTERNS

### Lab Objectives

```
✅ Understand structural design patterns
✅ Implement Adapter pattern
✅ Implement Bridge pattern
✅ Implement Composite pattern
✅ Implement Decorator pattern
✅ Implement Facade pattern
✅ Implement Flyweight pattern
✅ Implement Proxy pattern
```

### Implementation Structure

```
ADAPTER PATTERN:
├─ Class adapter
├─ Object adapter
├─ Two-way adapter
├─ Default adapter
└─ Adapter with inheritance

BRIDGE PATTERN:
├─ Abstraction hierarchy
├─ Implementation hierarchy
├─ Bridge connection
├─ Dynamic bridge selection
└─ Bridge with composition

COMPOSITE PATTERN:
├─ Component interface
├─ Leaf components
├─ Composite components
├─ Tree traversal
├─ Recursive operations
└─ Visitor pattern integration

DECORATOR PATTERN:
├─ Component interface
├─ Concrete components
├─ Decorator base class
├─ Concrete decorators
├─ Stacking decorators
└─ Dynamic decoration

FACADE PATTERN:
├─ Complex subsystem
├─ Facade interface
├─ Simplified access
├─ Subsystem coordination
└─ Facade with builder

FLYWEIGHT PATTERN:
├─ Intrinsic state
├─ Extrinsic state
├─ Flyweight factory
├─ Object pooling
└─ Memory optimization

PROXY PATTERN:
├─ Subject interface
├─ Real subject
├─ Proxy subject
├─ Lazy initialization
├─ Access control
└─ Logging proxy
```

### Code Examples

```java
// Adapter Pattern
public interface Target {
    void request();
}

public class Adapter implements Target {
    private Adaptee adaptee;
    
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public void request() {
        adaptee.specificRequest();
    }
}

// Decorator Pattern
public abstract class ComponentDecorator implements Component {
    protected Component component;
    
    public ComponentDecorator(Component component) {
        this.component = component;
    }
    
    @Override
    public void operation() {
        component.operation();
    }
}

public class ConcreteDecorator extends ComponentDecorator {
    public ConcreteDecorator(Component component) {
        super(component);
    }
    
    @Override
    public void operation() {
        super.operation();
        addedBehavior();
    }
    
    private void addedBehavior() {
        // Additional behavior
    }
}

// Composite Pattern
public interface Component {
    void operation();
}

public class Composite implements Component {
    private List<Component> children = new ArrayList<>();
    
    public void add(Component component) {
        children.add(component);
    }
    
    @Override
    public void operation() {
        for (Component child : children) {
            child.operation();
        }
    }
}

// Proxy Pattern
public class ProxySubject implements Subject {
    private RealSubject realSubject;
    
    @Override
    public void request() {
        if (realSubject == null) {
            realSubject = new RealSubject();
        }
        realSubject.request();
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Adapter conversion tests
├─ Bridge abstraction tests
├─ Composite tree tests
├─ Decorator stacking tests
├─ Facade simplification tests
├─ Flyweight pooling tests
└─ Proxy lazy loading tests

INTEGRATION TESTS:
├─ Pattern combination tests
├─ Complex structure tests
├─ Performance tests
└─ Real-world scenario tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

### Portfolio Project

```
PROJECT: Structural Pattern Framework
├─ Objective: Build reusable structural pattern framework
├─ Components:
│  ├─ Pattern implementations
│  ├─ Pattern composition
│  ├─ Pattern registry
│  └─ Example applications
├─ Features:
│  ├─ Multiple pattern implementations
│  ├─ Pattern composition
│  ├─ Flexible architecture
│  ├─ Comprehensive documentation
│  └─ Real-world examples
└─ Deliverables:
   ├─ Source code (4,000+ lines)
   ├─ Unit tests (400+ tests)
   ├─ Documentation
   └─ Examples
```

---

## 🏗️ LAB 23: BEHAVIORAL DESIGN PATTERNS

### Lab Objectives

```
✅ Understand behavioral design patterns
✅ Implement Chain of Responsibility pattern
✅ Implement Command pattern
✅ Implement Iterator pattern
✅ Implement Mediator pattern
✅ Implement Memento pattern
✅ Implement Observer pattern
✅ Implement State pattern
✅ Implement Strategy pattern
✅ Implement Template Method pattern
✅ Implement Visitor pattern
```

### Implementation Structure

```
CHAIN OF RESPONSIBILITY:
├─ Handler interface
├─ Concrete handlers
├─ Request passing
├─ Handler chain
└─ Dynamic chain building

COMMAND PATTERN:
├─ Command interface
├─ Concrete commands
├─ Invoker
├─ Receiver
├─ Command history
└─ Undo/Redo functionality

ITERATOR PATTERN:
├─ Iterator interface
├─ Concrete iterators
├─ Collection interface
├─ Concrete collections
├─ Internal iteration
└─ External iteration

MEDIATOR PATTERN:
├─ Mediator interface
├─ Concrete mediator
├─ Colleague interface
├─ Concrete colleagues
├─ Centralized control
└─ Decoupled communication

MEMENTO PATTERN:
├─ Originator
├─ Memento
├─ Caretaker
├─ State capture
├─ State restoration
└─ History management

OBSERVER PATTERN:
├─ Subject interface
├─ Concrete subjects
├─ Observer interface
├─ Concrete observers
├─ Event notification
└─ Loose coupling

STATE PATTERN:
├─ State interface
├─ Concrete states
├─ Context
├─ State transitions
├─ Behavior changes
└─ State machine

STRATEGY PATTERN:
├─ Strategy interface
├─ Concrete strategies
├─ Context
├─ Strategy selection
├─ Algorithm encapsulation
└─ Runtime selection

TEMPLATE METHOD PATTERN:
├─ Abstract template
├─ Template method
├─ Abstract operations
├─ Concrete implementations
├─ Algorithm skeleton
└─ Customization points

VISITOR PATTERN:
├─ Visitor interface
├─ Concrete visitors
├─ Element interface
├─ Concrete elements
├─ Double dispatch
└─ Separation of concerns
```

### Code Examples

```java
// Observer Pattern
public interface Observer {
    void update(Subject subject);
}

public class Subject {
    private List<Observer> observers = new ArrayList<>();
    private int state;
    
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
    
    public void setState(int state) {
        this.state = state;
        notifyObservers();
    }
}

// Strategy Pattern
public interface Strategy {
    int execute(int a, int b);
}

public class AddStrategy implements Strategy {
    @Override
    public int execute(int a, int b) {
        return a + b;
    }
}

public class Context {
    private Strategy strategy;
    
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }
    
    public int executeStrategy(int a, int b) {
        return strategy.execute(a, b);
    }
}

// Command Pattern
public interface Command {
    void execute();
    void undo();
}

public class ConcreteCommand implements Command {
    private Receiver receiver;
    
    public ConcreteCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        receiver.action();
    }
    
    @Override
    public void undo() {
        receiver.undoAction();
    }
}

// State Pattern
public interface State {
    void handle(Context context);
}

public class Context {
    private State state;
    
    public void setState(State state) {
        this.state = state;
    }
    
    public void request() {
        state.handle(this);
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Chain of responsibility tests
├─ Command execution tests
├─ Iterator traversal tests
├─ Mediator communication tests
├─ Memento state tests
├─ Observer notification tests
├─ State transition tests
├─ Strategy selection tests
├─ Template method tests
└─ Visitor traversal tests

INTEGRATION TESTS:
├─ Pattern combination tests
├─ Complex behavior tests
├─ Event handling tests
├─ State machine tests
└─ Real-world scenario tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

### Portfolio Project

```
PROJECT: Behavioral Pattern Framework
├─ Objective: Build reusable behavioral pattern framework
├─ Components:
│  ├─ Pattern implementations
│  ├─ Pattern orchestration
│  ├─ Event system
│  └─ Example applications
├─ Features:
│  ├─ Multiple pattern implementations
│  ├─ Event-driven architecture
│  ├─ State machine support
│  ├─ Comprehensive documentation
│  └─ Real-world examples
└─ Deliverables:
   ├─ Source code (4,000+ lines)
   ├─ Unit tests (400+ tests)
   ├─ Documentation
   └─ Examples
```

---

## 🔤 LAB 24: REGULAR EXPRESSIONS

### Lab Objectives

```
✅ Master regular expression syntax
✅ Implement pattern matching
✅ Implement text validation
✅ Implement text extraction
✅ Implement text replacement
✅ Implement advanced regex features
✅ Write comprehensive tests
✅ Create portfolio project
```

### Implementation Structure

```
REGEX BASICS:
├─ Character classes
├─ Quantifiers
├─ Anchors
├─ Alternation
├─ Grouping
├─ Escaping
└─ Flags

PATTERN MATCHING:
├─ Simple matching
├─ Complex patterns
├─ Multiline matching
├─ Case-insensitive matching
├─ Unicode support
└─ Performance optimization

TEXT VALIDATION:
├─ Email validation
├─ Phone number validation
├─ URL validation
├─ IP address validation
├─ Credit card validation
└─ Custom validation

TEXT EXTRACTION:
├─ Finding patterns
├─ Capturing groups
├─ Multiple matches
├─ Named groups
├─ Lookahead/Lookbehind
└─ Backreferences

TEXT REPLACEMENT:
├─ Simple replacement
├─ Pattern-based replacement
├─ Replacement with groups
├─ Conditional replacement
├─ Function-based replacement
└─ Performance optimization
```

### Code Examples

```java
// Email Validation
public class EmailValidator {
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
    
    private Pattern pattern;
    
    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }
    
    public boolean isValid(String email) {
        return pattern.matcher(email).matches();
    }
}

// Text Extraction
public class TextExtractor {
    public List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();
        Pattern pattern = Pattern.compile(
            "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        return emails;
    }
    
    public Map<String, String> extractKeyValues(String text) {
        Map<String, String> keyValues = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)=(\\w+)");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            keyValues.put(matcher.group(1), matcher.group(2));
        }
        return keyValues;
    }
}

// Text Replacement
public class TextReplacer {
    public String replaceEmails(String text, String replacement) {
        Pattern pattern = Pattern.compile(
            "[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}");
        return pattern.matcher(text).replaceAll(replacement);
    }
    
    public String replaceWithFunction(String text, 
            Function<String, String> replacer) {
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            matcher.appendReplacement(sb, 
                Matcher.quoteReplacement(replacer.apply(matcher.group())));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

// Advanced Pattern Matching
public class AdvancedMatcher {
    public boolean matchesPattern(String text, String pattern) {
        return Pattern.matches(pattern, text);
    }
    
    public List<String> findAllMatches(String text, String pattern) {
        List<String> matches = new ArrayList<>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }
    
    public Map<String, String> extractNamedGroups(String text, 
            String pattern) {
        Map<String, String> groups = new HashMap<>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                groups.put("group" + i, m.group(i));
            }
        }
        return groups;
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ Pattern matching tests
├─ Validation tests
├─ Extraction tests
├─ Replacement tests
├─ Edge case tests
├─ Performance tests
└─ Unicode tests

INTEGRATION TESTS:
├─ Complex pattern tests
├─ Real-world scenario tests
├─ Performance tests
└─ Integration tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

### Portfolio Project

```
PROJECT: Text Processing Framework
├─ Objective: Build comprehensive text processing framework
├─ Components:
│  ├─ Regex utilities
│  ├─ Validators
│  ├─ Extractors
│  ├─ Replacers
│  └─ Example applications
├─ Features:
│  ├─ Multiple validators
│  ├─ Advanced extraction
│  ├─ Performance optimization
│  ├─ Comprehensive documentation
│  └─ Real-world examples
└─ Deliverables:
   ├─ Source code (4,000+ lines)
   ├─ Unit tests (400+ tests)
   ├─ Documentation
   └─ Examples
```

---

## 📅 LAB 25: DATE-TIME API

### Lab Objectives

```
✅ Master Java Date-Time API
✅ Implement LocalDate operations
✅ Implement LocalTime operations
✅ Implement LocalDateTime operations
✅ Implement ZonedDateTime operations
✅ Implement Duration and Period
✅ Implement temporal queries
✅ Write comprehensive tests
✅ Create portfolio project
```

### Implementation Structure

```
LOCAL DATE:
├─ Creating dates
├─ Date arithmetic
├─ Date comparison
├─ Date formatting
├─ Date parsing
└─ Date queries

LOCAL TIME:
├─ Creating times
├─ Time arithmetic
├─ Time comparison
├─ Time formatting
├─ Time parsing
└─ Time queries

LOCAL DATETIME:
├─ Creating datetimes
├─ DateTime arithmetic
├─ DateTime comparison
├─ DateTime formatting
├─ DateTime parsing
└─ DateTime queries

ZONED DATETIME:
├─ Creating zoned datetimes
├─ Timezone conversion
├─ Timezone operations
├─ DST handling
├─ Zoned datetime formatting
└─ Zoned datetime parsing

DURATION & PERIOD:
├─ Duration creation
├─ Duration arithmetic
├─ Period creation
├─ Period arithmetic
├─ Duration/Period comparison
└─ Duration/Period formatting

TEMPORAL QUERIES:
├─ Custom queries
├─ Temporal adjusters
├─ Temporal calculations
├─ Temporal ranges
└─ Temporal patterns
```

### Code Examples

```java
// LocalDate Operations
public class DateOperations {
    public LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }
    
    public LocalDate subtractMonths(LocalDate date, int months) {
        return date.minusMonths(months);
    }
    
    public int daysBetween(LocalDate date1, LocalDate date2) {
        return (int) ChronoUnit.DAYS.between(date1, date2);
    }
    
    public boolean isLeapYear(LocalDate date) {
        return date.isLeapYear();
    }
    
    public LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }
    
    public LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }
}

// LocalDateTime Operations
public class DateTimeOperations {
    public LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }
    
    public LocalDateTime subtractMinutes(LocalDateTime dateTime, 
            int minutes) {
        return dateTime.minusMinutes(minutes);
    }
    
    public long secondsBetween(LocalDateTime dt1, LocalDateTime dt2) {
        return ChronoUnit.SECONDS.between(dt1, dt2);
    }
    
    public String formatDateTime(LocalDateTime dateTime, 
            String pattern) {
        DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
    
    public LocalDateTime parseDateTime(String dateTimeStr, 
            String pattern) {
        DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}

// ZonedDateTime Operations
public class ZonedDateTimeOperations {
    public ZonedDateTime createZonedDateTime(LocalDateTime dateTime, 
            String zoneId) {
        return dateTime.atZone(ZoneId.of(zoneId));
    }
    
    public ZonedDateTime convertTimezone(ZonedDateTime dateTime, 
            String targetZoneId) {
        return dateTime.withZoneSameInstant(ZoneId.of(targetZoneId));
    }
    
    public List<String> getAllTimezones() {
        return ZoneId.getAvailableZoneIds()
            .stream()
            .sorted()
            .collect(Collectors.toList());
    }
    
    public boolean isDaylightSavingTime(ZonedDateTime dateTime) {
        return dateTime.getZone()
            .getRules()
            .isDaylightSavings(dateTime.toInstant());
    }
}

// Duration and Period
public class DurationPeriodOperations {
    public Duration createDuration(long hours, long minutes) {
        return Duration.ofHours(hours).plusMinutes(minutes);
    }
    
    public Period createPeriod(int years, int months, int days) {
        return Period.of(years, months, days);
    }
    
    public LocalDate addPeriod(LocalDate date, Period period) {
        return date.plus(period);
    }
    
    public LocalDateTime addDuration(LocalDateTime dateTime, 
            Duration duration) {
        return dateTime.plus(duration);
    }
    
    public String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

// Temporal Queries
public class TemporalQueries {
    public LocalDate getNextMonday(LocalDate date) {
        return date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    }
    
    public LocalDate getLastDayOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }
    
    public LocalDate getNthDayOfMonth(LocalDate date, int n, 
            DayOfWeek dayOfWeek) {
        return date.with(TemporalAdjusters.dayOfWeekInMonth(n, dayOfWeek));
    }
    
    public boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
    
    public List<LocalDate> getBusinessDays(LocalDate startDate, 
            LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
            .filter(date -> !isWeekend(date))
            .collect(Collectors.toList());
    }
}
```

### Testing Strategy

```
UNIT TESTS:
├─ LocalDate tests
├─ LocalTime tests
├─ LocalDateTime tests
├─ ZonedDateTime tests
├─ Duration tests
├─ Period tests
├─ Temporal query tests
├─ Formatting tests
├─ Parsing tests
└─ Edge case tests

INTEGRATION TESTS:
├─ Complex date operations
├─ Timezone conversion tests
├─ DST handling tests
├─ Real-world scenario tests
└─ Performance tests

TEST COVERAGE:
├─ Target: 85%+
├─ Classes: 85%+
├─ Methods: 85%+
├─ Branches: 80%+
└─ Lines: 85%+
```

### Portfolio Project

```
PROJECT: Date-Time Utilities Framework
├─ Objective: Build comprehensive date-time utilities
├─ Components:
│  ├─ Date utilities
│  ├─ Time utilities
│  ├─ DateTime utilities
│  ├─ Timezone utilities
│  ├─ Formatting utilities
│  └─ Example applications
├─ Features:
│  ├─ Multiple date operations
│  ├─ Timezone support
│  ├─ Custom formatting
│  ├─ Temporal queries
│  ├─ Comprehensive documentation
│  └─ Real-world examples
└─ Deliverables:
   ├─ Source code (4,000+ lines)
   ├─ Unit tests (400+ tests)
   ├─ Documentation
   └─ Examples
```

---

## 📊 PHASE 2B SUMMARY

### Deliverables

```
TOTAL CONTENT: 20,500+ lines
├─ Lab 21: 4,000+ lines
├─ Lab 22: 4,000+ lines
├─ Lab 23: 4,500+ lines
├─ Lab 24: 4,000+ lines
└─ Lab 25: 4,000+ lines

TOTAL TESTS: 650+ unit tests
├─ Lab 21: 130+ tests
├─ Lab 22: 130+ tests
├─ Lab 23: 150+ tests
├─ Lab 24: 120+ tests
└─ Lab 25: 120+ tests

TOTAL PROJECTS: 5 portfolio projects
├─ Design Pattern Framework
├─ Structural Pattern Framework
├─ Behavioral Pattern Framework
├─ Text Processing Framework
└─ Date-Time Utilities Framework

QUALITY METRICS:
├─ Code Coverage: 85%+
├─ Test Pass Rate: 100%
├─ Quality Score: 85/100
├─ Defect Density: <1 per 1000 LOC
└─ Security Score: 95/100
```

### Timeline

```
WEEK 6: Design Patterns (Labs 21-23)
├─ Lab 21: Creational Patterns (3 days)
├─ Lab 22: Structural Patterns (2 days)
├─ Lab 23: Behavioral Patterns (2 days)
└─ Status: IN PROGRESS

WEEK 7: Regular Expressions (Lab 24)
├─ Lab 24: Regular Expressions (5 days)
└─ Status: PLANNED

WEEK 8: Date-Time API (Lab 25)
├─ Lab 25: Date-Time API (5 days)
└─ Status: PLANNED

TOTAL: 3 weeks, 5 labs, 20,500+ lines, 650+ tests, 5 projects
```

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Phase 2B Advanced Implementation Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Implementation |

---

**Java Master Lab - Phase 2B Advanced Implementation Guide**

*Detailed Implementation Guide for Labs 21-25*

**Status: ACTIVE | Focus: Implementation | Impact: Excellence**

---

*Implement Phase 2B labs and master advanced Java concepts!* 🚀