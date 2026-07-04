# How Distributed Transactions Work

## Two-Phase Commit (2PC)

```java
public class TwoPhaseCommitCoordinator {
    private final List<Participant> participants;
    
    public boolean executeTransaction(Transaction tx) {
        // Phase 1: Prepare
        List<Boolean> votes = new ArrayList<>();
        for (Participant p : participants) {
            try {
                votes.add(p.prepare(tx));
            } catch (Exception e) {
                votes.add(false);
            }
        }
        
        // Phase 2: Commit or Abort
        boolean allYes = votes.stream().allMatch(v -> v);
        for (int i = 0; i < participants.size(); i++) {
            if (allYes) {
                participants.get(i).commit();
            } else {
                participants.get(i).abort();
            }
        }
        
        return allYes;
    }
}

interface Participant {
    boolean prepare(Transaction tx);
    void commit();
    void abort();
}
```

## SAGA Pattern

```java
public class SagaOrchestrator {
    private final List<SagaStep> steps = new ArrayList<>();
    private final Stack<Integer> executedSteps = new Stack<>();
    
    public void execute() {
        for (int i = 0; i < steps.size(); i++) {
            SagaStep step = steps.get(i);
            try {
                step.execute();
                executedSteps.push(i);
            } catch (Exception e) {
                // Compensate in reverse order
                while (!executedSteps.isEmpty()) {
                    int failedIndex = executedSteps.pop();
                    steps.get(failedIndex).compensate();
                }
                throw new SagaException("Transaction failed", e);
            }
        }
    }
}
```
