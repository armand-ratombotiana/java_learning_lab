$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\backend"

function Write-MockFile {
    param($dir, $content)
    $path = Join-Path $base "$dir\MOCK_INTERVIEW.md"
    Set-Content -Path $path -Value $content -Encoding UTF8
    $lines = (Get-Content $path | Measure-Object -Line).Lines
    Write-Output "$dir : $lines lines"
}

# File 01
$c01 = @"
# Mock Interview: In-Memory Cache with TTL and Eviction Policies

**Role:** Senior Backend Engineer (Staff Level)
**Duration:** 55 minutes
**Difficulty Progression:** Easy, Medium, Hard

---

## Round 1: Easy — Problem Understanding and API Design

**Interviewer:** We need to design an in-memory cache. Walk me through the high-level API and data structures you would use.

**Candidate:** The API surface is straightforward with four core operations. First is put(K key, V value, long ttlMillis) which stores a value with an optional TTL in milliseconds. Second is get(K key) which retrieves a value returning null if the key is expired or absent. Third is remove(K key) for explicit deletion. Fourth is clear() which removes all entries. The core backing store is a ConcurrentHashMap mapping keys to CacheEntry objects where CacheEntry is a simple holder for the value and the absolute expiration timestamp computed as System.currentTimeMillis() plus ttlMillis. I separate data storage from eviction logic using the Strategy pattern with an EvictionPolicy interface and concrete implementations for LRU, LFU, and FIFO. This makes the cache open for extension but closed for modification following the Open-Closed Principle.

**Interviewer:** Why ConcurrentHashMap instead of a synchronized HashMap?

**Candidate:** ConcurrentHashMap uses fine-grained locking with striped locks that allow concurrent reads without blocking and concurrent writes to different segments without contention. A synchronized HashMap serializes all read and write operations to a single monitor lock which becomes a severe bottleneck under high throughput. For a cache handling thousands of reads per second this performance difference is critical. The trade-off is that ConcurrentHashMap iterators are weakly consistent reflecting the state at some point since the iterator was created. For a cache eventual visibility of entries is perfectly acceptable because the worst case is a slightly stale read which is far better than blocking.

**Interviewer:** How does the TTL mechanism work at a detailed level?

**Candidate:** There are two complementary mechanisms. The first is lazy eviction on every get() call. Before returning the value I check whether System.currentTimeMillis() exceeds entry.expiryTime. If expired I remove the key from the map and return null. This ensures no caller ever receives a stale expired value. However entries never accessed again would remain in memory indefinitely. The second mechanism is a background cleaner using a PriorityQueue of ExpiryEntry objects ordered by ascending expiry time. A single-threaded ScheduledExecutorService runs every second polls the queue and removes entries whose expiry has passed. The dual approach ensures prompt cleanup on access through lazy eviction and guaranteed eventual cleanup through the background cleaner. The priority queue provides O(log n) insertion and O(1) peek for the next expiring entry.

**Interviewer:** How do you handle concurrent modification between the cleaner and application threads?

**Candidate:** All operations modifying shared structures including the ConcurrentHashMap and the PriorityQueue are protected by a ReentrantLock. The background cleaner acquires this lock before polling the queue and removing entries. The put() and remove() methods also acquire the same lock. This ensures mutual exclusion between cleaner and application writes. Reads via get() can proceed without the lock for the map lookup but need the lock if they modify access order for LRU tracking. I use a tryLock pattern in the cleaner to avoid blocking application threads if the lock is contended.

---

## Round 2: Medium — Eviction Policies

**Interviewer:** Walk me through the LRU eviction implementation in detail.

**Candidate:** LRU evicts the least recently accessed entry when the cache reaches capacity. My implementation uses a LinkedHashMap constructed with accessOrder set to true and removeEldestEntry overridden. In access-order mode every successful get() or put() moves the accessed entry to the end of the internal doubly-linked list. When removeEldestEntry is called after each new insertion if the map size exceeds maxCapacity it returns true and the eldest entry at the head of the list is automatically removed. This gives O(1) time complexity for both access tracking and eviction because linked list maintenance is constant time per operation. The downside is that LinkedHashMap is not thread-safe so I wrap all operations in the ReentrantLock.

**Interviewer:** Now the LFU implementation. Why is it harder and how would you make it O(1)?

**Candidate:** LFU evicts the least frequently used entry based on access count. The naive approach scans all entries to find the minimum frequency resulting in O(n) eviction time. For small caches this is fine but it becomes a bottleneck as the cache grows. The classic O(1) optimization uses a HashMap of frequency buckets where each bucket is a LinkedHashSet of keys at that frequency. A global variable tracks the current minimum frequency. On access we remove the key from its current bucket increment its count and add it to the next bucket. If the old bucket becomes empty and was at the minimum frequency we increment the minimum. On eviction we remove any key from the min-frequency bucket which is O(1) because LinkedHashSet provides constant-time removal. This algorithm is described in the paper An O(1) algorithm for implementing the LFU cache eviction scheme by Shah et al.

