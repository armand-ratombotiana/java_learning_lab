# Interview Questions: Design Patterns

## Basic Questions

### Q1: Explain the Singleton pattern
**A**: Ensures a class has only one instance and provides global access point. Implementation: private constructor, static instance, getInstance() method.

### Q2: What is the Factory Method pattern?
**A**: Creates objects without specifying exact class. Defines interface for creation, lets subclasses decide the class.

### Q3: Explain the Observer pattern
**A**: One-to-many dependency where when one object changes state, all dependents are notified. Subject maintains list of observers.

### Q4: When would you use the Strategy pattern?
**A**: When you want to define a family of algorithms, encapsulate each one, and make them interchangeable. Allows swapping algorithms at runtime.

## Intermediate Questions

### Q5: Difference between Adapter and Decorator?
**A**: Adapter makes incompatible interfaces work together. Decorator adds responsibilities to objects dynamically without subclassing.

### Q6: What is the Builder pattern and when use it?
**A**: Separates construction of complex object from representation. Use when object has many optional parameters or complex creation.

### Q7: Explain the Facade pattern
**A**: Provides simplified interface to complex subsystem. Hides implementation complexity and provides easier-to-use API.

### Q8: When use Composite pattern?
**A**: When you want to treat individual objects and compositions uniformly. Tree structures, UI components, file systems.

## Advanced Questions

### Q9: How do patterns relate to SOLID principles?
**A**: Patterns often embody SOLID: Strategy/Observer (OCP), Factory (DIP), Command (ISP), Singleton (some SRP issues)

### Q10: How would you refactor code to use patterns?
**A**: 
1. Identify code smells (complex conditionals, duplicated code)
2. Select appropriate pattern
3. Apply gradually with tests
4. Don't force patterns

### Q11: What are anti-patterns?
**A**: Common solutions that seem correct but lead to bad outcomes. Examples: God class, Spaghetti code, Singleton overuse.

### Q12: How do you choose between patterns?
**A**: Understand the problem first. Consider: What are you trying to achieve? What are the tradeoffs? Is pattern solving real problem?