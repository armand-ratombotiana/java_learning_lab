# Module 11: Design Patterns - Pedagogic Guide

**Total Study Time**: 8-10 hours  
**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-10  
**Learning Outcomes**: 6 major competencies

---

## 📚 Learning Outcomes

By completing this module, you will be able to:

1. **Understand Design Patterns** - Know what design patterns are and why they matter
2. **Apply Creational Patterns** - Use Singleton, Factory, Builder, and Prototype patterns
3. **Apply Structural Patterns** - Use Adapter, Decorator, Facade, and Proxy patterns
4. **Apply Behavioral Patterns** - Use Observer, Strategy, Command, and State patterns
5. **Recognize Anti-Patterns** - Identify and avoid common design mistakes
6. **Select Appropriate Patterns** - Choose the right pattern for the problem

---

## 🎯 4-Phase Study Path

### Phase 1: Foundation (2 hours)

**Goal**: Understand what design patterns are and their importance

**Activities**:
1. Read DEEP_DIVE.md Introduction section (15 min)
2. Watch design patterns overview video (30 min)
3. Answer Beginner-level quiz questions (Q1-Q6) (30 min)
4. Discuss: Why are design patterns important? (15 min)

**Key Concepts**:
- What are design patterns?
- Why use design patterns?
- Pattern categories (Creational, Structural, Behavioral)

**Checkpoint**: Can you explain what a design pattern is in your own words?

---

### Phase 2: Creational & Structural Patterns (3 hours)

**Goal**: Master object creation and composition patterns

**Activities**:

**Creational Patterns (1.5 hours)**:
1. Study Singleton pattern (30 min)
   - Read DEEP_DIVE.md Singleton section
   - Review EDGE_CASES.md pitfalls 1-2
   - Code along: Implement Enum Singleton
2. Study Factory pattern (20 min)
   - Read DEEP_DIVE.md Factory section
   - Review EDGE_CASES.md pitfall 3
3. Study Builder pattern (20 min)
   - Read DEEP_DIVE.md Builder section
   - Review EDGE_CASES.md pitfall 4
4. Study Prototype pattern (20 min)
   - Read DEEP_DIVE.md Prototype section
   - Review EDGE_CASES.md pitfall 15

**Structural Patterns (1.5 hours)**:
1. Study Adapter pattern (20 min)
   - Read DEEP_DIVE.md Adapter section
   - Review EDGE_CASES.md pitfall 16
2. Study Decorator pattern (20 min)
   - Read DEEP_DIVE.md Decorator section
   - Review EDGE_CASES.md pitfall 7
3. Study Facade pattern (20 min)
   - Read DEEP_DIVE.md Facade section
   - Review EDGE_CASES.md pitfall 8
4. Study Proxy pattern (20 min)
   - Read DEEP_DIVE.md Proxy section
   - Review EDGE_CASES.md pitfall 9

**Practice Exercises**:
1. Implement a Singleton with serialization support
2. Create a Factory for different database types
3. Build a User object using Builder pattern
4. Implement a Decorator for a Coffee class
5. Create a Facade for a complex subsystem

**Checkpoint**: Can you implement each pattern from memory?

---

### Phase 3: Behavioral & Architectural Patterns (2.5 hours)

**Goal**: Master object interaction and system architecture patterns

**Activities**:

**Behavioral Patterns (1.5 hours)**:
1. Study Observer pattern (20 min)
   - Read DEEP_DIVE.md Observer section
   - Review EDGE_CASES.md pitfall 5
2. Study Strategy pattern (20 min)
   - Read DEEP_DIVE.md Strategy section
   - Review EDGE_CASES.md pitfall 6
3. Study Command pattern (20 min)
   - Read DEEP_DIVE.md Command section
   - Review EDGE_CASES.md pitfall 10
4. Study State pattern (20 min)
   - Read DEEP_DIVE.md State section
   - Review EDGE_CASES.md pitfall 11

**Architectural Patterns (1 hour)**:
1. Study MVC pattern (20 min)
   - Read DEEP_DIVE.md MVC section
   - Review EDGE_CASES.md pitfall 13
2. Study Repository pattern (20 min)
   - Read DEEP_DIVE.md Repository section
   - Review EDGE_CASES.md pitfall 12
3. Study pattern combinations (20 min)
   - Read DEEP_DIVE.md Real-World Applications

