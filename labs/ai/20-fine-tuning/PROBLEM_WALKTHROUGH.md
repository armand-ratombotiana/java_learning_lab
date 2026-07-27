# Fine-tuning Interview Problem Walkthroughs

> Step-by-step solutions to 3 canonical fine-tuning interview problems.
> Covers: LoRA implementation from scratch, single-GPU 70B fine-tuning, RLHF vs DPO comparison.

---

## Problem 1: Implement LoRA Fine-Tuning from Scratch

**Problem Statement:**
> Implement Low-Rank Adaptation (LoRA) from scratch using PyTorch. Your implementation should:
> 1. Define a LoRA layer that wraps any linear layer with a low-rank update
> 2. Support merging the LoRA weights back into the base weights for inference
> 3. Show how to apply LoRA to a Transformer's attention layers
> 4. Demonstrate that the LoRA update is zero at initialization

### Step 1: Define the LoRA Layer

```python
import torch
import torch.nn as nn
import math

class LoRALayer(nn.Module):
    """
    LoRA wrapper for any nn.Linear layer.
    
    W' = W + BA  where B (d_out x r), A (r x d_in), r << min(d_in, d_out)
    
    At init: B = 0, A ~ N(0, sigma^2) → BA = 0 → W' = W (no change)
    """
    def __init__(
        self,
        base_layer: nn.Linear,
        r: int = 8,
        lora_alpha: float = 16.0,
        lora_dropout: float = 0.0,
    ):
        super().__init__()
        self.base_layer = base_layer
        self.r = r
        self.lora_alpha = lora_alpha
        self.scaling = lora_alpha / r
        
        # Freeze base weights — they are NOT updated during training
        self.base_layer.weight.requires_grad = False
        if self.base_layer.bias is not None:
            self.base_layer.bias.requires_grad = False
        
        # LoRA parameters
        d_in = base_layer.in_features
        d_out = base_layer.out_features
        
        # A: random initialization (Kaiming uniform for the input projection)
        self.lora_A = nn.Parameter(torch.empty(r, d_in))
        nn.init.kaiming_uniform_(self.lora_A, a=math.sqrt(5))
        
        # B: zero initialization — ensures output = Wx at start
        self.lora_B = nn.Parameter(torch.zeros(d_out, r))
        
        # Optional dropout (applied to input before LoRA)
        self.dropout = nn.Dropout(lora_dropout) if lora_dropout > 0 else nn.Identity()
    
    def forward(self, x: torch.Tensor) -> torch.Tensor:
        """
        Forward pass: y = Wx + (alpha/r) * BAx
        
        The scaling factor controls how much the LoRA update contributes.
        """
        # Base output (frozen)
        base_output = self.base_layer(x)
        
        # LoRA update
        lora_output = (self.dropout(x) @ self.lora_A.T) @ self.lora_B.T
        
        return base_output + self.scaling * lora_output
    
    def merge_weights(self):
        """
        Merge LoRA weights into base weights for inference.
        
        After merging:
        - W_merged = W + (alpha/r) * BA
        - Inference uses base_layer only (no LoRA overhead)
        - Cannot easily unmerge — must keep original weights for training
        """
        w_merged = self.base_layer.weight.data + self.scaling * (
            self.lora_B @ self.lora_A
        ).to(self.base_layer.weight.dtype)
        
        self.base_layer.weight.data = w_merged
    
    def unmerge_weights(self, original_weight: torch.Tensor):
        """
        Restore original weights (merge is destructive).
        Store original weights before merge to enable unmerge.
        """
        self.base_layer.weight.data = original_weight
```

### Step 2: Validate Zero Initialization

```python
# Verify that LoRA update is zero at initialization
linear = nn.Linear(64, 128)
lora = LoRALayer(linear, r=8)

x = torch.randn(4, 64)

output_with_lora = lora(x)
output_without_lora = linear(x)

diff = (output_with_lora - output_without_lora).abs().max().item()
print(f"Max difference at init: {diff:.10f}")  # Should be ~0.0
```
**Expected output:** The difference is essentially 0 (floating-point epsilon), confirming that B=0 initialization preserves the pre-trained output.

### Step 3: Apply LoRA to a Transformer

