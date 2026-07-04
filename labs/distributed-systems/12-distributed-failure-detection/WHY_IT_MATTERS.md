# Why Failure Detection Matters

## Business Impact
- **Availability**: Fast detection enables quick failover
- **Reliability**: Accurate detection prevents false failovers
- **Cost**: Premature failure declaration causes unnecessary failover cost
- **User Experience**: Failures caught early reduce service disruption

## Technical Impact
- **False positives**: Can trigger unnecessary leader elections
- **False negatives**: Delayed recovery, data may be inaccessible
- **Detection latency**: Tradeoff between speed and accuracy
- **Network overhead**: More frequent heartbeats = more network traffic

## Key Insight
Perfect failure detection is impossible in asynchronous systems (FLP). Practical detectors trade speed for accuracy.
