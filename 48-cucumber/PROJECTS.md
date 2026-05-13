# Cucumber - Projects

This document contains two complete projects demonstrating Cucumber BDD testing: a mini-project for learning feature file writing and step definitions, and a real-world project implementing production-grade BDD tests.

## Mini-Projects by Concept

### 1. Feature Files (2 hours)
Write Gherkin feature files with scenarios, backgrounds, and scenario outlines. Use proper Given-When-Then structure.

### 2. Step Definitions (2 hours)
Create Java step definitions with regular expressions. Implement parameter handling and data tables.

### 3. Hooks & Tags (2 hours)
Configure before/after hooks for setup/teardown. Use tags for test organization and filtering.

### 4. Parameterized Tests (2 hours)
Implement scenario outlines with examples. Use data tables and scenario context for complex flows.

### Real-world: BDD Testing Framework
Build comprehensive BDD testing framework with reusable steps, custom formatters, and reporting.

---

## Project 1: Cucumber Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Cucumber concepts including feature files, step definitions, and scenario outlines. It serves as a learning starting point for BDD testing.

### Project Structure

```
cucumber-basics/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── cucumber/
                        └── Steps.java
```

### Steps.java

```java
package com.learning.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import static org.junit.jupiter.api.Assertions.*;

public class Steps {
    
    private String user;
    private String product;
    private boolean isLoggedIn;
    private String cartItem;
    
    @Given("I am a registered user")
    public void iAmRegisteredUser() {
        user = "testuser";
        isLoggedIn = false;
    }
    
    @Given("I am logged in")
    public void iAmLoggedIn() {
        isLoggedIn = true;
    }
    
    @When("I view the product catalog")
    public void iViewProductCatalog() {
        product = "laptop";
    }
    
    @When("I add {string} to cart")
    public void iAddToCart(String productName) {
        cartItem = productName;
    }
    
    @Then("I should see the product listed")
    public void shouldSeeProductListed() {
        assertNotNull(product);
    }
    
    @Then("the cart should contain {string}")
    public void cartShouldContain(String item) {
        assertEquals(item, cartItem);
    }
    
    @And("the quantity should be {int}")
    public void quantityShouldBe(int qty) {
        assertTrue(qty > 0);
    }
}
```

### Build and Run

```bash
cd cucumber-basics
mvn test
```

## Project 2: Production BDD Testing

### Overview

This real-world project implements comprehensive BDD tests with Page Object pattern, data tables, and hooks.

### Structure

```
cucumber-production/
├── pom.xml
├── src/test/java/com/learning/cucumber/
└── src/test/resources/features/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>cucumber-production</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <cucumber.version>7.15.0</cucumber.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>${cucumber.version}</version>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit-platform-engine</artifactId>
            <version>${cucumber.version}</version>
        </dependency>
    </dependencies>
</project>
```

### RunCucumberTest.java

```java
package com.learning.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.learning.cucumber"},
    plugin = {"pretty", "html:target/report.html"}
)
public class RunCucumberTest {}
```

### Hooks.java

```java
package com.learning.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class Hooks {
    
    @Before
    public void setup(Scenario scenario) {
        System.out.println("Starting: " + scenario.getName());
    }
    
    @After
    public void teardown(Scenario scenario) {
        System.out.println("Finished: " + scenario.getStatus());
    }
}
```

### OrderSteps.java

```java
package com.learning.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import java.util.List;
import java.util.Map;

public class OrderSteps {
    
    private List<Map<String, String>> cartItems;
    private String orderId;
    
    @Given("I have items in my cart:")
    public void haveItemsInCart(DataTable table) {
        cartItems = table.asMaps();
    }
    
    @When("I checkout")
    public void iCheckout() {
        orderId = "ORD-" + System.currentTimeMillis();
    }
    
    @Then("order {string} should be created")
    public void orderShouldBeCreated(String orderNum) {
        assertNotNull(orderId);
    }
}
```

### Build and Run

```bash
cd cucumber-production
mvn test

# With specific tag
mvn test -Dcucumber.filter.tags="@smoke"
```