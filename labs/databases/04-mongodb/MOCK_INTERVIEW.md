# Mock Interview: MongoDB (Lab 04)

**Role:** Database Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is MongoDB and how does it differ from relational databases?

**Candidate:** MongoDB is a NoSQL document database that stores data in JSON-like BSON documents. Key differences:
- **Schema:** Flexible schema vs rigid tables
- **Data model:** Embedded documents vs normalized relations
- **Queries:** Document-oriented (find, aggregate) vs SQL
- **Transactions:** Multi-document ACID (since 4.0) vs full ACID in RDBMS
- **Joins:** `$lookup` pipeline stage vs SQL JOIN
- **Scaling:** Native horizontal sharding vs Oracle RAC or sharding

**Interviewer:** How does MongoDB implement sharding?

**Candidate:** MongoDB sharding distributes data across shards (replica sets):
1. **Shard key:** A field or compound index used to distribute documents
2. **Chunks:** Contiguous ranges of shard key values
3. **Balancer:** Background process that migrates chunks between shards for even distribution
4. **mongos:** Query router that routes requests to appropriate shards

Shard key selection is critical — a poor key causes uneven distribution (jumbo chunks) or scalability bottlenecks.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Compare MongoDB with Oracle Database for a real-time analytics application.

**Candidate:**

| Aspect | MongoDB | Oracle |
|--------|---------|--------|
| Schema evolution | Flexible (add fields on-the-fly) | Requires ALTER TABLE |
| Write throughput | Very high (no joins, no schema validation) | Good but overhead of constraints |
| Read performance | Index-dependent, aggregation pipeline | SQL optimizer, materialized views |
| Aggregation | Pipeline stages ($match, $group, $sort) | SQL analytic functions |
| Transactions | Multi-document (limited concurrency) | Full ACID, high concurrency |
| Scaling | Native sharding | RAC (shared disk) or sharding |
| Consistency | Tunable (eventual → strong) | Strong by default |

**MongoDB fits:** High-velocity data, flexible schemas, horizontal scale-out, early-stage products.
**Oracle fits:** Complex relationships, strict consistency, advanced analytics, compliance-heavy apps.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a MongoDB schema for a social media platform's activity feed (posts, likes, comments, shares). Optimize for reads (fetch user's feed in < 100ms).

**Candidate:** 

**Schema design (denormalized for read performance):**
```json
// User feed item (pre-computed on write, not on read)
{
  "_id": ObjectId("..."),
  "userId": "u_12345",
  "postId": "p_78901",
  "type": "post",
  "author": {
    "id": "u_67890",
    "name": "John Doe",
    "avatar": "https://cdn.example.com/avatars/u_67890.jpg"
  },
  "content": {
    "text": "Beautiful sunset today!",
    "images": ["https://cdn.example.com/photos/sunset1.jpg"]
  },
  "stats": {
    "likeCount": 42,
    "commentCount": 7,
    "shareCount": 3
  },
  "createdAt": ISODate("2024-07-23T18:30:00Z")
}

// Indexes
db.user_feed.createIndex({ "userId": 1, "createdAt": -1 })  // Feed query
db.user_feed.createIndex({ "userId": 1, "postId": 1 }, { unique: true })  // Dedup
```

**Fan-out approach:** When a user posts, the post is inserted into all followers' feeds (pre-computed). For celebrities (millions of followers), use "pull" model — fetch posts at read time.

**Feed query:**
```javascript
db.user_feed.find({ "userId": "u_12345" })
  .sort({ "createdAt": -1 })
  .limit(20)
  .hint({ "userId": 1, "createdAt": -1 });
```

---

## Interviewer Feedback

**Strengths:** Clear MongoDB-RDBMS comparison, practical social media schema design  
**Areas to Improve:** Could discuss MongoDB Atlas Search integration  
**Verdict:** Hire

---

*Databases Lab 04 MOCK_INTERVIEW.md*
