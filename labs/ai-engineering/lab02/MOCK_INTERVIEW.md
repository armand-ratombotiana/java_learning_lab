# Lab 02: Mock Interview — Vector Database Integration

**Role**: AI Engineer / Backend Engineer
**Duration**: 60 minutes
**Focus**: Embeddings, index structures, similarity search, ANN vs exact search

---

**Interviewer**: "Walk me through the architecture of the vector database in this lab."

**Candidate**: "At the top is `VectorDatabaseDemo`, which owns a store of `VectorRecord`
entries — an id, a float vector, and metadata. A `DistanceFunction` strategy abstracts
the metric: cosine, Euclidean, or dot product, so the index code never hardcodes a
similarity formula. On top of that sit the two indexes the lab contrasts: `FlatIndex`,
which scores every vector linearly, and `IVFIndex`, which partitions the space into
clusters and only searches the nearest ones. A query goes through the index interface,
returns `ScoredResult` objects sorted by distance, and the demo prints the neighbors
for a probe vector."

**Interviewer**: "What is the difference between FlatIndex and IVFIndex?"

**Candidate**: "FlatIndex is exact: it computes the distance from the query to every
stored vector and returns the true k nearest neighbors. It is O(N) per query and every
result is guaranteed correct, which makes it the benchmark to compare against. IVF
stands for inverted file: it clusters vectors at build time — the lab seeds a small
number of centroids and assigns each vector to the nearest one — and at query time it
only scores vectors inside the closest centroids. That turns O(N) into O(N/numClusters)
but introduces approximation: a true neighbor sitting just outside the searched
clusters can be missed."

**Interviewer**: "How do you choose between cosine, Euclidean, and dot product?"

**Candidate**: "Cosine measures angle, so it ignores magnitude — the right default when
embedding models produce arbitrary-length vectors and you care about direction, which
is why it is the common choice for text and semantic search. Euclidean measures actual
distance and is sensitive to magnitude, which matters when magnitude carries meaning,
as it can in some image or recommendation embeddings. Dot product is the fastest of
the three and collapses into cosine when vectors are normalized — in practice you
normalize and use dot product for throughput. The lab keeps the metric pluggable
because the choice is a property of the embedding space, not of the index."

**Interviewer**: "Why does normalization change the answer?"

**Candidate**: "For unit vectors, the dot product and cosine similarity are the same
thing up to sign conventions, so normalizing once at insert time lets you use the
cheaper dot product at query time without changing the ranking. Normalization also
removes magnitude skew: without it, a long vector can dominate cosine comparisons of
nearby directions. The lab's `DistanceFunction` handles this consistently — the same
metric used at insert time must be used at query time, because mixing a normalized
store with an unnormalized query silently changes the ranking."

**Interviewer**: "How does IVF clustering work, and where can it go wrong?"

**Candidate**: "At build time the index picks centroids — a simple approach is to sample
vectors or use a coarse k-means — and each vector is assigned to its nearest centroid,
storing the centroid assignment per record. At query time the search computes the
distance to the centroids, picks the nearest few, and scores only those clusters.
Where it goes wrong: boundary vectors sitting between clusters can be assigned poorly,
so the nearest neighbor can live in a cluster that was not searched. That is why
production ANN indexes expose a probe parameter — search more clusters and you trade
recall for latency, which is the fundamental ANN dial."

**Interviewer**: "How does the lab demonstrate exact-versus-approximate behavior?"

**Candidate**: "It runs the same query against both indexes and compares results. The
flat index returns the true nearest neighbors — the reference ranking — while the IVF
index returns its approximate ranking; the demo can flag where the two diverge. If the
data is clustered and the probe is close to a centroid, both agree and IVF looks
perfect; if a vector sits at a cluster boundary, the approximate result can miss it.
That divergence is the point: approximation is a recall trade, and the lab makes the
cost visible instead of asserting that ANN is 'good enough'."

**Interviewer**: "What is recall@k and how would you measure it here?"

