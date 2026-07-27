# PROBLEM WALKTHROUGH: RLHF Reward Model Training

## Problem Statement

**Difficulty: Hard | Category: RLHF / Alignment**

Implement a Bradley-Terry preference model for Reinforcement Learning from Human Feedback (RLHF). Given a dataset of preference pairs (chosen response > rejected response), train a reward model that scores any response such that preferred responses get higher scores.

**Interview Context:** RLHF is the core alignment technique behind ChatGPT, Claude, and Gemini. Interviewers want to understand the preference modeling framework, the Bradley-Terry loss, reward normalization, and how the reward model connects to PPO.

### Requirements

1. **Preference Data Model:** Process pairs of (prompt, chosen_response, rejected_response).
2. **Bradley-Terry Loss:** Implement the log-likelihood loss for pairwise preferences.
3. **Reward Model:** A scoring function r(prompt, response) → scalar reward.
4. **Training Loop:** Train the reward model on preference pairs with gradient descent.
5. **Reward Normalization:** Maintain running statistics for reward scaling.
6. **Evaluation:** Compute accuracy of preference prediction (correctly ranking chosen > rejected).

### Input/Output Contract

```
Input:  Dataset of triples (prompt, chosen, rejected),
        model architecture (hidden sizes), learning rate, epochs
Output: Trained reward model, preference prediction accuracy, reward distribution stats
```

---

## Step-by-Step Solution Walkthrough

### 1. The Bradley-Terry Model for Preferences

The Bradley-Terry model, adapted for RLHF (Ouyang et al., 2022), assumes that human preferences follow a latent utility scale. Given two responses `y_a` and `y_b` for the same prompt `x`:

```
P(y_a > y_b | x) = exp(r(x, y_a)) / (exp(r(x, y_a)) + exp(r(x, y_b)))
                  = σ(r(x, y_a) - r(x, y_b))
```

where `r(x, y)` is the reward model score and `σ` is the sigmoid function.

### 2. Loss Function

Given a dataset `D = {(x_i, y_c_i, y_r_i)}` where `y_c` is the chosen (preferred) response and `y_r` is the rejected response, the loss is the negative log-likelihood:

```
L(r) = -E[log P(y_c > y_r | x)]
     = -E[log σ(r(x, y_c) - r(x, y_r))]
```

Minimizing this loss pushes the reward model to assign higher scores to chosen responses.

### 3. Gradient Derivation

Let `δ = r(x, y_c) - r(x, y_r)`. The loss is `L = -log σ(δ)`.

∇L = -(1/σ(δ)) * σ(δ) * (1 - σ(δ)) * ∇δ
    = -(1 - σ(δ)) * ∇δ
    = (σ(δ) - 1) * ∇δ

Where:
- `∂L/∂r(x, y_c) = σ(δ) - 1` (negative if chosen is scored higher)
- `∂L/∂r(x, y_r) = 1 - σ(δ)` (positive if rejected is scored lower)

### 4. Reward Model Architecture

The reward model typically starts from a pre-trained language model and adds a linear layer on top of the final hidden state to produce a scalar reward:

```
r(x, y) = W · h_last + b
```

where `h_last` is the embedding of the last token (or [CLS] token) from the base model.

### 5. Reward Normalization

During training, rewards can drift arbitrarily. Normalization stabilizes PPO training:

```
r_normalized = (r - μ_r) / σ_r
```

where `μ_r` and `σ_r` are running statistics of the reward distribution. The reward model is typically trained with a reference model (KL penalty) to prevent it from hacking the scoring function.

### 6. Connection to PPO

The trained reward model is used as the signal for PPO (Proximal Policy Optimization):

```
R(x, y) = r(x, y) - β * KL(π_θ || π_ref)
```

The KL penalty prevents the policy from diverging too far from the reference model while maximizing reward.

---

## Java Implementation

