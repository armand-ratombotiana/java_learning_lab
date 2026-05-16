# Refactoring to Functional Programming

## Loop to Stream

### Before: Imperative
```java
List<String> result = new ArrayList<>();
for (User user : users) {
    if (user.isActive() && user.getAge() > 18) {
        result.add(user.getName().toUpperCase());
    }
}
Collections.sort(result);
```

### After: Functional
```java
List<String> result = users.stream()
    .filter(User::isActive)
    .filter(u -> u.getAge() > 18)
    .map(User::getName)
    .map(String::toUpperCase)
    .sorted()
    .collect(Collectors.toList());
```

## Extract to Method Reference

### Before: Lambda
```java
list.stream()
    .map(s -> s.toLowerCase())
    .collect(Collectors.toList());
```

### After: Method Reference
```java
list.stream()
    .map(String::toLowerCase)
    .collect(Collectors.toList());
```

## Method Chaining

### Before: Separate Operations
```java
List<Integer> numbers = getNumbers();
numbers = filterPositive(numbers);
numbers = doubleValues(numbers);
numbers = removeDuplicates(numbers);
```

### After: Pipeline
```java
List<Integer> result = getNumbers().stream()
    .filter(n -> n > 0)
    .map(n -> n * 2)
    .distinct()
    .collect(Collectors.toList());
```

## Extract Helper Functions

### Before: Complex Lambda
```java
list.stream()
    .map(s -> {
        if (s == null) return "";
        s = s.trim();
        return s.isEmpty() ? "unknown" : s.substring(0, 1).toUpperCase() + s.substring(1);
    })
```

### After: Named Function
```java
list.stream()
    .map(this::formatName)
    
// where formatName is:
private String formatName(String s) {
    if (s == null) return "";
    s = s.trim();
    return s.isEmpty() ? "unknown" : 
        s.substring(0, 1).toUpperCase() + s.substring(1);
}
```

## When NOT to Refactor

- When code is clearer as loops
- Complex multi-step logic
- When debugging breakpoints matter
- Performance-critical inner loops