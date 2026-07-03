# How the Date-Time API Works

## Creating Date-Time Objects

```java
// Current date/time
LocalDate today = LocalDate.now();
LocalTime now = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();
Instant instant = Instant.now();

// Specific values
LocalDate date = LocalDate.of(2024, Month.MARCH, 15);
LocalTime time = LocalTime.of(14, 30, 0);
LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 30);
ZonedDateTime zdt = ZonedDateTime.of(2024, 3, 15, 14, 30, 0, 0, ZoneId.of("America/New_York"));

// Parse from string
LocalDate parsed = LocalDate.parse("2024-03-15");
LocalTime parsedTime = LocalTime.parse("14:30:00");
ZonedDateTime parsedZdt = ZonedDateTime.parse("2024-03-15T14:30:00-04:00[America/New_York]");
```

## Accessing Components

```java
LocalDate date = LocalDate.of(2024, Month.MARCH, 15);
int year = date.getYear();           // 2024
Month month = date.getMonth();       // MARCH
int monthValue = date.getMonthValue(); // 3
int day = date.getDayOfMonth();      // 15
DayOfWeek dayOfWeek = date.getDayOfWeek(); // FRIDAY
int dayOfYear = date.getDayOfYear(); // 75
boolean leap = date.isLeapYear();    // true (2024 is leap year)
```

## Date-Time Arithmetic

```java
LocalDate date = LocalDate.of(2024, 3, 15);

// Addition
LocalDate nextWeek = date.plusWeeks(1);
LocalDate nextMonth = date.plusMonths(1);
LocalDate nextYear = date.plusYears(1);
LocalDate plus10 = date.plusDays(10);

// Subtraction
LocalDate lastWeek = date.minusWeeks(1);
LocalDate lastMonth = date.minusMonths(1);

// With methods (set specific fields)
LocalDate firstOfMonth = date.withDayOfMonth(1);
LocalDate endOfYear = date.withMonth(12).withDayOfMonth(31);

// Adjusters
LocalDate nextMonday = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
LocalDate lastOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
```

## Timezone Operations

```java
// Timezone conversion
ZonedDateTime nyMeeting = ZonedDateTime.of(2024, 3, 15, 14, 30, 0, 0,
    ZoneId.of("America/New_York"));

ZonedDateTime londonMeeting = nyMeeting.withZoneSameInstant(ZoneId.of("Europe/London"));
// 2024-03-15T18:30:00Z[Europe/London] (5 hours ahead)

ZonedDateTime tokyoMorning = nyMeeting.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
// Next day in Tokyo

// All timezones
Set<String> allZones = ZoneId.getAvailableZoneIds();

// UTC offset
ZoneOffset offset = ZoneOffset.of("-05:00");
OffsetDateTime odt = OffsetDateTime.of(2024, 3, 15, 14, 30, 0, 0, offset);
```

## Duration and Period

```java
// Duration: time-based (seconds/nanos)
Duration fiveMinutes = Duration.ofMinutes(5);
Duration twoHours = Duration.ofHours(2);
Duration total = fiveMinutes.plus(twoHours);  // 2 hours 5 minutes

// Period: date-based (years/months/days)
Period oneMonth = Period.ofMonths(1);
Period tenDays = Period.ofDays(10);
Period combined = Period.of(1, 3, 15);  // 1 year, 3 months, 15 days

// Calculate between dates
LocalDate start = LocalDate.of(2024, 1, 1);
LocalDate end = LocalDate.of(2024, 12, 31);
long daysBetween = ChronoUnit.DAYS.between(start, end);  // 365
long monthsBetween = ChronoUnit.MONTHS.between(start, end);  // 11
```

## Formatting and Parsing

```java
// Predefined formatters
DateTimeFormatter isoDate = DateTimeFormatter.ISO_LOCAL_DATE;
DateTimeFormatter isoDateTime = DateTimeFormatter.ISO_DATE_TIME;

// Custom formatters
DateTimeFormatter custom = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
DateTimeFormatter fullDate = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

// Format
LocalDate date = LocalDate.of(2024, 3, 15);
String formatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
String full = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));

// Parse
LocalDate parsed = LocalDate.parse("15/03/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
```