**Interviewer:** Compare FIFO with LRU. When would you choose FIFO?

**Candidate:** FIFO evicts the oldest inserted entry regardless of access pattern. It is simpler and cheaper with no access-tracking overhead. FIFO performs poorly for workloads with temporal locality where recently added entries are most likely to be accessed again. However FIFO excels when the access pattern is a sequential scan such as processing a stream of records where you cache per-record results and never revisit them. FIFO also avoids the scan resistance problem that plagues LRU where a single pass through a large dataset can completely replace the entire cache contents. LRU-K and ARC address this by maintaining multiple lists for recent and frequent entries.

**Interviewer:** Explain scan resistance with a concrete example.

**Candidate:** Consider a cache with capacity C and a workload that sequentially scans N unique items where N is much larger than C. Under LRU every new item evicts the least recently used item from the scan. The entire cache becomes filled with the most recent C items from the scan. When the application references the original working set again every single access is a cache miss. The miss rate during the post-scan phase is 100% until the working set is re-cached. LFU avoids this because frequency accumulation protects frequently accessed entries. An item accessed once during the scan has frequency 1 while working set items have frequency much higher than 1 so they are protected from eviction.

---

## Round 3: Hard — Concurrency and Edge Cases

**Interviewer:** You are using a single ReentrantLock. How can you improve concurrency?

**Candidate:** A single global lock serializes all writes. For read-heavy workloads with 95% reads this is acceptable. For write-heavy workloads I would use striped locking sharding entries by key hash into N segments each with its own lock. ConcurrentHashMap does this internally with 16 segments by default. The challenge is that eviction and expiry cleanup need a globally consistent view across all shards. One solution uses a generation clock where each shard maintains a counter and the eviction thread selects the shard with the oldest average generation. Another approach uses ConcurrentSkipListMap for the expiry queue providing lock-free reads and O(log n) writes without a global lock.

**Interviewer:** How do you handle put with a new TTL for an existing key?

**Candidate:** The new put overwrites the ConcurrentHashMap entry with a new CacheEntry containing the updated value and expiry time. The old ExpiryEntry in the PriorityQueue becomes stale because its expiry time no longer matches the map entry. In the background cleaner I verify the match by checking whether map.get(entry.key).expiryTime equals entry.expiryTime. If they differ the queue entry is stale and I skip it. This is lazy cleanup of stale queue entries. The queue may temporarily grow beyond the number of actual entries but growth is bounded by the rate of TTL updates.

**Interviewer:** Memory management how do you prevent the cache from consuming all heap?

**Candidate:** I would add a maxMemoryBytes configuration and use the java.lang.instrument.Instrumentation API or the jamm library to estimate object memory footprints. When estimated memory exceeds the limit I trigger eviction regardless of entry count. A simpler approach uses SoftReference for values allowing the GC to reclaim entries before OutOfMemoryError. However soft references are GC-policy dependent leading to unpredictable behavior. I combine both count-based and memory-based capacity.

**Interviewer:** How would you add metrics and observability?

**Candidate:** AtomicLong counters for hits, misses, evictions, total puts, and expired entries. Expose a CacheStats record with derived metrics like hit rate. The most important metric is hit rate which dropping below 90% indicates under-provisioning. Export via Micrometer to Prometheus or Datadog. Also expose JMX MBeans for runtime inspection. Alert when hit rate drops below 90% for more than 5 minutes or when eviction rate spikes.

---

## Round 4: Hard — Distributed Caching

**Interviewer:** How would you scale this cache across multiple instances?

**Candidate:** Use Redis or Memcached as an L2 backing store with the in-memory cache as an L1 client-side cache. The L1 cache uses a short TTL in seconds and subscribes to invalidation events via Redis pub/sub. This is the cache-aside pattern read from L1, miss to L2, populate L1. On writes update L2 and invalidate L1. Cache invalidation across instances uses Redis pub/sub with per-key messages. For high consistency requirements use write-through caching.

**Interviewer:** What are the key trade-offs in your design?

**Candidate:** First, single global lock versus striped locking trading simplicity for write throughput. Second, lazy plus background TTL eviction versus eager eviction trading immediate reclamation for lower overhead. Third, Strategy pattern for eviction policies trading extensibility for negligible virtual method dispatch cost. Fourth, in-memory only versus distributed trading operational simplicity for scalability. For under 10K req/s the current design is optimal.

