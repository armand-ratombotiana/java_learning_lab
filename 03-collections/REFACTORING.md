# Refactoring Collection Code

## Common Refactoring Patterns

### Replace Array with List
```java
// Before
String[] names = new String[10];
names[0] = "John";

// After
List<String> names = new ArrayList<>();
names.add("John");
```

### Use Diamond Operator
```java
// Before
Map<String, List<Item>> map = new HashMap<String, List<Item>>();

// After
Map<String, List<Item>> map = new HashMap<>();
```

### Chain Stream Operations
```java
// Before
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.startsWith("A")) {
        result.add(s.toUpperCase());
    }
}

// After
List<String> result = list.stream()
    .filter(s -> s.startsWith("A"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### Use Utility Methods
```java
// Before
if (list.contains(item)) { ... }

// After - check existence before adding
if (!list.contains(item)) {
    list.add(item);
}
```

## Code Smells

- Raw type usage
- Returning null instead of empty collection
- Using Arrays.asList() with mutable list
- Modifying collection while iterating