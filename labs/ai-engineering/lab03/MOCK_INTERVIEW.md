# Lab 03: Mock Interview — RAG System Architecture

**Role**: AI Engineer / ML Engineer
**Duration**: 60 minutes
**Focus**: Chunking, retrieval, hybrid search, reranking, grounding, RAG evaluation

---

**Interviewer**: "Walk me through the RAG pipeline in this lab."

**Candidate**: "The pipeline has two halves. Offline: source documents are split into
`Chunk` objects by a `ChunkingStrategy`, and the chunks are embedded and indexed so
they are retrievable. Online: a user query is processed, the retrieval layer searches
the index, and `HybridSearchResult` objects combine the ranked candidates — the lab
fuses keyword and vector signals into one result list before the answer is generated.
The demo wires both halves against the same corpus so you can inspect exactly which
chunks a query retrieved and why they ranked where they did."

**Interviewer**: "How does chunking work, and why does chunk size matter?"

**Candidate**: "A `ChunkingStrategy` turns a document into overlapping segments — a
common approach is token-based or sentence-based windows with overlap so a concept
that straddles a boundary is not lost. Chunk size is the central trade: small chunks
are precise — a query matches tightly relevant text — but lose the surrounding
context the answer needs; large chunks carry context but bury the relevant passage in
noise, diluting the embedding. The lab exposes the strategy as a pluggable component
because the right answer depends on the content: code, legal text, and prose want
different windows."

**Interviewer**: "What is hybrid search, and why is keyword search still useful?"

**Candidate**: "Hybrid search runs a vector similarity search and a lexical search —
BM25-style term matching — and merges the rankings. Keyword search matters because
embeddings fail in specific ways: exact identifiers, product codes, names, and rare
terms are poorly represented by dense vectors, while BM25 nails exact-token matches.
The classic RAG failure is a query about a precise term like an error code or a part
number getting fuzzy neighbors instead of the exact document. The lab's
`HybridSearchResult` is the fusion of both signals — the ranking reflects that a
strong lexical hit and a strong vector hit are different kinds of evidence."

**Interviewer**: "How do you merge a vector ranking and a lexical ranking fairly?"

**Candidate**: "The naive merge — sum or average the raw scores — is wrong because the
two score scales are incomparable: a cosine similarity and a BM25 score live on
different ranges and distributions. The standard fix is rank fusion: convert each
result to a rank per system and combine the ranks, so a result that is third in both
systems beats one that is first in vector and fiftieth in lexical. The lab models this
as a fused result type because the merge policy is a first-class decision, not an
implementation detail — and it should be evaluated against the exact-retrieval
baseline just like any other component."

**Interviewer**: "How does retrieval failure surface in the final answer?"

**Candidate**: "Retrieval failure is the main source of RAG hallucinations: if the
relevant text is not in the context, the model cannot be grounded — it will either
answer from parametric memory or refuse, and you cannot tell which from the answer
alone. That is why the lab separates retrieval from generation: you inspect
`HybridSearchResult` — which chunks came back, their scores, their sources — before
any answer exists. If the retrieved set is empty or wrong, the fix belongs in
chunking or the index, not in prompt engineering."

**Interviewer**: "How would you handle a query that returns no relevant chunks?"

**Candidate**: "Three-layer response: detect the miss, degrade gracefully, and fix the
cause. Detect — score the top result and set a retrieval threshold, because an
arbitrary 'best' chunk with a terrible score is a miss dressed up as a hit. Degrade —
respond 'I could not find that in the source material' rather than answering from
memory, which is the honest and safe behavior. Fix — log the query, inspect why
chunking or the index missed it, and use it as an evaluation case. The lab encodes the
first two in the result type and the demo's refusal path; the third is a process
discipline."

**Interviewer**: "How do you evaluate a RAG system?"