**Interviewer:** How would you test this cache for correctness?

**Candidate:** Three levels. Unit tests verify each eviction policy with sequential operations. Integration tests verify TTL expiry with Thread.sleep() and concurrent access with CountDownLatch and multiple threads. Property-based tests with jqwik generate random sequences of puts, gets, removes, and sleeps verifying invariants like get never returns a value after TTL expiry and cache size never exceeds maxCapacity.

---

## Round 5: System Design Wrap-up

**Interviewer:** If you had to improve one thing what would it be?

**Candidate:** Replace the single ReentrantLock with a ReentrantReadWriteLock. Reads that do not modify access order only need the read lock allowing unlimited concurrent readers. Writes need the exclusive write lock. Since caches are predominantly read-heavy with hit rates above 90% this dramatically reduces contention. The background cleaner only acquires the write lock when actually removing entries. For access-order tracking I would use a concurrent LinkedHashMap like from Caffeine providing O(1) access-order recording without any locking on reads.

**Interviewer:** Good discussion. This covers the essential aspects of cache design.
"@
Write-MockFile "01-spring-boot-internals" $c01

# File 02
$c02 = @"
# Mock Interview: URL Shortener System Design

**Role:** Senior Backend Engineer
**Duration:** 55 minutes
**Difficulty Progression:** Easy, Medium, Hard

---

## Round 1: Easy — API and Core Logic

**Interviewer:** Design a URL shortener. What is the core API and how does shortening work?

**Candidate:** There are two primary endpoints. First is POST /shorten accepting a JSON body with fields for the long URL and an optional custom alias and returning a JSON response with the short key. Second is GET /{shortKey} returning an HTTP 301 or 302 redirect with the Location header set to the original long URL. The core operation is mapping a short key to a long URL. The key must be unique, short at 6 to 8 characters, and efficiently generated. I use SHA-256 hashing on the long URL then encode the first several bytes in Base-62 which uses a-z, A-Z, and 0-9 giving 62 characters. Base-62 with 7 characters yields 62 to the power of 7 which is approximately 3.5 trillion unique keys which is more than sufficient.

**Interviewer:** What exactly is Base-62 encoding and why use it over Base-64 or hex?

**Candidate:** Base-62 uses 62 characters consisting of digits zero to nine, uppercase letters A to Z, and lowercase letters a to z. It is URL-safe because it contains no special characters that would require URL encoding. Base-64 uses plus and forward slash which need encoding in URLs making keys longer. A 7-character Base-62 key represents about 41.7 bits of information calculated as log base 2 of 62 to the 7th power. Hex encoding with 7 characters gives only 28 bits which is 7 times 4 bits per character. So Base-62 is significantly more space-efficient. The encoding works by converting a large integer to base 62 by repeatedly dividing by 62 and taking the remainder as the next character index into the Base-62 alphabet string.

**Interviewer:** How do you handle key collisions when two different URLs hash to the same key?

**Candidate:** I generate the key from SHA-256 of the long URL. If a collision occurs which is extremely rare with 7 characters I append a salt consisting of a counter or timestamp suffix and rehash. In the code I retry up to 5 times using longUrl plus retryCount plus System.nanoTime as the input. The putIfAbsent method on the concurrent store ensures atomicity if the key already exists the next iteration tries a different salt. For custom aliases provided by the user the alias is used directly after validation for alphanumeric characters and length between 3 and 12 characters. If the alias is already taken we return 409 Conflict.

---

## Round 2: Medium — Redirect Strategy and Rate Limiting

**Interviewer:** 301 versus 302 redirect what is the difference and which would you use?

**Candidate:** A 301 redirect is a permanent redirect. Browsers cache this mapping and subsequent requests for that short URL go directly to the target URL without hitting the shortener server. This reduces load on our servers significantly but means we lose all analytics data because we never see those requests. A 302 redirect is a temporary redirect. Browsers always go through the shortener first allowing us to log every click for analytics. For a public URL shortener that provides click counts and geographic analytics I would use 302 by default. For link-in-bio services where the target never changes 301 is better for performance and reduced server load. A good compromise is using 302 by default but allowing the link creator to opt into 301 for known-stable links.

**Interviewer:** Your rate limiter uses a token bucket per IP. How does it work specifically?

**Candidate:** Each client IP has a bucket of tokens initialized to maxRequests. Every allowRequest call tries to consume one token from the bucket. If the bucket is empty the request is rejected. Tokens refill at a fixed rate calculated as maxRequests divided by windowSizeMs tokens per millisecond. My implementation uses lazy refill on each call I calculate the elapsed time since the last refill timestamp and add the appropriate number of tokens back to the bucket capped at maxTokens. I use AtomicLong with compareAndSet for lock-free token consumption. The bucket is created lazily on first request from a client and stored in a ConcurrentHashMap keyed by client IP.

