# Projection Engine Internals

## ⚙️ The Transformation Pipeline
A Projection Engine is a consumer of events. Its goal is to maintain a "Read Model" (a database optimized for specific queries).

### 1. Sequential Processing
Events must be processed in the exact order they occurred to ensure state consistency. 
- In a distributed system, this means the events for a specific aggregate (e.g., `User-123`) must all land in the same Kafka partition.

### 2. Checkpointing
The engine must remember which events it has already processed.
- It stores a **Sequence Number** (or Offset) in the same transaction as the read model update.
- If the engine crashes, it restarts from the last saved checkpoint, guaranteeing **Exactly-Once** processing (or effectively once via idempotency).

### 3. Blue-Green Projections
If you want to change your read model (e.g., add a new column), you don't migrate the existing table.
1. You create a brand new table (Version 2).
2. You start a new Projection Engine that replays *all* events from the beginning of time into Version 2.
3. Once Version 2 has caught up to the present, you flip the API to read from the new table.