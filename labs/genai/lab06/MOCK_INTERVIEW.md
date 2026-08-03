# Lab 06: Mock Interview — Fine-Tuning with LoRA/QLoRA

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: low-rank adaptation, frozen base weights, alpha/r scaling, rank selection, adapter merging, QLoRA quantization

---

**Interviewer**: "Explain LoRA's core idea in terms of the lab's `LoRALayer`."

**Candidate**: "Instead of updating the full `d x d` weight matrix — which for a real model
is billions of parameters — LoRA freezes `W` and learns a low-rank correction. The
lab's `LoRALayer` holds `W` (frozen), `A` (`d x r`), and `B` (`r x d`) with `r << d`.
The forward pass computes `y = xW + (xA)B * scaling`, where `scaling = alpha / r`. So
the adaptation `delta W` is the product `A B` — a rank-`r` matrix, which by the low-rank
hypothesis is enough to capture task-specific change to a pretrained model. The
parameter count drops from `d²` to `2rd`: my walkthrough's accounting shows 64 frozen
versus 32 trainable at `d=8, r=2` — and at real dimensions the gap is enormous, which
is the whole point of parameter-efficient fine-tuning."

**Interviewer**: "Walk through the initialization. Why is `A` initialized small and `B` to
zero?"

**Candidate**: "With `B = 0`, the adapter contributes nothing at the start: `(xA)B = 0`,
so the fine-tuned model begins as the exact pretrained model. That's a deliberate
stability choice — you don't want fine-tuning to immediately distort the pretrained
weights, and gradient steps stay in a controllable neighborhood of the base model.
`A` is initialized with small Gaussian values (`* 0.01` in the lab, using the seeded
`Random(42)`), so when training starts, updates are tiny even before `B` has moved
from zero. This mirrors the standard LoRA recipe: `A` gets the initialization, `B`
gets the learning, and the frozen `W` anchors the behavior."

**Interviewer**: "The demo trains 50 epochs and loss drops from 3.073154 to 3.072039 — a
tiny change. What's going on, and how would you make it actually learn?"

**Candidate**: "The toy is honest about the ingredients: `d = 8` with `r = 2`, one sample,
a 0.01 learning rate, and only `B` updated with a hand-derived gradient. The loss
declines smoothly — 3.0729 at epoch 10, 3.0726 at 20, down to 3.0720 — but the base
mapping `x*2 + 0.5` versus the frozen `W` (Gaussian `* 0.1`) has a large residual error
that a rank-2 adapter on one sample barely closes. To make it learn meaningfully you'd
raise the learning rate, train on a real batch of samples, update `A` as well as `B`,
and increase `r`. The important thing the demo proves is *mechanics*: the gradient
flow is correct and loss decreases monotonically — the pipeline works, the capacity is
just toy-sized."

**Interviewer**: "What does the `alpha / r` scaling factor do, and how do you choose `alpha`?"

