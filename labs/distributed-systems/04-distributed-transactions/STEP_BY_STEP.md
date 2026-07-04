# Distributed Transactions: Step by Step

## Implementing a 2PC Coordinator

### Step 1: Define transaction log
```java
class TransactionLog {
    enum State { INIT, PREPARED, COMMITTED, ABORTED }
    String txId;
    State state;
    List<String> participants;
}
```

### Step 2: Implement prepare phase
```java
boolean prepare() {
    log.write(txId, PREPARE_SENT);
    for (participant : participants) {
        boolean ok = participant.prepare(txId, payload);
        if (!ok) return abort();
    }
    log.write(txId, ALL_PREPARED);
    return true;
}
```

### Step 3: Implement commit phase
```java
boolean commit() {
    log.write(txId, COMMIT_SENT);
    for (participant : participants) {
        participant.commit(txId);
    }
    log.write(txId, COMMITTED);
    return true;
}
```

### Step 4: Add timeout handling
```java
class TimeoutHandler extends TimerTask {
    public void run() {
        if (inDoubtTransactions.size() > 0) {
            // Query participants for transaction status
            abort(inDoubtTransactions);
        }
    }
}
```
