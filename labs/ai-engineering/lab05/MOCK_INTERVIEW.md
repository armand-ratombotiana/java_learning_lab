# Lab 05: Mock Interview — Prompt Engineering at Scale

**Role**: AI Engineer / LLM Engineer
**Duration**: 60 minutes
**Focus**: Prompt registry, versioning, templates, A/B testing, evaluation, rollout

---

**Interviewer**: "Walk me through how this lab manages prompts at scale."

**Candidate**: "The centerpiece is `PromptRegistry`: prompts are not strings scattered
through code, they are registered artifacts with an id, a version, a template, and a
family — so a prompt is managed like a deployable unit, not a hardcoded literal.
`PromptTemplate` handles the parameterization: placeholders get substituted with
runtime values at render time, which keeps the template stable while the inputs vary.
`ABTestFramework` sits on top: it selects a variant per request, renders it, and
records which variant produced what outcome. The demo wires all three together with
a `MockLlmClient` so the whole lifecycle — register, render, test, compare — is
visible end to end."

**Interviewer**: "Why is a registry better than constants in code?"

**Candidate**: "Because prompts change faster than code and they change independently of
releases. In code, every tweak is a deploy, every deploy risks shipping a prompt
change alongside unrelated changes, and there is no history of what the model actually
saw last week. A registry gives every prompt an identity and a version: you can diff
prompts, roll back a bad one without a redeploy, and point the version selector at a
new variant instantly. The lab's registry makes versioning the core data model — the
unit of discussion in reviews is a prompt version, not a code diff."

**Interviewer**: "How does template rendering avoid prompt-injection-style breakage?"

**Candidate**: "The template and the data are kept separate until render time — the
template is the trusted program, the user content is data flowing through a
placeholder. That separation is what lets the demo render the same template against
different inputs and compare outputs cleanly. The discipline that follows: never
concatenate user text into a template string as if it were template syntax, because
that is how user content starts controlling the instruction. The lab's structure
makes the boundary explicit — placeholder substitution is the only path for dynamic
content — which is the correct foundation for the security work in later labs."

**Interviewer**: "How does the A/B test framework split traffic?"

**Candidate**: "`ABTestFramework` assigns each request to a variant — typically a
deterministic hash of a stable identifier, or a weighted random — renders the
selected variant, and records the outcome against the variant id. The demo can show
the split and the per-variant results, so you compare the control and treatment on
the same traffic. The rules that make this honest: the assignment must be stable for
the same user, the comparison must be on a predefined metric, and the experiment must
run long enough for the sample size to mean anything. A variant chosen by a hunch
and judged after fifty requests is not an experiment."

**Interviewer**: "What metrics do you compare between prompt variants?"

**Candidate**: "Task outcome first — for a classification prompt, accuracy; for a
generation prompt, judged quality or groundedness; for a support prompt, resolution
rate. Then behavior and cost: refusal rate, format validity, latency, and token usage
per variant, because a prompt that is slightly less accurate but consumes a third of
the tokens may be the right production choice. The lab's framework records the
outcome per variant so these comparisons are possible; the production lesson is to
define the success metric before the experiment and to include cost and safety in the
same comparison, since prompt changes move all of them at once."

**Interviewer**: "How do you version a prompt and roll it out safely?"

**Candidate**: "Each change creates a new version in the registry; the selector points
traffic at a target version. Safe rollout means: validate the new version offline on
a held-out evaluation set, ship it to a small percentage of traffic, watch the
outcome metrics against the incumbent, and roll back by flipping the selector — no
code deploy required. The lab's structure supports this because version selection and
rendering are separate from the application logic. The failure to avoid is
prompt-latte art: tiny unmeasured tweaks accumulating with no owner, no version
history, and no way to know which prompt produced which outcome."

**Interviewer**: "How do you handle model upgrades when you have thousands of prompts?"

