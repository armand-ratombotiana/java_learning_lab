# Lab 03: Mock Interview — Container Security Deep Dive

**Role**: Platform / Security Engineer
**Duration**: 60 minutes
**Focus**: Image scanning, Dockerfile hardening, seccomp, AppArmor, runtime detection (Falco)

---

**Interviewer**: "A development team just started shipping containers. What security advice would
you give them first?"

**Candidate**: "Start with the three things that cause most real incidents: who runs the process,
what the image contains, and how secrets get in. Run as a non-root user — a numeric UID,
because
a username that isn't in the image can silently resolve to root. Scan images in CI and
gate on
HIGH and CRITICAL findings, because a base image with a known CVE is a vulnerability you
ship on
every deploy. And never bake secrets into images — they end up in registries and every
layer they
were in. After those three, I'd add: minimal base images, pinned versions with digests,
and a
runtime detection story. Small, enforceable rules beat a big security checklist that
nobody can
maintain."

**Interviewer**: "Walk me through how an image scanner actually works."

**Candidate**: "The scanner does three steps. First, unpack: read the image layers and the package
database — dpkg/APK/RPM metadata or SBOM files — to build the inventory of packages and
exact
versions. Second, match: join that inventory against a CVE database, where each CVE
lists the
affected package ranges and fixed versions. Third, report and gate: aggregate by
severity, and
let policy decide — fail the build on HIGH+, allow MEDIUM with a deadline, or maintain
an
exception list with owners. The subtleties: the OS package scan misses application-level
dependencies, which is why scanning the SBOM or the lockfiles matters; and a scan is
only as good
as its vulnerability database freshness, so continuous scanning of the registry catches
disclosures that land after your last build."

**Interviewer**: "What would you put in a Dockerfile review checklist?"

**Candidate**: "I'd order it by impact. Multi-stage builds — build in a JDK image, copy only the
artifacts to a runtime image, so no compilers and build tooling ship to production.
Minimal,
pinned base images — pin by digest, prefer distroless or slim runtime images; unpinned
'latest'
is nondeterministic and un-reviewable. Non-root with numeric UID, verified with whoami
in the
final stage. No secrets in ENV or ARG — they persist in layers and image history;
secrets get
injected at runtime. No untrusted fetches in RUN — curl-to-sh in the build is a
supply-chain
risk, and every fetched artifact should have a checksum. And finally: read-only
filesystem where
the app allows it, and explicit HEALTHCHECK. Most of these are lintable with tools like
hadolint or Conftest — I'd make the top five part of CI, not a human review step."

**Interviewer**: "What's the difference between seccomp and AppArmor, and why do you need both?"

**Candidate**: "They operate at different boundaries. Seccomp filters system calls at the kernel
syscall boundary — it can block execve, mount, ptrace, but it doesn't know anything
about files
or paths. AppArmor works at the file and capability level — it can say 'this profile may
not
write to /etc/hosts' or 'no raw socket access', using path-based rules. So seccomp is
your
syscall allowlist — default-deny, allow what the runtime needs; AppArmor is your
file-access and
capability policy. You need both because they block different attack surfaces: seccomp
stops the
syscall that escapes the container, AppArmor stops the file write that exfiltrates data.
Docker
ships a sensible default seccomp profile; AppArmor needs explicit profiles per
workload."

**Interviewer**: "How does Falco detect a container breakout attempt?"

**Candidate**: "Falco watches the syscall stream of every container via a kernel module or eBPF
probe, and matches it against rules. The classic breakout signatures: a process in a
container
spawning a shell — that's the execve of bash or sh; writing to sensitive host paths —
like
/etc, /proc/sys, or the host root mount; privileged operations like mounting filesystems
or
loading kernel modules; and network behavior like binding to unexpected ports or
connecting to
external hosts. The rule language matches on fields like proc.name, fd.name, and
container.id.
The strength is detection of behavior, not just images: a compromised container doesn't
need a
new CVE — it needs to do something anomalous, and that's what the rules catch. The
output is an
alert stream, which you'd filter, enrich, and route to a SIEM or pager."

**Interviewer**: "Should every container use a read-only root filesystem?"

**Candidate**: "Ideally yes, but it's not free. A read-only root filesystem with an empty tmpfs
volume for /tmp makes it much harder for an attacker to persist anything — no dropping
binaries,
no modifying libraries. The cost: applications that write to their working directory or
config
paths break, and some runtimes need writeable /tmp or /var/run. My approach: default to
read-only root, carve out explicit writable volumes, and fix the app errors — most are
one or
two path changes. Kubernetes enforces this cleanly with readOnlyRootFilesystem in the
security
context, and Pod Security Standards push the same direction with restricted policies."

**Interviewer**: "An image scan shows a CRITICAL CVE in a base image you can't change this week.
What do you do?"

**Candidate**: "I treat it as a risk decision, not a wall. The gate blocks the build, but the
questions are: is the vulnerability exploitable in how we use the image — a CRITICAL in
a
network service is different from one in a CLI tool we never invoke; and can we mitigate
in
place — block the affected port, drop the capability, restrict with seccomp. If there's
a
convincing mitigation, I approve a time-boxed exception with an owner and an expiry —
the
exception forces the upgrade to be scheduled, and the expiry re-raises the risk if it
slips.
If there's no mitigation, the exception is rejected and the team reprioritizes the base
image
upgrade. The key: exceptions are the control that keeps a strict gate sustainable —
without
them, teams bypass the gate wholesale."

**Interviewer**: "How do you secure the container build pipeline itself, not just the images?"

**Candidate**: "The pipeline is the biggest supply-chain surface. I'd start with trusted base
images: pin by digest, mirror the registries you depend on, and scan every new base
before it
gets cached. Then CI hardening: short-lived credentials that only the runner can use, no
secrets
in CI logs or caches, and signed artifacts — cosign-sign the image at build, verify at
deploy,
so a compromised registry can't inject a tampered image. On the infrastructure side: the
runner
pool itself — isolated, ephemeral runners with no access to production. And version
pinning of
the tooling in the image, because curl-to-sh in a Dockerfile is how a lot of pipelines
get
poisoned. The mental model: the pipeline is the place where code becomes trusted, so
every input
to it — base images, dependencies, tooling — deserves the same scrutiny as your own
code."

**Interviewer**: "What does runtime detection give you that image scanning doesn't?"

**Candidate**: "Image scanning answers 'what was in the image when it was built' — it's a point
in time. Runtime detection answers 'what is the container doing right now'. A container
that
was clean at scan time can still be exploited: a web app that gets RCE'd and then runs
something it never should — a shell, a curl to an external host, a write to /etc. That's
exactly what scanning can never see, because it wasn't in the image. The practical
layering:
scan gates what gets deployed, runtime detection watches what runs, and together they
close
the window — scan catches the known, Falco catches the anomalous. Both are needed; one
without
the other leaves a big gap — image scanning without runtime detection misses the
compromise,
runtime detection without scanning floods you with alerts for known-bad images."
