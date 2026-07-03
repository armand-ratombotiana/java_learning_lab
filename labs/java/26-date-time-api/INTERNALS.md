# Internal Implementation of Date-Time API

## Internal Representation

### LocalDate
Stored as a single `int` field representing the Epoch Day (days since 1970-01-01). Range: about -999,999,999 to +999,999,999 days. The year, month, day are computed on demand.

### LocalTime
Stored as a single `long` field representing nanoseconds since midnight. Range: 0 to 86,399,999,999,999 ns (24 hours).

### LocalDateTime
Stored as a pair: `LocalDate date + LocalTime time`. No additional state.

### ZonedDateTime
Stored as `LocalDateTime dateTime + ZoneOffset offset + ZoneId zone`. The offset is cached for performance, but the zone rules are used to resolve DST transitions.

### Instant
Stored as two `long` fields: seconds since epoch + nanoseconds. Range: ±292 billion years.

### Duration
Stored as two `long` fields: seconds + nanoseconds.

### Period
Stored as three `int` fields: years + months + days.

## Performance Optimizations

### Caching
- `LocalDate` uses a cache for common years (roughly -999 to +999)
- `LocalTime` caches instances for common times (midnight, noon, etc.)
- `Month`, `DayOfWeek` are enums (value-cached)

### Serialization
All classes implement `Serializable`. The serialized form is compact:
- `LocalDate`: day epoch (int)
- `LocalTime`: nanosecond of day (long)
- `Instant`: seconds + nanos (two longs)

### Calendar System
The API uses an internal "proleptic" calendar that extends the Gregorian calendar backward before 1582 (the year the Gregorian calendar was adopted). This is different from the legacy API which used the hybrid Julian/Gregorian system.

## Timezone Data

The timezone data comes from the IANA Time Zone Database (tzdata), integrated via Unicode CLDR. The data is stored in the JRE's lib/tzdb.dat file and can be updated independently of the JDK version.

## Leap Seconds

The API does not handle leap seconds. It uses the "smoothed" UTC scale where a day is always exactly 86,400 SI seconds. Leap seconds are ignored (they're absorbed into the following day).
