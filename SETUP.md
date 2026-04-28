# ⚙️ Project Setup Guide

Complete setup instructions for the Java Learning project.

## Prerequisites

### Required

- **Java 21+** (LTS)
  ```bash
  java -version  # Should show Java 21+
  ```
  Install from: https://adoptium.net/ or https://www.oracle.com/java/

- **Maven 3.8.0+**
  ```bash
  mvn -version  # Should show Maven 3.8.0+
  ```
  Install from: https://maven.apache.org/

- **Git 2.25+**
  ```bash
  git --version  # Should show Git 2.25+
  ```
  Install from: https://git-scm.com/

### Optional but Recommended

- **Docker** (for integration tests with TestContainers)
  - Download from: https://www.docker.com/products/docker-desktop
  - Or: `brew install docker` (macOS)

- **pre-commit** (for automatic code quality checks)
  ```bash
  pip install pre-commit
  pre-commit install  # Run in project root
  ```

---

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/armand-ratombotiana/JavaLearning.git
cd JavaLearning
```

### 2. Verify Java Installation

```bash
java -version
javac -version
```

Both should show Java 21 or higher.

### 3. Verify Maven Installation

```bash
mvn -version
```

Should show Maven 3.8.0 or higher.

### 4. Build the Project

```bash
# Full build with tests
mvn clean verify

# Build without tests (faster)
mvn clean install -DskipTests

# Build specific module
mvn clean install -f 01-core-java/02-oop-concepts/pom.xml
```

### 5. Run Tests

```bash
# All tests
mvn clean test

# Specific module
mvn clean test -f 01-core-java/02-oop-concepts/pom.xml

# With coverage report
mvn clean verify  # Check target/site/jacoco/index.html
```

---

## 📁 Project Structure

```
JavaLearning/
├── pom.xml                          # Parent POM (centralized configuration)
├── 01-core-java/                    # Core Java modules
│   ├── 01-java-basics/
│   ├── 02-oop-concepts/
│   ├── 03-collections-framework/
│   └── ...
├── 02-spring-boot/                  # Spring Boot modules
├── quarkus-learning/                # Quarkus framework modules
├── EclipseVert.XLearning/           # Vert.x modules
├── docs/                            # Documentation
├── scripts/                         # Build scripts
├── .github/
│   ├── workflows/
│   │   ├── build.yml               # CI build pipeline
│   │   └── coverage.yml            # Code coverage pipeline
│   └── java-upgrade/               # Java upgrade tracking
├── .editorconfig                    # Code style configuration
├── .pre-commit-config.yaml          # Pre-commit hooks
├── CONTRIBUTING.md                  # Contribution guidelines
└── SETUP.md                         # This file
```

---

## 🔧 Development Environment

### IDE Setup (IntelliJ IDEA)

1. **Open project**:
   - File → Open → Navigate to project folder
   - Select `pom.xml` as project root

2. **Configure JDK**:
   - File → Project Structure → Project
   - Set Project SDK to Java 21
   - Set Language Level to Java 21

3. **Configure Maven**:
   - File → Settings → Build, Execution, Deployment → Maven
   - Set Maven home path (if not auto-detected)
   - Import all modules

4. **Enable Code Inspections**:
   - File → Settings → Editor → Inspections
   - Enable: Error Prone, SpotBugs, Android, Probable bugs

### IDE Setup (Eclipse)

1. **Import project**:
   - File → Import → Existing Maven Projects
   - Select project root folder

2. **Configure JDK**:
   - Window → Preferences → Java → Installed JREs
   - Add Java 21 installation
   - Set as default

3. **Install plugins**:
   - Help → Eclipse Marketplace
   - Search and install: Maven, Checkstyle, SpotBugs

### IDE Setup (VS Code)

1. **Install extensions**:
   - Extension Pack for Java
   - Maven for Java
   - Checkstyle for Java
   - SonarLint

2. **Configure Java Path**:
   - Open Command Palette: Ctrl+Shift+P
   - "Java: Update Language Server JDK"
   - Select Java 21

3. **Settings** (`.vscode/settings.json`):
   ```json
   {
     "java.home": "/path/to/java-21",
     "java.import.maven.version": "automatic",
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "redhat.java"
   }
   ```

---

## 🏗️ Build Commands

### Clean & Rebuild

```bash
# Full clean rebuild (recommended before commit)
mvn clean verify

# Fast rebuild (skip tests)
mvn clean install -DskipTests
```

### Testing

```bash
# Run all unit tests
mvn clean test

# Run specific test class
mvn test -Dtest=BankAccountTest

# Run specific test method
mvn test -Dtest=BankAccountTest#testDeposit

# Run with detailed output
mvn test -e -X

# Run integration tests
mvn verify -Pintegration-tests

