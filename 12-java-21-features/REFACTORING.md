# Refactoring with Java 21 Features

## Refactoring to Virtual Threads

### Before: Thread Pool
```java
ExecutorService exec = Executors.newFixedThreadPool(100);
for (Task task : tasks) {
    exec.submit(task::execute);
}
exec.shutdown();
```

### After: Virtual Threads
```java
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
for (Task task : tasks) {
    exec.submit(task::execute);
}
exec.shutdown();
```

## Pattern Matching in instanceof

### Before: Manual Cast
```java
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}
```

### After: Pattern Variable
```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

## Record Patterns in Switch

### Before: Nested If
```java
void process(Object o) {
    if (o instanceof Box) {
        Box b = (Box) o;
        if (b.point() instanceof Point p) {
            System.out.println(p.x() + p.y());
        }
    }
}
```

### After: Nested Pattern
```java
void process(Object o) {
    if (o instanceof Box(Point(int x, int y), int w)) {
        System.out.println(x + y + w);
    }
}
```

## String Templates

### Before: Concatenation
```java
String sql = "SELECT * FROM users WHERE name = '" + name + "'";
String msg = "Hello " + user.getName() + ", your score is " + score;
```

### After: Templates
```java
String sql = STR."SELECT * FROM users WHERE name = '\{name}'";
String msg = STR."Hello \{user.getName()}, your score is \{score}";