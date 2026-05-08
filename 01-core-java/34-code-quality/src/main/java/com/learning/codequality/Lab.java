package com.learning.codequality;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Code Quality Lab ===\n");

        staticAnalysis();
        codeReview();
        testingPractices();
        codeMetrics();
        cleanCode();
    }

    static void staticAnalysis() {
        System.out.println("--- Static Code Analysis ---");

        for (var r : List.of("SonarQube S1128: Remove unused imports (MINOR)",
                "SonarQube S106: Replace System.out with logger (MAJOR)",
                "PMD CyclomaticComplexity: max complexity 10 (CRITICAL)",
                "Checkstyle LineLength: max 120 chars (MAJOR)",
                "SpotBugs NP_NULL_PARAM_DEREF: null deref (CRITICAL)"))
            System.out.println("  " + r);

        System.out.println("\n  Build pipeline:");
        System.out.println("  mvn verify -> checkstyle -> pmd -> spotbugs -> jacoco -> sonar");

        System.out.println("\n  Severity: BLOCKER, CRITICAL, MAJOR, MINOR, INFO");
    }

    static void codeReview() {
        System.out.println("\n--- Code Review Practices ---");

        for (var c : List.of("Correctness: logic errors, edge cases?",
                "Security: input validation, SQL injection?",
                "Performance: N+1 queries, memory leaks?",
                "Readability: clear naming and structure?",
                "Test coverage: all paths tested?",
                "Error handling: exceptions handled properly?",
                "Concurrency: thread-safe operations?"))
            System.out.println("  " + c);

        System.out.println("\n  Anti-patterns:");
        for (var a : List.of("Nitpicking (formatting over logic)",
                "Rubber-stamping (approving without review)",
                "Bikeshedding (trivial details)",
                "Gatekeeping (unnecessary demands)"))
            System.out.println("  " + a);
    }

    static void testingPractices() {
        System.out.println("\n--- Testing Practices ---");

        for (var t : List.of("Unit: single class, every build, OrderServiceTest",
                "Integration: multiple modules, every build, OrderRepositoryTest",
                "Component: one layer, CI pipeline, UserControllerTest",
                "Contract: API interfaces, CI pipeline",
                "E2E: full system, pre-release",
                "Performance: system under load, pre-release"))
            System.out.println("  " + t);

        System.out.println("\n  Test Pyramid:");
        System.out.println("  E2E: 5%   Integration: 15%   Unit: 80%");

        System.out.println("\n  Best practices:");
        for (var p : List.of("FIRST: Fast, Isolated, Repeatable, Self-verifying, Timely",
                "Arrange-Act-Assert pattern", "One behavior per test",
                "Mock external dependencies", "Test behavior, not implementation"))
            System.out.println("  " + p);
    }

    static void codeMetrics() {
        System.out.println("\n--- Code Metrics ---");

        for (var m : List.of("Cyclomatic complexity: < 10 per method",
                "Method length: < 20 lines", "Parameter count: < 4",
                "Class coupling: < 10", "Depth of inheritance: < 5",
                "Test coverage: > 80%", "Duplicate lines: < 3%"))
            System.out.println("  " + m);

        System.out.println("\n  Quality gates:");
        for (var g : List.of("Reliability: A (no blocker bugs)",
                "Security: A (no critical vulns)",
                "Maintainability: A (debt < 5%)",
                "Coverage: > 80%", "Duplications: < 3%"))
            System.out.println("  " + g);

        System.out.println("\n  Maven quality plugins:");
        System.out.println("""
            maven-checkstyle-plugin (code style)
            jacoco-maven-plugin (coverage, haltOnFailure=true, min=0.80)
            maven-pmd-plugin, spotbugs-maven-plugin""");
    }

    static void cleanCode() {
        System.out.println("\n--- Clean Code Principles ---");

        for (var p : List.of(
            "Meaningful names: elapsedDays not d",
            "Small functions: one thing per method",
            "No side effects: don't modify state unexpectedly",
            "DRY: Don't Repeat Yourself (extract to class)",
            "KISS: Keep It Simple (avoid over-engineering)",
            "YAGNI: You Ain't Gonna Need It (add when needed)",
            "Fail fast: validate early, fail immediately",
            "Tell don't ask: encapsulate decisions"))
            System.out.println("  " + p);

        System.out.println("\n  Tooling ecosystem:");
        for (var t : List.of("SonarQube: continuous inspection platform",
                "Checkstyle: coding standards", "PMD: code smells",
                "SpotBugs: bug patterns via bytecode", "JaCoCo: coverage",
                "ArchUnit: architecture tests", "Error Prone: compile-time bugs",
                "OWASP Dependency Check: CVE detection"))
            System.out.println("  " + t);

        System.out.println("\n  IDE integrations: IntelliJ (SonarLint), Eclipse (Checkstyle), VS Code (SonarLint)");
    }
}