**Interviewer:** What are the limitations of per-IP rate limiting and how do you address them?

**Candidate:** IP-based rate limiting has three well-known issues. First, users behind NAT share the same public IP so one abusive user can exhaust the limit for everyone in that office or ISP. Second, attackers can rotate through many IP addresses using botnets or cloud providers. Third, IPv6 addresses need special handling using /64 subnet aggregation rather than individual addresses. The solution is multi-factor rate limiting that combines IP-based limits with API key-based limits. Authenticated users with API keys get higher limits while unauthenticated requests are limited by IP with a stricter cap. I also add CAPTCHA challenge when a client exceeds 80% of the limit.

**Interviewer:** How do you ensure rate limit state consistency across multiple servers?

**Candidate:** For distributed rate limiting I use Redis with Lua scripting. The Lua script atomically checks and decrements the token count or adds the timestamp to a sorted set for sliding window. Redis single-threaded execution ensures atomicity without race conditions. The script returns 1 for allowed or 0 for rejected. For the token bucket the Lua script reads the token count decrements if positive and sets the key with expiry. I would also add a local L1 cache using Guava cache with a short TTL to reduce Redis load for repeated checks from the same client.

---

## Round 3: Medium-Hard — Data Model and Storage

**Interviewer:** What database would you use for the key to long URL mapping and why?

**Candidate:** I would choose a distributed key-value store or a relational database with strong consistency on the key column. The access pattern is simple point lookups by key. A relational database like PostgreSQL with a unique index on the short key column provides ACID guarantees which are important for preventing duplicate keys. The schema would be a table with columns for short key as primary key, long URL text, created at timestamp, last accessed timestamp, and access count with a default of zero. For higher throughput I would add Redis as a cache layer in front of PostgreSQL. For write-heavy scenarios Cassandra with its LSM-tree storage engine is better optimized.

**Interviewer:** How do you handle TTL and expiration of old URLs?

**Candidate:** URLs not accessed in six months can be archived. A background job scans for entries where last accessed is before now minus 180 days. Archived URLs are moved to cold storage such as S3 or HDFS with a tombstone entry indicating the archive location. When a request comes for an archived key the shortener fetches it from cold storage caches it and returns it. This keeps the hot table small and queries fast. The archive process uses batching to avoid impacting production traffic.

**Interviewer:** How would you handle URL validation before shortening?

**Candidate:** I validate the URL at submission time through several steps. First parse with Java URI class to ensure valid syntax. Second check the scheme is HTTP or HTTPS. Third perform a DNS resolution check to verify the domain resolves to an IP address. Fourth check against a blocklist of known spam and malware domains updated hourly. Fifth optionally perform a reachability check with a HEAD request to verify the server responds. I also run an async background check for content safety using a web crawler and flag or delete URLs that violate terms of service.

---

## Round 4: Hard — Scaling and Analytics

**Interviewer:** The system goes viral with 1 billion clicks per day. How do you handle the read load?

**Candidate:** One billion clicks per day translates to approximately 11,500 requests per second sustained with peaks at 50,000 or more. The critical path is the redirect. I would put a CDN like CloudFront or Cloudflare in front of the shortener domain. The CDN caches 302 redirects for a short time such as 60 seconds. For popular short URLs the CDN serves the redirect without hitting our origin at all. The caching key is the short URL path. This reduces origin load by 80 to 90 percent for viral links. Behind the CDN I would have a Redis cluster sharded by key hash as the primary read layer with PostgreSQL as the persistent store. Redis is also used for rate limiting state.

**Interviewer:** How do you collect click analytics without slowing down the redirect?

**Candidate:** Analytics collection is decoupled from the redirect path. The redirect handler writes an event to a Kafka topic containing the short key, timestamp, IP address, User-Agent header, and referrer header. A separate consumer reads from Kafka and updates click counts in PostgreSQL and a time-series database such as ClickHouse or Druid for analytics queries. Kafka acts as a buffer if the analytics pipeline is slow the redirect is not affected. The consumer batches updates for efficiency using a single SQL update statement with multiple keys.

**Interviewer:** How do you prevent abuse such as someone shortening thousands of spam URLs?

**Candidate:** Multi-layered abuse prevention. First rate limiting per IP and per API key as discussed. Second CAPTCHA for anonymous submissions above a low threshold such as 10 per hour. Third a domain blocklist with automatic checks against Google Safe Browsing API. Fourth anomaly detection if a single user shortens 1000 URLs in 5 minutes flag for manual review. Fifth content scanning a background worker visits the shortened URL takes a screenshot and runs it through a classification model for phishing and spam detection. Sixth an abuse reporting button on the redirect page lets users flag malicious URLs which are automatically disabled until reviewed.

