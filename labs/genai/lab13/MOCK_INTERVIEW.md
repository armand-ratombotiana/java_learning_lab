# Lab 13: Mock Interview — Context Window Management

**Role**: LLM / Long-Context Engineer
**Duration**: 60 minutes
**Focus**: sliding window attention, RoPE, ALiBi, context compression, KV cache, extrapolation

---

**Interviewer**: "Walk me through the lab's long-context toolkit."

**Candidate**: "Four techniques for making attention survive and thrive at long
sequences. `slidingWindowAttention(Q, K, V, W)` restricts each token to attend
over the last W positions — complexity drops from O(n²) to O(n·W). `applyRoPE`
rotates the Q and K pairs by position-dependent angles, `theta = pos /
10000^(i/dk)`, so relative positions are encoded in the geometry of the vectors.
`alibiAttention` skips learned position embeddings entirely and adds a linear
bias `-m·|i-j|` to the attention scores. `ContextCompressor` shrinks the KV
stream by keeping a fraction of the tokens. The demo runs all four on a fixed
8×4 sequence and prints shapes — 8×4 out for windowed and ALiBi, 4 tokens after
50% compression."

**Interviewer**: "What does the sliding-window complexity actually mean — show me the
numbers from your walkthrough."

**Candidate**: "For 8 tokens, full attention computes 64 pairwise scores. With W=3,
each token attends to `min(i+1, 3)` positions: 1+2+3+3+3+3+3+3 = 21 scores —
67% fewer at this toy size. Projected to a realistic shape it's the real story:
1024 tokens at W=64 means 1024×64 = 65,536 score computations versus 1,048,576
for full attention — 93.75% fewer. That's what lets models like Mistral run
with a 32K window that would otherwise be quadratic suicide. The catch: anything
older than W positions is invisible — no long-range recall — which is why
production models pair sliding windows with global attention layers or periodic
refresh points."

**Interviewer**: "RoPE rotates vectors by `pos/10000^(i/dk)`. What does the `i/dk`
exponent do, and what did your walkthrough reveal about tiny dk?"

**Candidate**: "The exponent makes the rotation frequency vary per dimension: the
first dimension pair rotates fastest (theta = pos), later pairs rotate slower
(theta = pos/100, pos/1000...), so each position gets a unique 'fingerprint'
across dimensions. My walkthrough prints the dim-0 angle per position: 0°, 57.3°,
114.6°, 171.9°, 229.2°, 286.5°, 343.8°, then 41.1° — because pos 7 gives theta =
7 rad = 401°, which wraps past 360°. In a real model dk is 64-128 and the
angles stay in useful ranges; the wrap is an artifact of the lab's dk=4, but it
shows the mechanism honestly: RoPE's positional information is bounded by the
per-dimension frequencies, which is exactly the material that interpolation
techniques (PI, NTK-aware) manipulate to extend context."

**Interviewer**: "Why does relative position encoding generalize better than absolute?"

**Candidate**: "Absolute embeddings are learned vectors per position — the model has
never seen position 100,001 if trained at 100K, so it extrapolates poorly.
Relative schemes depend on *distance*, and distances repeat: the relationship
between token 5 and token 6 is the same shape as between token 1,000,005 and
1,000,006. RoPE encodes that by rotation — the dot product of two rotated vectors
depends only on their angle difference, i.e., relative offset. ALiBi is the same
idea in the score space: `-m·|i-j|` depends only on distance. That's why both
extrapolate beyond training lengths, which is the 'context extension' problem
from the INTERVIEW guide: trained-short, deployed-long, and the fix is
positional mechanisms that scale."

**Interviewer**: "Your walkthrough measures recency bias: ALiBi raised the weight on the
3 most recent tokens from 0.220 to 0.302 in the last row. Walk through why."

