# Spring Bean Lifecycle

Welcome to the atomic mastery lab for the **Spring Bean Lifecycle**. This lab is part of the Spring Boot Internals module.

## 🧠 What You Will Master
- The journey of a Bean from definition to destruction.
- The role of `BeanFactory` and `ApplicationContext`.
- Bean Post-Processors (BPPs) and how they modify beans.
- Aware interfaces (e.g., `BeanNameAware`, `ApplicationContextAware`).
- Initialization and Destruction callbacks (`@PostConstruct`, `afterPropertiesSet`).

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - The high-level lifecycle phases.
2. [INTERNALS.md](./INTERNALS.md) - Deep dive into BPPs and proxying.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Logging every step of a Bean's life in Java.