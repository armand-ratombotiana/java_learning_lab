# 11-saml-federated-identity

## Topic

SAML 2.0, SSO, identity federation, service providers, identity providers

## Overview

This lab provides a comprehensive exploration of SAML 2.0, SSO, identity federation, service providers, identity providers using Java and modern security frameworks.

## Prerequisites

- Java 21+
- Spring Security 6.x
- Maven or Gradle
- Basic understanding of XML and HTTP
- Familiarity with identity concepts

## Learning Objectives

- Understand SAML 2.0 protocol flow and message formats
- Implement SAML-based SSO with Spring Security
- Configure service provider and identity provider roles
- Apply identity federation patterns in enterprise applications
- Recognize and avoid common SAML security pitfalls

## Lab Structure

Each lab contains:

- **MINI_PROJECT/** - Hands-on mini project to practice SAML integration
- **REAL_WORLD_PROJECT/** - Full project demonstrating production SSO patterns
- **BENCHMARK/** - Performance benchmarks for SAML processing
- **CHALLENGE/** - Security challenges and capture-the-flag exercises
- **DIAGRAMS/** - Architecture and sequence diagrams
- **SOLUTION/** - Reference solutions for exercises
- **TESTS/** - Comprehensive test suites

## Topics Covered

1. SAML 2.0 protocol basics (assertions, protocols, bindings, profiles)
2. SAML SSO Web Browser SSO Profile (SP-initiated and IdP-initiated)
3. SAML assertions (authentication statements, attribute statements, authorization decisions)
4. XML digital signatures for SAML message integrity
5. XML encryption for SAML message confidentiality
6. Metadata exchange between SP and IdP
7. Name ID formats and attribute mapping
8. Single logout (SLO) protocol
9. SAML artifact binding vs POST binding vs redirect binding
10. SAML security considerations (XML wrapping attacks, replay attacks, clock skew)

## Key Concepts

- **Identity Provider (IdP)**: The system that authenticates users and issues SAML assertions
- **Service Provider (SP)**: The system that consumes SAML assertions to grant access
- **SAML Assertion**: XML document containing authentication, attribute, and authorization data
- **Federation**: Trust relationship between IdP and SP established via metadata exchange
- **SSO (Single Sign-On)**: Users authenticate once at the IdP and access multiple SPs without re-authentication

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
