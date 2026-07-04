# Visual Guide

## Topology
```
[Source: orders] -> [Join: customers] -> [EnrichedOrder]
                     |                   |
                     |         [GroupBy region] -> [Count] -> [Sink]
                     |         [Filter: amount>10000] -> [Alert] -> [Sink]
```

## Stream-Table Duality
KStream: append-only log. KTable: latest value per key.
