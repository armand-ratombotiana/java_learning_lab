# Meta DevOps/Production Engineer Interview Guide

> Comprehensive prep guide for Meta Production Engineer (PE), PE Network, and SRE roles.

---

## 1. Role Overview

### Production Engineer (PE)
- **Focus**: Hybrid of SRE and DevOps. Build and maintain infrastructure for Facebook, Instagram, WhatsApp, Messenger.
- **Expectation**: You write code AND manage infrastructure. No strict ops/dev split.
- **Levels**: E3 → E4 → E5 → E6 → E7 (Staff) → E8 (Principal).
- **Languages**: Python, C++, PHP, Hack, Go.

### Production Engineer (Network)
- **Focus**: Backbone, data center networking, BGP, SDN, Wedge/FBOSS.
- **Expectation**: Deep network engineering + software automation.
- **Key areas**: BGP, load balancing, CDN, optical networking.

### Site Reliability Engineer (Smaller Org)
- **Focus**: More traditional SRE within specific product teams.
- **Expectation**: Less common than PE at Meta.

---

## 2. Interview Process

```
Application → Recruiter Screen (30 min) → Coding Screen (45 min) 
→ Onsite (4 rounds, 45 min each) → Hiring Committee → Offer
```

### Recruiter Screen
- **Length**: 30 minutes.
- **Content**: Experience overview, role fit, logistics.
- **Tip**: Emphasize production debugging, automation projects, and operating at scale.

### Coding Screen
- **Length**: 45 minutes.
- **Format**: CoderPad (Python, C++, PHP, or Hack).
- **Difficulty**: LeetCode Medium.
- **Topics**: Arrays, strings, trees, hash maps.
- **Example**: "Valid parentheses," "Clone a graph," "Serialize a binary tree."
- **Expectation**: Clean working code. Discuss time/space complexity.

### Onsite Rounds

#### Round 1: System Design (45 min)
- **Topics**: Design Facebook News Feed, Messenger, Instagram Stories, WhatsApp, Video Upload.
- **Key areas**:
  - Feed: push vs pull, ranking, fanout.
  - Messenger: real-time messaging, presence, delivery guarantees.
  - Stories: ephemeral content, CDN, video processing.
  - Video upload: chunked upload, transcoding pipeline.
- **Meta-specific**:
  - MySQL, Memcached, TAO (graph), Cassandra.
  - Thrift (RPC), Hadoop, Hive, Presto, Scuba.
  - CDN: Akamai, Meta's own Edge.

#### Round 2: Coding (45 min)
- **Difficulty**: LeetCode Medium-Hard.
- **Topics**: Strings, dynamic programming, recursion, concurrency.
- **Expectation**: Production-quality code — helper functions, error handling.

#### Round 3: Coding (45 min)
- **Same difficulty**. May focus on concurrency.
- **Example**: "Implement a thread-safe counter," "Design a web crawler."

#### Round 4: Linux/Production Debugging (45 min)
- **Scenario-based debugging**:
  - "A service is running hot on CPU. Walk through debugging."
  - "MySQL replication lag is growing. Investigate."
  - "Network latency between data centers increased. Debug."
- **Tools expected**:
  - strace, ltrace, gdb, perf, flamegraphs.
  - tcpdump, tc, netstat, ss.
  - top, htop, iostat, vmstat, sar, dmesg.
  - /proc filesystem.
  - eBPF basics (BCC, bpftrace).
- **Meta context**: Tupperware, ZippyDB, Scuba, TAO.

---

## 3. Key Technical Areas

### Production Debugging
| Scenario | Approach |
|----------|----------|
| High CPU | `top` → `perf top` → `perf record` → flamegraph → code |
| Memory leak | `top` → `/proc/meminfo` → `valgrind` → `heaptrack` |
| High I/O | `iostat` → `iotop` → `blktrace` → storage config |
| Network latency | `ping` → `mtr` → `tcpdump` → Wireshark → BBR tuning |
| MySQL slow | `SHOW PROCESSLIST` → `EXPLAIN` → schema review |
| Apache Cassandra | `nodetool tpstats` → `nodetool cfstats` → GC tuning |

