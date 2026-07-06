# Challenge: Generate and Analyze GC Logs

## Problem
Create an allocation workload that produces specific GC behaviors, then analyze the resulting GC logs to identify the behavior. This tests your understanding of how each collector's internal mechanisms manifest in log output.

## Behaviors to Generate

### 1. Promotion Failure (G1)
Create objects that survive many young collections but are too large for the old gen. Look for "to-space overflow" and "promotion failed" in the log.

### 2. Humongous Allocation (G1)
Allocate objects larger than 50% of a G1 region (>512 KB with default region size). Look for "Pause Young (Humongous Allocation)" and "humongous allocation" in the log.

### 3. Concurrent Cycle (G1/ZGC)
Allocate at a rate that triggers concurrent marking. Look for "Concurrent Mark" phases (G1) or concurrent mark/relocate phases (ZGC).

### 4. Full GC (Parallel/G1)
Allocate aggressively until a full GC is triggered. Look for "Full GC" in the log and note the pause time difference.

### 5. Allocation Stall (ZGC)
Identify allocation stalls (when ZGC can't keep up). Look for "Allocation Stall" in ZGC logs.

### 6. System.gc() vs ExplicitGCInvokesConcurrent
Compare behavior of explicit GC with different flag combinations.

## Deliverables
- Java program(s) that reliably trigger each behavior
- GC logs (with `-Xlog:gc*`) for each collector
- Annotated log analysis explaining each event
- Recommended tuning to avoid each pathological behavior

## Success Criteria
- Each behavior is confirmed by actual GC log entries (not hypothesized)
- Log annotations correctly reference GC phases and their meaning
- Tuning recommendations are backed by documentation or experiments
