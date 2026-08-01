# Lab 08: Mock Interview — Incident Management

**Role**: Incident Commander / SRE
**Duration**: 60 minutes
**Focus**: Incident lifecycle, roles, escalation policies, timelines, blameless postmortems

---

**Interviewer**: "Walk me through an incident from detection to closure, naming the states."

**Candidate**: "Detection — an alert fires or a user reports something; the incident is opened
with a severity and a title. Investigation — the on-call acknowledges within the policy
window and the timeline starts: who was paged, who acknowledged, what was tried.
Mitigation —
the fix that restores service, which matters more than root cause during the incident
itself;
mitigation and root cause are different things and both get recorded. Resolution — the
service is back and the incident is declared resolved with a summary of what happened.
And
closure — after the postmortem is written and action items exist, the incident closes.
The walkthrough encodes these as `DETECTED -> INVESTIGATING -> RESOLVED -> CLOSED` with
guards, so you cannot skip investigation and resolve a cold alert."

**Interviewer**: "What is the Incident Commander's job during a SEV1?"

**Candidate**: "The IC is the single decision-maker — not necessarily the most senior engineer,
but the one accountable for the incident's outcome. Their job: set severity, assign
roles,
declare who works on what, decide when to escalate, and make the judgment calls like
'roll
back the deploy' or 'scale out'. They do not fix things — that's the SMEs' job. Why it
matters: without a single IC you get the classic failure mode of five senior engineers
each
trying different fixes and nobody tracking what was already tried; the timeline gets
invented after the fact and the postmortem is fiction."

**Interviewer**: "Why does the walkthrough have a deputy and a scribe?"

**Candidate**: "The deputy shadows the IC and can take over if the IC is unavailable or
loses the thread — continuity of command. The scribe owns the timeline: every decision,
every
action, every timestamp goes in the log, because the timeline is the raw material of the
postmortem and the only reliable record of *what the response actually did*. Two details
matter: the scribe records decisions and reasons, not just actions — 'we tried X and it
didn't work' is a finding — and the timeline entries must be written in real time,
because
reconstructed timelines are always wrong."

**Interviewer**: "How does the escalation policy work, and when should it escalate?"

**Candidate**: "The policy maps severity to acknowledgment timeouts: SEV1 gets five minutes,
SEV2 fifteen, and so on. The check is pure: `needsEscalation(severity, detectedAt,
acknowledgedAt, now)` — if there's no acknowledgment by the deadline, escalate to the
secondary on-call, then the tertiary, then the incident manager. The important design
point:
escalate on *not acknowledged*, not on 'taking too long to fix' — acknowledgment means
the
alert is seen and owned; a missed ack means the routing or the on-call is broken, and
that's
unambiguously actionable. Fixing is judgment work; acknowledging is a commitment, and
the
policy guards the commitment."

**Interviewer**: "The demo says escalation with an ack at 10:02 and deadline 10:05 is false.
Why not escalate on severity alone?"

**Candidate**: "Because escalation exists to repair the process, not to add noise. If the
incident was acknowledged within the window, the process is working — someone owns it,
and
escalating further would page people who add nothing yet. The escalation triggers when
the
process is broken: nobody acknowledged. That's why the policy is about the gap between
detected and acknowledged, not about severity — severity only sets how short the gap is
allowed to be. A SEV1 with a slow but working ack is still handled; a SEV3 with no ack
is a
process failure that needs the same escalation machinery."

**Interviewer**: "What's the difference between mitigation and root cause during an incident?"

**Candidate**: "Mitigation restores service; root cause explains why it broke. During the
incident, mitigation comes first — the goal is a working system, so 'restart the
database' or
'roll back the config' counts as success even if nobody knows the cause yet. Root cause
is
analyzed after, in the postmortem, when the pressure is off. The classic mistake is
stopping
at mitigation: the incident is resolved, the action items never happen, and the same
outage
returns. The walkthrough reflects this: `resolve()` takes a summary of the mitigation,
and
the RCA is a separate artifact that factors in contributing causes and owned action
items —
the incident's end is the postmortem's beginning."

**Interviewer**: "What should the postmortem contain, and what makes it blameless?"

**Candidate**: "The timeline, the root cause, contributing factors, and action items with
owners and due dates. Blameless means the analysis targets systems, not people: every
action
was a reasonable choice given the information available at the time; findings read 'no
canary
for config changes' rather than 'David broke Redis'. The reason is operational, not
ethical:
blame suppresses information, and the postmortem needs full information to be useful.
The
real test of a blameless process is whether an engineer can report their own mistake in
the
postmortem without fear — if they can't, your 'blameless' culture is a slide in a
review."

**Interviewer**: "How do you make sure action items from postmortems actually get done?"

**Candidate**: "Three properties and a loop. Properties: specific enough to verify — 'add
config review to the change template', not 'improve change management'; an owner who is
a
person, not an anonymous team; a due date that the tracking system pages on when missed.
The
loop: the reliability backlog is fed by postmortem action items, completion rate is a
leadership-reviewed metric, and unresolved items from the last incident block nothing
but are
visible to everyone. The failure mode to avoid: a backlog of forty unchecked boxes —
that's
not a reliability program, it's a paperweight. Fewer, completed items beat many,
abandoned
ones."

**Interviewer**: "How do you connect incident management to SLOs and error budgets?"

**Candidate**: "The incident consumed error budget; the postmortem's action items are how the
team pays it back. Concretely: when incidents exhaust the budget, deploys pause until
the
fixes land — that's the budget acting as the speed control. And the metrics
cross-pollinate:
error budget burn predicts incidents before they're declared; incident timelines tell
you
where the response failed — detection time, ack time, escalation gaps — which monitoring
alone can't see. A mature team treats the two as one ledger: SLOs measure the cost,
incidents
record the causes, and the postmortem is where the ledger is balanced."

**Interviewer**: "What's a common incident response failure you've seen, and how would you
fix it?"

**Candidate**: "The 'no one owns the incident' failure — five people in a channel, everyone
trying fixes, no IC, no timeline, and nobody remembers what was tried. The fix is the
role
model itself: an IC who assigns, a scribe who records, escalation when the IC is absent.
The
second most common: fixing symptoms without declaring resolution criteria — the service
recovers on its own, the incident fades, and the same failure returns next week because
the
postmortem never happened. The fix is process: closure requires a resolved state, and
the
postmortem is scheduled at closure, not 'when we get time'. Both failures are process
failures, not engineering failures — which is exactly why the walkthrough's state
machine
encodes the process in code."
