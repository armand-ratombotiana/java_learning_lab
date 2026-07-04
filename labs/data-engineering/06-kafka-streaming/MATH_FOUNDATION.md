# Math Foundation

## Throughput
`Records/Sec = BatchSize / ProcessingTime`
`MaxThroughput = Partitions * PerPartitionThroughput`

## State Size
`StateSize = Keys * ValueSize * (1 + Overhead)`
`ChangelogSize = StateSize * UpdateRate * Retention`
