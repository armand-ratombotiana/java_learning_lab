# Math Foundation

## Throughput
`ChangeRate = DML_ops / second`
`Lag = CurrentTime - LastCommittedTimestamp`
`CatchupTime = LagSize / (ConsumeRate - ChangeRate)`

## Event Size
`CDCEvent = KeySize + ValueSize + Metadata`
`NetworkLoad = CDCEvent * ChangeRate * Consumers`
