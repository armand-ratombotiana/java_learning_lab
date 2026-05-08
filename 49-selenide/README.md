# Selenide - UI Testing

## Overview
Selenide is a concise wrapper around Selenium WebDriver that provides automatic waiting, fluent API, and built-in screenshot capture.

## Key Features
- Auto-waiting for elements
- Concise fluent syntax
- Automatic screenshots on failure
- Built-in Ajax support
- Condition assertions

## Project Structure
```
49-selenide/
  selenide-ui/
    src/main/java/com/learning/selenide/SelenideLab.java
```

## Running
```bash
cd 49-selenide/selenide-ui
mvn compile exec:java
```

## Concepts Covered
- Element selection and actions
- Condition assertions (should, shouldNot)
- Page Object pattern
- Ajax handling

## Dependencies
- Selenide
- Selenium WebDriver