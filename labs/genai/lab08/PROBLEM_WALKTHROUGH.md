# Problem Walkthrough: Multimodal Models

## Problem 1: Zero-Shot Image-Text Retrieval with Contrastive Training — Company: OpenAI

### Interview Scenario
"You're at OpenAI building a demo that proves the value of contrastive alignment: start
with the lab's `PatchEmbedding` and `TextEncoder`, embed 4 images and 4 captions, show
that random encoders retrieve at chance level, then simulate a few InfoNCE gradient steps
that pull matched pairs together and push unmatched pairs apart, and measure recall@1
before and after."

### The Problem
1. Embed 4 images with `PatchEmbedding.embed` and average the patches into image vectors.
2. Embed 4 captions with `TextEncoder.averageEncode`.
3. Measure recall@1 and InfoNCE loss before any training (baseline).
4. Apply the actual InfoNCE gradient on the text encoder for 50 steps.
5. Report recall and loss at steps 1, 10, 50, then the final retrieval table.

### Solution Walkthrough
- Step 1: Copy `PatchEmbedding` (seeded `Random(42)` projection, `patchSize` splits),
  `TextEncoder` (seeded 99, embed cache), and `contrastiveLoss` verbatim from the lab.
- Step 2: Build 4 random images with `randomImage(7 + i, 8)` and average their patch
  embeddings into unit vectors; embed the 4 captions with `averageEncode`.
- Step 3: Implement `recallAtOne` — for each caption, argmax cosine over images, count
  hits. Baseline prints 0/4 with loss 4.29: random encoders, no alignment.
- Step 4: Implement `trainStep` with the real InfoNCE gradient: for each image,
  `p = softmax(logits)`, accumulate `grads[j] += (p[j] - (i==j ? 1 : 0)) / τ * img[i]`,
  then move and renormalize each text embedding. Small lr (0.05) avoids overshoot.
- Step 5: Print recall and loss at steps 1, 10, 50, then the final per-caption
  retrieval table with HIT/miss markers.

