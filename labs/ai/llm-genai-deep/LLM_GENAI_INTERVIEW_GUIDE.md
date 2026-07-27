# LLM / GenAI Deep Interview Guide

> Sub-academy level guide for the 10 llm-genai-deep micro-labs.
> Each section covers specific interview questions, company context, and ecosystem connections.

---

## Table of Contents

1. [01 — Embeddings & Semantic Similarity](#01--embeddings--semantic-similarity)
2. [02 — HNSW Indexing](#02--hnsw-indexing)
3. [03 — RAG Pipeline](#03--rag-pipeline)
4. [04 — RAG Evaluation](#04--rag-evaluation)
5. [05 — Prompt Engineering](#05--prompt-engineering)
6. [06 — LLM Agents](#06--llm-agents)
7. [07 — Fine-tuning](#07--fine-tuning)
8. [08 — RLHF](#08--rlhf)
9. [09 — Hallucination Mitigation](#09--hallucination-mitigation)
10. [10 — AI Safety](#10--ai-safety)

---

## 01 — Embeddings & Semantic Similarity

### Q1: How do text embeddings work, and how do you choose between OpenAI's `text-embedding-3-small` and `text-embedding-3-large`?

**Answer:**
Text embeddings convert variable-length text into fixed-dimensional vectors such that similar texts have similar vectors (measured by cosine similarity). Both models use a Transformer-based encoder with a contrastive training objective.

- `text-embedding-3-small` (512 dimensions default, up to 1536): Faster, cheaper ($0.02/1M tokens), 94% of large's quality.
- `text-embedding-3-large` (1024/3072 dimensions): Higher quality for fine-grained similarity, 95-99% recall on MTEB benchmarks.

**Choice rule:** Start with small for cost-sensitive applications. Use large when you need maximum retrieval precision (e.g., legal document retrieval, medical QA). Large dimensions are better for tasks requiring distinguishing very similar items.

**Company context:** OpenAI pioneered API-based embeddings — they're the default choice for production RAG systems at non-ML-first companies.

### Q2: Explain semantic similarity search at scale. How would you build a system to find similar documents among 10 million entries?

**Answer:**
1. **Offline indexing:** Compute embeddings for all 10M documents using a sentence-transformer model.
2. **ANN index:** Build an HNSW index (for accuracy, <5ms latency) or IVF+PQ (for memory efficiency).
3. **Online search:** Embed the query → search ANN index → return top-k nearest neighbors.
4. **Fallback:** Use a cross-encoder re-ranker on the top-100 retrieved documents for precision.

**Scale considerations:**
- 10M × 768-dim × 4 bytes = 30.7 GB RAM. Use product quantization to compress to 4-5 GB with IVF+PQ.
- Distribute across shards if >100M documents.

**Code example:**
```python
from sentence_transformers import SentenceTransformer
import faiss

model = SentenceTransformer('all-MiniLM-L6-v2')
embeddings = model.encode(documents)  # shape: (10M, 384)

dim = embeddings.shape[1]
quantizer = faiss.IndexFlatIP(dim)
index = faiss.IndexIVFFlat(quantizer, dim, 100)  # 100 centroids
index.train(embeddings)
index.add(embeddings)

query_emb = model.encode(["search query"])
D, I = index.search(query_emb, k=10)
```

**Ecosystem connection:** Embeddings are the foundation of all retrieval systems — RAG, semantic search, recommendation, clustering. Quality cascades: better embeddings → better recall → better RAG answers.

### Q3: How would you fine-tune an embedding model for a specific domain (e.g., legal documents)?

**Answer:**
Fine-tune using contrastive learning with (query, positive, negative) triplets.

```python
from sentence_transformers import SentenceTransformer, losses, InputExample
from torch.utils.data import DataLoader

model = SentenceTransformer('BAAI/bge-large-en-v1.5')

train_examples = [
    InputExample(texts=["contract breach clause", "Section 3.2: Breach of Contract", "nice weather today"]),
    InputExample(texts=["patent claim 1", "Claim 1: A method for...", "what's for lunch"]),
]
train_dataloader = DataLoader(train_examples, shuffle=True, batch_size=16)
train_loss = losses.TripletLoss(model)

model.fit(train_objectives=[(train_dataloader, train_loss)], epochs=3)
```

**Key techniques:**
- **Hard negative mining:** Include passages that are semantically similar but irrelevant (same topic, different answer).
- **In-batch negatives:** Use other samples in the batch as negatives (efficient, effective).
- **Domain corpus:** Fine-tune on 50K-100K domain-specific pairs.

**Company context:** This is what Cohere and Voyage AI sell as custom embedding models. Google uses this approach for enterprise document search.

---

## 02 — HNSW Indexing

### Q1: Explain the HNSW algorithm. How does it achieve logarithmic search complexity?

**Answer:**
HNSW (Hierarchical Navigable Small World) is a multi-layer graph index for approximate nearest neighbor search.

**Structure:**
- **Layer 0 (bottom):** Full dataset (all points), densely connected.
- **Layer 1:** Subset of points (sampled with probability p), coarser connections.
- **Layer 2:** Even smaller subset, even coarser.
- Higher layers are sparse "express lanes" to roughly locate the neighborhood.

**Search process:**
1. Start at the topmost layer's entry point.
2. Greedily traverse to the nearest neighbor in this layer.
3. Descend to the next layer (switch to the same point in the finer layer).
4. Repeat until reaching layer 0, then refine.

**Complexity:** O(log n) search, intuitively from the skip-list-like multi-layer structure.

**Company context:** HNSW powers most vector databases: Pinecone, Qdrant, Weaviate, Milvus, FAISS. It's the industry standard for high-recall ANN search.

### Q2: Compare HNSW, IVF+PQ, and Disk-ANN for a 100M vector dataset. Which would you choose?

**Answer:**

| Aspect | HNSW | IVF+PQ | Disk-ANN |
|--------|------|--------|----------|
| Memory | ~32 GB (raw) | ~2-4 GB (compressed) | ~0.5 GB index + storage |
| Recall@10 | ~99% | ~90-95% | ~97% |
| Latency | ~5ms | ~20ms | ~50ms |
| Build time | ~2 hours | ~30 min | ~4 hours (on SSD) |

**Decision:**
- **HNSW:** Use when you need maximum recall and have enough RAM. Best for production (speed + quality).
- **IVF+PQ:** Use when memory is constrained. Good for laptops, mobile, or very large datasets (>1B).
- **Disk-ANN:** Use when data exceeds RAM (1B+ vectors on a single machine). Trade-off: higher latency.

### Q3: How do you tune HNSW parameters for different latency/recall trade-offs?

**Answer:**
Key parameters:
- **ef_construction** (build time): Higher = better index quality but slower build. Range: 100-500. Default: 200.
- **ef_search** (search time): Higher = better recall but slower. Range: 50-1000. At runtime, can adjust dynamically.
- **M** (max edges per node): Higher = denser graph, better recall, more memory. Range: 8-64. Default: 16.

**Tuning strategy:**
```python
# Fast, lower recall
index = faiss.IndexHNSWFlat(dim, M=16)
index.hnsw.efConstruction = 100

# High recall, slower
index.hnsw.efConstruction = 500
index.hnsw.efSearch = 200

# Sweep ef_search to find latency/recall Pareto frontier
```

---

## 03 — RAG Pipeline

### Q1: Walk through the complete RAG pipeline from document ingestion to user query.

**Answer:**
1. **Ingestion:**
   - Load documents (PDF, HTML, Markdown, DB).
   - Chunk documents (semantic or fixed-size, 256-512 tokens, 10-20% overlap).
   - Embed each chunk using a sentence-transformer model.
   - Store embeddings in a vector database with metadata.

2. **Query:**
   - Embed the user query.
   - Retrieve top-k nearest neighbors from vector DB.
   - Optionally, apply a cross-encoder re-ranker on retrieved chunks.

3. **Augmentation:**
   - Construct a prompt with the retrieved context + user question.
   - Instruct the LLM to answer ONLY from the provided context.

4. **Generation:**
   - LLM generates the final answer.

```python
def rag_pipeline(query, vector_db, llm, embed_model):
    q_emb = embed_model.encode(query)
    chunks = vector_db.search(q_emb, k=5)
    context = "\n---\n".join(chunks)
    prompt = f"""Answer based ONLY on this context:
Context: {context}
Question: {query}
Answer:"""
    return llm.generate(prompt)
```

**Company context:** Google uses RAG for search (Bard/Gemini with Search Grounding). OpenAI uses it for GPT-4 with Bing. Every company building customer support bots uses RAG.

### Q2: How do you handle cases where the LLM ignores the retrieved context and hallucinates?

**Answer:**
Multi-layered approach:
1. **Prompt engineering:** Strong instruction: "Answer ONLY from context. If the context doesn't contain the answer, say 'I cannot answer from the given information.'"
2. **In-context examples:** Provide 1-2 examples where the model correctly cites or refuses.
3. **Fine-tuning:** Use a SFT dataset that rewards correct grounding and penalizes hallucination.
4. **Post-hoc verification:** After generation, use an NLI model to check each claim against the context.
5. **Temperature control:** Use low temperature (0.0-0.2) for factual QA to reduce creativity.

```python
# Post-hoc faithfulness check
from transformers import pipeline
nli = pipeline("text-classification", model="roberta-large-mnli")

def check_faithfulness(context, answer):
    for claim in split_sentences(answer):
        result = nli(f"{context} </s> {claim}")
        if result['label'] == 'CONTRADICTION':
            return False
    return True
```

### Q3: Design a RAG system that can handle multi-hop questions (e.g., "What is the salary of the CEO of the company that acquired OpenAI?")

**Answer:**
Multi-hop RAG with iterative retrieval:

1. Decompose the question into sub-questions using an LLM:
   - Sub-Q1: "Which company acquired OpenAI?"
   - Sub-Q2: "Who is the CEO of that company?"
   - Sub-Q3: "What is their salary?"
2. Retrieve for Sub-Q1, get answer "Microsoft."
3. Retrieve for Sub-Q2 with context from step 2 → get "Satya Nadella."
4. Retrieve for Sub-Q3 with context from step 3 → get "$XX million."

**Implementation:**
```python
def multi_hop_rag(query, db, llm):
    sub_questions = llm.decompose(query)  # ["Q1", "Q2", "Q3"]
    context = ""
    for sq in sub_questions:
        results = db.search(sq, top_k=3)
        context += "\n".join(results)
        answer = llm.answer(sq, context)
    return answer
```

---

## 04 — RAG Evaluation

### Q1: What metrics do you use to evaluate a RAG system? How do you measure them?

**Answer:**
**Retrieval metrics:**
- **Recall@K:** Fraction of relevant passages found in top-K. Primary metric (missing context causes hallucination).
- **MRR:** Inverse of the rank of first relevant passage.
- **NDCG:** Graded relevance (irrelevant, somewhat relevant, very relevant).

**Generation metrics:**
- **Correctness:** Is the answer factually accurate? (Human eval or LLM-as-judge)
- **Faithfulness:** Are all claims in the answer supported by the retrieved context? (NLI model)
- **Completeness:** Does the answer fully address the question?

**End-to-end metrics:**
- **Overall quality:** 1-5 Likert scale by human raters.
- **Grounding score:** % of generated sentences that are supported by retrieved context.

**Measurement tools:**
- RAGAS: Open-source framework for RAG evaluation (context precision, recall, faithfulness, answer relevancy).
- TruLens: Groundedness, context relevance, answer relevance.

```python
from ragas import evaluate
from ragas.metrics import faithfulness, answer_relevancy, context_recall

results = evaluate(
    dataset=test_set,
    metrics=[faithfulness, answer_relevancy, context_recall]
)
```

**Company context:** Google, Microsoft, and Cohere all have internal RAG evaluation frameworks. This is a critical skill for any RAG engineer role.

### Q2: How do you create a high-quality evaluation dataset for a RAG system?

**Answer:**
1. **Manual annotation:** Have domain experts create 200-500 (question, answer, relevant_passages) tuples.
2. **LLM-synthetic:** Use GPT-4/Claude to generate questions from document chunks, then validate manually.
3. **Template-based:** Generate questions from structured data (e.g., "What is the price of {product}?").
4. **Log-based:** Use real user queries from production logs, annotate the correct answer.

**Best practices:**
- Include edge cases: questions with no answer, ambiguous questions, multi-hop questions.
- Balance difficulty: 50% easy, 30% medium, 20% hard.
- Include both retrieval and generation failures.

**Company context:** Anthropic is known for extremely rigorous evaluation datasets. Their engineering interviews often focus on how you'd evaluate quality.

### Q3: Explain the difference between faithfulness and correctness. Why does it matter?

**Answer:**
- **Faithfulness:** The answer is supported by the provided context. "The answer says XYZ and the context supports XYZ."
- **Correctness:** The answer matches the ground truth. "The answer says XYZ and the correct answer is XYZ."

**Why it matters:**
- A RAG system can be correct but unfaithful (model knew the answer from pre-training, didn't use the context). This is fragile — if the context changes, the model may still give the old answer.
- A system can be faithful but wrong (context had outdated info). The system works correctly, but the data is bad.
- High faithfulness is the core promise of RAG: you can trace every claim to a source document.

---

## 05 — Prompt Engineering

### Q1: What are the fundamental principles of effective prompt engineering?

**Answer:**
1. **Clear instruction:** Be explicit about what you want. "Summarize this article in 3 bullet points" vs "Talk about this article."
2. **Role assignment:** Set context for the model. "You are an expert data scientist..."
3. **Output format specification:** "Return JSON: {'answer': string, 'confidence': float}"
4. **Few-shot examples:** Provide 2-3 examples of desired input-output pairs.
5. **Chain-of-thought:** Encourage step-by-step reasoning for complex tasks.
6. **Grounding:** Constrain the model to provided context for factual tasks.

**Code example:**
```python
prompt = """You are a helpful legal assistant. Answer the question using ONLY the provided context.

Context:
{context}

Question: {question}

First, determine if the context contains the answer. If yes, provide the answer with a citation from the context.
If no, say "I cannot answer this from the provided context."

Respond in JSON format:
{"answerable": true/false, "answer": "...", "citation": "..."}
"""
```

**Company context:** Anthropic is famous for its prompt engineering research and guides. Their Claude models are particularly sensitive to prompt structure.

### Q2: How does chain-of-thought prompting improve reasoning? When does it fail?

**Answer:**
CoT prompting encourages the model to verbalize intermediate reasoning steps, which helps:
- Breaks down complex problems into manageable pieces.
- Allows the model to "notice" its own mistakes along the way.
- Creates a reasoning trace that can be debugged.
- Improves performance on math, logic, and multi-hop tasks (GSM8K: ~20% gain).

**Failure cases:**
- **False confidence:** CoT can produce plausible-sounding but incorrect reasoning.
- **Confirmation bias:** The model may rationalize the wrong answer convincingly.
- **Overthinking:** For simple questions, CoT can harm performance by overcomplicating.
- **Context length:** Each reasoning step consumes tokens.

**When NOT to use CoT:**
- Simple factual questions (Q: "What year was Einstein born?" A: "1879").
- Tasks requiring creativity (poetry, brainstorming).
- Speed-critical applications (CoT adds latency).

### Q3: Design a prompt engineering strategy to reduce hallucination in a QA system.

**Answer:**
Multi-pronged approach:

1. **System prompt grounding:**
```
You are a factual assistant. You will ONLY answer using the information provided in the "Context" section below. If the context does not contain enough information to answer the question, say "I cannot answer based on the available information." Do not speculate or use outside knowledge.
```

2. **Structured output constraint:** Specify exact output format to avoid fluff.

3. **Self-consistency sampling:** Generate K answers independently, select the most consistent one.

4. **Uncertainty expression:** Instruct the model to express confidence levels.

5. **Iterative refinement:**
```
Pass 1: Generate answer from context.
Pass 2: For each claim in the answer, verify it against the context. If unsupported, remove it.
Pass 3: Produce final answer.
```

---

## 06 — LLM Agents

### Q1: Explain the ReAct agent architecture. How does it differ from standard LLM prompting?

**Answer:**
ReAct interleaves reasoning (Thought) and action (Action/Observation) in a loop:
```
User: Book a flight to New York next Tuesday
Thought: I need to find available flights
Action: search_flights[destination="New York", date="next Tuesday"]
Observation: Found 3 flights, $280-450
Thought: I should present the options to the user
Action: respond[Here are the available flights...]
```

**Key differences from standard prompting:**
- **Stateful:** The agent tracks its own trajectory and adapts based on observations.
- **Tool-use:** Can interact with external systems (databases, APIs, files).
- **Self-correction:** If an action fails, the agent can try a different approach.
- **Multi-step:** Works on tasks that require multiple interdependent steps.

**Company context:** Microsoft promotes their AutoGen framework for multi-agent systems. OpenAI popularized the function calling API that powers many agent frameworks.

### Q2: How do you handle tool call failures in an LLM agent?

**Answer:**
1. **Retry with backoff:** If a tool call fails (timeout, API error), retry up to 3 times with exponential backoff (1s, 4s, 9s).
2. **Alternative tool selection:** If the primary tool fails, provide the agent with a fallback (e.g., if database_search fails, try web_search).
3. **Error observation:** Return the error as a text observation, letting the agent decide what to try next.
4. **Timeout guard:** Set a maximum wall-clock time per task (e.g., 30 seconds). If exceeded, return partial results.
5. **Human escalation:** For unrecoverable errors or safety-critical decisions, escalate to a human.

```python
def execute_tool(name, args, max_retries=3):
    for attempt in range(max_retries):
        try:
            return call_tool(name, args)
        except Exception as e:
            if attempt == max_retries - 1:
                return f"Error after {max_retries} attempts: {e}"
            time.sleep(2 ** attempt)  # backoff
    return prompt_agent_for_escalation()
```

### Q3: Design a multi-agent system for automated code review.

**Answer:**
Agent roles:
1. **Orchestrator:** Receives PR diff, assigns to sub-agents, compiles final review.
2. **Code Reviewer:** Analyzes code quality, logic errors, style issues.
3. **Test Reviewer:** Checks if tests cover the change and pass.
4. **Documentation Reviewer:** Verifies code comments and docstrings.
5. **Security Reviewer:** Checks for vulnerabilities, hardcoded secrets.

```python
async def code_review_system(pull_request_diff):
    orchestrator = Agent("Orchestrator")

    # Parallel agent calls
    reviews = await asyncio.gather(
        code_reviewer.review(pull_request_diff),
        test_reviewer.review(pull_request_diff),
        doc_reviewer.review(pull_request_diff),
        security_reviewer.review(pull_request_diff),
    )

    final_review = orchestrator.compile(reviews)
    return final_review
```

---

## 07 — Fine-tuning

### Q1: When should you fine-tune vs use RAG vs prompt engineering? Walk through your decision process.

**Answer:**
| Scenario | Best approach | Why |
|----------|---------------|-----|
| Need up-to-date info | RAG | Changes dynamically, no retraining |
| Need to learn new format/style | Fine-tuning | Deep pattern learning |
| Simple task, quick prototype | Prompt engineering | No infrastructure needed |
| Need citation/source traceability | RAG | Retrieval provides provenance |
| Need to control output structure | Fine-tuning | Model learns the schema |
| Task is few-shot friendly | Prompt engineering | Add examples to prompt |
| Need domain expertise | Fine-tuning (or RAG + fine-tuning) | Best of both worlds |

**Hybrid approach:** Use RAG for factual retrieval + fine-tuning for format/style/task adherence.

**Company context:** OpenAI has fine-tuning API (curie, da Vinci). They recommend starting with RAG/prompting, then fine-tune only if needed.

### Q2: Walk through the full fine-tuning process using LoRA. What hyperparameters matter most?

**Answer:**
```python
from peft import LoraConfig, get_peft_model
from transformers import AutoModelForCausalLM, TrainingArguments, Trainer

# 1. Load base model
model = AutoModelForCausalLM.from_pretrained("mistralai/Mistral-7B-v0.1")

# 2. Configure LoRA
lora_config = LoraConfig(
    r=16,           # Rank — most important hyperparameter
    lora_alpha=32,  # Scaling factor (alpha / r = effective scale)
    target_modules=["q_proj", "v_proj", "k_proj", "o_proj"],
    lora_dropout=0.05,
    bias="none",
)

# 3. Apply LoRA
model = get_peft_model(model, lora_config)

# 4. Train
training_args = TrainingArguments(
    output_dir="./lora-finetuned",
    per_device_train_batch_size=4,
    gradient_accumulation_steps=8,  # Effective batch = 32
    learning_rate=2e-4,            # Higher than full fine-tuning
    num_train_epochs=3,
    fp16=True,
)
trainer = Trainer(model=model, args=training_args, train_dataset=dataset)
trainer.train()
```

**Key hyperparameters:**
- **r (rank):** Higher = more capacity, more memory. r=16 for most tasks, r=64 for domain shift.
- **lora_alpha:** Controls LoRA update magnitude. Default = 16-32. Too high: training unstable. Too low: adapter too weak.
- **learning_rate:** 1e-4 to 5e-4 (higher than full FT's 1e-5 to 5e-5).
- **target_modules:** Which weight matrices to adapt. Q,V is minimal; Q,K,V,O is better.
- **lora_dropout:** 0.0-0.1. Higher prevents overfitting on small datasets.

### Q3: How do you detect and prevent overfitting during fine-tuning?

**Answer:**
**Detection:**
- Monitor eval loss divergence (train loss goes down, eval loss goes up).
- Compare rouge/f1 on held-out validation set per epoch.
- Check for memorization: run the model on training samples, it should generate near-perfect training outputs (not signal of good fine-tuning).

**Prevention:**
1. **Early stopping:** Stop when eval loss stops improving (patience=2-3 epochs).
2. **LoRA rank tuning:** Use lower rank (r=8 instead of 64) for small datasets.
3. **Learning rate decay:** Use cosine schedule to reduce LR in later epochs.
4. **Data augmentation:** Add synthetic variations of training data.
5. **Weight decay:** 0.01-0.1 to penalize large weights.
6. **Mix with pre-training data:** Include 10-20% general-domain data to retain capabilities.

---

## 08 — RLHF

### Q1: Explain the RLHF pipeline end-to-end. What's the role of each component?

**Answer:**
1. **SFT (Supervised Fine-Tuning):** Train model on high-quality human demonstrations. Establishes base instruction-following behavior.
2. **Reward Model Training:** Collect human preferences (A vs B responses), train a model to predict which response humans prefer. Loss: `L = -log(sigma(r_a - r_b))`.
3. **PPO Training:** Use the reward model to score LLM outputs. Update the LLM to maximize reward, with a KL penalty to stay close to SFT model.

**Components in detail:**
- **Policy (LLM):** The model being trained.
- **Reward Model (RM):** A frozen classifier that scores response quality.
- **Reference Model:** Frozen copy of SFT model; used for KL divergence calculation.
- **PPO Objective:**
  ```
  L = E[min(pi_theta/pi_old * A, clip(pi_theta/pi_old, 1-eps, 1+eps) * A)]
  ```
  Where A = reward - baseline.

**Company context:** Anthropic heavily uses RLHF for Claude's safety and helpfulness. They've published extensively on their approach, including Constitutional AI. This lab is directly modeled on Anthropic's techniques.

### Q2: Compare RLHF and DPO. Why might DPO be preferred in some contexts?

**Answer:**

| Aspect | RLHF | DPO |
|--------|------|-----|
| Components | 3 models (policy, RM, reference) | 2 models (policy, reference) |
| Training steps | SFT → RM → PPO (3 stages) | SFT → DPO (2 stages) |
| Stability | PPO is notoriously unstable | More stable (offline from start) |
| Reward signal | Explicit RM (can be inspected) | Implicit (in policy ratio) |
| Multi-reward | Easy to combine rewards | Harder to decompose |
| Compute cost | High (4 models + PPO loop) | Lower (2 models, no PPO) |

**When to choose DPO:**
- Limited compute budget (no PPO overhead).
- Single preference signal (helpfulness or safety alone).
- Need training stability (DPO rarely diverges).

**When to choose RLHF:**
- Need to balance multiple objectives (helpfulness + safety + factuality).
- Want an explicit reward model for debugging.
- Training at very large scale (70B+) where DPO is less tested.

**Code comparison:**
```python
# DPO loss (conceptual)
def dpo_loss(policy_logps, ref_logps, win_mask, lose_mask, beta=0.1):
    win_ratio = (policy_logps - ref_logps)[win_mask]
    lose_ratio = (policy_logps - ref_logps)[lose_mask]
    loss = -log(sigmoid(beta * (win_ratio - lose_ratio)))
    return loss
```

### Q3: Explain the KL divergence penalty in RLHF. Why is it necessary?

**Answer:**
The KL penalty constrains the policy (trained LLM) from diverging too far from the SFT model.

```
reward = RM_reward - beta * KL(pi_theta || pi_sft)
```

**Why it's necessary:**
1. **Reward hacking:** Without KL penalty, the policy would learn to generate text that maximizes the reward model but is nonsensical or unhelpful.
2. **Mode collapse:** The policy could exploit reward model weaknesses, generating very specific patterns.
3. **Language quality preservation:** The SFT model produces natural language. Without KL constraint, the policy might explore unnatural outputs.

**Beta tuning:**
- High beta (0.1): Strong constraint, very small deviation from SFT, safer but less improvement.
- Low beta (0.01): Weak constraint, more aggressive optimization, higher risk of reward hacking.
- Sweet spot: Usually 0.01-0.05.

---

## 09 — Hallucination Mitigation

### Q1: What causes LLM hallucination? Categorize the types and mitigation strategies.

**Answer:**
**Types:**
1. **Intrinsic hallucination:** Contradicts the provided source (RAG context, prompt).
   - Cause: Model ignores context, falls back to parametric knowledge.
2. **Extrinsic hallucination:** Contradicts external facts.
   - Cause: Missing knowledge, training data gap, overconfidence in generation.

**Root causes:**
- **Training objective:** Models are trained to produce plausible continuations, not to verify truth.
- **Decoding strategy:** Sampling introduces randomness that can lead to unlikely but incorrect completions.
- **Knowledge representation:** Facts are distributed across parameters, not stored in a verifiable way.
- **Attention drift:** Long contexts cause the model to lose focus on relevant passages.

**Mitigation hierarchy:**
1. **Data level:** Train on factual, grounded data. Include "I don't know" examples.
2. **Prompt level:** Strong grounding instructions. Specify output format.
3. **Retrieval level:** Improve RAG quality, chunk overlap, re-ranking.
4. **Model level:** Fine-tune on factual consistency (DoLA, TruthfulQA).
5. **Decoding level:** Contrastive decoding, low temperature, top-k filtering.
6. **Post-hoc level:** NLI verification, claim extraction + verification.

**Company context:** OpenAI, Anthropic, and Google all have dedicated hallucination research teams. This is one of the most active research areas in LLM safety.

### Q2: Design a hallucination detection system for a production chatbot.

**Answer:**
**Multi-stage detection pipeline:**

1. **Claim extraction:** Parse the model's response into individual claims (using an NER/relation extraction model or regex).
2. **Context verification:** For each claim, check if it's supported by the provided context using an NLI model.
   ```python
   for claim in extract_claims(response):
       entailment = nli_model(context, claim)
       if entailment == "CONTRADICTION":
           mark_hallucination(claim)
   ```
3. **Knowledge base verification:** For claims about entities (dates, people, stats), query a structured KB.
4. **Self-consistency check:** Generate K responses, check for consistency across them.
5. **Confidence score:** Aggregate per-claim scores into an overall response hallucination score.

**Thresholds:**
- Score > 0.9: High confidence, return directly.
- Score 0.5-0.9: Return with disclaimer.
- Score < 0.5: Block response, fall back to "I'm not certain."

### Q3: How does retrieval-augmented generation (RAG) specifically reduce hallucination?

**Answer:**
RAG reduces hallucination by constraining the model's output to provided documents.

**Mechanisms:**
1. **Grounding:** The model has explicit evidence in the prompt. Strong instructions to "only use the provided context."
2. **Citations:** Generated answers can be traced back to specific passages.
3. **Fail-safe instruction:** "Say I don't know if the context doesn't contain the answer."
4. **Reduced reliance on parametric memory:** The model doesn't need to recall facts, just extract and synthesize.

**Empirical results:**
- RAG reduces hallucination rates by 40-60% across benchmarks.
- RAG + grounding instructions: 70-80% reduction.
- RAG + NLI verification: 85-90% reduction.

**Limitations:**
- RAG can't help if the retrieved context itself is wrong or missing.
- Retrieval quality is critical: if relevant passage is not retrieved, hallucination risk increases.

---

## 10 — AI Safety

### Q1: Explain the concept of "alignment" in AI safety. How do we align LLMs?

**Answer:**
Alignment means the model's behavior matches human intentions, values, and preferences.

**Alignment pipeline:**
1. **Pre-training:** Broad knowledge, no alignment. Can produce harmful, biased, or incorrect outputs.
2. **Supervised Fine-Tuning (SFT):** Train on human demonstrations of good behavior. Teaches format but may not generalize to edge cases.
3. **RLHF/DPO:** Train on human *preferences* (which of two responses is better). This improves the model's judgment, not just imitation.
4. **Constitutional AI (optional):** Model self-critiques and revises its own outputs based on written principles.

**Alignment challenges:**
- **Specification gaming:** Model finds loopholes in the training objective.
- **Reward hacking:** Maximizing reward model score in unintended ways.
- **Distribution shift:** Model behaves well on training distribution but fails in novel situations.

**Company context:** Anthropic is the leader in AI safety research. Their Claude models are trained with Constitutional AI. This lab series is directly inspired by Anthropic's safety approach.

### Q2: What is Constitutional AI? How does it reduce the need for human labeling?

**Answer:**
Constitutional AI (Bai et al., 2022) trains models to self-critique and self-revise based on a written constitution.

**Process:**
**Stage 1 (Self-Critique & Revision):**
1. Model generates a response to a harmful prompt.
2. Model critiques its own response using constitutional principles.
3. Model revises the response based on the critique.
4. Collect (prompt, revised_response) pairs.

**Stage 2 (RLHF with AI feedback):**
5. For each pair, have the model choose between revised_response and alternative → creates preference data.
6. Train reward model on this AI-generated preference data.
7. Run PPO.

**Constitution example principles:**
- "Please choose the response that is the most helpful, harmless, and honest."
- "Do not generate responses that promote violence or hate speech."
- "Refuse any requests for information that could cause harm."

**Reduction in human labeling:**
- Traditional RLHF requires 100K+ human preference labels.
- Constitutional AI reduces this to ~1K labels (to validate constitution) + AI-generated labels.
- The human role shifts from labeling to auditing/designing the constitution.

### Q3: Design a safety evaluation framework for a chatbot deployed to children.

**Answer:**
**Evaluation dimensions:**
1. **Inappropriate content:** Sexual, violent, self-harm, substance abuse.
2. **Cyberbullying:** Does the model generate or encourage bullying?
3. **Privacy:** Does the model ask for or share personal information?
4. **Manipulation:** Does the model try to influence behavior (buy products, share info)?
5. **Age-appropriateness:** Vocabulary, complexity, topic suitability.

**Testing methodology:**
1. **Red-teaming dataset:** 500+ adversarial prompts targeting each dimension (sourced from child safety experts).
2. **Baseline measurement:** Score models on each dimension.
3. **Guardrail implementation:**
   - Input filter: block harmful queries (blocklist + classifier).
   - Output filter: score each response, block if unsafe.
4. **Iterative improvement:** Red-team → fix → repeat.

**Monitoring in production:**
- Rate of blocked responses (should be 0.1-1% of total).
- Human-in-the-loop review for all blocked/filtered responses.
- Monthly adversarial testing by child safety experts.

```python
# Child safety output filter
def child_safety_filter(response):
    checks = [
        violence_classifier(response) < 0.1,
        sexual_content_classifier(response) < 0.05,
        privacy_scanner(response) == "CLEAR",
        age_appropriate_vocabulary(response) >= 0.8,
    ]
    if all(checks):
        return response
    return None  # Block
```
