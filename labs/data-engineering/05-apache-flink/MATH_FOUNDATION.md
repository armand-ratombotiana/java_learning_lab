# Math Foundation

## Parallelism
`Throughput = Events / Time`
`Optimal Parallelism = min(SourcePartitions, Cores)`

## Checkpoint Cost
`Duration = StateSize / SnapshotBandwidth + AlignTime`

## Watermarks
`Watermark = min(EventTimes) - MaxOutOfOrderness`
`LateFraction = LateEvents / TotalEvents`
