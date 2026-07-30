package com.devops.deep.lab03;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ContainerSecurityLab {
    public static void main(String[] args) {
        var scanner = new ImageScanner();
        scanner.scan("nginx:1.25");
        scanner.scan("myapp:latest");

        var dockerfile = new DockerfileAnalyzer();
        dockerfile.analyze("""
            FROM eclipse-temurin:21-jdk AS builder
            COPY . /app
            RUN javac Main.java

            FROM eclipse-temurin:21-jre
            COPY --from=builder /app/Main.class /app/
            USER 10001
            CMD ["java", "Main"]
            """);

        var seccomp = new SeccompProfile();
        seccomp.allow("read", "write", "exit", "mmap", "open", "close");

        var apparmor = new AppArmorProfile("container-secure");
        apparmor.denyWrite("/etc/hosts");
        apparmor.denyWrite("/proc/sys");

        var falco = new FalcoEngine();
        falco.addRule(new FalcoRule("Terminal shell in container", "spawned_process and container and proc.name in (bash, sh, zsh)"));
        falco.addRule(new FalcoRule("Write below /etc", "open_write and container and fd.name startswith /etc"));

        falco.evaluate(new SyscallEvent("spawned_process", Map.of("proc.name", "bash", "container", "web-app")));
        falco.evaluate(new SyscallEvent("open_write", Map.of("fd.name", "/etc/passwd", "container", "web-app")));

        System.out.println("\nSeccomp allowed: " + seccomp.allowedSyscalls());
        System.out.println("AppArmor profile: " + apparmor.name());
        System.out.println("Falco alerts triggered: " + falco.alerts());
    }
}

class ImageScanner {
    void scan(String image) {
        var vulns = List.of("CVE-2024-1234 (HIGH)", "CVE-2024-5678 (MEDIUM)");
        System.out.println("Scanning " + image + "...");
        System.out.println("  Vulnerabilities found: " + String.join(", ", vulns));
    }
}

class DockerfileAnalyzer {
    void analyze(String dockerfile) {
        boolean hasUser = dockerfile.contains("USER");
        boolean isMultiStage = dockerfile.contains("AS ");
        boolean isMinimal = dockerfile.contains("jre") || dockerfile.contains("scratch") || dockerfile.contains("alpine");
        System.out.println("Dockerfile analysis:");
        System.out.println("  Multi-stage: " + isMultiStage);
        System.out.println("  Non-root user: " + hasUser);
        System.out.println("  Minimal base: " + isMinimal);
    }
}

class SeccompProfile {
    private final Set<String> allowed = ConcurrentHashMap.newKeySet();

    void allow(String... syscalls) { allowed.addAll(List.of(syscalls)); }
    Set<String> allowedSyscalls() { return Set.copyOf(allowed); }
}

record AppArmorProfile(String name) {
    void denyWrite(String path) {
        System.out.println("AppArmor [" + name + "]: deny w " + path);
    }
}

record FalcoRule(String desc, String condition) {}
record SyscallEvent(String type, Map<String, String> fields) {}

class FalcoEngine {
    private final List<FalcoRule> rules = new ArrayList<>();
    private final List<String> alerts = new ArrayList<>();

    void addRule(FalcoRule rule) { rules.add(rule); }

    void evaluate(SyscallEvent event) {
        for (var rule : rules) {
            var conditionParts = rule.condition().split(" and ");
            boolean matches = true;
            for (var part : conditionParts) {
                var trimmed = part.trim();
                if (trimmed.contains("proc.name in")) {
                    var shell = trimmed.substring(trimmed.indexOf("(") + 1, trimmed.indexOf(")"));
                    if (shell.contains(event.fields().getOrDefault("proc.name", ""))) {
                        matches = true;
                    }
                }
                if (trimmed.contains("fd.name startswith")) {
                    var prefix = trimmed.substring(trimmed.lastIndexOf(" ") + 1);
                    if (event.fields().getOrDefault("fd.name", "").startsWith(prefix)) {
                        alerts.add("ALERT: " + rule.desc() + " in container " + event.fields().get("container"));
                    }
                }
            }
        }
    }

    List<String> alerts() { return List.copyOf(alerts); }
}
