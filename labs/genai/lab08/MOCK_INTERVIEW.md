# Lab 08: Mock Interview — Multimodal Models

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: dual encoders, patch embedding, cross-modal attention, contrastive loss (InfoNCE), zero-shot retrieval

---

**Interviewer**: "Walk me through how CLIP-style models are built, using the lab's
architecture."

**Candidate**: "Two encoders, one shared embedding space, trained contrastively. The lab's
`PatchEmbedding` turns an image into a sequence of patch vectors — the `embed` method
splits a `height x width x RGB` array into `patchSize` squares, flattens each into
`patchSize² * 3` values, and projects with a learned `projection` matrix to `dModel`
dimensions. The `TextEncoder` does the text side: `encode` maps a token to a
unit-normalized vector with an embed cache, and `averageEncode` combines tokens into
one caption vector. Alignment comes from `contrastiveLoss`: for a batch of matched
image-text pairs, it maximizes the similarity of the diagonal (matched) pairs and
minimizes everything off-diagonal. The result is a space where 'a cat sitting on a
mat' is near its image and far from the others — retrieval without any task labels."

**Interviewer**: "Why is the image embedding done through patches rather than treating the
whole image as one vector?"

**Candidate**: "Patches make images into sequences, which is what attention needs. A
16x16 patch of a 224x224 image gives a sequence of 196 tokens — each patch flattened
and linearly projected, exactly like token embeddings. The lab's toy uses 4x4 patches
on an 8x8 image, producing 4 patch vectors, and the demo prints 'Number of patches:
4'. This is the ViT recipe: the sequence of patches is processed by the same
transformer machinery as text tokens, which is what lets a *single* model consume both
modalities and what cross-modal attention keys off. Patch size is the resolution
trade-off — smaller patches, more tokens, more detail, quadratic attention cost."

**Interviewer**: "The lab's `TextEncoder` caches embeddings by token text and draws from a
shared RNG stream. What's the reasoning, and where does the reality diverge?"

**Candidate**: "The `embedCache.computeIfAbsent` is the right idea — the same token should
always produce the same vector, and caching avoids recomputation, mirroring how real
token embeddings are lookup tables. The seeded Gaussian stream is the toy: a real text
encoder is a transformer whose token embeddings are trained, so 'cat' near 'feline'
and far from 'car'. Note a subtlety: with a shared stream and a cache, the vector a
token receives depends on *which tokens came first* in the run — fine for a demo,
wrong for production, where embeddings must be order-independent. That's the kind of
detail worth flagging in review: determinism is not the same as consistency."

**Interviewer**: "Walk through the InfoNCE loss in `contrastiveLoss`."

**Candidate**: "For each image in the batch, it computes logits — `sim(i, j) / temperature`
against every text in the batch — then applies the standard trick: subtract the row max
before exponentiating to avoid overflow, sum the exponentials, and compute
`-(logits[i] - max) + log(sumExp)`, which is the negative log of the softmax
probability of the *matched* pair. Averaged over the batch, minimizing it pulls each
image toward its matched text and pushes it away from all others; the temperature
scales how hard: low temperature concentrates the distribution, making the loss
punish mismatches more aggressively. The lab demo prints 0.6931 for a degenerate batch
of two identical pairs — `log(2)`, exactly what you'd expect when the model can't
distinguish the pair."

**Interviewer**: "Why does the lab demo's batch — image 0 with text, then image 1 paired
with the *same* text — produce exactly `log(2)`?"

**Candidate**: "Because both image-text pairs have identical similarity patterns. With
`temperature = 0.07`, each row's logits are some value for the matched text and a
different value for the other; since both rows have the same structure, each image
assigns probability 1/2 to each text — softmax over two equal-ish logits. The loss
per row is `-log(1/2) = log(2) ≈ 0.6931`, and the demo prints exactly that. It's a
clean canary value: if your implementation ever prints something else for that
degenerate batch, your softmax or normalization is wrong. In my walkthrough, the
batch of 4 *different* pairs starts at 4.29 — random encoders, no alignment — and
drops to 0.94 after 50 simulated contrastive steps, with recall climbing 0/4 to 4/4."

