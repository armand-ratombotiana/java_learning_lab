# Module 34: Code Quality & Static Analysis - Edge Cases & Pitfalls

---

## Pitfall 1: Chasing 100% Code Coverage

### ❌ Wrong
Forcing the team to hit exactly 100% line coverage. This often leads to developers writing meaningless tests (e.g., testing getters and setters) just to appease the JaCoCo threshold, rather than testing actual business logic.

### ✅ Correct
Set a reasonable threshold (e.g., 70-80%) for overall coverage. Focus heavily on 100% *branch* coverage for critical business domain logic, and explicitly exclude Auto-generated code, DTOs, or configuration classes from the coverage report.

---

## Pitfall 2: Ignoring Quality Gate Failures

### ❌ Wrong
Configuring SonarQube but bypassing the Quality Gate during the CI/CD pipeline. Treating static analysis warnings as "suggestions" rather than blockers.

### ✅ Correct
Make the CI/CD build fail strictly if the SonarQube Quality Gate fails. If a false positive is detected, mark it as a false positive in the Sonar dashboard rather than bypassing the entire gate.

---

## Pitfall 3: Inconsistent Formatting Across the Team

### ❌ Wrong
Relying on developers to "just format code properly" in their IDEs. This leads to massive, unreadable git diffs where 90% of the changes are just whitespace and bracket realignments.

### ✅ Correct
Automate formatting checks on the build server. Enforce a shared XML Checkstyle configuration during the Maven/Gradle build process (e.g., `mvn checkstyle:check`) so unformatted code immediately fails the build.