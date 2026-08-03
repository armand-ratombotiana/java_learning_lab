# Lab 04: Mock Interview — RAG System Design

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: document chunking, embedding-based retrieval, vector store, augmentation, RAG evaluation, lost-in-the-middle

---

**Interviewer**: "Walk me through the components of the lab's RAG pipeline, end to end."

**Candidate**: "Four stages, and the lab maps them one-to-one onto classes. Ingestion:
`Chunker.fixedSize(text, chunkSize, overlap)` cuts the document into overlapping
chunks. Indexing: `VectorStore.add(chunk)` embeds each chunk — the lab's `embed`
produces a hash-seeded, unit-normalized vector — and stores it. Retrieval:
`VectorStore.search(query, topK)` embeds the query, computes `cosineSimilarity`
against every stored chunk, sorts descending, and returns the top K. Augmentation:
`RAGPipeline.query` concatenates the retrieved chunks into a context block, injects
the question, and emits the prompt for the generator. The pipeline is deliberately
separable — you can swap the embedding model, the store, or the prompt without
touching the other stages, which is exactly how production RAG is architected."

**Interviewer**: "The demo chunks a document into 6 chunks at size 60 with overlap 10. How
does the overlap affect retrieval, and how do you pick the parameters?"

**Candidate**: "Overlap exists because fixed-size cutting lands mid-sentence — the demo's
chunk [0] ends with 'us' and chunk [1] starts with ' et al. uses self-attention', so
without overlap the sentence is severed between chunks. `start += chunkSize - overlap`
walks the window forward by 50 while keeping 10 characters of boundary context, so
information near a boundary exists in two chunks and retrieval can still find it.
Parameter choice is an empirical trade: larger chunks give more context per retrieval
but dilute relevance; smaller chunks are more precise but lose narrative context. The
INTERVIEW guide suggests 256-1024 tokens with 10-20% overlap, tuned against retrieval
recall on a labeled set."

**Interviewer**: "The lab's `embed` uses `text.hashCode()` to seed a Gaussian vector. How
realistic is that, and what breaks in production?"

**Candidate**: "It's a stand-in for a real embedding model — it exercises the pipeline
mechanics, not semantics. A hash-seeded vector is a random projection: chunks share
similarity by chance, not by meaning. In production you'd use a trained encoder whose
vectors are trained so paraphrases are close and irrelevant text is far. The interface
is identical though — `embed(text, dim)` returning a normalized vector — and that's
the real lesson: the pipeline is agnostic to embedding quality. My walkthrough swaps
in a deterministic lexical embedding — term-frequency vectors over the corpus vocab —
and retrieval becomes meaningful: all three gold queries hit their target chunk at
3/3, which random hash vectors cannot produce."

**Interviewer**: "Why does `VectorStore.search` use cosine similarity, and what would you
use instead at real scale?"

**Candidate**: "Cosine measures the angle between vectors, which is natural for
unit-normalized embeddings — magnitude is ignored, so a verbose chunk isn't favored.
The lab's `cosineSimilarity` computes `dot / (sqrt(normA) * sqrt(normB))`, and since
`embed` normalizes, the denominators are 1. At real scale a linear scan is too slow —
that's where ANN indexes like HNSW or IVF-PQ come in, trading a little recall for
logarithmic search. The `search` method's contract — return top K by similarity — is
identical, which is why the lab's store is a drop-in stand-in for a real vector
database."

**Interviewer**: "The demo's RAG output appends 'Answer based on the context above.' after
the chunks. What's missing for a production prompt?"

**Candidate**: "`RAGPipeline.query` is the minimal skeleton: context, question, grounding
instruction. Production adds: a system message — 'if the context doesn't contain the
answer, say so'; explicit anti-hallucination instructions; citation requirements —
'answer with [chunk ids] in brackets'; a refusal path for out-of-scope questions; and
source metadata in the context rather than bare text, so the generator can cite and
the user can verify. The structure is exactly right; everything else is hardening."

