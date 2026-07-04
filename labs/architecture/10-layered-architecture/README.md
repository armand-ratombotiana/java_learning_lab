# Layered Architecture (N-Tier)

## Overview
Layered Architecture organizes code into horizontal layers where each layer has a specific responsibility. The most common pattern is presentation -> business -> persistence. Layers communicate from top to bottom, with each layer depending on the layer below.

## Topics Covered
- Presentation, business, persistence, and cross-cutting layers
- N-tier vs N-layer concepts
- Separation of concerns
- Layer dependency rules
- Anti-patterns and pitfalls
- Refactoring layered architecture

## Java/Spring Stack
- Spring Boot for application framework
- Spring MVC for presentation layer
- Spring Service for business layer
- Spring Data JPA for persistence layer
- Spring AOP for cross-cutting concerns
- Thymeleaf / REST for presentation
