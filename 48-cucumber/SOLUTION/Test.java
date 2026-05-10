package com.learning.cucumber;

import io.cucumber.java.DataTableType;
import io.cucumber.datatable.DataTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CucumberSolutionTest {

    private CucumberSolution solution;

    @BeforeEach
    void setUp() {
        solution = new CucumberSolution();
    }

    @Test
    void testUserOnLoginPage() {
        solution.userOnLoginPage();
    }

    @Test
    void testEnterUsername() {
        solution.enterUsername("testuser");
    }

    @Test
    void testEnterPassword() {
        solution.enterPassword("password123");
    }

    @Test
    void testClickSubmit() {
        solution.clickSubmit();
    }

    @Test
    void testVerifyDashboard() {
        solution.clickSubmit();
        solution.verifyDashboard();
    }

    @Test
    void testVerifyError() {
        solution.userOnLoginPage();
        solution.verifyError();
    }

    @Test
    void testUsersExist() {
        DataTable table = DataTable.create(List.of(
            Map.of("username", "user1", "email", "user1@test.com")
        ));
        solution.usersExist(table);
    }

    @Test
    void testSearchFor() {
        solution.searchFor("test query");
    }

    @Test
    void testVerifyResults() {
        solution.searchFor("test");
        solution.verifyResults("test");
    }
}