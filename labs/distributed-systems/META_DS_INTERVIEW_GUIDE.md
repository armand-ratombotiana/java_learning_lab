# Meta Distributed Systems Interview Guide

> Complete preparation guide for distributed systems roles at Meta (Facebook).

---

## How Meta Tests Distributed Systems

Meta's interview process focuses on empirically-proven systems at massive scale. They want to see that you understand how real systems work at Facebook/Meta.

### Interview Rounds That Test DS

1. **System Design (2 rounds)**: The core DS evaluation. 45 minutes each.
2. **Coding (1-2 rounds)**: Often DS-adjacent (graphs, trees, caches).
3. **Behavioral (1 round)**: "Tell me about a distributed system you built."
4. **Production Engineer**: Additional Linux/systems/networking round.

### Meta's Unique DS Focus

- **Proven Systems**: You need to know TAO, Haystack, Presto, Scuba, Unicorn.
- **Scale is Everything**: Design for 1B+ users. "How would Meta implement this?"
- **Caching is King**: Meta loves cache-first architectures (TAO, Memcached).
- **Graph-Based Thinking**: Social graph is Meta's primary data structure.
- **Empiricism**: "What does the data say?" - Every design decision backed by data.
- **Move Fast**: Pragmatic tradeoffs, incremental improvement.

### Meta's Systems You Must Know

| System | Purpose | Key Concepts |
|--------|---------|-------------|
| TAO | Social graph storage | Cache-first, association lists, read-through/write-through |
| Haystack | Photo storage | Blob storage, in-memory index, minimized metadata |
| Presto | Distributed SQL query | Connector architecture, MPP, in-memory execution |
| Scuba | Real-time analytics | In-memory column store, sampling, pre-aggregation |
| Unicorn | Entity search | Inverted index per entity type, real-time indexing |
| Memcached | Distributed cache | Consistent hashing, slab allocation, LRU |
| Scribe | Log aggregation | Message aggregation, compression, transport |

---

## Top 15 Distributed Systems Questions at Meta

### Question 1: Design Facebook News Feed
**Difficulty**: Hard | **Frequency**: Very High

**Answer Framework**:
```
Requirements:
- Billions of users, millions of posts/day
- Real-time updates, ranked by relevance
- Different content types (text, photo, video, link)

Architecture:
- Fan-out on write for active users (5000+ friends)
- Fan-out on read for inactive/passive users
- Hybrid approach: fan-out to active, pull for inactive

Data Model:
- FeedItem: user_id, author_id, content_id, timestamp, rank_score
- Feed stored in sorted set per user (Redis or custom)

Ranking Pipeline:
- Candidate generation: friends, pages, groups
- Feature extraction: affinity, recency, content type
- ML ranking model: neural network (200+ features)
- Re-ranking: diversity, remove duplicates

Storage:
- Feed stored in TAO (graph store) for persistence
- Pre-computed feed stored in distributed cache
- Memcached for hot feeds, TAO for serving

Challenges:
- Fan-out at billion scale requires efficient push mechanisms
- Cold start for new users
- Live videos and stories have different caching patterns
```

### Question 2: Design Facebook Messenger
**Difficulty**: Hard | **Frequency**: High

**Answer Framework**:
```
Requirements:
- Real-time messaging, billions of messages/day
- Multi-device sync, read receipts, typing indicators
- End-to-end encryption

Architecture:
- Connection management: long-polling -> MQTT/WebSocket migration
- Message store: InnoDB/MyRocks per user shard
- Presence: WebSocket heartbeat + aggregation service
- End-to-end encryption: Signal Protocol per conversation

Message Flow:
1. Sender sends to message router (WebSocket)
2. Router stores in sender's outbox, pushes to receiver
3. Receiver acknowledges, message stored in per-thread store
4. Delivery notification sent back to sender

Storage:
- Per-conversation thread store (ordered by message_id)
- Per-user message store (for cross-device sync)
- Media in Haystack (photo/video), Filesystem (docs)

Scaling:
- Shard by conversation_id (to avoid hot shards)
- Multi-region replication for global latency
- Data Center routing based on sender location
```

### Question 3: Design Haystack (Photo Storage)
**Difficulty**: Hard | **Frequency**: High

