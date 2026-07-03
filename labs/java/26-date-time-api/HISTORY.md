# History of the Date-Time API

## The Legacy Era

### Java 1.0 (1996) — java.util.Date
The original `java.util.Date` class was designed with serious flaws:
- Based on Unix's `time_t` representation
- Year 1900 offset (year = current - 1900)
- Zero-based months (0 = January)
- Mutable (setters modify the object)
- toString() uses local timezone but Date is always UTC
- No formatting/parsing built in

### Java 1.1 (1997) — java.util.Calendar
Recognizing the problems with Date, Java 1.1 introduced:
- `Calendar` abstract class with `GregorianCalendar` implementation
- `DateFormat` and `SimpleDateFormat` for formatting
- Timezone support via `TimeZone` and `SimpleTimeZone`

However, Calendar introduced its own problems:
- Complex API with integer field constants
- Still mutable
- Months still zero-based
- Inconsistent behavior across platforms
- Performance issues

### The Joda-Time Era (2002-2014)
Stephen Colebourne created Joda-Time in 2002 as a complete replacement for Date/Calendar. Joda-Time became the de facto standard for date/time handling in Java, used in thousands of projects. Key innovations:
- Immutable types
- Clean API (LocalDate, DateTime, etc.)
- Proper timezone handling
- ISO-8601 compliance
- Fluent API

## The java.time API

### JSR 310 (2007-2014)
Stephen Colebourne led JSR 310 under the Java Community Process, creating a new date/time API based on lessons from Joda-Time. The specification was developed from 2007 to 2014.

### Java 8 (March 2014) — Initial Release
The `java.time` package was released in Java 8 with:
- All core classes (LocalDate, LocalTime, LocalDateTime, ZonedDateTime, Instant)
- Duration and Period
- DateTimeFormatter with pre-defined formats
- TemporalAdjusters
- Interoperability with legacy classes

### Java 9 (September 2017)
- `LocalDate.datesUntil()` for date ranges
- `LocalTime.toEpochSecond()` variant
- `Duration.toDaysPart()`, `toHoursPart()`, etc. for component extraction

### Java 12 (March 2019)
- `TemporalAdjusters.next(DayOfWeek)` and `previous(DayOfWeek)` with inclusive options

### Later Versions
- Enhanced locale support in formatting
- Additional Chronology implementations
- Improved CLDR data integration

## Timeline

| Version | Date | Feature |
|---------|------|---------|
| Java 1.0 | 1996 | java.util.Date |
| Java 1.1 | 1997 | java.util.Calendar, DateFormat |
| 2002 | — | Joda-Time created |
| 2007 | — | JSR 310 started |
| Java 8 | 2014 | java.time package released |
| Java 9 | 2017 | datesUntil, Duration parts |
| Java 12 | 2019 | Enhanced adjusters |

## Key People

- **Stephen Colebourne**: Lead designer of JSR 310 and author of Joda-Time
- **Michael Nascimento Santos**: Co-spec lead for JSR 310
- **Roger Riggs**: Implementation lead for java.time
- **Original Date/Calendar designers**: Perhaps the most apologized-for API in Java history