**Practice Exercises**:
1. Implement an Observer for a stock price update system
2. Create different payment strategies
3. Build a command-based undo/redo system
4. Implement a state machine for order processing
5. Create a Repository for user data access

**Checkpoint**: Can you explain when to use each pattern?

---

### Phase 4: Integration & Mastery (2.5 hours)

**Goal**: Integrate patterns and master pattern selection

**Activities**:

1. **Pattern Selection Practice** (1 hour)
   - Read DEEP_DIVE.md Pattern Selection Guide
   - Answer Intermediate-level quiz questions (Q7-Q14)
   - Answer Advanced-level quiz questions (Q15-Q20)
   - Answer Expert-level quiz questions (Q21-Q24)

2. **Anti-Pattern Recognition** (30 min)
   - Read DEEP_DIVE.md Anti-Patterns section
   - Review EDGE_CASES.md pitfalls 17-18
   - Identify anti-patterns in sample code

3. **Real-World Application** (1 hour)
   - Study Spring Framework patterns
   - Analyze existing codebase for patterns
   - Refactor code using appropriate patterns

**Capstone Project**:
Design and implement a complete application using multiple patterns:

```
Project: E-Commerce Order Processing System

Requirements:
1. Create orders with multiple items (Builder pattern)
2. Support different payment methods (Strategy pattern)
3. Notify customers of order status (Observer pattern)
4. Manage order states (State pattern)
5. Access order data (Repository pattern)
6. Provide simple interface to complex subsystem (Facade pattern)

Deliverables:
- Design document with pattern selection rationale
- Complete implementation with all patterns
- Unit tests for each pattern
- Documentation of pattern usage
```

**Checkpoint**: Can you design a system using multiple patterns?

---

## 📖 Study Materials

### Required Reading
- DEEP_DIVE.md (60-75 minutes)
- QUIZZES.md (90-120 minutes)
- EDGE_CASES.md (45-60 minutes)

### Recommended Resources
- Gang of Four Design Patterns book
- Refactoring Guru Design Patterns website
- Spring Framework documentation
- Java Design Patterns tutorials

### Code Examples
- 140+ code examples in DEEP_DIVE.md
- 18 pitfall examples in EDGE_CASES.md
- 24 quiz questions with explanations

---

## 🎓 Practice Exercises

### Exercise 1: Singleton Implementation
**Difficulty**: Beginner  
**Time**: 30 minutes

Implement a thread-safe Singleton for a configuration manager:
- Support serialization
- Prevent reflection attacks
- Provide getInstance() method

```java
public class ConfigurationManager {
    // TODO: Implement Singleton
    
    public String getProperty(String key) {
        // TODO: Implement
    }
}
```

### Exercise 2: Factory Pattern
**Difficulty**: Beginner  
**Time**: 30 minutes

Create a factory for different database connections:
- Support MySQL, PostgreSQL, MongoDB
- Return appropriate database instance
- Handle invalid types

```java
public class DatabaseFactory {
    public static Database createDatabase(String type) {
        // TODO: Implement
    }
}
```

### Exercise 3: Builder Pattern
**Difficulty**: Intermediate  
**Time**: 45 minutes

Build a User class with optional fields:
- Name (required)
- Email (required)
- Age, Phone, Address (optional)
- Ensure immutability
- Provide fluent interface

```java
public class User {
    // TODO: Implement with Builder
}

// Usage
User user = new User.Builder("John", "john@example.com")
    .age(30)
    .phone("555-1234")
    .build();
```

### Exercise 4: Observer Pattern
**Difficulty**: Intermediate  
**Time**: 45 minutes

Implement a stock price notification system:
- Subject: StockPrice
- Observer: Investor
- Notify investors of price changes
- Support subscribe/unsubscribe

```java
public class StockPrice {
    // TODO: Implement Observer pattern
}

public class Investor {
    // TODO: Implement Observer
}
```

### Exercise 5: Strategy Pattern
**Difficulty**: Intermediate  
**Time**: 45 minutes

Create a payment processing system:
- Support Credit Card, PayPal, Bitcoin
- Allow runtime strategy selection
- Calculate fees based on strategy

```java
public interface PaymentStrategy {
    void pay(double amount);
    double calculateFee(double amount);
}

public class PaymentProcessor {
    // TODO: Implement with Strategy pattern
}
```

### Exercise 6: Decorator Pattern
**Difficulty**: Intermediate  
**Time**: 45 minutes

