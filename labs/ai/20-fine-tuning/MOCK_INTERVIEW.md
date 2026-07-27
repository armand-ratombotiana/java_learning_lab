# Mock Interview: Fine-Tuning

## Question 1: Fine-Tuning Approaches
**Q**: Compare full fine-tuning vs parameter-efficient fine-tuning (PEFT).

**A**:
| Aspect | Full Fine-Tuning | PEFT (LoRA, Adapters) |
|--------|------------------|----------------------|
| Parameters updated | All | Small subset (<1%) |
| Memory (training) | Very high (gradients + optimizer states) | Low |
| Storage per task | Full model copy (7B ~ 14GB) | Small adapter (LoRA ~ 14MB) |
| Training speed | Slow | Fast |
| Quality | Highest | Near full fine-tuning |
| Multi-task serving | Difficult (separate copies) | Easy (swap adapters) |

## Question 2: LoRA Implementation
**Q**: Implement LoRA fine-tuning. Explain the rank parameter.

**A**: LoRA freezes pre-trained weights W and adds a low-rank decomposition: W + BA.

```python
class LoRALayer(nn.Module):
    def __init__(self, in_dim, out_dim, rank=8, alpha=16):
        super().__init__()
        self.A = nn.Parameter(torch.randn(in_dim, rank) * 0.01)
        self.B = nn.Parameter(torch.zeros(rank, out_dim))
        self.scaling = alpha / rank

    def forward(self, x):
        return x @ (self.A @ self.B) * self.scaling

# Usage: Replace linear layers
original = nn.Linear(4096, 4096)
lora = LoRALayer(4096, 4096, rank=8)
# During training: output = original(x) + lora(x)
```

Rank is the bottleneck dimension. Higher rank = more capacity/capacity to learn. Rank=8-64 works well.

## Question 3: QLoRA
**Q**: How does QLoRA enable fine-tuning on consumer GPUs?

**A**: QLoRA = Quantized LoRA. Key innovations:
1. **NF4 quantization**: 4-bit normal float quantization of pre-trained weights
2. **Double quantization**: Quantize quantization constants (saves additional memory)
3. **Paged optimizers**: Use CPU RAM for optimizer states that don't fit in GPU

Memory reduction: 7B model FP32 (28GB) -> FP16 (14GB) -> QLoRA NF4 (4GB) + gradients (~6GB) = ~10GB total, fitting on RTX 3090/4090.

## Question 4: Fine-Tuning Data
**Q**: How do you prepare data for instruction fine-tuning?

**A**: Data quality is more important than quantity.

```python
# Format: instruction, input, output
{
    "instruction": "Translate to French",
    "input": "Hello, how are you?",
    "output": "Bonjour, comment allez-vous?"
}

# Chat template
[
    {"role": "system", "content": "You are a helpful assistant"},
    {"role": "user", "content": "What is ML?"},
    {"role": "assistant", "content": "Machine learning is..."}
]
```

Best practices:
- 100-1000 high-quality examples > 100K noisy examples
- Diverse tasks, balanced categories
- Decontaminate against eval benchmarks
- Include edge cases and refusal examples
- Human-verified outputs over synthetic data

## Question 5: RLHF & DPO
**Q**: Compare RLHF (PPO) vs DPO for aligning language models.

**A**: 
**RLHF**: 
1. Train reward model on human preferences
2. Fine-tune LLM with PPO to maximize reward
3. KL penalty to stay close to reference model

**DPO (Direct Preference Optimization)**:
- Directly optimize policy from preferences (no reward model)
- Analytical solution for the PPO objective
- Much simpler training (no PPO, no reward model)
- Often matches or exceeds RLHF quality

```python
# DPO loss (simplified)
def dpo_loss(policy_logps, ref_logps, win_logps, lose_logps, beta=0.1):
    win_ratio = policy_logps - ref_logps  # log pi(y_w|x) / pi_ref(y_w|x)
    lose_ratio = policy_logps - ref_logps  # log pi(y_l|x) / pi_ref(y_l|x)
    logits = win_ratio - lose_ratio
    return -torch.log(torch.sigmoid(beta * logits))
```

DPO is preferred for most use cases due to simplicity. RLHF is preferred when: reward signal is complex, you need to train reward model separately, or explore-exploit trade-off is important.
