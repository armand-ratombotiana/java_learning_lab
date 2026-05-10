# Spring Core - Pedagogic Guide

## Teaching Strategy

### Module Overview
This module introduces Spring's core DI container and IoC principles. Students should progress from understanding basic DI to implementing advanced Spring features.

### Suggested Learning Path
1. **Day 1**: IoC Container and Bean Lifecycle
   - Explain Inversion of Control concept
   - Demonstrate BeanFactory vs ApplicationContext
   - Show bean lifecycle: create → initialize → destroy

2. **Day 2**: Dependency Injection Patterns
   - Constructor injection (preferred)
   - Setter injection
   - Field injection (discourage)
   - Circular dependency handling

3. **Day 3**: Configuration Approaches
   - XML configuration (legacy but important)
   - Java-based configuration (@Configuration)
   - Component scanning with annotations

4. **Day 4**: Advanced Features
   - Bean scopes (singleton, prototype, custom)
   - Profile-based configuration
   - Conditional bean creation

5. **Day 5**: AOP Fundamentals
   - Cross-cutting concerns
   - Aspect, JoinPoint, Pointcut concepts
   - Proxy-based AOP

## Teaching Methods

### Lecture Style
- Start with conceptual explanation
- Follow with live code demonstrations
- Conclude with hands-on exercise

### Code Examples
- Keep examples simple and focused
- Show "before" and "after" patterns
- Use real-world scenarios (payment processing, notifications)

### Common Pitfalls to Address
1. Circular dependency errors
2. Bean scope misunderstandings
3. Lazy initialization confusion
4. AOP proxy limitations

## Hands-on Exercises

### Exercise Progression
| Exercise | Difficulty | Est. Time | Key Concept |
|----------|------------|-----------|--------------|
| 1 | Basic | 30 min | DI Container |
| 2 | Basic | 30 min | Java Config |
| 3 | Intermediate | 45 min | Bean Scopes |
| 4 | Intermediate | 45 min | Profiles |
| 5 | Advanced | 60 min | Post-Processors |
| 6 | Advanced | 60 min | Events |
| 7 | Advanced | 75 min | AOP Basics |
| 8 | Advanced | 45 min | Conditional |
| 9 | Expert | 60 min | Custom Scope |
| 10 | Expert | 45 min | FactoryBean |

## Assessment Criteria
- Students can explain IoC and DI concepts
- Students can create beans using multiple configuration approaches
- Students understand bean lifecycle and scopes
- Students can implement basic AOP aspects

## Recommended Projects

### Mini-Project (3-4 hours)
Spring DI Container Demo - Students build a complete DI container demonstrating all core concepts with practical examples.

### Real-World Project (8+ hours)
Full Spring DI Container - Advanced project covering custom scopes, post-processors, AOP integration, and event handling in a realistic scenario.

## Resources
- Spring Framework Documentation: https://docs.spring.io/spring-framework/reference/
- Spring Core Best Practices: See PROJECTS.md for production-ready examples
- Video Tutorials: Spring Framework 6 Fundamentals (Spring Academy)

## Time Allocation
- Lecture: 40%
- Code Demonstration: 30%
- Hands-on Exercises: 30%

## Prerequisites
- Java fundamentals
- Object-oriented programming concepts
- Basic understanding of design patterns (Factory, Strategy)