Build a text formatting system:
- Base: PlainText
- Decorators: Bold, Italic, Underline
- Combine multiple decorators
- Calculate final cost

```java
public interface Text {
    String format();
    double getCost();
}

public class PlainText implements Text {
    // TODO: Implement
}

public abstract class TextDecorator implements Text {
    // TODO: Implement
}
```

### Exercise 7: State Pattern
**Difficulty**: Advanced  
**Time**: 60 minutes

Implement an order processing state machine:
- States: Pending, Confirmed, Shipped, Delivered
- Transitions: Confirm → Ship → Deliver
- Prevent invalid transitions
- Log state changes

```java
public interface OrderState {
    void handle(Order order);
}

public class Order {
    // TODO: Implement with State pattern
}
```

### Exercise 8: Repository Pattern
**Difficulty**: Advanced  
**Time**: 60 minutes

Create a user data access layer:
- Interface: UserRepository
- Implementation: InMemoryUserRepository
- Support CRUD operations
- Support filtering and searching

```java
public interface UserRepository {
    void save(User user);
    User findById(int id);
    List<User> findAll();
    List<User> findByName(String name);
    void delete(int id);
}
```

---

## 🧪 Assessment Criteria

### Knowledge Assessment
- **Beginner Quiz**: 6 questions (25%)
- **Intermediate Quiz**: 8 questions (33%)
- **Advanced Quiz**: 6 questions (25%)
- **Expert Quiz**: 4 questions (17%)

### Practical Assessment
- **Exercise Completion**: 8 exercises (40%)
- **Code Quality**: Follows best practices (30%)
- **Pattern Implementation**: Correct usage (20%)
- **Documentation**: Clear and complete (10%)

### Mastery Criteria
- Score 80%+ on all quiz levels
- Complete all 8 exercises
- Implement capstone project
- Explain pattern selection rationale

---

## 📊 Progress Tracking

### Week 1: Foundation & Creational Patterns
- [ ] Read Introduction section
- [ ] Complete Beginner quiz (Q1-Q6)
- [ ] Study Singleton pattern
- [ ] Study Factory pattern
- [ ] Study Builder pattern
- [ ] Complete Exercise 1-2

### Week 2: Structural & Behavioral Patterns
- [ ] Study Adapter, Decorator, Facade, Proxy
- [ ] Study Observer, Strategy, Command, State
- [ ] Complete Intermediate quiz (Q7-Q14)
- [ ] Complete Exercise 3-5

### Week 3: Architectural Patterns & Mastery
- [ ] Study MVC and Repository patterns
- [ ] Complete Advanced quiz (Q15-Q20)
- [ ] Complete Expert quiz (Q21-Q24)
- [ ] Complete Exercise 6-8

### Week 4: Integration & Capstone
- [ ] Review all patterns
- [ ] Complete capstone project
- [ ] Document pattern usage
- [ ] Final assessment

---

## 🎯 Learning Tips

1. **Code Along**: Type out all examples, don't just read
2. **Understand Why**: Know why each pattern exists
3. **Practice Selection**: Practice choosing the right pattern
4. **Recognize Patterns**: Identify patterns in existing code
5. **Avoid Over-Engineering**: Use patterns when needed, not always
6. **Combine Patterns**: Learn how patterns work together
7. **Real-World Examples**: Study Spring Framework usage
8. **Refactor Code**: Apply patterns to improve existing code

---

## 🚀 Next Steps

After completing this module:

1. **Apply Patterns**: Use patterns in your projects
2. **Study Advanced Patterns**: Learn more patterns (Visitor, Iterator, etc.)
3. **Architectural Patterns**: Study system-level patterns
4. **Framework Patterns**: Study Spring, Hibernate patterns
5. **Refactoring**: Apply patterns to improve existing code

---

## 📞 Common Questions

**Q: Should I use all patterns in every project?**  
A: No. Use patterns when they solve a real problem. Avoid over-engineering.

**Q: How do I know which pattern to use?**  
A: Identify the problem, then match it to a pattern. Use the Pattern Selection Guide.

**Q: Can I combine multiple patterns?**  
A: Yes. Many real-world systems use multiple patterns together.

**Q: Are patterns language-specific?**  
A: No. Patterns are language-agnostic. They apply to most OOP languages.

**Q: How do I practice patterns?**  
A: Complete the exercises, refactor existing code, and build projects using patterns.

---

**Module 11 - Design Patterns Pedagogic Guide**  
*Your complete learning roadmap for design patterns mastery*