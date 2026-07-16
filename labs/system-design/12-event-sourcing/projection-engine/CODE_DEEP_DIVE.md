# Projection Engine Code Deep Dive

This lab provides a pure Java simulation of an Event Projection loop, transforming basic bank account events into a current balance read model.

## 💻 Pure Java Implementation

```java file="labs/system-design/12-event-sourcing/projection-engine/SOLUTION/ProjectionEngineSim.java"
package systemdesign.eventsourcing;

import java.util.*;

/**
 * A simulation of an Event Projection Engine.
 */
public class ProjectionEngineSim {

    // 1. THE EVENTS (The immutable source of truth)
    record Event(long id, String type, Map<String, Object> data) {}

    // 2. THE READ MODEL (Optimized for balance queries)
    static class AccountBalanceModel {
        Map<String, Double> balances = new HashMap<>();
        long lastProcessedEventId = -1;
    }

    private final List<Event> eventStore = new ArrayList<>();
    private final AccountBalanceModel readModel = new AccountBalanceModel();

    public void emit(String type, Map<String, Object> data) {
        eventStore.add(new Event(eventStore.size(), type, data));
    }

    /**
     * The Projection Loop: Processes new events and updates the read model.
     */
    public void runProjection() {
        System.out.println("--- Starting Projection Run ---");
        
        for (Event event : eventStore) {
            // Skip already processed events (Checkpointing)
            if (event.id() <= readModel.lastProcessedEventId) continue;

            System.out.println("Processing Event #" + event.id() + ": " + event.type());
            
            String accountId = (String) event.data().get("accountId");
            double amount = (double) event.data().getOrDefault("amount", 0.0);

            // The Projection Logic
            switch (event.type()) {
                case "ACCOUNT_CREATED" -> readModel.balances.put(accountId, 0.0);
                case "MONEY_DEPOSITED" -> readModel.balances.merge(accountId, amount, Double::sum);
                case "MONEY_WITHDRAWN" -> readModel.balances.merge(accountId, -amount, Double::sum);
            }

            // Update Checkpoint
            readModel.lastProcessedEventId = event.id();
        }
    }

    public static void main(String[] args) {
        ProjectionEngineSim engine = new ProjectionEngineSim();

        // Simulate events occurring over time
        engine.emit("ACCOUNT_CREATED", Map.of("accountId", "ACC-1"));
        engine.emit("MONEY_DEPOSITED", Map.of("accountId", "ACC-1", "amount", 100.0));
        engine.emit("MONEY_DEPOSITED", Map.of("accountId", "ACC-1", "amount", 50.0));
        engine.emit("MONEY_WITHDRAWN", Map.of("accountId", "ACC-1", "amount", 30.0));

        engine.runProjection();
        System.out.println("Final Balance for ACC-1: " + engine.readModel.balances.get("ACC-1"));
    }
}
```