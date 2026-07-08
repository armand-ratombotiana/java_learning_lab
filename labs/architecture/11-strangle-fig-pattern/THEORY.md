# Strangler Fig Pattern — Theory

## 1. Introduction
The Strangler Fig pattern (also known as the Strangler Application pattern) is inspired by strangler fig trees that grow around a host tree, eventually replacing it entirely. In software architecture, this pattern enables incremental migration from a legacy system to a new system with minimal risk.

## 2. Problem Context
Legacy systems are difficult to replace in a single cut-over. The risks include data loss during migration, extended downtime during transition, incomplete understanding of legacy behavior, business continuity requirements, and regulatory compliance during transition.

## 3. Core Principles

### 3.1 Incremental Replacement
Replace functionality piece by piece rather than all at once. Each replaced piece routes to the new system while the rest continues using the legacy system.

### 3.2 Coexistence
Both legacy and new systems run simultaneously during the transition period. Users experience seamless service regardless of which system handles their request.

### 3.3 Observability
Monitor both systems during migration to detect discrepancies, performance issues, and data inconsistencies early.

### 3.4 Reversibility
Every migration step must be reversible. If a new implementation fails, traffic must be immediately routable back to the legacy system.

## 4. Key Components

### 4.1 Routing Layer
An intelligent proxy or API gateway that directs traffic to either legacy or new system based on URL patterns, feature flags, user segments, and gradual rollout percentages.

### 4.2 Feature Toggles
Runtime switches controlling which features use the new system. Categories include release toggles, experiment toggles, ops toggles, and permission toggles.

### 4.3 Branch by Abstraction
Create an abstraction layer that both legacy and new implementations satisfy. The calling code depends only on the abstraction, enabling seamless switching.

## 5. Migration Strategies

### 5.1 Horizontal Strangling
Replace entire technology stacks layer by layer. Replace the web layer first, then business logic, then data layer.

### 5.2 Vertical Strangling
Replace complete vertical slices of functionality including UI, logic, and data storage for each slice.

### 5.3 Functionality-Based Strangling
Replace individual features or capabilities at the most granular level.

## 6. Implementation Considerations

### 6.1 Routing Strategy
Path-based routing is simplest but limited. Header-based routing enables user segmentation. Content-based routing provides maximum flexibility.

### 6.2 Data Migration
Dual-write during transition, synchronization jobs for catch-up, and data validation with reconciliation.

### 6.3 Testing Strategy
Parallel runs comparing old and new outputs, shadow traffic for pre-production validation, and canary releases for real-world testing.

## 7. Risks and Challenges
- Increased operational complexity during transition
- Maintaining backward compatibility with existing clients
- Data synchronization challenges between old and new systems
- Testing overhead for parallel system validation
- Team coordination across old and new codebases

## 8. Best Practices
1. Start with low-risk, low-complexity functionality
2. Establish clear success criteria for each migration step
3. Automate routing decisions and feature toggle management
4. Implement comprehensive monitoring and alerting
5. Maintain a rollback plan for every migration step
6. Document legacy behavior before migration
7. Invest in integration testing between old and new systems
8. Plan for the eventual retirement of the legacy system