```java
package com.llm.genai.deep.rlhf;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements a Bradley-Terry reward model for RLHF preference learning.
 * <p>
 * Trains a scoring function r(prompt, response) on pairwise preference data
 * such that preferred responses receive higher scores than rejected ones.
 * Supports reward normalization and preference prediction accuracy evaluation.
 */
public class RewardModel {

    private final int inputDim;
    private final int hiddenDim;
    private final double[][] W1;
    private final double[] b1;
    private final double[][] W2;
    private final double[] b2;
    private final double[] WOut;
    private final double bias;
    private double rewardMean;
    private double rewardStd;
    private int rewardCount;
    private final Random rng;

    /**
     * Represents a preference pair sample.
     */
    public static class PreferenceSample {
        public final double[] promptEmbedding;
        public final double[] chosenResponseEmbedding;
        public final double[] rejectedResponseEmbedding;

        public PreferenceSample(double[] promptEmb, double[] chosenEmb, double[] rejectedEmb) {
            this.promptEmbedding = promptEmb;
            this.chosenResponseEmbedding = chosenEmb;
            this.rejectedResponseEmbedding = rejectedEmb;
        }

        /**
         * Creates combined feature vector for prompt + response.
         */
        public double[] combinedFeatures(double[] responseEmb) {
            double[] combined = new double[promptEmbedding.length + responseEmb.length];
            System.arraycopy(promptEmbedding, 0, combined, 0, promptEmbedding.length);
            System.arraycopy(responseEmb, 0, combined, promptEmbedding.length, responseEmb.length);
            return combined;
        }
    }

    /**
     * Constructs a reward model with specified architecture.
     *
     * @param inputDim  dimensionality of combined prompt+response features
     * @param hiddenDim dimensionality of hidden layer
     */
    public RewardModel(int inputDim, int hiddenDim) {
        this.inputDim = inputDim;
        this.hiddenDim = hiddenDim;
        this.rng = new Random(42);

        // Initialize weights with Xavier/Glorot initialization
        this.W1 = new double[hiddenDim][inputDim];
        this.b1 = new double[hiddenDim];
        this.W2 = new double[hiddenDim][hiddenDim];
        this.b2 = new double[hiddenDim];
        this.WOut = new double[hiddenDim];
        this.bias = 0;

        double scale1 = Math.sqrt(2.0 / (inputDim + hiddenDim));
        double scale2 = Math.sqrt(2.0 / (hiddenDim + hiddenDim));
        double scaleOut = Math.sqrt(2.0 / (hiddenDim + 1));

        for (int i = 0; i < hiddenDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                W1[i][j] = rng.nextGaussian() * scale1;
            }
            for (int j = 0; j < hiddenDim; j++) {
                W2[i][j] = rng.nextGaussian() * scale2;
            }
            WOut[i] = rng.nextGaussian() * scaleOut;
        }

        this.rewardMean = 0.0;
        this.rewardStd = 1.0;
        this.rewardCount = 0;
    }

    /**
     * Scores a prompt-response pair, returning a scalar reward.
     *
     * @param features combined prompt+response embedding vector
     * @return scalar reward score
     */
    public double score(double[] features) {
        // Hidden layer 1 with ReLU
        double[] h1 = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            double sum = b1[i];
            for (int j = 0; j < inputDim; j++) {
                sum += W1[i][j] * features[j];
            }
            h1[i] = Math.max(0, sum); // ReLU
        }

        // Hidden layer 2 with ReLU
        double[] h2 = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            double sum = b2[i];
            for (int j = 0; j < hiddenDim; j++) {
                sum += W2[i][j] * h1[j];
            }
            h2[i] = Math.max(0, sum);
        }

        // Output layer: linear
        double reward = bias;
        for (int i = 0; i < hiddenDim; i++) {
            reward += WOut[i] * h2[i];
        }

        return reward;
    }

    /**
     * Computes the Bradley-Terry loss for a preference pair.
     *
     * @param sample the preference sample
     * @return negative log-likelihood loss
     */
    public double computeLoss(PreferenceSample sample) {
        double scoreChosen = score(sample.combinedFeatures(sample.chosenResponseEmbedding));
        double scoreRejected = score(sample.combinedFeatures(sample.rejectedResponseEmbedding));
        double delta = scoreChosen - scoreRejected;
        return -Math.log(sigmoid(delta));
    }

    /**
     * Backward pass computing gradients and updating weights for one sample.
     *
     * @param sample      the preference sample
     * @param learningRate step size for SGD
     * @return loss value for logging
     */
    public double trainStep(PreferenceSample sample, double learningRate) {
        // Forward pass for both chosen and rejected
        double[] featuresChosen = sample.combinedFeatures(sample.chosenResponseEmbedding);
        double[] featuresRejected = sample.combinedFeatures(sample.rejectedResponseEmbedding);

        // Cache activations for backprop
        ForwardCache cacheChosen = forwardWithCache(featuresChosen);
        ForwardCache cacheRejected = forwardWithCache(featuresRejected);

        double scoreChosen = cacheChosen.output;
        double scoreRejected = cacheRejected.output;
        double delta = scoreChosen - scoreRejected;
        double loss = -Math.log(sigmoid(delta));

        // Gradient of loss w.r.t. scores
        double gradChosen = sigmoid(delta) - 1.0; // negative if chosen > rejected
        double gradRejected = 1.0 - sigmoid(delta);

        // Backprop through the output layer and hidden layers for both
        backprop(cacheChosen, gradChosen, learningRate);
        backprop(cacheRejected, gradRejected, learningRate);

        return loss;
    }

    /**
     * Caches forward pass activations for backpropagation.
     */
    private static class ForwardCache {
        final double[] features;
        final double[] preAct1;
        final double[] h1;
        final double[] preAct2;
        final double[] h2;
        double output;

        ForwardCache(double[] features, int hiddenDim) {
            this.features = features;
            this.preAct1 = new double[hiddenDim];
            this.h1 = new double[hiddenDim];
            this.preAct2 = new double[hiddenDim];
            this.h2 = new double[hiddenDim];
        }
    }

    /**
     * Forward pass that caches activations for backprop.
     */
    private ForwardCache forwardWithCache(double[] features) {
        ForwardCache cache = new ForwardCache(features, hiddenDim);

        for (int i = 0; i < hiddenDim; i++) {
            double sum = b1[i];
            for (int j = 0; j < inputDim; j++) {
                sum += W1[i][j] * features[j];
            }
            cache.preAct1[i] = sum;
            cache.h1[i] = Math.max(0, sum);
        }

        for (int i = 0; i < hiddenDim; i++) {
            double sum = b2[i];
            for (int j = 0; j < hiddenDim; j++) {
                sum += W2[i][j] * cache.h1[j];
            }
            cache.preAct2[i] = sum;
            cache.h2[i] = Math.max(0, sum);
        }

        double output = bias;
        for (int i = 0; i < hiddenDim; i++) {
            output += WOut[i] * cache.h2[i];
        }
        cache.output = output;

        return cache;
    }

    /**
     * Backpropagates gradients through the network.
     */
    private void backprop(ForwardCache cache, double gradOutput, double lr) {
        // Gradient for output layer
        double[] gradH2 = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            gradH2[i] = gradOutput * WOut[i];
            WOut[i] -= lr * gradOutput * cache.h2[i];
        }
        bias -= lr * gradOutput;

        // Gradient for hidden layer 2 (ReLU)
        double[] gradPreAct2 = new double[hiddenDim];
        double[] gradH1 = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            gradPreAct2[i] = cache.preAct2[i] > 0 ? gradH2[i] : 0;
            for (int j = 0; j < hiddenDim; j++) {
                gradH1[j] += gradPreAct2[i] * W2[i][j];
                W2[i][j] -= lr * gradPreAct2[i] * cache.h1[j];
            }
            b2[i] -= lr * gradPreAct2[i];
        }

        // Gradient for hidden layer 1 (ReLU)
        double[] gradPreAct1 = new double[hiddenDim];
        for (int i = 0; i < hiddenDim; i++) {
            gradPreAct1[i] = cache.preAct1[i] > 0 ? gradH1[i] : 0;
            for (int j = 0; j < inputDim; j++) {
                W1[i][j] -= lr * gradPreAct1[i] * cache.features[j];
            }
            b1[i] -= lr * gradPreAct1[i];
        }
    }

    /**
     * Trains the reward model on a dataset of preference pairs.
     *
     * @param samples      list of preference samples
     * @param epochs       number of training epochs
     * @param learningRate step size
     */
    public void train(List<PreferenceSample> samples, int epochs, double learningRate) {
        System.out.println("Training reward model on " + samples.size() + " samples...");

        for (int epoch = 0; epoch < epochs; epoch++) {
            Collections.shuffle(samples, rng);
            double totalLoss = 0;
            int correct = 0;

            for (PreferenceSample sample : samples) {
                double loss = trainStep(sample, learningRate);
                totalLoss += loss;

                // Check preference prediction accuracy
                double scoreChosen = score(sample.combinedFeatures(sample.chosenResponseEmbedding));
                double scoreRejected = score(sample.combinedFeatures(sample.rejectedResponseEmbedding));
                if (scoreChosen > scoreRejected) correct++;
            }

            // Update reward statistics
            updateRewardStats(samples);

            double accuracy = (double) correct / samples.size();
            System.out.printf("Epoch %d: avg_loss=%.4f, accuracy=%.4f, "
                            + "reward_mean=%.2f, reward_std=%.2f%n",
                    epoch + 1, totalLoss / samples.size(), accuracy,
                    rewardMean, rewardStd);
        }
    }

    /**
     * Updates running reward normalization statistics.
     */
    private void updateRewardStats(List<PreferenceSample> samples) {
        double sum = 0, sumSq = 0;
        int count = 0;
        for (PreferenceSample sample : samples) {
            double rChosen = score(sample.combinedFeatures(sample.chosenResponseEmbedding));
            double rRejected = score(sample.combinedFeatures(sample.rejectedResponseEmbedding));
            sum += rChosen + rRejected;
            sumSq += rChosen * rChosen + rRejected * rRejected;
            count += 2;
        }
        rewardMean = sum / count;
        rewardStd = Math.sqrt(sumSq / count - rewardMean * rewardMean);
        rewardCount += count;
    }

    /**
     * Returns a normalized reward.
     *
     * @param reward raw reward score
     * @return normalized reward (z-score)
     */
    public double normalizeReward(double reward) {
        if (rewardStd == 0) return reward;
        return (reward - rewardMean) / rewardStd;
    }

    /**
     * Scores a prompt-response pair with normalization.
     *
     * @param sample the preference sample
     * @param useChosen whether to score the chosen or rejected response
     * @return normalized reward
     */
    public double evaluateResponse(PreferenceSample sample, boolean useChosen) {
        double[] emb = useChosen ? sample.chosenResponseEmbedding : sample.rejectedResponseEmbedding;
        double raw = score(sample.combinedFeatures(emb));
        return normalizeReward(raw);
    }

    /**
     * Sigmoid function.
     */
    private double sigmoid(double x) {
        if (x > 20) return 1.0;
        if (x < -20) return 0.0;
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Main method demonstrating reward model training on synthetic data.
     */
    public static void main(String[] args) {
        int promptDim = 32;
        int responseDim = 64;
        int inputDim = promptDim + responseDim;
        int hiddenDim = 128;
        int sampleCount = 2000;
        int epochs = 20;
        double learningRate = 0.001;

        RewardModel model = new RewardModel(inputDim, hiddenDim);
        Random rng = new Random(456);
        List<PreferenceSample> samples = new ArrayList<>();

        // Generate synthetic preference data
        // "Chosen" responses are engineered to have higher scores based on a hidden true reward
        double[] trueWeights = new double[inputDim];
        for (int i = 0; i < inputDim; i++) {
            trueWeights[i] = rng.nextGaussian() * 0.1;
        }

        for (int samp = 0; samp < sampleCount; samp++) {
            double[] promptEmb = new double[promptDim];
            for (int j = 0; j < promptDim; j++) promptEmb[j] = rng.nextGaussian();

            double[] chosenEmb = new double[responseDim];
            double[] rejectedEmb = new double[responseDim];
            for (int j = 0; j < responseDim; j++) {
                chosenEmb[j] = rng.nextGaussian();
                rejectedEmb[j] = rng.nextGaussian();
            }

            // Ensure chosen has higher true reward
            double[] combinedChosen = new double[inputDim];
            double[] combinedRejected = new double[inputDim];
            System.arraycopy(promptEmb, 0, combinedChosen, 0, promptDim);
            System.arraycopy(chosenEmb, 0, combinedChosen, promptDim, responseDim);
            System.arraycopy(promptEmb, 0, combinedRejected, 0, promptDim);
            System.arraycopy(rejectedEmb, 0, combinedRejected, promptDim, responseDim);

            double trueScoreChosen = 0, trueScoreRejected = 0;
            for (int j = 0; j < inputDim; j++) {
                trueScoreChosen += trueWeights[j] * combinedChosen[j];
                trueScoreRejected += trueWeights[j] * combinedRejected[j];
            }

            // If rejected accidentally has higher true score, swap them
            if (trueScoreChosen < trueScoreRejected) {
                double[] temp = chosenEmb;
                chosenEmb = rejectedEmb;
                rejectedEmb = temp;
            }

            samples.add(new PreferenceSample(promptEmb, chosenEmb, rejectedEmb));
        }

        model.train(samples, epochs, learningRate);

        // Evaluate on a held-out test set
        System.out.println("\n=== Test Evaluation ===");
        int testCorrect = 0;
        int testCount = 200;
        for (int i = 0; i < testCount; i++) {
            double[] promptEmb = new double[promptDim];
            for (int j = 0; j < promptDim; j++) promptEmb[j] = rng.nextGaussian();
            double[] chosenEmb = new double[responseDim];
            double[] rejectedEmb = new double[responseDim];
            for (int j = 0; j < responseDim; j++) {
                chosenEmb[j] = rng.nextGaussian();
                rejectedEmb[j] = rng.nextGaussian();
            }

            PreferenceSample testSample = new PreferenceSample(promptEmb, chosenEmb, rejectedEmb);
            double scoreChosen = model.score(testSample.combinedFeatures(chosenEmb));
            double scoreRejected = model.score(testSample.combinedFeatures(rejectedEmb));
            if (scoreChosen > scoreRejected) testCorrect++;
        }
        System.out.printf("Test accuracy: %.2f%% (%d/%d)%n",
                100.0 * testCorrect / testCount, testCorrect, testCount);
    }
}
```

