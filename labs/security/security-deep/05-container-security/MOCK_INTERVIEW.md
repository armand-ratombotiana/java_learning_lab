# Mock Interview: Image Vulnerability Scanner with CVE Matching

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Security Engineer (Supply Chain / Container Platform Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Software supply chain, version semantics, vulnerability matching, false positives
**Problem**: Implement an image vulnerability scanner: parse a package manifest, match each package against a CVE database using semantic-version range constraints, and produce a prioritized report.
**Language**: Java 21+ (records, no external libs)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. What is the data model of a vulnerability scan: manifest → package → CVE?
2. Why is version-range matching the hard part? What do SemVer, pre-release, and range operators have to do with it?
3. Why do scanners over-report — and how do you calibrate severity and trust?
4. What's the difference between a CVE in the DB and a *reachable* vulnerability?
5. How do you prioritize: CVSS alone, or EPSS / exploitability / reachability?
6. Follow-up: SBOM, package provenance, dependency confusion, and the scanner's place in the CI/CD gate.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "Our images ship dozens of base layers and we need a first-pass vulnerability scanner: read a manifest, match against CVE records, report. Scope it."

**Candidate**: "Three questions. First, manifest format: I'll assume a simple `name=version` per line (the universal envelope; we can bolt on dpkg/POM parsing later) — and I'll model the CVE database as records keyed by package name with version-range constraints. Second, version semantics: I'll implement a proper SemVer comparator — major.minor.patch with pre-release handling — because 'matching' is only as good as the version comparison. Third, the report contract: sorted by severity, with the matching constraint shown for each finding, so an analyst can audit why it fired."

**Interviewer**: "Agreed. And what do you need from the CVE record model?"

**Candidate**: "Each CVE: id, package name, an *affected constraint* (a set of version-range predicates), a severity, and a description. The matcher answers one question per (package, CVE): does the manifest version satisfy any of the vulnerable ranges? If yes → finding with the evidence (the constraint that matched). I'll include the famous cases in the test DB: log4j (CVE-2021-44228), Spring (CVE-2022-22965), OpenSSL (CVE-2022-0778) — because their constraint shapes (exclusive bounds, beta ranges, patch fences) exercise the comparator hard."

### Part 2: Theory — Version Matching (10 minutes)

**Interviewer**: "Walk me through SemVer and why the naive string compare fails."

**Candidate**: "SemVer is major.minor.patch[-prerelease][+build]: 2.14.1 < 2.15.0 — a string compare gives 2.14.1 > 2.15.0 because '4' > '5' at position 3... wait, string compare of '2.14.1' vs '2.15.0': character-by-character '2','.','1' vs '2','.','1' then '4' vs '5' — actually numeric comparison must compare *numeric segments*: 14 < 15 — string comparison fails precisely because '14' vs '15' differs only in the second character... no wait, '2.14.1' vs '2.15.0' — at the third character both '1', fourth '4' vs '5' — '4' < '5' so string says 2.14.1 < 2.15.0 — that one happens to work. The killer cases: 2.10.0 vs 2.9.0 ('1' vs '9' — string says 2.10.0 < 2.9.0, wrong); 1.0.10 vs 1.0.9; pre-releases: 1.0.0-beta < 1.0.0; and build metadata which must be ignored. So: numeric segments compared numerically, pre-release sorts before release, build metadata ignored. That's the comparator the matcher is built on."

**Interviewer**: "And the range syntax — what do you support?"

**Candidate**: "Constraint sets: each CVE declares one or more predicates — `>= 2.0, < 2.15`, `= 1.1.1k`, `<= 2.14.1` — comma-separated meaning AND. A version is *affected* iff it satisfies at least one full constraint set... rather: each CVE's affected set is a list of constraints, and matching means the version satisfies the conjunction within any single constraint. The log4j case is the instructive one: affected is `>= 2.0-beta9, <= 2.14.1` — the constraint includes the beta pre-releases, so 2.0-beta9 *is* affected while 1.2.17 (the ancient line) is not. A scanner that uses `>= 2.0` without the pre-release nuance would get the beta range wrong."

**Interviewer**: "Where do false positives come from, concretely?"

**Candidate**: "Four sources. (1) **Fixed-version ambiguity**: the constraint `<= 2.14.1` matches every build that still *ships* 2.14.1 even if the distro backported the fix — the famous distro-backport problem: RHEL patches in-place without changing the version. (2) **Reachability**: log4j 2.14.1 is only *exploitable* if the vulnerable class is loaded with attacker-controlled input — CVSS 10.0 says nothing about reachability. (3) **Naming**: log4j-core vs log4j-api — matching on name substrings creates phantom findings. (4) **Stale DBs**: a CVE record with wrong ranges mis-attributes either way. The professional answer: report *with evidence* and calibrate — the scanner says 'affected per constraint X', the analyst applies reachability and backport knowledge."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the classes."

**Candidate**: "Four records: `SemVer(major, minor, patch, prerelease)`, `VersionConstraint` (operator + SemVer), `Cve(id, packageName, List<VersionConstraint> constraints, double cvss, String description)`, and `Finding(packageName, version, Cve, matchingConstraint, Severity)`. A `Manifest` parser for `name=version` lines, a `CveDatabase` (the embedded test corpus), and `VulnerabilityScanner.scan(manifest, db) -> List<Finding>` sorted by severity. Severity is derived from the CVSS bands: 9+ CRITICAL, 7+ HIGH, 4+ MEDIUM, else LOW."

**Interviewer**: "Where does the matcher's evidence come from?"

**Candidate**: "Each finding records *which constraint matched* — 'log4j-core 2.14.1: affected by CVE-2021-44228 (constraint >= 2.0-beta9, <= 2.14.1)'. The evidence is the constraint, so the report is self-auditing: a reviewer sees exactly why the version tripped the filter, and can confirm or dismiss it (backport analysis) without re-running anything."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the SemVer comparator and the match."

**Candidate**:

```java
public record SemVer(int major, int minor, int patch, String prerelease)
        implements Comparable<SemVer> {
    public static SemVer parse(String s) {
        String core = s;
        String pre = null;
        int dash = s.indexOf('-');
        if (dash >= 0) {
            core = s.substring(0, dash);
            pre = s.substring(dash + 1).split("\\+")[0];
        }
        String[] parts = core.split("\\.");
        int mjr = Integer.parseInt(parts[0]);
        int mnr = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int ptc = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return new SemVer(mjr, mnr, ptc, pre);
    }

    @Override
    public int compareTo(SemVer o) {
        int c = Integer.compare(major, o.major);
        if (c != 0) return c;
        c = Integer.compare(minor, o.minor);
        if (c != 0) return c;
        c = Integer.compare(patch, o.patch);
        if (c != 0) return c;
        if (prerelease == null && o.prerelease == null) return 0;
        if (prerelease == null) return 1;   // release > prerelease
        if (o.prerelease == null) return -1;
        return prerelease.compareTo(o.prerelease);
    }
}
```

**Candidate**: "And the constraint test: an operator enum {GE, GT, LE, LT, EQ, NE} parsed from the range string, applied via `compareTo` — with the note that EQ on a version the distro backpatched is the *documented* false-positive trap, reported in the evidence rather than hidden."

**Interviewer**: "And the scan loop?"

**Candidate**: "For each manifest entry, look up CVEs by exact package name; if any constraint set is satisfied, emit the finding. Name matching is exact — no substring heuristics — because phantom findings from prefix matches are a documented disaster. The report sorts by CVSS descending and prints the summary: X critical, Y high, Z medium, W low."

### Part 5: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "Ten cases across four categories. Comparator: 2.14.1 < 2.15.0; 2.10.0 > 2.9.0; 1.0.0-beta < 1.0.0; 1.0.0+build == 1.0.0. Constraints: `>= 2.0, < 2.15` accepts 2.14.1 and rejects 2.15.0; the log4j beta range accepts 2.0-beta9; `= 1.1.1k` accepts only that build. Scan: log4j-core 2.14.1 → CVE-2021-44228 CRITICAL; 2.17.1 → clean; openssl 1.1.1k → CVE-2022-0778; spring 5.3.18 → CVE-2022-22965; a no-CVE package → no finding; malformed manifest lines → skipped with a warning."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "Beyond matching — where does this go?"

**Candidate**: "Four directions. (1) **SBOM integration**: the manifest becomes a real SBOM (SPDX/CycloneDX) with hashes — then package *provenance* matters: is this the package we think it is, or a dependency-confusion impostor? The scanner can't tell from the name alone. (2) **Reachability analysis**: call-graph analysis of the deployed artifact to ask whether the vulnerable method is on any execution path — the difference between 'log4j is present' (nearly every Java app) and 'log4j is exploitable'. (3) **Exploit intelligence**: CVSS says severity; EPSS says likelihood of exploitation within 30 days; the gate should use both — a CRITICAL with EPSS 0.1% and a HIGH with EPSS 90% are different operational events. (4) **CI gating economics**: block on CRITICAL+HIGH with evidence, let MEDIUM into the backlog, and never block on phantom `<=` matches without the reachability note."

---

## Extended Q&A: Follow-up Round

**Q: What does SBOM verification change about the scan?**

**A**: With an SBOM (CycloneDX/SPDX), each package carries a hash and the manifest itself is attested at build time. The scanner then answers two distinct questions: (1) is this the exact artifact we expect — provenance, tampering, dependency-confusion detection — and (2) does it contain known-bad versions — today's matcher. Provenance beats naming: an attacker publishing `log4j-core` on a shadow registry is invisible to version matching but caught by the hash.

**Q: When is a finding *not* actionable, and what do you do about it?**

**A**: Two cases. First, the package is present but the vulnerable class is unreachable — log4j-core in a batch job that never parses attacker-controlled input. Second, the distro backported the fix without bumping the version, so the constraint `<= 2.14.1` matches a patched build. Both are evidence-grade findings: the constraint says "affected per advisory", not "exploitable". The report keeps the constraint visible so the analyst can apply reachability and backport knowledge instead of paging the on-call.

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Version semantics | Numeric segments, pre-release ordering, build metadata | String compare | No comparator |
| Constraint model | Operator enum + AND-conjunction sets, evidence per finding | Simple ranges | `<=` only |
| False-positive literacy | Backport problem, reachability, exact-name matching | Mentions backports | None |
| Report | Severity bands, sorted, constraint evidence | List only | No ordering |
| Tests | Comparator + constraint + scan corpus | Happy path | None |

## Red Flags
- String comparison of versions (2.10.0 < 2.9.0 bug).
- Substring name matching (log4j-api phantom findings).
- Reporting CVSS without context (reachability/backports).
- Treating the scanner's verdict as ground truth.

## Key Takeaways
- SemVer: numeric segments, pre-release < release, build ignored.
- A finding must carry its evidence: the constraint that matched.
- Exact name matching; severity bands; sort the report.
- False positives come from backports, reachability, naming — report, don't hide.
