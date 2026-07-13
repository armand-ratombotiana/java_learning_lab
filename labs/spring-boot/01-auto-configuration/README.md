# Spring Boot Auto-Configuration

Welcome to the atomic mastery lab for **Auto-Configuration**. This lab is part of the Spring Boot Internals module.

## 🧠 What You Will Master
- The magic behind `@SpringBootApplication`.
- How `@EnableAutoConfiguration` works under the hood.
- The role of `spring.factories` and `AutoConfiguration.imports`.
- `@Conditional` annotations (e.g., `@ConditionalOnClass`, `@ConditionalOnMissingBean`).
- Writing your own custom Spring Boot Starter.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - Opinionated defaults and the end of XML hell.
2. [INTERNALS.md](./INTERNALS.md) - The Spring Factories Loader and Condition evaluation.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Pure Java implementation of a custom Auto-Configuration class.