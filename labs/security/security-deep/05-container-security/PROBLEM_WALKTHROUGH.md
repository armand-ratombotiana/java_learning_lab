# Problem Walkthrough: Image Vulnerability Scanner with CVE Matching

## Problem Statement

Your container images bundle hundreds of open-source packages, and you need a first-pass
vulnerability scanner: given a package manifest (lines of `name=version`) and a CVE
database, report every package that is affected by at least one known vulnerability,
sorted by severity, with the evidence of *which constraint* matched.

**Deliverable**: a Java 21+ program with a SemVer comparator, a constraint matcher, an
embedded CVE corpus, and a report printer. No external libraries.

### Constraints
- Version strings follow SemVer: `major.minor.patch[-prerelease][+build]` (pre-release optional, build metadata must be ignored).
- CVE database entries declare affected ranges as comma-separated constraint sets with
  operators `>=`, `>`, `<=`, `<`, `=`, `!=` (comma means AND).
- Matching must be **exact on package name** — no substring heuristics.
- Findings are sorted by CVSS score descending; severity bands: >= 9.0 CRITICAL, >= 7.0
  HIGH, >= 4.0 MEDIUM, else LOW.
- Malformed manifest lines are skipped with a warning; the scan must never crash.

---

## Mathematical Foundation

**SemVer ordering** (SemVer 2.0.0, §11). Compare `major`, then `minor`, then `patch` —
each as *integers*, never as strings (the string compare of `2.10.0` vs `2.9.0` says
`2.10.0 < 2.9.0` because `'1' < '9'` at the second segment's first character). Then:
- A release (`1.0.0`) is strictly greater than any of its own pre-releases (`1.0.0-beta`).
- Pre-releases compare lexicographically among themselves; build metadata (`+build`) is
  dropped before comparison.

**Affectation semantics.** A CVE record declares one *affected set* — a conjunction of
constraints (all must hold). A version `v` is affected iff `v` satisfies the conjunction:

```
affected(v, CVE) = ∀ c ∈ constraints(CVE): satisfies(c, v)
```

The scanner's core is then the composition:

```
findings = { (pkg, cve) | pkg ∈ manifest ∧ cve ∈ db ∧ name(pkg) == name(cve) ∧ affected(v(pkg), cve) }
```

This is the standard model used by OSV-style and advisory-style databases; the subtlety
(and the source of the industry's notorious false positives) is that `<= 2.14.1` matches
**every** 2.14.1 build, including distro backports that patched in place without a version
bump. We therefore attach the matching constraint to the finding as evidence so the
verdict is auditable rather than opaque.

---

## Solution Design

```
ManifestEntry(name, version)
SemVer(major, minor, patch, prerelease)        // Comparable
CmpOp {GE, GT, LE, LT, EQ, NE}                 // parses ">=" etc.
Constraint(op, version)
Cve(id, packageName, List<Constraint>, cvss, description)
Finding(packageName, version, cve, matchedConstraint)
VulnerabilityScanner.scan(manifest, db) -> List<Finding>
```

| Component | Responsibility |
|-----------|----------------|
| `SemVer.parse` | Split on `-`/`+`, numeric segments with default zeros |
| `SemVer.compareTo` | Numeric segments → pre-release rule → build ignored |
| `Constraint.satisfiedBy` | `apply(op, compareTo)`, EQ on backported builds is the documented trap |
| `CveDatabase.embedded()` | The corpus: log4j, Spring, OpenSSL, commons-text, nettle |
| `Scanner.scan` | Exact name lookup → conjunction test → Finding with evidence |
| `main` | Corpus scan, clean-image scan, sanity prints, exit code 0 |

---

## Full Java 21+ Implementation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ContainerSecurity {

    public record SemVer(int major, int minor, int patch, String prerelease)
            implements Comparable<SemVer> {

        public static SemVer parse(String s) {
            String core = s;
            String pre = null;
            int plus = core.indexOf('+');
            if (plus >= 0) core = core.substring(0, plus);   // build metadata is ignored
            int dash = core.indexOf('-');
            if (dash >= 0) {
                pre = core.substring(dash + 1);
                core = core.substring(0, dash);
            }
            String[] parts = core.split("\\.");
            int mjr = Integer.parseInt(parts[0]);
            int mnr = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int ptc = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            return new SemVer(mjr, mnr, ptc, pre);
        }

        @Override
        public String toString() {
            String s = major + "." + minor + "." + patch;
            return prerelease == null ? s : s + "-" + prerelease;
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
            if (prerelease == null) return 1;   // release > any pre-release
            if (o.prerelease == null) return -1;
            return prerelease.compareTo(o.prerelease);
        }
    }

    public enum CmpOp {
        GE(">="), GT(">"), LE("<="), LT("<"), EQ("="), NE("!=");
        final String token;
        CmpOp(String t) { this.token = t; }

        boolean test(int c) {
            return switch (this) {
                case GE -> c >= 0;
                case GT -> c > 0;
                case LE -> c <= 0;
                case LT -> c < 0;
                case EQ -> c == 0;
                case NE -> c != 0;
            };
        }

        static CmpOp from(String t) {
            for (CmpOp op : values()) if (op.token.equals(t)) return op;
            throw new IllegalArgumentException("unknown operator " + t);
        }
    }

    public record Constraint(CmpOp op, SemVer version) {
        public static Constraint parse(String raw) {
            String s = raw.trim();
            for (int len = 2; len >= 1; len--) {
                if (s.startsWith(s.substring(0, len)) && isOp(s.substring(0, len))) {
                    return new Constraint(CmpOp.from(s.substring(0, len)),
                            SemVer.parse(s.substring(len).trim()));
                }
            }
            return new Constraint(CmpOp.EQ, SemVer.parse(s));
        }

        private static boolean isOp(String t) {
            return t.equals(">=") || t.equals(">") || t.equals("<=") || t.equals("<")
                    || t.equals("=") || t.equals("!=");
        }

        boolean satisfiedBy(SemVer v) { return op.test(v.compareTo(version)); }

        public String toString() { return op.token + " " + version; }
    }

    public record Cve(String id, String packageName, List<Constraint> constraints,
                      double cvss, String description) {
        boolean affects(SemVer v) {
            for (Constraint c : constraints) if (!c.satisfiedBy(v)) return false;
            return true;
        }
    }

    public record ManifestEntry(String name, SemVer version) {}

    public record Finding(String packageName, SemVer version, Cve cve, String matchedConstraints) {
        public String severity() {
            double s = cve.cvss();
            if (s >= 9.0) return "CRITICAL";
            if (s >= 7.0) return "HIGH";
            if (s >= 4.0) return "MEDIUM";
            return "LOW";
        }
    }

    public static final class VulnerabilityScanner {
        private final Map<String, List<Cve>> byName = new TreeMap<>();

        public VulnerabilityScanner(List<Cve> db) {
            for (Cve cve : db) byName.computeIfAbsent(cve.packageName(), k -> new ArrayList<>()).add(cve);
        }

        public List<Finding> scan(List<ManifestEntry> manifest) {
            List<Finding> out = new ArrayList<>();
            for (ManifestEntry e : manifest) {
                List<Cve> candidates = byName.get(e.name());
                if (candidates == null) continue;
                for (Cve cve : candidates) {
                    if (!cve.affects(e.version())) continue;
                    String matched = cve.constraints().stream()
                            .map(Constraint::toString)
                            .collect(java.util.stream.Collectors.joining(" AND "));
                    out.add(new Finding(e.name(), e.version(), cve, matched));
                }
            }
            out.sort((a, b) -> Double.compare(b.cve().cvss(), a.cve().cvss()));
            return out;
        }
    }

    public static List<Cve> embeddedDatabase() {
        return List.of(
                new Cve("CVE-2021-44228", "log4j-core",
                        List.of(Constraint.parse(">= 2.0-beta9"), Constraint.parse("<= 2.14.1")),
                        10.0, "Log4Shell: JNDI remote class loading"),
                new Cve("CVE-2022-22965", "spring-core",
                        List.of(Constraint.parse(">= 5.3.0"), Constraint.parse("< 5.3.18")),
                        9.8, "Spring4Shell: data binding RCE"),
                new Cve("CVE-2022-0778", "openssl",
                        List.of(Constraint.parse(">= 3.0.0"), Constraint.parse("< 3.0.2")),
                        7.5, "BN_mod_sqrt infinite loop DoS"),
                new Cve("CVE-2022-42889", "commons-text",
                        List.of(Constraint.parse(">= 1.5"), Constraint.parse("< 1.10.0")),
                        9.8, "Text4Shell: interpolation RCE"),
                new Cve("CVE-2021-20305", "nettle",
                        List.of(Constraint.parse("< 3.7.2")),
                        7.5, "Classic side-channel in ECDSA"));
    }

    public static List<ManifestEntry> parseManifest(String manifest) {
        List<ManifestEntry> entries = new ArrayList<>();
        for (String line : manifest.split("\n")) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("#")) continue;
            int eq = t.indexOf('=');
            if (eq <= 0 || eq == t.length() - 1) {
                System.out.println("WARN: skipping malformed line: " + t);
                continue;
            }
            try {
                entries.add(new ManifestEntry(t.substring(0, eq).trim(),
                        SemVer.parse(t.substring(eq + 1).trim())));
            } catch (NumberFormatException ex) {
                System.out.println("WARN: bad version on line: " + t);
            }
        }
        return entries;
    }

    public static void main(String[] args) {
        System.out.println("== SemVer comparator sanity ==");
        List<SemVer> versions = List.of("2.14.1", "2.15.0", "2.10.0", "2.9.0",
                "1.0.0-beta", "1.0.0", "1.0.0+build7").stream()
                .map(SemVer::parse).toList();
        for (SemVer v : versions) System.out.println(v + "  ->  " + SemVer.parse("2.14.1").compareTo(v));

        System.out.println();
        System.out.println("== Vulnerable image scan ==");
        String manifest = """
                log4j-core=2.14.1
                spring-core=5.3.16
                openssl=3.0.1
                commons-text=1.9
                nettle=3.7.1
                postgresql=14.4
                # comment lines are ignored
                badentry-noversion
                """;
        List<ManifestEntry> entries = parseManifest(manifest);
        VulnerabilityScanner scanner = new VulnerabilityScanner(embeddedDatabase());
        List<Finding> findings = scanner.scan(entries);
        for (Finding f : findings) {
            System.out.printf("  %-12s %-9s %-6s %s (constraint: %s)%n",
                    f.packageName(), f.version(), f.severity(), f.cve().id(), f.matchedConstraints());
        }
        System.out.println("  total findings: " + findings.size());

        System.out.println();
        System.out.println("== Patched image: expect zero findings ==");
        String patched = """
                log4j-core=2.17.1
                spring-core=5.3.20
                openssl=3.0.3
                commons-text=1.10.0
                nettle=3.7.2
                """;
        List<Finding> none = scanner.scan(parseManifest(patched));
        System.out.println("  findings: " + none.size());
    }
}
```

---

## Walkthrough of a Run

SemVer comparator probes (as printed by `main`):

| `v` | `2.14.1`.compareTo(`v`) | Why |
|-----|--------------------------|-----|
| `2.14.1` | 0 | identical |
| `2.15.0` | -1 | minor 14 < 15 → 2.14.1 < 2.15.0 |
| `2.10.0` | +1 | minor 14 > 10 — the case that breaks string compare |
| `2.9.0` | +1 | minor 14 > 9 |
| `1.0.0-beta` | +1 | major 2 > 1 |
| `1.0.0` | +1 | major 2 > 1 |
| `1.0.0+build7` | +1 | major 2 > 1 (build ignored) |

Vulnerable image scan:

| Package | Version | Finding | Matched constraint |
|---------|---------|---------|--------------------|
| log4j-core | 2.14.1 | CVE-2021-44228 CRITICAL (10.0) | `>= 2.0-beta9` AND `<= 2.14.1` |
| spring-core | 5.3.16 | CVE-2022-22965 CRITICAL (9.8) | `>= 5.3.0` AND `< 5.3.18` |
| commons-text | 1.9 | CVE-2022-42889 CRITICAL (9.8) | `>= 1.5` AND `< 1.10.0` |
| openssl | 3.0.1 | CVE-2022-0778 HIGH (7.5) | `>= 3.0.0` AND `< 3.0.2` |
| nettle | 3.7.1 | CVE-2021-20305 HIGH (7.5) | `< 3.7.2` |
| postgresql | 14.4 | — | no matching CVE |
| badentry-noversion | — | skipped | warning printed |

The patched image (2.17.1 / 5.3.20 / 3.0.3 / 1.10.0 / 3.7.2) yields zero findings — each
fixed version sits exactly at or beyond the exclusion bound. Sort order: CRITICALs first
(by CVSS), then HIGHs; log4j's 10.0 leads.

---

## Verification

| # | Input | Expected |
|---|-------|----------|
| 1 | `2.14.1` vs `2.15.0` | `<` (minor 14 < 15) |
| 2 | `2.10.0` vs `2.9.0` | `>` (numeric minor — string compare would lie) |
| 3 | `1.0.0-beta` vs `1.0.0` | pre-release `<` release |
| 4 | `1.0.0+build7` vs `1.0.0` | `==` (build dropped) |
| 5 | log4j-core 2.0-beta9 | affected (`>= 2.0-beta9` includes pre-releases) |
| 6 | log4j-core 2.15.0 | NOT affected (`<= 2.14.1` fails) |
| 7 | `= 3.0.1` on 3.0.1 | affected (exact match) |
| 8 | log4j-api 2.14.1 | no finding (exact name match only) |
| 9 | malformed line | warning, no crash |
| 10 | empty manifest | zero findings |

---

## Complexity

- `SemVer.compareTo`: O(1) — fixed number of segments.
- Constraint test: O(k) per (package, CVE) pair, k = constraints per CVE.
- Scan: O(m · k̄) after a single O(d) index build over the database (name → CVEs),
  plus O(f log f) for the severity sort — no CVE lookup scans the whole DB per package.

## Edge Cases

- **Pre-release bounds**: the log4j beta range deliberately includes `-beta9`; a naive
  `>= 2.0` would have excluded the beta line.
- **Build metadata**: `3.0.1+openssl-fips` must match `= 3.0.1`.
- **Malformed lines**: never crash the gate; warn and continue.
- **Backported patches**: a finding is *not* proof of exploitability — the evidence
  constraint is the analyst's audit trail (distro-backport reality).

## Follow-ups

1. Reachability analysis (call-graph) to cut phantom findings — log4j is *present* in
   nearly every JVM image; it is *exploitable* far less often.
2. EPSS weighting alongside CVSS for prioritization.
3. SBOM ingestion (CycloneDX/SPDX) with hash verification against provenance.
4. CI gate policy: fail on CRITICAL/HIGH, defer MEDIUM, always carry evidence to the ticket.
5. Distro-override database to suppress backport false positives.
