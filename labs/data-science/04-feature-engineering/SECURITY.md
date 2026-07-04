# Security in Feature Engineering

## 1. PII in Derived Features

Features like `email_domain` derived from email PII should be treated as PII themselves.

```java
// Feature carries PII risk
data.addColumn("email_domain", extractDomain(data.stringColumn("email")));
// This derived feature is still PII — apply same access controls
```

## 2. Adversarial Feature Manipulation

If users can control input features, they may craft inputs that cause the model to produce desired outputs.

**Example**: An insurance pricing model uses `num_accidents`. A user could misreport 0 to lower their rate.

**Mitigation**: Feature validation (range checks, cross-referencing with trusted sources).

## 3. Feature Store Security

Shared feature stores must enforce access control — a feature engineered on PII data should not be consumable by all teams.

```java
// Feature registry with access control
public class FeatureDefinition {
    private String name;
    private String owner;
    private Set<String> allowedConsumers;
    private boolean containsPII;
    // ...
}
```

## 4. Model Inversion via Feature Values

If an engineered feature like `avg_transaction_30d` is near-constant for a specific user, an attacker can identify that user in a de-anonymization attack.

**Mitigation**: Differential privacy in feature computation, k-anonymity check on feature distributions.

## 5. Transformation Function Poisoning

If feature transformation code reads external config (e.g., category→mapping files), an attacker who modifies those files changes model behavior.

**Mitigation**: Checksum the config files; deploy config as immutable artifacts.
