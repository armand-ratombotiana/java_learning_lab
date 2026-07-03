# Mental Models for Date-Time API

## The Calendar Wall Model

Think of `LocalDate` as a **wall calendar**. Each day is a separate square on the calendar:
- You can look at today's date (`LocalDate.now()`)
- You can flip forward or backward (`plusDays(1)`, `minusMonths(1)`)
- The calendar has no concept of time (no hours, minutes)
- Changing the calendar creates a new calendar page (immutability)

`LocalTime` is a **wall clock** — it tracks hours, minutes, seconds independently of any particular day.

`LocalDateTime` is the wall calendar with a clock placed on top — you can see both the date square and the current time.

## The Machine Clock Model

Think of `Instant` as a **machine's internal clock counter** — a monotonically increasing number of nanoseconds since January 1, 1970, 00:00:00 UTC (the Unix epoch). It's what computers use internally to measure time. It has no concept of timezones, daylight saving, or human calendar conventions.

```
Instant.now() → 1700000000.123456789 seconds since epoch
```

## The Timezone Overlay Model

`ZonedDateTime` is like a `LocalDateTime` viewed through a **colored filter** (the timezone). The same moment in time looks different through different filters:
- New York filter: 2024-03-15 10:30 AM
- London filter: 2024-03-15 2:30 PM
- Tokyo filter: 2024-03-15 11:30 PM

The filter handles DST transitions automatically. When clocks spring forward, the filter smoothly adjusts (the hour is "lost" but the Instant remains continuous).

## The Duration vs. Period Model

**Duration** is like a **stopwatch**: it measures elapsed time in seconds and nanoseconds, regardless of calendar irregularities.
- 24 hours of elapsed time
- Used for machine time intervals

**Period** is like a **calendar span**: it measures time in years, months, and days, which vary in length.
- 1 calendar month (28-31 days)
- Used for human time intervals

```java
Duration.ofHours(24)  // Always 24 hours
Period.ofDays(1)      // Usually 24 hours, but 23 or 25 on DST transition days
```

## The Formatter Parser Model

`DateTimeFormatter` is like a **stencil** that shows how to write a date as text or read text as a date. The stencil has holes (format patterns) that define what parts of the date appear:
- `"yyyy-MM-dd"` → stencil that shows year, month, day with dashes
- `"dd/MM/yyyy"` → stencil that shows day, month, year with slashes
- `"EEEE, MMMM d"` → stencil that shows "Friday, March 15"

## The Adjuster Model

A `TemporalAdjuster` is like a **knob** on a calendar machine that automatically moves the date to a specific position:
- "Next Monday" knob: From any date, jump to the next Monday
- "Last day of month" knob: From any date, jump to the last day of that month
- "First day of year" knob: Jump to January 1

You can create custom knobs for business rules:
- "Next business day" knob
- "First Friday of the month" knob
- "Quarter end" knob

## The Local vs. Zoned Distinction

**Local** means "relative to the observer": if two people in different timezones both say "2024-03-15 at 10:00 AM," they mean different moments. Local types represent the human-readable time on a clock in some unspecified location.

**Zoned** means "absolute time anchored to a specific location": "2024-03-15 at 10:00 AM in New York" is a specific moment that can be converted to any other timezone.

The mental model: `LocalDateTime` is "what the clock says." `ZonedDateTime` adds "which clock."
