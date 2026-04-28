# 📋 Module Standardization & Best Practices

Complete guide for creating standardized, high-quality modules in the Java Learning platform.

## 📦 Module Template

Use thiS as a starting point for new modules. All modules should follow this structure.

### Directory Structure

```
module-name/
├── pom.xml                          # Maven configuration
├── README.md                        # Module documentation
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/learning/
│   │   │       └── <module>/
│   │   │           ├── Main.java
│   │   │           ├── dto/                 # Data Transfer Objects
│   │   │           ├── model/               # Domain models/entities
│   │   │           ├── service/             # Business logic
│   │   │           ├── repository/          # Data access
│   │   │           ├── util/                # Utility classes
│   │   │           └── exception/           # Custom exceptions
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── logback.xml
│   │       └── data.sql
│   └── test/
│       ├── java/
│       │   └── com/learning/
│       │       └── <module>/
│       │           ├── *Test.java           # Unit tests
│       │           ├── *ControllerTest.java # Controller tests
│       │           └── *IT.java             # Integration tests
│       └── resources/
│           ├── application-test.properties
│           └── test-data.sql
└── target/                          # Build output (generated, ignored)
```

---

## 🎯 POM.xml Template

All modules should have minimal pom.xml that **inherits from the parent pom.xml**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- INHERIT FROM PARENT -->
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>java-learning-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>

    <!-- MODULE-SPECIFIC CONFIG -->
    <artifactId>XX-module-name</artifactId>
    <name>XX - Module Display Name</name>
    <description>Brief description of what this module covers</description>
    <packaging>jar</packaging>

    <!-- MODULE-SPECIFIC PROPERTIES (if needed) -->
    <properties>
        <!-- Add only if different from parent -->
    </properties>

    <!-- MODULE-SPECIFIC DEPENDENCIES (if needed) -->
    <dependencies>
        <!-- Inherits all dependencies from parent -->
        <!-- Add only module-specific dependencies here -->
    </dependencies>

    <!-- MODULE-SPECIFIC BUILD PLUGINS (if needed) -->
    <build>
        <plugins>
            <!-- Add only if special configuration needed -->
        </plugins>
    </build>
</project>
```

---

## 📄 README.md Template

Each module must have a comprehensive README.md:

```markdown
# XX - Module Name

## 📌 Overview

One-paragraph overview of what this module teaches and its relevance.

## 🎯 Learning Objectives

By completing this module, you'll understand:

- [ ] Concept 1
- [ ] Concept 2
- [ ] Concept 3

## 📚 Topics Covered

### 1. Topic 1
Brief explanation of topic and why it's important.

**Key Concepts:**
- Subtopic 1.1
- Subtopic 1.2

**Real-world Applications:**
- Use case 1
- Use case 2

**Implementation:** [ClassName.java](src/main/java/...)

### 2. Topic 2
[Similar structure]

## 🏗️ Project Structure

```
src/main/java/com/learning/
├── Main.java              # Entry point with examples
├── package1/
│   ├── Class1.java
│   └── Class2.java
└── package2/
    ├── Class3.java
    └── Class4.java
```

## 🚀 Running the Module

### Prerequisites
- Java 21+
- Maven 3.8.0+

### Compile

\`\`\`bash
mvn clean compile -f pom.xml
\`\`\`

### Run Main Examples

\`\`\`bash
mvn exec:java@exec-main -f pom.xml
\`\`\`

### Run Tests

\`\`\`bash
mvn clean test -f pom.xml
\`\`\`

### Generate Coverage Report

\`\`\`bash
mvn clean verify -f pom.xml
# Open target/site/jacoco/index.html
\`\`\`

## 📖 Key Classes

### ClassName
**Purpose:** Brief description

**Methods:**
- `methodName(param)`: Description

**Example:**
\`\`\`java
ClassName example = new ClassName();
example.methodName("param");
\`\`\`

## 🧪 Test Coverage

- ✅ Unit Tests: XX classes
- ✅ Integration Tests: XX tests
- ✅ Code Coverage: XX%

## 💡 Key Learnings

1. Learning point 1 with explanation
2. Learning point 2 with explanation
3. Common pitfalls to avoid

## 🔗 Related Modules

- [Link to related module](../related-module/)

## 📚 Further Reading

- Topic 1: [Resource](url)
- Topic 2: [Resource](url)

## ❓ FAQ

**Q: Question?**
A: Answer with explanation.

## 📝 Notes

- Important note 1
- Important note 2

---

Last Updated: YYYY-MM-DD
```

