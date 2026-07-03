# Module 34: Code Quality & Static Analysis - Quizzes

---

## Q1: PMD vs SpotBugs
What is the primary difference in how PMD and SpotBugs analyze a Java application?

A) PMD requires a running JVM, while SpotBugs does not.
B) PMD analyzes the raw source code (`.java` files), whereas SpotBugs analyzes the compiled bytecode (`.class` files).
C) SpotBugs is only for memory leaks, while PMD is for security.
D) There is no difference; they are the same tool.

**Answer**: B
**Explanation**: PMD scans the source code and applies AST (Abstract Syntax Tree) rules to find flaws like unused variables. SpotBugs operates on compiled bytecode, allowing it to find deeper execution bugs like potential NullPointerExceptions.

---

## Q2: Code Coverage
What does "Branch Coverage" mean in the context of tools like JaCoCo?

A) The percentage of classes executed during a test run.
B) The percentage of if/else conditions and switch cases where both the "true" and "false" paths were executed during tests.
C) The percentage of Git branches merged into `main`.
D) The percentage of database tables accessed.

**Answer**: B
**Explanation**: Branch coverage tracks whether the boolean expressions in control structures (like `if` and `switch` statements) evaluated to both true and false across your test suite.

---

## Q3: SonarQube Quality Gates
What is a "Quality Gate" in SonarQube?

A) A firewall rule that prevents unauthorized access.
B) A set of strict conditions (e.g., minimum code coverage, maximum number of vulnerabilities) that the code must meet to "pass" the analysis.
C) A tool to format Java code automatically.
D) A database table used by SonarQube.

**Answer**: B
**Explanation**: Quality Gates define the minimum acceptable standards for a codebase. If a project fails the Quality Gate, CI/CD pipelines should typically be configured to fail the build.