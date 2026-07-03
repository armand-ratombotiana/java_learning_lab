# Security Considerations for Date-Time API

## Input Validation
Always validate date/time inputs from untrusted sources:
```java
public LocalDate safeParse(String input) {
    try {
        return LocalDate.parse(input);
    } catch (DateTimeParseException e) {
        throw new SecurityException("Invalid date input");
    }
}
```

## Timezone Injection
Avoid using user-provided timezone IDs without validation:
```java
if (!ZoneId.getAvailableZoneIds().contains(userInput)) {
    throw new SecurityException("Invalid timezone");
}
ZoneId zone = ZoneId.of(userInput);
```

## Denial of Service
Parsing extremely large/small dates (e.g., year ±999999999) can consume memory. Validate range:
```java
LocalDate date = LocalDate.parse(input);
if (date.isBefore(LocalDate.of(1900, 1, 1)) || 
    date.isAfter(LocalDate.of(2100, 1, 1))) {
    throw new SecurityException("Date out of allowed range");
}
```

## Thread Safety
Unlike `SimpleDateFormat` and `Calendar`, `java.time` classes are thread-safe. However, the values they contain might be sensitive (e.g., birth dates, appointment times). Be careful not to expose these in logs or error messages.