---

## Complexity Analysis

### Time Complexity

- **Forward pass (single sample):** O(hiddenDim × (inputDim + hiddenDim)) for two hidden layers + output.
- **Backward pass:** Same as forward, O(hiddenDim × (inputDim + hiddenDim)).
- **Per sample training step:** O(hiddenDim × inputDim) combined.
- **Per epoch:** O(N × hiddenDim × inputDim) for N samples.
- **Total training:** O(epochs × N × hiddenDim × inputDim).

### Space Complexity

- **Model parameters:** O(hiddenDim × (inputDim + hiddenDim + 2) + hiddenDim) ≈ O(hiddenDim²).
- **Activations:** O(inputDim + hiddenDim) per sample during forward pass.
- **Gradients:** Same as parameters, O(hiddenDim²).
- **Total:** For inputDim=96, hiddenDim=128 → ~50K parameters. Negligible for modern hardware.

### Scaling to LLMs

In practice, the reward model is a full transformer (6B-70B parameters). The training cost is:
- 1-3 days on 8-64 GPUs for a 6B reward model.
- The Bradley-Terry loss adds negligible overhead (<1%) to the forward pass.
- Memory is dominated by the transformer backbone.

---

## Follow-Up Questions

### Q1: Why use the Bradley-Terry model instead of a pointwise regression model?

