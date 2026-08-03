# Lab 11: Mock Interview — Model Quantization & Deployment

**Role**: ML Systems / Inference Engineer
**Duration**: 60 minutes
**Focus**: FP16 conversion, INT8 symmetric/asymmetric quantization, calibration, graph optimization, memory footprint

---

**Interviewer**: "Walk me through the lab's quantization toolkit."

**Candidate**: "Three precision paths and a graph optimizer. `toFP16`/`fromFP16`
convert between IEEE FP32 and half-precision: it slices the FP32 bit pattern,
rebias-  es the exponent (127 to 15), and truncates the mantissa to 10 bits —
3.14159 round-trips to 3.14063. `SymmetricQuantization` maps weights to
`[-127, 127]` with one scale = max|W|/127 and zero-point 0. `AsymmetricQuantization`
uses the full [min, max] range with scale = (max-min)/255 and a computed
zero-point, so it covers the whole range at the cost of the zero-point
arithmetic. `GraphOptimizer.fuseConvBiasRelu` fuses three kernels into one
TensorRT-style pass. The demo's numbers are the payoff: symmetric error 0.0299,
asymmetric 0.0271 on the same weights."

**Interviewer**: "Why does asymmetric beat symmetric in the demo, and when would it not?"

**Candidate**: "Because the lab's weight vector `{0.5, -1.2, ..., 2.1, -1.5, 0.0, -0.7}`
is unbalanced: the max is 2.1 but the distribution skews small. Symmetric
reserves ±127 for ±2.1, so the fine values like 0.5 land on coarse steps.
Asymmetric fits the [min, max] interval exactly, giving finer steps everywhere —
hence 0.0271 vs 0.0299. It would not win when weights are already symmetric
around zero (activations after a tanh, for instance): then the zero-point buys
nothing and the extra arithmetic is pure overhead. That's the classic rule of
thumb: activations (always non-negative after ReLU) want asymmetric; weights that
are roughly zero-centered do fine symmetric. My walkthrough pushes this to the
limit — a layer of 16 weights with an outlier produces FP16 error 0.0036 vs
symmetric 0.0717 vs asymmetric 0.0541."

**Interviewer**: "The FP16 error is 0.0036 — 20x smaller than INT8. So why ever use INT8?"

**Candidate**: "Two reasons: speed and memory. Memory is easy to quantify: FP32 is 64
bytes for that layer, FP16 32, INT8 16 — 50% and 75% reductions. INT8 also maps
to integer vectorized math (Tensor Cores, NEON, AVX2) that is often 2-4x faster
than FP16 compute, and it halves the memory bandwidth per weight, which at
inference is usually the bottleneck — loading weights is the dominant cost per
token. The trade is accuracy: FP16 is nearly lossless (5-6 mantissa bits of
precision loss, no range loss), INT8 needs calibration and care. The standard
tiering: FP16 for the weights that matter (first/last layers, attention
projections), INT8 for the rest, and mixed precision that assigns per-layer
precision by measured sensitivity."

**Interviewer**: "Explain the graph fusion: what does `fuseConvBiasRelu` actually save?"

**Candidate**: "Three kernels — conv, bias add, ReLU — become one. Instead of writing
the conv output to memory, reading it back for the bias add, writing again, and
reading for ReLU, the fused kernel keeps everything in registers: `max(0, w +
bias)` per element, as the lab's code does. The saved cost is memory traffic,
which dominates small-elementwise kernels — often 20-40% end-to-end latency in
real models. The lab's fused output `[0.6, 0.0, 0.4, 0.0]` shows it: 0.5+0.1=0.6
kept, -0.2+(-0.1)=-0.3 clamped to 0, and so on. TensorRT generalizes this to
kernel auto-tuning, precision calibration, and dynamic shapes — the lab's
`FusedNode("Conv+Bias+ReLU", ...)` is the concept in miniature."

**Interviewer**: "Walk through the quantization math for the symmetric case: weight 2.1."

