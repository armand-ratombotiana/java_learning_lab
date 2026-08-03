# Lab 10: Mock Interview — Model Evaluation

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Confusion matrix, precision/recall/F1, ROC/AUC, k-fold CV, bias-variance, class imbalance

---

**Interviewer**: "Walk me through the confusion matrix implementation in this
lab."

**Candidate**: "`confusionMatrix(actual, predicted)` walks both arrays in lockstep
and tallies four cells: actual 1 predicted 1 is TP, actual 0 predicted 1 is FP,
actual 1 predicted 0 is FN, and anything else is TN. The demo returns TP=3 FP=1
FN=2 TN=4 on 10 predictions. The important discipline: everything else is derived
from these four numbers — `accuracy = (TP+TN)/N`, `precision = TP/(TP+FP)`,
`recall = TP/(TP+FN)`, `f1 = 2PR/(P+R)` — and the lab's helper methods each take
the relevant cells, so the derived metrics can never disagree with the matrix.
That's the interview point: a confusion matrix isn't one more chart, it's the
ledger every metric is read from."

**Interviewer**: "The demo prints Acc=0.70, Prec=0.75, Rec=0.60, F1=0.67. What
do those four numbers tell you together?"

**Candidate**: "They tell different stories. Accuracy 0.70 says the model is
right 70% of the time overall. Precision 0.75 says of the 4 alarms it raised, 3
were real fraud — 1 false positive. Recall 0.60 says of the 5 real fraud cases,
it caught 3 and missed 2 — those 2 are the expensive ones, because missed fraud
is direct loss. F1 0.67 is the harmonic mean, a single number that punishes
either side collapsing. The gap between recall and precision is the real
content: this model is more conservative than the business needs for fraud,
and the fix is the threshold — the scores behind these predictions allow
trading precision for recall without retraining."

**Interviewer**: "Explain the AUC computation in the code, including the
tie-handling."

**Candidate**: "The lab's `auc` sorts scores descending and sweeps the
threshold through them. It tracks cumulative TPR = (positives seen so far)/
total positives and FPR = (negatives seen so far)/total negatives — that sweep
traces the ROC curve. The area is accumulated with the trapezoidal rule:
`area += (fpr - prevFpr) * (tpr + prevTpr) / 2`. The tie handling is the subtle
part: `if (i == n-1 || scores[idx[i]] != scores[idx[i+1]])` — when several
samples share a score, the code processes them all before closing the
trapezoid, which averages the TPR/FPR across the tied group and avoids a
zig-zag area. The demo's AUC of 0.9600 means a random positive scores above a
random negative 96% of the time — a strong ranking."

**Interviewer**: "What's the interpretation of AUC, and why is it a threshold-
independent metric?"

**Candidate**: "AUC is the probability that a randomly chosen positive sample
ranks above a randomly chosen negative one — it's the Mann-Whitney statistic.
It's threshold-independent because it never commits to a cut: it sweeps all
thresholds and summarizes the whole ROC curve. That's its strength and its
trap. The walkthrough's second problem shows the trap beautifully: a model can
have AUC 1.0 — perfect ranking — while its predictions at the 0.5 threshold
score 0.50 accuracy, because the operating point was chosen wrong. AUC tells
you the model has the right ordering; it tells you nothing about where to cut,
which is a business decision made from precision-recall."

**Interviewer**: "When is AUC a poor metric, as the INTERVIEW doc says?"

**Candidate**: "With high class imbalance. With 0.1% positives, a model that
ranks most positives moderately well gets a great AUC — 0.9+ — while its
precision at any useful recall is terrible, because the huge negative mass
swamps the threshold sweep. The fix is the precision-recall curve: PR focuses
on the positive class, so the numbers that matter for fraud and spam don't get
diluted by millions of negatives. The demo's fraud story is the canonical
example: in production I'd report AUC as a ranking summary and decide on PR
curves — and I'd never let a 0.96 AUC alone approve a model, because it says
nothing about false-positive volume at serving thresholds."

**Interviewer**: "Walk me through the k-fold cross-validation code."

**Candidate**: "`crossVal(X, y, k, rng)` shuffles the indices with a seeded
`Collections.shuffle`, then divides into k contiguous blocks with
`foldSize = n / k` — the last fold absorbs the remainder via
`end = (fold == k-1) ? n : ...`. Each fold becomes the test set
(`testIdx` as a `HashSet` for O(1) membership), the rest trains, and the code
plugs a dummy classifier — majority class of the training fold — into every
split, recording accuracy per fold. The output is the fold accuracies array,
and the demo reports mean ± std: 0.35 ± 0.255 on 20 random points. The
std is the honest part: on 20 samples with 5 folds, each fold has 4 points,
so the estimate is noisy — small data, wide intervals."