**Answer:** The Bradley-Terry pairwise approach has several advantages:
1. **No absolute ratings needed:** Humans are much better at comparing two responses than assigning absolute scores (like 1-5 stars). Inter-rater reliability for comparisons is ~0.8 vs ~0.3 for absolute ratings.
2. **Invariance to monotonic transformations:** The model only cares about the ordering, not the absolute scale. This matches how preferences work.
3. **Calibration:** The sigmoid output `P(chosen > rejected)` is naturally calibrated as a probability.
4. **No label bias:** Absolute ratings often have cultural or contextual biases (some annotators always give 3-5, others 1-3). Pairwise comparisons eliminate this.

### Q2: How do you prevent reward hacking (the reward model exploiting spurious patterns)?

**Answer:** Reward models can learn shortcuts that don't reflect true quality. Mitigations:
1. **KL regularization in PPO:** Penalize the policy for diverging from the reference model: `R = r(x,y) - β * KL(π_θ || π_ref)`. This prevents the policy from exploiting the reward model.
2. **Ensemble reward models:** Train multiple reward models on different data splits and use the minimum or mean score.
3. **Adversarial training:** During reward model training, include adversarial examples generated by a policy trying to hack the reward model.
4. **Regularization:** Add a penalty for extreme reward values during reward model training.
5. **Data diversity:** Ensure preference data covers many response types and failure modes.