### Linux
| Topic | Depth | Example Question |
|-------|-------|------------------|
| strace | Expert | "How do you trace all system calls for a process?" |
| perf | Expert | "How do you generate a CPU flamegraph?" |
| eBPF | Deep | "How does bpftrace work? Write a one-liner to count syscalls." |
| Process lifecycle | Expert | "Explain process states. Zombie vs orphan." |
| Memory management | Expert | "What's the page cache? How do you clear it?" |
| Filesystem | Deep | "How do you check inode usage? What happens when it's full?" |
| cgroups | Deep | "How does Tupperware use cgroups?" |
| Namespaces | Deep | "What network namespaces are used for?" |

### Networking
| Topic | Depth | Example |
|-------|-------|---------|
| TCP/IP | Expert | "Explain slow start, congestion avoidance, fast retransmit." |
| BGP | Deep | "How does BGP choose the best path?" |
| Load balancing | Deep | "How does L4 vs L7 load balancing work? How does consistent hashing affect cache hit ratio?" |
| CDN | Deep | "How does a CDN serve content? How does cache invalidation work?" |
| DNS | Expert | "What's the difference between authoritative and recursive DNS?" |

### Databases
| Topic | Depth | Example |
|-------|-------|---------|
| MySQL | Expert | "How does replication work? How do you handle replication lag?" |
| Memcached | Expert | "How does consistent hashing work for Memcached?" |
| Cassandra | Deep | "Explain read repair, hinted handoff, compaction." |
| TAO (Graph) | Deep | "How does Meta handle social graph lookups at scale?" |

### Meta Infrastructure
| System | Type | Analogy |
|--------|------|---------|
| Tupperware | Container orchestration | Similar to Kubernetes (pre-dates K8s) |
| Maille | Container management | Docker-like |
| ZippyDB | Distributed KV | RocksDB-based |
| Scuba | Real-time analytics | Columnar, in-memory |
| TAO | Graph data store | Social graph lookup |
| Presto/Hive | Analytics | SQL on Hadoop |

---

## 4. Behavioral — Ownership Culture

Meta's behavioral expectations are less structured than Amazon (no formal LPs) but focus heavily on:
- **Ownership**: "Tell me about a project you owned from start to finish."
- **Proactivity**: "Describe a time you fixed something without being asked."
- **Conflict resolution**: "Tell me about a time you disagreed with a teammate."
- **Directness**: "How do you give constructive feedback?"

### Sample Questions
| Question | Key Theme |
|----------|-----------|
| "Tell me about your most impactful project." | Ownership, results |
| "Describe a time you made a difficult technical decision." | Judgment, ownership |
| "How do you handle production emergencies?" | Calm under pressure, leadership |
| "Tell me about a time your team disagreed." | Conflict resolution |
| "What's the hardest bug you've debugged?" | Technical depth, persistence |

---

## 5. System Design — Meta Focus

### Common Topics
- Design Facebook News Feed.
- Design Facebook Messenger.
- Design Instagram Stories.
- Design WhatsApp.
- Design Facebook Video Upload.
- Design a real-time comment system.

### Key Meta-specific Considerations
| Concern | How Meta Handles It |
|---------|---------------------|
| Global scale | 3B+ users. Using at Meta = planetary scale. |
| Real-time | Push-based fanout for active users, pull for inactive. |
| Ephemeral content | Stories disappear after 24h. Different storage tier. |
| Multimedia | Video, images, messages — optimized pipelines. |
| Data replication | TAO for graph, MySQL for structured, Scuba for analytics. |

---

## 6. Study Resources

### Books
- _Designing Data-Intensive Applications_ (Kleppmann).
- _Linux Kernel Development_ (Love).

### Online
- Meta Engineering Blog (engineering.fb.com).
- Facebook Developer Documentation.
- LeetCode Medium (arrays, strings, trees, DP).

---

## 7. Preparation Checklist

- [ ] Master production debugging (strace, perf, flamegraphs, eBPF).
- [ ] Deep dive Linux internals (process, memory, storage, network).
- [ ] Practice LeetCode Medium (50+ problems).
- [ ] System design: News Feed, Messenger, Stories, Video.
- [ ] Understand Meta-specific tools (Tupperware, TAO, ZippyDB, Scuba).
- [ ] Prepare ownership stories (3-4, quantified).
- [ ] Practice concurrency (thread safety, locks, semaphores).
- [ ] Review MySQL, Memcached, Cassandra.

---

_End of META_DEVOPS_INTERVIEW_GUIDE.md_