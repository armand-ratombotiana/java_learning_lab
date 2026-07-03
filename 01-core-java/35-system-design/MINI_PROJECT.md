# Module 35: System Design - Mini Project

**Project Name**: URL Shortener System Design  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Apply system design principles (Scalability, Caching, Load Balancing, Database Sharding) to architect a high-scale URL Shortener service (like Bitly).

## 📝 Requirements

### Core Features (Architectural Document)
Instead of writing code, you will write a comprehensive architectural design document answering the following prompts.

1. **Requirements Clarification**:
   - Traffic estimates: Assume 100 million new URLs generated per month, and 1 billion URL redirections per month.
   - Storage estimates: Assume a URL mapping is kept for 5 years. Calculate the required database storage capacity.

2. **High-Level Design**:
   - Draw or describe the architecture involving: Clients -> Load Balancer -> Web Servers -> Cache -> Database.
   - Detail the two primary APIs:
     - `POST /api/v1/data/shorten` (takes a long URL, returns a short URL).
     - `GET /{shortUrl}` (redirects to the original long URL with HTTP 301/302).

3. **URL Encoding Logic**:
   - Explain how you will generate the short URL. Will you use Base62 encoding? MD5 hashing? Or a distributed ID generator (like Twitter Snowflake or a Zookeeper-backed counter) converted to Base62?

4. **Data Partitioning (Sharding)**:
   - Because of the massive volume of data over 5 years, a single database will not suffice.
   - Propose a sharding strategy. Will you shard by the original URL, or by the generated short URL? Explain the pros and cons of your chosen Shard Key.

5. **Caching Tier**:
   - Since reads heavily outnumber writes (10:1 ratio), describe how you will implement caching.
   - Which cache topology will you use? (e.g., Redis).
   - What eviction policy is most appropriate here? (e.g., LRU).

---

## 💡 Solution Blueprint (Excerpt)

1. **Storage Estimates**:
   - 100M URLs/month * 12 months * 5 years = 6 Billion URLs.
   - If each record (Short URL + Long URL + CreatedAt) is ~500 bytes: 6B * 500 bytes = 3 Terabytes of storage required.

2. **API Design**:
   - Redirection: Return `HTTP 301 Moved Permanently` if you want browsers to cache the redirect (reduces server load). Return `HTTP 302 Found` if you need to track analytics for every single click (bypasses browser cache).

3. **Encoding Logic**:
   - Best approach: Use a highly available, distributed ID generator (or a standalone database just for auto-incrementing IDs). Take the numeric ID (e.g., `1000000`) and convert it to Base62 (using `A-Z, a-z, 0-9`), resulting in a unique, collision-free short string.

4. **Sharding Strategy**:
   - Shard Key: Hash of the `shortUrl`. 
   - Reason: When a user hits `GET /{shortUrl}`, we hash the short string to determine exactly which database shard contains the original URL, ensuring an `O(1)` fast lookup.

5. **Caching**:
   - Use Redis with an LRU (Least Recently Used) eviction policy.
   - Keep roughly 20% of daily traffic in memory (the 80/20 rule: 20% of URLs generate 80% of the traffic).