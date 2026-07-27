# Uber System Design Interview Guide

> Comprehensive guide to system design interviews at Uber.

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
- **Phone screen**: 45 min coding
- **Onsite (virtual via CoderPad)**: 4-5 rounds
  - 2 coding rounds
  - 1-2 system design rounds
  - 1 behavioral (Uber values)

### System Design Specifics
- **Format**: CoderPad + whiteboard discussion
- **Duration**: 60 min per round
- **Structure**: Product scenario → system design → scaling → deep dive
- **Focus**: Real-time, geospatial, marketplace design

---

## 2. Top 5 System Design Problems

| # | Problem | Key Concepts |
|---|---------|-------------|
| 1 | Design Uber Ride Matching | Geohash, dispatch algorithm, real-time location |
| 2 | Design Uber ETA | Routing engine, traffic modeling, time-dependent A* |
| 3 | Design Surge Pricing | Supply/demand, elasticity, fairness |
| 4 | Design Driver Allocation | Prediction, dispatch optimization, rebalancing |
| 5 | Design Trip History | Event pipeline, time-series DB, data retention |

### Other Problems
- Design Uber Freight matching
- Design Uber Eats delivery dispatch
- Design Uber Pool (shared rides)
- Design Uber Safety Toolkit (share trip, emergency)
- Design Uber Rewards

---

## 3. Detailed Solution Frameworks

### Problem 1: Design Ride Matching

**Requirements**: 25M trips/day, <500ms matching, 50+ cities

**Architecture**:
- **Location Service**: GPS ingestion (MQTT) → geospatial index (H3 hex grid)
- **Dispatch Service**: Matching algorithm → nearest available driver → assignment
- **Trip Service**: State machine (requesting → accepted → in_progress → completed)
- **ETA Service**: Route calculation + traffic model + historical patterns
- **Surge Service**: Supply/demand ratio per hex cell → multiplier

**Matching Algorithm**:
1. Rider requests → find rider's H3 cell and surrounding cells
2. Query available drivers in those cells
3. Filter by constraints (type, rating, destination direction)
4. Score candidates (distance, ETA, driver rating)
5. Assign best match
6. Send offer to driver (timeout: 15s, then next best)

---

## 4. Evaluation Criteria

- **Real-time systems**: Streaming data, stateful services, WebSocket/MQTT
- **Geospatial expertise**: Spatial indexing (H3, S2, quad-tree), map-matching
- **Scalability**: Handling spikes (NYE, concerts, disasters)
- **Product understanding**: Marketplace dynamics (supply/demand)
- **Data-driven**: How do you measure matching quality?

---

## 5. Design Philosophy

### Core Principles
1. **Real-time everything**: Location, pricing, matching must be sub-second
2. **Marketplace balance**: Design serves both riders and drivers fairly
3. **Geospatial is primary**: Location data is the most important data type
4. **Reliability = trust**: Service failures directly impact revenue and user trust

---

## 6. Real Interview Stories

### Story 1: L4 — Ride Matching
> Candidate designed nearest-driver algorithm. Interviewer asked about concert venue spike (1000 people leaving simultaneously). Candidate enhanced with: queued dispatch, batch grouping, and surge pre-emption.

### Story 2: L5 — Surge Pricing
> Focus on fairness. Candidate proposed price elasticity modeling with demographic-aware adjustments to prevent low-income neighborhoods from constant surging.

---

## 7. Prep Strategy

### Study Focus
- H3 geospatial indexing system (Uber open source)
- Ringpop (consistent hashing)
- Schemaless (Uber's document store)
- Distributed Kafka architecture at Uber
- Presto (query engine originally from Uber/Facebook)

### Labs to Focus On
- 02-scalability (load balancing, auto-scaling)
- 09-distributed-database-design (consistent hashing, replication)
- 06-messaging (Kafka, pub-sub for event streaming)

### Must-Read
- Uber Engineering Blog
- "H3: Uber's Hexagonal Hierarchical Spatial Index"
- "Schemaless: Uber's Document Store"
- "Building Uber's Real-Time Marketplace"
