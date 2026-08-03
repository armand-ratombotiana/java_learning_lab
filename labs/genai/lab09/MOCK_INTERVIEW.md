# Lab 09: Mock Interview — LLM Evaluation & Benchmarks

**Role**: ML Evaluation Engineer / LLM Engineer
**Duration**: 60 minutes
**Focus**: BLEU/ROUGE, toxicity & bias screening, factual consistency, hallucination detection, evaluation design

---

**Interviewer**: "Walk me through the lab's evaluation toolkit."

**Candidate**: "Four instruments. `bleu` — unigram precision with clipped counts and a
brevity penalty: if the candidate is shorter than the reference, it multiplies
precision by `exp(1 - refLen/candLen)`, which is why 'The cat sat on mat' scores
0.8187 against 'The cat sat on the mat' — one missing token halves... no, reduces
the score sharply. `rouge1` is recall-oriented: what fraction of the reference's
token set appears in the candidate; same pair scores 1.0000 because the dropped
word is a duplicate of 'the' and the set-based overlap is complete. Then
`ToxicityClassifier` scores by keyword hits over a pseudo-log-scale,
`BiasEvaluator` measures the male/female term ratio with 1.0 as balance, and
`FactualConsistency` measures claim overlap between generated text and source."

**Interviewer**: "The demo shows BLEU 0.8187 and ROUGE-1 1.0000 for the same pair. Why
the divergence, and which is more informative?"

**Candidate**: "Because one is precision-oriented and the other recall-oriented, and
'one word dropped' happens to be a duplicate. The candidate 'The cat sat on mat'
loses one 'the'; BLEU's clipped counting still pays the full-length denominator,
so precision drops and the brevity penalty applies. ROUGE-1 uses sets, and the
overlap with the reference set is complete, so it stays at 1.0 — it cannot see
that a token was *dropped*, only what is present. That's the classic blind spot:
ROUGE rewards a summary that covers all reference points but never punishes
fluff or omissions of duplicates. In my walkthrough harness the divergence is
more visible: a paraphrase scores BLEU 0.6000 / ROUGE 0.7500, and a fabricated
sentence drops to BLEU 0.3333 / ROUGE 0.2000. The pair of metrics tells you more
than either alone — but both are still just n-gram statistics."

**Interviewer**: "How do you actually detect hallucinations then?"

**Candidate**: "N-gram overlap is the weakest tool. The lab's `FactualConsistency`
scores generated-vs-source token overlap: faithful output scores 0.80, a partial
0.60, and a contradiction 0.20 — so thresholds catch gross fabrication but miss
subtle ones, because a sentence can share all content words and still state the
opposite. Real systems use claim extraction plus verification against a
knowledge base, NER alignment with the source, round-trip verification (have the
model re-check its own output), and dedicated factuality classifiers. My
walkthrough demonstrates the overlap method's power and limit in one run: it
flags the dog-flew-over-the-moon case (0.20) but would also pass a well-paraphrased
lie that shares content words."

**Interviewer**: "The lab's toxicity score for 'You are stupid' is 2.5 — yet that's
clearly abusive. What's happening, and how would you fix it?"

**Candidate**: "The score is `count / (words + 1) * 10` — one keyword over four tokens
of context gives 2.5, and the demo prints it without a verdict, so nothing is
actually flagged. The lab's classifier is intentionally toy: substring matching
against five words. In my walkthrough I set a threshold of 2.0 and it correctly
flags 'You are a stupid idiot' (3.33) and 'Kill yourself' (3.33) while passing
'Hello world' (0.0) — but 'This design is ugly and broken' (1.43) slips through
even though it's an insult, and 'hate' used non-toxically gets flagged. The real
fixes: embeddings-based toxicity classifiers (Perspective API-style),
context-aware polarity, and per-demographic error-rate monitoring rather than a
global threshold."

**Interviewer**: "The bias ratio for 'The man and the woman worked hard. He and she
both succeeded.' is 1.0. Run through the math."

