package com.learning.lab.module48;

import io.cucumber.java.*;
import io.cucumber.java.en.*;
import io.cucumber.java.zh_cn.当;
import io.cucumber.java.zh_cn.那么;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 48: Cucumber Lab ===\n");

        System.out.println("1. BDD Concepts:");
        System.out.println("   - Feature: Business requirement");
        System.out.println("   - Scenario: Test case");
        System.out.println("   - Step: Individual action");
        System.out.println("   - Background: Pre-conditions");

        System.out.println("\n2. Feature File:");
        featureFileDemo();

        System.out.println("\n3. Step Definitions:");
        stepDefinitionsDemo();

        System.out.println("\n4. Scenario Types:");
        scenarioTypesDemo();

        System.out.println("\n5. Data Tables:");
        dataTablesDemo();

        System.out.println("\n6. Hooks:");
        hooksDemo();

        System.out.println("\n7. Tags:");
        tagsDemo();

        System.out.println("\n8. Assertions:");
        assertionsDemo();

        System.out.println("\n=== Cucumber Lab Complete ===");
    }

    static void featureFileDemo() {
        System.out.println("   Feature File Structure:");
        System.out.println("   Feature: User login");
        System.out.println("     As a user");
        System.out.println("     I want to login");
        System.out.println("     So that I can access my account");
        System.out.println("");
        System.out.println("     Scenario: Successful login");
        System.out.println("       Given I am on the login page");
        System.out.println("       When I enter valid credentials");
        System.out.println("       Then I should see the dashboard");
    }

    static void stepDefinitionsDemo() {
        System.out.println("   Step Definition Annotations:");
        System.out.println("   @Given(\"I am on the login page\")");
        System.out.println("   public void iAmOnTheLoginPage() { }");
        System.out.println("");
        System.out.println("   @When(\"I enter valid credentials\")");
        System.out.println("   public void iEnterValidCredentials() { }");
        System.out.println("");
        System.out.println("   @Then(\"I should see the dashboard\")");
        System.out.println("   public void iShouldSeeTheDashboard() { }");

        System.out.println("\n   Regular Expressions:");
        System.out.println("   @Given(\"I am on the (.+) page\")");
        System.out.println("   public void onPage(String page) { }");
    }

    static void scenarioTypesDemo() {
        System.out.println("   Scenario Types:");
        System.out.println("   - Basic: Single flow");
        System.out.println("   - Scenario Outline: Parametrized");
        System.out.println("   - Background: Preconditions");
        System.out.println("   - Rule: Group scenarios");
        System.out.println("   - Examples: Data-driven");

        System.out.println("\n   Scenario Outline Example:");
        System.out.println("   Scenario Outline: Login with various users");
        System.out.println("     Given user \"<username>\" with password \"<password>\"");
        System.out.println("     When I login");
        System.out.println("     Then result should be \"<result>\"");
        System.out.println("     Examples:");
        System.out.println("       | username | password | result |");
        System.out.println("       | admin    | admin123 | success |");
    }

    static void dataTablesDemo() {
        System.out.println("   Data Table Types:");
        System.out.println("   1. List<List<String>>:");
        System.out.println("      | key  | value |");
        System.out.println("      | age  | 25    |");

        System.out.println("\n   2. Map<String, String>:");
        System.out.println("      | name | John |");
        System.out.println("      | age  | 25   |");

        System.out.println("\n   3. DataTable (Typed):");
        System.out.println("      Given users exist:");
        System.out.println("        | name | email |");
        System.out.println("        | John | j@g  |");

        System.out.println("\n   4. Custom Type:");
        System.out.println("      Given I have products:");
        System.out.println("        | Product |");
        System.out.println("        | Laptop  |");
    }

    static void hooksDemo() {
        System.out.println("   Cucumber Hooks:");
        System.out.println("   @Before - Run before each scenario");
        System.out.println("   @After - Run after each scenario");
        System.out.println("   @BeforeStep - Run before each step");
        System.out.println("   @AfterStep - Run after each step");

        System.out.println("\n   Hook Order:");
        System.out.println("   @Before(order=1) - First");
        System.out.println("   @Before(order=2) - Second");
        System.out.println("   ...steps...");
        System.out.println("   @After(order=2) - Second");
        System.out.println("   @After(order=1) - First");
    }

    static void tagsDemo() {
        System.out.println("   Tags:");
        System.out.println("   @smoke - Quick tests");
        System.out.println("   @regression - Full test suite");
        System.out.println("   @wip - Work in progress");
        System.out.println("   @integration - Integration tests");

        System.out.println("\n   Tag Expressions:");
        System.out.println("   @smoke and @regression - Both tags");
        System.out.println("   @smoke or @integration - Either tag");
        System.out.println("   not @wip - Exclude tag");
        System.out.println("   @smoke and not @slow - Complex expression");
    }

    static void assertionsDemo() {
        System.out.println("   Cucumber Assertions:");
        System.out.println("   assertEquals(expected, actual)");
        System.out.println("   assertTrue(condition)");
        System.out.println("   assertFalse(condition)");
        System.out.println("   assertNull(object)");
        System.out.println("   assertNotNull(object)");

        System.out.println("\n   AssertJ (Recommended):");
        System.out.println("   assertThat(actual).isEqualTo(expected)");
        System.out.println("   assertThat(list).hasSize(3)");
        System.out.println("   assertThat(user.getName()).isEqualTo(\"John\")");
    }
}

@Given("user {string} exists")
public void userExists(String username) { }

@When("I login with username and password")
public void login() { }

@Then("I should see error message")
public void errorMessage() { }