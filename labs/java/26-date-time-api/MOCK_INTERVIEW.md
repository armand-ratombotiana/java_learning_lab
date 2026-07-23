# Mock Interview Transcript: Date & Time API

## Interviewer: Senior SWE, Microsoft
## Candidate: Mid-level Java developer
## Time: 30 minutes
## Focus: java.time, time zones, formatting, legacy migration

---

**Q1: What's wrong with `java.util.Date` and `Calendar`?**

**Candidate**: (1) Date is mutable — breaks in collections, can't be used safely in concurrent code. (2) Months are 0-indexed (January = 0). (3) Year is 1900-based for Date constructor. (4) Calendar mixes date and time concepts. (5) Both are confusing and error-prone. (6) Thread safety issues.

**Interviewer**: Compare `LocalDate`, `LocalTime`, `LocalDateTime`, and `ZonedDateTime`.

**Candidate**: `LocalDate` — date without time or timezone (e.g., birthday). `LocalTime` — time without date or timezone (e.g., store opening hours). `LocalDateTime` — date and time without timezone (combines the two). `ZonedDateTime` — date and time with timezone (e.g., meeting time that persists across DST changes). `OffsetDateTime` — date/time with UTC offset (for database timestamps).

**Interviewer**: How does `ZoneId` handle daylight saving time?

**Candidate**: `ZoneId` represents a timezone region like "America/New_York". `ZonedDateTime` adjusts for DST automatically. However, during DST transitions, ambiguous times can occur. The `ZonedDateTime.withEarlierOffsetAtOverlap()` and `.withLaterOffsetAtOverlap()` resolve ambiguity.

**Interviewer**: Parse "2024-03-15T10:30:00Z" into a Java time object.

**Candidate**: 
```java
Instant instant = Instant.parse("2024-03-15T10:30:00Z");
// Or:
ZonedDateTime zdt = ZonedDateTime.parse("2024-03-15T10:30:00Z");
```

**Interviewer**: How would you format a date for different locales?

**Candidate**: 
```java
DateTimeFormatter usFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
    .withLocale(Locale.US);
DateTimeFormatter deFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
    .withLocale(Locale.GERMANY);

ZonedDateTime now = ZonedDateTime.now();
System.out.println(now.format(usFormat));  // Friday, March 15, 2024
System.out.println(now.format(deFormat));  // Freitag, 15. März 2024
```

**Interviewer**: How does `Duration` differ from `Period`?

**Candidate**: `Duration` is for time-based (hours, minutes, seconds, nanos) — works with LocalTime, Instant. `Period` is for date-based (years, months, days) — works with LocalDate. Duration measures exact nanoseconds; Period accounts for calendar variations (months have different lengths).

**Interviewer**: Calculate someone's age from their birth date.

**Candidate**: 
```java
int calculateAge(LocalDate birthDate) {
    return Period.between(birthDate, LocalDate.now()).getYears();
}
```

**Interviewer**: How would you find the next Friday the 13th?

**Candidate**: 
```java
LocalDate findNextFriday13th() {
    LocalDate date = LocalDate.now();
    while (true) {
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY && date.getDayOfMonth() == 13) {
            return date;
        }
        date = date.plusDays(1);
    }
}
```
Or use a `YearMonth` approach to skip to first of each month.

**Interviewer**: How does TemporalAdjuster work?

**Candidate**: `TemporalAdjusters` provides common adjustments: `next(DayOfWeek)`, `previousOrSame(DayOfWeek)`, `lastDayOfMonth()`, `firstDayOfYear()`. Custom adjusters implement `TemporalAdjuster` functional interface:
```java
TemporalAdjuster nextFriday13th = temporal -> {
    // custom logic
};
```

**Interviewer**: Final: Migration strategy from `java.util.Date` to `java.time`.

**Candidate**: (1) Add `java.time` alongside legacy — gradual migration. (2) Convert legacy ↔ modern via: `Date.toInstant()`, `Date.from(Instant)`, `Calendar.toInstant()`. (3) For database: use `java.time` types with JDBC 4.2+ (`setObject`, `getObject`). (4) For serialization: serialize as Instant (epoch millis or ISO string). (5) JPA 2.2+ supports `java.time`. (6) Jackson module `jackson-datatype-jsr310` for JSON.

---

## Feedback

**Strengths**:
- Comprehensive java.time types knowledge
- Correct locale formatting
- Understands Duration vs Period
- Clear migration strategy from legacy

**Areas for Improvement**:
- Could discuss `Clock` for testing (inject Clock, use fixed clocks)
- Mention `YearMonth` and `MonthDay` for specific use cases

**Score**: 4/5 — Strong date/time API knowledge