**Interviewer**: "What does 'lost in the middle' mean, and how does your walkthrough
address it?"

**Candidate**: "LLMs attend unevenly to long contexts: information in the middle of a
long prompt is used less reliably than at the start and end. The mitigation is to
engineer the *ordering* of retrieved chunks, not just the selection. My walkthrough's
`rerankForContext` takes the ranked list and places the best chunk first and the
second-best last, so the strongest evidence sits at the high-attention positions. The
output shows original order `[7], [9], [0]` becoming context order `[7], [0], [9]` —
top two chunks moved to the edges. It costs nothing in retrieval quality and
measurably improves answer faithfulness."

**Interviewer**: "How do you evaluate whether a RAG system is working — what metrics?"

**Candidate**: "Three layers. Retrieval quality: recall@k — does the chunk containing the
answer appear in the top k — which is exactly what my walkthrough's gold-query loop
measures (hit rate 3/3). Answer quality: faithfulness — is every claim supported by
the retrieved context, the same idea as lab 09's `FactualConsistency.consistencyScore`
— plus relevance and absence of hallucination. Cost and latency: tokens per query,
cache hit rate, retrieval p95 — RAG only wins if it's cheaper and faster than stuffing
everything into context. The demo prints hit rate explicitly so the number is
inspectable."

**Interviewer**: "The chunking demo uses character counts, not token counts. Why does that
matter in production?"

**Candidate**: "Characters are a poor proxy for tokens — 'the' is 3 characters and 1
token, but 'internationalization' is 20 characters and roughly 6 tokens, so a 512-token
budget is not a 2,048-character budget. The lab's `fixedSize` operates on
`text.substring(start, end)` — character-based by necessity in a demo. Production
chunkers count actual tokens via the tokenizer (lab 02's `BPETokenizer` is the toy
version), respect sentence and paragraph boundaries, and optionally split on semantic
markers — headings, code blocks, function signatures. The overlap logic transfers
directly; the unit of measurement is what changes."

**Interviewer**: "When would you choose RAG over fine-tuning for a knowledge task?"

**Candidate**: "RAG wins when the knowledge changes — support docs, product policies,
per-tenant data — because updates are an indexing job, not a training run; when
answers must be verifiable and citeable; and when you can't afford a fine-tune per
domain. Fine-tuning wins when the *behavior* must change — tone, format, tool use —
or when per-query latency and cost must drop because retrieval adds a hop. They're
complementary: RAG supplies facts, fine-tuning supplies style and skill, and the
frontier pattern is fine-tuning the retriever to the domain and the generator on top
of retrieved context."

**Interviewer**: "What are the top failure modes you'd look for when on-call for a RAG
service?"

**Candidate**: "Retrieval-silent failures first: the retriever returning stale chunks
after a document update, embeddings drifting after a model upgrade so similarity
scores shift, or a query falling into a no-hit tail where the model improvises.
Second, chunk-quality failures: boundaries severing facts so no single chunk contains
the answer — exactly what overlap mitigates in the lab. Third, latency and cost
regressions: retrieval p95 climbing as the corpus grows, or the context budget
growing until the generator degrades. The monitoring pattern is the same as any
service — lab 14's metrics — but the unique RAG signal is faithfulness: sample
answers and check claim-by-claim against the retrieved context."

**Interviewer**: "If you were shipping this lab's pipeline to production at Stripe, what
would you change first?"

**Candidate**: "The embedding model — the hash-based `embed` must become a real trained
encoder with a stable API version, because embedding drift silently breaks retrieval.
Then the store: in-memory list scans to a persisted ANN index with replica sharding.
Then the chunker: token-aware, boundary-respecting splitting with a recall eval gate
in CI. Then the augmentation layer: source metadata, citations, and the
lost-in-the-middle ordering. And finally evaluation as a service: a labeled gold set,
recall and faithfulness dashboards, and a canary on any embedding or chunker change.
The lab's architecture survives all of it untouched — that's the point of separating
the stages."
