# Mathematical Foundations of Date-Time API

## Time Arithmetic

### Proleptic Gregorian Calendar
The API uses the proleptic Gregorian calendar, which extends the Gregorian calendar rules (leap years: divisible by 4, except centuries not divisible by 400) backward before 1582.

### Leap Year Rule
```
leap(year) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
```

### Day of Year Calculation
```
dayOfYear(year, month, day) = cumulativeDays[month - 1] + day + (leap(year) && month > 2 ? 1 : 0)
```

### Epoch Day Calculation
Days since 1970-01-01, where 1970-01-01 = epoch day 0.

## Duration Mathematics

Duration stores seconds + nanoseconds in the range ±Long.MAX_VALUE seconds:
```
duration = seconds + nanos / 1_000_000_000.0
```

Operations use normal arithmetic with overflow checking.

## Period Mathematics

Period stores years, months, days as three separate ints. Adding a period to a date:
1. Add years (reducing to valid day if month is February 29 in non-leap year)
2. Add months (similarly adjusting day)
3. Add days (no adjustment needed)

## Timezone Offset Calculation

The UTC offset for a given zoned date-time is computed from the timezone rules:
1. Get the zone rules for the ZoneId
2. Find the offset at the given instant
3. If the date-time is in a DST gap (spring forward), resolve forward to the next valid time
4. If in a DST overlap (fall back), usually pick the earlier offset (summer time)

## ChronoUnit Operations

The `ChronoUnit` enum defines time units as a mathematical calculation:
```
ChronoUnit.DAYS.between(start, end) = epochDay(end) - epochDay(start)
ChronoUnit.HOURS.between(start, end) = (epochSecond(end) - epochSecond(start)) / 3600
```

These calculations are exact for supported types, throwing `UnsupportedTemporalTypeException` for unsupported combinations.
