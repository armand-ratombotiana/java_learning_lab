# Theory: Date-Time API

## Design Principles

The `java.time` API was designed around several key principles:

### Immutability
All core classes (`LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`, `Instant`, `Duration`, `Period`) are immutable. Operations return new instances rather than modifying existing ones. This makes the API thread-safe and free from side effects.

```java
LocalDate date = LocalDate.of(2024, 1, 15);
LocalDate nextWeek = date.plusWeeks(1);  // date is unchanged
```

### Separation of Concerns
The API separates different concepts:
- **Human time vs. machine time**: `LocalDate`/`LocalTime` for human-readable dates; `Instant` for timestamps
- **Date vs. time vs. date-time**: Separate classes for dates, times, and combined date-times
- **With timezone vs. without**: `LocalDate` has no timezone; `ZonedDateTime` has full timezone support

### Fluent API
Methods return new instances with the modified value, enabling method chaining:

```java
ZonedDateTime meeting = ZonedDateTime.of(2024, 3, 15, 14, 30, 0, 0, ZoneId.of("America/New_York"))
    .plusDays(1)
    .withHour(10);
```

### Null Safety
The API uses `Objects.requireNonNull()` for parameters and never returns null from standard operations.

## Core Classes Hierarchy

### Time Classes
- **Instant**: A point on the UTC timeline (nanosecond precision)
- **LocalDate**: A date without time or timezone (ISO-8601)
- **LocalTime**: A time without date or timezone
- **LocalDateTime**: A date-time without timezone
- **OffsetTime**: A time with UTC offset
- **OffsetDateTime**: A date-time with UTC offset
- **ZonedDateTime**: A date-time with full timezone rules (handles DST)
- **Year/Month/DayOfMonth**: Partial date components

### Duration and Period
- **Duration**: A time-based amount (seconds, nanoseconds)
- **Period**: A date-based amount (years, months, days)

### Formatting and Parsing
- **DateTimeFormatter**: Format dates/times to strings and parse strings to dates/times
- **DateTimeFormatterBuilder**: Build custom formatters

### Temporal Adjusters
- **TemporalAdjusters**: Static methods for common adjustments (next Monday, last day of month, etc.)
- **TemporalAdjuster**: Interface for custom adjustments

## ISO-8601 Standard

The API is based on the ISO-8601 international standard:
- Dates: `YYYY-MM-DD` (e.g., 2024-03-15)
- Times: `HH:mm:ss.SSS` (e.g., 14:30:00.000)
- Date-times: `YYYY-MM-DDTHH:mm:ss` (e.g., 2024-03-15T14:30:00)
- Zoned: Appends zone offset (e.g., 2024-03-15T14:30:00-04:00)
- Instant: Appends 'Z' for UTC (e.g., 2024-03-15T18:30:00Z)

## Timezone Handling

Timezones are handled through:
- **ZoneId**: A timezone identifier (e.g., "America/New_York")
- **ZoneOffset**: A fixed offset from UTC (e.g., -05:00)
- **ZoneRules**: Rules for a timezone's offset and DST transitions

## Legacy Interoperability

The API provides conversion methods:
- `Date.from(Instant)` and `Date.toInstant()` for java.util.Date
- `GregorianCalendar.from(ZonedDateTime)` and `ZonedDateTime.toGregorianCalendar()`
- `SimpleDateFormat` can still be used but DateTimeFormatter is preferred
