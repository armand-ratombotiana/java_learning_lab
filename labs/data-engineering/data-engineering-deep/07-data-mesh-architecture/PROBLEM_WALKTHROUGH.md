# Lab 07: Problem Walkthrough — Cross-Domain Data Product Discovery

## Problem

Build a `CrossDomainDiscoveryService` that allows domains to discover, evaluate, and consume data products from other domains while respecting governance and SLA constraints.

## Walkthrough

### Step 1: Discovery Query Model

```java
public record DiscoveryQuery(String searchTerm, String consumerDomain,
                             List<String> requiredTags, String purpose) {}

public record DiscoveryResult(DataProduct product, String providerDomain,
                              double relevanceScore, boolean accessible) {}
```

### Step 2: Search Across Domains

```java
public class DiscoveryService {
    private final DomainCatalog catalog;
    private final SharingManager sharingManager;

    public List<DiscoveryResult> search(DiscoveryQuery query) {
        return catalog.search(query.searchTerm()).stream()
            .map(product -> {
                boolean accessible = sharingManager.canAccess(
                    query.consumerDomain(), product.id(), query.purpose());
                double score = relevanceScore(product, query);
                return new DiscoveryResult(product, product.domain(), score, accessible);
            })
            .sorted(Comparator.comparingDouble(DiscoveryResult::relevanceScore).reversed())
            .toList();
    }

    private double relevanceScore(DataProduct product, DiscoveryQuery query) {
        double score = 0;
        if (product.name().toLowerCase().contains(query.searchTerm().toLowerCase())) score += 10;
        if (product.metadata().values().stream().anyMatch(v -> v.contains(query.searchTerm()))) score += 5;
        long matchingTags = product.outputPorts().stream()
            .flatMap(p -> p.tags().stream())
            .filter(t -> query.requiredTags().contains(t))
            .count();
        score += matchingTags * 3;
        return score;
    }
}
```

### Step 3: Access Evaluation

```java
public class AccessEvaluator {
    public boolean canAccess(String consumerDomain, String productId, String purpose) {
        var agreement = findAgreement(consumerDomain, productId);
        if (agreement == null) return false;
        if (Instant.now().isAfter(agreement.validUntil())) return false;
        return agreement.allowedPurposes().contains(purpose);
    }
}
```

### Step 4: SLA Compliance Check

```java
public class SlaChecker {
    public boolean isHealthy(DataProduct product, Instant currentTime, Instant dataTimestamp) {
        Duration age = Duration.between(dataTimestamp, currentTime);
        return age.compareTo(product.sla().maxLatency()) <= 0;
    }
}
```

## Complexity

- **Time**: O(P * T) for search where P = products, T = search terms
- **Space**: O(P) for results
