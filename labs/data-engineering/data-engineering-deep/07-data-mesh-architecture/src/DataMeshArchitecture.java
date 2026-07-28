package com.dataengineering.deep.lab07;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataMeshArchitecture {

    public record InputPort(String id, String datasetRef, String schemaVersion, String description) {}
    public record OutputPort(String id, String location, String format, String schemaVersion,
                             String description, List<String> tags) {}
    public record DataProductSla(Duration maxLatency, Duration minFreshness, double minCompleteness,
                                 List<String> downstreamConsumers) {}
    public record DataProduct(String id, String name, String domain, String owner,
                              List<InputPort> inputPorts, List<OutputPort> outputPorts,
                              DataProductSla sla, Map<String, String> metadata) {}

    public static class DomainCatalog {
        private final Map<String, DataProduct> products = new ConcurrentHashMap<>();
        private final Map<String, String> productToDomain = new ConcurrentHashMap<>();

        public void register(DataProduct product) {
            products.put(product.id(), product);
            productToDomain.put(product.id(), product.domain());
        }

        public DataProduct getProduct(String id) { return products.get(id); }

        public List<DataProduct> searchByDomain(String domain) {
            return products.values().stream().filter(p -> p.domain().equals(domain)).toList();
        }

        public List<DataProduct> search(String term) {
            String lower = term.toLowerCase();
            return products.values().stream()
                .filter(p -> p.name().toLowerCase().contains(lower)
                    || p.metadata().values().stream().anyMatch(v -> v.toLowerCase().contains(lower)))
                .toList();
        }

        public List<DataProduct> findUpstream(String productId) {
            var product = products.get(productId);
            if (product == null) return List.of();
            return product.inputPorts().stream()
                .map(InputPort::datasetRef).map(products::get)
                .filter(Objects::nonNull).toList();
        }

        public Map<String, Long> productCountByDomain() {
            return products.values().stream()
                .collect(Collectors.groupingBy(DataProduct::domain, Collectors.counting()));
        }
    }

    public record DataSharingAgreement(String providerDomain, String consumerDomain, String productId,
                                       List<String> allowedPurposes, Instant validUntil) {}

    public static class SharingManager {
        private final List<DataSharingAgreement> agreements = new ArrayList<>();

        public void addAgreement(DataSharingAgreement agreement) { agreements.add(agreement); }

        public boolean canAccess(String consumerDomain, String productId, String purpose) {
            return agreements.stream().anyMatch(a ->
                a.consumerDomain().equals(consumerDomain)
                    && a.productId().equals(productId)
                    && a.allowedPurposes().contains(purpose)
                    && Instant.now().isBefore(a.validUntil()));
        }

        public List<DataSharingAgreement> agreementsForProduct(String productId) {
            return agreements.stream().filter(a -> a.productId().equals(productId)).toList();
        }
    }

    public record DiscoveryQuery(String searchTerm, String consumerDomain,
                                 List<String> requiredTags, String purpose) {}

    public record DiscoveryResult(DataProduct product, String providerDomain,
                                  double relevanceScore, boolean accessible) {}

    public static class DiscoveryService {
        private final DomainCatalog catalog;
        private final SharingManager sharingManager;

        public DiscoveryService(DomainCatalog catalog, SharingManager sharingManager) {
            this.catalog = catalog; this.sharingManager = sharingManager;
        }

        public List<DiscoveryResult> search(DiscoveryQuery query) {
            return catalog.search(query.searchTerm()).stream().map(product -> {
                boolean accessible = sharingManager.canAccess(query.consumerDomain(), product.id(), query.purpose());
                double score = relevanceScore(product, query);
                return new DiscoveryResult(product, product.domain(), score, accessible);
            }).sorted(Comparator.comparingDouble(DiscoveryResult::relevanceScore).reversed()).toList();
        }

        private double relevanceScore(DataProduct product, DiscoveryQuery query) {
            double score = 0;
            if (product.name().toLowerCase().contains(query.searchTerm().toLowerCase())) score += 10;
            long matchingTags = product.outputPorts().stream()
                .flatMap(p -> p.tags().stream())
                .filter(t -> query.requiredTags().contains(t))
                .count();
            score += matchingTags * 3;
            return score;
        }
    }

    public static class SlaChecker {
        public boolean isHealthy(DataProduct product, Instant dataTimestamp) {
            Duration age = Duration.between(dataTimestamp, Instant.now());
            return age.compareTo(product.sla().maxLatency()) <= 0;
        }
    }

    public static void main(String[] args) {
        var sla = new DataProductSla(Duration.ofHours(1), Duration.ofMinutes(15), 0.99, List.of("analytics"));
        var product = new DataProduct("dp_orders", "Orders", "commerce", "orders-team",
            List.of(new InputPort("in1", "ds_payments", "1.0", "Payment events")),
            List.of(new OutputPort("out1", "s3://mesh/orders", "parquet", "1.0", "Cleaned orders", List.of("core", "finance"))),
            sla, Map.of("description", "Order data product"));
        var catalog = new DomainCatalog();
        catalog.register(product);
        var sharing = new SharingManager();
        sharing.addAgreement(new DataSharingAgreement("commerce", "analytics", "dp_orders",
            List.of("analytics", "reporting"), Instant.now().plus(Duration.ofDays(365))));
        var discovery = new DiscoveryService(catalog, sharing);
        var results = discovery.search(new DiscoveryQuery("orders", "analytics", List.of("finance"), "analytics"));
        System.out.println("Discovery results:");
        results.forEach(r -> System.out.println("  " + r.product().name() + " (score=" + r.relevanceScore() + ", accessible=" + r.accessible() + ")"));
        System.out.println("Product count by domain: " + catalog.productCountByDomain());
    }
}
