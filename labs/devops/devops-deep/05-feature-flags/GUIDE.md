# Feature Flags — Step-by-Step Guide

## 1. Flag Types
- **Release Toggle**: control feature rollout.
- **Experiment Toggle**: A/B test variations.
- **Ops Toggle**: kill switch for production issues.
- **Permission Toggle**: feature access by user role.

## 2. Targeting Rules
- Target by user key, email, plan, region, or custom attributes.
- Percentage rollout: gradually increase % of users who see the feature.

## 3. A/B Testing
- Two flag variations: control (existing) vs treatment (new).
- Track metrics per variation (conversion, latency, errors).

## 4. Lifecycle Management
- **Creation**: define flag, description, defaults.
- **Evaluation**: SDK evaluates flag → returns variation.
- **Retirement**: flag permanently ON → remove conditional code.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab05/*.java
java --enable-preview -cp out com.devops.deep.lab05.FeatureFlagsLab
```
