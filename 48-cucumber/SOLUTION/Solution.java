package com.learning.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;

import java.util.List;
import java.util.Map;

public class CucumberSolution {

    private String currentState;

    @Given("the user is on the login page")
    public void userOnLoginPage() {
        currentState = "login";
    }

    @When("the user enters username {string}")
    public void enterUsername(String username) {
        currentState = "username:" + username;
    }

    @When("the user enters password {string}")
    public void enterPassword(String password) {
        currentState = "password:" + password;
    }

    @And("clicks the submit button")
    public void clickSubmit() {
        currentState = "submit";
    }

    @Then("the user should be redirected to the dashboard")
    public void verifyDashboard() {
        assertTrue(currentState.contains("submit"));
    }

    @Then("an error message should be displayed")
    public void verifyError() {
        assertTrue(currentState.startsWith("login"));
    }

    @Given("the following users exist:")
    public void usersExist(DataTable table) {
        List<Map<String, String>> users = table.asMaps();
        assertFalse(users.isEmpty());
    }

    @When("the user searches for {string}")
    public void searchFor(String query) {
        currentState = "search:" + query;
    }

    @Then("the results should contain {string}")
    public void verifyResults(String expected) {
        assertTrue(currentState.contains(expected.toLowerCase()));
    }
}