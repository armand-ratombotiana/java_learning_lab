# 48 - Cucumber Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Feature | Business-readable test specification |
| Scenario | Executable example of feature behavior |
| Step Definition | Glue code binding Gherkin to Java |
| Background | Steps run before each scenario |
| Scenario Outline | Parametrized scenario with examples |
| Hooks | Pre/post scenario execution logic |

## Gherkin Syntax

```gherkin
Feature: User login
  As a registered user
  I want to log in
  So that I can access my account

  Background:
    Given the user is on the login page

  @smoke
  Scenario: Successful login
    When I enter valid credentials
    And I click the login button
    Then I should be redirected to the dashboard

  @smoke @negative
  Scenario: Invalid password
    When I enter an invalid password
    And I click the login button
    Then I should see an error message

  @regression
  Scenario Outline: Login with different user types
    Given a <user_type> user
    When they log in with valid credentials
    Then they should see their <expected_page>

    Examples:
      | user_type | expected_page |
      | admin     | admin dashboard |
      | standard  | user dashboard  |
```

## Step Definitions

```java
// Basic step definition
@Given("the user is on the login page")
public void theUserIsOnTheLoginPage() {
    driver.get("https://example.com/login");
}

@When("I enter valid credentials")
public void iEnterValidCredentials() {
    driver.findElement(By.id("username")).sendKeys("testuser");
    driver.findElement(By.id("password")).sendKeys("password123");
}

@Then("I should be redirected to the dashboard")
public void iShouldBeRedirectedToTheDashboard() {
    assertThat(driver.getCurrentUrl()).contains("/dashboard");
}

// Data table handling
@Given("the following users exist:")
public void theFollowingUsersExist(DataTable dataTable) {
    List<User> users = dataTable.asList(User.class);
    userRepository.saveAll(users);
}

// Doc string
@Then("the error message should contain:")
public void theErrorMessageShouldContain(String docString) {
    String message = driver.findElement(By.className("error")).getText();
    assertThat(message).contains(docString);
}
```

## Hooks and Context

```java
public class Hooks {
    @Before(order = 1)
    public void setup(Scenario scenario) {
        // Setup code before each scenario
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            // Capture screenshot on failure
            byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure");
        }
    }

    @AfterAll
    public static void closeBrowser() {
        driver.quit();
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        // Runs before each step
    }
}
```

## Configuration

```java
// CucumberOptions annotation
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.example.steps", "com.example.hooks"},
    tags = "@smoke and not @manual",
    plugin = {
        "pretty",
        "html:target/cucumber.html",
        "json:target/cucumber.json",
        "junit:target/cucumber.xml"
    },
    dryRun = false,
    strict = true
)
public class RunCucumberTest {
}
```

## Data Tables Types

```java
// List of lists
@Given("the following products:")
public void theFollowingProducts(List<List<String>> table) {
    // table.get(0) = headers
    // table.get(1) = first row
}

// List of maps
@Given("the following products:")
public void theFollowingProducts(DataTable table) {
    Map<String, String> row = table.asMaps().get(0);
}

// Custom type
@Given("the following users:")
public void theFollowingUsers(DataTable table) {
    List<User> users = table.asList(User.class);
}

// Custom transformer
@Given("the user has {string} balance")
public void theUserHasBalance(String amount) {
    BigDecimal balance = new BigDecimal(amount.replace("$", ""));
}
```

## Common Step Patterns

| Pattern | Example |
|---------|---------|
| {string} | "I enter {string}" - simple text |
| {int} | "I add {int} items" - integer |
| {word} | "the user {word}" - no spaces |
| {float} | "price is {float}" - decimal |
|.* | Regex: "the following.*" |

## Best Practices

Use descriptive feature files that tell a story. Keep step definitions simple and reusable. Avoid hardcoded waits—use explicit waits. Take screenshots on failures. Use Background for common prerequisites. Organize scenarios by tag for selective execution.