package com.java.records.sealed.patterns;

import java.util.*;
import java.util.stream.*;

/**
 * Lab 04: Switch Expressions Deep Dive — arrow syntax, yield,
 * exhaustiveness, null handling, guarded patterns.
 */
public class SwitchExpressionsLab {

    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    // --- Arrow syntax, no fall-through ---
    public String dayType(Day d) {
        return switch (d) {
            case MON, TUE, WED, THU, FRI -> "Weekday";
            case SAT, SUN -> "Weekend";
        };
    }

    // --- Yield in block ---
    public String dayActivity(Day d) {
        return switch (d) {
            case MON -> "Gym";
            case TUE, THU -> {
                System.out.println("Study day");
                yield "Study";
            }
            case WED -> "Swimming";
            case FRI -> {
                System.out.println("Weekend prep");
                yield "Errands";
            }
            case SAT -> "Hiking";
            case SUN -> "Rest";
        };
    }

    // --- Null handling ---
    public String nullSafeDescribe(Object obj) {
        return switch (obj) {
            case null -> "null";
            case String s -> "string(" + s.length() + ")";
            case Integer i -> "int(" + i + ")";
            case Day d -> "day: " + d;
            default -> obj.getClass().getSimpleName();
        };
    }

    // --- Guarded patterns ---
    sealed interface Employee permits Manager, Engineer, Intern {}
    record Manager(String name, int years) implements Employee {}
    record Engineer(String name, int level) implements Employee {}
    record Intern(String name, int duration) implements Employee {}

    public String bonus(Employee e) {
        return switch (e) {
            case Manager m when m.years() > 5 -> "Executive bonus: $50k";
            case Manager m -> "Manager bonus: $20k";
            case Engineer eng when eng.level() >= 5 -> "Senior bonus: $30k";
            case Engineer eng -> "Engineer bonus: $15k";
            case Intern _ -> "Intern bonus: $5k";
        };
    }

    // --- Exhaustive on enum ---
    public boolean isWeekend(Day d) {
        return switch (d) {
            case SAT, SUN -> true;
            case MON, TUE, WED, THU, FRI -> false;
        };
    }

    // --- Calculator with switch expression ---
    public int calculate(String expr) {
        var parts = expr.split(" ");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid format");
        int a = Integer.parseInt(parts[0]);
        int b = Integer.parseInt(parts[2]);
        return switch (parts[1]) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown op");
        };
    }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new SwitchExpressionsLab();

        System.out.println("MON: " + lab.dayType(Day.MON));
        System.out.println("SAT: " + lab.dayType(Day.SAT));

        System.out.println(lab.dayActivity(Day.FRI));
        System.out.println(lab.dayActivity(Day.TUE));

        System.out.println(lab.nullSafeDescribe(null));
        System.out.println(lab.nullSafeDescribe("Hello"));

        var mgr = new Manager("Alice", 10);
        var intern = new Intern("Bob", 3);
        System.out.println(lab.bonus(mgr));
        System.out.println(lab.bonus(intern));

        System.out.println("3 + 5 = " + lab.calculate("3 + 5"));
        System.out.println("10 / 2 = " + lab.calculate("10 / 2"));
    }
}
