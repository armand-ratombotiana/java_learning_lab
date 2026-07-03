# Refactoring with Optional

## From Null Checks to Optional

### Before: Defensive null checks
```java
public String getCity(User user) {
    if (user != null) {
        Address address = user.getAddress();
        if (address != null) {
            return address.getCity();
        }
    }
    return "Unknown";
}
```

### After: Optional pipeline
```java
public String getCity(User user) {
    return Optional.ofNullable(user)
        .map(User::getAddress)
        .map(Address::getCity)
        .orElse("Unknown");
}
```

## From Null Returns to Optional Returns

### Before: Null returns
```java
public User findUser(String id) {
    User user = database.find(id);
    return user;  // Might be null!
}

// Callers must check:
User user = findUser(id);
if (user != null) {
    process(user);
}
```

### After: Optional returns
```java
public Optional<User> findUser(String id) {
    return Optional.ofNullable(database.find(id));
}

// Callers chain:
findUser(id).ifPresent(this::process);
```

## From Sentinel Values to Optional

### Before: Sentinel values
```java
public static final int NOT_FOUND = -1;

public int findIndex(String item, List<String> items) {
    for (int i = 0; i < items.size(); i++) {
        if (items.get(i).equals(item)) return i;
    }
    return NOT_FOUND;
}
```

### After: Optional returns
```java
public Optional<Integer> findIndex(String item, List<String> items) {
    for (int i = 0; i < items.size(); i++) {
        if (items.get(i).equals(item)) return Optional.of(i);
    }
    return Optional.empty();
}
```

## From Boolean Flags to Optional

### Before: Boolean flag pattern
```java
public boolean findUser(String id, List<User> result) {
    User user = database.find(id);
    if (user != null) {
        result.add(user);
        return true;
    }
    return false;
}
```

### After: Optional
```java
public Optional<User> findUser(String id) {
    return Optional.ofNullable(database.find(id));
}
```

## From if-else to Functional Chain

### Before: Imperative
```java
public String formatName(User user) {
    String name;
    if (user != null) {
        String display = user.getDisplayName();
        if (display != null && !display.isEmpty()) {
            name = display.trim();
        } else {
            name = user.getUsername();
        }
    } else {
        name = "Anonymous";
    }
    return name.toUpperCase();
}
```

### After: Functional
```java
public String formatName(User user) {
    return Optional.ofNullable(user)
        .map(User::getDisplayName)
        .filter(name -> !name.isBlank())
        .map(String::trim)
        .orElseGet(() -> Optional.ofNullable(user)
            .map(User::getUsername)
            .orElse("Anonymous"))
        .toUpperCase();
}
```

## Migration Checklist

1. **Identify null-returning methods**: Search for methods that return null or have `@Nullable` annotations
2. **Change return types**: Convert to `Optional<T>` return type
3. **Update callers**: Replace null checks with Optional operations
4. **Remove internal null checks**: Simplify method bodies using Optional pipelines
5. **Add orElse/defensive defaults**: Ensure callers handle the empty case
6. **Test both paths**: Always test the present and absent paths
7. **Document**: Remove `@return null` Javadoc (the Optional type is self-documenting)

## Caution: Backward Compatibility

Changing a method's return type from `T` to `Optional<T>` is a breaking API change. All callers must be updated. Consider:
- Adding new Optional-returning methods alongside existing ones
- Deprecating old methods and migrating callers incrementally
- Using static analysis tools to find all call sites