---

##  ✍️ Java Code Standards

### File Header

Every Java file should include a header:

```java
package com.learning.module;

/**
 * Brief description of class purpose.
 *
 * <p>Detailed explanation if needed, including usage examples
 * and important implementation details.</p>
 *
 * <p><b>Example Usage:</b>
 * <pre>
 * MyClass example = new MyClass();
 * example.doSomething();
 * </pre></p>
 *
 * @author Learning Team
 * @version 1.0
 * @since 1.0
 * @see RelatedClass
 */
public class MyClass {
    // Implementation
}
```

### Naming Conventions

```java
// Classes: PascalCase
public class BankAccount { }
public class UserController { }

// Interfaces: PascalCase with prefix or suffix (optional)
public interface PaymentService { }
public interface Drawable { }

// Methods: camelCase, descriptive verb-noun
public void depositFunds() { }
public double calculateInterest() { }
public boolean validateInput() { }

// Variables: camelCase, meaningful names
private String accountNumber;
private double currentBalance;
private List<Transaction> transactions;

// Constants: UPPER_SNAKE_CASE
public static final double MINIMUM_BALANCE = 100.0;
public static final String DEFAULT_CURRENCY = "USD";
private static final int MAX_RETRIES = 3;

// Package: lowercase, reverse domain notation
package com.learning.core.banking;
package com.learning.quarkus.reactive;
```

### Javadoc Comments

```java
/**
 * Transfers funds from this account to another.
 *
 * <p>This method validates the amount and both accounts before
 * processing the transfer. Transaction history is updated for both
 * accounts.</p>
 *
 * <p><b>Formula:</b>
 * <code>sourceBalance -= amount; targetBalance += amount</code></p>
 *
 * @param targetAccount the account to transfer to, must not be null
 * @param amount the amount to transfer, must be positive and not exceed balance
 * @return true if transfer successful, false otherwise
 * @throws IllegalArgumentException if amount is negative or targetAccount is null
 * @throws InsufficientBalanceException if amount exceeds current balance
 *
 * @see BankAccount#deposit(double)
 * @see BankAccount#withdraw(double)
 */
public boolean transferFunds(BankAccount targetAccount, double amount)
        throws IllegalArgumentException, InsufficientBalanceException {
    // Implementation
}
```

### Method Organization

```java
public class Example {
    
    // ===== CONSTANTS =====
    private static final String CONSTANT = "value";
    
    // ===== STATIC FIELDS =====
    private static int staticField;
    
    // ===== INSTANCE FIELDS =====
    private String field1;
    private int field2;
    
    // ===== CONSTRUCTORS =====
    public Example() {
        this("default");
    }
    
    public Example(String field1) {
        this.field1 = field1;
        this.field2 = 0;
    }
    
    // ===== PUBLIC METHODS =====
    public String getField1() {
        return field1;
    }
    
    public void setField1(String field1) {
        this.field1 = field1;
    }
    
    // ===== PROTECTED METHODS =====
    protected void protectedMethod() {
    }
    
    // ===== PRIVATE METHODS =====
    private void privateHelper() {
    }
    
    // ===== OVERRIDE METHODS =====
    @Override
    public String toString() {
        return "Example{" +
                "field1='" + field1 + '\'' +
                ", field2=" + field2 +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Example example = (Example) o;
        return field2 == example.field2 &&
               Objects.equals(field1, example.field1);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(field1, field2);
    }
}
```

---

## 🧪 Testing Standards

### Test Class Template