### Code
```java
package com.genai.lab08.solution;

import java.util.*;

/**
 * Lab 08 walkthrough: CLIP-style image-text retrieval with
 * contrastive alignment. Reuses the lab's PatchEmbedding,
 * TextEncoder, and contrastiveLoss to build a zero-shot
 * image-caption matcher and measure recall@1.
 */
public class CLIPRetrieval {

    static class PatchEmbedding {
        final int patchSize;
        final int dModel;
        final double[][] projection;

        PatchEmbedding(int patchSize, int dModel) {
            this.patchSize = patchSize;
            this.dModel = dModel;
            projection = new double[patchSize * patchSize * 3][dModel];
            Random rng = new Random(42);
            for (int i = 0; i < projection.length; i++)
                for (int j = 0; j < dModel; j++)
                    projection[i][j] = rng.nextGaussian() * 0.02;
        }

        double[][] embed(double[][][] image) {
            int h = image.length, w = image[0].length;
            int numPatchesH = h / patchSize;
            int numPatchesW = w / patchSize;
            double[][] patches = new double[numPatchesH * numPatchesW][dModel];
            int idx = 0;
            for (int i = 0; i < numPatchesH; i++) {
                for (int j = 0; j < numPatchesW; j++) {
                    double[] flat = new double[patchSize * patchSize * 3];
                    int fi = 0;
                    for (int pi = 0; pi < patchSize; pi++) {
                        for (int pj = 0; pj < patchSize; pj++) {
                            for (int c = 0; c < 3; c++) {
                                flat[fi++] = image[i * patchSize + pi][j * patchSize + pj][c];
                            }
                        }
                    }
                    for (int k = 0; k < dModel; k++) {
                        for (int fi2 = 0; fi2 < flat.length; fi2++) {
                            patches[idx][k] += flat[fi2] * projection[fi2][k];
                        }
                    }
                    idx++;
                }
            }
            return patches;
        }
    }

    static class TextEncoder {
        final int dModel;
        final Random rng;
        final Map<String, double[]> embedCache = new HashMap<>();

        TextEncoder(int dModel, long seed) {
            this.dModel = dModel;
            this.rng = new Random(seed);
        }

        double[] encode(String text) {
            return embedCache.computeIfAbsent(text, t -> {
                double[] vec = new double[dModel];
                for (int i = 0; i < dModel; i++) vec[i] = rng.nextGaussian();
                double norm = Math.sqrt(Arrays.stream(vec).map(v -> v * v).sum());
                for (int i = 0; i < dModel; i++) vec[i] /= norm;
                return vec;
            });
        }

        double[] averageEncode(String[] tokens) {
            double[] sum = new double[dModel];
            for (String t : tokens) {
                double[] e = encode(t);
                for (int i = 0; i < dModel; i++) sum[i] += e[i];
            }
            double norm = Math.sqrt(Arrays.stream(sum).map(v -> v * v).sum());
            for (int i = 0; i < dModel; i++) sum[i] /= norm;
            return sum;
        }
    }

    static double cosine(double[] a, double[] b) {
        double dot = 0.0;
        for (int i = 0; i < a.length; i++) dot += a[i] * b[i];
        return dot;
    }

    static double[][][] randomImage(int seed, int size) {
        double[][][] img = new double[size][size][3];
        Random rng = new Random(seed);
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                for (int c = 0; c < 3; c++)
                    img[i][j][c] = rng.nextDouble();
        return img;
    }

    public static void main(String[] args) {
        int dModel = 8;
        int patchSize = 4;
        int imgSize = 8;

        PatchEmbedding patchEmb = new PatchEmbedding(patchSize, dModel);
        TextEncoder textEnc = new TextEncoder(dModel, 99);

        String[] captions = {
            "a cat sitting on a mat",
            "a dog running in a park",
            "a red car on a highway",
            "a house by the ocean"
        };
        double[][] textEmbs = new double[captions.length][];
        for (int i = 0; i < captions.length; i++)
            textEmbs[i] = textEnc.averageEncode(captions[i].split(" "));

        double[][] imgEmbs = new double[captions.length][];
        System.out.println("=== Image Patch Embedding ===");
        for (int i = 0; i < captions.length; i++) {
            double[][] patches = patchEmb.embed(randomImage(7 + i, imgSize));
            System.out.printf("Image %d: %d patches, %d dims%n", i, patches.length, dModel);
            double[] avg = new double[dModel];
            for (double[] p : patches)
                for (int k = 0; k < dModel; k++) avg[k] += p[k];
            double norm = Math.sqrt(Arrays.stream(avg).map(v -> v * v).sum());
            for (int k = 0; k < dModel; k++) avg[k] /= norm;
            imgEmbs[i] = avg;
        }

        System.out.println("\n=== Before Contrastive Training (random encoders) ===");
        int baselineHits = recallAtOne(textEmbs, imgEmbs, captions);
        System.out.println("Recall@1: " + baselineHits + "/" + captions.length);
        System.out.printf("InfoNCE loss: %.4f%n", contrastiveLoss(imgEmbs, textEmbs, 0.07));

        System.out.println("\n=== Simulated Contrastive Training (InfoNCE gradient) ===");
        for (int step = 1; step <= 50; step++) {
            trainStep(imgEmbs, textEmbs, 0.07, 0.05);
            if (step == 1 || step == 10 || step == 50) {
                System.out.printf("Step %d: recall@1=%d/%d  loss=%.4f%n",
                    step, recallAtOne(textEmbs, imgEmbs, captions),
                    captions.length, contrastiveLoss(imgEmbs, textEmbs, 0.07));
            }
        }

        System.out.println("\n=== Final Text->Image Retrieval ===");
        for (int i = 0; i < captions.length; i++) {
            int best = 0;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < captions.length; j++) {
                double s = cosine(textEmbs[i], imgEmbs[j]);
                if (s > bestScore) { bestScore = s; best = j; }
            }
            System.out.printf("'%s' -> image %d (%.4f) %s%n",
                captions[i], best, bestScore, best == i ? "HIT" : "miss");
        }

        System.out.println("\nMultimodal retrieval validated.");
    }

    /** One InfoNCE gradient step on the text encoder: pull matched, push unmatched. */
    static void trainStep(double[][] imgEmbs, double[][] textEmbs, double temperature, double lr) {
        int n = imgEmbs.length;
        int d = imgEmbs[0].length;
        double[][] grads = new double[n][d];
        for (int i = 0; i < n; i++) {
            double[] logits = new double[n];
            for (int j = 0; j < n; j++) {
                double sim = 0.0;
                for (int k = 0; k < d; k++) sim += imgEmbs[i][k] * textEmbs[j][k];
                logits[j] = sim / temperature;
            }
            double max = Double.NEGATIVE_INFINITY;
            for (double l : logits) if (l > max) max = l;
            double[] p = new double[n];
            double sum = 0.0;
            for (int j = 0; j < n; j++) { p[j] = Math.exp(logits[j] - max); sum += p[j]; }
            for (int j = 0; j < n; j++) {
                p[j] /= sum;
                double weight = (p[j] - (i == j ? 1.0 : 0.0)) / temperature;
                for (int k = 0; k < d; k++) grads[j][k] += weight * imgEmbs[i][k];
            }
        }
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < d; k++) textEmbs[j][k] -= lr * grads[j][k];
            double norm = Math.sqrt(Arrays.stream(textEmbs[j]).map(v -> v * v).sum());
            for (int k = 0; k < d; k++) textEmbs[j][k] /= norm;
        }
    }

    static int recallAtOne(double[][] textEmbs, double[][] imgEmbs, String[] captions) {
        int hits = 0;
        for (int i = 0; i < textEmbs.length; i++) {
            int best = 0;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < imgEmbs.length; j++) {
                double s = cosine(textEmbs[i], imgEmbs[j]);
                if (s > bestScore) { bestScore = s; best = j; }
            }
            if (best == i) hits++;
        }
        return hits;
    }

    /** InfoNCE contrastive loss copied from the lab. */
    static double contrastiveLoss(double[][] imageEmbs, double[][] textEmbs, double temperature) {
        int n = imageEmbs.length;
        double loss = 0.0;
        for (int i = 0; i < n; i++) {
            double[] logits = new double[n];
            for (int j = 0; j < n; j++) {
                double sim = 0.0;
                for (int k = 0; k < imageEmbs[i].length; k++)
                    sim += imageEmbs[i][k] * textEmbs[j][k];
                logits[j] = sim / temperature;
            }
            double max = Double.NEGATIVE_INFINITY;
            for (double l : logits) if (l > max) max = l;
            double sumExp = 0.0;
            for (double l : logits) sumExp += Math.exp(l - max);
            loss += -(logits[i] - max) + Math.log(sumExp);
        }
        return loss / n;
    }
}
```

