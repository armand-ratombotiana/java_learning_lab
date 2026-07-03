# Module 34: Code Quality & Static Analysis - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between Static Code Analysis and Dynamic Code Analysis?
**Answer**:
- **Static Code Analysis**: Analyzing the code (or bytecode) without actually executing the program. Tools like SonarQube, PMD, Checkstyle, and SpotBugs look for syntactical errors, code smells, security vulnerabilities, and adherence to formatting standards by parsing the abstract syntax tree (AST) or inspecting the compiled `.class` files.
- **Dynamic Code Analysis**: Analyzing the code while the program is actively running. This involves techniques like unit testing, integration testing, memory profiling, and fuzz testing to find runtime errors (like memory leaks or concurrency issues) that static analysis cannot catch.

### Q2: Why is "Branch Coverage" often considered a better metric than "Line Coverage"?
**Answer**:
Line coverage simply calculates the percentage of executable lines of code that were hit during a test run. However, a single line of code can contain complex logical branches (e.g., `if (a && b || c)` or a ternary operator `return x ? y : z;`). 
If a test only exercises the `true` path of that line, line coverage will report 100% for that line. Branch coverage tracks whether *all possible logical paths* through the control structures were tested. Achieving 100% line coverage but only 50% branch coverage means significant logic remains completely untested, creating a false sense of security.

### Q3: How do Quality Gates in tools like SonarQube prevent technical debt?
**Answer**:
A Quality Gate is a set of boolean conditions (e.g., "0 New Critical Vulnerabilities", "Code Coverage on New Code > 80%", "0 Blocker Code Smells") that act as a strict threshold. 
By integrating the Quality Gate into a CI/CD pipeline (e.g., Jenkins, GitHub Actions), any Pull Request that introduces technical debt or drops coverage below the threshold will automatically fail the build. This automates code reviews for objective metrics and prevents bad code from ever reaching the `main` branch.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring a Code Smell
**Problem**: A static analysis tool flags the following method as a "Code Smell" due to its high **Cyclomatic Complexity**. Refactor it to reduce complexity and improve readability.

```java
public double calculateDiscount(Customer customer, double amount) {
    if (customer != null) {
        if (customer.isActive()) {
            if (customer.getYearsAsMember() > 5) {
                if (amount > 1000) {
                    return amount * 0.20;
                } else {
                    return amount * 0.10;
                }
            } else {
                if (amount > 1000) {
                    return amount * 0.10;
                } else {
                    return amount * 0.05;
                }
            }
        }
    }
    return 0.0;
}
```

**Solution**:
The deep nesting ("Arrow Anti-Pattern") makes the code hard to read and test. The solution is to use **Guard Clauses** (returning early) to flatten the logic.

```java
public double calculateDiscount(Customer customer, double amount) {
    // Guard clauses to exit early
    if (customer == null || !customer.isActive()) {
        return 0.0;
    }

    boolean isLongTermMember = customer.getYearsAsMember() > 5;
    boolean isLargeAmount = amount > 1000;

    // Flattened logic
    if (isLongTermMember && isLargeAmount) {
        return amount * 0.20;
    }
    
    if (isLongTermMember || isLargeAmount) {
        return amount * 0.10;
    }
    
    return amount * 0.05;
}
```