**Candidate**: "Recall@k is the fraction of the true top-k that the approximate index
found in its own top-k. You measure it by taking a set of probe queries, computing the
exact neighbors with the flat index, running the same queries through the approximate
index, and counting overlap. The lab structures the data so this comparison is
repeatable: same vectors, same metric, same query set, two indexes. The number you
report should always come with the query set and the k — recall on five hand-picked
probes proves nothing, a held-out probe set with a stated k is a measurement."

**Interviewer**: "How do you handle inserts and deletes in an IVF index?"

**Candidate**: "Inserts are easy: compute the nearest centroid, append to that cluster.
Deletes are harder, because the standard approach is a tombstone: mark the record as
removed and filter it out of results, with a periodic compaction pass that physically
rebuilds the clusters and drops dead records. The lab mirrors this — records carry
metadata that filtering uses, and removal is a flagged state rather than a memory
hole. The design constraint: an index that never compacts slowly accumulates dead
weight and the clusters drift from the actual data distribution, so compaction is a
scheduled operation, not an afterthought."

**Interviewer**: "What metadata do you keep per vector, and how is it used?"

**Candidate**: "A `VectorRecord` carries the vector plus its metadata — source document,
section, timestamp, or any other attribute the application needs to filter. The lab
uses it two ways: it labels results for display, and it supports filtering so a query
can restrict the search to a subset, like 'only chunks from this document'. The
production lesson: filtering before the ANN search shrinks the candidate set and
improves both latency and recall, but you have to decide whether the filter applies
inside the index traversal or as a post-filter on the result list — post-filtering
can silently drop results and hurt recall."

**Interviewer**: "When would you choose an exact index in production?"

**Candidate**: "When the corpus is small enough that brute force is fast — tens of
thousands of vectors on a single machine is often fine, especially with dot product
on GPU — and when correctness matters more than latency, for example in small
deduplication jobs or a reference set used for evaluation. The habit of defaulting to
ANN for every corpus is wrong: ANN's whole benefit is sub-linear scaling, and it buys
nothing if the dataset fits in a single scan budget. The lab's lesson is that exact is
the baseline and approximation is a deliberate, measured trade, not the default."

**Interviewer**: "How do you choose the embedding model for a corpus?"

**Candidate**: "You choose on the retrieval benchmark, not on model buzz: build a
query-to-relevant-document set from the actual corpus, embed it with each candidate
model, and measure recall@k — the model that retrieves the right chunks for the
queries that matter wins. Two properties to check beyond accuracy: dimension, which
drives memory and query cost, and stability, because a model whose vectors drift
between versions silently degrades every index built on them. The lab's structure
supports this comparison because the index and the metric are independent of the
vectors — you can re-embed the same corpus with a new model and rerun the same
probes. The discipline: the embedding model is a versioned, tested dependency, not
a one-time choice."

**Interviewer**: "How would you scale this to a production RAG service?"

**Candidate**: "Three layers. First, the embedding pipeline: consistent chunking and a
stable embedding model, because the index is only as good as the vectors. Second, the
index layer: a sharded store partitioned by document or tenant so a query fans out to
a few shards instead of one giant index, and the metric and filter semantics are
identical on every shard. Third, the serving layer: cache hot queries, monitor recall
against a held-out probe set, and reindex on a schedule — embeddings drift when the
model changes, and the index must be rebuilt to match."

**Interviewer**: "What is the most common failure you see in vector search systems?"

**Candidate**: "Silent ranking drift — the system still returns results, so nobody
notices that quality degraded. The causes are invisible: a new embedding model with
different vector geometry, an index that was never rebuilt after large deletes, a
metric mismatch between insert and query, or probes tuned on stale data. The lab's
answer is to keep the exact index around as the ground truth and run periodic
recall checks against it, so approximation quality is a monitored number, not a
vibe. The other common failure is treating the vector store as a database: no
metadata filtering, no deletion strategy, no versioning of vectors — and it becomes a
black box you cannot debug."
