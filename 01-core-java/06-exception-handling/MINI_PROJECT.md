# Module 06: Exception Handling - Mini Project

**Project Name**: Resilient File Parser  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Build a robust data parsing tool that reads configurations from a file, demonstrating the use of custom exceptions, `try-with-resources`, multi-catch blocks, and exception propagation.

## 📝 Requirements

### Core Features
1. **Custom Exceptions**:
   - Create a checked exception `InvalidConfigurationException`.
   - Create an unchecked exception `DataCorruptionException`.

2. **File Reading (Try-with-Resources)**:
   - Create a method `parseConfig(String filePath)` that reads a CSV or key-value text file.
   - Use `BufferedReader` or `Scanner` within a `try-with-resources` block to ensure the file is closed automatically, even if errors occur.

3. **Data Validation & Throwing**:
   - Iterate through the lines. If a line is missing a required delimiter (e.g., "="), throw an `InvalidConfigurationException`.
   - If a numeric value cannot be parsed (e.g., `timeout=abc`), catch the standard `NumberFormatException` and re-throw it wrapped in your custom `DataCorruptionException` (Exception Chaining).

4. **Multi-Catch & Finally**:
   - In the `main` method, call `parseConfig`.
   - Use a multi-catch block to handle `IOException` and `InvalidConfigurationException` in the same way (e.g., logging an error message).
   - Use a `finally` block in the caller to log "Execution finished."

---

## 💡 Solution Blueprint

1. Create classes: `InvalidConfigurationException extends Exception`, `DataCorruptionException extends RuntimeException`.
2. Mock a file (or create a real one) with content like:
   ```
   host=localhost
   port=8080
   timeout=invalid_number
   bad_line_no_equals
   ```
3. Read the file:
   ```java
   try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
       // Loop and parse
   }
   ```
4. During parsing:
   ```java
   if (!line.contains("=")) throw new InvalidConfigurationException("Missing delimiter");
   try {
       int val = Integer.parseInt(valueStr);
   } catch (NumberFormatException e) {
       throw new DataCorruptionException("Failed to parse number", e);
   }
   ```