**Answer Framework**:
```
Problem: Storing billions of photos with minimal metadata overhead
Traditional FS: each file requires inode (256 bytes) for metadata
Meta's photos: 20PB+ storage, 100K+ photos/sec upload

Haystack Design:
- Store multiple photos in a single 100GB file (Needle)
- In-memory index maps photo_id -> (Needle file, offset, size)
- Tiny metadata per photo (only file_id, offset, size)
- Three operations: READ, WRITE, DELETE (mark tombstone)

Read Path:
1. Client sends photo_id + cookie (for CDN)
2. CDN server extracts Needle file, offset, size from in-memory index
3. Server reads directly from Needle file at offset
4. Returns photo data with no additional metadata lookup

Write Path:
- Photo uploaded -> resize to multiple thumbnail sizes
- Compress and store in Haystack Needle
- Update in-memory index

Index:
- All in RAM (tens of GB for billions of photos)
- Rebuilt from Needle file on restart (scan all needles)
- Multiple index servers for redundancy
```

### Question 4: Design Instagram Feed
**Difficulty**: Medium | **Frequency**: Very High

**Answer Framework**:
```
Similar to News Feed but media-focused:
- Fan-out on write for users with < 1000 followers
- Pull for users with > 1000 followers
- Ranking: based on engagement (likes, comments, saves)
- Stories: ephemeral content with separate ranking

Storage:
- Media in Haystack (photos) / video infrastructure
- Feed metadata in TAO
- Feed cache in Redis

Key Differences from News Feed:
- Visual-first: ranking considers image quality, color
- Stories: separate ranking, ephemeral (24hr)
- Explore: recommendation-based discovery
- Reels: TikTok-like video feed
```

### Question 5: Design TAO (The A-social Organization)
**Difficulty**: Very Hard | **Frequency**: Medium

**Answer Framework**:
```
Purpose: Social graph storage (users, pages, edges)
Data Model: Objects (nodes) + Associations (edges)
- Object: (id, type, data)
- Association: (id, from_id, to_id, type, time, data)

Architecture:
- Cache-first: all reads go through TAO cache
- Read-through cache: miss -> fetch from DB -> populate cache
- Write-through cache: write to cache + DB synchronously
- Async replication: cache writes propagate to followers

Cache Topology:
- Tier-1: Memcached followers (1000s of nodes)
- Tier-2: TAO cache leaders (stores full association lists)
- DB: MySQL (sharded by object id)

Read Path:
1. Client requests associations for object
2. TAO worker checks Tier-1 cache
3. Miss -> check Tier-2 cache (full list)
4. Miss -> fetch from DB, populate caches
5. Return association list

Write Path:
1. Client creates association
2. Write-through to Tier-2 cache leader
3. Leader writes to DB
4. Leader asynchronously invalidates Tier-1 caches
```

### Question 6: Design WhatsApp
**Difficulty**: Hard | **Frequency**: High

**Answer Components**:
- **Connection**: custom MQTT-like protocol over persistent TCP
- **Routing**: user_id -> IP mapping service
- **Message Store**: per-message, persisted for 7 days
- **Group Chat**: fan-out to all group members
- **Media**: encrypted upload, thumbnail + reference in message
- **End-to-End Encryption**: Signal Protocol

### Question 7: Design Facebook Search (Unicorn)
**Difficulty**: Very Hard | **Frequency**: Medium

**Answer Components**:
- **Indexing**: inverted index per entity type (user, page, post, group)
- **Real-time Indexing**: Scribe log -> indexer pipeline
- **Query**: entity search + keyword search
- **Ranking**: PageRank-like + real-time signals
- **Scaling**: index shards, replica sets

### Question 8: Design Video Recommendation System
**Difficulty**: Hard | **Frequency**: High

**Answer Components**:
- **Candidate Generation**: collaborative filtering + content-based
- **Ranking**: Deep neural network with 100+ features
- **Re-ranking**: Diversity, freshness, watch time optimization
- **A/B Testing**: bandit algorithms for exploration
- **Cold Start**: popularity-based fallback

### Question 9: Design Distributed Counter (Likes)
**Difficulty**: Medium | **Frequency**: Very High

**Answer Components**:
- **Pre-sharded Counters**: N shards, distributed increment
- **Batching**: buffer writes locally, batch-update database
- **Read Path**: sum across all shards (async merge)
- **Consistency**: eventual consistency is acceptable for like counts