**Candidate**: "You evaluate retrieval and generation separately. Retrieval: build a
query-to-relevant-chunk set and measure recall@k — did the right chunk make the
context? Generation: check groundedness — is the answer supported by the retrieved
chunks — and correctness against reference answers. The lab supports the retrieval
side directly: deterministic queries against a known corpus make recall measurable,
and the hybrid fusion should be compared against vector-only and keyword-only baselines
to prove it helps. Groundedness evaluation needs either a judge model or
entailment-style checks that the answer's claims appear in the context."

**Interviewer**: "What is the role of reranking in a production RAG stack?"

**Candidate**: "First-stage retrieval is designed for recall — it pulls a wide candidate
set cheaply. A reranker sits after it and scores candidates with a cross-encoder
that sees the query and the chunk together, which is far more accurate than the
bichannel similarity of first-stage retrieval but too expensive to run over the whole
corpus. The lab's hybrid fusion plays the same role conceptually: cheap signals first,
a more careful ranking decision on top. In production the lesson is to keep stages
separate — a reranker that only reorders the top-k cannot rescue a first stage that
already lost the right document."

**Interviewer**: "How do you keep the index fresh as documents change?"

**Candidate**: "Updates flow through the same pipeline as initial load: changed
documents are re-chunked, re-embedded, and replaced in the index; deletions remove the
chunks from their source; and chunk identity is tied to the source document and
version, so a re-ingest never leaves orphaned chunks behind. The failure mode to avoid
is timestampless ingests: without a version or source lineage, you cannot tell whether
the index is serving a stale document, and stale retrieval quietly corrupts every
answer that touches it. The lab's chunk model carries source metadata precisely so
this bookkeeping is explicit."

**Interviewer**: "How do you handle multi-turn or rephrased queries?"

**Candidate**: "The hard part is that retrieval matches the query as written, and a
follow-up question often drops the context: 'what about the pricing?' retrieves
nothing useful unless the conversation's subject — the earlier document or entity —
is carried into the search query. Production systems rewrite the query from
conversation context before retrieval, and that rewrite is itself an LLM step that
needs the same testing as everything else. The lab keeps retrieval pure — query in,
chunks out — which makes the baseline honest, and the extension is explicit: a
rewrite stage in front of the index, measured by whether it improves recall, not
by whether it sounds better."

**Interviewer**: "How would you serve RAG at scale with low latency?"

**Candidate**: "Cache aggressively and partition deliberately. Query-side caching handles
repeat questions without touching the index. For new queries, the hot path is:
embed the query, run hybrid search against a sharded index — fan out to relevant
shards in parallel — fuse ranks, and hand a compact context to the generator. The
latency budget should be dominated by generation, not retrieval: if retrieval is
expensive, the index or the candidate-set size is wrong. And you measure the stages
individually, because a slow pipeline with fast generation is still a slow pipeline."

**Interviewer**: "When would you NOT use RAG?"

**Candidate**: "When the knowledge is small, static, and fully encodable — a stable
set of a few hundred facts can live in a structured store or fine-tuned knowledge
instead of a retrieval stack, which saves an entire serving tier. When the knowledge
changes constantly, RAG is ideal; when it changes rarely, retrieval adds operational
complexity without value. And when the answer must be derived from reasoning rather
than retrieved text, RAG can still help as grounding, but the retrieval layer is not
the solution to the reasoning problem. RAG is an architecture, not a default."

**Interviewer**: "What is the most common failure you have seen in RAG systems?"

**Candidate**: "The silent-relevance failure: everything is running, retrieval returns
results, the generator is fluent — and the answers are wrong because the retrieved
chunks are plausible but not actually relevant. The causes accumulate: chunk sizes
chosen once and never revisited, hybrid fusion weights tuned on anecdotes, no
threshold on retrieval scores, no baseline comparison. The lab's design counters
this by making retrieval inspectable — you can see the chunks and scores — and by
keeping the exact and hybrid paths comparable so quality drift is measurable. A RAG
system you cannot inspect is a RAG system you cannot debug."