### Q3: How does RLHF differ from DPO (Direct Preference Optimization)?

**Answer:** DPO (Rafailov et al., 2023) removes the need for a separate reward model:

**RLHF:**
1. Train a reward model on preference data.
2. Use PPO to optimize the policy against the reward model.
3. Requires maintaining 4 models (policy, reference, reward, value).

**DPO:**
1. Directly optimize the policy using preference data.
2. Derives the optimal policy as a function of the reference policy and the preference likelihood.
3. Loss: `L = E[-log σ(β * (log π_θ(y_c|x)/π_ref(y_c|x) - log π_θ(y_r|x)/π_ref(y_r|x)))]`

**Trade-offs:**
- DPO is simpler (no reward model, no PPO).
- RLHF can use unlabeled data (reward model scores any response, DPO needs pairs).
- RLHF's reward model provides a signal that can be analyzed and validated.
- DPO tends to be more stable in practice and requires less hyperparameter tuning.

### Q4: What is the role of the reference model in PPO training?

**Answer:** The reference model serves as an anchor:
1. **KL divergence penalty:** The policy is penalized for deviating from the reference model, preventing mode collapse and maintaining diversity.
2. **Gradient signal:** The ratio `π_θ(y|x) / π_ref(y|x)` is used in the PPO objective to re-weight rewards for actions that are more/less likely under the current policy.
3. **Safety:** Without a reference, the policy could learn to output garbage text that somehow scores high on the reward model. The KL penalty ensures outputs remain human-readable.

