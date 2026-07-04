# Time and Ordering: Code Deep Dive

## Causal Broadcast with Vector Clocks

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class CausalBroadcast {
    private final String processId;
    private final Map<String, Integer> clock = new ConcurrentHashMap<>();
    private final List<CausalBroadcast> peers;
    private final Queue<Message> pending = new PriorityQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    
    public CausalBroadcast(String processId, List<CausalBroadcast> peers) {
        this.processId = processId;
        this.peers = peers;
        clock.put(processId, 0);
    }
    
    public void broadcast(String content) {
        lock.lock();
        try {
            clock.merge(processId, 1, Integer::sum);
            Map<String, Integer> snapshot = new HashMap<>(clock);
            Message msg = new Message(content, processId, snapshot);
            
            for (CausalBroadcast peer : peers) {
                peer.receive(msg);
            }
        } finally {
            lock.unlock();
        }
    }
    
    public synchronized void receive(Message msg) {
        pending.add(msg);
        deliverReadyMessages();
    }
    
    private void deliverReadyMessages() {
        while (!pending.isEmpty()) {
            Message msg = pending.peek();
            if (canDeliver(msg)) {
                pending.poll();
                deliver(msg);
                clock.merge(msg.senderId, 0, Math::max);
            } else {
                break;
            }
        }
    }
    
    private boolean canDeliver(Message msg) {
        for (Map.Entry<String, Integer> entry : msg.clock.entrySet()) {
            String pid = entry.getKey();
            int required = entry.getValue();
            if (pid.equals(msg.senderId)) {
                // Sender's own clock should be exactly one ahead
                if (clock.getOrDefault(pid, 0) != required - 1) return false;
            } else {
                if (clock.getOrDefault(pid, 0) < required) return false;
            }
        }
        return true;
    }
    
    private void deliver(Message msg) {
        System.out.println("[" + processId + "] Delivered: " + msg.content
            + " from " + msg.senderId);
    }
    
    static class Message implements Comparable<Message> {
        final String content;
        final String senderId;
        final Map<String, Integer> clock;
        
        Message(String content, String senderId, Map<String, Integer> clock) {
            this.content = content;
            this.senderId = senderId;
            this.clock = new HashMap<>(clock);
        }
        
        public int compareTo(Message o) {
            return Integer.compare(
                this.clock.values().stream().mapToInt(Integer::intValue).sum(),
                o.clock.values().stream().mapToInt(Integer::intValue).sum());
        }
    }
}
```
