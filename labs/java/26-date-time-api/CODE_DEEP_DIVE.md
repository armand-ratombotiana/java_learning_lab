# Code Deep Dive: Date-Time API

## Event Scheduling System

```java
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

public class EventScheduler {
    
    public record Event(String name, ZonedDateTime start, Duration duration) {
        public ZonedDateTime end() { return start.plus(duration); }
    }
    
    private final List<Event> events = new ArrayList<>();
    
    public Event scheduleEvent(String name, ZonedDateTime start, Duration duration) {
        Event event = new Event(name, start, duration);
        events.add(event);
        return event;
    }
    
    public List<Event> findEventsForDate(LocalDate date) {
        return events.stream()
            .filter(e -> e.start().toLocalDate().equals(date))
            .sorted(Comparator.comparing(e -> e.start()))
            .toList();
    }
    
    public Optional<Event> findNextEvent() {
        ZonedDateTime now = ZonedDateTime.now();
        return events.stream()
            .filter(e -> e.start().isAfter(now))
            .min(Comparator.comparing(e -> e.start()));
    }
    
    public boolean hasConflict(Event newEvent) {
        return events.stream().anyMatch(existing -> 
            !newEvent.end().isBefore(existing.start()) &&
            !newEvent.start().isAfter(existing.end()));
    }
    
    public Duration totalDurationForDate(LocalDate date) {
        return findEventsForDate(date).stream()
            .map(Event::duration)
            .reduce(Duration.ZERO, Duration::plus);
    }
    
    public List<Event> getOverlappingEvents(ZonedDateTime time, Duration window) {
        ZonedDateTime windowEnd = time.plus(window);
        return events.stream()
            .filter(e -> e.start().isBefore(windowEnd) && e.end().isAfter(time))
            .toList();
    }
}
```

## Recurrence Rule Parser

```java
public class RecurrenceRule {
    public enum Frequency { DAILY, WEEKLY, MONTHLY, YEARLY }
    
    public record Schedule(
        Frequency frequency, int interval, LocalDate start, 
        LocalDate end, Set<DayOfWeek> daysOfWeek
    ) {}
    
    public List<LocalDate> generateDates(Schedule schedule) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = schedule.start();
        
        while (!current.isAfter(schedule.end())) {
            if (matchesSchedule(current, schedule)) {
                dates.add(current);
            }
            current = switch (schedule.frequency()) {
                case DAILY -> current.plusDays(schedule.interval());
                case WEEKLY -> current.plusWeeks(schedule.interval());
                case MONTHLY -> current.plusMonths(schedule.interval());
                case YEARLY -> current.plusYears(schedule.interval());
            };
        }
        return dates;
    }
    
    private boolean matchesSchedule(LocalDate date, Schedule schedule) {
        if (schedule.daysOfWeek() != null && !schedule.daysOfWeek().isEmpty()) {
            if (!schedule.daysOfWeek().contains(date.getDayOfWeek())) {
                return false;
            }
        }
        return true;
    }
}
```

## Business Calendar

```java
public class BusinessCalendar {
    private final Set<LocalDate> holidays = new HashSet<>();
    
    public void addHoliday(LocalDate date) { holidays.add(date); }
    
    public boolean isBusinessDay(LocalDate date) {
        return !isWeekend(date) && !holidays.contains(date);
    }
    
    public LocalDate nextBusinessDay(LocalDate date) {
        LocalDate next = date.plusDays(1);
        while (!isBusinessDay(next)) {
            next = next.plusDays(1);
        }
        return next;
    }
    
    public LocalDate previousBusinessDay(LocalDate date) {
        LocalDate prev = date.minusDays(1);
        while (!isBusinessDay(prev)) {
            prev = prev.minusDays(1);
        }
        return prev;
    }
    
    public long businessDaysBetween(LocalDate start, LocalDate end) {
        long count = 0;
        LocalDate current = start.plusDays(1);
        while (!current.isAfter(end)) {
            if (isBusinessDay(current)) count++;
            current = current.plusDays(1);
        }
        return count;
    }
    
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }
}
```

## Date-Time Formatting Utility

```java
public class DateTimeUtils {
    
    private static final Map<String, DateTimeFormatter> FORMATTERS = new HashMap<>();
    
    static {
        FORMATTERS.put("iso", DateTimeFormatter.ISO_DATE_TIME);
        FORMATTERS.put("full", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL));
        FORMATTERS.put("long", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG));
        FORMATTERS.put("medium", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        FORMATTERS.put("short", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
        FORMATTERS.put("date", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        FORMATTERS.put("time", DateTimeFormatter.ofPattern("HH:mm:ss"));
        FORMATTERS.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        FORMATTERS.put("rfc1123", DateTimeFormatter.RFC_1123_DATE_TIME);
    }
    
    public static String format(ZonedDateTime dateTime, String format) {
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(format, 
            DateTimeFormatter.ofPattern(format));
        return dateTime.format(formatter);
    }
    
    public static ZonedDateTime parse(String text, String format) {
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(format,
            DateTimeFormatter.ofPattern(format));
        return ZonedDateTime.parse(text, formatter);
    }
    
    public static String toRelativeTime(ZonedDateTime dateTime) {
        Duration duration = Duration.between(dateTime, ZonedDateTime.now());
        
        if (duration.isNegative()) {
            Duration past = duration.negated();
            if (past.toMinutes() < 1) return "just now";
            if (past.toHours() < 1) return past.toMinutes() + " minutes ago";
            if (past.toDays() < 1) return past.toHours() + " hours ago";
            if (past.toDays() < 30) return past.toDays() + " days ago";
            return dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        } else {
            if (duration.toMinutes() < 1) return "now";
            if (duration.toHours() < 1) return "in " + duration.toMinutes() + " minutes";
            if (duration.toDays() < 1) return "in " + duration.toHours() + " hours";
            return "in " + duration.toDays() + " days";
        }
    }
}
```
