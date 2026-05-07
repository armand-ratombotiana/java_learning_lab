package com.learning.cucumber;

public class CucumberLab {

    public static void main(String[] args) {
        System.out.println("=== Cucumber BDD Lab ===\n");

        System.out.println("1. Feature File Example:");
        System.out.println("""
            Feature: User Login
              Scenario: Successful login
                Given a user exists
                When I login with valid credentials
                Then access should be granted
                And user should see dashboard
            """);

        System.out.println("2. Step Definitions:");
        System.out.println("   @Given(\"a user exists\") - Setup test data");
        System.out.println("   @When(\"I login with valid credentials\") - Perform action");
        System.out.println("   @Then(\"access should be granted\") - Assert result");
        System.out.println("   @Then(\"user should see dashboard\") - Assert navigation");

        System.out.println("\n3. Cucumber Benefits:");
        System.out.println("   - Human-readable Gherkin syntax");
        System.out.println("   - Bridge between technical and non-technical stakeholders");
        System.out.println("   - Reusable step definitions");
        System.out.println("   - Integration with JUnit/TestNG");

        System.out.println("\n=== Cucumber BDD Lab Complete ===");
    }
}