```python
from transformers import AutoModelForCausalLM
from peft import get_peft_model, LoraConfig

def apply_lora_to_transformer(model_name="mistralai/Mistral-7B-v0.1", r=16):
    """
    Apply LoRA to attention layers of a Transformer model.
    
    Target modules in a standard Transformer:
    - q_proj: Query projection (W_Q)
    - k_proj: Key projection (W_K)  
    - v_proj: Value projection (W_V)
    - o_proj: Output projection (W_O)
    
    Some implementations also target:
    - gate_proj, up_proj, down_proj (FFN layers in SwiGLU)
    """
    model = AutoModelForCausalLM.from_pretrained(
        model_name,
        torch_dtype=torch.bfloat16,
        device_map="auto",
    )
    
    lora_config = LoraConfig(
        r=r,
        lora_alpha=32,
        target_modules=["q_proj", "k_proj", "v_proj", "o_proj"],
        lora_dropout=0.05,
        bias="none",
        task_type="CAUSAL_LM",
    )
    
    model = get_peft_model(model, lora_config)
    
    # Print trainable parameters
    trainable = sum(p.numel() for p in model.parameters() if p.requires_grad)
    total = sum(p.numel() for p in model.parameters())
    print(f"Trainable: {trainable:,} / {total:,} ({100 * trainable / total:.2f}%)")
    
    return model
```

### Step 4: Training Loop

```python
def train_lora(model, dataset, batch_size=4, lr=2e-4, num_epochs=3):
    """Simple training loop for LoRA fine-tuning."""
    
    optimizer = torch.optim.AdamW(
        filter(lambda p: p.requires_grad, model.parameters()),
        lr=lr,
        weight_decay=0.01,
    )
    
    model.train()
    
    for epoch in range(num_epochs):
        for batch in dataset:
            input_ids = batch["input_ids"].cuda()
            labels = batch["labels"].cuda()
            
            outputs = model(input_ids=input_ids, labels=labels)
            loss = outputs.loss
            
            loss.backward()
            torch.nn.utils.clip_grad_norm_(
                filter(lambda p: p.requires_grad, model.parameters()),
                max_norm=1.0,
            )
            optimizer.step()
            optimizer.zero_grad()
        
        print(f"Epoch {epoch+1}, Loss: {loss.item():.4f}")
    
    # Save only LoRA weights (tiny ~2-10 MB)
    model.save_pretrained("./lora_adapter")
```

### Detailed Explanation

| Concept | Why it matters |
|---------|---------------|
| B=0 init | Ensures no disruption to pre-trained output at step 0 |
| A ~ uniform | Provides symmetry breaking — both zero would make gradients zero |
| Scaling = alpha/r | Controls update magnitude independent of rank. alpha=r → scale=1 |
| r selection | Lower r = more regularization, less capacity. Higher r = more expressiveness |
| Dropout on input to LoRA | Regularization to prevent LoRA-specific overfitting |
| Merge for inference | Zero latency overhead at inference (just slightly different weights) |

---

## Problem 2: Fine-tune a 70B Model on a Single GPU

**Problem Statement:**
> You need to fine-tune a 70B-parameter LLM for a domain-specific task. You have access to a single NVIDIA A100 80GB GPU. Walk through your approach, including memory budget, model modifications, and training strategy.

### Step 1: Memory Budget

**Memory accounting for a 70B model:**

| Component | Memory (GB) | Notes |
|-----------|-------------|-------|
| Model weights (FP16) | 140 | 70B × 2 bytes per param |
| Optimizer states (FP32) | 240 | 70B × 4 bytes × 2 (moments) |
| Gradients (FP16) | 140 | 70B × 2 bytes |
| Activations | 10-40 | Batch-size dependent |
| **Total (full fine-tune)** | **~530-560 GB** | ❌ Impossible |

**Solution: QLoRA** — combine quantization + LoRA to fit in 48 GB.

| Component | Memory (GB) | How |
|-----------|-------------|-----|
| Model weights (NF4) | 35 | 70B × 0.5 bytes (4-bit NF4) |
| LoRA adapters (FP16) | ~0.5 | r=16, ~10M trainable params |
| Optimizer states (LoRA) | ~0.5 | Only LoRA params need Adam |
| Gradients (LoRA) | ~0.3 | Only LoRA params need gradients |
| Activations | ~2-5 | Gradient checkpointing (recompute) |
| **Total** | **~38-41 GB** | ✅ Fits in A100 80GB |

### Step 2: Implementation