### Q5: How do you evaluate reward model quality?

**Answer:** Beyond preference prediction accuracy:
1. **Rank correlation (Spearman's ρ, Kendall's τ):** Compare model rankings with human rankings on held-out data.
2. **Calibration:** Does `P(chosen > rejected)` match empirical frequency?
3. **Robustness to adversarial inputs:** Does the reward model correctly score clearly bad responses (empty, repetitive, offensive)?
4. **Reward distribution:** Is the reward spread reasonable? Very narrow distributions mean the model can't discriminate; very wide means it's exploiting spurious features.
5. **Downstream task performance:** Ultimately, does PPO with this reward model improve the quality of the final policy? This is the gold standard.

---

## Test Cases

### Test Case 1: Perfect Preference Prediction After Training

```
Input: Synthetic data where chosen response always has higher hidden true reward.
Training: 1000 samples, 50 epochs.
Expected: Training accuracy approaches 100%. Test accuracy > 90%.
```

### Test Case 2: Loss is Zero When Scores are Equal

```
Input: chosenEmb == rejectedEmb (identical embeddings)
Forward: scoreChosen == scoreRejected, delta = 0
Loss: -log(sigmoid(0)) = -log(0.5) = 0.6931

Expected: computeLoss() returns ≈ 0.6931
```

### Test Case 3: Reward Normalization

```
Input: After training, feed a test sample.
Output: evaluateResponse() returns a normalized reward.
Expected: Normalized rewards have mean ≈ 0, std ≈ 1 (approximately, over a large batch).
```

### Test Case 4: Preference Reversal

```
Input: ("What is 2+2?", "4", "5") 
If trained on similar data, reward(4) > reward(5).
After swapping labels: ("What is 2+2?", "5", "4")
Expected: After retraining, reward(5) > reward(4). The model adapts to preference direction.
```

### Test Case 5: Sigmoid Invariance

```
Input: Two scores s1 = 5.0, s2 = 3.0
P(chosen=1) = sigmoid(5-3) = sigmoid(2) = 0.881
P(chosen=2) = sigmoid(3-5) = sigmoid(-2) = 0.119
Total = 1.0

Expected: Probabilities are complementary and sum to 1.0.
```

### Test Case 6: Initial State (Before Training)

```
Input: Freshly initialized reward model, random data.
Forward: W1, W2, WOut are random.
Expected: Initial accuracy ≈ 50% (random guessing). Initial loss ≈ 0.693 (log(2)).
This confirms the model starts unbiased.
```

---

## Summary

This walkthrough implemented a Bradley-Terry reward model for RLHF with:
1. **Pairwise preference modeling** using the Bradley-Terry formulation `P(chosen > rejected) = σ(r_chosen - r_rejected)`.
2. **Negative log-likelihood loss** for training the reward model to rank preferred responses higher.
3. **Two-layer neural network architecture** for scoring prompt-response pairs.
4. **Reward normalization** with running statistics for PPO compatibility.
5. **Preference prediction accuracy** as an evaluation metric.

The key insight is that RLHF converts the inherently human task of pairwise comparison into a differentiable loss function for learning a reward signal, which then guides the policy optimization toward aligned behavior.