---

## Round 5: Summary

**Interviewer:** Summarize the key design decisions and trade-offs.

**Candidate:** The main decisions are first SHA-256 plus Base-62 for key generation which is deterministic and requires no central coordinator. Second 302 redirect which favors analytics over caching with CDN caching as a compromise. Third Redis plus PostgreSQL for storage using the cache-aside pattern to balance read performance with durability. Fourth Kafka for analytics to decouple the hot path from data processing. Fifth token bucket rate limiting which allows bursts while enforcing average rate. The key trade-off is between redirect latency and analytics accuracy where 302 gives perfect analytics at the cost of an extra round trip mitigated by CDN caching.
"@
Write-MockFile "02-rest-api-design" $c02

# File 03
$c03 = @"
# Mock Interview: Pagination and Sorting Framework

**Role:** Senior Backend Engineer
**Duration:** 55 minutes
**Difficulty Progression:** Easy, Medium, Hard

---

## Round 1: Easy — Problem and API Design

**Interviewer:** Design a generic pagination and sorting framework. What are the core components?

**Candidate:** The framework has two sides the request and the response. The request is a Pageable object containing a page number starting from 1, a page size typically between 1 and 1000, sort criteria as a list of sort orders, and optionally a cursor string for keyset pagination. The response is a Page object containing the items list for the current page, total count of all matching items, pagination metadata including hasNext, hasPrevious, totalPages, the current sort specification, and optionally a next cursor for keyset pagination. The framework must support two pagination strategies. Offset-based pagination uses LIMIT and OFFSET which is simple but inefficient for large datasets. Cursor-based pagination also called keyset pagination uses a seek approach with the WHERE clause that is efficient regardless of depth.

**Interviewer:** What is wrong with offset-based pagination for large datasets?

**Candidate:** Deep offset pagination has O(offset plus limit) complexity because the database must scan and discard offset rows even though they are not returned. At offset one million the database scans a million rows just to return twenty. The SQL OFFSET clause does exactly this there is no optimization for high offsets in any major database. Additionally under high write load offset-based pagination can miss or duplicate rows because new insertions can shift the position of existing rows between pages. Cursor-based pagination avoids these issues by using WHERE id greater than lastSeen ORDER BY id LIMIT 20 which leverages the primary key B-tree index for O(log n plus limit) performance.

**Interviewer:** What is the structure of your Sort specification?

**Candidate:** Sort is a list of SortOrder records each with three fields. The property name which can be a nested path like address.city. The direction which is ASC for ascending or DESC for descending. The null handling which is NULLS FIRST or NULLS LAST. Multiple sort orders create a composite sort for example Sort.by(SortOrder.desc(score), SortOrder.asc(name)). Explicit null handling is critical because NULL comparison behavior differs across databases. PostgreSQL treats NULL as larger than any value. MySQL treats NULL as smaller than any value. SQL Server is configurable. Making null handling explicit ensures predictable behavior regardless of the underlying database.

---

## Round 2: Medium — Keyset Pagination

**Interviewer:** Explain keyset pagination in detail. What are the prerequisites for using it?

**Candidate:** Keyset pagination requires a unique ordered column or tuple of columns to serve as the cursor. The cursor is typically the primary key or a composite of the sort column plus the primary key. The query pattern is SELECT columns FROM table WHERE (sort_col, id) greater than (lastSortVal, lastId) ORDER BY sort_col, id LIMIT 20. No OFFSET is needed. The database uses a composite index on (sort_col, id) for an efficient index seek rather than a full scan. The prerequisites are that the cursor column must be indexed, the cursor value must be unique to avoid skipping duplicates, and the client must send the last cursor value from the previous page.

**Interviewer:** How do you handle the no OFFSET constraint when the user wants page five?

**Candidate:** Keyset pagination does not support arbitrary page jumps you can only go forward or backward from the current position. For applications that need numbered page navigation keyset is a poor fit. There are three solutions. First a hybrid approach that uses offset-based for the first few pages and keyset for deeper pages. Second estimate the cursor for the desired page using inverse distribution functions like percentile estimation. Third use a different UI pattern such as infinite scroll or load more that does not require page numbers. In practice most modern applications prefer infinite scroll making keyset the natural choice.

**Interviewer:** How does your cursor encoding work?

**Candidate:** The cursor is an opaque string that encodes the last seen sort values. For a single-column sort on id the cursor is just the last id as a string. For multi-column sort on score, name, and id I serialize the tuple as score colon name colon id and Base-64 encode it. The client sends this cursor back in the next request. The server decodes it and uses it in the WHERE clause. The cursor must be tamper-proof so I add an HMAC signature to prevent clients from manipulating the cursor to access data they should not see.

