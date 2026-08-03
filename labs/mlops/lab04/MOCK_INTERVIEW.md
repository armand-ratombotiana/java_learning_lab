# Lab 04: Mock Interview — Feature Store Architecture

**Role**: ML Platform Engineer / MLOps Engineer
**Duration**: 60 minutes
**Focus**: Online vs offline stores, point-in-time joins, feature serving latency, training/serving consistency, Feast/Tecton trade-offs

---

**Interviewer**: "Walk me through the feature store in this lab. What are the two stores and what is each for?"

**Candidate**: "The lab splits the store the way production systems do. `OfflineStore` is the training side: it computes features in batch and stores them as time-series per entity and feature — the storage map is keyed `entityId:groupName`, then feature name, then a list of `TimeSeriesValue(timestamp, value)`. `computeAndStore` runs a `FeatureGroup`'s transformations over raw data and appends timestamped values, simulating a nightly batch write to Parquet. `OnlineStore` is the serving side: a simple in-memory KV map with TTL — `set(entityId, groupName, features)` writes the vector and stamps an expiry `Instant.now().plusSeconds(ttlSeconds)`; `get` returns `Map.of()` if the TTL passed. Same features, two physical stores: one optimized for historical queries, one for low-latency reads."

**Interviewer**: "Explain `getPointInTime`. What makes it correct rather than just 'the latest values'?"

**Candidate**: "`getPointInTime(entityId, groupName, atTime)` answers: what did we know about this entity *at this moment*? For each feature's time-series it filters `!v.timestamp.isAfter(atTime)` — keep only values observed at or before the query time — then takes the max timestamp among those. The demo makes the payoff explicit: features are computed on `day1`, `day2`, `day3`, and the training label is at `2025-01-02T12:00:00Z` — so the transaction features come back as the day-2 values (`avg 52.30`, `count 18`, `max 200.00`), not the day-3 values. A naive join would read today's features to predict yesterday's label, and the model would learn from the future — leakage. ASOF semantics, `merge_asof`, Spark range joins: same idea, and this is exactly what Airbnb's Zipline and Tecton automate."

**Interviewer**: "The demo's offline features show `account_tenure_days = 365` while the online store serves `366`. Same entity, different values. Is that a bug?"

**Candidate**: "No — it's the intended asymmetry, and it's the heart of feature stores. The offline value is the point-in-time value at the label timestamp: on `2025-01-01` the account had 365 days of tenure. The online value is what the model should use *right now* for real-time scoring: today the tenure is 366. Training and serving need different snapshots of the same logical feature, and the store keeps both. The failure mode to guard against is the inverse: if the online store silently served stale values (TTL expired, pipeline lag), the served vector would drift away from what the model saw in training — that's what the guide's 'monitor feature value distributions for drift' best practice is for."

**Interviewer**: "The `OnlineStore` implements TTL eviction. Walk through the mechanics and why TTLs exist at all."

**Candidate**: "Every `set` records `ttls.put(key, Instant.now().plusSeconds(ttlSeconds))`. Every `get` checks whether now is past the expiry; if so it removes the key — `store.remove` and `ttls.remove` — and returns `Map.of()` instead of stale data. That's the eviction policy, and it serves two purposes: memory hygiene — without it the store grows unboundedly as entities churn — and correctness — a feature vector computed three weeks ago shouldn't be scored as if it's current. In production this maps to Redis key TTLs and to the write-behind pattern in the interview notes, where batch updates refresh values before they expire. The `invalidate` method is the third path: explicit eviction when you know the value is wrong, e.g., after a data fix."

**Interviewer**: "Design a real-time feature store with P99 < 10ms, per the lab's Q1."

**Candidate**: "Redis cluster for the online store with consistent hashing for sharding — keys distribute by `entityId:featureGroup`, so one entity's vector lives on one node and reads are single-hops. Pre-compute features with Kafka Streams or Flink: consume the event stream, run the same transformations as batch, and write dual — Parquet to S3 for offline, Redis for online. The latency budget then comes from three more layers: local Caffeine cache on the prediction servers for hot keys — a top-10% user's features are read thousands of times a second; request coalescing so concurrent requests for the same key share one Redis fetch; and batching feature reads per request — one pipeline, not N round-trips. That gets you to single-digit-millisecond P99 with headroom."

**Interviewer**: "How do you keep training and serving features consistent — the Q4 'single definition' question?"

**Candidate**: "The lab's `FeatureGroup` is the answer in miniature: the transformation is defined once, as a `Function<Map<String, Object>, Object>`, and the same `FeatureGroup` object feeds `computeAndStore` (offline batch) — and conceptually the online writer — so there's one definition, not two codebases that can drift. The interview notes call this a single feature definition API that generates both training datasets and online vectors. The classic disaster this prevents: a team hand-writes features in Spark for training and in the Java service for serving, the Java version computes `age` differently, and the model silently degrades because serving features don't match the training distribution. Feast's `FeatureView`/`FeatureService` and Tecton's transforms are the productized versions of the lab's `FeatureGroup`."

