# Mental Models for Probability

## The Venn Diagram

Events are sets in a sample space. $P(A \cup B) = P(A) + P(B) - P(A \cap B)$.

## The Tree of Outcomes

Each branching point represents a random choice. Multiply along branches, sum across leaves:

```
        / H (0.5) → HH (0.25)
   / H (0.5)
  |         \ T (0.5) → HT (0.25)
  |
  |         / H (0.5) → TH (0.25)
   \ T (0.5)
            \ T (0.5) → TT (0.25)
```

## The Law of Large Numbers

As sample size grows, the sample average converges to the expected value. $\frac{1}{n}\sum X_i \to \mathbb{E}[X]$.

## The Central Limit Theorem

Sum of many independent random variables is approximately normal, regardless of their original distribution.

## Bayes' Theorem (the Learning Engine)

$$
P(H \mid E) = \frac{P(E \mid H) \cdot P(H)}{P(E)}
$$

Prior belief → update with evidence → posterior belief.

## The Expected Value

The long-run average: $\mathbb{E}[X] = \sum_x x \cdot P(X = x)$.
