# Lab 01: Mock Interview — Transformer Architecture Deep Dive

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: scaled dot-product attention, multi-head attention, positional encoding, encoder blocks, residual connections, layer normalization

---

**Interviewer**: "Walk me through scaled dot-product attention. Why is it called 'scaled'?"

**Candidate**: "Attention computes a weighted average of values where the weights come from a
query-key similarity. In the lab's `scaledDotProductAttention(Q, K, V)` we take every
pair of rows — query `Q[i]` dotted with key `K[j]` — and divide the dot product by
`Math.sqrt(dk)` before softmax. The scaling matters because `dk` is typically 64 or
128, and the sum of `dk` unit-variance products has variance `dk`, so raw dot products
grow like the square root of the dimension. Unscaled, those large values push softmax
into the saturated regime where the gradient is essentially zero, and training stalls.
Dividing by `sqrt(dk)` keeps the scores near unit variance, so the softmax operates in
its sensitive region."

**Interviewer**: "In the lab demo, position 0 of the first attention output row is 0.1181
with Q, K, V seeded as sine, cosine, and `(i+j)*0.1`. What does that number actually mean?"

**Candidate**: "That's the first row of `V`-weighted output for query position 0. The softmax
weights are distributed across all four key positions, and each weight multiplies the
corresponding `V` row, `(i+j)*0.1`. Position 0's value vector is `[0, 0, 0, 0]`, so the
output of row 0 is purely the weighted contribution of the other rows — you can see the
four output values are `0.1181, 0.2181, 0.3181, 0.4181`, which are just the weighted
averages of `0.1, 0.2, 0.3` per column. The demo is validating that the weighted sum
machinery is correct, not that the numbers are meaningful."

**Interviewer**: "Why does the Transformer use multi-head attention instead of one single
head with a larger dimension?"

**Candidate**: "One head computes one similarity function; it can only find one pattern of
association per position pair. Splitting into `h` heads, each with its own `Wq`, `Wk`,
`Wv` projections, lets different heads specialize — one head may track syntactic
dependencies, another semantic similarity, another position-based relations. The lab
shows this design as separate `Wq`, `Wk`, `Wv`, `Wo` matrices flowing into
`scaledDotProductAttention`, then a concatenation projection back out. Practically,
multi-head also acts as a mild ensemble: averaging across heads reduces variance, and
the concatenation layer can recombine the subspaces however the task needs."

**Interviewer**: "What does `positionalEncoding` in the lab do, and why is the sinusoid
formula structured as it is?"

**Candidate**: "Self-attention is permutation-invariant — it would treat 'the cat sat' and
'sat cat the' identically — so we must inject order information. The lab's
`positionalEncoding(seqLen, dModel)` adds `sin(angle)` to even dimensions and
`cos(angle)` to odd dimensions, where `angle = pos / 10000^(2i/dModel)`. Two properties
make this clever: the frequencies decrease along the dimension axis, so low dimensions
encode fine-grained position and high dimensions encode coarse position, and because of
trig identities, the encoding at position `p + k` can be expressed as a linear
combination of the encodings at position `p`, which makes it easy for attention to learn
relative positions. You can see it in the demo: row 0 is `[0, 1, 0, ...]`, row 1 is
`[0.8415, 0.5403, 0.0998, ...]` — each position gets a distinct vector."

**Interviewer**: "The demo output for positional encoding starts with `[0.0, 1.0, 0.0]` for
position zero. Why exactly that?"

**Candidate**: "For `pos = 0` the angle is zero for every dimension: `sin(0) = 0` and
`cos(0) = 1`. Since even dimensions add `sin` and odd dimensions add `cos`, position 0
gets `0` on every even dimension and `1` on every odd dimension — so the first three
dims print `[0.0, 1.0, 0.0]`. That's the demo's first sanity check: the boundary
condition of the encoding is exactly the identity you'd expect, and then subsequent
rows show the sinusoids kicking in."

**Interviewer**: "Walk through the encoder block in the code, operation by operation."

**Candidate**: "`encoderBlock` takes the input `x` and seven weight matrices: `Wq`, `Wk`,
`Wv` to project into attention space, `Wo` to project attention output back, and `W1`,
`W2` for the FFN. First it runs multi-head-style attention: `scaledDotProductAttention(
matMul(x,Wq), matMul(x,Wk), matMul(x,Wv))`, then `matMul(attnOut, Wo)`. Then the
residual: `add(x, attnOut)` followed by `layerNorm`. Then the two-layer FFN with `relu`
between, another `add(norm1, ffnOut)`, and a final `layerNorm`. So the shape is exactly
the canonical `MHA -> Add & Norm -> FFN -> Add & Norm` from the original paper, with the
residuals inside the sub-layers, which is the standard post-norm arrangement."

**Interviewer**: "Why do we need both the residual and the layer norm? What does each
contribute?"

