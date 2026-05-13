# Architecture in Java Basics

## Project Structure

```
src/
└── main/
    └── java/
        └── com/
            └── learning/
                └── lab/
                    └── module01/
                        ├── Main.java              # Entry point
                        ├── service/              # Business logic
                        │   └── Calculator.java
                        ├── model/                # Data models
                        │   └── User.java
                        ├── util/                 # Utilities
                        │   └── ValidationUtil.java
                        └── config/               # Configuration
                            └── AppConfig.java
```

## Layered Architecture

```
┌─────────────────────────────────────────────┐
│  Presentation Layer (main, I/O)             │
├─────────────────────────────────────────────┤
│  Service Layer (business logic)            │
├─────────────────────────────────────────────┤
│  Data Layer (models, utilities)             │
└─────────────────────────────────────────────┘
```

## Single Responsibility Principle

```java
// BEFORE - mixed responsibilities
class User {
    void setName(String name) { ... }
    void saveToDatabase() { ... }        // Wrong
    void sendEmail() { ... }             // Wrong
}

// AFTER - separate concerns
class User {
    void setName(String name) { ... }
}
class UserRepository {
    void save(User user) { ... }
}
class EmailService {
    void send(User user) { ... }
}
```

## Composition over Inheritance

```java
// PREFERRED - composition
class Car {
    private Engine engine;
    private Wheels wheels;
    // ...
}

// AVOID - deep inheritance
class Vehicle { }
class Car extends Vehicle { }
class SportsCar extends Car { }  // Problematic
```

## Constants and Enums

```java
// BAD - magic numbers
if (status == 1) { }  // What is 1?

// GOOD - clear intent
enum OrderStatus { PENDING, SHIPPED, DELIVERED }
if (status == OrderStatus.PENDING) { }

// GOOD - constants
class Constants {
    public static final int MAX_RETRY = 3;
    public static final long TIMEOUT_MS = 5000;
}
```