```java
package com.learning.module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link MyClass}.
 *
 * <p>Tests cover all public methods and edge cases.</p>
 *
 * @author Learning Team
 * @version 1.0
 * @see MyClass
 */
@DisplayName("MyClass Unit Tests")
class MyClassTest {
    
    private MyClass sut;  // System Under Test
    
    @BeforeEach
    void setUp() {
        // Initialize test fixtures
        sut = new MyClass();
    }
    
    @Test
    @DisplayName("Should create instance with default values")
    void testDefaultConstruction() {
        assertThat(sut).isNotNull();
        assertThat(sut.getField()).isEqualTo("default");
    }
    
    @Test
    @DisplayName("Should deposit funds successfully")
    void testDepositSuccess() {
        // Arrange
        double initialBalance = sut.getBalance();
        double depositAmount = 100.0;
        
        // Act
        sut.deposit(depositAmount);
        
        // Assert
        assertThat(sut.getBalance())
                .isEqualTo(initialBalance + depositAmount);
    }
    
    @Test
    @DisplayName("Should throw exception on negative deposit")
    void testDepositNegativeThrows() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> sut.deposit(-100.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be positive");
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {0.01, 50.0, 1000.0})
    @DisplayName("Should deposit various amounts successfully")
    void testDepositVariousAmounts(double amount) {
        double initialBalance = sut.getBalance();
        sut.deposit(amount);
        assertThat(sut.getBalance()).isEqualTo(initialBalance + amount);
    }
}
```

### Test Naming Convention

```java
// Pattern: test[WhatIsBeing][ExpectedResult]

testDepositSuccess()                    // Success case
testDepositNegativeThrows()             // Error case
testDepositZeroThrows()                 // Edge case
testDepositMaxAmountSucceeds()          // Boundary case
testMultipleDepositsAccumulate()        // State change case
```

### Test Coverage Requirements

- **Minimum**: 70% overall
- **Target**: 85%+ for core modules
- **Critical paths**: 100%
- **All tests must pass**: No skipped tests

Run coverage:
```bash
mvn clean verify jacoco:report
# View: target/site/jacoco/index.html
```

---

## 📊 Documentation Standards

### Module Validation

Before completing a module, verify:

- ✅ README.md is comprehensive
- ✅ All code is documented with Javadoc
- ✅ Tests pass: `mvn clean test` = 100% pass
- ✅ Code coverage ≥ 70%: `mvn jacoco:report`
- ✅ No code quality issues: `mvn verify -Pquality`
- ✅ Build succeeds: `mvn clean verify`
- ✅ Examples run correctly
- ✅ Git is clean: `git status` empty

### Module Completion Checklist

```markdown
## Module Completion Checklist

- [ ] Source code complete
- [ ] All tests passing (100%)
- [ ] Code coverage ≥ 70%
- [ ] Javadoc comments complete
- [ ] README.md comprehensive
- [ ] Examples executable
- [ ] Code quality checks passed
- [ ] No compiler warnings
- [ ] Build successful
- [ ] Git status clean
- [ ] Ready for production
```

---

## 🔄 Module Migration/Upgrade

### When Updating a Module

1. **Update dependencies** in parent POM first
2. **Run compatibility tests**: `mvn clean verify`
3. **Fix any breaking changes**
4. **Update documentation**
5. **Verify all tests pass**
6. **Commit with clear message**: "Upgrade [module]: Update [dep] to X.Y.Z"

### Common Upgrades

```bash
# Update Java version (in parent POM only)
# Properties: <maven.compiler.source>21</maven.compiler.source>

# Update framework version (in parent POM dependencyManagement)
<spring-boot.version>3.3.0</spring-boot.version>

# Then rebuild all:
mvn clean verify
```

---

## ✨ Best Practices

### Do ✅

- ✅ Write clear, descriptive method names
- ✅ Use meaningful variable names
- ✅ Keep methods small and focused (<30 lines)
- ✅ Add comprehensive Javadoc comments
- ✅ Write tests for all public methods
- ✅ Use constants for magic numbers
- ✅ Follow the single responsibility principle
- ✅ Include usage examples in READMEs
- ✅ Document edge cases and exceptions
- ✅ Use appropriate exception types

### Don't ❌

- ❌ Leave TODO comments without tracking issues
- ❌ Use or reuse generic variable names (e.g., `data`, `temp`, `x`)
- ❌ Write single-responsibility methods
- ❌ Mix business logic with presentation logic
- ❌ Ignore exceptions silently
- ❌ Duplicate code (use inheritance or composition)
- ❌ Leave commented-out code
- ❌ Use hardcoded values (use constants)
- ❌ Commit failing tests
- ❌ Ignore compiler warnings

---

## 📞 Support & Questions

For questions or issues:
1. Check existing [GitHub Issues](https://github.com/armand-ratombotiana/JavaLearning/issues)
2. Review [CONTRIBUTING.md](../CONTRIBUTING.md)
3. Check [README.md](../README.md)

---

**Last Updated**: April 2026
