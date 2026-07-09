package com.databases.advsql;

import java.util.*;
import java.util.stream.*;

public class SqlWindowFunctions {

    public record Employee(int empId, String name, String dept, double salary) {}

    public static List<Map<String, Object>> rowNumber(List<Employee> employees, String partitionBy, String orderBy, boolean desc) {
        Map<Object, List<Employee>> groups = employees.stream()
            .collect(Collectors.groupingBy(e -> switch (partitionBy) {
                case "dept" -> e.dept();
                default -> throw new IllegalArgumentException("Unknown partition: " + partitionBy);
            }, Collectors.toList()));

        var result = new ArrayList<Map<String, Object>>();
        groups.forEach((key, group) -> {
            var sorted = desc
                ? group.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).toList()
                : group.stream().sorted(Comparator.comparingDouble(Employee::salary)).toList();
            int[] rank = {1};
            sorted.forEach(e -> {
                var map = new HashMap<String, Object>();
                map.put("empId", e.empId()); map.put("name", e.name());
                map.put("dept", e.dept()); map.put("salary", e.salary());
                map.put("rn", rank[0]++);
                result.add(map);
            });
        });
        return result;
    }

    public static List<Map<String, Object>> rankAndDenseRank(List<Employee> employees, String sortBy, boolean desc) {
        var sorted = desc
            ? employees.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).toList()
            : employees.stream().sorted(Comparator.comparingDouble(Employee::salary)).toList();

        var result = new ArrayList<Map<String, Object>>();
        int rank = 1, denseRank = 1;
        Double prevSal = null;
        int sameCount = 1;
        for (var e : sorted) {
            var map = new HashMap<String, Object>();
            map.put("empId", e.empId()); map.put("name", e.name());
            map.put("salary", e.salary());
            if (prevSal != null && e.salary() == prevSal) {
                sameCount++;
            } else if (prevSal != null) {
                rank += sameCount;
                denseRank++;
                sameCount = 1;
            }
            map.put("rank", rank);
            map.put("denseRank", denseRank);
            prevSal = e.salary();
            result.add(map);
        }
        return result;
    }

    public static List<Map<String, Object>> nTile(List<Employee> employees, int buckets, String sortBy, boolean desc) {
        var sorted = desc
            ? employees.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).toList()
            : employees.stream().sorted(Comparator.comparingDouble(Employee::salary)).toList();

        int total = sorted.size();
        int baseSize = total / buckets;
        int remainder = total % buckets;
        var result = new ArrayList<Map<String, Object>>();
        int idx = 0;
        for (int b = 0; b < buckets; b++) {
            int bucketSize = baseSize + (b < remainder ? 1 : 0);
            for (int j = 0; j < bucketSize && idx < total; j++) {
                var e = sorted.get(idx++);
                var map = new HashMap<String, Object>();
                map.put("empId", e.empId()); map.put("name", e.name());
                map.put("salary", e.salary()); map.put("bucket", b + 1);
                result.add(map);
            }
        }
        return result;
    }

    public static List<Map<String, Object>> lagLead(List<Employee> employees, String sortBy) {
        var sorted = employees.stream().sorted(Comparator.comparingDouble(Employee::salary)).toList();
        var result = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < sorted.size(); i++) {
            var e = sorted.get(i);
            var map = new HashMap<String, Object>();
            map.put("empId", e.empId()); map.put("name", e.name());
            map.put("salary", e.salary());
            map.put("lag", i > 0 ? sorted.get(i - 1).salary() : null);
            map.put("lead", i < sorted.size() - 1 ? sorted.get(i + 1).salary() : null);
            result.add(map);
        }
        return result;
    }

    public static List<Employee> sampleData() {
        return List.of(
            new Employee(1, "Alice", "IT", 5000),
            new Employee(2, "Bob", "IT", 4500),
            new Employee(3, "Carol", "HR", 4800),
            new Employee(4, "David", "HR", 5500),
            new Employee(5, "Eve", "IT", 5000)
        );
    }
}