# LLM Fine-tuning - Theory

## 1. Full Fine-tuning

### Process
- Update all parameters
- Task-specific dataset
- High resource requirements

### Considerations
- Catastrophic forgetting
- Overfitting
- Compute requirements

## 2. Parameter-Efficient FT

### LoRA (Low-Rank Adaptation)
- Add low-rank matrices
- Original weights frozen
- High efficiency

### QLoRA
- LoRA + quantization
- Even more memory efficient
- 4-bit base model

### Prefix Tuning
- Learn continuous prompts
- Prepend to inputs

### Adapters
- Small bottleneck layers
- Insert into transformers

## 3. Instruction Tuning

### Data
- Instruction-response pairs
- Diverse tasks
- Quality matters

### Training
- Next-token prediction
- Cross-entropy loss

## 4. RLHF

### Steps
1. **SFT**: Supervised fine-tuning
2. **Reward Model**: Learn preferences
3. **PPO**: Optimize against reward

### Reward Modeling
- Compare responses
- Learn human preferences

### PPO
- Policy gradient
- KL divergence constraint
- Value estimation

## 5. Practical Considerations

### Dataset Size
- Quality > quantity
- Few thousand examples often enough

### Hardware
- Multi-GPU training
- Mixed precision
- Gradient checkpointing

### Evaluation
- Human evaluation
- Automated benchmarks
- Task-specific metrics