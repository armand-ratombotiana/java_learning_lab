# Lab 02: Mock Interview — GPT Architecture

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: decoder-only architecture, causal masking, BPE tokenization, autoregressive generation, KV cache

---

**Interviewer**: "Why is GPT decoder-only, and what trade-off does that make versus
encoder-decoder models?"

**Candidate**: "Decoder-only means a single stack of blocks with causal masking throughout —
every token only sees tokens to its left. The lab's `causalAttention` enforces that by
setting `scores[i][j] = Double.NEGATIVE_INFINITY` when `j` is beyond position `i`, which
becomes a zero probability after softmax. The trade-off: an encoder-decoder model can
give the encoder full bidirectional context over the input, which helps translation or
summarization, but the decoder-only design is simpler to scale — one stack, one
objective, left-to-right language modeling — and with enough parameters and data it
learns to infer the input-side understanding implicitly. In practice nearly every
frontier model now is decoder-only, including the reasoning models."

**Interviewer**: "The demo tokenizes 'the cat sat on mat' and prints `[3, 4, 5, 6, 7]`.
Where do those ids come from?"

**Candidate**: "From the constructor order of the vocabulary. The `BPETokenizer` assigns ids
sequentially as it first encounters each token: `[<pad>, <unk>, <eos>, the, cat, sat,
on, mat, hello, world]`, so `the=3`, `cat=4`, `sat=5`, `on=6`, `mat=7`. `encode` splits
on spaces and looks up each part, and unknown words map to `<unk>` via
`getOrDefault(parts[i], tokenToId.get("<unk>"))`. `decode` reverses the map and joins
with spaces. It's a demonstration tokenizer — a real BPE would iteratively merge the
most frequent adjacent pairs to build the vocab, as the guide describes, but the
interface is the same."

**Interviewer**: "Explain the causal mask logic in the lab's `causalAttention`. It doesn't
build an upper-triangular matrix — how does it work?"

**Candidate**: "It does masking without ever allocating a mask. The scores matrix is
`seqLen x fullLen` — the new tokens' rows against all cached plus new keys. The
condition `if (j > i + fullLen - seqLen)` makes `-infinity` exactly the upper-right
triangle of each row: for row `i`, only the first `i + 1` positions survive. When
`kvCacheK` is null, `fullLen == seqLen` and the guard reduces to `j > i`, the classic
upper-triangular mask. When a cache is present, the offset `fullLen - seqLen` shifts
the triangle so cached keys are always visible. After softmax those `-infinity`
entries are zero probability, so the output at position `i` is a weighted sum of
positions `0..i` only."

**Interviewer**: "The demo's full attention prints rows `[0.1], [0.15], [0.2], [0.25]`.
Why does the output grow linearly like that?"

**Candidate**: "Because the inputs are degenerate by design: every `Q` row is `0.5`, every
`K` row is `0.3`, and `V` rows are `0.1 * (i + 1)`. The dot products are all identical,
so after softmax, row `i` weights the values uniformly — row 0 attends only to
`V[0] = 0.1`, row 1 attends equally to `V[0]` and `V[1]`, averaging to `0.15`, and so
on. Row `i` is the average of the first `i + 1` values, hence `0.1, 0.15, 0.2, 0.25`.
It's the cleanest possible sanity check that the causal weighting is summing over the
right positions."

**Interviewer**: "The KV-cache test shows `[0.30000000000000004]` for the cached path.
What is that value and why the funny float?"

**Candidate**: "The cached path passes one new query and one new key-value pair while
reusing the full `K` and `V` from before, so the new token attends over all five
positions. The first four `V` rows are `0.1, 0.2, 0.3, 0.4` and the new one is `0.5`,
and with uniform weights the average is `0.3`. The `0.30000000000000004` tail is just
binary floating point: `0.1 + 0.2` computed as a sum of binary fractions can't be
represented exactly, so the average lands one ulp off. Worth mentioning because in
production, comparing cached vs recomputed outputs needs a tolerance, not exact
equality."

**Interviewer**: "What does the KV cache actually cache, and why does it turn per-step cost
from quadratic into linear?"

**Candidate**: "It caches the key and value projections of every previously generated
token. Without the cache, generating token `n+1` recomputes `K` and `V` for the entire
prefix — the lab's `causalAttention` gets called over all `n` rows, which is `O(n²·dk)`
multiplied across layers. With the cache, you compute `K`, `V` only for the one new
token and `concatRows(kvCacheK, K)` joins them, so each step is `O(n·dk)` — one new
query against `n` cached keys. My walkthrough program quantifies this on the prompt
'the cat sat on' over 6 steps: 1420 attention FLOPs recomputed versus 180 cached,
a 7.89x measured speedup on this toy size. At real context lengths the gap is much
wider — that's why the KV cache is the single most important inference optimization."