**Interviewer**: "Why is the demo's 5-fold CV result 0.35, not ~0.5?"

**Candidate**: "Because the data is random and the baseline is a majority
classifier. Each training fold is a random sample of 16 points; the majority
class in it predicts a constant for its 4 test points, and with 20 random
labels the fold accuracies land between 0.25 and 0.5 — the mean comes out 0.35
with std 0.255. There are two lessons. First, 0.35 is not 'worse than random
guessing' — it's the *majority-class baseline* on a coin-flip problem, and
reporting a model's CV score without its baseline is meaningless. Second, the
large std warns you: a single 80/20 split could have produced any of these
numbers, which is exactly why cross-validation exists instead of one train-test
split."

**Interviewer**: "How do you improve this CV implementation for real work?"

**Candidate**: "Three changes. Stratification: the demo shuffles blindly, so
folds can end up with zero positives — with imbalanced fraud data that silently
breaks the estimates; stratified CV keeps each fold's class ratio close to the
overall ratio. K selection: 5 folds on 20 points means 4-point test folds; 10
folds on hundreds of thousands of rows gives stable, low-variance estimates.
And a real model in the loop: the dummy classifier is the baseline — the demo
is scaffolding to make CV mechanics visible, and swapping in Lab 02's logistic
regression or Lab 09's boosting is where the actual model comparison happens.
Also worth noting: for time-series fraud, folds must be temporal, not random —
leakage across folds is the quiet killer in financial ML."

**Interviewer**: "What is the bias-variance tradeoff, and how do these metrics
detect it?"

**Candidate**: "Bias is systematic underfitting — a linear model on a curved
problem; variance is sensitivity to training luck — a depth-100 tree. The demo
CV loop is the detector: cross-validation scores train the model on K−1 folds
and test on the held-out fold, so a model with high variance shows high fold
spread (the 0.255 std is the extreme case), while high bias shows uniformly low
scores. The practical workflow: run CV for each complexity setting — tree depth,
boosting rounds, polynomial degree — and pick the complexity where validation
error bottoms out. The lab's own sequence embodies it: Lab 03's fully-grown
tree overfits, Lab 09's shrinkage controls variance, and this lab is where you
measure both."

**Interviewer**: "Macro vs micro F1 — when would you use each?"

**Candidate**: "Macro averages each class's F1 equally — a small class's
mediocre F1 moves the number as much as the dominant class's excellent one.
Micro aggregates TP/FP/FN across all classes into one confusion matrix and
computes F1 once — it's dominated by the largest class. For the fraud model:
micro F1 would be nearly the ham-class F1 because 99.9% of samples are
legitimate, hiding a terrible fraud-class recall; macro F1 exposes it. Rule of
thumb: macro when every class matters equally (multi-class moderation),
micro when you care about total error volume (ranking systems). For binary
fraud, neither replaces precision-recall on the positive class — that stays
the decision metric."

**Interviewer**: "How does this lab connect to the rest of the series?"

**Candidate**: "It's the measurement layer for every model built in labs 01–09.
The confusion matrix and F1 grade Lab 02's logistic regression and Lab 04's
SVM; the AUC grades the ranking quality of Lab 09's booster; the CV loop is how
you pick K in Lab 05, tree depth in Lab 03, and learning rate in Lab 09
without fooling yourself. The series is a pipeline: build the model (01–09),
then verify it didn't memorize (10). The interview version of this insight:
'evaluate with the same rigor as you build' — and the demo's deliberately
mediocre numbers (0.70 accuracy, 0.35 CV baseline) are the reminder that
metrics are only meaningful relative to a baseline and an operating point."

**Interviewer**: "How would you run this evaluation in production for a fraud
model?"

**Candidate**: "Three layers. Offline: temporal holdout plus stratified
k-fold CV, with precision-recall as the primary chart and AUC as the ranking
summary; pick the operating threshold from the PR curve using the business's
false-positive cost. Shadow: score live traffic against the incumbent without
acting on it, and compare score distributions and precision on confirmed
cases. Monitoring: track precision, recall, and the score distribution per
hour — drift in either triggers retraining, and the threshold should be
re-tuned at least as often as the model, because the demo's fixed-0.5 story
is a simplification — production fraud economics move the cut long before the
weights do."
