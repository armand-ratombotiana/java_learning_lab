# Problem Walkthrough: Descriptive Statistics

## Problem 1: Search Latency Outlier Detection — Company: Google
### Interview Scenario
"You're at Google on the Search infrastructure SRE team. A monitoring window shows a p95 latency series of [55, 58, 60, 63, 65, 67, 70, 72, 74, 78, 82, 95, 240] ms. Your alerting uses the mean and standard deviation, and it has paged on this window three times — but the pager says the median is fine. Your job: build a summary-statistics pipeline that reports the true shape of the distribution, flags the offending observation with the 1.5×IQR rule, and tells the team how much a single outlier distorts the 'average' they were quoting."

### The Problem
1. Compute mean, median, and mode for the 13-point latency window
2. Compute Q1, Q3, and IQR and the outlier fence (Q1 − 1.5×IQR, Q3 + 1.5×IQR)
3. Flag every observation as inlier or outlier using the fence
4. Compute population vs sample variance and standard deviation for the cleaned series
5. Report the coefficient of variation so the spread is comparable across services
6. Explain why mean-based alerting false-positives here

### Solution Walkthrough
- Step 1: Reuse the lab's `DescriptiveStatistics` methods verbatim — `mean`, `median`, `mode`, `populationVariance`, `sampleVariance`, `populationStdDev`, `sampleStdDev`, `quartile1`, `quartile3`, `iqr` — so the walkthrough mirrors the lab's exact Javadoc'd behavior
- Step 2: Compute mean (83.00 ms) and median (70.00 ms); the 13 ms gap is the first skew signal
- Step 3: Compute Q1 = 61.50, Q3 = 80.00 via the median-of-halves convention (odd n excludes the median from both halves), then IQR = 18.50
- Step 4: Build the fence: below 33.75 ms or above 107.75 ms is an outlier
- Step 5: Scan the series; every value passes except 240 ms — the single spike that moved the mean
- Step 6: Recompute dispersion on the cleaned 12-point series; the sample standard deviation (11.30 ms) and CV (≈16%) give a defensible comparison baseline
- Step 7: Wire the conclusion into alerting: page on median/IQR shifts, use the fence to name the culprit observation

### Code
```java
package com.statistics.lab01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mirrors the lab's DescriptiveStatistics class (mean, median, mode,
 * variance, std dev, quartiles, IQR) and applies it to Google-style
 * search latency monitoring with the 1.5 x IQR outlier rule.
 */
public final class SearchLatencyMonitor {

    private SearchLatencyMonitor() {
    }

    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }

    public static double median(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
        }
        return sorted[n / 2];
    }

    public static List<Double> mode(double[] data) {
        Map<Double, Integer> freq = new HashMap<>();
        for (double v : data) {
            freq.merge(v, 1, Integer::sum);
        }
        int maxCount = 0;
        for (int count : freq.values()) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        List<Double> modes = new ArrayList<>();
        for (Map.Entry<Double, Integer> e : freq.entrySet()) {
            if (e.getValue() == maxCount) {
                modes.add(e.getKey());
            }
        }
        return modes;
    }

    public static double populationVariance(double[] data) {
        double m = mean(data);
        double sumSq = 0;
        for (double v : data) {
            sumSq += (v - m) * (v - m);
        }
        return sumSq / data.length;
    }

    public static double sampleVariance(double[] data) {
        double m = mean(data);
        double sumSq = 0;
        for (double v : data) {
            sumSq += (v - m) * (v - m);
        }
        return sumSq / (data.length - 1);
    }

    public static double populationStdDev(double[] data) {
        return Math.sqrt(populationVariance(data));
    }

    public static double sampleStdDev(double[] data) {
        return Math.sqrt(sampleVariance(data));
    }

    public static double quartile1(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int half = sorted.length / 2;
        return median(Arrays.copyOfRange(sorted, 0, half));
    }

    public static double quartile3(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        int half = n / 2;
        int start = (n % 2 == 0) ? half : half + 1;
        return median(Arrays.copyOfRange(sorted, start, n));
    }

    public static double iqr(double[] data) {
        return quartile3(data) - quartile1(data);
    }

    public static void main(String[] args) {
        double[] latency = {55, 58, 60, 63, 65, 67, 70, 72, 74, 78, 82, 95, 240};
        System.out.println("=== Search latency (ms) from one p95 window ===");
        System.out.println("Data: " + Arrays.toString(latency));

        double q1 = quartile1(latency);
        double q3 = quartile3(latency);
        double iqr = iqr(latency);
        double lo = q1 - 1.5 * iqr;
        double hi = q3 + 1.5 * iqr;

        System.out.printf("Mean:   %.2f ms%n", mean(latency));
        System.out.printf("Median: %.2f ms%n", median(latency));
        System.out.printf("Mode:   %s%n", mode(latency));
        System.out.printf("Q1 = %.2f, Q3 = %.2f, IQR = %.2f%n", q1, q3, iqr);
        System.out.printf("Outlier bounds: below %.2f ms or above %.2f ms%n", lo, hi);

        System.out.println("\n=== Outlier flags (1.5 x IQR rule) ===");
        for (double v : latency) {
            String flag = (v < lo || v > hi) ? "OUTLIER" : "ok";
            System.out.printf("  %6.1f ms  %s%n", v, flag);
        }

        System.out.println("\n=== Dispersion, cleaned data (spike removed) ===");
        double[] cleaned = Arrays.copyOf(latency, latency.length - 1);
        System.out.printf("Pop variance: %.4f  Pop std: %.4f%n",
            populationVariance(cleaned), populationStdDev(cleaned));
        System.out.printf("Sample var:   %.4f  Sample std: %.4f%n",
            sampleVariance(cleaned), sampleStdDev(cleaned));
        System.out.printf("Coefficient of variation (sample): %.2f%%%n",
            100 * sampleStdDev(cleaned) / mean(cleaned));
    }
}
```

