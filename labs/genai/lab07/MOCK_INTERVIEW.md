# Lab 07: Mock Interview — RLHF & Preference Optimization

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: preference data, reward modeling, PPO clipped surrogate, KL divergence, DPO, reward hacking

---

**Interviewer**: "Walk me through the three stages of RLHF, mapping each to the lab."

**Candidate**: "Stage one is supervised fine-tuning — SFT on high-quality demonstrations so
the model already produces good text. Stage two is reward modeling: the lab's
`RewardModel.score(text)` assigns a score — length-based base plus boosts for words
like 'good' or 'excellent' and penalties for 'bad' or 'terrible', with seeded Gaussian
noise — and `loss(data)` trains on paired preferences: for each `PreferenceExample`
with a chosen and a rejected response, it maximizes the log-probability that the
chosen scores above the rejected, via the sigmoid term
`-log(1/(1+exp(rRejected - rChosen)))`. Stage three is policy optimization: PPO pushes
the policy toward high reward while a KL penalty keeps it near the SFT model — the
lab's `PPOSimulation.policyUpdate` returns exactly `clippedSurrogate - klCoeff * kl`."

**Interviewer**: "The demo prints a reward model loss of 0.6145 over two examples. What
does that number tell you?"

**Candidate**: "It's the mean of the per-pair logistic losses. Since the scores include
random noise (`Random(42)` Gaussian `* 0.1`), the chosen score doesn't always exceed
the rejected score cleanly, so the loss is meaningfully above zero — with 4 examples
my walkthrough prints 0.6228. A loss near zero would mean the model ranks almost every
chosen above its rejected pair with high margin — the model has learned the
preference signal. What's important is the *trend across training*, not the absolute
value: the reward model is only useful if it generalizes to new pairs, which is why
you hold out preference data and measure accuracy — the fraction of pairs where the
model agrees with the human label — rather than trusting the loss alone."

**Interviewer**: "Why does PPO use a clipped surrogate objective, and how does the lab's
`clippedSurrogate` implement it?"

**Candidate**: "The probability ratio `pi_new / pi_old` can explode in one update if the
policy shifts hard, destabilizing training. The clip bounds it: the lab computes
`Math.clamp(probRatio, 1 - epsilon, 1 + epsilon)` — epsilon 0.2, the standard value —
and takes `Math.min(ratio * advantage, clipped * advantage)`. The min means: if the
ratio is inside the clip range, the unclipped term applies (normal gradient flow); if
it exceeds the range, the clipped term caps the update. So the policy only absorbs
credit for improvements within the trust region, and beyond it the gradient is zero —
that's the mechanism that makes PPO stable enough to run on language models."

**Interviewer**: "The lab's `policyUpdate` computes a KL divergence. What role does it
play, and what does the `klCoeff` sweep in the walkthrough show?"

**Candidate**: "KL measures how far the new policy's token distribution has drifted from
the old — the lab computes it as `sum pOld * log(pOld / pNew)` over the softmaxed
logits — and it enters the objective as a penalty: `clippedSurrogate - klCoeff * kl`.
The penalty is what prevents reward hacking and fluency collapse: a policy that
maximizes reward by degrading into gibberish would have enormous KL, so the guard
rejects it. My walkthrough sweeps `klCoeff` from 0.0 to 0.5 and the objective declines
monotonically — 1.0099 down to 1.0074 — because the penalty is subtracted. Tuning
`klCoeff` is a core RLHF engineering task: too high and the model never adapts, too
low and it drifts or hacks the reward."

**Interviewer**: "What is reward hacking, and how do you detect it?"

**Candidate**: "Reward hacking is when the policy finds a *spurious* pattern that scores
high on the reward model without actually improving quality — exploiting the reward
model's shortcuts rather than the human preference they proxy for. The lab's toy
reward model is a perfect illustration: `score()` boosts anything containing 'good'
or 'excellent', so an optimizing policy could learn to sprinkle those words everywhere
— the reward goes up while quality goes down. Detection: track reward versus human
evaluation correlation (hacking appears as diverging curves), monitor KL from the SFT
model (unusual drift is a red flag), audit top-reward generations manually, and watch
for distribution shift in generated text. Defense: better reward models, KL guards,
and human evaluation in the loop."

**Interviewer**: "How does DPO simplify RLHF? Walk through `DPOLoss.compute`."

