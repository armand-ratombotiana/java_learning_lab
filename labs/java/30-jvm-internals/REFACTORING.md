# Refactoring: JVM Optimization

## Refactoring Pattern 1: Object Elimination
### Before
```java
public long sumItems(List<Integer> items) {
    long sum = 0L;
    for (Integer item : items) {  // Auto-boxing creates Integer objects
        sum += item;              // Unboxing each iteration
    }
    return sum;
}
```
### After
```java
public long sumItems(List<Integer> items) {
    long sum = 0L;
    for (int i = 0; i < items.size(); i++) {
        sum += items.get(i);      // Still has boxing on get()
    }
    return sum;
}
```
### Better
```java
public long sumItems(int[] items) {
    long sum = 0L;
    for (int item : items) {      // No boxing at all
        sum += item;
    }
    return sum;
}
```

## Refactoring Pattern 2: Reduce Object Allocation
### Before
```java
String result = "";
for (String s : items) {
    result += s;  // Creates new StringBuilder each iteration
}
```
### After
```java
StringBuilder sb = new StringBuilder(items.size() * 10);
for (String s : items) {
    sb.append(s);
}
String result = sb.toString();
```

## Refactoring Pattern 3: Lazy Initialization
### Before
```java
class ExpensiveResource {
    private final DatabaseConnection conn = new DatabaseConnection();
}
```
### After
```java
class ExpensiveResource {
    private DatabaseConnection conn;  // Lazy, avoid if unused
    public Connection getConnection() {
        if (conn == null) conn = new DatabaseConnection();
        return conn;
    }
}
```

## Refactoring Pattern 4: Avoid Megamorphic Calls
### Before
```java
void process(Shape shape) {
    shape.draw();  // Could be megamorphic with 3+ Shape types
}
```
### After
```java
void process(Circle circle) {
    circle.draw();   // Monomorphic — JIT can inline
}
void process(Square square) {
    square.draw();   // Monomorphic — JIT can inline
}
```
