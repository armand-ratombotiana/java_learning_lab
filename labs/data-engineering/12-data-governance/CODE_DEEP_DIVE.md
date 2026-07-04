# Code Deep Dive: Governance API

## REST Controllers
```java
@RestController
@RequestMapping("/api/v1/governance")
public class GovernanceApi {
    @GetMapping("/catalog")
    public Page<DataAsset> searchCatalog(
            @RequestParam String q, Pageable p) {
        return catalogService.search(q, p);
    }

    @GetMapping("/lineage/{id}")
    public LineageGraph getLineage(@PathVariable String id) {
        return lineageService.getLineage(id, Direction.UPSTREAM, 3);
    }

    @PostMapping("/policies/evaluate")
    public AccessDecision evaluate(@RequestBody AccessRequest r) {
        return policyEngine.evaluate(r);
    }

    @GetMapping("/compliance/gdpr")
    public ComplianceReport gdprReport() {
        return complianceService.generateReport("GDPR");
    }
}
```

## Policy Engine
```java
@Component
public class PolicyEngine {
    public AccessDecision evaluate(AccessRequest request) {
        List<Policy> policies = policyRepo.findByResourceAndActive(
            request.getResource(), true);
        for (Policy p : policies) {
            if (!p.isAllow()) return AccessDecision.deny("Denied: " + p.getName());
        }
        DataAsset asset = catalogService.getAsset(request.getResource());
        if (asset.getClassification() == Classification.PII
            && !userContext.hasRole("PII_ACCESS")) {
            return AccessDecision.deny("PII requires special role");
        }
        return AccessDecision.allow();
    }
}
```
