# 📚 Java Learning Lab - Pedagogical Guide

## 🎓 Learning Methodology

This learning lab follows a **progressive mastery approach** with hands-on practice.

---

## 📋 Lab Structure

### Core Labs (01-10)
| Lab | Module | Focus Area | Difficulty |
|-----|--------|------------|------------|
| 01 | Java Basics | Variables, Control Flow, OOP | ⭐ Beginner |
| 02 | OOP Concepts | Inheritance, Polymorphism, Interfaces | ⭐⭐ Beginner |
| 03 | Collections | List, Set, Map, Queue | ⭐⭐ Intermediate |
| 04 | Streams API | Functional Programming, Lazy Evaluation | ⭐⭐ Intermediate |
| 05 | Concurrency | Threads, Executors, Futures | ⭐⭐⭐ Advanced |

### Framework Labs (11-30)
| Lab | Module | Focus Area |
|-----|--------|------------|
| 11 | Spring Boot | REST APIs, Dependency Injection |
| 12 | Spring Data JPA | ORM, Repositories, Transactions |
| 13 | Spring Security | OAuth2, JWT, Authentication |
| 14 | Quarkus | Reactive, Native Image, Hot Reload |
| 15 | Vert.x | Event Bus, Event Loop, Non-blocking |

### Enterprise Labs (31-50)
| Lab | Module | Use Case |
|-----|--------|----------|
| 31 | MongoDB | Document-based persistence |
| 32 | Redis | Caching, Session management |
| 33 | PostgreSQL | Relational databases |
| 34 | RabbitMQ | Message queuing |
| 35 | Kafka | Event streaming |

### Advanced Labs (51-70)
| Lab | Module | Architecture |
|-----|--------|--------------|
| 51 | CQRS | Command Query Responsibility Segregation |
| 52 | DDD | Domain-Driven Design |
| 53 | Event Sourcing | Event-driven architecture |
| 54 | Service Mesh | Istio, Linkerd |

---

## 🧪 How to Use These Labs

### 1. Setup Environment
```bash
# Clone repository
git clone https://github.com/armand-ratombotiana/JavaLearning.git
cd JavaLearning

# Build all modules
mvn clean install

# Run specific lab
cd 02-spring-boot/01-spring-boot-basics
mvn spring-boot:run
```

### 2. Lab Progression
```
Week 1-2: Core Java (01-10)
  └── Focus: Syntax, OOP, Collections

Week 3-4: Framework Basics (11-20)
  └── Focus: Spring Boot, REST APIs

Week 5-6: Data & Messaging (21-35)
  └── Focus: Databases, Message queues

Week 7-8: Cloud Native (36-50)
  └── Focus: Docker, Kubernetes, CI/CD
```

### 3. Each Lab Contains
- **README.md** - Theory and concepts
- **src/main/java** - Implementation examples
- **src/test/java** - Unit and integration tests
- **Exercises** - Hands-on practice problems

---

## ✅ Lab Completion Checklist

- [ ] Read the module README
- [ ] Understand the core concepts
- [ ] Run the example code
- [ ] Complete the exercises
- [ ] Write tests for your solutions
- [ ] Review and refactor code

---

## 🚀 Quick Start Commands

```bash
# Build entire project
mvn clean compile

# Run tests
mvn test

# Run specific module
mvn -pl 02-spring-boot/01-spring-boot-basics spring-boot:run

# Run with Docker
docker-compose up -d
```

---

## 📖 Additional Resources

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Quarkus Guides](https://quarkus.io/guides/)

---

<div align="center">

**Happy Learning! 🎉**

Build something amazing today!

</div>