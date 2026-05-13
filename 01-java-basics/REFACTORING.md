# Refactoring Java Basics

## Extract Method

Before:
```java
public void processOrder() {
    // validate order
    if (order.getTotal() <= 0) {
        throw new IllegalArgumentException();
    }
    // calculate shipping
    double shipping = order.getTotal() > 100 ? 0 : 5.99;
    // apply discount
    double discount = order.getTotal() > 500 ? 0.1 : 0;
}
```

After:
```java
public void processOrder() {
    validateOrder(order);
    double shipping = calculateShipping(order);
    applyDiscount(order);
}

private void validateOrder(Order order) {
    if (order.getTotal() <= 0) {
        throw new IllegalArgumentException();
    }
}
```

## Replace Magic Numbers

Before:
```java
if (age > 18) { ... }  // what is 18?
```

After:
```java
private static final int LEGAL_AGE = 18;
if (age > LEGAL_AGE) { ... }
```

## Simplify Boolean Expressions

Before:
```java
if (isActive == true) { ... }
if (isValid != false) { ... }
```

After:
```java
if (isActive) { ... }
if (isValid) { ... }
```

## Use Enhanced For Loop

Before:
```java
for (int i = 0; i < items.size(); i++) {
    System.out.println(items.get(i));
}
```

After:
```java
for (String item : items) {
    System.out.println(item);
}
```

## Extract Variable

Before:
```java
return new Date().getTime() - order.getCreatedAt().getTime();
```

After:
```java
long now = new Date().getTime();
long orderTime = order.getCreatedAt().getTime();
return now - orderTime;
```