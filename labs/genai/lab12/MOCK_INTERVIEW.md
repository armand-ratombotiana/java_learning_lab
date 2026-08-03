# Lab 12: Mock Interview — Cost Optimization for LLMs

**Role**: Infrastructure / Applied AI Engineer
**Duration**: 60 minutes
**Focus**: exact & semantic caching, dynamic batching, prompt compression, speculative decoding, cost-per-request

---

**Interviewer**: "Walk me through the lab's cost-reduction toolkit."

**Candidate**: "Five levers on one theme — avoid paying for compute you don't need.
`ExactCache` is an LRU map over question→answer pairs: repeated queries skip
inference entirely; the demo shows a hit rate of 0.50 for one cached entry and
one miss. `SemanticCache` extends the idea by embedding queries and serving a
cached response when cosine similarity crosses a threshold — paraphrases hit
without exact string equality. `DynamicBatcher` groups queued requests into
batches up to a max size, amortizing GPU work — 6 requests drain as one batch of
4 plus one of 2. `PromptCompressor` strips stop words ('The cat is on the mat' →
'cat mat'). And `SpeculativeDecoder` has a small draft model propose tokens that
a big model verifies in parallel, at ~0.8 acceptance in the demo."

**Interviewer**: "The demo's semantic cache returns null for 'What is the capital of
France?' at threshold 0.85 — even though it's semantically the same as the cached
'capital of France'. Why?"

**Candidate**: "Because the lab's embeddings are random: `embed` seeds an RNG from
`text.hashCode()` and draws 8 Gaussian values. Two sentences that share words get
embeddings that are essentially independent random unit vectors, whose expected
cosine is zero. So the similarity between the query and the cached entry is noise,
and 0.85 is unreachable except for the exact same string. That's the honest lesson
of the demo: a semantic cache is only as good as its embeddings — production uses
a real encoder (CLIP-style, as in lab 08) where paraphrase similarity is high. My
walkthrough fixes the mechanism rather than the threshold: I override `embed` with
a deterministic lexical vector so 'capital of France' genuinely hits at 0.707, and
unrelated questions miss at ~0.2."

**Interviewer**: "Walk through the semantic cache math in your walkthrough."

**Candidate**: "The vocab accumulates words from every stored query; each query becomes
a normalized count vector over that vocab. 'What is the capital of France?' is a
6-word vector; 'capital of France' shares 3 words, so cosine = 3/(√3·√6) =
0.7071 — above the 0.7 threshold, hit, and the cached answer 'Paris' returns.
'France's capital city?' shares only 2 words: 2/(√4·√6) = 0.408 — miss. And 'Is
it raining in Rome?' has one overlapping stopword, ~0.18 — miss. The mix is
deliberate: one exact hit, one paraphrase hit, two misses, which is exactly the
precision-recall trade-off a threshold controls. The threshold is the knob: too
low, and 'raining in Rome' returns 'Paris' — a wrong hit, which is worse than a
miss because the user trusts it."

**Interviewer**: "What's the cost math in the walkthrough — what actually gets saved?"

**Candidate**: "I price every inference at $0.002 per request. Forty requests against
4 unique questions: without a cache that's 40 calls and $0.08; with the exact
cache all 40 are hits, so 0 calls and $0.00 — a 100% saving on that traffic,
which is realistic only because the workload is 100% repeats. Then batching: 100
requests at batch size 8 drain as 13 batches — 13 forward passes instead of 100,
87% fewer. Compression: a 16-token prompt drops to 10 tokens, 38% fewer tokens to
process and to store in the KV cache. Speculative decoding verifies 8 draft
tokens at ~0.88 acceptance — roughly one large-model forward pass instead of
eight. Real savings multiply these: cache the hot 20% of queries, batch the
long tail, compress the RAG context, and the bill drops by an order of magnitude
without changing the model."

**Interviewer**: "Why is the acceptance rate important for speculative decoding?"

