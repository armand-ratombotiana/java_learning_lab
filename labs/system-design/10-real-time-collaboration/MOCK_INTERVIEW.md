# Mock Interview: Real-Time Collaboration

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Collaboration Platform Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a real-time collaborative document editing system like Google Docs.

---

## Transcript

**Interviewer**: "Design a system that allows multiple users to edit a document simultaneously. Changes should appear in near-real-time, and conflicts must be resolved."

**Candidate**: "This is a classic OT (Operational Transformation) or CRDT (Conflict-free Replicated Data Type) problem. I'll design using OT, which is what Google Docs uses. The server maintains the authoritative document state, and clients send operations that the server transforms and broadcasts."

**Interviewer**: "Walk me through the architecture."

**Candidate**: "Components: 1) WebSocket server for real-time communication (persistent connections), 2) Document Service that maintains document state and applies operations, 3) OT Engine for transformation logic, 4) Storage Layer for persistence. The flow: Client sends operation → Server transforms against concurrent ops → Applies to document → Broadcasts to other clients."

**Interviewer**: "Explain Operational Transformation."

**Candidate**: "When two users edit the same document concurrently, their operations may conflict. Example: User A inserts 'X' at position 3. User B inserts 'Y' at position 5. If A's op is applied first, the document shifts, and B's position 5 is wrong. OT transforms B's operation: 'insert Y at 6' (accounting for A's insert). The server maintains revision history to compute transformations."

**Interviewer**: "What about CRDTs as an alternative?"

**Candidate**: "CRDTs are simpler from a systems perspective — no central transformation needed. Each replica independently applies operations and converges to the same state. For text editing, a sequence CRDT (like Logoot or RGA) assigns unique identifiers to each character. The downside: CRDT metadata per character can be large (40+ bytes per char vs OT's minimal state). Google Docs uses OT because it's more space efficient for large documents."

**Interviewer**: "How do you handle conflict resolution for collaborative editing?"

**Candidate**: "OT resolves conflicts automatically. But for some data types (e.g., formatting, images), automated resolution is harder. Strategy: 1) Character-level insertion/deletion: OT handles perfectly. 2) Formatting conflicts: last-writer-wins per span. 3) Structural conflicts (merging table cells): ask user to resolve."

**Interviewer**: "How do you handle undo?"

**Candidate**: "Undo is surprisingly complex in collaborative editing. Each client maintains its own undo stack of operations it has sent. When a user undoes, the client sends a 'undo' operation. The server transforms it against concurrent operations to determine what to reverse. This ensures that undo reverses the user's own changes, not someone else's."

**Interviewer**: "How do you make this resilient to network issues?"

**Candidate**: "Client-side operation queue: operations are queued locally and sent when the connection is available. The server acknowledges each operation. If the connection drops, the client reconnects and replays unacknowledged operations. The server deduplicates by operation ID. For long disconnections, the client receives a full snapshot and re-calculates state."

---

## Key Takeaways

- **OT vs CRDT**: OT for space-efficient text editing (Google Docs style)
- **WebSocket transport**: Persistent connections for low-latency updates
- **Server-authoritative**: Server transforms and broadcasts operations
- **Operation queue**: Client queues operations during disconnection
- **Undo in collaboration**: Transformed undo that reverses the user's own changes
- **Snapshot + replay**: Rejoin after long disconnection
