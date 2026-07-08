# 15-api-security-testing

## Topic

OWASP Top 10 testing, penetration testing, SAST/DAST, fuzzing

## Overview

This lab provides a comprehensive exploration of API security testing including OWASP Top 10 vulnerability assessment, penetration testing methodologies, SAST/DAST integration, and fuzzing techniques for Java APIs.

## Prerequisites

- Java 21+
- Spring Boot 3.x
- Maven or Gradle
- OWASP ZAP or Burp Suite basics
- Understanding of REST API design

## Learning Objectives

- Understand OWASP Top 10 vulnerabilities and their impact on APIs
- Perform penetration testing on Java web applications
- Integrate SAST and DAST tools into CI/CD pipelines
- Apply fuzzing techniques to discover edge-case vulnerabilities
- Recognize and avoid common API security testing pitfalls

## Lab Structure

Each lab contains:

- **MINI_PROJECT/** - Hands-on mini project to practice security testing
- **REAL_WORLD_PROJECT/** - Full project demonstrating production testing patterns
- **BENCHMARK/** - Performance benchmarks for scanning tools
- **CHALLENGE/** - Security challenges and capture-the-flag exercises
- **DIAGRAMS/** - Architecture and sequence diagrams
- **SOLUTION/** - Reference solutions for exercises
- **TESTS/** - Comprehensive test suites

## Topics Covered

1. OWASP Top 10 (2021) deep dive for APIs (broken access control, injection, misconfiguration)
2. Penetration testing methodology (reconnaissance, scanning, exploitation, reporting)
3. SAST (Static Application Security Testing) integration with Java (SpotBugs, PMD, FindSecBugs)
4. DAST (Dynamic Application Security Testing) with OWASP ZAP
5. API fuzzing techniques (input fuzzing, parameter fuzzing, schema fuzzing)
6. Dependency vulnerability scanning (OWASP Dependency-Check, Snyk, Dependabot)
7. Rate limiting and brute force attack testing
8. Authentication and authorization testing (JWT tampering, privilege escalation)
9. Business logic vulnerability assessment
10. Security considerations (false positives, risk prioritization, remediation)

## Key Concepts

- **SAST**: White-box testing that analyzes source code for security vulnerabilities without executing the application
- **DAST**: Black-box testing that probes running applications for vulnerabilities by simulating attacks
- **Fuzzing**: Automated technique of providing invalid, unexpected, or random data as inputs to find crashes or vulnerabilities
- **False Positive**: A security alert that incorrectly identifies a benign condition as a vulnerability
- **Risk Prioritization**: Ranking vulnerabilities by severity, exploitability, and business impact

## Getting Started

1. Review the theory in THEORY.md
2. Understand the math foundation in MATH_FOUNDATION.md
3. Study the code deep dive for implementation patterns
4. Complete the exercises
5. Test your knowledge with the quiz
6. Build the mini project
7. Explore the real-world project
8. Review interview questions

### Detailed Lab Map

| Module | File | Description |
|--------|------|-------------|
| Theory | THEORY.md | Comprehensive theoretical background |
| Math | MATH_FOUNDATION.md | Mathematical foundations and calculations |
| Code | CODE_DEEP_DIVE.md | Detailed code walkthrough and patterns |
| Exercises | EXERCISES.md | Practice exercises by difficulty level |
| Quiz | QUIZ.md | Knowledge assessment with answers |
| Architecture | ARCHITECTURE.md | System and deployment architecture |
| Security | SECURITY.md | Security analysis and threat model |
| Performance | PERFORMANCE.md | Benchmarks and optimization |
| Refactoring | REFACTORING.md | Code improvement strategies |
| Debugging | DEBUGGING.md | Common issues and solutions |
| Mistakes | COMMON_MISTAKES.md | Pitfalls and how to avoid them |
| Step-by-Step | STEP_BY_STEP.md | Implementation walkthrough |
| Visual Guide | VISUAL_GUIDE.md | Architecture and flow diagrams |
| Internals | INTERNALS.md | Internal implementation details |
| How It Works | HOW_IT_WORKS.md | Detailed mechanism explanation |
| Mental Models | MENTAL_MODELS.md | Conceptual frameworks |
| History | HISTORY.md | Evolution of the technology |
| Why Matters | WHY_IT_MATTERS.md | Business and industry impact |
| Why Exists | WHY_IT_EXISTS.md | Problem statement and solution |
| References | REFERENCES.md | Standards, books, tools, resources |
| Reflection | REFLECTION.md | Self-assessment and next steps |
| Interview | INTERVIEW.md | Common interview questions |
| Flashcards | FLASHCARDS.md | Key concept memorization |

### Assessment

Each lab includes:
- **Beginner Exercises**: Basic implementation tasks
- **Intermediate Exercises**: Integration and optimization challenges
- **Advanced Exercises**: Production-ready implementation
- **Knowledge Quiz**: 20+ questions covering all topics
- **Interview Questions**: 25+ curated questions for interview prep
- **Flashcards**: 30+ concept cards for quick review

### Support

If you encounter issues:
1. Check the DEBUGGING.md for common problems
2. Review COMMON_MISTAKES.md for known pitfalls
3. Use the lab's TESTS directory for reference tests
4. Compare with SOLUTION directory for exercise answers

### Contributing

To contribute improvements:
1. Fork the repository
2. Create a feature branch
3. Make changes following existing patterns
4. Ensure all tests pass
5. Submit a pull request with clear description
