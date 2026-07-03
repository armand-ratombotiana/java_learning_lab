# Quiz: Date-Time API

## Question 1
Which class represents a date without time or timezone?

A) `Date` B) `LocalDate` C) `LocalDateTime` D) `ZonedDateTime`

## Question 2
What is the month numbering in java.time?

A) 0-11 B) 1-12 C) 1-13 D) 0-12

## Question 3
Which class should you use for timezone-aware date-time operations?

A) `LocalDateTime` B) `ZonedDateTime` C) `OffsetDateTime` D) Both B and C

## Question 4
What does `Duration` measure?

A) Calendar years, months, days B) Seconds and nanoseconds C) Milliseconds only D) Business days

## Question 5
Is `DateTimeFormatter` thread-safe?

A) No B) Yes C) Only for ISO formats D) Only in Java 11+

## Question 6
Which method converts `java.util.Date` to `Instant`?

A) `.toInstant()` B) `.toLocalDate()` C) `.toZonedDateTime()` D) `.asInstant()`

## Question 7
What happens when you call `plusMonths(1)` on January 31?

A) Returns February 28 (or 29 in leap year) B) Throws exception C) Returns March 3 D) Returns January 31

## Question 8
Which `TemporalAdjuster` finds the next Monday?

A) `next(DayOfWeek.MONDAY)` B) `nextMonday()` C) `following(MONDAY)` D) `future(MONDAY)`

## Question 9
What is `Instant` primarily used for?

A) Human-readable date display B) Machine timestamps C) Calendar calculations D) Timezone conversion

## Question 10
Which package contains the modern date-time API?

A) `java.util.date` B) `java.time` C) `java.datetime` D) `org.joda.time`

## Answer Key
1. B, 2. B, 3. D, 4. B, 5. B, 6. A, 7. A, 8. A, 9. B, 10. B
