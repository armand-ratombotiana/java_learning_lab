# Distributed ID Generation: Step by Step

## Implementing a Distributed ID Generator

### Step 1: Define requirements
```java
// Requirements: 64-bit, time-sortable, unique per region
```

### Step 2: Design bit layout
```java
long timestampBits = 41L; // ~69 years
long regionBits = 4L;     // 16 regions
long workerBits = 6L;     // 64 workers per region
long sequenceBits = 12L;  // 4096 per ms
```

### Step 3: Implement generator
```java
public long nextId(long regionId, long workerId) {
    long timestamp = getTimestamp();
    long sequence = getSequence(timestamp);
    return (timestamp << 22) | (regionId << 18) | (workerId << 12) | sequence;
}
```

### Step 4: Handle clock skew
```java
if (timestamp < lastTimestamp) {
    // Report and wait
}
```

### Step 5: Test uniqueness
```java
Set<Long> ids = new HashSet<>(1_000_000);
for (int i = 0; i < 1_000_000; i++) {
    if (!ids.add(generator.nextId())) {
        System.out.println("Collision!");
    }
}
```
