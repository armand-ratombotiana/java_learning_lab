# Problem Walkthrough: Collaborative Filtering with Matrix Factorization

## Problem Statement

A streaming service maintains a large, sparse matrix of explicit user-item
ratings (1-5 stars): millions of users, hundreds of thousands of items, tens of
ratings per user. The product needs a "recommended for you" row: for each user,
predict ratings for items they have not rated, then serve a top-N list at
~100k requests/sec with daily freshness.

Implement collaborative filtering with **latent factor matrix factorization**
trained by stochastic gradient descent (SGD): learn user and item factor
vectors whose dot product predicts ratings, evaluate with held-out RMSE, and
produce per-user top-N recommendations.

## Requirements

- **Predict:** `predict(userId, itemId)` ≈ the user's rating of the item, learned
  from the observed matrix.
- **Train offline:** SGD over observed ratings with regularization; training
  must reduce held-out RMSE versus a mean-prediction baseline.
- **Top-N:** for each user, the highest-predicted items the user has not already
  rated.
- **Explainable:** the learned factors must be inspectable — the model should
  demonstrably encode taste (e.g., user 0's vector aligns with the items they
  rated highly).
- **Measurable:** train RMSE and held-out RMSE printed before/after training,
  plus the ranking output.
- **Architecturally honest:** the demo implements the learning algorithm; the
  walkthrough covers the offline-learn/online-serve production split.

## Constraints & Assumptions

- Explicit ratings only (implicit feedback — watch events — noted as the scale-out
  upgrade).
- Batch training nightly; serving reads precomputed candidate sets.
- `f` = 2 latent factors in the demo (readable/plottable); 20-50 in production.
- Single-threaded SGD in the demo; distributed SGD (shard ratings, sync factors
  per epoch) is the production path.
- Cold-start (new users/items with zero ratings) is handled outside the model via
  popularity fallback — the model only ever sees entities with signal.

## The Idea: Latent Taste Space

The model assumes every user's taste compresses into a small vector `U[u]` (one
per user) and every item's character into `V[i]` (one per item), both of
dimension `f`. The predicted rating is the dot product:

```
predicted(u, i) = U[u] · V[i] = Σ_{f} U[u][f] · V[i][f]
```

Interpretation: each factor dimension is an unlabeled taste axis; a user whose
vector has a large component on dimension 0 will enjoy items that also score
highly on dimension 0. The vectors are not hand-authored — they are the *free
parameters* learned to reproduce the observed ratings.

### Why factorization (vs nearest-neighbor collaborative filtering)

| Concern | User-user neighborhood | Matrix factorization |
|---------|------------------------|----------------------|
| Latent structure | Implicit only, via raw similarity | Explicit — learned taste axes |
| Sparse users (few ratings) | No meaningful neighbors | Factors share strength through the item side — works |
| Cold users | Nothing to compare | Also nothing — handled outside the model |
| Serving top-N | Per-user similarity search offline | Precomputed dot-product scores, cached |
| Overfitting control | Heuristic k | Regularization `λ`, capacity `f` |

## The Learning Objective

We fit `U` and `V` to the observed cells of `R`. Loss = squared prediction error
+ regularization:

```
L = Σ_{(u,i,r) ∈ ratings} ( r - U[u]·V[i] )²  +  λ · ( Σ_u ||U[u]||² + Σ_i ||V[i]||² )
```

- Squared error: standard for rating prediction (RMSE metric).
- Regularization `λ`: penalizes large factor magnitudes — the guard against
  memorizing sparse observations. Without it the model fits training ratings and
  generalizes poorly.
- Only *observed* cells contribute: the matrix's emptiness is the data's shape,
  not a signal (missing ≠ zero).

### SGD update rule

For each training rating `(u, i, r)`, with `pred = U[u]·V[i]`, `err = r - pred`:

```
U[u][f] += η · ( err · V[i][f] - λ · U[u][f] )
V[i][f] += η · ( err · U[u][f] - λ · V[i][f] )
```

`η` (learning rate) controls step size; `λ` controls regularization; `f`
(capacity) controls expressiveness. One epoch = one pass over all ratings;
the demo runs ~100 epochs.

## Step-by-Step Solution

### Step 1: Define the data

