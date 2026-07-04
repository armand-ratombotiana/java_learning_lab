# Actor Model Interview Questions

## Junior Level

### Q: What is the Actor Model?
**A:** A conceptual model for concurrent computation where actors are the fundamental units. Actors encapsulate state and behavior, communicate exclusively through asynchronous message passing, and process messages one at a time.

### Q: How is actor model different from object-oriented programming?
**A:** In OOP, objects communicate via method calls (synchronous, shared memory). In actor model, actors communicate only via messages (asynchronous, no shared state). Actors are inherently concurrent; objects require explicit thread management.

## Mid Level

### Q: How does fault tolerance work in actor model?
**A:** Through supervision hierarchies. Each actor has a supervisor (parent). When an actor fails, its supervisor decides the recovery strategy: resume (continue), restart (create new instance), stop (terminate), or escalate (pass to parent supervisor).

### Q: What is Akka Cluster and how does it scale?
**A:** Akka Cluster allows actors to be distributed across multiple nodes. Cluster sharding distributes stateful actors across nodes. Cluster singleton ensures one instance of an actor exists in the cluster. Cluster aware routers distribute messages across node boundaries.

## Senior Level

### Q: Design a real-time chat system using actor model.
**A:**
- ChatRoomActor per room (manages participants, message history)
- UserSessionActor per connected user (handles user state, connection)
- ChatSupervisor manages room lifecycle
- Cluster sharding distributes rooms across nodes
- Pub-sub for cross-room notifications
- Persistence for message history
- Supervision handles user disconnection and room errors

### Q: How do you handle backpressure in actor systems?
**A:** Use Akka Streams with backpressure protocol. Actors can implement mailbox with bounded capacity. Use throttling messages to slow down producers. Stash pattern to temporarily buffer messages. Circuit breaker pattern to stop accepting messages when overloaded.
