# Mathematical Foundation of LoRA

## 📐 The Standard Forward Pass
In a standard dense layer (like a linear projection in a Transformer), the forward pass is a simple matrix multiplication.

Let:
- $x$ be the input vector of dimension $d$.
- $W_0$ be the pre-trained weight matrix of dimension $d \times k$.
- $h$ be the output vector of dimension $k$.

The standard forward pass is:
$$ h = W_0 x $$

During full fine-tuning, we update $W_0$ by adding a gradient matrix $\Delta W$:
$$ h = (W_0 + \Delta W) x $$
Here, $\Delta W$ has the exact same massive dimensions as $W_0$ ($d \times k$).

## 📉 The Intrinsic Rank Hypothesis
Research by Aghajanyan et al. (2020) showed that while LLMs have billions of parameters, the actual "information" required to adapt them to a specific new task resides in a very low-dimensional space. This is called the "Intrinsic Rank" of the adaptation.

We don't need the full capacity of $\Delta W$ to learn a new task.

## 🧮 The LoRA Decomposition
LoRA constrains the update matrix $\Delta W$ by representing it as the product of two much smaller matrices.

Let $r$ be the chosen low rank (e.g., $r = 8$), where $r \ll \min(d, k)$.
We define two new matrices:
- $B$: dimension $d \times r$
- $A$: dimension $r \times k$

We replace $\Delta W$ with the product $B A$:
$$ \Delta W = B A $$

The new forward pass becomes:
$$ h = W_0 x + B A x $$

### Training Dynamics
1. $W_0$ is frozen. No gradients are calculated for it.
2. Matrix $A$ is initialized with random Gaussian noise.
3. Matrix $B$ is initialized with exactly zero. 
   *(This ensures that at the very beginning of training, $BA = 0$, meaning the model behaves exactly like the original pre-trained model before any learning happens).*
4. During backpropagation, gradients are only calculated and applied to $A$ and $B$.

## ⚡ The Inference Optimization (Merging)
When training is finished, we have our frozen $W_0$ and our trained $A$ and $B$.
If we run inference using $h = W_0 x + B A x$, we are doing two matrix multiplications, which adds slight latency.

Because matrix multiplication is distributive, we can pre-compute the merged weights *before* serving the model:
$$ W_{merged} = W_0 + B A $$

We simply add the expanded $BA$ matrix to the original $W_0$ matrix. Now, during inference, we just do:
$$ h = W_{merged} x $$
There is **zero inference latency overhead** when using LoRA!