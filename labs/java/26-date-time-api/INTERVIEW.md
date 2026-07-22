# Interview Questions: Date & Time API

## Company-Specific Focus

### Google
- java.time vs legacy Date/Calendar: why a new API was needed
- Instant, LocalDate, LocalTime, ZonedDateTime: when to use each
- Duration and Period: machine time vs human time representation

### Microsoft
- Java java.time vs .NET DateTime and TimeSpan
- Time Zone handling: ZoneId, ZoneOffset, ZonedDateTime
- Formatting and parsing: DateTimeFormatter design

### Amazon
- Distributed time handling across AWS regions: using Instant for UTC shared timestamps
- Scheduling via the new API: expressing interval in Duration
- Date range queries in DynamoDB using the SDK and ISO-8601

### Meta
- Time zone conversion: storing as UTC and converting to local time
- DST handling: ZonedDateTime vs OffsetDateTime
- The day light savings overlap: choosing early vs late offset

### Apple
- Immutability of java.time types: all are final and thread safe
- Truncation: specifically truncatedTo for time buckets
- Temporal adjusters: firstDayOfMonth, next, etc.

### Oracle
- JSR 310: the design from Stephen Colebourne (Joda-Time creator)
- JLS: The new Date and Time API replaces the old Date and Calendar
- The JVM backing: these are all pure Java with no native code

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 539 Minimum Time Difference | Medium | Amazon, Google, Apple | Convert to minutes since midnight and sort |
| 1185 Day of the Week | Easy | Amazon, Apple, Google | Use LocalDate to get DayOfWeek |
| 1154 Day of the Year | Easy | Google, Microsoft | Using LocalDate.of(YYYY, MM, DD).getDayOfYear() |
| 1360 Number of Days Between Two Dates | Easy | Amazon, Apple | ChronoUnit.DAYS.between |
| 1185 Day of week from date | Easy | Amazon, Google | LocalDate parsing and getDayOfWeek |

## Real Production Scenarios
- **Stripe**: Using the legacy Date class for transaction timestamps caused an off-by-one-hour bug in 30% of US Eastern Time Zone customers due to DST
- **Uber**: Using LocalDateTime instead of ZonedDateTime for trip times resulted in a one-hour difference in all trip data during DST transitions
- **LinkedIn**: The Gregorian calendar for an old crop of user dates gave a wrong age result; refactoring to java.time fixed it

## Interview Patterns & Tips
- **Instant vs ZonedDateTime**: Instant is a machine timestamp; ZonedDateTime is human-readable time
- **Formatting is not thread safe**: The old SimpleDateFormat is not thread safe; DateTimeFormatter is
- **Parse vs parseBest**: Use parseBest when trying to accept a variable format input

## Deep Dive Questions
- **Int representation**: How does LocalDate store internal state? Since Java 9, it uses an int for the day (epoch day)
- **JIT**: Are java.time operations JIT friendly? The classes have small allocations for each value
- **Memory size**: How many bytes the Instant object takes? (two longs and object header)
- **Performance**: How fast is a call to Instant.now()? What is the use of a tick clock?
- **Leap seconds**: Does java.time handle leap seconds?