# Selenide - Projects

This document contains two complete projects demonstrating Selenide browser automation: a mini-project for learning UI automation basics and a real-world project implementing production-grade browser tests.

## Mini-Projects by Concept

### 1. Element Selection (2 hours)
Select elements using CSS, XPath, and Selenide-specific selectors. Handle dynamic elements and frame switching.

### 2. Actions & Interactions (2 hours)
Perform clicks, typing, drag-and-drop, and keyboard actions. Handle alerts and file uploads.

### 3. Waits & Timeouts (2 hours)
Configure implicit and explicit waits. Handle AJAX loading and dynamic content.

### 4. Page Objects (2 hours)
Implement Page Object pattern with Selenide. Create reusable page components and assertions.

### Real-world: Browser Automation Suite
Build comprehensive browser automation suite with Page Objects, screenshot capture, and reporting.

---

## Project 1: Selenide Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Selenide concepts including element selection, actions, and waiting. It serves as a learning starting point for browser automation.

### Structure

```
selenide-basics/
├── pom.xml
└── src/test/java/com/learning/selenide/
```

### pom.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>selenide-basics</artifactId>
    <version>1.0.0</version>
    <dependencies>
        <dependency>
            <groupId>com.codeborne</groupId>
            <artifactId>selenide</artifactId>
            <version>6.17.2</version>
        </dependency>
    </dependencies>
</project>
```

### BasicTest.java

```java
package com.learning.selenide;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import org.junit.jupiter.api.Test;

public class BasicTest {
    
    @Test
    void shouldSearchGoogle() {
        open("https://google.com");
        $("#APjFqf").setValue("Selenide");
        $("input[name='btnK']").click();
        $(".gL9HX").shouldBe(visible);
    }
    
    @Test
    void shouldFillForm() {
        open("/form");
        $("#username").setValue("admin");
        $("#password").setValue("secret");
        $("#login").click();
        $(".success").shouldBe(visible);
    }
}
```

## Project 2: Production Browser Automation

### Structure

```
selenide-production/
├── pom.xml
├── src/test/java/com/learning/selenide/pages/
└── src/test/java/com/learning/selenide/tests/
```

### PageObject.java

```java
package com.learning.selenide.pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Condition.*;

public class LoginPage {
    
    private SelenideElement username = $("#username");
    private SelenideElement password = $("#password");
    private SelenideElement submit = $("#login");
    
    public LoginPage enterUsername(String user) {
        username.setValue(user);
        return this;
    }
    
    public LoginPage enterPassword(String pass) {
        password.setValue(pass);
        return this;
    }
    
    public void clickLogin() {
        submit.click();
    }
    
    public void login(String user, String pass) {
        enterUsername(user).enterPassword(pass).clickLogin();
    }
}
```

### POMTest.java

```java
package com.learning.selenide;

import com.learning.selenide.pages.LoginPage;
import org.junit.jupiter.api.Test;
import static com.codeborne.selenide.Selenide.*;

public class POMTest {
    
    @Test
    void shouldLoginSuccessfully() {
        open("/login");
        new LoginPage().login("admin", "secret");
        $(".dashboard").shouldBe(visible);
    }
}
```

### Build and Run

```bash
mvn test -Dselenide.browser=chrome
```