**Candidate**: "DPO removes the reward model and the PPO loop entirely. It uses the
observation that the optimal policy under a KL constraint has a closed form in terms
of the reference policy and the reward, so the preference loss can be written directly
against the policy and a fixed reference model. The lab's `compute` takes policy
logits and reference logits for the chosen and rejected completions and forms
`logRatio = beta * (logPiChosen - logRefChosen - logPiRejected + logRefRejected)` —
the beta-scaled advantage of the chosen over the rejected relative to the reference —
then applies the same logistic loss as the reward model:
`-log(1/(1+exp(-logRatio)))`. My walkthrough shows it: loss 0.6832 at `beta=0.1`,
dropping to 0.6444 at `beta=0.5` — higher beta amplifies preference differences.
Training is one supervised-style step per batch; no reward model, no sampling
rollouts, no clipped surrogate."

**Interviewer**: "The lab's reward model is a few keyword checks. How does a real reward
model differ, and what does the loss actually train?"

**Candidate**: "A real reward model is a language model with a regression head on the
final token, trained on tens of thousands of human preference pairs — typically
collected via pairwise comparisons of two completions, which is exactly the
`PreferenceExample` structure. The loss trains the scalar head to rank chosen above
rejected, and the base LM weights are usually frozen or lightly trained to avoid
forgetting. The lab's keyword-based `score` is the right *shape* — a scalar with
deterministic-seeded noise standing in for a trained model — and the `loss` over
paired data is the actual RLHF loss. The demos are honest about what matters: the
data structure and the loss are real; the scoring function is a placeholder."

**Interviewer**: "What are the failure modes of preference data itself?"

**Candidate**: "Four big ones. Selection bias: raters prefer longer, more confident
answers — the lab's `score` even bakes in `text.length() * 0.01`, which is a known
real-world artifact. Noise: raters disagree; that's why you collect multiple ratings
and model label noise. Distribution: preferences collected on model A's outputs don't
transfer to model B's — your preference data must be collected from the policy you're
training. And contamination: if the chosen/rejected pairs leak into evaluation, your
measured win rate is fiction. The lab's `PreferenceExample` records prompt, chosen,
rejected — a schema that supports tracking all of these as long as you also store the
source model, the rater, and the collection date."

**Interviewer**: "Compare PPO and DPO for a production alignment pipeline."

**Candidate**: "PPO is the higher-ceiling, higher-cost option: it needs the reward model,
online rollouts from the current policy, advantage estimation, and KL control — more
moving parts, more hyperparameters, but it can search beyond the preference data
distribution and is still what many frontier labs use for the final push. DPO is
simpler and more stable: no reward model, one loss, works well with a strong SFT
base and large preference datasets, and it's the default starting point for most
teams. A pragmatic pipeline: SFT, then DPO on a few preference rounds, and only
consider PPO if you have the infrastructure and the quality ceiling demands it. The
lab demonstrates both in one file, which mirrors how they coexist in production."

**Interviewer**: "How do you evaluate that alignment actually worked?"

**Candidate**: "Headline: human win rate against the SFT baseline on a held-out prompt
set — the model's output preferred at least 50% of the time, plus preference accuracy
if the reward model is involved. Then safety and quality regressions: the metrics from
lab 09 — toxicity, bias, factual consistency — must not regress, because alignment
can trade them away. Then behavioral checks: refusal rates (too high means
over-alignment), instruction-following accuracy, and KL drift as a process signal.
Finally, per-prompt qualitative review of the biggest reward gains — that's where
reward hacking surfaces. The labs fit together here: 07 produces the aligned model,
09 measures it, and 14 monitors it in production."

**Interviewer**: "If you could improve this lab's RLHF demo in one change, what would it
be?"

**Candidate**: "Make the policy actually train. The lab's `policyUpdate` is a single
objective computation over fixed logits — it demonstrates the formula but not the
loop. I'd add a mini policy optimizer: start from SFT logits, apply `policyUpdate` as
the objective, take gradient steps, and watch the objective rise while the KL penalty
keeps the distribution near the reference — that would show the tension between
reward and KL in action, which is the heart of RLHF. The `DPOLoss` already works this
way conceptually: it's differentiable end-to-end, so it could be wired into the same
training loop the LoRA lab uses, with the preference data as the dataset."
