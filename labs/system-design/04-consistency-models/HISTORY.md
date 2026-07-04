# Consistency Models - HISTORY

## Timeline

### 1970s: ACID Birth
- System R (IBM) formalizes ACID properties
- Two-phase commit for distributed transactions
- Strict serializability as gold standard

### 1980s: Weak Consistency Exploration
- 1985: Lamport's logical clocks
- 1989: David Giffard proposes quorum systems
- NFS with client-side caching → cache inconsistency issues

### 1990s: Distributed Databases
- 1997: Oracle RAC (Real Application Clusters) — cache fusion
- Dynamo (pre-Amazon paper) — eventually consistent KV store
- DNS already using eventual consistency

### 2000s: CAP Theorem Era
- 2000: Eric Brewer presents CAP theorem at PODC
- 2002: Gilbert & Lynch prove CAP theorem
- 2004: Amazon Dynamo paper — eventual consistency at scale
- 2006: Google Bigtable — strong consistency within tablet
- 2007: Amazon SimpleDB (consistency trade-offs)

### 2010s: Linearizability & Consensus
- 2012: Google Spanner — TrueTime + global consistency
- 2013: Raft consensus algorithm (Ongaro & Ousterhout)
- 2014: CockroachDB — distributed SQL with serializable isolation
- 2015: Jepsen testing — systematic consistency verification
- 2017: Amazon DynamoDB Transactions — strong consistency add-on

### 2020s: Hybrid Models
- Causal consistency in production (MongoDB, Redis)
- Conflict-free replicated data types (CRDTs) for collaboration
- Galaxy (Meta) — globally distributed, causally consistent DB
- Deterministic databases reduce coordination

## Java's Role
- ZooKeeper (2009) — Zab consensus in Java
- Apache Cassandra (2010) — tunable consistency in Java
- JGroups for reliable multicast
- Atomix (2017) — Raft in Java