**Candidate**: "The `computeBiasRatio` counts male terms — he, him, his, man, men, boy
— against female terms — she, her, hers, woman, women, girl — using substring
`contains`. Male hits: 'man' and 'he'. Female hits: 'woman' and 'she'. 2/2 =
1.0, balanced. But the mechanism is fragile: in my walkthrough, 'The men and
boys played while the girls watched.' scores 3.0 because 'he' matches inside
'the', and 'A nurse and a teacher helped the girl.' scores 0.50 because 'her'
matches inside 'teacher'. Substring matching turns innocent text into
male-skewed or female-skewed samples. The lab methodology is sound — term
association ratios are a legitimate lens — but the tokenizer is not: you need
word-boundary matching and, ideally, counterfactual swaps where you replace
demographic terms and measure output shift."

**Interviewer**: "What would you change to make this lab's evaluation trustworthy?"

**Candidate**: "Five things. One: word-boundary regex instead of `contains` for bias
and toxicity. Two: report the verdict, not just the score — the demo prints
2.5 with no threshold applied. Three: a held-out golden set with known labels so
the metrics themselves are evaluated (evaluation of the evaluator). Four:
semantic metrics alongside n-gram ones — embedding distance, or an LLM-as-judge
for fluency and faithfulness. Five: calibration — threshold selection on a
validation set with measured false-positive and false-negative rates, which is
exactly what my walkthrough's toxicity screen does when it reports 2/4 texts as
toxic at threshold 2.0."

**Interviewer**: "The lab mentions held-out vs held-in evaluation. Why both?"

**Candidate**: "Held-out measures generalization: can the model answer things it
wasn't trained on. Held-in measures memorization: if a model reproduces training
data nearly verbatim, held-in scores look perfect — and that's a red flag for
leakage, which is a safety issue when the training data contains personal or
copyrighted material. Both are needed: a model that scores well only on
held-in data is overfitting or memorizing; one that fails held-in but passes
held-out is probably fine. The lab's own eval set — 'The cat sat on the mat'
style samples — would never catch either effect, so my walkthrough separates the
two: exact-match cases (memorization-friendly) vs paraphrases (generalization)."

**Interviewer**: "How does the aggregated report in your walkthrough decide what a
'model pass' means?"

**Candidate**: "It makes the thresholds explicit and auditable. Translation passes at
BLEU >= 0.6 (3/4 — the hallucination case fails at 0.3333). Safety reports how
many texts are flagged toxic at threshold 2.0 (2/4). Factuality passes at
consistency >= 0.6 (2/3 — the contradiction fails at 0.20). The report format is
the important part for a production eval harness: each metric has a name, a
threshold, and a pass/fail per sample, so regressions are attributable. The
numbers themselves are secondary — the design decision is that a harness must
give you *per-sample* evidence, not just a mean, otherwise a model that is
excellent on 90% and catastrophic on 10% hides its failure."

**Interviewer**: "How would you design the evaluation for a summarization product
using this lab's primitives?"

**Candidate**: "Three layers. Quality: ROUGE/BLEU against reference summaries as a
cheap smoke test, plus LLM-as-judge for coherence and faithfulness on a sample —
n-gram metrics catch regressions, judges catch meaning drift. Factuality:
`FactualConsistency` against the source document on every production summary,
with a low threshold alerting to potential hallucinations. Safety: the toxicity
and bias screens on outputs, at stricter thresholds than in training because
the user sees this text. And the final layer is human evaluation on a golden
set for the launch decision. The lab's classes give me the skeleton; production
replaces the keyword lists with trained classifiers and adds the feedback loops
the lab doesn't have — thumbs up/down correlation with metric drift over time."

**Interviewer**: "What's your single strongest takeaway from this lab's design?"

**Candidate**: "That evaluation is a product with its own requirements. The lab
correctly separates quality metrics (BLEU/ROUGE), safety metrics (toxicity,
bias), and factuality metrics (consistency) — that's the right architecture.
But it also shows, by omission, what production needs: labeled golden sets,
calibrated thresholds, per-sample reporting, and semantic signal. The 0.8187
BLEU / 1.0 ROUGE pair in the demo is a perfect example: two metrics that agree
on the surface but measure different things, and a team that reads only ROUGE
would miss a dropped word."