**Candidate**: "In the last row (position 7), plain scaled-dot attention puts 0.220 of
its weight on tokens 5-7 — whatever the random Q/K geometry happened to do. ALiBi
subtracts `0.1 × |7-j|`: token 7 pays 0.0, token 6 pays 0.1, token 5 pays 0.2,
while token 0 pays 0.7 — after softmax, nearby tokens' scores are boosted and the
weight rises to 0.302. It's a deliberate prior: in most long documents, the
recent tokens are the most relevant (the model is often predicting something that
depends on the immediate context). The slope m controls the strength of the
prior; per-head slopes make some heads focus tightly and others loosely. The
lab uses a single 0.1 slope — simplified, but the demonstration is faithful:
position as a soft inductive bias rather than hard masking."

**Interviewer**: "What's the difference between what RoPE and ALiBi do at the mechanism
level, since both handle positions?"

**Candidate**: "RoPE transforms the *representations*: it rotates Q and K before the
dot product, so position enters the geometry of every vector and interacts with
content multiplicatively — a rotated query doesn't just know where it is, its
direction encodes where relative to the keys it will look. ALiBi operates on the
*score*: content computes `Q·K/√dk` as usual, and position is a pure additive
penalty, `-m·dist`. That's why ALiBi needs no position embeddings at all and is
trivial to add to any attention, while RoPE requires the rotation to be applied
consistently through every layer and interacts with attention variants
(careful with flash-attention implementations). Both pass the extrapolation
test; RoPE is the current default (GPT, LLaMA families), ALiBi is the elegant
lightweight option that some long-context research still builds on."

**Interviewer**: "ContextCompressor in the lab just truncates to the first k tokens.
That's not real compression — what does the real thing look like?"

**Candidate**: "Right — the lab's `compress(tokens, 0.5)` is `Arrays.copyOf(tokens,
keep)`: it keeps a prefix, which is a crude eviction policy, not compression. My
walkthrough still reports the KV arithmetic honestly — 8×4 → 4×4 is 50% KV memory
saved — but real context compression keeps the *informative* tokens: top-k
selection by attention weight, extractive summarization of the long document, or
a learned compressor that rewrites the context into condensed form (the
LLMLingua family from the INTERVIEW guide). The trade is always the same: lossy
reduction of context vs KV cache and compute savings, and the risk is dropping
the one fact the user asked about. The lab's lesson — measure what compression
saves in memory — transfers directly; the mechanism is the production part."

**Interviewer**: "How do these four techniques compose in one real model?"

**Candidate**: "A production long-context model typically uses RoPE for position,
which extrapolates to maybe 1.5-2x its training length, then either trains on
longer data with interpolation or switches to a windowed structure. Sliding
window handles the bulk quadratic cost; ALiBi can replace RoPE entirely in
designs that want zero learned position embeddings. Compression sits on top as a
serving-time lever: compress the conversation history or retrieved documents
(lab 04's chunks) before they enter the context, cutting both prefill cost and
KV growth. And every choice shows up in the KV cache budget — which is why my
walkthrough pairs each mechanism with its arithmetic: 21 vs 64 scores, 67%
savings, 93.75% projected, 50% compression."

**Interviewer**: "What breaks when you naively extend a model's context window?"

**Candidate**: "Positional breakdown and attention dilution. If the model used
absolute embeddings, unseen positions are garbage; if it used RoPE, the
distances exceed what the rotation frequencies were tuned for and attention
geometry distorts — that's where interpolation (stretching theta by a factor)
and NTK-aware rescaling come in. Independent of position, longer context means
more keys, so per-key attention mass shrinks — 'lost in the middle', which lab
04's reranker fights by repositioning evidence. And the KV cache balloons
linearly, which is the *serving* wall: you can extend the model but not the
GPU memory. The lab's toolkit addresses each: RoPE/ALiBi for position, windows
for quadratic cost, compression for KV — and the interview answer is to name
all three failures."

**Interviewer**: "Final question: which single metric should you track when deploying
long-context?"

**Candidate**: "Effective recall on a needle-in-a-haystack eval — can the model
actually use information placed deep in context — because context window size is
a marketing number, not a capability. My walkthrough's complexity numbers tell
you what the machinery *costs*; the needle test tells you what it's *worth*.
Everything else — RoPE angles, ALiBi slopes, compression ratios — is in service
of that question. The lab gives you the mechanisms and their arithmetic; the
eval is the arbiter of whether the mechanism helps."
