# Flashcards: Date-Time API

## Card 1
**Q**: What is LocalDate?
**A**: An immutable date-time object representing a date (year-month-day) without time or timezone.

## Card 2
**Q**: What is ZonedDateTime?
**A**: A date-time with full timezone rules, handling DST transitions.

## Card 3
**Q**: What is Instant?
**A**: A point on the UTC timeline, stored as seconds + nanoseconds since epoch.

## Card 4
**Q**: What is the difference between Duration and Period?
**A**: Duration measures seconds/nanos (machine time). Period measures years/months/days (human time).

## Card 5
**Q**: How do you format a date?
**A**: `date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))`

## Card 6
**Q**: Is DateTimeFormatter thread-safe?
**A**: Yes. Cache formatters as constants.

## Card 7
**Q**: How do you parse a date string?
**A**: `LocalDate.parse("2024-03-15")` or with a custom formatter.

## Card 8
**Q**: What TemporalAdjuster finds the last day of month?
**A**: `TemporalAdjusters.lastDayOfMonth()`

## Card 9
**Q**: How do you add days to a date?
**A**: `date.plusDays(5)` or `date.plus(Period.ofDays(5))`

## Card 10
**Q**: How do you convert between timezones?
**A**: `zdt.withZoneSameInstant(ZoneId.of("Europe/London"))`

## Card 11
**Q**: What is ChronoUnit?
**A**: A standard set of date/time units (DAYS, HOURS, MINUTES, etc.) for measurement.

## Card 12
**Q**: How do you calculate days between dates?
**A**: `ChronoUnit.DAYS.between(start, end)`

## Card 13
**Q**: What package is the modern date-time API in?
**A**: `java.time`

## Card 14
**Q**: Is java.time immutable?
**A**: Yes. All core classes are immutable and thread-safe.

## Card 15
**Q**: How do you handle DST transitions?
**A**: ZonedDateTime handles DST automatically. Use `withZoneSameInstant()` for conversions.

## Card 16
**Q**: What is the ISO date format?
**A**: `yyyy-MM-dd` (e.g., 2024-03-15)

## Card 17
**Q**: How do you get the current date?
**A**: `LocalDate.now()`

## Card 18
**Q**: What is Clock used for?
**A**: A testable source of current time. Use `Clock.fixed()` for testing.

## Card 19
**Q**: How do you convert java.util.Date to java.time?
**A**: `date.toInstant().atZone(ZoneId.systemDefault())`

## Card 20
**Q**: What does `Month` represent?
**A**: An enum of the 12 months (JANUARY through DECEMBER), 1-based.