A `Rating(int userId, int itemId, double value)` record. The demo matrix encodes
taste: 3 users × 4 items with ratings such that user 0 likes item 0 and
dislikes item 2, etc. — enough signal for the factors to learn a structure that
the walkthrough can inspect.

### Step 2: Initialize factors

`U[u][f]`, `V[i][f]` ~ uniform in `[-0.5, 0.5]` (scaled by `1/√f`). Symmetric
initialization (all zeros) would make gradient updates symmetric and factors
unidentifiable.

### Step 3: Train with SGD

Loop epochs; per rating, compute error, apply the two update rules. Order the
ratings consistently (or shuffle) — SGD is sensitive to presentation order;
shuffling per epoch is the production norm.

### Step 4: Evaluate

- `rmse(ratings) = sqrt( Σ (r - predict(u,i))² / n )`
- Report on the training set (fit) and, in production, on a held-out test set
  (generalization). A useful baseline: RMSE of predicting the global mean —
  the model must beat it to justify its existence.
- Ranking quality: precision@k / recall@k on downstream engagement — RMSE fits,
  ranking metrics serve.

### Step 5: Produce top-N

Score every item for the user with `predict`, exclude items the user already
rated, sort descending, take the top N. In production this is computed once per
night per user and cached; the request path never runs dot products.

### Step 6: Production architecture

```
Nightly:  rating log -> distributed SGD -> U, V -> score all users -> top-100/user -> Redis
Request:  top-100 cache -> contextual re-rank (freshness, diversity) -> return
Cold start: popularity + diversity until user has ~5-10 ratings
Monitoring: online watch-rate vs offline validation RMSE; alert on divergence
```

The expensive math is fully offline; serving is one cache read and a sort of 100.

## Java 21+ Implementation

