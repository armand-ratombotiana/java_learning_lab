# Google SRE/DevOps Interview Guide

> Comprehensive prep guide for Google SRE, Software Engineer Infrastructure, and Cloud DevOps Engineer roles.

---

## 1. Role Overview

### Site Reliability Engineer (SRE)
- **Classic SRE**: 50% ops, 50% software engineering.
- **Responsibilities**: SLI/SLO/SLA definition, incident response, capacity planning, toil reduction, production debugging.
- **Levels**: SRE I → II → III → Staff → Principal.
- **Expectation**: You write code to solve operations problems at scale.

### Software Engineer, Infrastructure
- **Focus**: Build internal tooling, cluster management (Borg/Omega), networking stack, storage systems.
- **Difference from SRE**: Less on-call, more feature development on infrastructure software.
- **Expectation**: Distributed systems expertise, system design, hard algorithms.

### Cloud DevOps Engineer (Google Cloud)
- **Focus**: Customer-facing — helps enterprises adopt GCP.
- **Skills**: Terraform, CI/CD, Kubernetes (GKE), migrations.
- **Difference from SRE**: More customer interaction, less deep infrastructure work.

---

## 2. Interview Process

```
Application → Recruiter Screen (30 min) → Phone Screen (45 min) 
→ Onsite (4-5 rounds, 45 min each) → Hiring Committee → Offer
```

### Recruiter Screen
- **Length**: 30 minutes.
- **Content**: Resume walkthrough, role fit, logistics.
- **Tip**: Prepare 3-4 talking points. Quantify impact in your resume.

### Phone Screen
- **Length**: 45 minutes.
- **Format**: Google Docs (no syntax highlighting, no autocomplete).
- **Content**: 1-2 LeetCode Medium problems + systems fundamentals.
- **Example questions**:
  - "Write a function that checks if a string is a palindrome."
  - "What happens when you type google.com in a browser?"
- **Tip**: Think out loud. The interviewer cares about your thought process.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Hard.
- **Topics**: Graphs (DFS, BFS, topological sort), dynamic programming, trees, arrays.
- **Expectation**: Optimal time/space complexity. Clean code. Edge cases.
- **Language**: C++, Java, Python, Go.

#### Round 2: Coding (45 min)
- **Same format**. May include concurrency or system-level coding.
- **Example**: "Design a thread-safe cache." "Implement a file system API."

#### Round 3: System Design (45 min)
- **Topics**: Design YouTube, Google Drive, Google Search, Google Maps, Gmail.
- **Key areas**: Sharding, replication, caching, consistency models, load balancing.
- **Expectation**: 10 minutes clarifying requirements, 20 minutes high-level design, 15 minutes deep dive.
- **Google-specific**: Mention Colossus/GFS, Bigtable, Spanner, Borg, Omega.

#### Round 4: SRE Technical (45 min)
- **Linux internals**: Process scheduling (CFS, nice), memory management (page cache, swap, THP), filesystem (ext4, XFS, inodes), cgroups, namespaces, capabilities.
- **Networking**: TCP congestion control (CUBIC, BBR), HTTP evolution, BGP, anycast, DNS.
- **Debugging**: strace, perf, eBPF, bpftrace, tcpdump, iptables/nftables.
- **Example**: "A service is experiencing latency spikes. Walk through debugging step by step."

#### Round 5: Googleyness & Leadership (45 min)
- **Behavioral questions**:
  - "Tell me about a time you had to lead without authority."
  - "Tell me about a time you disagreed with your manager."
  - "Describe an incident you led. What was your role?"
  - "How do you handle ambiguous situations?"
- **Tip**: Show humility, collaboration, and data-driven decision making.

---

## 3. Key Technical Areas

### Linux
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Process lifecycle | Deep | "What happens during `fork()`? What's a zombie process?" |
| cgroups & namespaces | Deep | "How does Docker use namespaces?" |
| OOM killer | Deep | "How do you configure `oom_score_adj`?" |
| eBPF | Deep | "What tools use eBPF? How would you trace a syscall?" |
| Performance tools | Expert | "A process is using 100% CPU. Debug it." |
| Systemd | Moderate | "How do you create a custom systemd service?" |

