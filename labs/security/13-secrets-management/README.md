# 13-secrets-management

## Topic

Vault, encrypted configs, secret rotation, environment-based secrets

## Overview

This lab provides a comprehensive exploration of secrets management including HashiCorp Vault, encrypted configuration files, secret rotation strategies, and environment-based secret handling in Java applications.

## Prerequisites

- Java 21+
- Spring Boot 3.x
- Maven or Gradle
- HashiCorp Vault (basic understanding)
- Docker for running Vault server

## Learning Objectives

- Understand secrets management principles and the secrets lifecycle
- Integrate HashiCorp Vault with Spring Boot applications
- Implement encrypted configuration files with Spring Cloud Config
- Design and automate secret rotation strategies
- Apply environment-based secret resolution patterns
- Recognize and avoid common secrets management pitfalls

## Lab Structure

Each lab contains:

- **MINI_PROJECT/** - Hands-on mini project to practice secrets management
- **REAL_WORLD_PROJECT/** - Full project demonstrating production secrets patterns
- **BENCHMARK/** - Performance benchmarks for secret retrieval operations
- **CHALLENGE/** - Security challenges and capture-the-flag exercises
- **DIAGRAMS/** - Architecture and sequence diagrams
- **SOLUTION/** - Reference solutions for exercises
- **TESTS/** - Comprehensive test suites

## Topics Covered

1. Secrets management principles (identification, storage, access, rotation, audit)
2. HashiCorp Vault architecture (server, seal/unseal, secret engines, auth methods)
3. Vault KV secret engine for static and dynamic secrets
4. Vault database secret engine for dynamic credentials
5. Vault PKI secret engine for certificate issuance
6. Spring Vault integration (PropertySource, template, annotation-driven)
7. Encrypted configuration files (Jasypt, Spring Cloud Config encryption)
8. Environment-based secret resolution (profiles, environment variables, external stores)
9. Secret rotation strategies (manual, automated, just-in-time, leasing)
10. Security considerations (encryption at rest, TLS for transit, audit logging)

## Key Concepts

- **Dynamic Secrets**: Secrets generated on-demand with automatic expiration and rotation
- **Static Secrets**: Long-lived secrets stored in encrypted backends with manual rotation
- **Secret Sprawl**: The uncontrolled proliferation of secrets across systems, increasing attack surface
- **Leasing**: Vault mechanism where secrets have a time-to-live and must be renewed
- **Seal/Unseal**: Vault encryption mechanism where the master key is split using Shamir's Secret Sharing

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