```python
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    BitsAndBytesConfig,
    TrainingArguments,
    Trainer,
)
from peft import LoraConfig, get_peft_model, prepare_model_for_kbit_training
import torch

# Step 1: 4-bit quantization config
bnb_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_quant_type="nf4",          # NormalFloat4: optimal for normal distributions
    bnb_4bit_use_double_quant=True,      # Double quantization of scale factors
    bnb_4bit_compute_dtype=torch.bfloat16,  # Compute in BF16 (stable, good range)
)

# Step 2: Load model in 4-bit
model = AutoModelForCausalLM.from_pretrained(
    "meta-llama/Llama-2-70b-hf",
    quantization_config=bnb_config,
    device_map="auto",                   # Distribute across GPU + CPU if needed
    torch_dtype=torch.bfloat16,
    use_cache=False,                     # Disable KV cache for training
)

# Step 3: Prepare for k-bit training
model = prepare_model_for_kbit_training(
    model,
    use_gradient_checkpointing=True,     # Trade compute for memory
    gradient_checkpointing_kwargs={"use_reentrant": False},
)

# Step 4: Configure LoRA
lora_config = LoraConfig(
    r=16,                                # Rank 16 — good balance
    lora_alpha=32,
    target_modules=["q_proj", "v_proj", "k_proj", "o_proj"],
    lora_dropout=0.05,
    bias="none",
    task_type="CAUSAL_LM",
)

model = get_peft_model(model, lora_config)

# Step 5: Training arguments
training_args = TrainingArguments(
    output_dir="./qlora-70b",
    per_device_train_batch_size=1,       # Batch size 1 due to memory
    gradient_accumulation_steps=16,      # Effective batch = 16
    learning_rate=2e-4,
    fp16=False,
    bf16=True,                          # A100 supports BF16 natively
    max_steps=500,
    logging_steps=10,
    save_steps=100,
    optim="paged_adamw_8bit",           # Paged optimizer: offload to CPU
    gradient_checkpointing=True,
    gradient_checkpointing_kwargs={"use_reentrant": False},
)

# Step 6: Custom data collator for packing sequences
def data_collator(features):
    """Pack multiple short examples into a single sequence."""
    input_ids = [f["input_ids"] for f in features]
    labels = [f["labels"] for f in features]
    
    # Concatenate and pad to power of 2 for CUDA efficiency
    return {
        "input_ids": torch.nn.utils.rnn.pad_sequence(
            input_ids, batch_first=True, padding_value=tokenizer.pad_token_id
        ),
        "labels": torch.nn.utils.rnn.pad_sequence(
            labels, batch_first=True, padding_value=-100
        ),
    }

trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=dataset,
    data_collator=data_collator,
    tokenizer=tokenizer,
)

trainer.train()
```

### Step 3: Alternative — Use Multiple GPUs

If you have access to multiple GPUs (unlikely for "single GPU" problem, but good to discuss):

| GPUs | Strategy | Memory per GPU |
|------|----------|---------------|
| 2×A100 80GB | Tensor parallelism | ~40 GB each |
| 4×A100 80GB | Zero-3 + LoRA | ~20 GB each |
| 8×A100 80GB | Full fine-tune possible | ~80 GB each |

### Interview Discussion Points

**Q: Why NF4 instead of regular INT4?**
NF4 is information-theoretically optimal for normally distributed weights (which LLM weights approximately follow). It places more quantization levels near zero, preserving precision for small-magnitude weights.

**Q: What is double quantization?**
Quantize the FP32 scale factors (one per 64 weight block) to FP8. Scale factors are ~0.5 bits per parameter. Double quantization brings this to ~0.13 bits per parameter. Saves ~0.37 bits/param × 70B ≈ 3.3 GB.

**Q: Why paged optimizer?**
Optimizer states (Adam moments) are 8 bytes per parameter (FP32 momentum + FP32 variance). For the quantized model, we only need optimizer states for LoRA parameters (~10M params → 80 MB). But with gradient checkpointing, we may need to offload to CPU and page back on demand.

**Q: What gradient accumulation steps should be used?**
With batch size 1 and GPU memory full, gradient_accumulation_steps=16 gives effective batch size 16. The loss is scaled by 1/accumulation_steps before backward.

### Practical Caveats

1. **Training time:** 70B QLoRA at batch size 1 on a single A100: ~3-5 seconds per step. 500 steps ≈ 45 minutes. For real quality: 500-2000 steps typically sufficient.
2. **Evaluation:** Always evaluate on a validation set during training to detect overfitting.
3. **Merge for inference:** After training, merge LoRA weights into the base model and convert back to FP16 for inference speed.

---

## Problem 3: Compare RLHF and DPO — When Would You Choose Each?

**Problem Statement:**
> Compare Reinforcement Learning from Human Feedback (RLHF) and Direct Preference Optimization (DPO) for aligning a large language model. Explain the trade-offs, and provide a decision framework for when to choose each approach.

### The Two Approaches

