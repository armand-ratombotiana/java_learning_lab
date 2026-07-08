# Math Foundation for Apache Beam

## Throughput
```
ElementsPerSecond = TotalElements / WallTime
BundleThroughput = BundleSize / BundleProcessingTime
Parallelism = NumWorkers * CoresPerWorker
```

## Windowing Math
```
Fixed Windows: NumWindows = TotalTime / WindowSize
Sliding Windows: NumWindows = TotalTime / SlidePeriod
Session Windows: Variable, depends on gap duration and event distribution
```

## Watermark
```
Watermark(t) = min(event_timestamps_processed) + expected_skew
Skew = max expected out-of-orderness
LateData = events with timestamp < watermark
```

## Trigger Latency
```
OnTime = WindowEnd - WatermarkArrival
Early = OnTime - EarlyFiringPeriod
Late = OnTime + AllowedLateness
```
