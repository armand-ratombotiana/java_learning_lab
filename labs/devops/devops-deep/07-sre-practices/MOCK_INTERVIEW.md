# Lab 07: Mock Interview — SRE Practices Deep Dive

**Role**: Site Reliability Engineer / Platform Engineer
**Duration**: 60 minutes
**Focus**: SLIs, SLOs, error budgets, burn rate alerting, toil, incident postmortems

---

**Interviewer**: "Define SLO, SLI, and error budget in one sentence each, and then connect them."

**Candidate**: "An SLI is the metric that measures how well the service is doing — availability,
latency, throughput. An SLO is the target for that metric over a period — 99.9%
availability
over 30 days. The error budget is the allowance implied by the SLO: 0.1% of the period's
requests may fail, and the team may 'spend' that allowance on releases and experiments.
They
connect like this: the SLO converts user experience into a number; the SLI measures the
number; and the error budget converts the SLO from a promise into a working budget that
operations and development both manage — when the budget is exhausted, velocity stops
until
reliability recovers."

**Interviewer**: "How do you pick which SLIs to track?"

**Candidate**: "Track the metrics that match how users experience the service — the classic set
is availability, latency, and throughput, with the specific ones derived from the
service's
purpose: for an API, request success rate and latency; for a data pipeline, freshness
and
completeness; for a storage system, durability and latency percentiles. The discipline
is to
pick SLIs that are (a) meaningful — a regression in the SLI is a user-visible
regression, (b)
measurable — we can actually collect them with low overhead, and (c) few — three to five
SLIs; a dashboard with twenty indicators is not an SLO program, it's monitoring with a
spreadsheet. And define them precisely: 'availability' is 2xx and 3xx responses over all
responses, measured per minute — the definition lives in the code, not in a meeting
note."

**Interviewer**: "Walk me through choosing an SLO target."

**Candidate**: "The target must be a real user tolerance, not an aspiration. Process: measure
the current SLI over a representative period — that's the starting point; then set the
target
as the tightest number the product can tolerate with headroom for error-budget spending
—
meaning the budget should cover your expected deploys and experiments; and then state
the
consequence of missing it: 'at 99.9%, we page and stop deploying; at 99.99%, we page the
on-call and roll back'. A target with no consequence is decorative. And avoid
over-tuning:
moving from 99.9% to 99.99% changes allowed downtime per month from 43 minutes to 4 —
that's
a tenfold change in operational pressure for the same users, unless the product truly
needs
it."

**Interviewer**: "The budget is half consumed in the first week of a 30-day window. What do
you do?"

**Candidate**: "That's a burn rate of about 2 — the budget will be exhausted in roughly half
the period — and the response has two halves. The technical half: find and fix the
reliability problem now — the burn is a symptom of something degrading; the error budget
didn't cause the failures, the failures consumed the budget. The process half: freeze
error-budget-spending activities — deploys, experiments, migrations — until the burn
stops
and the budget has headroom again. The key principle: the budget is the control
mechanism for
velocity. If teams ignore the exhausted budget and keep shipping, the SLO is just a
poster —
so the mechanism needs teeth, which is what burn-rate alerting and deploy gating give
it."

**Interviewer**: "Why is burn rate better than alerting on remaining budget?"

**Candidate**: "Alerting on remaining budget is a late alarm: by the time 'budget 10% left'
fires, the service has been failing for days and the recovery is weeks away from
restoring
the budget. Burn rate is a leading indicator: it measures the speed of consumption, not
the
level — a burn rate of 2 means 'budget exhausted in half the remaining period at this
rate',
and 14.4 means 'gone in hours'. The SRE book's recommended windows: page when burn rate
is
14.4x over 5 minutes — that's fast breakage with instant impact — or 2x over 1 hour, and
warn
at 1x over 6 hours. Same math, different windows, each tuned to a failure class: sudden
outages versus slow degradation."

**Interviewer**: "What is toil, and how do you manage it?"

**Candidate**: "Toil is manual, repetitive, automatable work tied to operating a service —
restarting pods by hand, manual backup runs, hand-executed migrations, responding to
alert
spam. The test: if a human must do it every time and the need scales with the fleet,
it's
toil. Management is measurement plus reduction: track toil hours per week per engineer —
the walkthrough's tracker is the tool; keep it under 50% of on-call-adjacent time — the
SRE
book's rule of thumb; and treat each recurring toil task as a backlog item with an
automation owner, because toil doesn't reduce on its own. The cost of unmanaged toil: it
crowds out reliability work, it makes on-call unsustainable, and it's invisible in most
organizations because it's 'just work'."

**Interviewer**: "What does a blameless postmortem actually mean?"

**Candidate**: "Blameless means the review assumes every person involved made the best
decision with the information they had, and the findings target systems and processes,
not
people. In practice: the postmortem documents the timeline — what was observed and when;
the
root cause — usually a chain, not a single event; the contributing factors — why the
detection, response, and prevention failed; and action items that are specific, owned,
and
scheduled. The blamelessness is what makes the postmortem honest — if people fear being
punished for their actions in an incident, they stop reporting details, and the
postmortem
becomes theater. The outcome to measure: action items actually completed, not documents
written."

**Interviewer**: "Your team is all-in on alerting on every anomaly. How do you fix that?"

**Candidate**: "That's alert fatigue, and the fix is to make alerts mean something. The
principle: alerts should be actionable — every page must have a runbook, an owner, and a
reason that connects to an SLO. My process: inventory the alerts and classify each as
(a) SLO-connected — it fires when a burn-rate or budget threshold is crossed, (b) SLO-
irrelevant but essential — disk full, cert expiring, (c) noise — delete or convert to a
dashboard panel. Then tune: group correlated alerts, raise thresholds to where the alert
predicts the SLO miss rather than reporting every blip, and measure the page rate per
on-call shift — the target is a handful of pages per week, not per hour. The ideal end
state: alerts are rare, each one demands an action, and on-call is quiet enough that
when
something pages, it gets real attention."

**Interviewer**: "Do internal platform services need SLOs too — or is that overkill?"

**Candidate**: "They need them the most, because internal consumers can't switch providers and
can't see your dashboards. A platform team that says 'we're fine, we're just the
infrastructure' makes every dependent app carry the reliability cost invisibly. The
honest
set: API latency and availability for the control plane, pipeline success rate for CI,
and
secret/registry uptime — each with a target negotiated with consumers, because a
platform SLO
is a contract between teams. The practical difference from user-facing SLOs: the budget
spenders are your own consumers' deploys, so the burn-rate alerts should page the
platform
team before consumers' on-call does — that's what 'platform reliability' looks like from
outside."

**Interviewer**: "How do you know the SLO program is actually working?"

**Candidate**: "By outcomes, not artifacts. Three signals: error budget trends — consumption
should be stable or improving, with deliberate, visible spending during releases rather
than
accidental burn; alert quality — page volume trending down while mean-time-to-respond
and
mean-time-to-resolve trend down; and behavior change — deploy gates actually block when
the
budget is out, teams actually fix chronic toil instead of absorbing it, and postmortem
action items close within their schedules. The uncomfortable part: an SLO program with
beautiful dashboards and no behavior change is a reporting exercise, not an SRE practice
—
the artifacts exist to change decisions, and that's what you verify."
