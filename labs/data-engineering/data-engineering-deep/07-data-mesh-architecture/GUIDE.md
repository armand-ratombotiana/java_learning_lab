# Lab 07: Data Mesh Architecture — Implementation Guide

## Step 1: Data Product Model

```java
public record DataProduct(String id, String name, String domain, String owner,
                          List<InputPort> inputPorts, List<OutputPort> outputPorts,
                          DataProductSla sla, Map<String, String> metadata) {}
```

## Step 2: Input / Output Ports

```java
public record InputPort(String id, String datasetRef, String schemaVersion, String description) {}
public record OutputPort(String id, String location, String format, String schemaVersion,
                         String description, List<String> tags) {}
```

## Step 3: Data Product SLA

```java
public record DataProductSla(Duration maxLatency, Duration minFreshness, double minCompleteness,
                             List<String> downstreamConsumers) {}
```

## Step 4: Domain Catalog

```java
public class DomainCatalog {
    private final Map<String, DataProduct> products = new ConcurrentHashMap<>();
    private final Map<String, String> productToDomain = new ConcurrentHashMap<>();

    public void register(DataProduct product) {
        products.put(product.id(), product);
        productToDomain.put(product.id(), product.domain());
    }

    public List<DataProduct> searchByDomain(String domain) {
        return products.values().stream()
            .filter(p -> p.domain().equals(domain))
            .toList();
    }

    public List<DataProduct> findUpstream(String productId) {
        var product = products.get(productId);
        if (product == null) return List.of();
        return product.inputPorts().stream()
            .map(InputPort::datasetRef)
            .map(products::get)
            .filter(Objects::nonNull)
            .toList();
    }
}
```

## Step 5: Cross-Domain Data Sharing

```java
public class DataSharingAgreement {
    private final String providerDomain;
    private final String consumerDomain;
    private final String productId;
    private final List<String> allowedPurposes;
    private final Instant validUntil;
}

public class SharingManager {
    public boolean canAccess(String consumerDomain, String productId, String purpose) {
        var agreement = findAgreement(consumerDomain, productId);
        return agreement != null
            && agreement.allowedPurposes().contains(purpose)
            && Instant.now().isBefore(agreement.validUntil());
    }
}
```
