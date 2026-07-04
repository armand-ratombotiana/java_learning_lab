# Failure Detection: Step by Step

## Implementing a Heartbeat Detector

### Step 1: Define heartbeat message
```java
class Heartbeat {
    String nodeId;
    long timestamp;
    long sequenceNumber;
}
```

### Step 2: Create heartbeat sender
```java
class HeartbeatSender {
    ScheduledExecutorService scheduler;
    
    void start() {
        scheduler.scheduleAtFixedRate(() -> {
            broadcast(new Heartbeat(nodeId, System.currentTimeMillis(), seq++));
        }, 0, 1, TimeUnit.SECONDS);
    }
}
```

### Step 3: Create heartbeat receiver
```java
class HeartbeatReceiver {
    Map<String, Long> lastHeartbeat = new ConcurrentHashMap<>();
    
    void receive(Heartbeat hb) {
        lastHeartbeat.put(hb.nodeId, hb.timestamp);
    }
}
```

### Step 4: Implement failure checker
```java
class FailureChecker {
    void check() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : lastHeartbeat.entrySet()) {
            if (now - entry.getValue() > TIMEOUT) {
                onFailure(entry.getKey());
            }
        }
    }
}
```

### Step 5: Handle detected failure
```java
void onFailure(String nodeId) {
    membership.remove(nodeId);
    triggerLeaderElection();
    rebalancePartitions();
}
```