**Interviewer**: "How does `crossModalAttention` differ from the self-attention in lab 01?"

**Candidate**: "Self-attention has queries, keys, and values all from the same sequence —
positions attend within their own modality. Cross-modal attention keeps the keys and
values from one modality and the queries from the other: the lab's
`crossModalAttention(textQ, imgK, imgV)` lets every text token attend over *image*
patches, so the text output at each position is a weighted blend of image information
chosen by text relevance. That's the fusion mechanism behind captioning and
VQA-style generation: the decoder queries the image, not just the text prefix.
Everything else — `dot / sqrt(dk)`, softmax, weighted sum — is identical to lab 01's
scaled dot-product attention."

**Interviewer**: "What makes zero-shot transfer work in CLIP, and how does the walkthrough
demonstrate it?"

**Candidate**: "Contrastive pretraining on massive image-text pairs teaches the encoders a
space where similarity *is* semantics: any new concept described in natural language
can be matched to an image without task labels. My walkthrough builds 4 images, runs
`PatchEmbedding.embed` to get patch representations, averages to image vectors, and
queries them with caption embeddings — the retrieval loop is the zero-shot harness:
for each caption, pick the image with max cosine. Before contrastive training the
random encoders score 0/4; after 50 InfoNCE steps recall is 4/4. The point is
mechanical: retrieval is a nearest-neighbor lookup in the aligned space, and
alignment is a training artifact, not an architectural given."

**Interviewer**: "The walkthrough trains only the text encoder. What's the actual training
setup in production?"

**Candidate**: "Both encoders are updated, and with a big batch — CLIP-style training uses
batches of 32K pairs because the negative pairs *within the batch* are the learning
signal; the larger the batch, the harder the contrastive task and the better the
embeddings. The lab's `contrastiveLoss` computes the full `n x n` similarity matrix,
which at real scale is exactly why you need the memory-efficient tricks (gather all
embeddings across GPUs, lossy communication). There's also the stability machinery the
lab skips: embedding normalization, the temperature as a *learned* parameter, and
gradient clipping. The gradient formula my walkthrough implements — `(p_ij - δ_ij)/τ`
weighted against the image embeddings — is the actual InfoNCE gradient, just applied
to one side for simplicity."

**Interviewer**: "How do you evaluate a multimodal model's alignment?"

**Candidate**: "Retrieval metrics first: recall@K for image-to-text and text-to-image —
does the matched item appear in the top K; my walkthrough's `recallAtOne` is the
`K=1` case. Then zero-shot classification accuracy — prompt 'a photo of a {class}'
and check the top label, which is the classic CLIP eval. Then image-text matching
accuracy on held-out paired data, and the loss itself as a training signal. Finally,
human evaluation for generation tasks like captioning. The lab's `contrastiveLoss` is
the training objective, and the INTERVIEW guide's Recall@K and zero-shot accuracy are
its evaluation counterpart — same embedding space, different lens."

**Interviewer**: "How would you take this lab to a production multimodal search service?"

**Candidate**: "Replace both toy encoders with trained ones and freeze their versions in
the index pipeline — encoder upgrades require re-embedding the whole catalog, so
versioning is a product decision. Move the in-memory arrays to a real vector store
with ANN search, mirroring lab 04's storage story. Serve the encoders through the same
gateway and observability as any model: batch image embedding at ingest, cache text
queries (lab 12's `SemanticCache`), and monitor recall against a labeled eval set
after every encoder change. And be honest about failure modes: the similarity
threshold for 'no match' needs calibration so irrelevant pairs don't surface as hits.
The lab's retrieval loop — embed, cosine, top-K — is the core that all of that wraps."
