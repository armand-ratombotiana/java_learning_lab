# Uber Architecture Interview Guide (Senior+)

> Senior/Staff Engineer system design and leadership evaluation at Uber.

---

## Table of Contents

1. [Uber's Engineering Culture](#1-ubers-engineering-culture)
2. [Senior+ Level Expectations](#2-senior-level-expectations)
3. [System Design Interview Format](#3-system-design-interview-format)
4. [Common Uber System Design Questions](#4-common-uber-system-design-questions)
5. [Deep Dive: Design Uber Dispatch System](#5-deep-dive-design-uber-dispatch-system)
6. [Deep Dive: Design Uber Price Engine](#6-deep-dive-design-uber-price-engine)
7. [Geospatial Systems Design](#7-geospatial-systems-design)
8. [Marketplace and Real-Time Systems](#8-marketplace-and-real-time-systems)
9. [Behavioral and Leadership Evaluation](#9-behavioral-and-leadership-evaluation)
10. [Evaluation Rubric](#10-evaluation-rubric)
11. [Preparation Strategy](#11-preparation-strategy)

---

## 1. Uber's Engineering Culture

### Key Cultural Tenets

- **Customer obsession**: Serve riders AND drivers — their needs are equally important
- **Bias for action**: Move quickly, iterate, learn from mistakes
- **Ownership**: Take end-to-end responsibility for problems and solutions
- **Data-informed**: Let data guide decisions, but use judgment when data is incomplete
- **Platform thinking**: Build platforms that enable other teams to build on

### What Uber Values at Senior+

- **Real-time systems expertise**: Deep understanding of latency-critical, high-throughput systems
- **Geospatial proficiency**: Working with location data, maps, routing at scale
- **Marketplace dynamics**: Designing systems that balance supply and demand
- **Owner mentality**: Treating the product as your own business
- **Bias for action**: Making progress in ambiguous situations

---

## 2. Senior+ Level Expectations

### Senior Engineer

- Leads technically complex projects within a domain
- Makes architecture decisions for their team
- Deep expertise in one area (dispatch, pricing, maps, payments)
- Mentors engineers and conducts design reviews

### Staff Engineer (L5a)

- Sets technical direction across multiple teams
- Drives architecture decisions with org-wide impact
- Recognized expert within Uber
- Influences engineering culture, standards, and practices

---

## 3. System Design Interview Format

### Structure

- **Duration**: 60 minutes per round (2 system design rounds for staff+)
- **Format**: Whiteboard or virtual whiteboard
- **Focus**: Real-time, geospatial, marketplace systems

### Time Allocation

| Phase | Time | Activity |
|-------|------|----------|
| Requirements | 5 min | Functional and non-functional requirements |
| Scale estimation | 5 min | Riders, drivers, trips, geolocation updates |
| Data model | 5 min | Geospatial data modeling |
| High-level design | 10 min | Components, interactions, data flow |
| Deep dive | 20 min | Critical components (dispatch, pricing, ETA) |
| Trade-offs | 10 min | Alternative approaches, why chosen |
| Summary | 5 min | Recap, failure modes, next steps |

---

## 4. Common Uber System Design Questions

### Tier 1 (Ride-hailing core)

| Question | Key Focus Areas |
|----------|----------------|
| Design Uber Dispatch System | Driver-rider matching, geospatial indexing, real-time optimization |
| Design Uber Price Engine | Dynamic pricing, surge, market elasticity |
| Design Uber Routing Engine | Real-time navigation, traffic prediction, ETA calculation |
| Design Uber Payment System | Payouts, billing, multi-currency, fraud detection |

### Tier 2 (Uber Eats and beyond)

| Question | Key Focus Areas |
|----------|----------------|
| Design Uber Eats | Food delivery, multi-sided marketplace, real-time tracking |
| Design Uber Freight | Logistics, load matching, carrier management |
| Design Uber Maps | Custom maps, geocoding, points of interest |

### Tier 3 (Infrastructure)

| Question | Key Focus Areas |
|----------|----------------|
| Design Uber's Real-time Platform | Event processing, stream processing, analytics |
| Design Uber's Data Platform | Data ingestion, storage, querying at scale |
| Design Uber's ML Platform | Model training, serving, feature store |

---

## 5. Deep Dive: Design Uber Dispatch System

### Requirements

**Functional:**
- Match riders with nearby drivers
- Real-time location tracking (both rider and driver)
- ETA calculation and display
- Trip management (start, track, complete)
- Surge pricing integration

**Non-functional:**
- 100M+ monthly active users
- 25M+ trips per day
- P99 matching latency < 2 seconds
- P99 location update latency < 1 second
- Global: 10,000+ cities in 70+ countries
- 5M+ drivers online at peak

### Scale Estimation

```
Daily trips: 25M
Peak trips per second: 5,000
GPS location updates: 5M+ per second (driver + rider)
Driver state changes: 100K per second (online, offline, trip accepted)
Geospatial queries: 500K per second (find nearby drivers)
Storage: 25M trips × 1KB metadata = 25GB/day + GPS traces = 500GB+/day
```

### Architecture

```
Rider App → [API Gateway] → [Dispatch Service]
                                │
                    ┌───────────┼───────────┐
                    │           │           │
              ┌─────▼──┐ ┌─────▼──┐ ┌─────▼──────┐
              │Geospatial│ │Market │ │ ETA        │
              │Index     │ │Service│ │ Service    │
              │(H3/S2)   │ │       │ │            │
              └────┬────┘ └───┬───┘ └──────┬──────┘
                   │          │             │
              ┌────▼────┐ ┌───▼───┐  ┌─────▼──────┐
              │Redis    │ │Kafka  │  │  ML Model   │
              │(Driver  │ │(Events)│  │  (ETA Pred)│
              │Locations)│ │       │  │            │
              └─────────┘ └───────┘  └────────────┘
```

### Matching Algorithm

```
1. Rider requests trip (with pickup location)
2. Query geospatial index for nearby available drivers
3. Filter candidates: distance, rating, vehicle type, acceptance history
4. Calculate ETA for each candidate
5. Score candidates based on:
   - Distance/time to pickup
   - Driver rating and acceptance rate
   - Surge multiplier
   - Supply/demand balance
6. Select best match
7. Send dispatch request to driver
8. If not accepted within 15 seconds, try next candidate
9. Once accepted, notify rider and create trip
```

### Geospatial Indexing

**Why H3 (hexagonal hierarchical geospatial indexing):**
- Uniform grid (better than lat/lng for distance calculations)
- Hierarchical (different resolutions for different use cases)
- Fast adjacency queries (hexagons have consistent neighbors)
- Supports range queries and clustering

**Index structure:**
- Driver location → H3 cell at level 8 (~1km resolution)
- Map: H3 cell → List of driver IDs + metadata
- For nearby queries: query driver's cell + surrounding cells
- Real-time updates: driver sends GPS → update in Redis hash per cell

---

## 6. Deep Dive: Design Uber Price Engine

### Requirements

**Functional:**
- Calculate fare for a trip
- Dynamic pricing based on supply/demand
- Surge multiplier when demand exceeds supply
- Price estimation before ride request
- Different pricing models (per mile, per minute, minimum fare)

**Non-functional:**
- P99 pricing calculation latency < 100ms
- Real-time supply/demand monitoring
- Multi-currency support
- Fare splits, promotions, and discounts

### Surge Pricing Algorithm

```
Surge Calculation:
  Supply = Available drivers in area
  Demand = Ride requests in area

  SurgeMultiplier = f(Demand / Supply)

  If Demand/Supply > threshold:
    SurgeMultiplier increases
  If Demand/Supply decreases:
    SurgeMultiplier decays exponentially

  Final Price = BasePrice × SurgeMultiplier
```

### Architecture

```
Rider Request → [Pricing Service] → [Surge Calculator]
                                       │
                          ┌────────────┼────────────┐
                          │            │            │
                    ┌─────▼───┐  ┌────▼───┐  ┌─────▼─────┐
                    │Supply   │  │Demand  │  │Base       │
                    │Tracker  │  │Tracker │  │Price      │
                    │(Real-   │  │(Events)│  │Calculator │
                    │time)    │  │        │  │           │
                    └────┬────┘  └───┬────┘  └─────┬─────┘
                         │           │              │
                    ┌────▼───────────▼──────────────▼──┐
                    │    Kafka Event Stream            │
                    │  (DriverOnline, RideRequest,     │
                    │   RideComplete, SurgeChange)     │
                    └─────────────────────────────────┘
```

---

## 7. Geospatial Systems Design

### Geospatial Indexing Comparison

| Index | Pros | Cons | Uber's Choice |
|-------|------|------|---------------|
| Geohash | Simple, widely used | Edge cases at boundaries, rectangular cells | Used in earlier versions |
| S2 (Google) | Hierarchical, excellent library support | Complex implementation | Used for maps and regions |
| H3 (Uber) | Uniform hexagons, consistent neighbors, open source | Newer, less adoption | **Primary index for dispatch** |

### Geospatial Query Patterns

**Find nearest drivers:**
```
1. Map rider location to H3 cell (level 8)
2. Query drivers in that cell
3. If not enough, query neighboring cells (ring of radius 1, 2, 3...)
4. Sort candidates by proximity
5. Return top N candidates
```

**Geofencing:**
```
1. Define airport as H3 cells at level 5
2. When driver enters airport geofence → trigger airport queue logic
3. When rider requests from airport → apply airport pricing rules
```

**Heat maps:**
```
1. Aggregate supply and demand per H3 cell
2. Calculate supply/demand ratio
3. Visualize as heat map on rider/driver apps
```

---

## 8. Marketplace and Real-Time Systems

### Marketplace Dynamics

**Supply-demand equilibrium:**
- More supply → lower prices, shorter ETAs
- More demand → higher prices (surge), longer ETAs
- Goal: balance supply and demand through pricing incentives

**Real-time monitoring:**
- Supply and demand tracked per geohash cell
- Surge triggers when demand/supply ratio exceeds threshold
- Price elasticity: how does demand change with price?

### Real-Time Infrastructure

**Kafka as the backbone:**
- All events streamed through Kafka (location, trips, payments)
- Stream processors (Flink, Kafka Streams) for aggregation and ML
- Real-time dashboards for operations

**Key real-time systems:**
- **Location service**: GPS ingestion, update, and query
- **Dispatch engine**: Matching algorithm, real-time optimization
- **Pricing engine**: Dynamic pricing updates
- **ETA service**: Real-time arrival time prediction

---

## 9. Behavioral and Leadership Evaluation

### Key Behavioral Themes

**Customer obsession:**
- "Tell me about a time you went above and beyond for riders or drivers"
- "How do you balance the needs of riders and drivers in your designs?"

**Bias for action:**
- "Tell me about a time you made a quick decision with incomplete information"
- "Describe a situation where you shipped something imperfect to get feedback"

**Ownership:**
- "Tell me about a project you drove from idea to production"
- "Describe a time you took on a problem outside your area of responsibility"

**Data-informed:**
- "Tell me about a technical decision you made based on data"
- "How do you use experimentation to validate your assumptions?"

**Platform thinking:**
- "Tell me about a platform you built that enabled other teams"
- "How do you design APIs that are easy for other teams to use?"

---

## 10. Evaluation Rubric

### Senior+ Scoring

| Criteria | Weight | Senior+ Expectation |
|----------|--------|-------------------|
| System Design | 35% | Real-time, geospatial, marketplace-aware |
| Technical Depth | 25% | Deep expertise in core domain |
| Problem Solving | 20% | Structured approach, trade-off analysis |
| Leadership | 20% | Ownership, bias for action, influence |

### Common Rejection Reasons

1. **Not real-time aware**: Designs that assume batch processing for real-time problems
2. **Poor geospatial knowledge**: Unable to design location-based indexing
3. **No marketplace understanding**: Doesn't consider supply-demand dynamics
4. **Not data-informed**: Making decisions without considering data or experimentation

---

## 11. Preparation Strategy

### Week 1-2: Foundation
- Study Uber's engineering blog (eng.uber.com)
- Understand H3 geospatial indexing in depth
- Review real-time streaming systems (Kafka, Flink, Spark Streaming)

### Week 3-4: System Design Practice
- Design 5-7 Uber-scale systems (dispatch, pricing, Uber Eats, routing, payments)
- Practice geospatial data modeling for each design
- Time yourself (60 minutes per round)

### Week 5-6: Behavioral & Marketplace
- Prepare stories demonstrating bias for action and ownership
- Practice discussing marketplace economics and supply-demand dynamics
- Prepare to discuss real-time system trade-offs (consistency vs availability vs latency)

### Must-Know Uber Technologies

| Technology | Purpose | Interview Relevance |
|-----------|---------|-------------------|
| H3 | Geospatial indexing | Location-based design |
| Kafka | Event streaming | Real-time data pipeline |
| Flink | Stream processing | Real-time analytics |
| Peloton | Resource management | Container orchestration |
| Ringpop | Application layer | Service communication |
| Schemaless | Database | Data storage (MySQL-based) |
| Redis | Caching | Real-time state, location cache |

---

*Combine this guide with the ACADEMY_INTERVIEW_GUIDE.md and COMPANY_INTERVIEW_GUIDE.md for complete Uber Senior+ interview preparation.*

---

## Appendix A: Uber System Design — Uber Eats

### Food Delivery Architecture

```
Customer App → [API Gateway] → [Uber Eats Service]
                                    │
              ┌─────────────────────┼──────────────────┐
              │                     │                  │
        ┌─────▼─────┐       ┌──────▼──────┐    ┌──────▼──────┐
        │ Restaurant │       │ Delivery    │    │ Order       │
        │ Service    │       │ Service     │    │ Service     │
        │ (Menu,     │       │ (Courier    │    │ (Cart,      │
        │  Hours)    │       │  Matching,  │    │  Checkout)  │
        │            │       │  Routing)   │    │             │
        └────────────┘       └─────────────┘    └─────────────┘
```

**Key differences from ride-hailing:**
- Three-sided marketplace (customer, restaurant, courier)
- Food preparation time adds scheduling complexity
- Real-time order tracking with ETA adjustments
- Batch orders (courier picks up multiple orders)

**Order fulfillment optimization:**
- Estimated preparation time prediction (ML model)
- Courier dispatch optimization (batching, routing)
- Real-time ETA updates based on courier location and traffic

## Appendix B: Uber Real-Time Infrastructure

### The Real-Time Event Pipeline

```
GPS Updates (5M/sec) → [Kafka] → [Flink Stream Processor]
                                      │
                    ┌─────────────────┼─────────────────┐
                    │                 │                 │
              ┌─────▼─────┐   ┌──────▼──────┐   ┌──────▼──────┐
              │ Dispatch   │   │ Pricing     │   │ ETA         │
              │ Service    │   │ Engine      │   │ Service     │
              │ (Matching) │   │ (Surge)     │   │ (Routing)   │
              └────────────┘   └─────────────┘   └─────────────┘
```

**Key real-time infrastructure choices:**
- Kafka for event ingestion (durable, ordered, replayable)
- Flink for stream processing (stateful, exactly-once, low latency)
- Redis for real-time state (driver locations, surge zones)
- H3 geospatial index for location-based queries

## Appendix C: Uber Senior+ Behavioral Questions

### Customer Obsession
- "Tell me about a time you went above and beyond for riders or drivers"
- "How do you balance the needs of both sides of the marketplace?"

### Bias for Action
- "Tell me about a time you shipped something quickly with incomplete information"
- "How do you decide when to move fast vs gather more data?"

### Ownership
- "Describe a project you drove from idea to production with end-to-end ownership"
- "Tell me about a time you fixed a problem outside your area of responsibility"

### Data-Informed Decisions
- "Tell me about a technical decision you changed based on data"
- "How do you design experiments to validate architectural decisions?"

## Appendix D: Uber Technology Stack Reference

| Technology | Type | Purpose |
|-----------|------|---------|
| H3 | Geospatial | Hexagonal hierarchical spatial index |
| Peloton | Resource Management | Container orchestration |
| Kafka | Streaming | Real-time event pipeline |
| Flink | Stream Processing | Real-time analytics |
| Schemaless | Database | MySQL-based scalable data store |
| Redis | Cache | Real-time state management |
| Ringpop | RPC | Application layer communication |
| M3 | Metrics | Distributed metrics platform |
