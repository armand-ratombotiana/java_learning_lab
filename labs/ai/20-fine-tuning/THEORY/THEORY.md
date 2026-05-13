# Fine-Tuning - Complete Theory

## 1. Why Fine-Tune?

### 1.1 Pre-trained Limitations
- General knowledge
- May not fit domain
- No task-specific behavior

### 1.2 Fine-tuning Benefits
- Adapt to domain
- Custom behavior
- Better performance

## 2. Full Fine-tuning

### 2.1 Process
- Start with pre-trained weights
- Update all parameters
- Task-specific data

### 2.2 Challenges
- Computationally expensive
- Risk of catastrophic forgetting
- Large storage requirements

## 3. Parameter-Efficient Fine-tuning (PEFT)

### 3.1 LoRA (Low-Rank Adaptation)
- Decompose weight updates
- Only train adapters
- A: down-projection, B: up-projection
- W' = W + BA

### 3.2 QLoRA
- Quantized base model
- LoRA adapters
- Efficient memory usage

### 3.3 Adapter Tuning
- Add small modules
- Prefix tuning
- Prompt tuning

### 3.4 BitFit
- Only train bias terms

## 4. Adapter Types

### 4.1 Sequential Adapters
- Add between layers
- Series configuration

### 4.2 Parallel Adapters
- Add in parallel with layers

### 4.3 Hypercomplex Adapters
- Hierarchical structure

## 5. Training Techniques

### 5.1 Learning Rate
- Lower than pre-training
- Warmup schedule

### 5.2 Regularization
- Early stopping
- Weight decay
- Gradient clipping

### 5.3 Data Formatting
- Instruction format
- Chat format
- Special tokens

## 6. Evaluation

### 6.1 Task Metrics
- Accuracy
- F1
- BLEU
- ROUGE

### 6.2 Model Quality
- Perplexity
- Hallucination rate
- Toxicity

## Java Implementation

```java
public class LoRALayer {
    private Matrix A;  // down-projection
    private Matrix B;  // up-projection
    private int rank;
    private double alpha;
    
    public LoRALayer(int inDim, int outDim, int rank) {
        this.rank = rank;
        this.alpha = rank;  // common scaling
        
        Random gen = new Random(42);
        A = Matrix.random(outDim, rank, gen.nextLong());
        B = Matrix.random(rank, inDim, gen.nextLong());
        
        // Initialize A with random, B with zeros
        for (int i = 0; i < rank; i++) {
            for (int j = 0; j < inDim; j++) {
                B.data[i][j] = 0;
            }
        }
    }
    
    public Matrix forward(Matrix input) {
        // W' = W + alpha * BA
        // Return adapter output only: alpha * BA
        Matrix BA = MatrixOperations.multiply(
            MatrixOperations.multiply(B, input),
            A
        );
        return MatrixOperations.scale(BA, alpha / rank);
    }
    
    public void update(Matrix gradOutput, double lr) {
        // Compute gradients for A and B
        // Simplified SGD update
    }
}

public class FineTuner {
    private LLM baseModel;
    private LoRALayer[] adapters;
    private double learningRate;
    
    public FineTuner(LLM model, int[] layerRanks) {
        this.baseModel = model;
        this.adapters = new LoRALayer[layerRanks.length];
        
        for (int i = 0; i < adapters.length; i++) {
            adapters[i] = new LoRALayer(512, 512, layerRanks[i]);
        }
    }
    
    public void train(Dataset dataset, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (Sample sample : dataset) {
                // Forward pass with adapters
                Matrix output = baseModel.forward(sample.input);
                
                for (int i = 0; i < adapters.length; i++) {
                    output = MatrixOperations.add(output, 
                        adapters[i].forward(output));
                }
                
                // Compute loss
                double loss = computeLoss(output, sample.target);
                
                // Backward pass
                Matrix grad = computeGrad(loss);
                
                for (LoRA adapter : adapters) {
                    adapter.update(grad, learningRate);
                }
            }
        }
    }
    
    public LLM save() {
        // Combine base model with trained adapters
        return baseModel;
    }
}
```

## 7. RLHF (Reinforcement Learning from Human Feedback)

### 7.1 Reward Model
Train model to predict human preferences

### 7.2 PPO (Proximal Policy Optimization)
- Maximize reward
- KL constraint from base model
- Multiple epochs of refinement

### 7.3 DPO (Direct Preference Optimization)
- Simplify training
- Use preferences directly
- No separate reward model