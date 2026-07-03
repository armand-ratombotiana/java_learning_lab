# Debugging Date-Time API

## Common Exceptions

### DateTimeParseException
Occurs when parsing a string that doesn't match the expected format. The exception message shows the failed input and position. **Fix**: Ensure the string matches the format pattern, or use a custom formatter.

### UnsupportedTemporalTypeException
Occurs when using a unit that doesn't apply to the temporal type (e.g., MONTHS on LocalTime). **Fix**: Check `isSupported()` before using `plus()`, `minus()`, or `with()`.

### DateTimeException
General date-time exception for invalid values (e.g., February 30). **Fix**: Validate date components before creation.

## Debugging Strategies

### Verify Timezone Conversions
```java
ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
ZonedDateTime local = utc.withZoneSameInstant(ZoneId.systemDefault());
System.out.println("UTC: " + utc + ", Local: " + local);
```

### Check for DST Issues
```java
// Print the offset to see DST status
ZoneId zone = ZoneId.of("America/New_York");
System.out.println(zone.getRules().getOffset(Instant.now()));
```

### Use Predefined Formatters for Debugging
```java
System.out.println("ISO: " + dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
System.out.println("Full: " + dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
```

## IDE Support

IntelliJ IDEA and Eclipse both provide:
- Inline display of LocalDate/LocalDateTime values during debugging
- Date picker UI for date-related fields
- Formatting hints for temporal values

## Testing Date-Time Code

```java
// Test with fixed clock
Clock fixedClock = Clock.fixed(Instant.parse("2024-03-15T10:00:00Z"), ZoneOffset.UTC);
LocalDate testDate = LocalDate.now(fixedClock);
LocalTime testTime = LocalTime.now(fixedClock);
```