**Candidate**: "It's the efficiency of the draft model: if the small model's guesses
match the big model's distribution, the big model verifies K tokens per forward
pass and you approach K× speedup. If acceptance is low, you paid for the draft
passes AND the verification, and you're slower than plain decoding. The lab's
verify accepts a token when `rng.nextDouble() > 0.2` — a fixed 80% probability —
which is why the demo lands near 0.80. Real systems train the draft model on the
target's distribution and adapt draft length to observed acceptance. The
walkthrough uses a seeded RNG so the number is reproducible (0.88), and the point
stands: the metric to watch is accepted tokens per target-model pass."

**Interviewer**: "When would you NOT use each of these techniques?"

**Candidate**: "Caching: when responses must be fresh — anything user-specific,
time-sensitive, or non-deterministic; a stale cached answer is a correctness
bug, not just a quality gap. Semantic caching needs careful threshold tuning and
a monitoring signal for wrong hits, because failures are silent. Batching: when
latency dominates — a 50ms collection window is a lot for a chatbot; the lab's
INTERVIEW guide says exactly that: throughput-sensitive, latency-tolerant
workloads. Compression: when the prompt is short, or when removing stop words
breaks the model's formatting or few-shot structure — and learned compression is
lossy, so facts in compressed regions can vanish. Speculative decoding: when the
draft model is too big, or the workload is latency-critical decode — the
verification adds fixed overhead per step. Every technique trades a cost axis
for another one; the walkthrough's savings table makes the trades explicit."

**Interviewer**: "How do these techniques interact with the KV cache?"

**Candidate**: "KV cache memory is `2 × layers × d_model × sequence_length` per
request — it grows linearly with context, which is why prompt compression helps
twice: fewer input tokens means less compute in prefill AND less KV memory to
hold. Exact and semantic caching help differently: they serve whole responses, so
they eliminate both the prefill and the KV allocation for the repeated request
entirely. Batching increases total KV memory in flight (more requests alive at
once) but amortizes the weights. So the two best friends of the KV cache are
compression (lab 13 territory) and caching (this lab). My walkthrough's token
reduction line — 16 → 10 tokens — is a direct KV budget number."

**Interviewer**: "The `ExactCache` is an LRU with a `removeEldestEntry` override. Why
LRU rather than something fancier?"

**Candidate**: "For a query cache, recency beats frequency in practice: users rediscover
the same questions in bursts, and LRU is O(1) amortized with a LinkedHashMap. The
access-order constructor flag (`true` third argument) turns every `get` into a
recency update, so the eldest entry is genuinely the least-recently-used. It's
also easy to reason about: bounded memory, predictable eviction. The lab could
have used a frequency-based policy (LFU) to protect a hot question from eviction,
but LRU's simplicity is a feature for a cache whose hit-rate payoff we're trying
to demonstrate. In my walkthrough I give it 100 slots and 4 questions, so nothing
evicts — the point there is the hit accounting (40/40), not the eviction policy."

**Interviewer**: "How would you take this lab to a production caching tier?"

**Candidate**: "Three additions. One: an embedding service with a real model — the lab
08 encoder or an off-the-shelf one — with versioned embeddings, because changing
the encoder invalidates every cached embedding. Two: hybrid lookup — exact hash
first, then ANN semantic search over the cache entries with a tuned threshold
(lab 04's retrieval stack), plus a TTL and an invalidation channel for
user-specific data. Three: observability — hit rate, semantic false-hit rate,
and cost-per-request by cache tier, feeding lab 14's metrics. And a product
decision: what's the staleness budget? The lab's cache stores plain strings; the
production version stores responses plus the parameters that make them valid."

**Interviewer**: "What's your strongest takeaway?"

**Candidate**: "That cost optimization is a set of quantified trades, and the lab's
value is that it makes the arithmetic visible: 0.50 exact-cache hit rate in the
demo, 13 vs 100 forward passes in the batcher, 16 → 10 tokens compressed, 0.80
draft acceptance. My walkthrough turns those into a single savings ledger —
$0.08 → $0.00 on cached traffic, 87% fewer batches, 38% token reduction. In an
interview, that's the narrative: name the lever, state the measurable, and be
ready to say which axis you're trading. The lab gives you all five levers and the
numbers to argue with."
