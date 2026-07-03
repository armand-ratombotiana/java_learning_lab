# Module 34: Code Quality & Static Analysis - Deep Dive

**Difficulty Level**: Intermediate  
**Prerequisites**: Modules 01-33  
**Estimated Reading Time**: 45 minutes  

---

## 📚 Table of Contents

1. [Introduction to Code Quality](#intro)
2. [Checkstyle and Code Formatting](#checkstyle)
3. [Static Analysis with SpotBugs and PMD](#static-analysis)
4. [SonarQube Integration](#sonarqube)
5. [Code Coverage (JaCoCo)](#jacoco)

---

## 1. Introduction to Code Quality <a name="intro"></a>
Code quality refers to how safe, secure, reliable, and maintainable your codebase is. In a Java environment, a mix of strict formatting, static analysis, and code coverage metrics are used to ensure long-term health.

---

## 2. Checkstyle and Code Formatting <a name="checkstyle"></a>
Checkstyle is a development tool to help programmers write Java code that adheres to a coding standard (like Google Java Style). It automates the process of checking Java code.

```xml
<!-- Maven Plugin Configuration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
    </configuration>
</plugin>
```

---

## 3. Static Analysis with SpotBugs and PMD <a name="static-analysis"></a>
- **SpotBugs**: Looks for bugs in Java programs. It relies on analyzing bytecode rather than source code. It can detect things like null pointer dereferences, infinite recursive loops, and misuse of Java libraries.
- **PMD**: Analyzes source code. It finds common programming flaws like unused variables, empty catch blocks, and unnecessary object creation.

---

## 4. SonarQube Integration <a name="sonarqube"></a>
SonarQube acts as a centralized dashboard that aggregates metrics from Checkstyle, PMD, SpotBugs, and JaCoCo. It evaluates the project against predefined Quality Gates.

- **Quality Gate**: A set of conditions (e.g., "Code Coverage > 80%", "0 Critical Bugs") that a project must meet before it can be merged or deployed.

---

## 5. Code Coverage (JaCoCo) <a name="jacoco"></a>
JaCoCo (Java Code Coverage) is a free code coverage library for Java. It instruments the Java bytecode to track which lines and branches of code are executed during tests.

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```