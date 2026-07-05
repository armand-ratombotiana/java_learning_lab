package com.learning.lab28;

import java.util.*;

/**
 * Demonstrates EnumMap and EnumSet — specialized collections for enum types.
 */
public class EnumMapSetExample {

    public static void showEnumMapSet() {
        System.out.println("=== EnumMap & EnumSet ===");

        EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
        schedule.put(Day.MONDAY, "Gym");
        schedule.put(Day.WEDNESDAY, "Study Java");
        schedule.put(Day.FRIDAY, "Movie night");
        schedule.put(Day.SUNDAY, "Rest");

        System.out.println("Weekly schedule: " + schedule);

        EnumSet<Day> workDays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
        System.out.println("Work days: " + workDays);

        EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
        System.out.println("Weekend: " + weekend);

        Set<Day> all = EnumSet.allOf(Day.class);
        System.out.println("All days: " + all);
    }
}

enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
