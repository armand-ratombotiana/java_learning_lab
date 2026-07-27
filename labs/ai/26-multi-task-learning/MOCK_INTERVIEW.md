# Mock Interview: Multi-Task Learning

**Topic:** Design a multi-task learning system for vision + language

## Core Questions

### Q1: What is multi-task learning and why use it?

**Answer:**
Multi-task learning (MTL) trains a single model on multiple related tasks simultaneously.

**Benefits:**
- **Improved generalization:** Shared representations benefit from inductive bias of related tasks
- **Data efficiency:** Leverages data from all tasks
- **Reduced parameters:** One model instead of many
- **Inference efficiency:** Single forward pass produces all task outputs
- **Regularization:** Learning multiple tasks prevents overfitting to any single task

### Q2: Design an MTL system for vision + language.

**Answer:**
**Architecture (Shared Encoder + Task-Specific Heads):**

```
Input Image → ViT Encoder (shared) → [CLS] token → Task Heads
Input Text → Text Encoder (shared) → [CLS] token → Task Heads

Task Heads:
  1. Image Classification  → Linear(num_classes)
  2. Captioning            → Transformer Decoder
  3. VQA                   → Cross-Attention → Linear(answer_vocab)
  4. OCR Detection         → Feature Pyramid → Box Regression + Class
  5. Visual Grounding      → Multi-modal Fusion → Bounding Box
```

**Training:**
- Batch contains examples from all tasks
- Each task has its own loss function
- Total loss = $\sum w_t \mathcal{L}_t$

### Q3: How do you handle conflicting gradients?

**Answer:**
**Challenges:** Tasks may have conflicting gradient directions — optimizing for one hurts another.

**Solutions:**

1. **Gradient surgery (PCGrad):** Project conflicting gradient components onto normal plane
   ```
   if g_i · g_j < 0:  # conflicting
       g_i = g_i - (g_i · g_j / ||g_j||²) * g_j
   ```

2. **Uncertainty weighting:** $L = \sum \frac{1}{2\sigma_t^2} L_t + \log \sigma_t$ — learns task-specific uncertainty

3. **Dynamic weight adjustment:** Adjust $w_t$ based on gradient norms or loss ratios

4. **MGDA:** Find Pareto-stationary point via multi-gradient descent algorithm

5. **Task grouping:** Train only compatible tasks together (measure gradient similarity)

### Q4: How do you prevent negative transfer?

**Answer:**
**Negative transfer** occurs when sharing hurts individual task performance.

**Mitigation strategies:**

- **Soft parameter sharing:** Each task has its own encoder, with cross-task regularization (e.g., attention, AdaIN)
- **Task-specific modules:** Adapters (bottleneck layers per task), Sparse MoE with task routing
- **Progressive growth:** Start with single task, gradually add tasks, freeze earlier layers
- **Gating mechanisms:** Learn which features to share per task via soft gates
- **Distillation-based:** Train separate models, then distill into shared model

### Q5: What are common MTL architectures?

| Architecture | Sharing | Pros | Cons |
|-------------|---------|------|------|
| **Hard sharing** | Shared encoder + task heads | Simple, efficient | Prone to negative transfer |
| **Cross-stitch** | Learnable linear combination of task features | Flexible | More parameters |
| **NDDR** | Task-specific conv layers + cross-talk connections | Good for vision | Complex |
| **Routing networks** | MoE: samples routed to different experts | Very flexible | Training instability |
| **Adapter-based** | Frozen shared backbone + per-task adapters | Parameter efficient | Limited capacity |

### Q6: Design for vision + language specifically.

**Answer:**
```
Input: Image + Query Text → ViT + BERT → Unified Encoder (cross-attention)

Heads:
  ├── Captioning (autoregressive decoder)
  ├── VQA (classifier over answer options)
  ├── Visual entailment (3-way classifier)
  ├── Referring expression (bounding box regression)
  └── OCR (token classification per patch)

Loss: ℒ = ℒ_caption + ℒ_vqa + ℒ_entail + ℒ_bbox + ℒ_ocr

Training strategy:
  1. Pretrain on image-text pairs (contrastive + MLM)
  2. Multi-task finetuning on specific tasks
  3. Task sampling: proportional to dataset size + inverse training speed
```

## Advanced

- **Pareto optimality in MTL:** Find set of weights where no task can improve without harming another
- **Task affinity:** Measure via $affinity_{i,j} = \frac{L_i(\text{shared}) - L_i(\text{single})}{L_i(\text{single})}$ — negative = helpful sharing
- **Frozen vs. trainable encoders:** For large models, freeze ViT/BERT and train small adapters per task
- **Gradient normalization:** Scale task gradients to similar magnitude to prevent dominant tasks
