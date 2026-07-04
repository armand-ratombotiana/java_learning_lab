# Math Foundation for Actor Model

## Throughput Calculation
```
Throughput = Actors × ThroughputPerActor
ThroughputPerActor = 1 / ProcessTimePerMessage

Example: 1000 actors, 1ms per message = 1,000,000 msg/s
```

## Mailbox Size
```
MailboxGrowth = ArrivalRate - (Throughput / Actors)
Stable system requires: ArrivalRate < Throughput

Example: 1000 msg/s arrival, 800 msg/s processing = mailbox grows 200/s
```

## Actor Count Scaling
```
OptimalActorCount ≈ TaskLatency / DesiredResponseTime
Too few: underutilized hardware
Too many: scheduling overhead dominates

Example: Task takes 100ms, want 10ms response
Optimal = 100ms / 10ms = 10 actors
```

## Supervision Tree Depth
```
Depth = log(Actors) / log(FanoutFactor)
Deeper = more isolation, more overhead
Rule of thumb: depth 3-5 levels

Example: 1000 actors, fanout=10
Depth = log(1000) / log(10) = 3 levels
```

```java
public class ActorModelMath {

    public long maxThroughput(int actorCount, long processTimePerMsg) {
        return actorCount * (1000L / processTimePerMsg);
    }

    public int optimalActorCount(long taskLatency, long desiredResponse) {
        return (int) Math.ceil((double) taskLatency / desiredResponse);
    }
}
```
