package com.distributed.scheduling;

import java.time.LocalDateTime;
import java.util.*;

public class CronExpression {
    private final String expression;
    private final int[] minuteSet;
    private final int[] hourSet;
    private final int[] dayOfMonthSet;
    private final int[] monthSet;
    private final int[] dayOfWeekSet;

    public CronExpression(String expression) {
        this.expression = expression;
        String[] fields = expression.trim().split("\\s+");
        if (fields.length != 5) {
            throw new IllegalArgumentException("Cron must have 5 fields, got: " + fields.length);
        }
        this.minuteSet = parseField(fields[0], 0, 59);
        this.hourSet = parseField(fields[1], 0, 23);
        this.dayOfMonthSet = parseField(fields[2], 1, 31);
        this.monthSet = parseField(fields[3], 1, 12);
        this.dayOfWeekSet = parseField(fields[4], 0, 7);
    }

    private int[] parseField(String field, int min, int max) {
        if (field.equals("*")) {
            int[] result = new int[max - min + 1];
            for (int i = 0; i < result.length; i++) result[i] = min + i;
            return result;
        }
        if (field.contains("/")) {
            String[] parts = field.split("/");
            int step = Integer.parseInt(parts[1]);
            int start = parts[0].equals("*") ? min : Integer.parseInt(parts[0]);
            int count = ((max - start) / step) + 1;
            int[] result = new int[count];
            for (int i = 0; i < count; i++) result[i] = start + i * step;
            return result;
        }
        Set<Integer> values = new HashSet<>();
        for (String part : field.split(",")) {
            values.add(Integer.parseInt(part));
        }
        return values.stream().mapToInt(Integer::intValue).sorted().toArray();
    }

    public boolean matches(LocalDateTime time) {
        return contains(minuteSet, time.getMinute())
            && contains(hourSet, time.getHour())
            && contains(dayOfMonthSet, time.getDayOfMonth())
            && contains(monthSet, time.getMonthValue())
            && contains(dayOfWeekSet, time.getDayOfWeek().getValue() % 7);
    }

    private boolean contains(int[] arr, int value) {
        return Arrays.binarySearch(arr, value) >= 0;
    }

    public LocalDateTime nextFireTime(LocalDateTime after) {
        LocalDateTime candidate = after.withSecond(0).withNano(0).plusMinutes(1);
        for (int i = 0; i < 525600; i++) {
            if (matches(candidate)) return candidate;
            candidate = candidate.plusMinutes(1);
        }
        return null;
    }

    @Override
    public String toString() { return expression; }
}
