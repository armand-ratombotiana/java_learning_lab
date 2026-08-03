# Lab 07: Mock Interview — AI Testing & Evaluation

**Role**: AI Engineer / ML Engineer
**Duration**: 60 minutes
**Focus**: Test suites, benchmarks, regression gates, metrics, golden sets, CI

---

**Interviewer**: "Walk me through how this lab tests an AI system."

**Candidate**: "Two complementary layers. `TestSuite` is the unit-test layer: each case
feeds an input to the system under test — the lab uses a `MockClassifier` — and
asserts on the expected outcome, so a classifier that mislabels 'Great service' fails
a test. `BenchmarkRunner` is the batch layer: it runs a larger evaluation set through
the system and computes aggregate `Metrics` — accuracy, precision, recall, F1 —
returned as a `BenchmarkResult`. The suite catches specific regressions; the
benchmark catches degradation in aggregate. The lab runs both and prints the metrics,
because a test suite without aggregate numbers and a benchmark without individual
cases are each half of the story."

**Interviewer**: "Why do AI systems need different testing than traditional software?"

**Candidate**: "Traditional testing asserts exact behavior — the same input produces the
same expected output, deterministically. AI systems are stochastic and
approximate: the same input can produce different outputs, and 'correct' is a
judgment, not a constant. So you cannot write one assertion that pins every
behavior; you need distribution-level checks — the aggregate metrics — alongside
case-level checks. The other difference: AI quality is data-dependent, so tests
must cover the data distribution, not just code paths. The lab's two-layer design
is the answer to both: deterministic cases where they apply, aggregate metrics
where they are the truth."

**Interviewer**: "How do you choose what goes in the test suite?"

**Candidate**: "The suite is curated: edge cases, boundary inputs, the failures from
past bugs — every regression becomes a permanent case, and every new requirement
gets a case. The criteria are coverage of behavior the system must not regress, and
speed, because the suite runs on every change. The lab's cases are small and
deterministic by design — a classifier with known labels — so they can gate every
run. The mistake to avoid is treating the suite as the only evaluation: a suite that
passes tells you nothing about performance on unseen inputs, which is what the
benchmark layer exists to measure."

**Interviewer**: "Why report precision, recall, and F1 instead of just accuracy?"

**Candidate**: "Accuracy is the count of correct predictions over all predictions, and
it lies when classes are imbalanced: a classifier that always says 'negative'
scores high accuracy on a mostly-negative set while being useless. Precision asks
of the positive predictions how many were right; recall asks of the actual positives
how many were found; F1 balances the two. The lab's `Metrics` class computes the
full set precisely so you see the trade: high precision with low recall and
low precision with high recall are different failure modes that accuracy hides. You
pick the headline metric per domain — spam wants precision, cancer screening wants
recall — but you always look at all four."

**Interviewer**: "How does the lab demonstrate a regression in the metrics?"

**Candidate**: "The `BenchmarkRunner` produces a `BenchmarkResult` with the aggregate
metrics, and the demo compares results across versions — a version that changed the
classification logic shows up as a changed accuracy, precision, or recall, even when
the case-level suite passes. That is the regression gate: before shipping, the new
version's metrics must be at least as good as the incumbent's on the same evaluation
set. The lab's structure makes the comparison repeatable — same set, same metrics,
same runner — which is the entire point. Without a fixed evaluation set, a 'better'
version is an opinion."

**Interviewer**: "What makes an evaluation set trustworthy?"

**Candidate**: "Representativeness, independence, and cleanliness. Representative: it
covers the production input distribution — the failures that matter, the edge cases,
the class mix your users actually produce. Independent: it is not the data the
system was tuned on, because performance on the tuning set is inflated and
untrustworthy. Clean: labels are reviewed and versioned, so a change in the set is a
deliberate act with a record. The lab's evaluation set is small and fixed — the
teaching point is the discipline, not the scale: a small clean set with known
properties beats a huge noisy one, and a versioned set beats a mysterious one."

**Interviewer**: "Where do golden sets fit in?"

