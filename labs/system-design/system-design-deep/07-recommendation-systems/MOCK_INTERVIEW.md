# Mock Interview Transcript: Collaborative Filtering with Matrix Factorization

| Field | Detail |
|-------|--------|
| **Level** | Senior ML / Backend Engineer |
| **Duration** | 45 minutes |
| **Format** | Whiteboard + implementation |
| **Problem** | "Implement collaborative filtering with matrix factorization for a recommendation system. Users rate items; predict missing ratings and produce a top-N list." |

---

## Part 0: Scene Setting (2 minutes)

**Interviewer (I):** Our streaming service wants a "recommended for you" row. We
have explicit ratings — users rate shows 1-5 stars — and a big sparse matrix:
millions of users, hundreds of thousands of items, each user has rated dozens of
items. Implement collaborative filtering with matrix factorization: learn latent
factors, predict unrated cells, and serve a top-N list.

**Candidate (C):** Before the math, I need the product constraints, because they
decide the algorithm's shape and the serving architecture.

---

## Part 1: Clarifying Questions (5 minutes)

**C:** Four questions. One: is this offline (batch nightly) or online (learn
continuously)? Two: cold-start — a brand-new user has zero ratings; how important
is that path? Three: does the top-N need to be explainable, or is quality the
only metric? Four: what's the latency and freshness budget for serving?

**I:** Batch nightly is fine. Cold-start matters — new users must still get
*something*. Quality over explainability, but you should be able to say what the
model actually learned. Serving: top-100 for 100k requests/sec, fresh within a
day.

**C:** Good — those answers let me pick the standard tool: **latent factor
matrix factorization with SGD**, trained offline, served as precomputed
candidate sets. It fits: sparse explicit ratings, nightly refresh, quality
priority. Cold-start gets its own path (popularity/feature-based fallback), and
"what did the model learn" gets answered by inspecting factor values.

---

## Part 2: The Idea (7 minutes)

**C:** The premise: a user's taste can be summarized by a small vector — say 20-50
numbers — called their *latent factors*. An item's *character* is the same kind
of vector. A user's predicted rating for an item is the **dot product** of their
vector and the item's vector:

```
predicted(u, i) = U[u] · V[i] = Σ_f U[u][f] · V[i][f]
```

If a user likes action movies, their vector aligns with action movies' vectors;
the dot product is high. We never hand-author these vectors — we *learn* them
from the ratings. That's the whole trick: the observed ratings are the training
signal, and the factors are the compressed explanation.

**I:** And why does factorization beat, say, nearest-neighbor user-user CF?

**C:** Three reasons at this scale. (1) **Latent structure**: the factors capture
underlying taste dimensions — "action", "drama", "older films" — that
neighborhood methods can only approximate through explicit similarity. (2)
**Sparsity**: a user who has rated 20 items has no neighbors under cosine
distance on raw ratings, but still has well-defined factors shared through the
item side — factorization *shares statistical strength* across users. (3)
**Serving**: top-N via precomputed dot products is a sort, not a join; user-user
nearest-neighbor at millions of users is effectively an offline per-user search.

---

## Part 3: The Learning Algorithm — SGD (8 minutes)

**I:** Walk me through training.

**C:** We have the rating matrix `R` (mostly empty). We want `U` (users × f) and
`V` (items × f) such that `U·Vᵀ ≈ R` on the *observed* cells — we only train on
ratings that exist. The standard loss is squared error plus regularization:

```
L = Σ_{(u,i,r) in ratings} (r - U[u]·V[i])²  +  λ(||U[u]||² + ||V[i]||²)
```

**SGD** iterates over ratings one at a time and nudges the factors in the
negative-gradient direction:

```
err = r - U[u]·V[i]
U[u][f] += η · (err · V[i][f] - λ·U[u][f])
V[i][f] += η · (err · U[u][f] - λ·V[i][f])
```

**I:** What do the two knobs do?

**C:** `η` (learning rate) sets step size — too big overshoots and diverges, too
small crawls. `λ` (regularization) penalizes large factors — it's the
overfitting guard: with millions of parameters and sparse signal, unregularized
factors memorize the training ratings and predict garbage on unseen ones.
`f` (latent dimension count) is capacity: more factors = more expressiveness,
more data needed and more overfitting risk. The three form the tuning surface;
validation RMSE picks the operating point.

**I:** Bias terms?

