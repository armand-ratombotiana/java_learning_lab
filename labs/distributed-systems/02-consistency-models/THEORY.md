# Consistency Models: Theory

## Hierarchy of Consistency Models

### Strongest to Weakest:

1. **Linearizability** (Strongest)
   - Operations appear to execute atomically in real-time order
   - Once a write completes, all subsequent reads see it

2. **Sequential Consistency**
   - Operations from each process appear in program order
   - Total order exists across all processes

3. **Causal Consistency**
   - Causally related operations are seen in order
   - Concurrent operations may be seen in different orders

4. **PRAM (Pipelined RAM)**
   - Writes from each process are seen in order
   - Writes from different processes can interleave arbitrarily

5. **Read-Your-Writes Consistency**
   - A process always sees its own writes

6. **Monotonic Read Consistency**
   - Once a process reads a value, future reads return that or newer

7. **Monotonic Write Consistency**
   - Writes from a process are applied in order

8. **Eventual Consistency** (Weakest)
   - Given enough time without updates, all replicas converge