**Interviewer**: "Feast vs Tecton vs SageMaker Feature Store — pick for three contexts."

**Candidate**: "Feast: open-source, self-hosted, flexible — right when you want control and have the infra skills; you own the servers and the operational burden. Tecton: managed, with automatic point-in-time joins and monitoring baked in — right for teams that want the correctness machinery without building it. SageMaker Feature Store: native AWS integration — right when the whole stack is already AWS. The lab's framing: Feast for flexibility and cost, Tecton for enterprise features, SageMaker for AWS-native shops. My default: Feast if the platform team is strong, Tecton if the ML team wants to spend its time on models, not joins."

**Interviewer**: "The guide lists 'monitor feature value distributions for drift'. What does that mean concretely for a feature store?"

**Candidate**: "You compute each feature's distribution at training time — the reference — and compare production values over time using the drift machinery from Lab 08: PSI, KL divergence, range checks. If `avg_transaction_amount_7d` shifts, either the world changed (concept drift — retrain) or the store broke (pipeline lag, double-counting events, a bad join — fix the pipeline). The feature store is the natural collection point because it already sees every training dataset and every served vector. The lab's best-practice list pairs this with TTL-based eviction and point-in-time joins as the three correctness pillars: right values, current values, and watched values."

**Interviewer**: "The lab returns `Map.of()` for missing features — both when the key was never computed and when the TTL expired. What's wrong with that as a production contract?"

**Candidate**: "Nothing, if callers are disciplined — an empty map is an explicit 'no data', and the demo's `get` distinguishes the expiry path by evicting. But it's also an easy way to ship silent zeros: a model server that sees an empty vector may fill defaults, and the monitoring layer can't tell 'no feature' from 'feature is zero'. Production hardening: return a typed result that distinguishes `FOUND`, `MISSING`, and `EXPIRED`; log the miss — a high miss rate means the feature pipeline is behind, which is an alert, not a shrug; and enforce fallback policy at the serving boundary. The `invalidate` method is the start of that discipline — explicit state changes instead of silent reads."

**Interviewer**: "The offline store's `storage` map is a `ConcurrentHashMap` and the online store's too. What concurrency properties do you actually need here?"

**Candidate**: "The maps give atomic `putIfAbsent`/`computeIfAbsent` and safe concurrent reads from multiple serving threads — needed because the online store is read on every prediction while batch jobs may refresh it. But the lab's structures are per-process: `computeIfAbsent(entityId + ":" + group.name, ...)` guards the inner-map creation race, which is the classic ConcurrentHashMap pattern. The lesson for production: concurrency correctness starts in the store so the serving layer doesn't have to lock globally; what you *don't* get here is distributed consistency — that's what Redis replication, consistent hashing, and the interview notes' write-behind cache add on top."

**Interviewer**: "Tie the lab to LeetCode 706, 981, and 146."

**Candidate**: "706 (Design HashMap) is the online store's skeleton — the KV map with put/get and, in production, sharding. 981 (Time-Based Key-Value Store) is `getPointInTime` to the letter: values stamped with time, lookup of the most recent value at or before a query time — the lab's `max(Comparator.comparing(v -> v.timestamp))` over the filtered series is the interview answer for 981. 146 (LRU Cache) is the eviction policy: the online store uses TTL eviction, and the interview notes' local caching layer adds Caffeine with size-based eviction — both are the 'keep hot keys, drop stale keys' trade-off 146 formalizes."

**Interviewer**: "What happens when the batch feature pipeline is late — the nightly job hasn't run and the online store still has yesterday's values?"

**Candidate**: "The TTLs eventually force honesty: values expire, `get` returns empty, and callers fall back rather than score on stale vectors. That's the design working. But the operational fix is the pipeline side: the offline job's freshness should be monitored with an SLA — 'features for day D are ready by 03:00' — and the online refresh should be write-behind so the store is *replenished* before values age out, not re-filled after expiry. The interview notes' write-behind cache is exactly that: batch updates keep the online store within a bounded staleness window. The point-in-time machinery protects training correctness; the TTL plus freshness SLA protects serving correctness."

**Interviewer**: "What would you change first if `getPointInTime` were called 10,000 times a second for training-data generation?"

**Candidate**: "Add an index. The linear scan — filter the list, take the max — is correct but O(k) per feature per query. For bulk generation you'd either pre-sort the series and binary-search the timestamp bound (as-of join on sorted tables), or switch to a range-join implementation like Spark's where both sides are sorted and joined in one pass. The correctness contract — most recent value with `timestamp <= atTime` — stays identical; only the data structure changes. That's the nice property of the lab's abstraction: `getPointInTime` is a semantic API, so swapping its internals doesn't change a single training pipeline that calls it."