**Candidate**: "You treat the model change as a change to the environment, not to the
prompts: run the prompt suite against the new model on an evaluation set before the
upgrade, because prompts tuned for one model often behave differently on the next.
Two practical patterns: pin a prompt family to a model version when behavior is
sensitive, and keep a regression harness that replays recorded requests through
candidate models and compares outputs. The lab's registry plus evaluation design is
the foundation for this: if prompts have versions and outcomes are recorded, an
upgrade becomes an A/B test across models instead of a leap of faith."

**Interviewer**: "What makes a good template design?"

**Candidate**: "Structure and isolation. A template should have explicit sections —
instructions, examples, data, output format — because the model follows structure
and you can debug output against structure. It should parameterize everything that
varies and hardcode nothing that changes by user or by context, so rendering is pure.
And it should be self-testable: the same template rendered against known inputs
should produce known outputs, which the lab demonstrates with the mock client. The
tell of a bad template: the answer to 'what exactly did the model see?' requires
reading three files and remembering a string concatenation."

**Interviewer**: "How do you evaluate prompts without a human in the loop?"

**Candidate**: "Three tiers. Deterministic checks: does the output parse, match a
regex, contain the required format — cheap and catch format regressions instantly.
Reference-based: compare against gold answers with similarity or exact-match metrics —
the mock client in the lab makes these runs fast and repeatable. Model-based: a judge
model scores outputs on rubric dimensions like helpfulness or groundedness — closest
to human judgment, and needs validation that the judge agrees with humans. The lab
encourages the habit that matters most: evaluation is a repeatable run against a
fixed set, not a manual look at five outputs."

**Interviewer**: "What belongs in the registry beyond the prompt text?"

**Candidate**: "Metadata that makes a prompt governable: owner, family, version, target
model, evaluation results, and rollout status. The text alone is not enough — an
orphaned prompt nobody owns is a liability, and a prompt whose evaluation set is
unknown cannot be changed safely. The lab models the essentials — id, version,
template — and the discipline is to grow from there as the system does: deployment
metadata, per-version metrics, and change history. A registry that records only
strings is a fancy dictionary; a registry that records provenance and outcomes is an
operational asset."

**Interviewer**: "How do you manage token cost and caching for prompts?"

**Candidate**: "The registry makes cost measurable per template: the fixed portion of a
template is cacheable — identical prefixes across requests can reuse a cached
prefix instead of re-billing — while the variable substitutions are where the
marginal cost lives. The practical rules: keep the static scaffolding of every
prompt byte-identical so prefix caching hits, and track cost per variant in the
A/B comparison, because a variant that performs the same with fewer tokens wins on
the operating budget. The lab's template model supports this because the structure
is explicit — you can compute the cost of the fixed part and the variable part
separately. Prompts are not free; the registry is where their economics are
governed."

**Interviewer**: "How would you respond to 'just change the prompt' as a fix request?"

**Candidate**: "I would treat it as an experiment: write the change as a new version,
define the success metric, run it against the incumbent on the evaluation set and in
A/B traffic, and report the outcome — not because the change is suspicious, but
because prompt changes deserve the same evidence as code changes. I would also ask
what problem the change is solving: if the model misbehaves on specific inputs, the
right fix may be the evaluation set, the tooling, or the model itself, and a prompt
tweak can paper over a problem that needs a structural fix. The lab's whole design
is the counter-culture: prompts as versioned, tested artifacts."

**Interviewer**: "What is the most common prompt engineering failure at scale?"

**Candidate**: "The unmeasured drift: prompt performance degrades over time — the
distribution of inputs shifts, or the model updates — and nobody notices until a
support spike, because there was no baseline to compare against. The second common
failure is the reverse: endless prompt optimization on anecdotes, changing one
phrase at a time with no evaluation set, which produces prompts that pass the
memorized five examples and fail everything else. The lab's answer to both is the
same: registered versions, a fixed evaluation run, and recorded outcomes — so
'better' is a measurement and regressions are visible the day they happen."
