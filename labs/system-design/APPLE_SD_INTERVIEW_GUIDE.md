# Apple System Design Interview Guide

> Comprehensive guide to system design interviews at Apple.

---

## Table of Contents

1. [Interview Process](#1-interview-process)
2. [Top 5 System Design Problems](#2-top-5-system-design-problems)
3. [Detailed Solution Frameworks](#3-detailed-solution-frameworks)
4. [Evaluation Criteria](#4-evaluation-criteria)
5. [Design Philosophy](#5-design-philosophy)
6. [Real Interview Stories](#6-real-interview-stories)
7. [Prep Strategy](#7-prep-strategy)

---

## 1. Interview Process

### Rounds
- **Phone screen**: 45-60 min technical (mix of coding, architecture, domain knowledge)
- **Onsite (in-person preferred)**: 5-7 rounds
  - 2-3 coding rounds (whiteboard)
  - 1-2 system design rounds
  - 1-2 domain-specific rounds (specific to the team's focus area)
  - 1 behavioral / hiring manager
  - 1 cross-functional (sometimes with partner team for collaboration fit)

### System Design Specifics
- **Format**: In-person whiteboard (Apple strongly prefers physical whiteboarding)
- **Duration**: 45-60 min per round
- **Structure**: Conversation-driven, less formulaic than Google/Amazon
- **Level**: ICT3 = 1 SD round; ICT4+ = 2-3 SD rounds

### Key Differences
- Apple interviews are less structured — the conversation flows naturally
- More focus on product intuition and user experience
- Privacy is ALWAYS a consideration — expect questions about data minimization
- Hardware-software integration is a common theme (battery, sensors, chips)

---

## 2. Top 5 System Design Problems

| # | Problem | Key Concepts |
|---|---------|-------------|
| 1 | Design iCloud Sync Engine | Differential sync, CRDT, conflict resolution, encryption |
| 2 | Design Apple Push Notification Service | Persistent TCP, battery efficiency, prioritization |
| 3 | Design iMessage | E2E encryption, key management, multi-device |
| 4 | Design Apple Maps | Privacy-first routing, on-device processing, vector tiles |
| 5 | Design App Store | App review pipeline, delta updates, receipt verification |

### Other Problems
- Design Apple Music streaming
- Design AirDrop discovery protocol
- Design Siri voice processing pipeline
- Design FaceTime
- Design Apple Pay
- Design iCloud Photos
- Design Find My network (crowd-sourced location)

---

## 3. Detailed Solution Frameworks

### Problem 1: Design iCloud Sync Engine

**Requirements**: 1B+ devices, real-time sync, conflict resolution, 5GB free

**Design**:
- File coordination: NSFileCoordinator for atomic reads/writes
- Differential sync: Only sync changed blocks, not whole files
- Content-addressable storage: Blocks identified by hash, deduplication
- Conflict resolution: Version-based for complex conflicts, LWW for simple

**Privacy**:
- End-to-end encryption for sensitive data types (health, passwords, iCloud Keychain)
- Apple knows your files exist but can't read them

### Problem 2: Design APNs

**Design**:
- Persistent TCP connections with TLS
- Per-app topic routing
- Priority queues: critical alerts (immediate), time-sensitive, passive
- Coalescing: Same app, same content → single notification
- Feedback service: Detect uninstalled apps, stop pushing
- Battery-aware: Connection draining, idle timeout, batching

### Problem 3: Design iMessage

**Design**:
- Identity service: Per-device public key directory
- E2E encryption: Each message encrypted per-recipient-device
- Routing: APNs for notification, direct P2P for content
- Multi-device: Shared key material via iCloud Keychain
- Message storage: Minimal server storage (privacy-focused)

---

## 4. Evaluation Criteria

| Criterion | Weight | Apple Expectation |
|-----------|--------|-------------------|
| Privacy-first thinking | 20% | Data minimization, on-device, encryption by default |
| Product intuition | 20% | How design impacts user experience |
| Cross-functional awareness | 15% | Battery, heat, hardware constraints |
| Simplicity | 15% | Clean, non-over-engineered |
| Security & correctness | 15% | Encryption, error handling, edge cases |
| Attention to detail | 15% | Design for every state: loading, empty, error, offline |

---

## 5. Design Philosophy

### Core Principles
1. **Privacy is a human right**: Design for data minimization, on-device processing, transparency
2. **Design for delight**: Every interaction should feel fluid and responsive
3. **End-to-end ownership**: Apple controls hardware, OS, and services
4. **Simplicity**: The best design is invisible — users shouldn't think about the system

### Apple-Specific Design Considerations
- **Battery life**: Every network request costs battery. Batch, compress, coalesce.
- **On-device processing**: Move computation to the device when possible (Neural Engine, Secure Enclave)
- **Offline-first**: Apple services should work without network (drafts, caches, sync when available)
- **Hardware integration**: Leverage T2/M-series chips, Secure Enclave, Neural Engine

---

## 6. Real Interview Stories

### Story 1: ICT3 — iCloud Sync
> Interviewer focused on conflict resolution when editing same file on iPhone and MacBook. Candidate proposed version-based resolution. Apple engineer pushed: "How do you make this invisible to users?" Candidate discussed CRDT merging for notes, photos, and contacts where automatic merge is possible.

### Story 2: ICT4 — APNs
> Primary challenge: battery-efficient push while maintaining <100ms delivery. Candidate designed coalescing on the server and connection draining on device sleep. Privacy discussion: "How do you deliver notifications without revealing user behavior to app servers?" — introduced relay-based notification delivery.

---

## 7. Prep Strategy

### Study Focus
- Privacy-preserving design patterns (on-device ML, differential privacy)
- Apple's WWDC sessions on iCloud, APNs, Maps
- FoundationDB (used inside Apple for iCloud)
- CRDTs and offline-first sync patterns
- Encryption protocols (Signal, TLS 1.3)

### Labs to Focus On
- 10-real-time-collaboration (CRDT, OT, sync)
- 04-consistency-models (CAP, consistency trade-offs)
- 05-caching (CDN, local caching)

### Common Mistakes
- Not considering privacy: Every design must have a privacy section
- Ignoring battery: Mobile-first, battery-aware design
- Over-building: Apple prefers simple, focused solutions
- Not knowing Apple's ecosystem: iCloud, APNs, Secure Enclave
