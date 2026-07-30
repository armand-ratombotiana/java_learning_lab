# GUIDE — Stream Collectors

## Step 1: Built‑in Collectors
```java
toList(), toSet(), toMap(keyMapper, valueMapper)
joining(", "), summarizingDouble(Transaction::amount)
```

## Step 2: groupingBy
```java
Map<Department, List<Employee>> byDept =
    employees.stream().collect(groupingBy(Employee::dept));

Map<Department, Long> countByDept =
    employees.stream().collect(groupingBy(Employee::dept, counting()));
```

## Step 3: partitioningBy
```java
Map<Boolean, List<Integer>> parts =
    nums.stream().collect(partitioningBy(n -> n % 2 == 0));
```

## Step 4: teeing (Java 12+)
```java
Map.Entry<Long, Double> stats = nums.stream()
    .collect(teeing(counting(), averagingDouble(n -> n), Map::entry));
```

## Step 5: Exercises
1. Custom collector that builds an immutable `LinkedHashSet`
2. Group transactions by currency, sum amounts
3. Use `teeing` to compute min and max in one pass
