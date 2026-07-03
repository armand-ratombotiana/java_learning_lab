# Debugging — Java Streams

## Using `peek()`
```java
List<String> result = words.stream()
    .filter(w -> w.length() > 3)
    .peek(w -> System.out.println("After filter: " + w))
    .map(String::toUpperCase)
    .peek(w -> System.out.println("After map: " + w))
    .collect(Collectors.toList());
```

## IDE Stream Debugger
IntelliJ IDEA supports "Trace Current Stream Chain" — click the gutter icon on a stream call to visualise each element.

## Breakpoint Conditions
Place method-reference breakpoints on lambdas:
```java
list.stream()
   .filter(s -> s.startsWith("A")) // breakpoint here
   .collect(Collectors.toList());
```

## Logging Intermediate State
```java
long count = list.stream()
    .filter(s -> s.length() > 5)
    .map(String::trim)
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        l -> { System.out.println("Debug: " + l); return l.stream(); }
    ))
    .count();
```

## Stack Traces in Parallel Streams
Parallel stream errors are wrapped in `CompletionException` — check `getCause()`.
