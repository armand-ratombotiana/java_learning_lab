# 🤖 Multi-Agent System - Usage Guide

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue?style=for-the-badge)
![Agents](https://img.shields.io/badge/agents-11-green?style=for-the-badge)
![Status](https://img.shields.io/badge/status-active-success?style=for-the-badge)

**Complete guide to using the Multi-Agent Validation System**

</div>

---

## 📋 Table of Contents

- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage](#-usage)
- [Configuration](#-configuration)
- [Agents Overview](#-agents-overview)
- [Quality Gates](#-quality-gates)
- [Reports](#-reports)
- [CI/CD Integration](#-cicd-integration)
- [Troubleshooting](#-troubleshooting)
- [Best Practices](#-best-practices)

---

## 🚀 Quick Start

### Prerequisites

```bash
# Required
- Java 17 or 21
- Maven 3.8+
- Docker 20.10+
- Git 2.30+

# Optional (for full validation)
- SonarQube
- Snyk CLI
- OWASP Dependency Check
```

### 5-Minute Setup

```bash
# 1. Clone the repository
git clone https://github.com/your-org/java-learning-journey.git
cd java-learning-journey

# 2. Make scripts executable
chmod +x scripts/validate-module.sh
chmod +x scripts/validate-all-modules.sh

# 3. Validate a single module
./scripts/validate-module.sh quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus

# 4. Validate all modules
./scripts/validate-all-modules.sh
```

---

## 📦 Installation

### Step 1: System Requirements

```bash
# Check Java version
java -version
# Should be 17 or 21

# Check Maven version
mvn -version
# Should be 3.8+

# Check Docker version
docker --version
# Should be 20.10+
```

### Step 2: Install Optional Tools

```bash
# Install SonarQube Scanner (optional)
wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.8.0.2856-linux.zip
unzip sonar-scanner-cli-4.8.0.2856-linux.zip
export PATH=$PATH:$(pwd)/sonar-scanner-4.8.0.2856-linux/bin

# Install Snyk CLI (optional)
npm install -g snyk
snyk auth

# Install OWASP Dependency Check (optional)
wget https://github.com/jeremylong/DependencyCheck/releases/download/v8.4.0/dependency-check-8.4.0-release.zip
unzip dependency-check-8.4.0-release.zip
export PATH=$PATH:$(pwd)/dependency-check/bin
```

### Step 3: Configure Agents

```bash
# Copy default configuration
cp agent-config.yml.example agent-config.yml

# Edit configuration
nano agent-config.yml
```

---

## 💻 Usage

### Validate Single Module

```bash
# Basic validation
./scripts/validate-module.sh <module-path>

# Example: Validate Quarkus hello-world
./scripts/validate-module.sh quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus

# Example: Validate Vert.x basics
./scripts/validate-module.sh EclipseVert.XLearning/01-vertx-basics

# Example: Validate Spring Boot module
./scripts/validate-module.sh 02-spring-boot/01-spring-basics
```

### Validate All Modules

```bash
# Validate everything
./scripts/validate-all-modules.sh

# Validate with verbose output
VERBOSE=true ./scripts/validate-all-modules.sh

# Validate and generate HTML report
./scripts/validate-all-modules.sh --html
```

### Validate Specific Category

```bash
# Validate only Quarkus modules
./scripts/validate-category.sh quarkus

# Validate only Vert.x modules
./scripts/validate-category.sh vertx

# Validate only Spring Boot modules
./scripts/validate-category.sh spring-boot
```

### Generate Reports

```bash
# Generate HTML report
./scripts/generate-report.sh --format html --output report.html

# Generate PDF report
./scripts/generate-report.sh --format pdf --output report.pdf

# Generate Markdown report
./scripts/generate-report.sh --format markdown --output report.md

# Generate all formats
./scripts/generate-report.sh --all
```

---

## ⚙️ Configuration

### Agent Configuration File

Edit `agent-config.yml` to customize agent behavior:

```yaml
# Enable/disable agents
agents:
  code_quality:
    enabled: true
    thresholds:
      coverage: 80
      complexity: 10
  
  testing:
    enabled: true
    timeout: 10m
  
  security:
    enabled: true
    fail_on_high: true
```

### Environment Variables

```bash
# Set validation timeout
export VALIDATION_TIMEOUT=30m

# Enable verbose logging
export VERBOSE=true

# Set report directory
export REPORT_DIR=./custom-reports

# Enable debug mode
export DEBUG=true

# Set parallel execution
export MAX_PARALLEL=5
```

### Quality Gate Thresholds

```yaml
quality_gates:
  gate_1_code_quality:
    required:
      - coverage >= 80
      - bugs == 0
      - vulnerabilities == 0
  
  gate_2_testing:
    required:
      - unit_tests: all_passing
      - test_coverage >= 80
```

---

## 🤖 Agents Overview

### 1. Orchestrator Agent 🎭

**Purpose:** Coordinates all agents and manages workflow

**What it checks:**
- Module path exists
- Report directory setup
- Agent coordination
- Result aggregation

**Example output:**
```
✓ Orchestrator Agent - PASSED
  - Module path validated
  - Report directory created
  - Agents initialized
```

### 2. Code Quality Agent 📊

**Purpose:** Analyzes code quality and style

**What it checks:**
- Code coverage (>= 80%)
- Code complexity (<= 10)
- Code smells (<= 5)
- Duplications (<= 3%)
- Checkstyle violations
- PMD violations

**Example output:**
```
✓ Code Quality Agent - PASSED
  → Running Checkstyle...
    ✓ Checkstyle passed
  → Running PMD...
    ✓ PMD passed
  → Checking code coverage...
    ✓ Coverage >= 80%
```

### 3. Testing Agent 🧪

**Purpose:** Validates all tests

**What it checks:**
- Unit tests (all passing)
- Integration tests (all passing)
- Test coverage (>= 80%)
- No flaky tests
- Test execution time

**Example output:**
```
✓ Testing Agent - PASSED
  → Running unit tests...
    ✓ All unit tests passed (45/45)
  → Running integration tests...
    ✓ All integration tests passed (12/12)
```

### 4. Security Agent 🔒

**Purpose:** Scans for security vulnerabilities

**What it checks:**
- Dependency vulnerabilities
- Hardcoded secrets
- OWASP Top 10 compliance
- Security best practices

**Example output:**
```
✓ Security Agent - PASSED
  → Scanning dependencies...
    ✓ No critical vulnerabilities found
  → Checking for secrets...
    ✓ No hardcoded secrets detected
```

### 5. Performance Agent ⚡

**Purpose:** Validates performance metrics

**What it checks:**
- Response time (p95 <= 100ms)
- Throughput (>= 1000 req/s)
- Memory usage (<= 512MB)
- CPU utilization (<= 70%)

**Example output:**
```
✓ Performance Agent - PASSED
  → Analyzing performance metrics...
    ✓ Response time: 75ms (p95)
    ✓ Throughput: 1250 req/s
    ✓ Memory usage: 384MB
```

### 6. Documentation Agent 📚

**Purpose:** Validates documentation completeness

**What it checks:**
- README.md present
- Javadoc coverage (>= 80%)
- API documentation
- Examples provided

**Example output:**
```
✓ Documentation Agent - PASSED
    ✓ README.md present
  → Checking Javadoc coverage...
    ✓ Javadoc present (85%)
    ✓ Examples provided
```

### 7. Build Agent 🔨

**Purpose:** Validates build process

**What it checks:**
- Compilation success
- No build errors
- Dependencies resolved
- Artifacts generated

**Example output:**
```
✓ Build Agent - PASSED
  → Running clean build...
    ✓ Build successful
    ✓ Artifacts generated
```

### 8. Integration Agent 🔗

**Purpose:** Validates integration tests

**What it checks:**
- Docker Compose setup
- Testcontainers configuration
- Integration test structure
- External service mocking

**Example output:**
```
✓ Integration Agent - PASSED
  → Checking integration test setup...
    ✓ Docker Compose configuration present
    ✓ Test directory structure correct
```

### 9. Deployment Agent 🚀

**Purpose:** Validates deployment readiness

**What it checks:**
- Dockerfile present
- Kubernetes manifests
- Health checks implemented
- Environment configuration

**Example output:**
```
✓ Deployment Agent - PASSED
    ✓ Dockerfile present
    ✓ Kubernetes manifests present
    ✓ Health checks implemented
```

### 10. Monitoring Agent 📊

**Purpose:** Validates observability setup

**What it checks:**
- Metrics library present
- Logging framework configured
- Tracing setup (optional)
- Dashboard configuration

**Example output:**
```
✓ Monitoring Agent - PASSED
  → Checking monitoring setup...
    ✓ Metrics library present
    ✓ Logging framework present
```

### 11. Report Agent 📋

**Purpose:** Generates comprehensive reports

**What it does:**
- Aggregates all agent results
- Calculates overall score
- Generates JSON/HTML/Markdown reports
- Provides recommendations

**Example output:**
```
✓ Report Agent - PASSED

Module: quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus
Overall Status: PASSED
Overall Score: 92/100

Agent Results:
  ✓ orchestrator: 100/100
  ✓ code_quality: 85/100
  ✓ testing: 90/100
  ✓ security: 95/100
  ✓ performance: 88/100
  ✓ documentation: 80/100
  ✓ build: 100/100
  ✓ integration: 85/100
  ✓ deployment: 70/100
  ✓ monitoring: 75/100

✓ Module validation PASSED
✓ Module is ready for production
```

---

## 🚦 Quality Gates

### Gate 1: Code Quality ✅

**Requirements:**
- Code coverage >= 80%
- Bugs == 0
- Vulnerabilities == 0
- Code smells <= 5
- Duplications <= 3%

**Status:** BLOCKING

### Gate 2: Testing ✅

**Requirements:**
- All unit tests passing
- All integration tests passing
- Test coverage >= 80%
- No flaky tests

**Status:** BLOCKING

### Gate 3: Security ✅

**Requirements:**
- Critical vulnerabilities == 0
- High vulnerabilities == 0
- No exposed secrets
- OWASP Top 10 protected

**Status:** BLOCKING

### Gate 4: Performance ⚡

**Requirements:**
- Response time p95 <= 100ms
- Throughput >= 1000 req/s
- Memory usage <= 512MB
- CPU usage <= 70%

**Status:** NON-BLOCKING

### Gate 5: Production Ready 🚀

**Requirements:**
- Documentation complete
- Build successful
- Deployment validated
- Monitoring configured

**Status:** NON-BLOCKING

---

## 📊 Reports

### Report Types

#### 1. JSON Report
```json
{
  "module": "quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus",
  "timestamp": "2024-01-15T10:30:00Z",
  "overall_status": "PASSED",
  "overall_score": 92,
  "agents": {
    "code_quality": {
      "status": "PASSED",
      "score": 85
    }
  }
}
```

#### 2. HTML Report
- Interactive dashboard
- Charts and graphs
- Detailed agent results
- Trend analysis

#### 3. Markdown Report
- GitHub-friendly format
- Easy to read
- Can be included in PRs

### Report Location

```
validation-reports/
├── report_20240115_103000.json
├── report_20240115_103000.html
├── report_20240115_103000.md
└── summary_20240115_103000.txt
```

---

## 🔄 CI/CD Integration

### GitHub Actions

The multi-agent system is automatically triggered on:

- **Push to main/develop:** Full validation
- **Pull requests:** Full validation with PR comments
- **Daily schedule:** Complete validation at 2 AM UTC
- **Manual trigger:** Via workflow_dispatch

### Viewing Results

1. Go to **Actions** tab in GitHub
2. Select **Multi-Agent Module Validation** workflow
3. View job results and artifacts
4. Download validation reports

### PR Comments

Automated comments on PRs include:
- Overall validation status
- Agent results summary
- Quality gate status
- Link to detailed reports

---

## 🔧 Troubleshooting

### Common Issues

#### Issue 1: Module Not Found
```
Error: Module path does not exist
```

**Solution:**
```bash
# Check module path
ls -la quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus

# Use correct relative path
./scripts/validate-module.sh quarkus-learning/01-Introduction-to-Quarkus/hello-quarkus
```

#### Issue 2: Build Failures
```
Error: Build failed
```

**Solution:**
```bash
# Clean and rebuild
cd <module-path>
mvn clean install

# Check for compilation errors
mvn compile
```

#### Issue 3: Test Failures
```
Error: Some unit tests failed
```

**Solution:**
```bash
# Run tests with verbose output
mvn test -X

# Run specific test
mvn test -Dtest=YourTestClass
```

#### Issue 4: Permission Denied
```
Error: Permission denied
```

**Solution:**
```bash
# Make scripts executable
chmod +x scripts/*.sh

# Run with proper permissions
./scripts/validate-module.sh <module-path>
```

---

## 📚 Best Practices

### 1. Run Validation Before Commit

```bash
# Validate your changes
./scripts/validate-module.sh <your-module>

# Only commit if validation passes
git add .
git commit -m "Your message"
```

### 2. Review Reports Regularly

```bash
# Generate weekly report
./scripts/generate-report.sh --format html --output weekly-report.html

# Review trends
cat validation-reports/summary_*.txt
```

### 3. Fix Issues Immediately

- **Critical issues:** Fix before committing
- **High priority:** Fix within 24 hours
- **Medium priority:** Fix within 1 week
- **Low priority:** Fix in next sprint

### 4. Keep Configuration Updated

```bash
# Review agent configuration monthly
nano agent-config.yml

# Update thresholds as needed
# Adjust quality gates
# Enable/disable agents
```

### 5. Monitor CI/CD Pipeline

- Check GitHub Actions regularly
- Review failed builds immediately
- Update dependencies monthly
- Keep tools up to date

---

## 🎯 Success Criteria

A module is **PRODUCTION READY** when:

✅ All 11 agents report PASSED status  
✅ Overall score >= 90/100  
✅ All blocking quality gates passed  
✅ Zero critical/high vulnerabilities  
✅ Code coverage >= 80%  
✅ All tests passing  
✅ Documentation complete  
✅ Build successful  
✅ Deployment validated  
✅ Monitoring configured  

---

## 📞 Support

### Getting Help

- **Documentation:** See `MULTI_AGENT_SYSTEM.md`
- **Issues:** Open a GitHub issue
- **Discussions:** Use GitHub Discussions
- **Email:** support@example.com

### Contributing

See `CONTRIBUTING.md` for guidelines on:
- Adding new agents
- Improving existing agents
- Reporting bugs
- Suggesting features

---

<div align="center">

## 🎉 Happy Validating!

**Every module validated by 10+ specialized agents**

**100% quality assurance guaranteed**

---

Made with ❤️ by the Java Learning Journey Team

</div>