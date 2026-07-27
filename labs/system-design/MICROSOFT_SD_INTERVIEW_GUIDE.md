# Microsoft System Design Interview Guide

> Comprehensive guide to system design interviews at Microsoft.

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
- **Phone screen**: 45 min (coding or system design, depending on level)
- **Onsite (virtual via Teams)**: 4-5 rounds
  - 2 coding rounds (whiteboard/OneNote)
  - 1-2 system design rounds
  - 1 ASAP (Microsoft's bar raiser) — typically a senior engineer from another org
  - 1 cross-functional/culture fit

### System Design Specifics
- **Format**: Microsoft Teams + OneNote or whiteboard
- **Duration**: 45 min per round
- **Structure**: Requirements → design → component deep dive → testing discussion
- **Level**: 60-61 = 1 SD round; 62-63 = 2 rounds; 64+ = 2-3 rounds

### ASAP (As Soon As Possible) Round
- Equivalent to Amazon's Bar Raiser
- Evaluates: Are you hireable at this level?
- Asks cross-functional questions combining design, coding, and architecture
- Often the deciding round

---

## 2. Top 5 System Design Problems

| # | Problem | Key Concepts |
|---|---------|-------------|
| 1 | Design Azure Blob Storage | Erasure coding, metadata, multi-tenancy, geo-replication |
| 2 | Design Microsoft Teams | Real-time comms, WebRTC, federation, compliance |
| 3 | Design Azure Active Directory | Identity, OAuth2/OIDC, multi-tenant, federation |
| 4 | Design Outlook/Exchange | Transport pipeline, mail storage, full-text search |
| 5 | Design Xbox Live | Session management, matchmaking, multiplayer |

### Other Problems
- Design Azure Cosmos DB
- Design OneDrive sync engine
- Design GitHub Actions
- Design Visual Studio Code extensions marketplace
- Design LinkedIn Feed (Microsoft owns LinkedIn)
- Design Bing Search
- Design Azure DevOps pipelines

---

## 3. Detailed Solution Frameworks

### Problem 1: Design Azure Blob Storage

**Architecture**:
- Front-end tier: Request processing, authentication, rate limiting
- Partition layer: Range-partitioned metadata store (keys → storage nodes)
- Storage tier: Erasure-coded data (12+4 scheme), geo-replication (LRS, GRS, RA-GRS)
- Access tiers: Hot (frequent access), Cool (<30 days), Archive (<180 days)

### Problem 2: Design Microsoft Teams

**Components**:
- **Chat**: Exchange Online for persistence, real-time via SIP/WebRTC
- **Meetings**: Media routing via Azure Media Services, TURN for NAT
- **Files**: SharePoint Online, real-time co-authoring
- **Calendar**: Exchange Calendar integration
- **Compliance**: eDiscovery, legal hold, retention policies

### Problem 3: Design Azure Active Directory

**Design**:
- Multi-tenant identity store (directory + authentication)
- OAuth2/OIDC/SAML federation gateway
- Conditional access (policy-based access control)
- Token caching (distributed in-memory cache)
- Audit logging (event pipeline → Cosmos DB)

---

## 4. Evaluation Criteria

- **Design completeness**: Testing, deployment, rollback strategy
- **Enterprise awareness**: Compliance, multi-tenancy, geo-residency
- **Growth mindset**: How you handle feedback during design discussion
- **Testing approach**: How to verify correctness at scale
- **Azure knowledge**: Familiarity with Azure services is a bonus

---

## 5. Design Philosophy

Microsoft focuses on **enterprise reliability with hybrid capability**:
- On-prem + cloud (hybrid cloud design)
- Backward compatibility obsession
- Enterprise compliance built-in, not bolted-on
- Integration across Microsoft ecosystem (M365, Azure, LinkedIn, GitHub)

---

## 6. Real Interview Stories

**Story 1: L63 — Cosmos DB**
> Interviewer asked about multi-region write conflicts. Candidate discussed LWW (last-writer-wins) and CRDTs. The deep dive compared different consistency models in Cosmos DB (Strong, Bounded Staleness, Session, Consistent Prefix, Eventual).

**Story 2: L65 — Teams**
> Focus was on geo-redundant failover for chat. Candidate designed active-passive deployment with DNS-based routing and session state replication via Azure Redis.

---

## 7. Prep Strategy

- Study Azure services: Blob, Cosmos DB, AD, DevOps
- Read Microsoft engineering blog
- Practice enterprise-focused designs
- Understand hybrid cloud patterns
- Lab focus: 07-transactions, 04-databases, 08-observability
