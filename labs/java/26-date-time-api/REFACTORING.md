# Refactoring with Date-Time API

## From java.util.Date to java.time

### Before
```java
Date now = new Date();
Date tomorrow = new Date(now.getTime() + 86400000L);  // Magic number
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String formatted = sdf.format(tomorrow);
```

### After
```java
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plusDays(1);
String formatted = tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE);
```

## From java.util.Calendar to java.time

### Before
```java
Calendar cal = Calendar.getInstance();
cal.set(2024, Calendar.MARCH, 15, 14, 30, 0);
cal.add(Calendar.DAY_OF_MONTH, 7);
int month = cal.get(Calendar.MONTH);  // 2 (March), not 3!
```

### After
```java
LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 30, 0);
LocalDateTime nextWeek = dt.plusDays(7);
int month = dt.getMonthValue();  // 3 (correct!)
```

## Migration Checklist
1. Identify all uses of `Date`, `Calendar`, `SimpleDateFormat`
2. Replace with appropriate `java.time` equivalents
3. Use `Date.toInstant()` and `Date.from(Instant)` for legacy interop
4. Replace `SimpleDateFormat` with `DateTimeFormatter`
5. Update database interaction to use JDBC 4.2+ java.time support
6. Test DST boundary dates thoroughly
