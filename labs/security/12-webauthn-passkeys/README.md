# 12-webauthn-passkeys

## Topic

WebAuthn API, FIDO2, passkeys, biometric auth, hardware tokens

## Overview

This lab provides a comprehensive exploration of WebAuthn, FIDO2, passkeys, biometric authentication, and hardware security tokens using Java and Spring Security.

## Prerequisites

- Java 21+
- Spring Security 6.x
- Maven or Gradle
- Understanding of public key cryptography
- Familiarity with web authentication flows

## Learning Objectives

- Understand WebAuthn API and FIDO2 protocol specifications
- Implement passkey-based authentication in Java applications
- Register and authenticate with biometric and hardware token factors
- Apply attestation and assertion verification patterns
- Recognize and avoid common WebAuthn implementation pitfalls

## Lab Structure

Each lab contains:

- **MINI_PROJECT/** - Hands-on mini project to practice WebAuthn integration
- **REAL_WORLD_PROJECT/** - Full project demonstrating production passkey patterns
- **BENCHMARK/** - Performance benchmarks for cryptographic operations
- **CHALLENGE/** - Security challenges and capture-the-flag exercises
- **DIAGRAMS/** - Architecture and sequence diagrams
- **SOLUTION/** - Reference solutions for exercises
- **TESTS/** - Comprehensive test suites

## Topics Covered

1. WebAuthn API core concepts (credentials, authenticators, relying parties)
2. FIDO2 CTAP1 and CTAP2 protocols
3. Passkey architecture and synchronization across devices
4. Resident keys vs non-resident keys
5. Attestation types (none, basic, self, privacy CA, ECDAA)
6. User verification methods (PIN, biometric, presence)
7. Credential ID management and discoverable credentials
8. Authentication ceremony flow (registration and assertion)
9. Extension support (appid, credProps, hmac-secret)
10. Security considerations (phishing resistance, replay protection, attestation privacy)

## Key Concepts

- **Relying Party (RP)**: The web application that registers and authenticates users via WebAuthn
- **Authenticator**: The device or software that generates and stores cryptographic key pairs
- **Attestation**: Proof that an authenticator is genuine and trusted
- **Assertion**: Proof that a user possesses the private key for a registered credential
- **Passkey**: A discoverable credential that can be synced across devices for passwordless login

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
