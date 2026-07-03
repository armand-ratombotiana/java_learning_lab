# Common Mistakes with Date-Time API

## Mistake 1: Using LocalDateTime Instead of ZonedDateTime

```java
// BAD: Loses timezone information
LocalDateTime dt = LocalDateTime.now();
// What timezone? Depends on the JVM!

// GOOD: Use ZonedDateTime for timezone-sensitive operations
ZonedDateTime now = ZonedDateTime.now();  // System timezone
ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
```

## Mistake 2: Assuming LocalDate.parse Accepts Any Format

```java
// BAD: Wrong format
LocalDate.parse("15/03/2024");  // DateTimeParseException!

// GOOD: Use correct format or custom formatter
LocalDate.parse("2024-03-15");  // ISO format (yyyy-MM-dd)
LocalDate.parse("15/03/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
```

## Mistake 3: Forgetting Duration and Period Are Different

```java
// BAD: Using Duration for date-based amounts
Duration oneDay = Duration.ofDays(1);
LocalDate tomorrow = LocalDate.now().plus(oneDay);  // Compiles but wrong!

// GOOD: Use Period for dates
LocalDate tomorrow = LocalDate.now().plus(Period.ofDays(1));

// Or use the specific methods:
LocalDate tomorrow = LocalDate.now().plusDays(1);
```

## Mistake 4: Ignoring DST with Time Arithmetic

```java
// BAD: Adding days might shift timezone offset
ZonedDateTime meeting = ZonedDateTime.of(2024, 3, 8, 10, 0, 0, 0, 
    ZoneId.of("America/New_York"));
ZonedDateTime nextWeek = meeting.plusDays(7);
// 10:00 AM EST → 10:00 AM EDT (same clock time, different offset)

// GOOD: Use withZoneSameInstant to preserve the instant
ZonedDateTime sameInstant = meeting.withZoneSameInstant(ZoneId.of("Europe/London"));
```

## Mistake 5: Using Month Constants Incorrectly

```java
// WRONG (but won't compile — better than legacy API!)
LocalDate.of(2024, 0, 15);  // Month is 1-based, not 0!

// CORRECT
LocalDate.of(2024, Month.JANUARY, 15);
LocalDate.of(2024, 1, 15);  // Month value 1-12
```

## Mistake 6: Not Handling UnsupportedTemporalTypeException

```java
// BAD: ChronoUnit.MONTHS on LocalTime
LocalTime.now().plus(1, ChronoUnit.MONTHS);  // UnsupportedTemporalTypeException!

// GOOD: Check support
if (LocalTime.now().isSupported(ChronoUnit.MONTHS)) {
    // Only for date-based temporals
}
```

## Mistake 7: Forgetting that DateTimeFormatter is Thread-Safe

```java
// BAD: Creating formatter every time (performance)
public String formatDate(LocalDate date) {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return date.format(fmt);
}

// GOOD: Cache the formatter (it's immutable and thread-safe)
private static final DateTimeFormatter FORMATTER = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd");

public String formatDate(LocalDate date) {
    return date.format(FORMATTER);
}
```