# Skip code coverage during tests
mvn test -DskipCoverage=true
```

### Code Quality

```bash
# Run all quality checks
mvn clean verify -Pquality

# Checkstyle only
mvn checkstyle:check

# PMD analysis
mvn pmd:check

# SpotBugs analysis
mvn spotbugs:check

# JaCoCo code coverage
mvn jacoco:report  # View: target/site/jacoco/index.html

# Javadoc generation
mvn javadoc:javadoc  # View: target/site/apidocs/index.html

# Dependency analysis
mvn dependency:analyze
mvn versions:display-dependency-updates
```

### Module-Specific Builds

```bash
# Build only core-java modules
mvn clean install -f 01-core-java/pom.xml

# Build OOP Concepts module
mvn clean install -f 01-core-java/02-oop-concepts/pom.xml

# Run only OOP Concepts tests
mvn clean test -f 01-core-java/02-oop-concepts/pom.xml
```

---

## 🔒 Pre-commit Hooks

### Install

```bash
pip install pre-commit
cd JavaLearning
pre-commit install
```

### Usage

Pre-commit hooks will run automatically on `git commit`. To run manually:

```bash
# Run all hooks on staged files
pre-commit run

# Run all hooks on all files
pre-commit run --all-files

# Bypass hooks (not recommended)
git commit --no-verify
```

### Hooks Included

- Trailing whitespace removal
- End-of-file fixing
- YAML validation
- JSON validation
- XML validation
- Merge conflict detection
- Checkstyle enforcement
- PMD analysis
- Markdown linting
- Secret detection

---

## 🚦 CI/CD Pipelines

### GitHub Actions

Workflows run automatically on:
- Push to `main` or `develop`
- Pull requests to `main` or `develop`
- Daily schedule (2 AM UTC)

#### Build Pipeline (`build.yml`)

- Compiles code
- Runs unit tests
- Generates coverage reports
- Uploads to Codecov

#### Quality Pipeline (`quality.yml`)

- Checkstyle validation
- PMD analysis
- SpotBugs detection
- Dependency vulnerability scan

#### Coverage Pipeline (`coverage.yml`)

- Generates JaCoCo reports
- Creates coverage artifacts
- Comments on PRs

---

## 🐛 Troubleshooting

### Issue: Maven command not found

**Solution:**
```bash
# Check Maven installation
which mvn  # or where mvn on Windows

# Add Maven to PATH
export PATH=$PATH:/path/to/maven/bin

# Or use Maven wrapper
./mvnw clean verify  # Linux/macOS
mvnw.cmd clean verify  # Windows
```

### Issue: Java version mismatch

**Solution:**
```bash
# Check current Java version
java -version

# Set JAVA_HOME
export JAVA_HOME=/path/to/java-21  # Linux/macOS
set JAVA_HOME=C:\path\to\java-21   # Windows

# Verify
echo $JAVA_HOME  # Linux/macOS
echo %JAVA_HOME%  # Windows
```

### Issue: Tests fail locally but pass in CI

**Solution:**
```bash
# Run with same environment as CI
mvn clean verify -B -V

# Or use Docker
docker run --rm -v $(pwd):/project -w /project maven:3.9-eclipse-temurin-21 mvn clean verify
```

### Issue: Long build times

**Solution:**
```bash
# Use parallel build
mvn clean verify -T 1C  # 1 thread per core

# Skip tests
mvn clean install -DskipTests

# Skip code analysis
mvn clean install -Dmaven.javadoc.skip=true -DskipTests
```

### Issue: Dependency download fails

**Solution:**
```bash
# Clear local Maven cache and retry
rm -rf ~/.m2/repository
mvn clean verify

# Or use alternative Maven mirror
mvn -Dhttp.keepAlive=false -Dmaven.wagon.http.pool=false clean verify
```

---

## 📊 Monitoring Build Status

### Local Build Metrics

```bash
# Time each module
mvn clean verify -DtimingFileName=target/timing.xml

# Generate dependency tree
mvn dependency:tree > deps.txt

# Check for dependency conflicts
mvn dependency:analyze

# List outdated dependencies
mvn versions:display-dependency-updates
```

### Remote Build Status

Check the GitHub Actions tab: https://github.com/armand-ratombotiana/JavaLearning/actions

---

## 🔗 Useful Links

- **Java Documentation**: https://docs.oracle.com/en/java/javase/21/
- **Maven Documentation**: https://maven.apache.org/guides/
- **JUnit 5 Guide**: https://junit.org/junit5/docs/current/user-guide/
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Quarkus Docs**: https://quarkus.io/guides/

---

## ❓ Getting Help

- Check existing GitHub Issues
- Create a new issue with:
  - Environment details (OS, Java version, Maven version)
  - Error message/log output
  - Steps to reproduce
  - Expected vs actual behavior

---

**Last Updated**: April 2026
