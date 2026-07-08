package com.security15;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SecurityTestRunner {

    private static final Pattern SQL_INJECTION = Pattern.compile(
        ".*(\\bSELECT\\b.*\\bFROM\\b|\\bUNION\\b.*\\bSELECT\\b|\\bINSERT\\b.*\\bINTO\\b|\\bDROP\\b.*\\bTABLE\\b|\\bOR\\s+1=1|'.*--).*",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern XSS_PATTERN = Pattern.compile(
        ".*(<script>|javascript:|onload=|onerror=|onclick=|alert\\().*",
        Pattern.CASE_INSENSITIVE
    );

    public List<Finding> runFuzzingTest(String endpoint, List<String> payloads) {
        List<Finding> findings = new ArrayList<>();
        for (String payload : payloads) {
            if (SQL_INJECTION.matcher(payload).matches()) {
                findings.add(new Finding("SQL Injection", endpoint, payload, "HIGH"));
            }
            if (XSS_PATTERN.matcher(payload).matches()) {
                findings.add(new Finding("XSS", endpoint, payload, "HIGH"));
            }
        }
        return findings;
    }

    public VulnReport analyzeDependencies(List<Dependency> deps) {
        VulnReport report = new VulnReport();
        report.critical = 0; report.high = 0; report.medium = 0; report.low = 0;
        report.vulnerabilities = new ArrayList<>();

        for (Dependency dep : deps) {
            if (dep.cveId != null) {
                report.vulnerabilities.add(dep);
                switch (dep.severity.toLowerCase()) {
                    case "critical" -> report.critical++;
                    case "high" -> report.high++;
                    case "medium" -> report.medium++;
                    default -> report.low++;
                }
            }
        }
        return report;
    }

    public SecurityHeaderCheck checkSecurityHeaders(Map<String, String> responseHeaders) {
        SecurityHeaderCheck check = new SecurityHeaderCheck();
        check.missingHeaders = new ArrayList<>();
        check.passed = true;

        Map<String, String> required = Map.of(
            "X-Content-Type-Options", "nosniff",
            "X-Frame-Options", "DENY",
            "Content-Security-Policy", "default-src 'self'",
            "Strict-Transport-Security", "max-age=31536000",
            "X-XSS-Protection", "1; mode=block"
        );

        for (var entry : required.entrySet()) {
            String value = responseHeaders.get(entry.getKey());
            if (value == null) {
                check.missingHeaders.add(entry.getKey());
                check.passed = false;
            }
        }
        return check;
    }

    public record Finding(String type, String endpoint, String payload, String severity) {}
    public static class Dependency { public String name, version, cveId, severity; }
    public static class VulnReport {
        public int critical, high, medium, low;
        public List<Dependency> vulnerabilities;
    }
    public static class SecurityHeaderCheck {
        public boolean passed;
        public List<String> missingHeaders;
    }

    public static void main(String[] args) {
        SecurityTestRunner runner = new SecurityTestRunner();
        var findings = runner.runFuzzingTest("/api/login", List.of("' OR '1'='1", "<script>alert(1)</script>", "valid_payload"));
        findings.forEach(f -> System.out.println(f.type + ": " + f.payload));
    }
}
