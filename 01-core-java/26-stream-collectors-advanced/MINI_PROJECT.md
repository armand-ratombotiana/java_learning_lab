# Mini Project: Statistical Analyzer using Custom Collectors

## Objective
Build a program that analyzes a stream of financial transactions. You will build a custom `Collector` to calculate a moving average, and use `Collectors.teeing()` to find the highest and lowest transactions in a single pass.

## Prerequisites
*   Java 12+ (for `teeing`)

## Step 1: Define the Domain Model
Create a simple `Transaction` record.

```java
public record Transaction(String id, double amount, String category) {}
```

## Step 2: Build a Custom Collector (Summary Statistics)
While Java provides `DoubleSummaryStatistics`, we will build our own custom collector to calculate the Total Sum and the Average simultaneously, demonstrating the `Collector.of()` method.

```java
import java.util.stream.Collector;

public class StatsAccumulator {
    double totalSum = 0;
    int count = 0;

    // Accumulator logic
    public void add(double amount) {
        totalSum += amount;
        count++;
    }

    // Combiner logic (for parallel streams)
    public StatsAccumulator combine(StatsAccumulator other) {
        this.totalSum += other.totalSum;
        this.count += other.count;
        return this;
    }
}

public record CustomStatsResult(double total, double average) {}

public class CustomCollectors {
    
    public static Collector<Transaction, StatsAccumulator, CustomStatsResult> toCustomStats() {
        return Collector.of(
            StatsAccumulator::new,                               // 1. Supplier
            (acc, tx) -> acc.add(tx.amount()),                   // 2. Accumulator
            StatsAccumulator::combine,                           // 3. Combiner (for parallel)
            acc -> new CustomStatsResult(                        // 4. Finisher
                acc.totalSum, 
                acc.count == 0 ? 0 : acc.totalSum / acc.count
            ),
            // No Characteristics.IDENTITY_FINISH because A (StatsAccumulator) != R (CustomStatsResult)
            Collector.Characteristics.UNORDERED
        );
    }
}
```

## Step 3: Use `Collectors.teeing()`
We want to find the highest value transaction and the lowest value transaction at the same time. Doing this with two separate streams requires iterating the data twice. We will use `teeing` to do it in one pass.

```java
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record MinMaxResult(Transaction min, Transaction max) {}

public class Analyzer {
    
    public MinMaxResult findMinMax(List<Transaction> transactions) {
        return transactions.stream().collect(
            Collectors.teeing(
                // Downstream 1: Find Min
                Collectors.minBy(Comparator.comparingDouble(Transaction::amount)),
                // Downstream 2: Find Max
                Collectors.maxBy(Comparator.comparingDouble(Transaction::amount)),
                // Merger: Combine the two Optionals into our custom Record
                (minOpt, maxOpt) -> new MinMaxResult(minOpt.orElse(null), maxOpt.orElse(null))
            )
        );
    }
}
```

## Step 4: Test the Analyzer
Create a `Main` class to run the data through our custom collector and our teeing collector.

```java
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Transaction> data = List.of(
            new Transaction("T1", 100.0, "GROCERY"),
            new Transaction("T2", 5000.0, "RENT"),
            new Transaction("T3", 50.0, "GAS"),
            new Transaction("T4", 1200.0, "TRAVEL"),
            new Transaction("T5", 10.0, "COFFEE")
        );

        System.out.println("--- 1. Testing Custom Collector ---");
        // Test sequentially
        CustomStatsResult resultSeq = data.stream().collect(CustomCollectors.toCustomStats());
        System.out.println("Sequential -> Total: $" + resultSeq.total() + ", Avg: $" + resultSeq.average());

        // Test in parallel (This will invoke our combiner method!)
        CustomStatsResult resultPar = data.parallelStream().collect(CustomCollectors.toCustomStats());
        System.out.println("Parallel   -> Total: $" + resultPar.total() + ", Avg: $" + resultPar.average());

        System.out.println("\n--- 2. Testing Teeing Collector ---");
        Analyzer analyzer = new Analyzer();
        MinMaxResult minMax = analyzer.findMinMax(data);
        
        System.out.println("Lowest  Tx: " + minMax.min().id() + " ($" + minMax.min().amount() + ")");
        System.out.println("Highest Tx: " + minMax.max().id() + " ($" + minMax.max().amount() + ")");
    }
}
```

## Expected Output
```text
--- 1. Testing Custom Collector ---
Sequential -> Total: $6360.0, Avg: $1272.0
Parallel   -> Total: $6360.0, Avg: $1272.0

--- 2. Testing Teeing Collector ---
Lowest  Tx: T5 ($10.0)
Highest Tx: T2 ($5000.0)
```