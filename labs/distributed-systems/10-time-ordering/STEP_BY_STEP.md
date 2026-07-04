# Time and Ordering: Step by Step

## Implementing a Lamport Clock

### Step 1: Create clock class
```java
class LamportClock {
    private int time = 0;
}
```

### Step 2: Implement tick for local events
```java
int tick() {
    return ++time;
}
```

### Step 3: Implement update for received messages
```java
void update(int receivedTime) {
    time = Math.max(time, receivedTime) + 1;
}
```

### Step 4: Attach to messages
```java
class Message {
    int timestamp;
    String content;
}
```

### Step 5: Compare timestamps
```java
// Send: timestamp = clock.tick()
// Receive: clock.update(message.timestamp)
```
