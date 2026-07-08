package com.security17;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmartContractAuditor {

    private static final Pattern REENTRANCY = Pattern.compile(
        "\\.call\\{[^}]*\\}\\.*\\.*\\s*(.*require\\(|.*\\s*=\\s*balance)"
    );
    private static final Pattern TX_ORIGIN = Pattern.compile("tx\\.origin");
    private static final Pattern BLOCK_TIMESTAMP = Pattern.compile("block\\.timestamp");
    private static final Pattern UNCHECKED_CALL = Pattern.compile("\\.call\\{[^}]*\\}(?!\\.*require\\(success\\))");

    public AuditReport analyzeContract(String contractCode) {
        List<Vulnerability> vulns = new ArrayList<>();
        if (REENTRANCY.matcher(contractCode).find()) {
            vulns.add(new Vulnerability("Reentrancy", "HIGH",
                "External call before state update allows recursive re-entry",
                "Apply Checks-Effects-Interactions pattern"));
        }
        if (TX_ORIGIN.matcher(contractCode).find()) {
            vulns.add(new Vulnerability("tx.origin Usage", "MEDIUM",
                "tx.origin can be manipulated via phishing",
                "Use msg.sender instead of tx.origin"));
        }
        if (BLOCK_TIMESTAMP.matcher(contractCode).find()) {
            vulns.add(new Vulnerability("Timestamp Dependence", "LOW",
                "Block timestamp can be manipulated by miners",
                "Avoid using block.timestamp for critical logic"));
        }
        return new AuditReport(vulns, calculateRiskScore(vulns));
    }

    private int calculateRiskScore(List<Vulnerability> vulns) {
        int score = 100;
        for (Vulnerability v : vulns) {
            switch (v.severity) {
                case "CRITICAL" -> score -= 30;
                case "HIGH" -> score -= 20;
                case "MEDIUM" -> score -= 10;
                case "LOW" -> score -= 5;
            }
        }
        return Math.max(0, score);
    }

    public record Vulnerability(String title, String severity, String description, String remediation) {}
    public record AuditReport(List<Vulnerability> vulnerabilities, int riskScore) {}

    public static void main(String[] args) {
        SmartContractAuditor auditor = new SmartContractAuditor();
        String contract = """
            function withdraw(uint amount) public {
                require(balances[msg.sender] >= amount);
                (bool success, ) = msg.sender.call{value: amount}("");
                require(success);
                balances[msg.sender] -= amount;
            }
        """;
        AuditReport report = auditor.analyzeContract(contract);
        System.out.println("Findings: " + report.vulnerabilities().size() + ", Risk Score: " + report.riskScore());
    }
}