**Candidate**: "`scale = max|W| / 127 = 2.1/127 ≈ 0.0165`. Every weight divides by the
scale and rounds: 2.1/0.0165 ≈ 127 (the max saturates to 127), 1.2/0.0165 ≈ -73,
and 0.5 → 30. Dequantization multiplies back: `q * scale`. The demonstrable
artifact: the rounding error is at most half a step — 0.0083 — which is why
`dequantize` reproduces small weights to ~1-2 decimal places but the error
accumulates to 0.0299 total. The clamp is also visible in the code: `Math.clamp(q,
-128, 127)` guards the rare overflow. The asymmetric case: scale = (2.1-(-1.5))/255
≈ 0.0141, zeroPoint = round(1.5/0.0141) ≈ 106, and quantized values shift by 106
so 0.0 maps to 106 exactly — that's the value of the zero-point: float 0 is
representable, which symmetric only achieves for 0 itself."

**Interviewer**: "What's the difference between PTQ and QAT, and which does the lab
show?"

**Candidate**: "The lab is pure post-training quantization: it takes frozen weights and
fits scale/zero-point by measuring the range — `SymmetricQuantization` and
`AsymmetricQuantization` constructors do exactly that, no training loop. PTQ is
fast and works well at 8 bits, but at INT4 the error compounds and QAT wins:
QAT injects fake quantization nodes into the graph during training so the model
learns weights that survive rounding. There's also calibration: PTQ often
collects activation statistics on a calibration dataset to pick ranges that
minimize per-tensor error — the lab skips that by using the weight range
directly, which is fine for weights but optimistic for activations. In my
walkthrough the whole process is PTQ by construction: fixed weights in, scale
and zero-point out, error measured on the same tensors."

**Interviewer**: "How would you choose a precision per layer in a real deployment?"

**Candidate**: "Measure sensitivity: quantize one layer at a time, evaluate a
calibration set, and record the metric drop (perplexity for LLMs, accuracy for
classifiers). The layers that cost the most get FP16 or stay FP32 — typically
the embedding/output heads and the first few layers — while the bulk gets INT8.
Then sweep quantization of the KV cache separately, since it dominates memory at
long context (lab 12 territory). Finally, verify with the target workload, not a
microbenchmark: real inference mixes compute-bound prefill with bandwidth-bound
decode. The lab's memory math — 64/32/16 bytes — is the table I'd hand a PM when
explaining why a 7B model drops from ~28GB to ~7GB."

**Interviewer**: "The lab's `toFP16` returns 0 for tiny or overflowing values. What are
the failure modes of naive FP16 conversion?"

**Candidate**: "Range: FP16 exponents go from -14 to 15, so values below ~6e-5 flush
to zero — `if (newExp <= 0) return 0` — and values above ~65504 overflow to
infinity — `if (newExp >= 31) return 0x7c00`. A model with large logits or
un-normalized weights can silently corrupt under FP16; the standard fix is loss
scaling in training (multiply the loss by a scale factor before backprop) or
BF16, which keeps FP32's exponent range and sacrifices mantissa bits instead.
The lab's code is pedagogically honest: it implements the IEEE semantics
including the flush, and `fromFP16` even reconstructs the signed zeros and
NaN/inf encodings. For a deployment you'd test with real value distributions
before trusting the 50% memory saving."

**Interviewer**: "How does this lab connect to the rest of the GenAI track?"

**Candidate**: "Deployment is where everything lands. Lab 11's quantized weights are
what lab 15's registry serves, and the `costUnits` in lab 15's `Model` record are
exactly what memory and compute savings like these lower. The KV cache pressure
that quantization reduces is lab 02's cache and lab 13's context window.
Speculative decoding (lab 12) runs a quantized draft model against a full-precision
target — a textbook mixed-precision deployment. So when I'm asked about
production cost, I reach for this lab's numbers: 75% memory reduction from INT8,
kernel fusion for latency, and per-layer precision selection for accuracy."

**Interviewer**: "What's your final takeaway from the lab?"

**Candidate**: "Quantization is an error-budget exercise, not a format choice. The lab
demonstrates the three tools — range mapping, zero-point shifting, and graph
fusion — and gives you the measurement loop: quantize, dequantize, compare L1
error. 0.0299 vs 0.0271 in the demo and 0.0717 vs 0.0541 in my 16-weight
walkthrough are tiny absolute numbers, but they compound across hundreds of
layers, which is why real deployments calibrate per layer and verify with
end-task metrics. Choose the smallest precision that holds the error budget —
the lab gives you the machinery to make that decision measurable."
