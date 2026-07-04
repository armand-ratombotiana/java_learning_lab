# Refactoring Trigonometry Code

## Extract Angle Conversions

```java
// BEFORE
double x = Math.sin(angle * 180 / Math.PI); // confusing

// AFTER
double x = Math.sin(toRadians(angleInDegrees));
```

## Use Records for Angles

```java
public record Radians(double value) {
    public Radians {
        value = normalize(value);
    }
    public static Radians fromDegrees(double deg) {
        return new Radians(deg * Math.PI / 180);
    }
    private static double normalize(double theta) {
        theta = theta % (2 * Math.PI);
        return theta >= 0 ? theta : theta + 2 * Math.PI;
    }
    public double sin() { return Math.sin(value); }
    public double cos() { return Math.cos(value); }
}
```

## Replace Raw Trig with Domain-Specific API

```java
// BEFORE
double dx = p2.x() - p1.x();
double dy = p2.y() - p1.y();
double angle = Math.atan2(dy, dx);
double dist = Math.sqrt(dx * dx + dy * dy);

// AFTER
Vector v = Vector.between(p1, p2);
double angle = v.angle();
double dist = v.magnitude();
```
