# Mock Interview: Consistency Models

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Distributed Systems Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a globally distributed key-value store with configurable consistency levels.

---

## Transcript

**Interviewer**: "We need a key-value store deployed across 3 regions (US, EU, Asia). Users should be able to choose either strong or eventual consistency per request. Design the system."

**Candidate**: "Let me start with the data model. Key-value pairs with version metadata. Each write carries a timestamp (or vector clock for causality tracking). The system provides both consistency modes depending on the read request's consistency header."

**Interviewer**: "How does strong consistency work across regions?"

**Candidate**: "Strong consistency requires a consensus protocol. I'd use Raft within each region's replica group, and across regions, reads go to the region designated as the leader for that key's partition. The leader region serves strong reads. We assign key ranges to a 'home region' — writes always go to the home region first, then replicate asynchronously to other regions."

**Interviewer**: "What happens if the home region is unreachable from another region?"

**Candidate**: "Strong reads from other regions would fail. The client must either accept eventual consistency or retry. This is by design — strong consistency requires availability trade-offs (CAP theorem). Clients mark requests with `consistency: strong` or `consistency: eventual`. Strong reads go to the home region; eventual reads go to the local region."

**Interviewer**: "How do you detect and resolve conflicts in eventual consistency mode?"

**Candidate**: "We use vector clocks to track causality. Each replica maintains a version vector. When two writes conflict (concurrent writes to the same key), the system stores both versions and returns them to the client. The client resolves or uses LWW (last-writer-wins) with timestamp. For automated resolution, CRDTs work for counters and sets."

**Interviewer**: "Let's discuss read repair."

**Candidate**: "When reading with eventual consistency, we query multiple replicas (configurable — e.g., 2 of 3), compare their versions, and if any replica is stale, we update it with the latest version. This is called read repair — it ensures eventual convergence. Combined with anti-entropy (background process comparing replicas), the system converges even without client writes."

**Interviewer**: "How would you implement session consistency (read-your-writes)?"

**Candidate**: "On write, return a session token containing the write timestamp. The client passes this token on subsequent reads. The read coordinator ensures the replica it reads from has processed all writes up to that timestamp. If not, it waits for replication or reads from the leader."

---

## Key Takeaways

- **Home region**: Strong consistency requires a single authority per partition
- **Vector clocks**: Track causality without a global clock
- **Consistency on the wire**: Client specifies consistency per request
- **Read repair**: Self-healing during reads
- **Session consistency**: Simple read-your-writes via session tokens