**Interviewer**: "What's the memory cost of the KV cache in a real deployment?"

**Candidate**: "It's `2 * numLayers * numHeads * headDim * numTokens * bytesPerElement`
per sequence — 2 for K and V, times layers and heads and context length. For a 70B
model at 8K context and batch 64 that's tens of gigabytes per batch, which is why
memory, not compute, is usually the bottleneck at long context. The levers: batch and
context dominate, so serving systems do prefix sharing and PagedAttention-style
management; quantization brings it down (the `bytesPerElement` factor — this is where
lab 11's INT8 ideas apply); and chunked prefill overlaps compute with memory transfer.
The lab's toy cache has none of these concerns, but the shape of the problem is
exactly what the `fullLen - seqLen` offset in the mask is handling."

**Interviewer**: "Walk through the lab's `generate` method. What are its assumptions, and
what's the sampling loop doing?"

**Candidate**: "`generate(prompt, weights, maxTokens, tok, temperature)` seeds a `Random(42)`
so runs are reproducible. At each step it computes logits as a weighted sum of the last
`contextLen` token ids against a fixed weight matrix, applies softmax with temperature
— `Math.exp((logits[i] - maxLogit) / temperature)` — then draws one token from the
resulting categorical distribution. The context windowing — `Math.min(seq.size(),
weights.length)` — models a fixed-size context, which is a real constraint. The
implementation is simplified: a real model's logits come from the final layer norm and
output head, and temperature 1.0 is identity while lower temperatures sharpen the
distribution toward greedy. But the structure — predict, append, repeat — is exactly
the autoregressive loop that everything, from GPT-2 to frontier models, runs."

**Interviewer**: "The demo vocabulary contains `<pad>` and `<eos>`. How would a production
generation loop use `<eos>`?"

**Candidate**: "The loop should stop when the sampled token is `<eos>` rather than running
`maxTokens` unconditionally — otherwise every completion has the same fixed length,
which is wrong for real workloads. In the lab's `generate` the loop only counts steps,
it never checks the sampled token, which is fine for a demo but a production
generation loop terminates on `<eos>`, enforces a max length as a safety bound, and
handles the interplay with beam search or sampling parameters. `<pad>` matters
differently: it's for batching sequences of unequal length together, and the causal
mask or an attention bias must ensure padding positions never contribute — the lab's
`<unk>` fallback in `encode` is the related tokenizer behavior."

**Interviewer**: "Greedy decoding, beam search, temperature sampling — when do you use each?"

**Candidate**: "Greedy takes the argmax at every step: cheap and deterministic, but one bad
early choice is never recovered. Beam search keeps the top-`k` sequences, which helps
structured tasks like translation where you want global consistency. Temperature
sampling draws from the distribution — `temperature < 1` sharpens toward greedy and is
what the lab's `generate` demonstrates; it's the standard for open-ended chat and
creative generation because it introduces variety. The trade-off axis is quality
versus diversity and latency; a production service typically exposes temperature and
top-p to clients and runs greedy internally for deterministic tasks like classification
or extraction."

**Interviewer**: "The walkthrough's generation produced 'mat mat cat the on hello' with a
visible loop of repeated tokens. Why does repetition happen and how do you mitigate it?"

**Candidate**: "Autoregressive models can get stuck in a self-reinforcing loop: once 'mat'
is likely, it becomes even more likely, and nothing in plain sampling penalizes
repetition. Mitigations are usually 'no repeat' constraints — block the last `k`
tokens, or penalize the logits of previously generated tokens (repetition penalty) —
or sampling tweaks like top-p and temperature that widen the distribution.
Inspectability matters too: my walkthrough prints the token ids and decoded sequence
so the repetition is visible in the artifact, which is the same reason the lab's agent
trace pattern exists — you can't fix what you can't observe. A production system
would log the full sequence and run repetition metrics per stream."

**Interviewer**: "How do you go from this toy to a real GPT serving stack?"

**Candidate**: "Four upgrades. First, real weights: replace the hand-built `weights` matrix
and hash embeddings with trained parameters, bf16, and multi-head blocks stacked
`numLayers` deep. Second, the tokenizer: real BPE with byte-level merges, trained on
the corpus, with a vocab of tens of thousands — the lab's split-on-space tokenizer
cannot handle punctuation, unicode, or subwords. Third, inference engineering: KV
cache with paged allocation, fused attention kernels, tensor parallelism across GPUs,
and continuous batching (lab 12's `DynamicBatcher` is the same idea). Fourth, serving
controls: temperature/top-p as client parameters, `<eos>` termination, max tokens,
and observability so you can see generation quality and latency per request. The lab
is the skeleton — every production piece hangs off the same joints."
