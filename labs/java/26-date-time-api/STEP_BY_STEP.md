# Step-by-Step Guide to Date-Time API

## Step 1: Working with LocalDate

```java
// Current date
LocalDate today = LocalDate.now();
System.out.println("Today: " + today);

// Specific date
LocalDate christmas = LocalDate.of(2024, Month.DECEMBER, 25);
System.out.println("Christmas: " + christmas);

// Parse from string
LocalDate parsed = LocalDate.parse("2024-12-25");
System.out.println("Parsed: " + parsed);

// Access components
System.out.println("Year: " + christmas.getYear());
System.out.println("Month: " + christmas.getMonth());
System.out.println("Day: " + christmas.getDayOfMonth());
System.out.println("Day of week: " + christmas.getDayOfWeek());
```

## Step 2: Working with LocalTime

```java
// Current time
LocalTime now = LocalTime.now();
System.out.println("Current time: " + now);

// Specific time
LocalTime meeting = LocalTime.of(14, 30, 0);
System.out.println("Meeting time: " + meeting);

// Parse
LocalTime parsed = LocalTime.parse("14:30:00");

// Arithmetic
LocalTime lunchEnd = meeting.minusHours(1);
LocalTime nextSlot = meeting.plusMinutes(30);
```

## Step 3: Working with LocalDateTime

```java
// Combine date and time
LocalDate date = LocalDate.of(2024, 3, 15);
LocalTime time = LocalTime.of(14, 30, 0);
LocalDateTime dt = LocalDateTime.of(date, time);
// or: LocalDateTime.of(2024, 3, 15, 14, 30)

// Current
LocalDateTime now = LocalDateTime.now();

// Conversion
LocalDate justDate = dt.toLocalDate();
LocalTime justTime = dt.toLocalTime();
```

## Step 4: Working with ZonedDateTime

```java
// Create zoned date-time
ZonedDateTime nyMeeting = ZonedDateTime.of(
    2024, 3, 15, 14, 30, 0, 0,
    ZoneId.of("America/New_York")
);

// Convert timezones
ZonedDateTime londonTime = nyMeeting.withZoneSameInstant(
    ZoneId.of("Europe/London"));
ZonedDateTime tokyoTime = nyMeeting.withZoneSameInstant(
    ZoneId.of("Asia/Tokyo"));

// Get all available zones
Set<String> zones = ZoneId.getAvailableZoneIds();
```

## Step 5: Duration and Period

```java
// Duration (seconds/nanos)
Duration twoHours = Duration.ofHours(2);
Duration thirtyMins = Duration.ofMinutes(30);
Duration total = twoHours.plus(thirtyMins);

// Period (years/months/days)
Period oneMonth = Period.ofMonths(1);
Period oneYear = Period.ofYears(1);

// Calculate between
LocalDate start = LocalDate.of(2024, 1, 1);
LocalDate end = LocalDate.of(2024, 12, 31);
long days = ChronoUnit.DAYS.between(start, end);
```

## Step 6: Formatting

```java
LocalDateTime now = LocalDateTime.now();

// Predefined formats
System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
System.out.println(now.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

// Custom patterns
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' HH:mm");
System.out.println(now.format(formatter));

// Parsing
LocalDate date = LocalDate.parse("15/03/2024", 
    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
```

## Step 7: TemporalAdjusters

```java
LocalDate today = LocalDate.now();

// Find next/previous day of week
LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
LocalDate lastFriday = today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));

// Month boundaries
LocalDate firstOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
LocalDate lastOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

// Custom adjuster
LocalDate nextPayday = today.with(temporal -> {
    LocalDate date = LocalDate.from(temporal);
    LocalDate next15th = date.withDayOfMonth(15);
    if (next15th.isBefore(date)) {
        next15th = next15th.plusMonths(1);
    }
    return next15th;
});
```

## Step 8: Legacy Conversion

```java
// java.util.Date to Instant
Date legacyDate = new Date();
Instant instant = legacyDate.toInstant();

// Instant to Date
Date back = Date.from(instant);

// Calendar to ZonedDateTime
Calendar cal = Calendar.getInstance();
ZonedDateTime zdt = cal.toInstant().atZone(ZoneId.systemDefault());

// ZonedDateTime to GregorianCalendar
GregorianCalendar gc = GregorianCalendar.from(zdt);
```
