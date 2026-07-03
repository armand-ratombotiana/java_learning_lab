# How Enums Work

## Basic Enum Declaration
```java
public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
```

## Enum with Fields and Methods
```java
public enum Planet {
    MERCURY(3.303e23, 2.4397e6),
    VENUS(4.869e24, 6.0518e6),
    EARTH(5.976e24, 6.37814e6);
    
    private final double mass;  // kg
    private final double radius;  // m
    
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    
    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }
}
```

## Behavior-Driven Enum
```java
public enum Operation {
    PLUS { public double apply(double x, double y) { return x + y; } },
    MINUS { public double apply(double x, double y) { return x - y; } },
    TIMES { public double apply(double x, double y) { return x * y; } },
    DIVIDE { public double apply(double x, double y) { return x / y; } };
    
    public abstract double apply(double x, double y);
}
```

## EnumSet and EnumMap Usage
```java
EnumSet<DayOfWeek> weekend = EnumSet.of(SATURDAY, SUNDAY);
EnumMap<Planet, Double> masses = new EnumMap<>(Planet.class);
masses.put(Planet.EARTH, 5.976e24);
```

## Enum in Switch
```java
switch (day) {
    case MONDAY, TUESDAY -> System.out.println("Start of week");
    case FRIDAY -> System.out.println("TGIF");
    case SATURDAY, SUNDAY -> System.out.println("Weekend");
}
```