### Question 10: Design Live Video Streaming
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Ingest**: RTMP/WHIP protocol, ingestion servers
- **Transcoding**: adaptive bitrate profiles (240p to 4K)
- **Distribution**: CDN, edge servers
- **Chat**: IRC-style, fan-out per viewer
- **Recording**: on-the-fly to Haystack/video store

### Question 11: Design Notification System
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Notification Types**: push, email, in-app
- **Channel Priority**: push (urgent), email (daily digest), in-app (all)
- **Delivery**: priority queue per channel
- **Template System**: notification templates with dynamic fields

### Question 12: Design Graph Search
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Entity Resolution**: parse query to entities
- **Graph Traversal**: BFS with pruning
- **Ranking**: proximity + relevance + popularity
- **Indexing**: pre-computed paths between entities

### Question 13: Design A/B Testing at Scale
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Assignment**: hash-based, consistent across user sessions
- **Metrics Pipeline**: real-time + batch aggregation
- **Statistical Methods**: sequential testing, Bonferroni correction
- **Execution**: bucketing, gradual rollout, auto-rollback

### Question 14: Design Distributed Photo Upload
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Upload**: direct to CDN edge, resumable upload
- **Processing**: resize (thumbnails, standard, high-res)
- **Transcoding**: CPU-optimized (JPEG, WebP, HEIC, AVIF)
- **Storage**: Haystack for originals, CDN for serve

### Question 15: Design Realtime Presence System
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Heartbeat**: WebSocket/SSE connection per user
- **Aggregation**: per-conversation presence state
- **Scalability**: shard by user_id, in-memory store
- **Expiry**: mark offline after N seconds of no heartbeat

---

## Meta-Specific Evaluation Criteria

| Criteria | Weight | What They Look For |
|----------|--------|-------------------|
| System Design | 40% | Scale, caching, pragmatic tradeoffs |
| Coding | 25% | Clean, efficient, tested code |
| Behavioral | 20% | Impact, speed, data-driven decisions |
| Domain Knowledge | 15% | Know Meta's systems and their tradeoffs |

### Key Behavioral Themes at Meta

- **Impact**: Quantify results. "Improved feed read latency by 50%"
- **Move Fast**: "We shipped the MVP in 2 weeks, then iterated"
- **Be Open**: "We shared our post-mortem company-wide"
- **Focus on Impact**: "I chose this approach because it had 10x the impact"

---

## Java Code Examples for Meta Interviews

### TAO-Style Cache-Through

```java
public class TaoCache {
    private final Cache<String, AssociationList> tier1 = new MemcachedClient();
    private final Cache<String, AssociationList> tier2 = new RedisClient();
    private final Database db;

    public AssociationList getAssociations(String objectId, String assocType) {
        String key = objectId + ":" + assocType;

        // Check Tier 1
        AssociationList result = tier1.get(key);
        if (result != null) return result;

        // Check Tier 2
        result = tier2.get(key);
        if (result != null) {
            tier1.put(key, result); // warm Tier 1
            return result;
        }

        // Cache miss - fetch from DB
        result = db.query(objectId, assocType);
        tier2.put(key, result);
        tier1.put(key, result);
        return result;
    }
}
```

---

## Study Plan for Meta DS Interviews

### Week 1-2: Meta Systems Deep Dive
- Read TAO, Haystack, Scuba papers
- Understand cache-first architecture pattern
- Study News Feed ranking system

### Week 3-4: Scale Thinking
- Practice designing for 1B+ users
- Focus on caching patterns (read-through, write-through)
- Study fan-out patterns (push vs pull)

### Key LeetCode Problems
| Problem | # | Meta Relevance |
|---------|---|--------------|
| Clone Graph | 133 | Graph replication (TAO) |
| LRU Cache | 146 | Cache design |
| Binary Tree Right Side | 199 | Quorum view |
| Clone N-ary Tree | 1490 | Data replication |
| Subtree of Another Tree | 572 | Merkle tree sync |
| Task Scheduler | 621 | Distributed scheduling |
| Number of Islands | 200 | Partition isolation |
| Merge Intervals | 56 | Consistency merge |

---

> **Meta Tip**: Meta interviewers love when you reference their actual systems. Say "TAO solves this by..." or "Haystack handled this by..." instead of generic designs. Show you've studied what Meta actually built.