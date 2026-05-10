# Selenide Solution

## Overview
This module covers browser automation with Selenide.

## Key Features

### Elements
- Finding elements
- Clicking
- Typing
- Getting values

### Conditions
- Visibility
- Text presence
- Custom conditions

### Waits
- Explicit waits
- Implicit waits
- Timeouts

## Usage

```java
SelenideSolution solution = new SelenideSolution();

// Open URL
solution.openUrl("https://example.com");

// Type
solution.type(By.name("username"), "testuser");

// Click
solution.click(By.id("submit"));

// Get text
String text = solution.getText(By.tagName("h1"));

// Wait
solution.waitForVisible(By.className("loader"), 5);
```

## Dependencies
- Selenide
- JUnit 5
- WebDriver