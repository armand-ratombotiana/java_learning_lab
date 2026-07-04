# Math Foundation

## Storage
`ComputeCost = Sum(FeatureCost) * UpdateFrequency`
`Online = Entities * Features * AvgValueSize`
`Offline = Events * Features * Size * Retention`

## Serving
`Online Latency = Network + Cache + Serialization (P99 < 10ms)`
`Offline Throughput = ScannedData / ComputeTime (1TB / 30min)`
