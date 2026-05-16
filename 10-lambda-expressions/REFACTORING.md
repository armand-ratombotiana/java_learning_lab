# Refactoring to Lambda Expressions

## Common Refactoring Patterns

### 1. Anonymous Class to Lambda

Before:
```java
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running");
    }
};
```

After:
```java
Runnable r = () -> System.out.println("Running");
```

### 2. Loop to Stream

Before:
```java
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.length() > 3) {
        result.add(s.toUpperCase());
    }
}
```

After:
```java
List<String> result = list.stream()
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### 3. Extract to Method Reference

Before:
```java
list.stream()
    .map(s -> s.toLowerCase())
```

After:
```java
list.stream()
    .map(String::toLowerCase)
```

### 4. Composing Functions

Before:
```java
public String process(String input) {
    String trimmed = input.trim();
    String upper = trimmed.toUpperCase();
    return upper.replace("X", "Y");
}
```

After:
```java
Function<String, String> process = 
    String::trim
        .andThen(String::toUpperCase)
        .andThen(s -> s.replace("X", "Y"));
```

## When NOT to Use Lambdas

- Complex multi-line logic is clearer as named method
- When stack trace readability matters (lambdas have cryptic names)
- When debugging breakpoints need to be set
- Complex branching logic may be clearer as traditional code