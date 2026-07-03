# Step-by-Step: Using Enums

## Step 1: Basic Enum
```java
public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

Day today = Day.MONDAY;
System.out.println(today);           // MONDAY
System.out.println(today.ordinal()); // 0
System.out.println(today.name());    // "MONDAY"
```

## Step 2: Enum with Fields and Constructor
```java
public enum Planet {
    MERCURY(3.303e23, 2.4397e6),
    VENUS(4.869e24, 6.0518e6),
    EARTH(5.976e24, 6.37814e6),
    MARS(6.421e23, 3.3972e6),
    JUPITER(1.9e27, 7.1492e7),
    SATURN(5.688e26, 6.0268e7),
    URANUS(8.686e25, 2.5559e7),
    NEPTUNE(1.024e26, 2.4746e7);

    private final double mass;
    private final double radius;
    
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    
    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }
}

Planet earth = Planet.EARTH;
System.out.println(earth.surfaceGravity());
```

## Step 3: Switch on Enum
```java
switch (day) {
    case MONDAY -> System.out.println("Start of work week");
    case FRIDAY -> System.out.println("End of work week");
    case SATURDAY, SUNDAY -> System.out.println("Weekend!");
    default -> System.out.println("Midweek");
}
```

## Step 4: Enum with Abstract Method
```java
public enum Operation {
    PLUS { double apply(double x, double y) { return x + y; } },
    MINUS { double apply(double x, double y) { return x - y; } },
    TIMES { double apply(double x, double y) { return x * y; } },
    DIVIDE { double apply(double x, double y) { return x / y; } };
    
    abstract double apply(double x, double y);
}
```

## Step 5: EnumSet and EnumMap
```java
EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
EnumSet<Day> all = EnumSet.allOf(Day.class);
EnumSet<Day> none = EnumSet.noneOf(Day.class);

EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
schedule.put(Day.MONDAY, "Meeting");
schedule.put(Day.FRIDAY, "Review");
```
