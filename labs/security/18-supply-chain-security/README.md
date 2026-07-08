# 18-supply-chain-security

## Topic

SBOM, dependency scanning, Sigstore, SLSA framework

## Overview

This lab provides a comprehensive exploration of supply chain security including Software Bill of Materials (SBOM), dependency vulnerability scanning, Sigstore for signing and verification, and the SLSA framework for supply chain integrity.

## Prerequisites

- Java 21+
- Maven or Gradle
- Spring Boot 3.x
- Docker and containerization basics
- Understanding of CI/CD pipelines

## Learning Objectives

- Understand supply chain security threats and attack vectors
- Generate and validate Software Bill of Materials (SBOM)
- Implement dependency vulnerability scanning in CI/CD
- Apply Sigstore for artifact signing and verification
- Implement SLSA framework compliance levels
- Recognize and avoid common supply chain security pitfalls

## Lab Structure

Each lab contains:

- **MINI_PROJECT/** - Hands-on mini project to practice supply chain security
- **REAL_WORLD_PROJECT/** - Full project demonstrating production supply chain security patterns
- **BENCHMARK/** - Performance benchmarks for scanning and verification
- **CHALLENGE/** - Security challenges and capture-the-flag exercises
- **DIAGRAMS/** - Architecture and sequence diagrams
- **SOLUTION/** - Reference solutions for exercises
- **TESTS/** - Comprehensive test suites

## Topics Covered

1. Supply chain security landscape (dependency confusion, typo-squatting, malicious packages)
2. SBOM generation and formats (SPDX, CycloneDX, SWID)
3. Dependency scanning tools (OWASP Dependency-Check, Snyk, GitHub Dependabot, Renovate)
4. Sigstore architecture (Fulcio, Rekor, Cosign, Gitsign)
5. Cosign for container image signing and verification
6. SLSA framework levels and requirements (SLSA 1-4)
7. Provenance generation and attestation
8. Package manager security (Maven Central, npm, PyPI security features)
9. CI/CD pipeline security (GitHub Actions security, self-hosted runner risks)
10. Security considerations (build reproducibility, hermetic builds, trusted publishers)

## Key Concepts

- **SBOM (Software Bill of Materials)**: A formal record containing the details and supply chain relationships of components used in building software
- **Sigstore**: A non-profit service for signing and verifying software artifacts using ephemeral keys and transparency logs
- **SLSA (Supply chain Levels for Software Artifacts)**: A security framework to prevent tampering and improve integrity of software artifacts
- **Provenance**: Verifiable information about how a software artifact was built and where it came from
- **Dependency Confusion**: An attack where a malicious package with the same name as an internal package is uploaded to a public repository

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