---

## Round 3: Medium-Hard — Sorting and Comparison

**Interviewer:** How does your framework handle sorting on nested property paths like address.city?

**Candidate:** I support nested property paths using dot notation. The framework splits the path by the dot character and navigates the object graph using reflection or a provided function. For example Sort.asc(address.city) calls getAddress().getCity() on each entity. For SQL generation nested paths become JOINs on the related table with ORDER BY on the joined column. For performance I recommend denormalizing commonly sorted nested fields into the main entity table or using computed columns with indexes. Another option is to use a document store like Elasticsearch for complex sorting scenarios.

**Interviewer:** Case-insensitive sorting how would you add it?

**Candidate:** I would add a caseInsensitive boolean flag to SortOrder. When enabled the comparator converts both values to the same case using toLowerCase before comparison. For SQL generation it becomes ORDER BY LOWER(name) ASC. The flag has CPU and memory overhead from the extra string allocation so it should only be used when needed. I would also add locale-aware collation for internationalized data using a java.text.Collator parameter. Different locales sort characters differently and the Collator handles these rules correctly.

**Interviewer:** How do you handle sorting by nullable columns?

**Candidate:** The NullHandling enum with NULLS_FIRST and NULLS_LAST values defines the insertion behavior for null values. In the comparator implementation both null values are considered equal. If one value is null and the other is not the null handling decides whether null comes first or last. For SQL generation it becomes ORDER BY score ASC NULLS LAST on databases that support the native NULLS syntax or ORDER BY CASE WHEN score IS NULL THEN 1 ELSE 0 END, score ASC for databases that do not. The default null handling depends on the sort direction ASC typically puts nulls last while DESC puts nulls first.

---

## Round 4: Hard — Performance and Production

**Interviewer:** The COUNT query for totalCount is expensive on large tables. How do you avoid it?

**Candidate:** There are several strategies each with different trade-offs. Strategy one is estimation using EXPLAIN or pg_class.reltuples for approximate counts sacrificing accuracy for speed. Strategy two is caching the count and updating it asynchronously every few seconds accepting staleness. Strategy three is adding an includeCount boolean flag to the request defaulting to false so the caller can opt out when not needed. Strategy four is the hasNext trick which fetches size plus 1 rows returns only size rows and sets hasNext to true only if rows.size() exceeds size. This completely avoids the COUNT query. Strategy five is accepting that for keyset pagination total count is often irrelevant because users navigate forward infinitely. I would use strategy four as the default.

**Interviewer:** How would you implement streaming export of 10 million records?

**Candidate:** Streaming does not load the entire result set into memory. I return a Stream backed by keyset pagination that fetches records in batches of 1000. The Spliterator implementation handles this by returning the next record from the current buffer and fetching the next page when the buffer is empty. The HTTP response uses chunked transfer encoding with Transfer-Encoding chunked. For database streaming I use a forward-only read-only JDBC cursor with fetchSize set to a reasonable value like 1000. This avoids loading the entire result set into application memory.

**Interviewer:** How does your framework handle cursor invalidation when a row is deleted between pages?

**Candidate:** In keyset pagination if a row is deleted the next page simply starts from the cursor position and returns the next available row. No rows are missed because the cursor is the last successfully returned value. If the cursor row still exists the WHERE clause correctly skips it. The only issue is if the cursor value itself changes due to an update. In that case the row might appear again in the next page or be skipped. Using an immutable sort column or snapshot isolation prevents this. For most applications this edge case is rare enough to accept.

**Interviewer:** How do you test a pagination framework?

**Candidate:** I test several aspects. First correct page boundaries verifying first page last page and out-of-bounds pages return expected results. Second sort correctness for ascending descending multi-column and null handling. Third keyset pagination ensuring sequential pages return consecutive non-overlapping gapless results. Fourth edge cases like empty dataset single page page size larger than dataset and duplicate sort values. Fifth concurrent modifications verifying that concurrent inserts and deletes do not produce inconsistent results. Sixth streaming verifying that streaming returns exactly all records without exhausting memory.

---

## Round 5: Wrap-up

**Interviewer:** What is the most important lesson about pagination for a junior developer?

**Candidate:** Never use OFFSET-based pagination beyond the first few pages on a large or write-heavy dataset. Always prefer keyset pagination for production systems. The performance difference between OFFSET 1000000 LIMIT 20 which scans one million rows and WHERE id greater than 1000000 LIMIT 20 which scans 20 rows via an index is three to four orders of magnitude. Always include a unique tiebreaker column such as the primary key in your ORDER BY clause to avoid row skipping or duplication.
"@
Write-MockFile "03-spring-data-jpa" $c03