**C:** Worth mentioning — real models add per-user bias, per-item bias, and a
global mean, because "this user rates generously" and "this item is generally
liked" are real effects the dot product can't express. `predicted = μ + b_u +
b_i + U[u]·V[i]`. I'll keep the demo to the core dot product for clarity but
flag the biases as the immediate production upgrade.

---

## Part 4: Evaluation (5 minutes)

**I:** How do you know it's working?

**C:** Two honest numbers. **RMSE on held-out ratings**: split ratings into
train/validate/test, train only on train, and report RMSE on test — a drop from
the baseline (predict the global mean) shows real signal. **Ranking quality**:
top-N is a ranking problem, so precision@k / recall@k on "did the user actually
watch a recommended item" beats pure RMSE for product impact. RMSE says the
model fits; ranking metrics say it *serves*. My demo prints RMSE before/after
training and a top-N list per user — plus a sanity check that predicted
ratings correlate with actual ones.

**I:** Overfitting detection?

**C:** Train RMSE dropping while validation RMSE rising is the classic sign —
that's when `λ` goes up or `f` comes down, or we add early stopping. With 100k
ratings and factors per user-item pair, it's easy to overfit; the validation
split is not optional.

---

## Part 5: Implementation Walkthrough (10 minutes)

**C:** (writing) The implementation: a `Rating` record, the `MatrixFactorization`
class holding `U` and `V` as 2D double arrays initialized small-random (so
symmetric rows don't learn identically), `train` iterating epochs over ratings
with the two update rules, `predict` as the dot product, `rmse` over a set, and
`topN(userId, n)` scoring every item and sorting — excluding items the user
already rated.

**I:** Your demo data?

**C:** A tiny explicit matrix — 3 users, 4 items, ratings that encode a taste
signal (e.g., user 0 likes item 0 and dislikes item 2). After ~100 epochs of
SGD, RMSE should drop well below the mean-prediction baseline, and `predict`
for an unrated cell should be plausible. The demo prints RMSE before/after,
per-user top-N, and the learned factor vectors — so we can *look* at what the
model learned, which answers your explainability question concretely.

---

## Part 6: Production Architecture (6 minutes)

**I:** Now the serving side — 100k req/s, fresh daily.

**C:** Three layers. **Offline**: the nightly job runs SGD on the full rating
log (in production, a distributed trainer — Spark/parameter-server style — since
SGD parallelizes by sharding ratings and synchronizing factors per epoch). The
output is `U` and `V`. **Candidate generation**: for each user, score all items
once, keep top-100, and store in a key-value store (Redis) — serving never
computes dot products online. **Ranking**: the request path reads the cached
top-100, applies freshness/contextual re-ranking (trending, locality, diversity),
and returns. The key insight: the *expensive* part is fully offline; the request
path is one cache read plus a sort of 100 items.

**I:** Cold start?

**C:** Separate path: new users get popularity-ranked items (with
diversity injection) until they rate ~5-10 items, then the factor model engages.
Similarly for new items — they ride on content-based features or popularity
until they accumulate ratings. The factorization never sees cold entities;
that's a deliberate boundary, not an afterthought.

**I:** Bias and retraining drift?

**C:** Daily retraining handles drift by construction; we monitor online CTR /
watch rate of recommended items vs the offline validation RMSE, and alert when
online metrics diverge from model expectations — that's the signal that the
data distribution moved. Matrix factorization is a *static* model; the
monitoring loop is what makes it production-safe.

---

## Part 7: Closing and Feedback (3 minutes)

**I:** Summarize.

**C:** Matrix factorization learns a compressed taste space — user and item
vectors whose dot product predicts ratings — trained by SGD on observed cells
with regularization as the overfitting guard. It wins over neighborhood methods
on sparse data because factors share statistical strength. In production, the
heavy math is offline; serving is precomputed top-N with a popularity cold-start
path, and monitoring closes the loop against drift.

**I:** Strong answer — the architecture split (offline learn, online serve) is
exactly right, and you justified the algorithm choice against the product
constraints. Two improvements: show the update equations on the board *before*
writing code (you did eventually), and mention implicit feedback (watch events,
not just stars) as the scale-out path — most production recommenders outgrow
explicit ratings.

---

## Evaluation Scorecard

| Dimension | Observation | Score (1-5) |
|-----------|-------------|-------------|
| Requirements | Latency, freshness, cold-start asked before math | 5 |
| Algorithm choice | Factorization vs neighborhood: latent structure, sparsity, serving | 5 |
| Training | SGD updates, η/λ/f role, overfitting, biases | 5 |
| Evaluation | Train/validate/test, RMSE + ranking metrics | 4 |
| Implementation | Dot-product predict, topN excluding rated, factor inspection | 5 |
| Production | Offline learn / online serve, Redis candidates, cold start, drift monitoring | 5 |
| Breadth | Implicit feedback mentioned only when prompted | 4 |

**Overall: Strong Hire** — the rare candidate who treats the algorithm and the
serving architecture as one design.

## Common Pitfalls Candidates Hit

- Treating matrix factorization as "just a library call" without the loss or
  the update rule.
- No validation split, then claiming good RMSE on training data.
- Serving dot products online at 100k req/s — the compute belongs offline.
- Forgetting cold start entirely, or pretending popularity is a factorization.
- Ignoring biases and regularization until prompted.
- No monitoring story for drift after nightly retraining.
