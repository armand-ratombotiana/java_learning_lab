package com.dataeng.eighteen;

import java.util.*;
import java.util.stream.*;

public class DataProfiler {

    public record ProfileResult(String columnName, long rowCount, long nullCount,
                                 long distinctCount, double min, double max, double mean,
                                 double stddev, Map<String, Long> topValues) {
        public double nullRate() { return rowCount > 0 ? (double) nullCount / rowCount : 0; }
        public double distinctRate() { return rowCount > 0 ? (double) distinctCount / rowCount : 0; }
    }

    public ProfileResult profileNumericColumn(List<Number> values, String columnName) {
        long rowCount = values.size();
        long nullCount = values.stream().filter(Objects::isNull).count();
        List<Double> nonNull = values.stream().filter(Objects::nonNull).map(Number::doubleValue).toList();
        long distinctCount = nonNull.stream().distinct().count();
        double min = nonNull.stream().mapToDouble(v -> v).min().orElse(0);
        double max = nonNull.stream().mapToDouble(v -> v).max().orElse(0);
        double mean = nonNull.stream().mapToDouble(v -> v).average().orElse(0);
        double variance = nonNull.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0);
        double stddev = Math.sqrt(variance);

        Map<String, Long> topValues = nonNull.stream()
            .limit(1000)
            .collect(Collectors.groupingBy(Object::toString, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));

        return new ProfileResult(columnName, rowCount, nullCount, distinctCount, min, max, mean, stddev, topValues);
    }

    public ProfileResult profileStringColumn(List<String> values, String columnName) {
        long rowCount = values.size();
        long nullCount = values.stream().filter(Objects::isNull).count();
        long distinctCount = values.stream().filter(Objects::nonNull).distinct().count();

        Map<String, Long> topValues = values.stream().filter(Objects::nonNull)
            .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, LinkedHashMap::new));

        return new ProfileResult(columnName, rowCount, nullCount, distinctCount, 0, 0, 0, 0, topValues);
    }
}
