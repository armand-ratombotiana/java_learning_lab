package com.learning.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelenideSolutionTest {

    private SelenideSolution solution;

    @BeforeEach
    void setUp() {
        solution = new SelenideSolution();
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWindow();
    }

    @Test
    void testClick() {
        By button = By.id("submit");
        solution.click(button);
    }

    @Test
    void testType() {
        By input = By.name("username");
        solution.type(input, "testuser");
    }

    @Test
    void testGetText() {
        By heading = By.tagName("h1");
        String text = solution.getText(heading);
        assertNotNull(text);
    }

    @Test
    void testGetValue() {
        By input = By.name("email");
        String value = solution.getValue(input);
        assertNotNull(value);
    }

    @Test
    void testIsVisible() {
        By element = By.className("visible");
        boolean visible = solution.isVisible(element);
        assertNotNull(visible);
    }

    @Test
    void testFindAll() {
        By items = By.cssSelector(".item");
        List<SelenideElement> elements = solution.findAll(items);
        assertNotNull(elements);
    }

    @Test
    void testSelectOption() {
        By dropdown = By.id("country");
        solution.selectOption(dropdown, "USA");
    }
}