**Candidate**: "A golden set is the curated subset of the evaluation data with the
highest-confidence labels, the ones you trust absolutely — the lab's case-level
assertions are effectively the golden core. Golden sets are used for the fast checks:
regression gates in CI, smoke tests after deploys, and prompt or model comparison,
where speed and trust matter more than breadth. The design rule is layering: golden
set for the quick gate, broader evaluation set for release decisions, and online
monitoring for production drift. The golden set is the floor — if the system breaks
the golden core, nothing else matters."

**Interviewer**: "How do you handle label noise in evaluation data?"

**Candidate**: "Measure it before trusting the metrics: have a second reviewer label a
sample and compute agreement, and treat disagreement as either a noisy label or an
ambiguous case — both need fixing. Estimate the ceiling: if label agreement is 95%,
metrics above 95% on that set are partly measuring the labels' noise, not the
system. The practical consequences: keep ambiguous cases out of the golden set,
document known disagreements, and never compare two systems on a noisy set with
fake precision. The lab uses clean synthetic labels so the metrics are
interpretable — that is the ideal you are approximating in the real world."

**Interviewer**: "How does this integrate with CI/CD?"

**Candidate**: "The suite gates the fast loop: on every commit and pull request, run
the deterministic cases — they are fast and cheap. The benchmark gates the release
loop: before a deploy, run the full evaluation set and compare the metrics against
the incumbent's recorded baseline, blocking if the new version regresses. The lab's
`BenchmarkRunner` produces exactly the artifact a gate needs — a `BenchmarkResult`
with a pass/fail comparison against a stored baseline. The failure mode to design
against is a gate that is slow or flaky, because then teams bypass it and it stops
protecting anything."

**Interviewer**: "How do you test the tests themselves?"

**Candidate**: "Mutation-style sanity checks: introduce a known bug into the system and
verify the suite catches it — a test that does not fail on a broken system is
testing nothing. On the benchmark side, verify the metrics are computed correctly
with hand-computed examples, and check the set's statistics (class mix, label
balance) stay as intended after changes. The lab's tests are deterministic and its
metrics are simple enough to verify by hand, which is exactly why they are
trustworthy. The rule of thumb: if you would not trust the test to catch a real
regression, it is a test of your imagination, not of your system."

**Interviewer**: "How do you evaluate a system with subjective outputs?"

**Candidate**: "You move from exact-match to judged evaluation: rubrics — criteria with
explicit scales — applied by human raters or a judge model, with agreement checks
to validate the judges. The lab's classifier example is deterministic, which makes
the metrics exact, and the extension path is to treat subjective quality the same
way: turn 'is this good' into scored dimensions, measure inter-rater agreement, and
report score distributions, not just means. Two rules keep this honest: the rubric
is written before the outputs are seen, and the judge is evaluated against human
judgment before it is trusted."

**Interviewer**: "How do you compare two candidate models fairly?"

**Candidate**: "Same evaluation set, same metrics, same runner — that is the whole
trick, and it is why the lab structures the benchmark as a fixed pipeline: a
`BenchmarkRunner` over a fixed set producing the same `Metrics`. You run both
candidates through it, compare the metric vectors, and decide on the deltas —
accuracy up, precision down, recall flat — rather than on anecdotes or spot
demos. The fairness traps are the interesting part: a candidate that saw the
evaluation set during training inflates every number; a set that drifted from
production deflates both equally but hides the real question. The discipline is
versioning — candidate, set, and runner all pinned — so 'compare fairly' is
reproducible, not rhetorical."

**Interviewer**: "What is the most common failure you have seen in AI testing?"

**Candidate**: "Testing that passes while the system is wrong — the metrics look good
because the evaluation set is stale, or tiny, or overlaps the training data, or the
suite asserts on things the system was never expected to do. The second most common
is the opposite: testing theater, where a thick report is produced but nothing gates
on it, so regressions ship anyway. The lab's design counters both by construction:
metrics computed from a fixed set with a runner you can re-execute, and a
benchmark-vs-baseline comparison that is the actual decision. The lesson: evaluation
only matters if it is repeatable, and it only protects if it gates."
