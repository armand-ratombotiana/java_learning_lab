# Module 33: Code Organization - Deep Dive

**Difficulty Level**: Intermediate  
**Prerequisites**: Modules 01-32  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Package by Feature vs Package by Layer](#package-structure)
2. [Module System (Project Jigsaw)](#modules)
3. [Clean Architecture Basics](#clean)
4. [SOLID Principles in Code Organization](#solid)
5. [Hexagonal Architecture](#hexagonal)

---

## 1. Package by Feature vs Package by Layer <a name="package-structure"></a>
- **Package by Layer**: Classes are grouped by their technical layer (e.g., `controllers`, `services`, `repositories`). Often leads to scattered changes when implementing a single business feature.
- **Package by Feature**: Classes are grouped by the business feature they implement (e.g., `user`, `billing`, `inventory`). Promotes higher cohesion and makes it easier to extract into microservices later.

---

## 2. Module System (Project Jigsaw) <a name="modules"></a>
Introduced in Java 9, the Java Platform Module System (JPMS) allows you to define modules with explicit dependencies and exports, encapsulating internal APIs securely.

```java
// module-info.java
module com.example.billing {
    requires com.example.core;
    requires java.sql;
    
    exports com.example.billing.api;
    // Internal packages are NOT exported
}
```

---

## 3. Clean Architecture Basics <a name="clean"></a>
Clean Architecture puts the business rules (Entities and Use Cases) at the center of the application. Frameworks, databases, and UI are treated as external details. Dependencies only point inwards.

---

## 4. SOLID Principles in Code Organization <a name="solid"></a>
- **Single Responsibility Principle (SRP)**: A class should have one reason to change.
- **Open/Closed Principle (OCP)**: Software entities should be open for extension but closed for modification.
- **Liskov Substitution Principle (LSP)**: Subtypes must be substitutable for their base types.
- **Interface Segregation Principle (ISP)**: Many client-specific interfaces are better than one general-purpose interface.
- **Dependency Inversion Principle (DIP)**: Depend upon abstractions, not concretions.

---

## 5. Hexagonal Architecture <a name="hexagonal"></a>
Also known as Ports and Adapters. The core logic is isolated from the outside world. Communication happens through "Ports" (Interfaces) and "Adapters" (Implementations like REST controllers or DB repositories).