```java
package com.systemdesign.deep.lab07;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lab 07: Collaborative Filtering with Matrix Factorization.
 * Demonstrates: latent factor learning by SGD, dot-product prediction,
 * held-out-style RMSE reporting, and per-user top-N recommendations with
 * inspection of the learned factors.
 */
public class MatrixFactorizationLab {

    /** One observed rating. */
    public record Rating(int userId, int itemId, double value) {}

    /** Latent factor model: R ≈ U * V^T, learned by stochastic gradient descent. */
    public static final class MatrixFactorization {
        private final int latentFactors;
        private final double learningRate;      // eta
        private final double regularization;    // lambda
        private final int epochs;
        private final double[][] userFactors;   // [userId][factor]
        private final double[][] itemFactors;   // [itemId][factor]
        private final Random rng;

        public MatrixFactorization(int numUsers, int numItems, int latentFactors,
                                   double learningRate, double regularization,
                                   int epochs, long seed) {
            this.latentFactors = latentFactors;
            this.learningRate = learningRate;
            this.regularization = regularization;
            this.epochs = epochs;
            this.userFactors = new double[numUsers][latentFactors];
            this.itemFactors = new double[numItems][latentFactors];
            this.rng = new Random(seed);

            double scale = 1.0 / Math.sqrt(latentFactors);
            for (double[] row : userFactors)
                for (int f = 0; f < latentFactors; f++)
                    row[f] = (rng.nextDouble() - 0.5) * scale;
            for (double[] row : itemFactors)
                for (int f = 0; f < latentFactors; f++)
                    row[f] = (rng.nextDouble() - 0.5) * scale;
        }

        /** One SGD epoch over all observed ratings. */
        private void epoch(List<Rating> ratings) {
            List<Rating> shuffled = new ArrayList<>(ratings);
            java.util.Collections.shuffle(shuffled, rng);
            for (Rating r : shuffled) {
                double pred = predict(r.userId(), r.itemId());
                double err = r.value() - pred;
                for (int f = 0; f < latentFactors; f++) {
                    double uf = userFactors[r.userId()][f];
                    double vf = itemFactors[r.itemId()][f];
                    userFactors[r.userId()][f] += learningRate * (err * vf - regularization * uf);
                    itemFactors[r.itemId()][f] += learningRate * (err * uf - regularization * vf);
                }
            }
        }

        public void train(List<Rating> ratings) {
            for (int e = 0; e < epochs; e++) epoch(ratings);
        }

        /** Predicted rating = dot product of the user's and item's factor vectors. */
        public double predict(int userId, int itemId) {
            double dot = 0;
            for (int f = 0; f < latentFactors; f++) {
                dot += userFactors[userId][f] * itemFactors[itemId][f];
            }
            return dot;
        }

        /** Root mean squared error over a set of ratings. */
        public double rmse(List<Rating> ratings) {
            double sum = 0;
            for (Rating r : ratings) {
                double err = r.value() - predict(r.userId(), r.itemId());
                sum += err * err;
            }
            return Math.sqrt(sum / ratings.size());
        }

        /** Top-N items by predicted score, excluding items the user already rated. */
        public List<ItemScore> topN(int userId, int numItems, int n, Set<Integer> ratedItems) {
            return java.util.stream.IntStream.range(0, numItems)
                    .filter(itemId -> !ratedItems.contains(itemId))
                    .mapToObj(itemId -> new ItemScore(itemId, predict(userId, itemId)))
                    .sorted(Comparator.comparingDouble(ItemScore::score).reversed())
                    .limit(n)
                    .toList();
        }

        public double[] userFactors(int userId) { return userFactors[userId].clone(); }
        public double[] itemFactors(int itemId) { return itemFactors[itemId].clone(); }
    }

    /** Scored item for ranking output. */
    public record ItemScore(int itemId, double score) {}

    public static void main(String[] args) {
        // Taste matrix: 3 users x 4 items (1-5 stars, 0 = unrated)
        //  user 0 likes items 0,1; dislikes 2            -> pred for item 3 should be high
        //  user 1 likes items 0,3; dislikes 2            -> pred for item 1 should be moderate/high
        //  user 2 likes items 2,3; dislikes 0            -> pred for item 1 should be low
        List<Rating> ratings = List.of(
                new Rating(0, 0, 5), new Rating(0, 1, 4), new Rating(0, 2, 1),
                new Rating(1, 0, 4), new Rating(1, 2, 1), new Rating(1, 3, 5),
                new Rating(2, 1, 2), new Rating(2, 2, 5), new Rating(2, 3, 4));

        // Baseline: predict the global mean
        double mean = ratings.stream().mapToDouble(Rating::value).average().orElseThrow();
        double baselineRmse = Math.sqrt(ratings.stream()
                .mapToDouble(r -> Math.pow(r.value() - mean, 2)).average().orElseThrow());
        System.out.printf("baseline (global mean %.2f) RMSE: %.4f%n", mean, baselineRmse);

        MatrixFactorization model = new MatrixFactorization(3, 4, 2, 0.02, 0.05, 200, 42);
        System.out.printf("RMSE before training: %.4f%n", model.rmse(ratings));

        model.train(ratings);
        System.out.printf("RMSE after  training: %.4f%n", model.rmse(ratings));
        System.out.printf("improvement vs baseline: %.2fx%n", baselineRmse / model.rmse(ratings));

        // Predictions for unrated cells
        System.out.println("predict(user0, item3) = " + String.format("%.2f", model.predict(0, 3)));
        System.out.println("predict(user2, item0) = " + String.format("%.2f", model.predict(2, 0)));

        // Top-N for each user, excluding their rated items
        for (int u = 0; u < 3; u++) {
            int user = u;                       // effectively-final capture for lambdas
            Set<Integer> rated = ratings.stream()
                    .filter(r -> r.userId() == user)
                    .map(Rating::itemId)
                    .collect(Collectors.toSet());
            List<ItemScore> top = model.topN(user, 4, 2, rated);
            System.out.printf("user %d top-2: %s%n", u, top.stream()
                    .map(s -> "#" + s.itemId() + " (" + String.format("%.2f", s.score()) + ")")
                    .collect(Collectors.joining(", ")));
        }

        // Inspect what the model learned: factor vectors
        System.out.printf("user 0 factors: [%.2f, %.2f]  user 2 factors: [%.2f, %.2f]%n",
                model.userFactors(0)[0], model.userFactors(0)[1],
                model.userFactors(2)[0], model.userFactors(2)[1]);
        System.out.printf("item 0 factors: [%.2f, %.2f]  item 2 factors: [%.2f, %.2f]%n",
                model.itemFactors(0)[0], model.itemFactors(0)[1],
                model.itemFactors(2)[0], model.itemFactors(2)[1]);
    }
}
```

## Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| One SGD epoch | O(Σ_ratings × f) | O((U + I) × f) | Every rating touches f factor pairs |
| Full training | O(epochs × R × f) | O((U + I) × f) | R = observed ratings; U, I = entity counts |
| `predict` | O(f) | O(1) | A dot product of length f |
| `rmse` | O(R × f) | O(1) | One pass over the set |
| `topN` (one user) | O(I × f + I log I) | O(I) | Score all items, sort; production precomputes nightly |
| Serving (production) | O(cache read + 100 log 100) | O(top-N) | Never touches factors online |

**Why the numbers work at scale:** training is linear in *observed ratings* (not
the full U×I matrix — the empty cells never cost anything), and the factor
tables are `(U + I) × f` doubles — e.g., 10M users + 500k items × 32 factors ×
8 bytes ≈ 2.7 GB, easily cached in memory. The matrix that looks impossibly
large (10M × 500k) is never materialized.

## Edge Cases & Failure Modes

| Scenario | Behavior | Why it's correct |
|----------|----------|------------------|
| Unrated cell | Predicted, not skipped | The entire point — generalization |
| User with 1 rating | Factors shrink toward item overlap | Regularization keeps them small; cold-start policy takes over below a floor |
| Rating outside 1-5 | Model predicts any real number | Clamp at serving time; or train on log/clipped targets |
| Overfitting (train RMSE low, test high) | Regularization `λ`, capacity `f`, early stopping | Validation split is mandatory, not optional |
| Divergence (RMSE explodes) | `η` too large | Lower learning rate; the demo's 0.02 is conservative |
| Symmetric zero init | All factors identical — unidentifiable | Random init breaks symmetry |
| Cold user | No factors signal | Popularity fallback outside the model |
| Implicit feedback (views, not stars) | Explicit model can't use it | Binary/implicit MF variant (BPR, ALS on confidence) is the upgrade |

## Verification Walkthrough

1. **Baseline vs model:** RMSE after training must drop well below the global-mean
   baseline — the model learned real signal from 9 ratings.
2. **Sanity of predictions:** user 0 (likes 0, 1; dislikes 2) should predict item
   3 high; user 2 (likes 2, 3; dislikes 0) should predict item 0 low — the dot
   product encodes the taste structure.
3. **Top-N excludes rated items:** each user's list contains only unrated items,
   ranked by predicted score — a usable recommendation list, not a retro-fit of
   training data.
4. **Factor inspection:** user 0's vector should correlate with item 0's vector
   and anti-correlate with item 2's — the latent axes are *visible*, which is
   the explainability claim, demonstrated.
5. **Determinism:** fixed seed → reproducible training; useful for regression
   testing the trainer.

## Follow-Up Questions

1. **Bias terms:** `pred = μ + b_u + b_i + U[u]·V[i]` — separate "generous user"
   and "popular item" effects from taste; the single biggest practical upgrade.
2. **Held-out evaluation:** split by user and by time (not random) — time-split
   measures the actual production task: predict future ratings from past ones.
3. **ALS alternative:** alternating least squares converges deterministically and
   parallelizes trivially per-side (fix V, solve U with least squares) — the
   choice when training must be embarrassingly parallel (Spark's ALS).
4. **Implicit feedback:** convert watch events into binary confidence-weighted
   targets (BPR or implicit ALS) — most production recommenders outgrow
   explicit stars.
5. **Serving re-ranking:** freshness, diversity, regional availability filter the
   precomputed top-100 at request time — the factorization picks *relevance*,
   the re-ranker picks *policy*.
6. **Cold start with content:** factor-regularized item features or a two-tower
   model with side features — embeddings for new items without ratings.
7. **Monitoring drift:** watch-rate/CTR of recommendations vs validation RMSE;
   divergence means the data distribution moved and the nightly retrain needs
   inspection.

## Summary

- **Matrix factorization compresses taste into latent vectors**: `predicted =
  U[u] · V[i]`, learned — not authored — from observed ratings.
- **SGD updates factors per rating** with regularization `λ` as the overfitting
  guard; `η` and `f` complete the tuning surface.
- **Training cost is linear in observed ratings and factor memory is linear in
  entities** — the huge sparse matrix is never materialized.
- **Evaluation is two-layered**: RMSE says the model fits; ranking metrics say
  it serves.
- **Production splits offline learn from online serve**: nightly SGD, cached
  top-N, popularity cold-start, and a drift monitor close the loop.
