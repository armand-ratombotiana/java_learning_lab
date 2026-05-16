# How Java 21 Features Work

## Virtual Threads

### Creation
```java
// Create virtual thread
Thread.ofVirtual().start(() -> System.out.println("Virtual!"));

// With name
Thread.ofVirtual().name("worker-1").start(task);

// In thread pool (ExecutorService)
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> process(request));
```

### Carrier Threads
- Virtual threads run on carrier threads (platform threads)
- When virtual thread blocks, carrier runs another
- OS schedules carrier threads, JVM manages virtual threads

## Sequenced Collections

```java
List<String> list = new ArrayList<>();
list.addFirst("first");
list.addLast("last");

String first = list.getFirst();
String last = list.getLast();

// Reverse view
SequencedCollection<String> reversed = list.reversed();
```

## Record Patterns

```java
record Point(int x, int y) {}
record Box(Point p, int width) {}

Object obj = new Box(new Point(1, 2), 10);

// Type + position extraction
if (obj instanceof Box(Point(int x, int y), int w)) {
    System.out.println(x + y + w);
}
```

## Pattern Matching for Switch

```java
static String formatter(Object obj) {
    return switch(obj) {
        case null -> "null";
        case String s -> "String: " + s;
        case Integer i -> "int: " + i;
        case Double d && d > 0 -> "positive double: " + d;
        default -> "unknown";
    };
}
```

## String Templates

```java
// Enable preview feature
import static java.StringTemplate.STR;

// Basic
String name = "Ada";
String greeting = STR."Hello \{name}!";  // "Hello Ada!"

// Expressions
int a = 5, b = 10;
String math = STR."\{a} + \{b} = \{a + b}";  // "5 + 10 = 15"

// Multi-line
String html = STR."""
    <div>
        <h1>Hello</h1>
    </div>
    """;