### Expected Output
```
=== Search latency (ms) from one p95 window ===
Data: [55.0, 58.0, 60.0, 63.0, 65.0, 67.0, 70.0, 72.0, 74.0, 78.0, 82.0, 95.0, 240.0]
Mean:   83.00 ms
Median: 70.00 ms
Mode:   [67.0, 65.0, 70.0, 74.0, 72.0, 78.0, 82.0, 95.0, 55.0, 58.0, 240.0, 60.0, 63.0]
Q1 = 61.50, Q3 = 80.00, IQR = 18.50
Outlier bounds: below 33.75 ms or above 107.75 ms

=== Outlier flags (1.5 x IQR rule) ===
    55.0 ms  ok
    58.0 ms  ok
    60.0 ms  ok
    63.0 ms  ok
    65.0 ms  ok
    67.0 ms  ok
    70.0 ms  ok
    72.0 ms  ok
    74.0 ms  ok
    78.0 ms  ok
    82.0 ms  ok
    95.0 ms  ok
   240.0 ms  OUTLIER

=== Dispersion, cleaned data (spike removed) ===
Pop variance: 117.0764  Pop std: 10.8202
Sample var:   127.7197  Sample std: 11.3013
Coefficient of variation (sample): 16.16%
```

### Company Evaluation
- Google: p95/p99 alerting, outlier-aware SLO monitoring, mean-vs-median debate on dashboards, no false-positive paging.
- Amazon: latency percentiles on retail pages, robust anomaly detection on checkout metrics, capacity planning from cleaned statistics.
- Stripe: transaction amount distributions, IQR fences on payment fraud flags, unit-cost monitoring across payment rails.
- Airbnb: skewed nightly-price distributions, median-based pricing benchmarks, outlier listing detection before recommendation ingestion.

---

## Problem 2: Skewed Pricing — Median vs Mean — Company: Airbnb
### Interview Scenario
"You're at Airbnb building nightly-price benchmarks for hosts. The price distribution is right-skewed: most listings are $80–$150, a few are $900+. A host-facing dashboard shows 'average nightly price in your city: $210'. Hosts complain the number is meaningless. You need to pick the right summary."

### The Problem
1. Compute mean, median, and quartiles for a skewed sample of nightly prices
2. Show how one luxury listing moves the mean but not the median or IQR
3. Recommend the summary the dashboard should display and why

### Solution Walkthrough
- Step 1: Take prices {55, 68, 72, 75, 81, 84, 90, 96, 105, 118, 132, 210, 940}
- Step 2: `mean()` lands well above `median()` — the classic skew signature
- Step 3: `quartile1`, `quartile3`, `iqr` build the fence; 940 is flagged as an outlier
- Step 4: Removing the outlier barely moves `median` while the mean drops sharply — proving the median is the stable host-facing benchmark

### Code
```java
// Reuse SearchLatencyMonitor's statistics methods (same class shape).
public static void main(String[] args) {
    double[] prices = {55, 68, 72, 75, 81, 84, 90, 96, 105, 118, 132, 210, 940};
    double[] withoutLuxury = Arrays.copyOf(prices, prices.length - 1);
    System.out.printf("Mean: %.2f -> %.2f (luxury listing removed)%n",
        mean(prices), mean(withoutLuxury));
    System.out.printf("Median: %.2f -> %.2f%n", median(prices), median(withoutLuxury));
    System.out.printf("Q1=%.2f, Q3=%.2f, IQR=%.2f%n",
        quartile1(prices), quartile3(prices), iqr(prices));
}
```

### Expected Output
```
Mean: 163.54 -> 98.83 (luxury listing removed)
Median: 90.00 -> 87.00
Q1=73.50, Q3=125.00, IQR=51.50
```

---

## Problem 3: Payment Amount Variability — Company: Stripe
### Interview Scenario
"You're at Stripe monitoring fee revenue per merchant. You need a per-merchant variability score that is scale-free so a $2B merchant and a $20k merchant can be compared on the same chart."

### The Problem
1. Compute sample variance and standard deviation of a merchant's daily fee amounts
2. Normalize by the mean to get a scale-free coefficient of variation
3. Contrast population vs sample estimators on the same window

### Solution Walkthrough
- Step 1: Daily fees {980, 1010, 995, 1005, 1020, 990, 1015}
- Step 2: `sampleVariance` (divides by n-1, unbiased) vs `populationVariance` (divides by n) — the gap shrinks as the window grows
- Step 3: CV = sampleStdDev / mean, reported as a percentage, comparable across merchants

### Code
```java
public static void main(String[] args) {
    double[] fees = {980, 1010, 995, 1005, 1020, 990, 1015};
    System.out.printf("Pop variance: %.2f, Pop std: %.2f%n",
        populationVariance(fees), populationStdDev(fees));
    System.out.printf("Sample var:   %.2f, Sample std: %.2f%n",
        sampleVariance(fees), sampleStdDev(fees));
    System.out.printf("CV (sample):  %.2f%%%n", 100 * sampleStdDev(fees) / mean(fees));
}
```

### Expected Output
```
Pop variance: 177.55, Pop std: 13.32
Sample var:   207.14, Sample std: 14.39
CV (sample):  1.44%
```
