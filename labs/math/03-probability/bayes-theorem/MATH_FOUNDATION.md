# Mathematical Foundation of Bayes' Theorem

## 📐 The Formula
Bayes' Theorem mathematically describes the probability of an event, based on prior knowledge of conditions that might be related to the event.

$$ P(A|B) = \frac{P(B|A) \cdot P(A)}{P(B)} $$

Where:
- **$P(A|B)$ (Posterior)**: The probability of hypothesis $A$ being true, given that evidence $B$ has occurred.
- **$P(B|A)$ (Likelihood)**: The probability of observing evidence $B$, given that hypothesis $A$ is true.
- **$P(A)$ (Prior)**: The probability of hypothesis $A$ being true *before* observing any evidence.
- **$P(B)$ (Marginal / Evidence)**: The total probability of observing evidence $B$ under all possible hypotheses.

## 🧮 Solving the Disease Example
Let's plug the numbers from the `THEORY.md` example into the formula.
- $A$ = You have the disease.
- $B$ = You tested positive.

We know:
- $P(A) = 0.01$ (Prior: 1% of population has it).
- $P(B|A) = 0.99$ (Likelihood: 99% true positive rate).
- $P(\neg A) = 0.99$ (99% of population does not have it).
- $P(B|\neg A) = 0.01$ (1% false positive rate for healthy people).

First, calculate the total probability of testing positive $P(B)$ using the Law of Total Probability:
$$ P(B) = P(B|A)P(A) + P(B|\neg A)P(\neg A) $$
$$ P(B) = (0.99 \times 0.01) + (0.01 \times 0.99) = 0.0099 + 0.0099 = 0.0198 $$

Now, apply Bayes' Theorem:
$$ P(A|B) = \frac{0.99 \times 0.01}{0.0198} = \frac{0.0099}{0.0198} = 0.50 $$

Exactly **50%**.

## 🤖 Naive Bayes Classifier
In Machine Learning, we use Bayes' Theorem for classification. If we have a dataset of emails and we want to classify a new email as Spam ($A$) based on the words it contains ($B_1, B_2, \dots, B_n$).

$$ P(\text{Spam} | \text{Words}) = \frac{P(\text{Words} | \text{Spam}) \cdot P(\text{Spam})}{P(\text{Words})} $$

**Why "Naive"?**
Calculating $P(B_1, B_2, \dots, B_n | A)$ exactly is computationally impossible because words depend on each other. The algorithm makes a "naive" assumption: it assumes every feature (word) is completely independent of every other feature.

This allows us to simplify the math to a simple chain of multiplications:
$$ P(\text{Words} | \text{Spam}) = P(B_1 | \text{Spam}) \times P(B_2 | \text{Spam}) \times \dots \times P(B_n | \text{Spam}) $$