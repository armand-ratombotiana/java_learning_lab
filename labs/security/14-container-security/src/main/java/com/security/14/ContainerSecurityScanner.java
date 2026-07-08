package com.security14;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContainerSecurityScanner {

    private final Set<String> allowedBaseImages = Set.of(
        "eclipse-temurin:21-jre", "eclipse-temurin:21-jre-alpine",
        "amazoncorretto:21", "gcr.io/distroless/java21-debian12"
    );
    private final Set<String> prohibitedPackages = Set.of(
        "curl", "wget", "netcat", "telnet", "bash", "openssh-client"
    );

    public ScanResult scanDockerfile(String dockerfileContent) {
        ScanResult result = new ScanResult();
        result.issues = new ArrayList<>();
        result.score = 100;

        for (String line : dockerfileContent.split("\n")) {
            String trimmed = line.strip();
            if (trimmed.startsWith("FROM ")) {
                String image = trimmed.substring(5).strip().split("\\s+")[0];
                if (!allowedBaseImages.contains(image)) {
                    result.issues.add("Unapproved base image: " + image);
                    result.score -= 15;
                }
            }
            if (trimmed.startsWith("USER root")) {
                result.issues.add("Running as root is not allowed");
                result.score -= 20;
            }
            if (trimmed.startsWith("ADD ")) {
                result.issues.add("Prefer COPY over ADD for security");
                result.score -= 5;
            }
            if (trimmed.startsWith("RUN ") || trimmed.startsWith("apt-get")) {
                String aptLine = trimmed.toLowerCase();
                for (String prohibited : prohibitedPackages) {
                    if (aptLine.contains(prohibited)) {
                        result.issues.add("Prohibited package: " + prohibited);
                        result.score -= 10;
                    }
                }
            }
        }
        return result;
    }

    public Set<String> detectVulnerabilities(List<Dependency> deps) {
        return deps.stream()
            .filter(d -> "critical".equalsIgnoreCase(d.severity))
            .map(d -> d.name + "@" + d.version + ": " + d.cveId)
            .collect(Collectors.toSet());
    }

    public SecurityContext createSecurityContext() {
        SecurityContext ctx = new SecurityContext();
        ctx.readOnlyRootFs = true;
        ctx.runAsUser = 10001;
        ctx.capabilities = Set.of("NET_BIND_SERVICE", "CHOWN");
        ctx.allowPrivilegeEscalation = false;
        ctx.seccompProfile = "runtime/default";
        return ctx;
    }

    public static class ScanResult {
        public List<String> issues;
        public int score;
        public boolean isPassed() { return score >= 60; }
    }

    public static class Dependency {
        public String name, version, cveId, severity;
        public Dependency(String name, String version, String cveId, String severity) {
            this.name = name; this.version = version; this.cveId = cveId; this.severity = severity;
        }
    }

    public static class SecurityContext {
        public boolean readOnlyRootFs;
        public int runAsUser;
        public Set<String> capabilities;
        public boolean allowPrivilegeEscalation;
        public String seccompProfile;
    }

    public static void main(String[] args) {
        ContainerSecurityScanner scanner = new ContainerSecurityScanner();
        String df = "FROM eclipse-temurin:21-jre\nUSER root\nCOPY target/app.jar /app/\n";
        ScanResult r = scanner.scanDockerfile(df);
        System.out.println("Score: " + r.score + " Passed: " + r.isPassed());
        r.issues.forEach(i -> System.out.println("  Issue: " + i));
    }
}
