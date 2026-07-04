# Distributed ID Generation: Internals

## Snowflake Bit Layout

```
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
├─┼──────────────────────────────────────┼──────────┼──────────┼───────┤
│S│           Timestamp (41)             │  DC (5)  │ Work(5) │Seq(12)│
└─┴──────────────────────────────────────┴──────────┴──────────┴───────┘
```

* S = Sign bit (always 0)
* Timestamp = milliseconds since custom epoch
* DC = Datacenter ID
* Work = Worker/Node ID
* Seq = Sequence number per millisecond

## Clock Drift Handling

### Problem
Snowflake relies on monotonically increasing system clocks. Clock drift can cause non-unique or out-of-order IDs.

### Solutions
1. **Clock skew detection**: Throw exception or wait
2. **ZooKeeper-based sequence**: Use ZooKeeper for sequence allocation
3. **Hybrid clocks**: Use logical clocks (HLC) instead of wall clock
4. **Reserved sequence bits**: Use sequence bits to handle clock skew up to N milliseconds

## Database Sequence Alternatives

### Hi/Lo Algorithm
```java
public class HiLoGenerator {
    private final int maxLo = 1000;
    private long hi;
    private long lo = -1;
    
    public synchronized long nextId() {
        if (lo < 0 || lo >= maxLo) {
            hi = database.nextSequenceValue();
            lo = 0;
        }
        return hi * maxLo + lo++;
    }
}
```
