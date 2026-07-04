# Refactoring Arithmetic Code

## Extract Magic Numbers

```java
// BEFORE
double total = price * 0.07 + price;

// AFTER
private static final double SALES_TAX_RATE = 0.07;
double total = price * (1 + SALES_TAX_RATE);
```

## Replace Repeated Computation with Variables

```java
// BEFORE
double area = Math.PI * radius * radius;
double circumference = 2 * Math.PI * radius;

// AFTER
double pi = Math.PI;
double area = pi * radius * radius;
double circumference = 2 * pi * radius;
```

## Use Descriptive Methods

```java
// BEFORE
double x = (a * b) / (c - d);

// AFTER
public double weightedAverage(double value, double weight, double total, double discount) {
    return (value * weight) / (total - discount);
}
```

## Replace Primitive Obsession

```java
// BEFORE
void processPayment(int cents) { ... }

// AFTER
record Money(long cents) {
    Money add(Money other) { return new Money(this.cents + other.cents); }
}
```