**RLHF (Three-stage pipeline):**
1. **SFT:** Supervised fine-tuning on human demonstrations
2. **Reward Model (RM):** Train a model to predict human preferences
3. **PPO:** Optimize the policy against the reward model with KL constraint

**DPO (Two-stage pipeline):**
1. **SFT:** Supervised fine-tuning on human demonstrations
2. **DPO:** Directly optimize the policy on preference pairs using a closed-form solution

### Mathematical Comparison

**RLHF Objective:**
```
max_{pi_theta} E[R(x,y)] - beta * KL(pi_theta(y|x) || pi_ref(y|x))
```
Solved via PPO, which requires:
- Sampling from current policy
- Computing reward from RM
- Ratio clipping for stability
- KL penalty for constraint

**DPO Objective:**
```
L_DPO = -E[log(sigma(beta * log(pi_theta(y_w|x) / pi_ref(y_w|x))
                  - beta * log(pi_theta(y_l|x) / pi_ref(y_l|x))))]
```
Derivation: The optimal policy under KL constraint has closed form:
```
pi*(y|x) = (1/Z) * pi_ref(y|x) * exp(R(x,y)/beta)
```
Rearranging:
```
R(x,y) = beta * log(pi*(y|x) / pi_ref(y|x)) + beta * log(Z)
```
Substitute into Bradley-Terry preference model:
```
P(y_w > y_l) = sigma(R(x,y_w) - R(x,y_l))
= sigma(beta * log(pi_theta(y_w|x)/pi_ref(y_w|x)) - beta * log(pi_theta(y_l|x)/pi_ref(y_l|x)))
```
This yields the DPO loss directly — no RM, no PPO needed.

### Detailed Comparison

| Aspect | RLHF | DPO |
|--------|------|-----|
| **Training stages** | 3 (SFT → RM → PPO) | 2 (SFT → DPO) |
| **Components** | Policy, Reward Model, Reference Model | Policy, Reference Model |
| **Compute cost** | Very high: 4 models in memory + PPO loop | Lower: 2 models, single forward pass |
| **Training stability** | Unstable: reward spiking, mode collapse, KL oscillation | Stable: convex-like loss landscape |
| **Hyperparameters** | Many: PPO clip, KL beta, GAE lambda, value loss coef | Few: beta (KL strength), learning rate |
| **Reward model** | Explicit: can be analyzed, debugged, shaped | Implicit: embedded in policy ratio |
| **Multi-reward** | Easy: combine multiple RMs (weighted sum) | Hard: needs single preference signal |
| **Offline data** | Can use offline (preference data + PPO off-policy) | Naturally offline: batch update |
| **On-policy data** | Supports on-policy (sample from current policy) | Primarily offline (on-policy extension possible but complex) |
| **Scalability** | Proven at 175B+ (InstructGPT, Llama 2) | Proven at 7B-70B (Zephyr, Llama 2 Chat) |

### Decision Framework

```
                     ┌─────────────────────────────────────┐
                     │  Do you have compute for 3 stages?  │
                     └──────────────────┬──────────────────┘
                              Yes│                        │No
                           ┌─────▼──────┐          ┌──────▼──────┐
                           │ RLHF viable │          │ Use DPO     │
                           └─────┬──────┘          └──────────────┘
                    ┌──────────────┼──────────────────┐
                    │              │                  │
              ┌─────▼───┐   ┌─────▼─────┐    ┌───────▼──────┐
              │Multiple │   │Single      │    │Need explicit │
              │rewards? │   │preference? │    │reward model? │
              └────┬────┘   └─────┬─────┘    └───────┬──────┘
               Yes│        No│    │Yes               │Yes
           ┌──────▼──┐   ┌──▼────▼──┐     ┌─────────▼─────────┐
           │ Use     │   │ DPO      │     │ RLHF (explicit RM)│
           │ RLHF    │   │ (simpler)│     │                   │
           └─────────┘   └──────────┘     └───────────────────┘
```

### When to Choose RLHF

**Scenario 1: Balancing Helpfulness + Harmlessness + Honesty (Anthropic Claude)**
```
R_total = w_1 * R_helpfulness + w_2 * R_harmlessness + w_3 * R_honesty
```
Three separate reward models, each trained on different preference data. Combined via weighted sum. This is the standard approach for safety-constrained models.

**Scenario 2: Iterative refinement with red-teaming**
- Deploy model → collect red-teaming data → retrain RM → retrain policy.
- The explicit RM makes it easy to target specific failure modes: just add more data for that failure mode in the RM training set.

**Scenario 3: When you need to inspect the reward model**
- Debug reward hacking: examine RM scores for specific inputs.
- Identify distribution drift: compare RM scores on production vs training data.