**Candidate**: "The scaling controls how much of the adapter's contribution reaches the
forward pass. With `alpha = r`, scaling is 1 and the adapter acts at natural scale;
with `alpha = 2r` you're doubling its effect. The subtlety: `A B` shrinks with `r`
(you're summing `r` terms), so the scaling compensates so that changing `r` doesn't
change the effective update magnitude — you can raise `r` for capacity without
retuning `alpha`. In the lab, `LoRALayer(d, r, 4.0)` with `r=2` gives `scaling = 2.0`.
In practice `alpha = 2r` is the common starting point, and both `alpha` and `r` are
tuned together against a validation set."

**Interviewer**: "How do you choose the rank `r`?"

**Candidate**: "Rank is the capacity knob: lower `r` is more parameter-efficient and
regulates against overfitting; higher `r` captures more task-specific structure. The
INTERVIEW guide gives 8-64 as the typical range, and the right value depends on task
complexity — a style transfer needs less rank than a domain shift like legal
terminology. The empirical method: sweep `r` over a few values, watch validation loss
and the intrinsic dimensionality of the learned `A B` (do its singular values decay
fast? if so, lower `r` is fine), and pick the smallest `r` that meets quality targets.
The lab's `r=2` is deliberately minimal — it demonstrates the mechanism, not a
realistic deployment."

**Interviewer**: "The lab trains only `B`. Why does that work at all, and what's the
difference from training both?"

**Candidate**: "It works because `B` multiplies the learned `A` — the gradient flows
through `A` into `B`'s update, so `B` alone can represent the task adaptation,
especially since `A` was randomly initialized with fixed direction. But training both
`A` and `B` converges faster and typically reaches better solutions — the lab's
`trainStep` computes `gradB` explicitly from `(y - target) * A[k][i] * x[j]` with the
`scaling / y.length` factor, and an analogous `gradA` term would let both matrices
move. In practice both are trained with the same optimizer; the walkthrough updates
`B` only to mirror the lab and to make the merge test meaningful — frozen `W` plus
trained `B` is still a real LoRA variant."

**Interviewer**: "Why can adapters be merged, and what are the trade-offs? The demo's
merge verification prints a max diff of 1e-17."

**Candidate**: "Because the forward pass is linear in the weights: `y = xW + (xA)B·s` is
the same as `y = x(W + s·AB)`. My walkthrough's `merge()` builds exactly that —
`merged[i][j] = W[j][i] + scaling * sum_k A[j][k] * B[k][i]`, mirroring the lab's
transposed indexing — and the verification diff `8.3e-17` is floating-point noise,
proving the math. The trade-off: merging eliminates per-adapter inference overhead and
lets you serve the adapted model at base-model speed, but you lose the ability to swap
tasks dynamically — with unmerged adapters, switching tasks is just changing which
`A`/`B` you load, which is how multi-tenant LoRA serving works. Merging also bakes the
adapter into the checkpoint, so updates require re-export."

**Interviewer**: "What does QLoRA add on top of LoRA, and how does `QuantizedWeights`
illustrate it?"

**Candidate**: "QLoRA quantizes the *frozen* base weights while keeping the adapters in
high precision — that's what makes fine-tuning a 70B model fit on one GPU: the base
model lives in 4-bit, and only the small FP16 adapters get gradients. The lab's
`QuantizedWeights` captures the quantization half: it computes a scale from the max
absolute weight — `max / ((1 << (bits-1)) - 1)`, so 7 for 4-bit — rounds each weight to
`int` via `Math.round(v / scale)`, and `dequantize` multiplies back by scale. The demo
prints 64 entries at 4-bit each for the `d=8` matrix, and my walkthrough's memory
accounting shows the punchline: 256 bytes of FP32 versus 32 bytes packed — an 8x
reduction, which is exactly the memory equation that makes QLoRA feasible."

**Interviewer**: "How does LoRA compare to full fine-tuning in terms of quality?"

**Candidate**: "For most task adaptation the gap is small — the low-rank hypothesis says
pretrained models need only low-rank updates for new tasks — and LoRA often
generalizes *better* because it regularizes: it can't make large arbitrary changes,
which protects against overfitting small datasets. Full fine-tuning has more capacity
and can reach higher ceiling on genuinely new capabilities, but at the cost of storing
a full copy of the model per task, more VRAM, and catastrophic-forgetting risk.
The pragmatic line: LoRA for task adaptation, style, and per-tenant customization;
full fine-tuning only when the task demands it. Most production fine-tuning of open
models — Llama, Mistral — is LoRA or QLoRA."

**Interviewer**: "How would you deploy a LoRA adapter at scale — say 1,000 adapters for
1,000 tenants?"

**Candidate**: "The multi-adapter serving pattern: one base model in memory, adapters
kept unmerged as small files (`2rd` parameters each — my walkthrough's `Trainable A+B:
32` for a toy `8x8` layer scales to a few hundred MB per adapter at real size, which
is trivial), loaded on demand per tenant request. With a router that batches requests
by adapter to share the base-model forward pass, throughput stays high. Merging is
the fallback for hot tenants whose adapters should run at base speed. QLoRA enters at
the edge: quantized base + high-precision adapters means the shared base is small
enough to replicate cheaply. The failure modes to monitor are adapter interference —
per-tenant quality drift — and storage cost growing linearly with tenant count."

**Interviewer**: "What would you change about the lab's LoRA implementation to train a
real model?"

**Candidate**: "Everything that's a toy simplification: real data batches with an
optimizer (AdamW with weight decay) instead of single-sample SGD; gradients for both
`A` and `B` via autodiff instead of hand-derived `gradB`; dropout on the adapter path;
`r` in the 8-64 range with `alpha = 2r`; weights stored in bf16 with a fused
`W + scaling * AB` kernel for efficiency. And the `QuantizedWeights` toy stores each
4-bit value in an `int` — a real implementation packs four values per byte and uses
dequantization *during* matmul, not before. The lab's skeleton — frozen `W`, low-rank
`A B`, scaling, merge — is exactly the production architecture; the gap is engineering,
not concept."
