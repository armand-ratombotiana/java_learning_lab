# Math Foundation

## Throughput
TPS = N / dt; Total = Sum(TP_i) for i workers

## Latency
Total = T_extract + T_transform + T_load + T_queue

## Amdahl's Law
Speedup = 1 / ((1-P) + P/N) where P = parallelizable fraction

## Capacity
```java
public static double calcNodes(long volGB, double rate, double sla) {
    return Math.ceil(volGB / 24.0 / rate * 24.0 / sla);
}
```