# File 04
$c04 = @"
# Mock Interview: JWT with Refresh Token Rotation

**Role:** Senior Backend Engineer
**Duration:** 55 minutes
**Difficulty Progression:** Easy, Medium, Hard

---

## Round 1: Easy — JWT Basics

**Interviewer:** What is a JWT and why is it used for authentication?

**Candidate:** JWT stands for JSON Web Token and is defined by RFC 7519. It is a compact URL-safe token format consisting of three Base64-url-encoded parts separated by dots. The header specifies the signing algorithm such as HS256 for HMAC-SHA256 and the token type which is JWT. The payload contains claims about the user and the token itself including the subject or user ID, issuer, issued-at timestamp, expiration timestamp, and a unique JWT ID. The signature cryptographically binds the header and payload so that any tampering is detectable upon verification. For authentication the server issues a JWT on successful login and the client sends it in the Authorization Bearer header on all subsequent requests. The server verifies the signature without needing to query a database which is the key advantage JWTs are stateless.

**Interviewer:** What is the difference between access tokens and refresh tokens?

**Candidate:** Access tokens are short-lived typically lasting 15 minutes and they carry the user identity and permissions. They are sent with every API request. Refresh tokens are long-lived typically lasting 7 days and they are used only to obtain new access tokens. The separation serves two purposes. First if an access token is stolen the damage window is limited to 15 minutes. Second the refresh token can be stored more securely such as in an HttpOnly cookie or a secure client-side store because it is used infrequently only when the access token expires.

**Interviewer:** What goes in the JWT payload give a concrete example.

**Candidate:** The payload contains registered claims including sub for subject or user ID, iss for issuer, iat for issued-at time as Unix timestamp, exp for expiration time as Unix timestamp, and jti for JWT ID which is a unique identifier for the token. Custom claims include roles as a list of role names and type which is either access or refresh. A decoded access token payload example is user ID user-42, issuer backend-academy, issued at 1700000000, expiration at 1700000900, roles including admin and user, and type set to access. The expiration is calculated by adding the token TTL in seconds to the current time.

---

## Round 2: Medium — Refresh Token Rotation

**Interviewer:** What is refresh token rotation and why is it important?

**Candidate:** Refresh token rotation means that every time a refresh token is used to obtain a new access token the server also issues a new refresh token and invalidates the old one. This limits the window of compromise significantly. Without rotation a stolen refresh token is valid for its entire lifetime such as 7 days giving the attacker a full week of access. With rotation the token is valid for only a single use. If an attacker steals the token and uses it before the legitimate user the legitimate user next refresh will fail alerting them to the compromise. Rotation is an OAuth 2.0 Best Current Practice recommendation from RFC 9700.

**Interviewer:** Explain the reuse detection mechanism in detail.

**Candidate:** The server maintains a persistent set of used refresh token hashes. When a refresh request comes in the server first checks whether the token hash is in the used set. If it is not the token is valid and the server rotates it by invalidating the old token and issuing a new access token and a new refresh token. The old token hash is then added to the used set. If the hash is already in the used set the token has been reused meaning someone has a copy. In this case the server revokes all tokens for that user forcing them to re-authenticate. The detection relies on a race condition between the legitimate user and the attacker racing to use the refresh token first. The loser request triggers the alarm.

**Interviewer:** What happens when the legitimate user loses the race and gets their tokens revoked?

**Candidate:** They receive a 401 response with a specific error code indicating token reuse was detected. Their client application should handle this by redirecting to the login page for re-authentication. This is a minor inconvenience compared to the attacker maintaining persistent access to the account. To reduce false positives I add a grace period on first reuse detection I issue a warning but do not revoke tokens. Only if reuse happens again within the grace period do I revoke everything. This handles network retries that might cause the same valid token to be sent twice in rapid succession.

**Interviewer:** How do you implement the used token store and what happens if it is lost on restart?

**Candidate:** The used token set must be persistent across restarts. I use Redis with a TTL matching the refresh token lifetime. The key is rotated colon and the token hash with an expiry of 7 days. This prevents unbounded growth of the set. If Redis is lost due to a crash we lose the used-token set temporarily. The consequence is that a compromised token could be reused once without detection. To mitigate this I make the refresh token lifetime short enough that the risk window is small. I also use Redis persistence with AOF and RDB to minimize data loss.

---

## Round 3: Medium-Hard — Token Signing and Verification

**Interviewer:** HMAC-SHA256 versus RSA or ECDSA which signing algorithm would you choose?