### When to Choose DPO

**Scenario 1: Compute-constrained team**
No budget for training a reward model or running PPO. DPO can be done on a single GPU with consumer hardware.

```python
# Minimal DPO training loop
for batch in dataloader:
    # Forward pass on chosen and rejected responses
    chosen_logps = policy(batch["chosen"]).log_probs.sum(-1)
    rejected_logps = policy(batch["rejected"]).log_probs.sum(-1)
    ref_chosen_logps = reference(batch["chosen"]).log_probs.sum(-1).detach()
    ref_rejected_logps = reference(batch["rejected"]).log_probs.sum(-1).detach()
    
    # DPO loss
    chosen_ratio = chosen_logps - ref_chosen_logps
    rejected_ratio = rejected_logps - ref_rejected_logps
    loss = -F.logsigmoid(beta * (chosen_ratio - rejected_ratio)).mean()
    
    loss.backward()
    optimizer.step()
```

**Scenario 2: Rapid iteration on preference data**
Want to try different beta values, different preference datasets, different base models? DPO's simplicity makes this fast — no need to retrain RM each time.

**Scenario 3: Small-scale deployment (< 13B models)**
DPO is well-validated at 7B-13B scale. For Llama 3 8B, Zephyr 7B, Mistral 7B — DPO achieves comparable quality to RLHF.

### Hybrid Approaches

**DPO + RM Ensemble:**
- Train a reward model anyway (for evaluation/analysis).
- Use RM to filter preference pairs that the model is most confused about.
- Train DPO on hard, filtered pairs.

**Iterative DPO:**
- Sample from current policy, get preferences from humans, train DPO.
- Repeat: essentially on-policy DPO without PPO's complexity.
- Used in the Zephyr paper (UltraFeedback → DPO → repeat).

### Code Examples

**RLHF PPO Implementation (simplified):**
```python
def ppo_step(policy, reference, reward_model, batch, beta=0.1, clip_eps=0.2):
    # Generate responses from current policy
    responses = policy.generate(batch["prompts"])
    
    # Compute rewards
    rewards = reward_model(batch["prompts"], responses)
    
    # Compute KL penalty
    policy_logps = policy.log_prob(responses)
    ref_logps = reference.log_prob(responses).detach()
    kl = (policy_logps - ref_logps).mean(-1)
    
    # Combined reward
    rewards_with_kl = rewards - beta * kl
    
    # PPO clip objective
    ratio = (policy_logps - old_policy_logps.detach()).exp()
    adv = rewards_with_kl - rewards_with_kl.mean()
    
    pg_loss = -torch.min(
        ratio * adv,
        ratio.clamp(1 - clip_eps, 1 + clip_eps) * adv,
    ).mean()
    
    return pg_loss
```

**DPO Implementation (full):**
```python
def dpo_loss(policy_logps_chosen, policy_logps_rejected,
             ref_logps_chosen, ref_logps_rejected, beta=0.1):
    """
    policy_logps: log probabilities from current policy
    ref_logps: log probabilities from frozen reference model
    """
    # Log ratios
    log_ratio_chosen = policy_logps_chosen - ref_logps_chosen
    log_ratio_rejected = policy_logps_rejected - ref_logps_rejected
    
    # DPO loss
    logits = beta * (log_ratio_chosen - log_ratio_rejected)
    loss = -F.logsigmoid(logits).mean()
    
    # Optional: accuracy metric (how often does model prefer chosen)
    accuracy = (logits > 0).float().mean().item()
    
    return loss, accuracy
```

### Interview Answer Template

When asked "Compare RLHF and DPO," structure your answer:

1. **Define both:** RLHF is a 3-stage pipeline (SFT → RM → PPO). DPO is a 2-stage pipeline (SFT → DPO loss).

2. **Key difference:** RLHF trains an explicit reward model and uses on-policy RL (PPO). DPO reparameterizes the reward in terms of the policy ratio, enabling direct optimization.

3. **Trade-offs:**
   - RLHF: More compute, more hyperparameters, but supports multi-reward and has proven at 175B+ scale.
   - DPO: Simpler, cheaper, more stable, but primarily validated at <70B and hard to compose rewards.

4. **Decision:**
   - Large-scale (70B+), multi-reward (helpfulness + safety + honesty): RLHF.
   - Small team, single preference, rapid iteration: DPO.

5. **Nutshell answer:**
   > Choose RLHF when you need to balance multiple reward signals at scale and have the compute budget. Choose DPO for simplicity, stability, and cost-efficiency on smaller models.
