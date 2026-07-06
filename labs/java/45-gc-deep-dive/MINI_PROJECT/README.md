# Mini Project: GC Simulator

## Objective
Build a simulator that models garbage collection algorithms to visualize how different collectors work. The simulator tracks object allocation, reference graphs, and collection events.

## Requirements
1. Simulate a heap divided into regions (like G1) or generations (like Parallel)
2. Track objects with: size (bytes), age (surviving collections), references to other objects
3. Implement a simple mark-sweep collector: trace from roots, sweep unreachable objects
4. Implement a copying collector (like Parallel Scavenge): copy survivors to a "to" space
5. Report collection statistics: pause time, bytes freed, survivors promoted

## Advanced Features
- Simulate remembered sets (G1): track cross-region references
- Simulate SATB (Snapshot-At-The-Beginning) concurrent marking
- Simulate colored pointers (ZGC): use bit flags in object references
- Visualize collection using ASCII art or JavaFX/Swing

## Example Output
```
=== GC Simulation Cycle 5 ===
  Before: 845 objects, 12.4 MB live
  Roots: 23 (8 stack, 12 static, 3 JNI)
  Mark phase: 1.2 ms (traced 845 objects)
  Sweep phase: 0.8 ms (freed 312 objects, 4.1 MB)
  After: 533 objects, 8.3 MB live
  Promoted: 89 objects to old gen
  Pause time: 2.0 ms
```

## Deliverables
- `GcSimulator.java` — core simulation engine
- `MarkSweepCollector.java` — mark-sweep algorithm
- `CopyingCollector.java` — semispace copying algorithm
- `GcSimulatorTest.java` — test with known object graphs
- Visualization output or report