**Candidate**: "They solve different problems. The residual connection gives the gradient a
shortcut path: the identity term means `dL/dx` contains a term that flows through
untouched, which mitigates vanishing gradients when you stack dozens of blocks — at
depth `L`, gradients don't have to traverse `L` separate transformations to reach early
layers. Layer norm stabilizes the *distribution* of activations: the lab's `layerNorm`
computes mean and variance per row and normalizes with a `1e-6` epsilon for numerical
safety. That prevents activations from drifting to extreme scales layer after layer,
which keeps the optimizer's effective learning rate consistent. Post-norm versus
pre-norm placement is a separate design choice — modern models put the norm before
attention — but the lab follows the original post-norm layout."

**Interviewer**: "The lab's `softmax` subtracts the row max before exponentiating. Why?"

**Candidate**: "Numerical stability. `exp(1000)` overflows to infinity in double precision,
and if the logits are large — which is exactly the failure mode the `sqrt(dk)` scaling
is designed to prevent — subtracting the max first shifts the whole row so the largest
value is `exp(0) = 1`. Since softmax is invariant to adding a constant to every logit
in a row, the result is mathematically identical but numerically safe. This is the
standard trick in every production implementation, and it's worth calling out in an
interview because it shows awareness that these formulas are implemented under floating
point constraints, not just on paper."

**Interviewer**: "The encoder block accepts `Wq`, `Wk`, `Wv` and `Wo` separately. Why not
fold them into one projection?"

**Candidate**: "You could concatenate them into a single `Wqkv` matrix and slice the output,
and production inference engines do exactly that for memory layout efficiency. But
keeping them separate makes the code faithful to the mathematical definition and makes
each head's projection explicit. More importantly, `Wo` must be separate from `Wq/k/v`
because its job is different: it maps the concatenated multi-head output back into the
model dimension, and it's where heads get mixed together. Folding `Wo` into the
attention projections would destroy the per-head separation the multi-head design
exists for."

**Interviewer**: "What happens to the attention complexity as sequence length grows, and how
does the lab's implementation reflect that?"

**Candidate**: "The score matrix is `seqLen x seqLen` — the nested loops in
`scaledDotProductAttention` compute `seqLen²` dot products, each `O(dk)`. So the
complexity is `O(n² · dk)` in time and `O(n²)` in memory, which is why long-context
models from lab 13's world use sparse patterns like sliding windows or linear attention
instead. In an interview I'd also point out that the lab's `encoderBlock` passes the
full sequence through attention once per block, so the quadratic term multiplies with
the number of layers — the total is `L · n² · dk`, which is the number that actually
dominates training FLOPs on long inputs."

**Interviewer**: "How would you debug a Transformer whose attention weights look flat — all
positions equally attended?"

**Candidate**: "Flat attention usually means the scaling is broken or the projections have
collapsed. First check: is the `1/sqrt(dk)` factor present? If `dk` is large and
unscaled, softmax saturates and one position wins — the opposite failure. If the weights
are genuinely near-uniform, the keys are probably uninformative: either the embedding
space collapsed (all token embeddings identical) or the weights died. In the lab's demo
you can actually observe a mild version of this — the attention rows are fairly diffuse
because the synthetic Q/K vectors are simple sine/cosine patterns with no real
semantics. The debugging move is to print the score matrix before softmax, check its
variance, and then check whether the keys differ across positions at all."

**Interviewer**: "Encoder-decoder versus decoder-only — when would you pick each?"

**Candidate**: "Encoder-decoder, like the T5-style architecture the lab describes, gives the
encoder bidirectional context — every token sees the whole input — which is best for
tasks where the input must be fully understood first: translation, summarization,
question answering over documents. Decoder-only applies causal masking throughout, so
every position sees only the past; that's what you want for autoregressive generation,
and it's the dominant choice at scale because a single stack is simpler to train and
the objective — predict the next token — makes it trivially parallelizable and
incredibly data-hungry in a way that scales. The lab's `encoderBlock` shows the
bidirectional variant; lab 02's causal attention is the decoder version. In production
today, most new models are decoder-only even for understanding tasks."

**Interviewer**: "The demo prints 'Transformer components validated successfully.' What
would actually fail if someone deleted the `relu` from the FFN?"

**Candidate**: "The block would still run, but you'd lose the nonlinearity — the whole
Transformer would collapse into a linear transformation. Two stacked matrix
multiplications with only affine operations between them compose into a single linear
map, so the model could not learn anything a linear classifier couldn't. The FFN with
its nonlinearity is where most of the model's capacity and most of the parameters live,
and it's also where knowledge tends to get stored in practice. `relu` itself is the
cheapest choice — `Math.max(0.0, x)` — but SwiGLU and GELU variants give better
training dynamics; the lab deliberately uses ReLU for simplicity."

**Interviewer**: "If you were shipping this attention code to production at Google scale,
what would you change?"

**Candidate**: "Three things. First, replace the row-wise softmax loops with the numerically
stable fused kernel — the max-subtraction is there, but a production kernel fuses the
softmax into the matrix multiply to avoid materializing the `n x n` score matrix.
Second, use FlashAttention-style tiling so the attention never writes the full `n²`
matrix to HBM — the lab materializes `scores` in a `double[seqLen][seqLen]`, which is
fine at `seqLen = 4` and catastrophic at 8K tokens. Third, switch from dense `double`
to bf16 with block-level scaling once the math is validated — that's the difference
between a pedagogical implementation and one that fits in a GPU's registers."
