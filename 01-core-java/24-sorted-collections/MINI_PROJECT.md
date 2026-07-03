# Mini Project: Event Scheduling System

## Objective
Build a scheduling system that manages events using a `TreeMap`. You will implement custom `Comparator` logic, use `NavigableMap` methods to find upcoming events, and use `subMap` to generate daily itineraries.

## Prerequisites
*   Java 17+

## Step 1: Define the Event Class
Create an immutable `Event` class.

```java
import java.time.LocalDateTime;

public record Event(String title, LocalDateTime startTime, LocalDateTime endTime) {
    public Event {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}
```

## Step 2: Build the Scheduler
Use a `TreeMap` where the key is the `LocalDateTime` of the event's start time, and the value is the `Event` itself.

```java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Scheduler {
    // TreeMap automatically sorts by the natural ordering of LocalDateTime
    private final NavigableMap<LocalDateTime, Event> schedule = new TreeMap<>();

    public void addEvent(Event event) {
        // Prevent double-booking exactly at the same start time
        if (schedule.containsKey(event.startTime())) {
            throw new IllegalArgumentException("Time slot already booked!");
        }
        
        // Advanced: Check for overlaps using lowerEntry
        Map.Entry<LocalDateTime, Event> previousEvent = schedule.lowerEntry(event.startTime());
        if (previousEvent != null && previousEvent.getValue().endTime().isAfter(event.startTime())) {
            throw new IllegalArgumentException("Overlaps with previous event: " + previousEvent.getValue().title());
        }

        schedule.put(event.startTime(), event);
        System.out.println("Added: " + event.title());
    }

    // Use ceilingEntry to find the very next event happening after 'now'
    public Event getNextUpcomingEvent(LocalDateTime now) {
        Map.Entry<LocalDateTime, Event> next = schedule.ceilingEntry(now);
        return next != null ? next.getValue() : null;
    }

    // Use subMap to get all events for a specific day
    public NavigableMap<LocalDateTime, Event> getItineraryForDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // fromKey (inclusive), toKey (exclusive)
        return schedule.subMap(startOfDay, true, endOfDay, false);
    }
    
    public void printSchedule() {
        System.out.println("\n--- Full Schedule ---");
        schedule.values().forEach(e -> System.out.println(e.startTime() + " : " + e.title()));
    }
}
```

## Step 3: Test the Scheduler
Create a `Main` class to test adding events, finding the next event, and generating daily itineraries.

```java
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        LocalDateTime today = LocalDateTime.now();
        LocalDate tomorrow = today.toLocalDate().plusDays(1);

        // 1. Add Events
        scheduler.addEvent(new Event("Morning Standup", today.withHour(9).withMinute(0), today.withHour(9).withMinute(30)));
        scheduler.addEvent(new Event("Client Lunch", today.withHour(12).withMinute(0), today.withHour(13).withMinute(0)));
        scheduler.addEvent(new Event("System Update", tomorrow.atTime(2, 0), tomorrow.atTime(4, 0)));

        // 2. Test Overlap Logic
        try {
            scheduler.addEvent(new Event("Overlap Meeting", today.withHour(12).withMinute(30), today.withHour(13).withMinute(30)));
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to add: " + e.getMessage());
        }

        scheduler.printSchedule();

        // 3. Find Next Upcoming Event
        LocalDateTime checkTime = today.withHour(10).withMinute(0);
        Event next = scheduler.getNextUpcomingEvent(checkTime);
        System.out.println("\nNext event after 10:00 AM is: " + (next != null ? next.title() : "None"));

        // 4. Get Daily Itinerary
        System.out.println("\n--- Today's Itinerary ---");
        scheduler.getItineraryForDay(today.toLocalDate())
                .values()
                .forEach(e -> System.out.println(e.title()));
    }
}
```

## Expected Output
```text
Added: Morning Standup
Added: Client Lunch
Added: System Update
Failed to add: Overlaps with previous event: Client Lunch

--- Full Schedule ---
2026-05-01T09:00 : Morning Standup
2026-05-01T12:00 : Client Lunch
2026-05-02T02:00 : System Update

Next event after 10:00 AM is: Client Lunch

--- Today's Itinerary ---
Morning Standup
Client Lunch
```