**Candidate:** HMAC-SHA256 is symmetric meaning the same secret both signs and verifies tokens. It is simpler to implement but any service that can verify tokens can also sign them if the secret is shared. RSA and ECDSA are asymmetric a private key signs and a public key verifies. For microservice architectures I would choose RSA or ECDSA because only the authentication service holds the private key while all other services only need the public key. This limits the blast radius. Between RSA and ECDSA I would choose ECDSA with curve P-256 also called ES256 because the keys are smaller and verification is significantly faster.

**Interviewer:** How do you handle JSON Web Key rotation for asymmetric keys?

**Candidate:** The signing key should be rotated periodically such as every 90 days. The authentication service exposes a well-known JWKS endpoint listing current and recent public keys. Each key has a key ID that appears in the JWT header. Verifying services cache the JWK set and refresh it periodically such as every hour. When a new key is introduced tokens signed with the old key remain valid until they expire. Old keys are removed from the JWK set after the maximum token lifetime such as 7 days for refresh tokens. This allows graceful rotation without invalidating any existing sessions.

**Interviewer:** Why use constant-time comparison in signature verification?

**Candidate:** Java MessageDigest.isEqual performs a constant-time comparison meaning it compares all bytes regardless of when the first mismatch is found. A naive Arrays.equals or String.equals short-circuits on the first different byte which leaks timing information about how many prefix bytes matched. An attacker can exploit this to forge signatures byte by byte measuring the response time to determine when each byte is correct. Constant-time comparison prevents this timing side-channel attack entirely.

**Interviewer:** How do you handle clock skew between servers?

**Candidate:** I add a configurable clock skew parameter defaulting to 5000 milliseconds to the expiration check. Instead of checking whether the current time is greater than the expiration time I check whether it is greater than the expiration time plus the clock skew. This allows tokens to be accepted up to 5 seconds after their nominal expiration to account for minor clock differences between the issuing server and the verifying server. The same skew is applied to the issued-at and not-before claims. The skew should be kept small because large skews defeat the purpose of token expiration.

---

## Round 4: Hard — Production Hardening

**Interviewer:** How would you scale JWT verification across 100 microservices without duplicating the secret everywhere?

**Candidate:** I would use the API Gateway pattern. The gateway handles all authentication by verifying access tokens extracting the user context and forwarding requests to downstream services with the user information in HTTP headers such as X-User-Id and X-User-Roles. Downstream services trust the gateway because they are in a private network and do not need to verify JWT signatures themselves. This keeps the JWT secret in one place the gateway and simplifies the architecture. For service-to-service communication within the private network I use mutual TLS instead of JWTs.

**Interviewer:** Your active refresh token store is in-memory. How does it survive a restart?

**Candidate:** It does not survive a restart which means all active sessions would be invalidated forcing all users to re-authenticate. For production I persist refresh token metadata to Redis with a TTL matching the token lifetime. The stored data includes user ID, family ID, expiry time, and a revoked flag. Redis persistence using AOF and RDB provides recovery across restarts. The in-memory ConcurrentHashMap in my code is for demonstration only production systems should never rely on ephemeral storage for session data.

**Interviewer:** How would you implement token blacklisting for immediate logout?

**Candidate:** I store the token hash in Redis with a TTL equal to the remaining token lifetime. The verify method checks this blacklist before verifying the signature. The Redis key format is blacklist colon and the token hash with an expiry of the remaining TTL. This list is self-cleaning because Redis automatically removes expired keys. Each verify request now requires a Redis round trip which adds latency. A compromise uses a Bloom filter for a fast probabilistic blacklist check with Redis as the authoritative source for confirmation.

**Interviewer:** What are the security implications of storing the signing secret in source code?

**Candidate:** The signing secret must never be stored in source code. It should be injected via environment variables or a secrets manager such as HashiCorp Vault or AWS Secrets Manager. If the secret leaks an attacker can forge arbitrary tokens that would be accepted by all services. Regular rotation of the signing key mitigates the impact of a leak because tokens signed with the compromised key will stop being accepted. I also set up monitoring for unusual token issuance patterns such as a sudden spike in token creation and alert on anomalies.

---

## Round 5: Summary

**Interviewer:** Summarize the critical design decisions in your JWT system.

**Candidate:** The critical decisions are first the two-tier token architecture with access and refresh tokens balancing security with user experience. Second refresh token rotation with reuse detection which detects token theft in near real-time. Third choosing between HMAC simplicity and RSA or ECDSA flexibility based on architecture. Fourth constant-time signature comparison to prevent timing attacks. Fifth clock skew tolerance to avoid false rejections. Sixth Redis-backed persistence for session state that survives restarts. The most important principle is defense in depth even if one layer fails such as the signing key leaking the rotation and reuse detection provide additional protection.
"@
Write-MockFile "04-spring-security" $c04

Write-Output "Files 01-04 complete"
