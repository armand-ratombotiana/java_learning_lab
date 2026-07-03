# Refactoring: Enums

## Pattern 1: String Constants to Enum

### Before
```java
public static final String STATUS_PENDING = "PENDING";
public static final String STATUS_ACTIVE = "ACTIVE";
public static final String STATUS_COMPLETED = "COMPLETED";

if (status.equals(STATUS_PENDING)) { ... }
```

### After
```java
public enum Status {
    PENDING, ACTIVE, COMPLETED
}

if (status == Status.PENDING) { ... }
```

## Pattern 2: Switch/Case to Enum Method

### Before
```java
String getPlanetType(Planet p) {
    switch (p) {
        case MERCURY: case VENUS: return "Terrestrial";
        case EARTH: case MARS: return "Terrestrial";
        case JUPITER: case SATURN: return "Gas Giant";
        case URANUS: case NEPTUNE: return "Ice Giant";
        default: throw new IllegalArgumentException();
    }
}
```

### After
```java
enum Planet {
    MERCURY("Terrestrial"),
    VENUS("Terrestrial"),
    EARTH("Terrestrial"),
    MARS("Terrestrial"),
    JUPITER("Gas Giant"),
    SATURN("Gas Giant"),
    URANUS("Ice Giant"),
    NEPTUNE("Ice Giant");
    
    final String type;
    Planet(String type) { this.type = type; }
}
```

## Pattern 3: Multiple If-Else to Enum Polymorphism

### Before
```java
double calculate(double a, double b, String op) {
    if (op.equals("+")) return a + b;
    else if (op.equals("-")) return a - b;
    else if (op.equals("*")) return a * b;
    else throw new IllegalArgumentException();
}
```

### After
```java
enum Operation {
    PLUS { double apply(double x, double y) { return x + y; } },
    MINUS { double apply(double x, double y) { return x - y; } },
    TIMES { double apply(double x, double y) { return x * y; } };
    abstract double apply(double x, double y);
}
```

## Pattern 4: Int Constants to Enum with Ordinal

### Before
```java
int MONDAY = 0, TUESDAY = 1, WEDNESDAY = 2;
```

### After
```java
enum Day { MONDAY, TUESDAY, WEDNESDAY }
```

## Pattern 5: Enum for Singleton

### Before (traditional singleton)
```java
class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private AppConfig() {}
    public static AppConfig getInstance() { return INSTANCE; }
}
```

### After
```java
enum AppConfig {
    INSTANCE;
    // fields and methods
}
```
