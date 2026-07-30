package com.security.deep.lab04;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class SastDast {

    public static List<Finding> sastScan(String sourceCode) {
        List<Finding> findings = new ArrayList<>();
        String[] lines = sourceCode.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNum = i + 1;

            if (line.contains("Statement.executeQuery") || line.contains("executeQuery(")) {
                if (!line.toLowerCase().contains("preparedstatement")) {
                    findings.add(new Finding("SQL_INJECTION", "Possible SQL injection",
                        lineNum, "HIGH", "Use PreparedStatement with parameterized queries"));
                }
            }

            if (line.contains("<script>") || line.contains("innerHTML") || line.contains("document.write")) {
                findings.add(new Finding("XSS", "Potential XSS vulnerability",
                    lineNum, "HIGH", "Use output encoding and Content-Security-Policy"));
            }

            Matcher pwMatcher = Pattern.compile("password\\s*=\\s*\"[^\"]+\"").matcher(line);
            if (pwMatcher.find()) {
                findings.add(new Finding("HARDCODED_SECRET", "Hardcoded password detected",
                    lineNum, "CRITICAL", "Use environment variables or vault"));
            }

            if (line.contains("= eval(") || line.contains("= eval (")) {
                findings.add(new Finding("CODE_INJECTION", "Eval usage detected",
                    lineNum, "HIGH", "Avoid eval(); use safe parsers"));
            }

            if (line.matches(".*\\b(toString|concat)\\(\\s*\"[^\"]*\\+.*")) {
                findings.add(new Finding("STRING_INJECTION", "String concatenation in sensitive context",
                    lineNum, "MEDIUM", "Use StringBuilder or format strings safely"));
            }
        }
        return findings;
    }

    public static record Finding(String type, String description, int line, String severity, String recommendation) {}

    public static List<Map<String, String>> dastScan(String baseUrl, int timeoutMs) {
        List<Map<String, String>> findings = new ArrayList<>();
        List<String> testPaths = Arrays.asList(
            "/admin", "/config", "/.env", "/WEB-INF/web.xml",
            "/api/users", "/login", "/../../etc/passwd",
            "/?id=1' OR '1'='1", "/<script>alert(1)</script>"
        );
        for (String path : testPaths) {
            try {
                URL url = URI.create(baseUrl + path).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(timeoutMs);
                conn.setReadTimeout(timeoutMs);
                conn.setRequestMethod("GET");
                conn.setInstanceFollowRedirects(false);
                int status = conn.getResponseCode();
                int contentLength = conn.getContentLength();

                Map<String, String> finding = new LinkedHashMap<>();
                finding.put("path", path);
                finding.put("status", String.valueOf(status));

                if (status == 200 && contentLength > 0) {
                    finding.put("severity", "MEDIUM");
                    finding.put("description", "Path accessible: " + path);
                    if (path.contains("' OR")) {
                        finding.put("severity", "CRITICAL");
                        finding.put("description", "Possible SQL injection: " + path);
                    }
                    if (path.contains("<script>")) {
                        finding.put("severity", "CRITICAL");
                        finding.put("description", "Reflected XSS vector: " + path);
                    }
                } else if (status >= 500) {
                    finding.put("severity", "HIGH");
                    finding.put("description", "Server error on path: " + path);
                } else {
                    finding.put("severity", "INFO");
                    finding.put("description", "Path returned " + status + ": " + path);
                }
                findings.add(finding);
                conn.disconnect();
            } catch (Exception ignored) {}
        }
        return findings;
    }

    public static String generateReport(List<Finding> sastFindings, List<Map<String, String>> dastFindings) {
        StringBuilder sb = new StringBuilder("=== SAST/DAST Security Report ===\n\n");
        sb.append("SAST Findings (").append(sastFindings.size()).append("):\n");
        for (Finding f : sastFindings) {
            sb.append("  [").append(f.severity()).append("] ")
              .append(f.type()).append(" (line ").append(f.line()).append("): ")
              .append(f.description()).append("\n");
            sb.append("    Fix: ").append(f.recommendation()).append("\n");
        }
        sb.append("\nDAST Findings (").append(dastFindings.size()).append("):\n");
        for (Map<String, String> f : dastFindings) {
            sb.append("  [").append(f.get("severity")).append("] ")
              .append(f.get("path")).append(": ")
              .append(f.get("description")).append("\n");
        }
        return sb.toString();
    }
}