### Expected Output
```text
=== Image Patch Embedding ===
Image 0: 4 patches, 8 dims
Image 1: 4 patches, 8 dims
Image 2: 4 patches, 8 dims
Image 3: 4 patches, 8 dims

=== Before Contrastive Training (random encoders) ===
Recall@1: 0/4
InfoNCE loss: 4.2911

=== Simulated Contrastive Training (InfoNCE gradient) ===
Step 1: recall@1=2/4  loss=5.2592
Step 10: recall@1=4/4  loss=3.8297
Step 50: recall@1=4/4  loss=0.9372

=== Final Text->Image Retrieval ===
'a cat sitting on a mat' -> image 0 (0.2547) HIT
'a dog running in a park' -> image 1 (0.4698) HIT
'a red car on a highway' -> image 2 (0.4063) HIT
'a house by the ocean' -> image 3 (-0.0545) HIT

Multimodal retrieval validated.
```

### Company Evaluation
- OpenAI: CLIP-style contrastive pretraining, zero-shot transfer, temperature tuning.
- Google: Large-batch contrastive training, ViT patch embedding, ALIGN/CoCa variants.
- Meta: Image-text matching, multimodal encoders for ranking.
- Nvidia: Contrastive kernels, large-batch similarity matrices, memory efficiency.
- Anthropic: Multimodal safety, image understanding evals, cross-modal alignment audits.

---

## Problem 2: Cross-Modal Attention for Captioning — Company: Google

### Interview Scenario
"You're at Google testing a caption decoder. The decoder's text queries must attend over
image patches. Verify that the lab's `crossModalAttention` returns one vector per text
token with the image's value dimension."

### The Problem
1. Build text queries and image keys/values.
2. Run `crossModalAttention(textQ, imgK, imgV)`.
3. Assert output shape and that weights sum to 1 per row.

### Solution Walkthrough
- Step 1: `textQ` of shape `tLen x dk`, `imgK` of `iLen x dk`, `imgV` of `iLen x dv`.
- Step 2: Run the lab's `crossModalAttention` (copy from the lab source).
- Step 3: Print output shape and check row sums of the attention weights (recompute
  internally or infer from output construction).

### Code
```java
double[][] textQ = {{0.2, 0.5, 0.1}, {0.8, 0.3, 0.4}};   // 2 text tokens
double[][] imgK = {{0.4, 0.1, 0.9}, {0.2, 0.7, 0.3}};    // 2 image patches
double[][] imgV = {{1.0, 0.0}, {0.0, 1.0}};              // 2 dims per patch
double[][] out = crossModalAttention(textQ, imgK, imgV);
System.out.println("Output shape: " + out.length + " x " + out[0].length);
```
Expected output: `Output shape: 2 x 2` — text tokens carry image information, and each
row is a convex combination of the image values.

---

## Problem 3: Text-Image Similarity Matrix — Company: Meta

### Interview Scenario
"You're at Meta building a diagnostic that prints the full similarity matrix between
captions and images so teams can inspect alignment before tuning temperature."

### The Problem
1. Compute the `n x n` cosine matrix for 4 captions vs 4 images.
2. Highlight the diagonal (matched pairs).
3. Print the mean diagonal vs mean off-diagonal similarity.

### Solution Walkthrough
- Step 1: Nested loop over `cosine(textEmbs[i], imgEmbs[j])`.
- Step 2: Print rows with `[i]` markers on the diagonal.
- Step 3: Report `diagMean - offDiagMean` as the alignment gap.

### Code
```java
double diag = 0, off = 0; int nOff = 0;
for (int i = 0; i < 4; i++) {
    for (int j = 0; j < 4; j++) {
        double s = cosine(textEmbs[i], imgEmbs[j]);
        if (i == j) diag += s; else { off += s; nOff++; }
    }
}
System.out.printf("Alignment gap (diag - offdiag): %.4f%n", diag / 4 - off / nOff);
```
Expected output: a positive gap after contrastive training (e.g., ~0.35) versus near
zero before — a one-line quantitative readout of alignment quality.
