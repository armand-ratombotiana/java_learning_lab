# Fine-tuning - EXERCISES

## Exercise 1: Calculate LoRA Parameters
Compute trainable params for LoRA with r=8, d_model=1024.

```java
public int countLoRA() {
    int r = 8;
    int d = 1024;
    
    int params = 2 * r * d;
    
    return params;
    // 2 * 8 * 1024 = 16384 parameters
}
```

## Exercise 2: Compare Full Fine-tuning vs LoRA
What % of params is trainable with LoRA (r=8, d=1024, layers=12)?

```java
public double ratio() {
    int r = 8;
    int d = 1024;
    int layers = 12;
    
    int loraParams = 2 * r * d * layers;
    int fullParams = d * d * layers;
    
    double ratio = (double) loraParams / fullParams * 100;
    
    return ratio;
    // ~1.56% of full model
}
```

## Exercise 3: Configure QLoRA
Set up QLoRA with 4-bit quantization.

```java
public QLoRALayer setup() {
    int dModel = 1024;
    int r = 8;
    int bits = 4;
    
    QLoRALayer layer = new QLoRALayer(dModel, r);
    
    return layer;
}
```

## Exercise 4: Prepare Fine-tuning Data
Tokenize prompt-response pair.

```java
public int[] tokenize() {
    String prompt = "Translate to French: Hello";
    String response = "Bonjour";
    
    int[] promptIds = tokenize(prompt);
    int[] responseIds = tokenize(response);
    
    int[] combined = new int[promptIds.length + responseIds.length];
    
    System.arraycopy(promptIds, 0, combined, 0, promptIds.length);
    System.arraycopy(responseIds, 0, combined, promptIds.length, responseIds.length);
    
    return combined;
}

private int[] tokenize(String text) {
    return text.split("").length > 0 ? 
           new int[]{1, 2, 3} : new int[]{};
}
```

## Exercise 5: Train LoRA Layer
Single training step for LoRA.

```java
public void trainStep() {
    LoRALayer lora = new LoRALayer(512, 8, 16);
    
    double[] input = new double[512];
    double[] gradient = new double[512];
    
    lora.update(input, gradient);
    
    System.out.println("Trainable params: " + lora.getParameters());
}
```

---

## Solutions

### Exercise 1:
```java
// 2 matrices: A (r×d) + B (d×r) = 2rd = 16384
```

### Exercise 2:
```java
// LoRA uses ~1.56% of full model parameters
```

### Exercise 3:
```java
// QLoRA with 4-bit quantized weights + LoRA adapters
```

### Exercise 4:
```java
// Combined sequence length limited by maxLength
```

### Exercise 5:
```java
// Only LoRA matrices updated, base model frozen
```