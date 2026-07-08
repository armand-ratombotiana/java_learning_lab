package com.dataeng.nineteen;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class PiiDetector {
    private static final Map<String, Pattern> PATTERNS = new LinkedHashMap<>();
    static {
        PATTERNS.put("EMAIL", Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"));
        PATTERNS.put("SSN", Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$"));
        PATTERNS.put("PHONE", Pattern.compile("^\\+?1?[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$"));
        PATTERNS.put("CREDIT_CARD", Pattern.compile("^\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}$"));
        PATTERNS.put("ZIP_CODE", Pattern.compile("^\\d{5}(-\\d{4})?$"));
        PATTERNS.put("IP_ADDRESS", Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"));
    }

    private static final Set<String> COLUMN_NAME_PATTERNS = Set.of(
        "email", "e-mail", "ssn", "social", "phone", "telephone", "mobile",
        "credit_card", "cc_number", "card_number", "password", "secret",
        "passport", "driver_license", "bank_account", "routing_number");

    public record PiiResult(String piiType, double confidence, String matchedPattern) {}

    public PiiResult analyzeColumn(String columnName, List<String> sampleValues) {
        String colLower = columnName.toLowerCase().replaceAll("[^a-z0-9]", "");

        if (COLUMN_NAME_PATTERNS.contains(colLower)) {
            double valueConfidence = 0.8;
            for (var entry : PATTERNS.entrySet()) {
                if (entry.getKey().equals("EMAIL") && colLower.contains("email")) valueConfidence = 0.95;
                if (entry.getKey().equals("SSN") && colLower.contains("ssn")) valueConfidence = 0.95;
            }
            return new PiiResult(detectTypeFromName(colLower), valueConfidence, "column_name");
        }

        if (sampleValues == null || sampleValues.isEmpty()) {
            return new PiiResult("UNKNOWN", 0, "no_data");
        }

        Map<String, Long> matches = new HashMap<>();
        for (String value : sampleValues) {
            if (value == null) continue;
            for (var entry : PATTERNS.entrySet()) {
                if (entry.getValue().matcher(value).matches()) {
                    matches.merge(entry.getKey(), 1L, Long::sum);
                }
            }
        }

        if (matches.isEmpty()) return new PiiResult("UNKNOWN", 0, "no_match");

        String bestType = matches.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).orElse("UNKNOWN");

        double matchRate = (double) matches.get(bestType) / Math.max(1, sampleValues.size());
        return new PiiResult(bestType, matchRate, "value_pattern");
    }

    private String detectTypeFromName(String name) {
        if (name.contains("email")) return "EMAIL";
        if (name.contains("ssn")) return "SSN";
        if (name.contains("phone") || name.contains("mobile") || name.contains("telephone")) return "PHONE";
        if (name.contains("credit") || name.contains("card_number") || name.contains("cc_")) return "CREDIT_CARD";
        if (name.contains("password") || name.contains("secret")) return "PASSWORD";
        if (name.contains("passport")) return "PASSPORT";
        if (name.contains("bank") || name.contains("routing")) return "BANK_ACCOUNT";
        return "PII";
    }
}
