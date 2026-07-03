# Interview Questions: Date-Time API

## Q1: What problems did the java.time API solve?
It replaced the flawed `java.util.Date` and `java.util.Calendar` classes. Key improvements: immutable and thread-safe, clear separation of concerns (date vs time vs datetime vs zoned), ISO-8601 compliance, fluent API, proper DST handling, and thread-safe formatting.

## Q2: Explain the difference between LocalDate, LocalDateTime, and ZonedDateTime.
LocalDate is a date without time (e.g., 2024-03-15). LocalDateTime adds time but no timezone (e.g., 2024-03-15T14:30). ZonedDateTime adds timezone rules (e.g., 2024-03-15T14:30-04:00[America/New_York]). Use LocalDate for birthdays, ZonedDateTime for appointments, LocalDateTime as intermediate representation.

## Q3: How does the API handle DST transitions?
ZonedDateTime uses the ZoneId's rules to handle DST. When adding days across DST transitions, the clock time is preserved (e.g., 10 AM stays 10 AM). When converting between timezones, the instant is preserved. The API also handles gaps (spring forward) and overlaps (fall back) by resolving to the valid offset.

## Q4: What is the difference between Duration and Period?
Duration is time-based (seconds, nanoseconds) for machine time. Period is date-based (years, months, days) for human time. Duration.ofDays(1) is always 24 hours. Period.ofDays(1) can be 23, 24, or 25 hours on DST transition days.

## Q5: How is DateTimeFormatter different from SimpleDateFormat?
DateTimeFormatter is immutable and thread-safe. SimpleDateFormat is not thread-safe. DateTimeFormatter uses pattern letters similar to SimpleDateFormat but has more consistent behavior and better error messages. It also provides predefined ISO formatters and localized formatters.

## Q6: Explain the hierarchy of the java.time package.
Core classes: `Instant` (machine timestamp), `LocalDate`/`LocalTime`/`LocalDateTime` (local), `ZonedDateTime`/`OffsetDateTime` (zoned). Amounts: `Duration` (time), `Period` (date). Formatting: `DateTimeFormatter`. Adjusters: `TemporalAdjusters`. Units: `ChronoUnit`. All implement `Temporal` or `TemporalAccessor`.

## Q7: How do you test code that uses java.time?
Use `Clock` to inject time. `Clock.fixed()` provides a fixed instant for deterministic tests. `Clock.offset()` shifts the clock. The `now()` methods are overloaded to accept a `Clock` parameter.

## Q8: What is the proleptic Gregorian calendar?
The java.time API extends Gregorian calendar rules backward before 1582 (when the Gregorian calendar was adopted). This is called the "proleptic" Gregorian calendar. It differs from the legacy `GregorianCalendar` which used a hybrid Julian/Gregorian system.

## Q9: How do you handle leap seconds?
The API does not handle leap seconds. It uses "smoothed" UTC where a day is always exactly 86,400 seconds. Leap seconds are absorbed into the following day's first second.

## Q10: How do you calculate age in years?
`Period.between(birthDate, today).getYears()`. This handles leap years correctly. Don't use `ChronoUnit.YEARS.between()` which gives a simpler but less accurate calculation.
