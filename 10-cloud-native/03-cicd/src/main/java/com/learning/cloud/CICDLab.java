package com.learning.cloud;

public class CICDLab {

    public static void main(String[] args) {
        System.out.println("=== CI/CD Learning Lab ===\n");

        demonstrateCI();
        demonstrateCD();
        demonstrateGitHubActions();
    }

    private static void demonstrateCI() {
        System.out.println("--- Continuous Integration ---");
        System.out.println("1. Code pushed to repository");
        System.out.println("2. Automated build triggered");
        System.out.println("3. Unit tests executed");
        System.out.println("4. Code quality checks");
        System.out.println("5. Artifact created");
    }

    private static void demonstrateCD() {
        System.out.println("\n--- Continuous Deployment ---");
        System.out.println("1. Artifact stored");
        System.out.println("2. Deploy to staging");
        System.out.println("3. Integration tests");
        System.out.println("4. Deploy to production");
        System.out.println("5. Monitor & rollback");
    }

    private static void demonstrateGitHubActions() {
        System.out.println("\n--- GitHub Actions Example ---");
        System.out.println("""
        name: CI
        on: [push, pull_request]
        jobs:
          build:
            runs-on: ubuntu-latest
            steps:
              - uses: actions/checkout@v3
              - name: Set up JDK 21
                uses: actions/setup-java@v3
                with:
                  java-version: '21'
              - name: Build
                run: mvn clean package
              - name: Test
                run: mvn test
        """);
    }
}