# 49 - Selenide Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Selenide | Wrapper around Selenium WebDriver |
| Condition | Built-in wait conditions |
| Collection | Element collection handling |
| Screenshot | Automatic failure capture |
| Configuration | Centralized test settings |

## Element Selection

```java
// Basic selectors
$("#login-button").click();
$(".error-message").shouldBe(visible);
$("input[name='username']").setValue("admin");

// CSS and XPath
$(byText("Submit")).click();
$(byId("password")).pressEnter();
$(byClassName("dropdown-menu")).shouldBe(visible);

// Dynamic elements
$(".item").shouldHave(text("Product"));
$("#dynamic").shouldNotBe(visible);
```

## Conditions

```java
// Visibility conditions
element.shouldBe(visible);
element.shouldNotBe(visible);
element.shouldBe(hidden);
element.shouldBe(enabled);
element.shouldBe(disabled);

// Text conditions
element.shouldHave(text("Welcome"));
element.shouldHave(textCaseSensitive("Welcome"));
element.shouldHave(exactText("Welcome, John"));
element.shouldNotHave(text("Error"));

// Value conditions
$("#input").shouldHave(value("default"));
$("#input").shouldNotHave(value("error"));

// Attribute conditions
$("a").shouldHave(href("https://example.com"));
$("#image").shouldHave(attribute("alt", "Logo"));

// State conditions
element.shouldBe(selected);
element.shouldNotBe(selected);
```

## Collections

```java
// Find all matching elements
$$(".product-item").shouldHave(size(10));
$$(".product-item").first().click();
$$(".product-item").last().shouldBe(visible);
$$(".product-item").get(2).click();

// Filtering
$$(".item").filter(visible).shouldHave(size(5));
$$(".item").filterBy(text("Active")).first().click();

// Iteration
$$(".error").forEach(element -> 
    element.shouldHave(cssClass("alert"))
);
```

## Waits

```java
// Implicit wait (default 4 seconds)
$(".loading").waitUntil(visible, 3000);
$(".modal").waitUntil(hidden, 5000);

// Explicit condition
$("#result").shouldHave(text("Success"), Duration.ofSeconds(10));

// Wait for URL change
switchTo().window(1);
$("#loaded").shouldBe(visible);

// Ajax handling
$(".spinner").shouldNotBe(visible);
$$(".items").shouldHave(sizeGreaterThan(0));
```

## Actions

```java
// Click options
$("#btn").click();
$("#btn").doubleClick();
$("#btn").contextClick();

// Keyboard
$("#input").pressEnter();
$("#input").pressEscape();
$("#input").sendKeys(Keys.CONTROL + "a");
$("#input").sendKeys(Keys.DELETE);

// Drag and drop
$("#draggable").dragAndDropTo("#droppable");

// File upload
$("#file-upload").uploadFile(new File("test.txt"));

// JavaScript execution
Selenide.executeJavaScript("window.scrollTo(0, document.body.scrollHeight)");
```

## Configuration

```java
// Configuration class
Configuration.browser = "chrome";
Configuration.headless = true;
Configuration.timeout = 10000;
Configuration.reportsFolder = "target/selenide";

// BaseUrl
Configuration.baseUrl = "https://example.com";

// Browser options
Configuration.browserSize = "1920x1080";

// Hold browser open
Configuration.holdBrowserOpen = true;
```

## Page Objects

```java
@Name("Login Page")
public class LoginPage {
    public LoginPage() {
        PageFactory.initElements(new SelenideElementFactory(), this);
    }

    @Name("Username field")
    @FindBy(id = "username")
    private SelenideElement username;

    @FindBy(id = "password")
    private SelenideElement password;

    @FindBy(css = ".error")
    private SelenideElement errorMessage;

    public void login(String user, String pass) {
        username.setValue(user);
        password.setValue(pass).pressEnter();
    }

    public DashboardPage loginAsAdmin() {
        username.setValue("admin");
        password.setValue("admin").pressEnter();
        return new DashboardPage();
    }
}
```

## Best Practices

Use Page Object pattern for maintainability. Leverage built-in waits instead of explicit sleeps. Take advantage of automatic screenshots on failures. Use meaningful element names with @Name annotation. Configure reasonable timeouts for CI environments.