### Networking
| Topic | Depth | Example Question |
|-------|-------|------------------|
| TCP/IP stack | Expert | "Explain TCP slow start. How does BBR differ from CUBIC?" |
| HTTP/2 vs HTTP/3 | Deep | "What's multiplexing? How does QUIC solve HOL blocking?" |
| DNS | Expert | "What happens during a recursive DNS lookup?" |
| Load balancing | Deep | "Compare L4 vs L7 load balancing. How does Maglev route traffic?" |
| BGP | Moderate | "What's an ASN? How does BGP route to the nearest region?" |

### Distributed Systems
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Consensus (Paxos, Raft) | Deep | "Explain Raft leader election. What happens during a network partition?" |
| CAP theorem | Expert | "Is etcd CP or AP? Why?" |
| Replication | Deep | "Compare single-leader vs multi-leader replication." |
| Sharding | Deep | "How would you shard a database to handle 10TB of data?" |
| Caching | Deep | "Design a distributed cache. What's consistent hashing?" |

### Kubernetes (Borg Context)
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Architecture | Deep | "Explain the control plane components." |
| Scheduling | Expert | "How does the scheduler bin-pack pods?" |
| Networking | Deep | "How does kube-proxy work? Compare iptables vs IPVS." |
| Storage | Deep | "Explain PersistentVolume claim lifecycle." |

---

## 4. SRE Technical Deep Dives

### Debugging Scenarios
1. **High CPU**: Use top → perf → flamegraph → code review.
2. **Memory leak**: Use top → /proc/meminfo → valgrind → heap profiler.
3. **Network latency**: Use ping → mtr → tcpdump → Wireshark → BBR tuning.
4. **Disk I/O bottleneck**: Use iostat → iotop → blktrace → RAID/SSD tuning.
5. **Application slow response**: Check logs → metrics → tracing → dependency chain.

### Incident Response
1. **Detection**: Monitoring alert or user report.
2. **Triage**: Determine severity, assign IC.
3. **Mitigation**: Rollback, scale, redirect traffic, feature flag off.
4. **Resolution**: Permanent fix deployed.
5. **Postmortem**: Timeline, root cause, action items.

---

## 5. Googleyness & Leadership

### Core Values
- **Focus on the user**: All decisions start with user impact.
- **Respect each other**: Assume good intent.
- **Ambition**: Think 10x, not 10%.
- **Humility**: You can always learn.

### Common Questions
| Question | Key Theme |
|----------|-----------|
| "Tell me about a time you disagreed with your team." | Have backbone, disagree and commit |
| "Describe a project with ambiguous requirements." | Handling ambiguity |
| "Tell me about a time you failed." | Humility, learning |
| "How do you influence without authority?" | Leadership, collaboration |
| "Describe a time you took a technical risk." | Calculated risk-taking |

---

## 6. Study Resources

### Books
- _Site Reliability Engineering_ (O'Reilly) — Chapters 1-6, 8, 11-14.
- _The Site Reliability Workbook_ — Practical SRE implementation.
- _Designing Data-Intensive Applications_ (Kleppmann) — Distributed systems.
- _Linux Kernel Development_ (Love) — If you want deep Linux knowledge.

### Online
- Google SRE Blog (sre.google).
- Google Cloud Blog.
- LeetCode Hard — Graphs, DP, Trees, Arrays.
- Prometheus and Grafana documentation.

---

## 7. Preparation Checklist

- [ ] Read SRE Book chapters 1-6.
- [ ] Master LeetCode Hard (50+ problems).
- [ ] Practice system design (5+ whiteboard designs).
- [ ] Prepare 3-4 Googleyness stories.
- [ ] Deep dive Linux debugging (strace, perf, eBPF).
- [ ] Review TCP, HTTP, DNS, BGP fundamentals.
- [ ] Practice coding in Google Docs (no IDE).
- [ ] Prepare questions for your interviewers.
- [ ] Mock interview with a friend.

---

_End of GOOGLE_SRE_DEVOPS_INTERVIEW_GUIDE.md_