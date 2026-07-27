# Mock Interview: Scalability

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Senior Engineer Interviewer  
**Candidate Level**: Senior Engineer (E5/L5)  
**Problem**: Design a system that can scale from 10K to 10M DAU.

---

## Transcript

**Interviewer**: "Design Twitter's timeline service. Start with 10K users, then scale to 10M. Show me how the architecture evolves."

**Candidate**: "At 10K users, a simple approach works: single web server, single PostgreSQL database. Timeline generated on-read: query database for recent posts from followed users, order by timestamp. Cache results for 30 seconds."

**Interviewer**: "Good. Now scale to 100K users. What breaks?"

**Candidate**: "Database becomes bottleneck. Add read replicas — web server reads from replicas, writes to master. Add Redis cache for timeline results — user's timeline cached for 60 seconds, invalidated on new post from followed users."

**Interviewer**: "1M users?"

**Candidate**: "At 1M users (10K QPS reads, 500 QPS writes), we need sharding. Shard database by user_id hash. Each shard has its own master + replicas. Add a fanout-on-write approach: when a user posts, push tweet IDs to all followers' timeline caches. This shifts read cost to write time."

**Interviewer**: "10M users — with some celebrities having 1M+ followers?"

**Candidate**: "Fanout-on-write breaks for celebrities — writing to 1M followers' caches is too slow. Hybrid approach: regular users (<10K followers) get push fanout, celebrities get pull fanout. When a celebrity tweets, store the tweet with a marker. When users load their timeline, the timeline service merges: cached regular content + on-the-fly celebrity content."

**Interviewer**: "How do you decide the fanout threshold?"

**Candidate**: "It's calculated based on: max acceptable fanout latency (e.g., 500ms for the write to propagate) divided by the average cache write time. If each cache write takes 1ms, we can fanout to 500 followers in 500ms. Threshold would be around 5,000 followers per user for push fanout."

**Interviewer**: "What about geo-distribution?"

**Candidate**: "At 10M users, deploy to multiple regions. Each region has its own deployment (app + DB + cache). Cross-region replication for user data. Timeline reads are region-local, writes propagate asynchronously."

---

## Key Takeaways

- **Start simple**: 10K users doesn't need distributed systems
- **Read replicas**: First scaling step before sharding
- **Sharding**: Hash-based for even distribution
- **Hybrid fanout**: Push for small accounts, pull for celebrities
- **Threshold calculation**: Data-driven decision based on latency budget
