# Distributed Transactions: Internals

## 2PC State Machine

### Coordinator States
```
INIT → WAITING → PREPARED → COMMITTED
                     ↓
                  ABORTED
```

### Participant States
```
INIT → PREPARED → COMMITTED
          ↓
       ABORTED
```

## Recovery After Coordinator Failure
- Participants with prepared but uncertain state must contact coordinator
- Coordinator writes transaction log before each step
- Recovery coordinator reads log and resolves pending transactions

## XA Interface (Java)
```java
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class JDBCXAResource {
    public void xaCommit(Xid xid, boolean onePhase) {
        // XA commit logic
    }
    
    public void xaPrepare(Xid xid) {
        // XA prepare logic
    }
    
    public void xaRollback(Xid xid) {
        // XA rollback logic
    }
}
```
